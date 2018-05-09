package com.anur.messageserver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.Data;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Objects;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

@EnableDiscoveryClient
@SpringBootApplication
@EnableAsync
public class MessageServerApplication {

    @Autowired
    Environment environment;

    public String getArtist() {
        return Objects.requireNonNull(environment.getProperty("spring.application.name")).toUpperCase();
    }

    public static void main(String[] args) {
        SpringApplication.run(MessageServerApplication.class, args);
    }

}

