package com.example.storage.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * File Upload Request DTO
 */
@Data
public class FileUploadRequest {
    
    /**
     * File to upload
     */
    private MultipartFile file;
    
    /**
     * Related document ID
     */
    private Long documentId;
    
    /**
     * Custom file name (optional)
     */
    private String customFileName;
    
    /**
     * Upload user ID
     */
    private Long uploadUserId;
    
    /**
     * Upload username
     */
    private String uploadUserName;
}