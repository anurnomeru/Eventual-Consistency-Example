package com.anur.messageserver.rabbitmq;

import com.anur.config.ArtistConfiguration;
import com.anur.messageserver.service.TransactionMsgService;
import lombok.extern.java.Log;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Anur IjuoKaruKas on 2018/5/10
 */
@Component
@Log
public class MsgSender implements RabbitTemplate.ReturnCallback, RabbitTemplate.ConfirmCallback, InitializingBean {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private TransactionMsgService transactionMsgService;

    @Autowired
    private ArtistConfiguration artistConfiguration;

    public void send(String exchange, String routingKey, String msg, CorrelationData correlationData) {

        Message message = MessageBuilder
                .withBody(msg.getBytes())
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .build();
        rabbitTemplate.convertAndSend(exchange, routingKey, message, correlationData);
    }

    // 实现ReturnCallback
    // 当消息发送出去找不到对应路由队列时，将会把消息退回
    // 如果有任何一个路由队列接收投递消息成功，则不会退回消息
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        log.info("Msg send to broker error: " + message.getMessageProperties().getCorrelationId());
    }

    // 实现ConfirmCallback
    // ACK=true仅仅标示消息已被Broker接收到，并不表示已成功投放至消息队列中
    // ACK=false标示消息由于Broker处理错误，消息并未处理成功
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (!ack) {
            log.info("Msg ack fail: " + correlationData.getId());
        }
        int result = transactionMsgService.acknowledgement(correlationData.getId(), artistConfiguration.getArtist());
        if (result == 0) {
            log.info("MSG: " + correlationData.getId() + "ACK SUCCESS BUT UPDATE ACK STATUS FAIL");
        }
    }

    // 实现InitializingBean
    // 设置消息送达、确认的方式
    @Override
    public void afterPropertiesSet() throws Exception {
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }
}
