package com.example.user.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String phone;
    private String department;
    private String position;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}