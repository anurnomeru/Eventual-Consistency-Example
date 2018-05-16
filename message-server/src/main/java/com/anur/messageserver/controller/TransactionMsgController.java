package com.anur.messageserver.controller;

import com.anur.messageapi.api.TransactionMsgApi;
import com.anur.messageserver.service.TransactionMsgService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Anur IjuoKaruKas on 2018/5/8
 */
@RestController
@Log
public class TransactionMsgController implements TransactionMsgApi {

    @Autowired
    private TransactionMsgService transactionMsgService;

    @Override
    public int prepareMsg(String id, String msg, String routingKey, String exchange, String paramMap, String artist) {
        return transactionMsgService.prepareMsg(id, msg, routingKey, exchange, paramMap, artist);
    }

    @Override
    public int confirmMsgToSend(String id, String caller) {
        int result = transactionMsgService.confirmMsgToSend(id, caller);
        if (result == 1) {
            transactionMsgService.sendMsg(id);
        } else {
            log.info("Msg confirm fail: " + id + ", caller is " + caller);
        }
        return result;
    }

    @Override
    public void sendMsg(String id) {
        transactionMsgService.sendMsg(id);
    }

    @Override
    public int acknowledgement(String id, String artist) {
        return transactionMsgService.acknowledgement(id, artist);
    }

    @GetMapping("test")
    public Object test() {
        return transactionMsgService.getUnConfirmList();
    }
}
