package com.example.collab.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Document Service Feign Client
 */
@FeignClient(name = "document-service", path = "/api/v1/documents")
public interface DocumentClient {

    /**
     * Check if document exists
     */
    @GetMapping("/{id}/exists")
    boolean checkDocumentExists(@PathVariable("id") Long id);

    /**
     * Get document basic info
     */
    @GetMapping("/{id}")
    Object getDocument(@PathVariable("id") Long id);
}