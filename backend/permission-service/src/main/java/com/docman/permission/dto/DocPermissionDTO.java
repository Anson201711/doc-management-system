package com.docman.permission.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 文档权限DTO - 文档级别的细粒度权限控制
 */
@Data
public class DocPermissionDTO {
    
    private Long id;
    
    @NotNull(message = "文档ID不能为空")
    private Long documentId;
    
    private Long userId;
    
    private Long roleId;
    
    @NotNull(message = "权限类型不能为空")
    private String permissionType;  // read, write, admin
    
    private LocalDateTime expiryDate;
    
    private List<Long> userIds;
    
    private List<Long> roleIds;
}