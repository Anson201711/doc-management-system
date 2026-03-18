package com.example.document.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.example.document.dto.DocumentCreateDTO;
import com.example.document.dto.DocumentResponseDTO;
import com.example.document.dto.DocumentUpdateDTO;
import com.example.document.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {
    
    private final DocumentService documentService;
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createDocument(@Valid @RequestBody DocumentCreateDTO dto) {
        DocumentResponseDTO document = documentService.createDocument(dto);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "文档创建成功");
        response.put("data", document);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateDocument(@PathVariable Long id, @RequestBody DocumentUpdateDTO dto) {
        dto.setId(id);
        DocumentResponseDTO document = documentService.updateDocument(dto);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "文档更新成功");
        response.put("data", document);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "文档删除成功");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDocumentById(@PathVariable Long id) {
        DocumentResponseDTO document = documentService.getDocumentById(id);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", document);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllDocuments() {
        List<DocumentResponseDTO> documents = documentService.getAllDocuments();
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", documents);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/page")
    public ResponseEntity<Map<String, Object>> getDocumentsPage(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        IPage<DocumentResponseDTO> page = documentService.getDocumentsPage(pageNum, pageSize);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", page);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/creator/{creatorId}")
    public ResponseEntity<Map<String, Object>> getDocumentsByCreator(@PathVariable Long creatorId) {
        List<DocumentResponseDTO> documents = documentService.getDocumentsByCreator(creatorId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", documents);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/folder/{folderId}")
    public ResponseEntity<Map<String, Object>> getDocumentsByFolder(@PathVariable Long folderId) {
        List<DocumentResponseDTO> documents = documentService.getDocumentsByFolder(folderId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("data", documents);
        return ResponseEntity.ok(response);
    }
}