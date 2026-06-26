package com.telerikacademy.com.springdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EntityScan(basePackages = "com.telerikacademy.com.springdemo.models")
@EnableJpaRepositories(basePackages = "com.telerikacademy.com.springdemo.repositories")
@EnableMethodSecurity(prePostEnabled = true)
public class TelerikAlphaSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(TelerikAlphaSpringApplication.class, args);
    }

}
