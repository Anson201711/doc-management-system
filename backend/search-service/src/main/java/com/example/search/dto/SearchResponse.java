package com.example.search.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Search Response DTO
 */
@Data
public class SearchResponse {
    
    /**
     * Total hits
     */
    private Long totalHits;
    
    /**
     * Current page
     */
    private Integer page;
    
    /**
     * Page size
     */
    private Integer pageSize;
    
    /**
     * Total pages
     */
    private Integer totalPages;
    
    /**
     * Search results
     */
    private List<SearchResult> results;
    
    /**
     * Search suggestions
     */
    private List<String> suggestions;
    
    /**
     * Search execution time in milliseconds
     */
    private Long took;
    
    @Data
    public static class SearchResult {
        /**
         * Document ID
         */
        private Long documentId;
        
        /**
         * Document title
         */
        private String title;
        
        /**
         * Document content snippet
         */
        private String content;
        
        /**
         * Document type
         */
        private String documentType;
        
        /**
         * Owner name
         */
        private String ownerName;
        
        /**
         * Created timestamp
         */
        private String createdAt;
        
        /**
         * Highlighted fields
         */
        private Map<String, List<String>> highlights;
        
        /**
         * Score
         */
        private Float score;
    }
}