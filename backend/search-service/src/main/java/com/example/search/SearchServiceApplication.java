package com.example.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Search Service Application
 * Full-text search service based on Elasticsearch
 */
@SpringBootApplication
@EnableDiscoveryClient
public class SearchServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SearchServiceApplication.class, args);
    }
}