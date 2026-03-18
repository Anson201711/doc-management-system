package com.example.search.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Search Request DTO
 */
@Data
public class SearchRequest {
    
    /**
     * Search keywords
     */
    private String keyword;
    
    /**
     * Document types to filter
     */
    private List<String> documentTypes;
    
    /**
     * Owner IDs to filter
     */
    private List<Long> ownerIds;
    
    /**
     * Status to filter (active, archived)
     */
    private String status;
    
    /**
     * Page number (starting from 1)
     */
    private Integer page = 1;
    
    /**
     * Page size
     */
    private Integer pageSize = 20;
    
    /**
     * Fields to highlight
     */
    private List<String> highlightFields = List.of("title", "content");
}