package com.docman.document.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.docman.document.config.MinioProperties;
import com.docman.document.entity.Attachment;
import com.docman.document.mapper.AttachmentMapper;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.presigner.PreSignedApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

/**
 * MinIO存储服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {
    
    private final AttachmentMapper attachmentMapper;
    private final MinioProperties minioProperties;
    private final MinioClient minioClient;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Attachment upload(Long documentId, MultipartFile file, Long uploaderId) {
        try {
            String storageKey = generateStorageKey(documentId, file.getOriginalFilename());
            
            // 上传到MinIO
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(storageKey)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            
            // 保存附件记录
            Attachment attachment = new Attachment();
            attachment.setDocumentId(documentId);
            attachment.setFileName(file.getOriginalFilename());
            attachment.setFileSize(file.getSize());
            attachment.setFileType(file.getContentType());
            attachment.setStorageKey(storageKey);
            attachment.setUploaderId(uploaderId);
            
            attachmentMapper.insert(attachment);
            
            return attachment;
        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }
    
    @Override
    public byte[] download(Long attachmentId) {
        Attachment attachment = attachmentMapper.selectById(attachmentId);
        if (attachment == null) {
            throw new RuntimeException("附件不存在");
        }
        
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(attachment.getStorageKey())
                            .build()
            ).transferTo(outputStream);
            
            return outputStream.toByteArray();
        } catch (Exception e) {
            log.error("文件下载失败", e);
            throw new RuntimeException("文件下载失败: " + e.getMessage());
        }
    }
    
    @Override
    public List<Attachment> listByDocumentId(Long documentId) {
        return attachmentMapper.selectByDocumentId(documentId);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long attachmentId) {
        Attachment attachment = attachmentMapper.selectById(attachmentId);
        if (attachment == null) {
            throw new RuntimeException("附件不存在");
        }
        
        try {
            // 从MinIO删除
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(minioProperties.getBucketName())
                            .object(attachment.getStorageKey())
                            .build()
            );
        } catch (Exception e) {
            log.warn("MinIO文件删除失败，将继续删除数据库记录", e);
        }
        
        // 删除数据库记录
        attachmentMapper.deleteById(attachmentId);
    }
    
    @Override
    public String getPresignedUploadUrl(Long documentId, String fileName) {
        String storageKey = generateStorageKey(documentId, fileName);
        
        try {
            ZonedDateTime expiration = ZonedDateTime.now().plus(Duration.ofSeconds(minioProperties.getUrlExpiration()));
            
            return minioClient.getPresignedObjectUrl(
                    io.minio.http.Method.PUT,
                    minioProperties.getBucketName(),
                    storageKey,
                    null,
                    null
            );
        } catch (Exception e) {
            log.error("生成预签名上传URL失败", e);
            throw new RuntimeException("生成预签名上传URL失败: " + e.getMessage());
        }
    }
    
    @Override
    public String getPresignedDownloadUrl(String storageKey) {
        try {
            ZonedDateTime expiration = ZonedDateTime.now().plus(Duration.ofSeconds(minioProperties.getUrlExpiration()));
            
            return minioClient.getPresignedObjectUrl(
                    io.minio.http.Method.GET,
                    minioProperties.getBucketName(),
                    storageKey,
                    null,
                    null
            );
        } catch (Exception e) {
            log.error("生成预签名下载URL失败", e);
            throw new RuntimeException("生成预签名下载URL失败: " + e.getMessage());
        }
    }
    
    private String generateStorageKey(Long documentId, String fileName) {
        return String.format("documents/%d/%s_%s", documentId, UUID.randomUUID(), fileName);
    }
}