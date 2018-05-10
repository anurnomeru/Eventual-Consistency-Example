package com.anur.provider.controller;

import com.anur.config.ArtistConfiguration;
import com.anur.model.TestMsg;
import com.anur.provider.service.TransactionMsgService;
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
    private void request() {
        TestMsg testMsg = new TestMsg();
        testMsg.setContent("这是一条测试消息");
        testMsg.setDate(new Date());

        String routingKey = "testKey";
        String exchangeName = "testExchange";
        HashMap hashMap = new HashMap();
        hashMap.put("orderId", "10086");
        hashMap.put("state", "CLEAR");

        String msgId = transactionMsgService.prepareMsg(testMsg, routingKey, exchangeName, hashMap, artistConfiguration.getArtist());
        transactionMsgService.confirmMsgToSend(msgId);
    }
}
