package com.docman.document.dto;

import lombok.Data;

/**
 * 文件夹DTO
 */
@Data
public class FolderDTO {
    
    private String name;
    
    private Long parentId;
}