package com.anur.consumer.receiver;

import com.anur.common.Constant;
import com.anur.model.TestMsg;
import com.google.gson.Gson;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.rabbitmq.client.Channel;

import java.io.IOException;


/**
 * Created by Anur IjuoKaruKas on 2018/5/10
 */
@Component
public class TestReceiver {

    @RabbitListener(queues = Constant.QUEUE_NAME)
    public void receivMsg(String msg, Message message, Channel channel) throws IOException {
        System.out.println("MSG: " + msg);
        TestMsg testMsg = new Gson().fromJson(msg, TestMsg.class);
        System.out.println("Receiver : " + testMsg.toString());

        long tag = message.getMessageProperties().getDeliveryTag();
        System.out.println(tag);
        channel.basicAck(tag, true);

    }
}
