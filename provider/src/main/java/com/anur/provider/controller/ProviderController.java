package com.anur.provider.controller;

import com.anur.common.Constant;
import com.anur.config.ArtistConfiguration;
import com.anur.model.TestMsg;
import com.anur.provider.feignservice.TransactionMsgService;
import com.anur.provider.model.ProviderOrder;
import com.anur.provider.service.ProviderOrderService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Created by Anur IjuoKaruKas on 2018/5/8
 */

@RestController
public class ProviderController {

    @Autowired
    private TransactionMsgService transactionMsgService;

    @Autowired
    private ArtistConfiguration artistConfiguration;

    @Autowired
    private ProviderOrderService providerOrderService;

    @GetMapping
    public void test() throws Exception {
        TestMsg testMsg = new TestMsg();
        testMsg.setContent("这是一条测试消息");
        testMsg.setDate(new Date());
        String testMsgStr = new Gson().toJson(testMsg);

        String routingKey = "test.key.testing";
        Map<String, String> map = new HashMap<>();

        String orderId = UUID.randomUUID().toString() + System.currentTimeMillis();
        map.put("id", orderId);
        String mapStr = new Gson().toJson(map);

        String msgId = transactionMsgService.prepareMsg(testMsgStr, routingKey, Constant.TEST_EXCHANGE, mapStr, artistConfiguration.getArtist());


        Random random = new Random();

        // 模拟业务执行很久，那边确认不到，然后进行重试。
        if (random.nextBoolean()) {
            Thread.sleep(random.nextInt(20) * 1000);

            if (random.nextBoolean()){
                throw new Exception("模拟业务执行失败！");
            }

            // 执行业务
            ProviderOrder providerOrder = new ProviderOrder();
            providerOrder.setId(orderId);
            providerOrderService.save(providerOrder);
        }

        // 确认消息可以被发送
        transactionMsgService.confirmMsgToSend(msgId);
    }

    @GetMapping("check")
    public boolean check(String id) {
        return providerOrderService.findById(id) != null;
    }
}
