package com.example.document.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class DocumentCreateDTO {
    
    @NotBlank(message = "文档标题不能为空")
    private String title;
    
    private String content;
    
    private String docType;
    
    private Long creatorId;
    
    private Long folderId;
    
    private String fileUrl;
}