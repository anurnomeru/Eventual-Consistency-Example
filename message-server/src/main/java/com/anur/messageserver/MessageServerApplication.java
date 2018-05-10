package com.anur.messageserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableDiscoveryClient
@SpringBootApplication
@EnableAsync
@ComponentScan(value = "com.anur.*")
public class MessageServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageServerApplication.class, args);
    }

}

