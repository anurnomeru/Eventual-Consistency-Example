package com.anur.consumer.receiver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.anur.common.Constant;
import com.anur.config.ArtistConfiguration;
import com.anur.consumer.feignservice.TransactionMsgService;
import com.anur.model.TestMsg;
import com.google.gson.Gson;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Anur IjuoKaruKas on 2018/5/10
 */
@Component
public class TestReceiver implements ChannelAwareMessageListener {

//    private AtomicInteger atomicInteger = new AtomicInteger(0);
//
//    private AtomicInteger atomicIntegerAll = new AtomicInteger(0);

//    @Scheduled(cron = "*/5 * * * * *")
//    public void printer() {
//        System.out.println("每五秒消费：" + atomicInteger.get());
//        atomicInteger = new AtomicInteger(0);
//    }
//
//    @Scheduled(cron = "*/5 * * * * *")
//    public void printerAll() {
//        System.out.println("总消费：" + atomicIntegerAll.get());
//    }

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        boolean success = false;
        TestMsg testMsg = null;
//        atomicInteger.incrementAndGet();
//        atomicIntegerAll.incrementAndGet();

        try {
            testMsg = JSON.parseObject(new String(message.getBody()), new TypeReference<TestMsg>() {
            });
            success = true;
        } finally {
            if (success) {
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } else {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            }
        }
    }
}
