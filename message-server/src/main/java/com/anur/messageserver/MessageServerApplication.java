package com.anur.messageserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.Data;

@SpringBootApplication
public class MessageServerApplication {

    public static void main(String[] args) {
        Car car = new Car();
        car.setName("五菱荣光");
        System.out.println(car.toString());
        SpringApplication.run(MessageServerApplication.class, args);
    }

    @Data
    public static class Car {
        String name;
    }
}
