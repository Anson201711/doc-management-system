package com.docman.user.dto;

import lombok.Data;

/**
 * 用户信息更新请求
 */
@Data
public class UserUpdateRequest {
    
    private String fullName;
    
    private String avatarUrl;
    
    private String phone;
    
    private Long departmentId;
}