package com.anur.provider.controller;

import com.anur.messageapi.config.ArtistConfiguration;
import com.anur.provider.service.TransactionMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

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
    public String test() {
        String msgId = transactionMsgService.prepareMsg("test", "key", "exchange", "param", artistConfiguration.getArtist());
        if (Math.random() < 0.5) {
            throw new NullPointerException();
        }
        return transactionMsgService.confirmMsgToSend(msgId) + "";
    }

}
