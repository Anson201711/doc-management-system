package com.example.permission;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.example.permission.repository")
public class PermissionServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PermissionServiceApplication.class, args);
    }
}