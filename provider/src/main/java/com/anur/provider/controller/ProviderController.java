package com.anur.provider.controller;

import com.anur.common.Constant;
import com.anur.config.ArtistConfiguration;
import com.anur.model.TestMsg;
import com.anur.provider.service.TransactionMsgService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anur IjuoKaruKas on 2018/5/8
 */

@RestController
public class ProviderController {

    @Autowired
    private TransactionMsgService transactionMsgService;

    @Autowired
    private ArtistConfiguration artistConfiguration;

    @GetMapping
    public void test() {
        TestMsg testMsg = new TestMsg();
        testMsg.setContent("这是一条测试消息");
        testMsg.setDate(new Date());
        String testMsgStr = new Gson().toJson(testMsg);

        String routingKey = "test.key.testing";
        Map<String, String> map = new HashMap<>();
        map.put("orderId", "10086");
        map.put("state", "CLEAR");
        String mapStr = new Gson().toJson(map);

        System.out.println("MSG: " + testMsgStr);
        String msgId = transactionMsgService.prepareMsg(testMsgStr, routingKey, Constant.TEST_EXCHANGE, mapStr, artistConfiguration.getArtist());
        transactionMsgService.confirmMsgToSend(msgId);
    }
}
