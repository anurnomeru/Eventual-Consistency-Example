package com.anur.consumer.receiver;

import com.anur.common.Constant;
import com.anur.model.TestMsg;
import com.google.gson.Gson;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;

/**
 * Created by Anur IjuoKaruKas on 2018/5/10
 */
@Component
public class TestReceiver {

    @RabbitListener(queues = Constant.QUEUE_NAME)
    public void receiveMsg(String msg, Message message, Channel channel) throws Exception {
        try {
            TestMsg testMsg = new Gson().fromJson(msg, TestMsg.class);
            System.out.println("Receiver : " + testMsg.toString());

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
        }
    }
}
