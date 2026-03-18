package com.docman.permission.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 权限创建/更新DTO
 */
@Data
public class PermissionDTO {
    
    private Long id;
    
    @NotBlank(message = "权限名称不能为空")
    private String name;
    
    @NotBlank(message = "资源不能为空")
    private String resource;
    
    @NotBlank(message = "操作不能为空")
    private String action;
    
    private String description;
}