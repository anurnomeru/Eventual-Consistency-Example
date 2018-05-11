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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    public void test() {
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

        // 执行业务
        ProviderOrder providerOrder = new ProviderOrder();
        providerOrder.setId(orderId);
        providerOrderService.save(providerOrder);

        // 确认消息可以被发送
        transactionMsgService.confirmMsgToSend(msgId);
    }
}
