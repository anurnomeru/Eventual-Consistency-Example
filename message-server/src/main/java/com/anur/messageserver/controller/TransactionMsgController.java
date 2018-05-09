package com.anur.messageserver.controller;

import com.anur.messageapi.api.TransactionMsgApi;
import com.anur.messageserver.service.TransactionMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;

/**
 * Created by Anur IjuoKaruKas on 2018/5/8
 */
@RestController
public class TransactionMsgController implements TransactionMsgApi {

    @Autowired
    private TransactionMsgService transactionMsgService;

    @Override
    public String prepareMsg(@NotNull Object msg, @NotNull String routingKey, @NotNull String exchange, @NotNull String paramMap, @NotNull String artist) {
        transactionMsgService.sendMsg("5f2a1edc-de27-4edd-97e1-80556d1c08850.05825923041095116");
        return transactionMsgService.prepareMsg(msg, routingKey, exchange, paramMap, artist);
    }

    @Override
    public int confirmMsgToSend(String id) {
        return transactionMsgService.confirmMsgToSend(id);
    }

    @Override
    public void sendMsg(String id) {
        transactionMsgService.sendMsg(id);
    }

    @Override
    public int acknowledgement(String id, String artist) {
        return transactionMsgService.acknowledgement(id, artist);
    }
}
