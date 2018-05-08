package com.anur.messageserver.controller;

import com.anur.messageserver.model.TransactionMsg;
import com.anur.messageserver.service.TransactionMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Anur IjuoKaruKas on 2018/5/7
 */
@RestController
public class TestController {

    @Autowired
    TransactionMsgService transactionMsgService;

    @GetMapping("/test")
    public Object test() {
        transactionMsgService.prepareMsg(new TransactionMsg());
        return transactionMsgService.findAll();
    }
}
