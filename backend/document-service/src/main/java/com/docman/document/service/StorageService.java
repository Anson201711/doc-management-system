package com.docman.document.service;

import com.docman.document.entity.Attachment;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件存储服务接口
 */
public interface StorageService {
    
    /**
     * 上传文件
     */
    Attachment upload(Long documentId, MultipartFile file, Long uploaderId);
    
    /**
     * 下载文件
     */
    byte[] download(Long attachmentId);
    
    /**
     * 获取文档附件列表
     */
    List<Attachment> listByDocumentId(Long documentId);
    
    /**
     * 删除附件
     */
    void delete(Long attachmentId);
    
    /**
     * 获取预签名上传URL
     */
    String getPresignedUploadUrl(Long documentId, String fileName);
    
    /**
     * 获取预签名下载URL
     */
    String getPresignedDownloadUrl(String storageKey);
}