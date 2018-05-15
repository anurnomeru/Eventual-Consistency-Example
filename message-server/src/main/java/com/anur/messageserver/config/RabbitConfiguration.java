package com.anur.messageserver.config;

import com.anur.config.ArtistConfiguration;
import com.anur.messageserver.service.TransactionMsgService;
import org.omg.IOP.TransactionService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Anur IjuoKaruKas on 2018/5/11
 */
@Configuration
public class RabbitConfiguration {
    @Resource
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private TransactionMsgService transactionMsgService;

    @Autowired
    private ArtistConfiguration artistConfiguration;

    private AtomicInteger atomicInteger = new AtomicInteger();

    private AtomicInteger atomicIntegerACK = new AtomicInteger();

    @Scheduled(cron = "*/5 * * * * *")
    public void printerACK() {
        System.out.println("总ACK次数：" + atomicIntegerACK.get());
    }

    @Scheduled(cron = "*/5 * * * * *")
    public void printer() {
        System.out.println("每五秒ACK次数：" + atomicInteger.get());
        atomicInteger = new AtomicInteger();
    }

    @Bean
    public AmqpTemplate amqpTemplate() {
        // 开启returnCallBack
        // 需要配置 publisher-returns: true
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setChannelTransacted(false);
        return rabbitTemplate;
    }
//
//        // 消息确认
//        // 需要配置 publisher-returns: true
//        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
//            if (ack) {
//                transactionMsgService.acknowledgement(correlationData.getId(), artistConfiguration.getArtist());
//                atomicInteger.incrementAndGet();
//                atomicIntegerACK.incrementAndGet();
//                System.out.println(String.format("MSG ACK SUCCESS, id: %s", correlationData.getId()));
//            } else {
//                System.out.println(String.format("消息发送到exchange失败, id: %s, 原因: %s", correlationData.getId(), cause));
//            }
//        });
//        return rabbitTemplate;
//    }

}
