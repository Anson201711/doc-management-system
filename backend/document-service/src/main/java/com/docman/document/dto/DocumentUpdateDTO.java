package com.docman.document.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 文档更新DTO
 */
@Data
public class DocumentUpdateDTO {
    
    @NotBlank(message = "文档标题不能为空")
    private String title;
    
    private String content;
    
    private String status;
    
    private String documentType;
    
    private String[] tags;
    
    private String changeSummary;
}