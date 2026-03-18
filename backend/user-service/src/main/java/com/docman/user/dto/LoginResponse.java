package com.docman.user.dto;

import lombok.Data;

/**
 * 登录响应
 */
@Data
public class LoginResponse {
    
    private String token;
    
    private String tokenType = "Bearer";
    
    private Long expiresIn;
    
    private UserInfo user;
    
    public LoginResponse(String token, Long expiresIn, UserInfo user) {
        this.token = token;
        this.expiresIn = expiresIn;
        this.user = user;
    }
}