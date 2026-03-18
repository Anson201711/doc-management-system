package com.example.storage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Storage Service Application
 * File storage service based on MinIO (S3 compatible)
 */
@SpringBootApplication
@EnableDiscoveryClient
public class StorageServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(StorageServiceApplication.class, args);
    }
}