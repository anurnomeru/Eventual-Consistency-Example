package com.anur.consumer.config;

import com.anur.common.Constant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Created by Anur IjuoKaruKas on 2018/5/10
 */
@Configuration
public class RabbitConfiguration {

    @Bean
    Queue queue() {
        return new Queue(Constant.QUEUE_NAME, false);
    }

    @Bean
    TopicExchange exchange() {
        return new TopicExchange(Constant.TEST_EXCHANGE);
    }

    @Bean
    Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("test.key.*");
    }

}
