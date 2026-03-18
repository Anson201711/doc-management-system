package com.docman.permission.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

/**
 * 角色创建/更新DTO
 */
@Data
public class RoleDTO {
    
    private Long id;
    
    @NotBlank(message = "角色名称不能为空")
    private String name;
    
    private String description;
    
    private String status;
    
    private List<Long> permissionIds;
}