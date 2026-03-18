package com.example.search.controller;

import com.example.search.dto.SearchRequestDTO;
import com.example.search.dto.SearchResultDTO;
import com.example.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController {
    
    private final SearchService searchService;
    
    /**
     * 全文搜索
     * GET /api/v1/search?q=关键词
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String docType,
            @RequestParam(required = false) Long creatorId,
            @RequestParam(required = false) Long folderId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        SearchRequestDTO request = new SearchRequestDTO();
        request.setKeyword(q);
        request.setDocType(docType);
        request.setCreatorId(creatorId);
        request.setFolderId(folderId);
        request.setStatus(status);
        request.setPageNum(pageNum);
        request.setPageSize(pageSize);
        
        List<SearchResultDTO> results = searchService.search(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "搜索成功");
        response.put("data", results);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 搜索文档列表
     * GET /api/v1/search/documents
     */
    @GetMapping("/documents")
    public ResponseEntity<Map<String, Object>> getDocuments(
            @RequestParam(required = false) String docType,
            @RequestParam(required = false) Long creatorId,
            @RequestParam(required = false) Long folderId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        
        SearchRequestDTO request = new SearchRequestDTO();
        request.setDocType(docType);
        request.setCreatorId(creatorId);
        request.setFolderId(folderId);
        request.setStatus(status);
        request.setPageNum(pageNum);
        request.setPageSize(pageSize);
        
        List<SearchResultDTO> results = searchService.search(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", results);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 重建索引
     * POST /api/v1/search/index
     */
    @PostMapping("/index")
    public ResponseEntity<Map<String, Object>> rebuildIndex() {
        searchService.rebuildIndex();
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "索引重建成功");
        
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }
    
    /**
     * 删除文档索引
     * DELETE /api/v1/search/index/{id}
     */
    @DeleteMapping("/index/{id}")
    public ResponseEntity<Map<String, Object>> deleteIndex(@PathVariable String id) {
        searchService.deleteDocument(id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "索引删除成功");
        
        return ResponseEntity.ok(response);
    }
}