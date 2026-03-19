package com.example.storage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.storage.dto.FileUploadRequest;
import com.example.storage.dto.FileUploadResponse;
import com.example.storage.entity.FileInfo;
import com.example.storage.repository.FileInfoRepository;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.List;
import java.util.UUID;

/**
 * Storage Service
 * Provides file storage functionality using MinIO
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StorageService extends ServiceImpl<FileInfoRepository, FileInfo> {
    
    private final MinioClient minioClient;
    
    @Value("${minio.bucket-name}")
    private String bucketName;
    
    @Value("${minio.public-url}")
    private String publicUrl;
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    
    /**
     * Initialize bucket
     */
    public void initBucket() {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                log.info("Created bucket: {}", bucketName);
            }
        } catch (Exception e) {
            log.error("Failed to initialize bucket", e);
            throw new RuntimeException("Failed to initialize bucket", e);
        }
    }
    
    /**
     * Upload file
     */
    @Transactional(rollbackFor = Exception.class)
    public FileUploadResponse uploadFile(FileUploadRequest request) {
        MultipartFile file = request.getFile();
        
        try {
            // Initialize bucket if not exists
            initBucket();
            
            // Generate object name
            String extension = getFileExtension(file.getOriginalFilename());
            String objectName = generateObjectName(extension);
            
            // Calculate file hash
            String fileHash = calculateMD5(file.getBytes());
            
            // Upload to MinIO
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            
            // Save file info to database
            FileInfo fileInfo = new FileInfo();
            fileInfo.setFileName(objectName);
            fileInfo.setOriginalName(request.getCustomFileName() != null ? 
                    request.getCustomFileName() : file.getOriginalFilename());
            fileInfo.setFileSize(file.getSize());
            fileInfo.setContentType(file.getContentType());
            fileInfo.setExtension(extension);
            fileInfo.setObjectName(objectName);
            fileInfo.setBucketName(bucketName);
            fileInfo.setFileUrl(publicUrl + "/" + bucketName + "/" + objectName);
            fileInfo.setFileHash(fileHash);
            fileInfo.setUploadUserId(request.getUploadUserId());
            fileInfo.setUploadUserName(request.getUploadUserName());
            fileInfo.setDocumentId(request.getDocumentId());
            fileInfo.setStatus("active");
            fileInfo.setCreatedAt(LocalDateTime.now());
            fileInfo.setUpdatedAt(LocalDateTime.now());
            
            save(fileInfo);
            
            // Build response
            FileUploadResponse response = new FileUploadResponse();
            response.setFileId(fileInfo.getId());
            response.setFileName(fileInfo.getFileName());
            response.setOriginalName(fileInfo.getOriginalName());
            response.setFileSize(fileInfo.getFileSize());
            response.setContentType(fileInfo.getContentType());
            response.setFileUrl(fileInfo.getFileUrl());
            response.setFileHash(fileInfo.getFileHash());
            response.setUploadTime(LocalDateTime.now().format(DATE_FORMATTER));
            
            log.info("File uploaded successfully: {}", fileInfo.getId());
            return response;
        } catch (Exception e) {
            log.error("Failed to upload file", e);
            throw new RuntimeException("Failed to upload file", e);
        }
    }
    
    /**
     * Delete file
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteFile(Long fileId) {
        FileInfo fileInfo = getById(fileId);
        if (fileInfo == null) {
            throw new RuntimeException("File not found");
        }
        
        try {
            // Delete from MinIO
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(fileInfo.getBucketName())
                    .object(fileInfo.getObjectName())
                    .build());
            
            // Update status in database
            fileInfo.setStatus("deleted");
            fileInfo.setUpdatedAt(LocalDateTime.now());
            updateById(fileInfo);
            
            log.info("File deleted successfully: {}", fileId);
        } catch (Exception e) {
            log.error("Failed to delete file", e);
            throw new RuntimeException("Failed to delete file", e);
        }
    }
    
    /**
     * Get file info
     */
    public FileInfo getFileInfo(Long fileId) {
        return getById(fileId);
    }
    
    /**
     * Get files by document ID
     */
    public List<FileInfo> getFilesByDocumentId(Long documentId) {
        return list(new LambdaQueryWrapper<FileInfo>()
                .eq(FileInfo::getDocumentId, documentId)
                .eq(FileInfo::getStatus, "active")
                .orderByDesc(FileInfo::getCreatedAt));
    }
    
    /**
     * Generate presigned URL for download
     */
    public String getPresignedUrl(Long fileId, int expiryMinutes) {
        FileInfo fileInfo = getById(fileId);
        if (fileInfo == null) {
            throw new RuntimeException("File not found");
        }
        
        try {
            return minioClient.getPresignedObjectUrl(
                    io.minio.GetPresignedObjectUrlArgs.builder()
                            .bucket(fileInfo.getBucketName())
                            .object(fileInfo.getObjectName())
                            .expiry(expiryMinutes)
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to generate presigned URL", e);
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }
    
    private String generateObjectName(String extension) {
        String datePath = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return datePath + "/" + UUID.randomUUID() + (extension.isEmpty() ? "" : "." + extension);
    }
    
    private String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot + 1).toLowerCase() : "";
    }
    
    private String calculateMD5(byte[] data) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(data);
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
}