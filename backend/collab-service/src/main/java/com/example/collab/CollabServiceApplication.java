package com.example.collab;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Collab Service Application - Comments and Annotations Service
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.example.collab.mapper")
public class CollabServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CollabServiceApplication.class, args);
    }
}