package com.example.search.controller;

import com.example.search.dto.SearchRequest;
import com.example.search.dto.SearchResponse;
import com.example.search.entity.SearchDocument;
import com.example.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Search Controller
 * Provides REST API for full-text search
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {
    
    private final SearchService searchService;
    
    /**
     * Search documents
     */
    @PostMapping
    public ResponseEntity<SearchResponse> search(@RequestBody SearchRequest request) {
        log.info("Search request: keyword={}, page={}, pageSize={}", 
                request.getKeyword(), request.getPage(), request.getPageSize());
        SearchResponse response = searchService.search(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get search suggestions (autocomplete)
     */
    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getSuggestions(@RequestParam String prefix) {
        List<String> suggestions = searchService.getSuggestions(prefix);
        return ResponseEntity.ok(suggestions);
    }
    
    /**
     * Index a document
     */
    @PostMapping("/index")
    public ResponseEntity<Void> indexDocument(@RequestBody SearchDocument document) {
        log.info("Indexing document: {}", document.getDocumentId());
        searchService.indexDocument(document);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Delete a document from index
     */
    @DeleteMapping("/index/{documentId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long documentId) {
        log.info("Deleting document from index: {}", documentId);
        searchService.deleteDocument(documentId);
        return ResponseEntity.ok().build();
    }
}