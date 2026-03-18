package com.docman.document.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 文档创建DTO
 */
@Data
public class DocumentCreateDTO {
    
    @NotBlank(message = "文档标题不能为空")
    private String title;
    
    private String content;
    
    private Long folderId;
    
    private String documentType;
    
    private String[] tags;
}