package com.docman.document.dto;

import lombok.Data;

/**
 * 版本创建DTO
 */
@Data
public class VersionCreateDTO {
    
    private String content;
    
    private String changeSummary;
}