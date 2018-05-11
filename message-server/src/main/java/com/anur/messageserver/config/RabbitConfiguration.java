package com.anur.messageserver.config;

import com.anur.config.ArtistConfiguration;
import com.anur.messageserver.service.TransactionMsgService;
import org.omg.IOP.TransactionService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

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

    @Bean
    public AmqpTemplate amqpTemplate() {
        // 开启returnCallBack
        // 需要配置 publisher-returns: true
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String correlationId = message.getMessageProperties().getCorrelationId();
            System.out.println(String.format("消息：%s 发送失败, 应答码: %s 原因：%s 交换机: %s  路由键: %s", correlationId, replyCode, replyText, exchange, routingKey));
        });
        // 消息确认
        // 需要配置 publisher-returns: true
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (ack) {
                transactionMsgService.acknowledgement(correlationData.getId(), artistConfiguration.getArtist());
                System.out.println(String.format("消息发送到exchange成功, id: %s", correlationData.getId()));
            } else {
                System.out.println(String.format("消息发送到exchange失败, id: %s, 原因: %s", cause));
            }
        });
        return rabbitTemplate;
    }
}
