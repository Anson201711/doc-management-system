package com.example.document.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DocumentResponseDTO {
    private Long id;
    private String title;
    private String content;
    private String docType;
    private Long creatorId;
    private Long folderId;
    private String status;
    private String version;
    private Long fileSize;
    private String fileUrl;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}