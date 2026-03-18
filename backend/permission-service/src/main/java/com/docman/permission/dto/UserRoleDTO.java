package com.docman.permission.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;

/**
 * 用户角色关联DTO
 */
@Data
public class UserRoleDTO {
    
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    @NotNull(message = "角色ID不能为空")
    private Long roleId;
}