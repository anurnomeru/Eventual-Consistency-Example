package com.anur.provider.controller;

import com.alibaba.fastjson.JSON;
import com.anur.common.Constant;
import com.anur.model.TestMsg;
import com.anur.provider.feignservice.TransactionMsgService;
import com.anur.provider.model.PrepareMsg;
import com.anur.provider.model.ProviderOrder;
import com.anur.provider.service.PrepareMsgService;
import com.anur.provider.service.ProviderOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.Future;

/**
 * Created by Anur IjuoKaruKas on 2018/5/8
 */

@RestController
public class ProviderController {

    @Autowired
    private TransactionMsgService transactionMsgService;

    @Autowired
    private ProviderOrderService providerOrderService;

    @Autowired
    private PrepareMsgService prepareMsgService;

    @GetMapping
    public void test() throws Exception {

        String routingKey = "test.key.testing";
        Map<String, String> map = new HashMap<>();

        String orderId = UUID.randomUUID().toString() + System.currentTimeMillis();
        map.put("id", orderId);
        String mapStr = JSON.toJSONString(map);

        TestMsg testMsg = new TestMsg();
        testMsg.setContent("这是一条测试消息");
        String testMsgStr = JSON.toJSONString(testMsg);
        // ===============================

        PrepareMsg prepareMsg = prepareMsgService.genMsg(orderId, testMsgStr, routingKey, Constant.TEST_EXCHANGE, mapStr);
        Future<Integer> future = prepareMsgService.prepareMsg(prepareMsg);

        // 执行业务
        ProviderOrder providerOrder = new ProviderOrder();
        providerOrder.setId(orderId);
        providerOrderService.save(providerOrder);

        // 确认消息可以被发送
        if (future.get() == 1) {
            prepareMsgService.confirmMsgToSend(orderId, this.getClass().getSimpleName());
        }
    }

    @GetMapping("check")
    public boolean check(String id) {
        return providerOrderService.findById(id) != null;
    }
}
