package com.anur.messageapi.api;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Created by Anur IjuoKaruKas on 2018/5/8
 */
public interface TransactionMsgApi {

    /**
     * 预发送消息，先将消息保存到消息中心
     */
    @RequestMapping(value = "prepare", method = RequestMethod.GET)
    int prepareMsg(
            @RequestParam("id") String id,
            @RequestParam("msg") String msg,
            @RequestParam("routingKey") String routingKey,
            @RequestParam("exchange") String exchange,
            @RequestParam("paramMap") String paramMap,
            @RequestParam("artist") String artist);

    /**
     * 生产者确认消息可投递
     */
    @RequestMapping(value = "confirm", method = RequestMethod.GET)
    int confirmMsgToSend(@RequestParam("id") String id, @RequestParam("caller") String caller);

    /**
     * 向队列投递消息
     */
    @RequestMapping(value = "send", method = RequestMethod.GET)
    void sendMsg(@RequestParam("id") String id);

    /**
     * 消费者确认消费成功
     */
    @RequestMapping(value = "ack", method = RequestMethod.GET)
    int acknowledgement(@RequestParam("id") String id,
                        @RequestParam("artist") String artist);
}
