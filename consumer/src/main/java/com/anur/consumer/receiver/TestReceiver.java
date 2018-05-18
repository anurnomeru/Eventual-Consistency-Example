package com.anur.consumer.receiver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.anur.common.Constant;
import com.anur.config.ArtistConfiguration;
import com.anur.consumer.feignservice.TransactionMsgService;
import com.anur.model.TestMsg;
import com.google.gson.Gson;
import lombok.extern.java.Log;
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
@Log
public class TestReceiver implements ChannelAwareMessageListener {

    private AtomicInteger atomicInteger = new AtomicInteger(0);

    private AtomicInteger atomicIntegerAll = new AtomicInteger(0);

    private Long timeStart = null;

    private Long timeEnd = null;

    private boolean start = true;

    @Scheduled(cron = "*/1 * * * * *")
    public void printer() {
        int count = atomicInteger.get();
        if (count != 0) {
            if (start) {
                timeStart = System.currentTimeMillis();
                start = false;
            } else {
                if (timeEnd == null) {
                    timeEnd = System.currentTimeMillis();
                } else {
                    log.info("cost : " + (timeEnd - timeStart) / 1000 + "s");
                }
            }
        }
        atomicInteger = new AtomicInteger(0);
        log.info("consume per/sec： " + count);
        log.info("consume total: " + atomicIntegerAll.get());
    }


    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        boolean success = false;
        TestMsg testMsg = null;
        atomicInteger.incrementAndGet();
        atomicIntegerAll.incrementAndGet();

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
