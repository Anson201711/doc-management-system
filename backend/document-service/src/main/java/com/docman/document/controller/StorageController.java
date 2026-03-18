package com.docman.document.controller;

import com.docman.document.entity.Attachment;
import com.docman.document.service.StorageService;
import com.docman.document.vo.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文件存储控制器
 */
@Tag(name = "文件存储管理")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StorageController {
    
    private final StorageService storageService;
    
    @Operation(summary = "上传文档附件")
    @PostMapping("/documents/{docId}/attachments")
    public Result<Attachment> upload(@PathVariable Long docId,
                                       @RequestParam("file") MultipartFile file,
                                       @RequestHeader("X-User-Id") Long userId) {
        Attachment attachment = storageService.upload(docId, file, userId);
        return Result.success(attachment);
    }
    
    @Operation(summary = "获取文档附件列表")
    @GetMapping("/documents/{docId}/attachments")
    public Result<List<Attachment>> listAttachments(@PathVariable Long docId) {
        List<Attachment> attachments = storageService.listByDocumentId(docId);
        return Result.success(attachments);
    }
    
    @Operation(summary = "下载附件")
    @GetMapping("/attachments/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id) {
        byte[] data = storageService.download(id);
        
        ByteArrayResource resource = new ByteArrayResource(data);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"download\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(data.length)
                .body(resource);
    }
    
    @Operation(summary = "删除附件")
    @DeleteMapping("/attachments/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        storageService.delete(id);
        return Result.success();
    }
    
    @Operation(summary = "获取预签名上传URL")
    @GetMapping("/documents/{docId}/attachments/presigned-upload")
    public Result<String> getPresignedUploadUrl(@PathVariable Long docId,
                                                  @RequestParam String fileName) {
        String url = storageService.getPresignedUploadUrl(docId, fileName);
        return Result.success(url);
    }
}