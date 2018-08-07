# Eventual-Consistency-Example
可靠消息最终一致性在spring cloud下的实现Demo：实现思路来自于龙果学院的最终一致性视频

[建议简单看看上一篇文章再往下阅读](https://blog.csdn.net/anurnomeru/article/details/80306002)

我们的项目就基于这个模型：

![~](http://morgoth-aman.huainanhai.com/etc/linzishuang/20180807163817/5b695a793e7c3)

接下来就到了我们的实战时刻~

项目基于spring cloud编写，没有spring cloud基础看起来可能有一点点费力。

### 准备阶段：定义可靠消息接口

```java
package com.anur.messageapi.api;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Created by Anur IjuoKaruKas on 2018/5/8
 */
public interface TransactionMsgApi {

    /**
     * 预发送消息，先将消息保存到消息中心
     */
    @RequestMapping(value = "prepare", method = RequestMethod.GET)
    int prepareMsg(
            @RequestParam("id") String id,
            @RequestParam("msg") String msg,
            @RequestParam("routingKey") String routingKey,
            @RequestParam("exchange") String exchange,
            @RequestParam("paramMap") String paramMap,
            @RequestParam("artist") String artist);

    /**
     * 生产者确认消息可投递
     */
    @RequestMapping(value = "confirm", method = RequestMethod.GET)
    int confirmMsgToSend(@RequestParam("id") String id, @RequestParam("caller") String caller);

    /**
     * 向队列投递消息
     */
    @RequestMapping(value = "send", method = RequestMethod.GET)
    void sendMsg(@RequestParam("id") String id);

    /**
     * 消费者确认消费成功
     */
    @RequestMapping(value = "ack", method = RequestMethod.GET)
    int acknowledgement(@RequestParam("id") String id,
                        @RequestParam("artist") String artist);
}

```
我们先忽略后面的两个接口，先看第一个，一共有六个参数

 * id：消息的id，这个设计其实很自由，可以在可靠消息服务中生成，也可以在生产者端生成，本项目选择在生产者端生成。
 * msg：消息的主体，可以是普通的字符串，也可以是对象
 * routingKey：路由键，发送消息时用（不懂的可以去看看[MQ基础](https://blog.csdn.net/anurnomeru/article/details/80093539)）
 * exchange：交换器，发送消息时用（不懂的可以去看看[MQ基础](https://blog.csdn.net/anurnomeru/article/details/80093539)）
 * paramMap：可靠消息服务回查时用，比如说我一个消息发送到可靠消息服务，结果没确认，可靠消息服务就根据这个paramMap进行消息的回查，向生产者查询这个业务到底执行成功了没。
 * artist：回调（回查）地址，在springCloud中，其实就是serverName
 
# 具体场景解析：订单服务

### 一、创建预发送消息，并将其保存到数据库
我们首先生成一条消息，我们往paramMap中指定了，我们这个订单的订单id是orderId，消息内容我瞎写的，这条消息要保存到数据库（它的作用是保证消息一定被可靠消息接收并持久化）
	
```java
        String routingKey = "test.key.testing";
        Map<String, String> map = new HashMap<>();

        String orderId = UUID.randomUUID().toString() + System.currentTimeMillis();
        map.put("id", orderId);
        String mapStr = JSON.toJSONString(map);

        TestMsg testMsg = new TestMsg();
        testMsg.setContent("这是一条测试消息");
        String testMsgStr = JSON.toJSONString(testMsg);
        // ===============================

		// 要保存到数据库（它的作用是保证消息一定被可靠消息接收并持久化）
        PrepareMsg prepareMsg = prepareMsgService.genMsg(orderId, testMsgStr, routingKey, Constant.TEST_EXCHANGE, mapStr);
```

### 二、异步发送这条消息，将其标记为预发送

异步发送了一条**【预发送】**消息给消息可靠消息服务
```java
		Future<Integer> future = prepareMsgService.prepareMsg(prepareMsg);


// 下面是prepareMsg的实现

    @Async
    @Override
    public Future<Integer> prepareMsg(PrepareMsg prepareMsg) {
    // 调用我们刚才在【准备阶段】定义的接口
        int result = transactionMsgService.prepareMsg(prepareMsg.getId(), prepareMsg.getMsg(), prepareMsg.getRoutingKey(), prepareMsg.getExchange(), prepareMsg.getParamMap(), artistConfiguration.getArtist());
    // 如果调用成功，删除刚才本地保存的数据库
        if (result == 1) {
            prepareMsgMapper.deleteByPrimaryKey(prepareMsg.getId());
        }
        return new AsyncResult<>(result);
    }
```
### 三、执行业务
你可以把下面那些想象成处理订单状态，上面的这个步骤是有事务的，也就是说：

 - 如果执行失败，我们的可靠消息服务只会收到一条预发送的消息，保证了操作的原子性。
 - **或者执行成功，但没有及时向可靠消息服务发送，这种情况往下看，先忽略它。**
```java
		///////////// 事务
        ProviderOrder providerOrder = new ProviderOrder();
        providerOrder.setId(orderId);
        providerOrderService.save(providerOrder);
        ///////////// 事务
```
### 四、异步告知可靠消息服务，业务处理成功，将刚才预发送的消息标记为待发送

```java
       // 确认消息可以被发送
        if (future.get() == 1) {
            prepareMsgService.confirmMsgToSend(orderId, this.getClass().getSimpleName());
        }
```

### Extra、异常情况

######1、执行成功，但没有及时向可靠消息服务发送通知。

这时候我们的artist和paramMap就发挥作用了，我们的可靠消息服务，可以拿着这两个东西，定时向生产者查询那些没有被标记为【待发送】的消息。比如说这样：

```java
        // 这里是可靠消息服务
        String url = String.format("http://%s/check?", transactionMsg.getCreater());
        Map<String, String> paramMap = JSON.parseObject(transactionMsg.getParamMap(), new TypeReference<HashMap<String, String>>() {
        });

        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> stringStringEntry : paramMap.entrySet()) {
            sb.append(stringStringEntry.getKey()).append("=").append(stringStringEntry.getValue()).append("&");
        }

        sb.deleteCharAt(sb.length() - 1);
        
        // 结果为true，代表这条消息的业务执行成功了，可自助将消息状态标记为【待发送】
        // 反之执行失败
        resultBoolean = restTemplate.getForObject(url + sb, boolean.class);
```

######2、执行失败，也没有及时向可靠消息服务发送通知。

这个情况并不影响，因为可靠消息服务会回查，发现消息没有执行成功，不会将消息投递出去。

这里要注意，每条消息最好设置一个查询次数的限制

######3、预发送失败，业务执行成功

这时候我们在第一步事先存储的消息就发挥作用了，这里只要写一个定时任务，向可靠消息服务定时投递即可。这里要注意可靠消息服务的幂等性。

由于消息id是由生产者指定，所以即使可靠消息服务收到了重复的创建【预发送】的消息，插入数据库也是会失败的。

```java
    @Scheduled(cron = "*/1 * * * * *")
    public void checkPrepareMsg() {
        List<PrepareMsg> prepareMsgList = prepareMsgService.getUnConfirmList();
        if (prepareMsgList.size() > 0) {
            System.out.println("消息重发中");
        }
        for (PrepareMsg prepareMsg : prepareMsgList) {
            prepareMsgService.prepareMsg(prepareMsg);
        }
    }
```

Github -- >  [可靠消息服务 example](https://github.com/anurnomeru/Eventual-Consistency-Example)
