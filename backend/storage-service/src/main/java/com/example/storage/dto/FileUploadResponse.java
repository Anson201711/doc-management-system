package com.example.storage.dto;

import lombok.Data;

/**
 * File Upload Response DTO
 */
@Data
public class FileUploadResponse {
    
    /**
     * File ID
     */
    private Long fileId;
    
    /**
     * File name
     */
    private String fileName;
    
    /**
     * Original file name
     */
    private String originalName;
    
    /**
     * File size in bytes
     */
    private Long fileSize;
    
    /**
     * MIME type
     */
    private String contentType;
    
    /**
     * File URL
     */
    private String fileUrl;
    
    /**
     * File hash (MD5)
     */
    private String fileHash;
    
    /**
     * Upload timestamp
     */
    private String uploadTime;
}