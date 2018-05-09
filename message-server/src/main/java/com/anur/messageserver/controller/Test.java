package com.anur.messageserver.controller;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Created by Anur IjuoKaruKas on 2018/5/9
 */
@Component
public class Test {

    @Async
    public void sout() {
        try {
            System.out.println(111111);
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(1111111);
    }
}
