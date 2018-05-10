package com.anur.messageserver.controller;

import com.anur.exception.ServiceException;
import com.anur.messageapi.api.TransactionMsgApi;
import com.anur.messageserver.service.TransactionMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.HashMap;

/**
 * Created by Anur IjuoKaruKas on 2018/5/8
 */
@RestController
public class TransactionMsgController implements TransactionMsgApi {

    @Autowired
    private TransactionMsgService transactionMsgService;

    @Override
    public String prepareMsg(@NotNull Object msg, @NotNull String routingKey, @NotNull String exchange, @NotNull HashMap paramMap, @NotNull String artist) {
        return transactionMsgService.prepareMsg(msg, routingKey, exchange, paramMap, artist);
    }

    @Override
    public int confirmMsgToSend(String id) {
        int result = transactionMsgService.confirmMsgToSend(id);
        transactionMsgService.sendMsg(id);
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
}
