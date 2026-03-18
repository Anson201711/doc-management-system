package com.docman.user.dto;

import lombok.Data;

/**
 * 用户信息
 */
@Data
public class UserInfo {
    
    private Long id;
    
    private String username;
    
    private String email;
    
    private String fullName;
    
    private String avatarUrl;
    
    private String phone;
    
    private Long departmentId;
    
    private String departmentName;
    
    private String status;
}