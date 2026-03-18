package com.example.storage.controller;

import com.example.storage.dto.FileUploadRequest;
import com.example.storage.dto.FileUploadResponse;
import com.example.storage.entity.FileInfo;
import com.example.storage.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Storage Controller
 * Provides REST API for file storage
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/storage")
@RequiredArgsConstructor
public class StorageController {
    
    private final StorageService storageService;
    
    /**
     * Upload file
     */
    @PostMapping("/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "documentId", required = false) Long documentId,
            @RequestParam(value = "customFileName", required = false) String customFileName,
            @RequestParam(value = "uploadUserId", required = false) Long uploadUserId,
            @RequestParam(value = "uploadUserName", required = false) String uploadUserName) {
        
        log.info("Uploading file: {}, size: {}", file.getOriginalFilename(), file.getSize());
        
        FileUploadRequest request = new FileUploadRequest();
        request.setFile(file);
        request.setDocumentId(documentId);
        request.setCustomFileName(customFileName);
        request.setUploadUserId(uploadUserId);
        request.setUploadUserName(uploadUserName);
        
        FileUploadResponse response = storageService.uploadFile(request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Delete file
     */
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable Long fileId) {
        log.info("Deleting file: {}", fileId);
        storageService.deleteFile(fileId);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Get file info
     */
    @GetMapping("/{fileId}")
    public ResponseEntity<FileInfo> getFileInfo(@PathVariable Long fileId) {
        FileInfo fileInfo = storageService.getFileInfo(fileId);
        if (fileInfo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(fileInfo);
    }
    
    /**
     * Get files by document ID
     */
    @GetMapping("/document/{documentId}")
    public ResponseEntity<List<FileInfo>> getFilesByDocumentId(@PathVariable Long documentId) {
        List<FileInfo> files = storageService.getFilesByDocumentId(documentId);
        return ResponseEntity.ok(files);
    }
    
    /**
     * Get presigned URL for download
     */
    @GetMapping("/{fileId}/download")
    public ResponseEntity<Map<String, String>> getDownloadUrl(
            @PathVariable Long fileId,
            @RequestParam(value = "expiry", defaultValue = "60") int expiryMinutes) {
        
        String url = storageService.getPresignedUrl(fileId, expiryMinutes);
        Map<String, String> response = new HashMap<>();
        response.put("url", url);
        response.put("expiresIn", expiryMinutes + " minutes");
        return ResponseEntity.ok(response);
    }
    
    /**
     * Initialize bucket
     */
    @PostMapping("/init-bucket")
    public ResponseEntity<Void> initBucket() {
        storageService.initBucket();
        return ResponseEntity.ok().build();
    }
}