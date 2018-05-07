package com.anur.messageserver.controller;

import com.anur.messageserver.common.Result;
import com.anur.messageserver.model.TransactionMsg;
import com.anur.messageserver.service.TransactionMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Created by Anur IjuoKaruKas on 2018/5/7
 */
@RestController
public class TestController {

    @Autowired
    TransactionMsgService transactionMsgService;

    @GetMapping("/test")
    public Object test() {
        TransactionMsg transactionMsg = new TransactionMsg();
        transactionMsg.setId(UUID.randomUUID().toString());
        transactionMsgService.save(transactionMsg);

        return transactionMsgService.findAll();
    }
}
