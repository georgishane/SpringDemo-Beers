package com.telerikacademy.com.springdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan(basePackages = "com.telerikacademy.com.springdemo.models")
public class TelerikAlphaSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelerikAlphaSpringApplication.class, args);
    }

}
