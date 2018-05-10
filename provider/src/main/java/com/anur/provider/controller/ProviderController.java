package com.anur.provider.controller;

import com.anur.config.ArtistConfiguration;
import com.anur.provider.Tester;
import com.anur.provider.service.TransactionMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Anur IjuoKaruKas on 2018/5/8
 */

@RestController
public class ProviderController {

    @Autowired
    private TransactionMsgService transactionMsgService;

    @Autowired
    private ArtistConfiguration artistConfiguration;

    private void request() {
        String msgId = transactionMsgService.prepareMsg("test", "key", "exchange", "param", artistConfiguration.getArtist());
        transactionMsgService.confirmMsgToSend(msgId);
    }

    @GetMapping
    public int test() {
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 0; i < 1000000; i++) {
            executorService.execute(this::request);
        }
        return 0;
    }


}
