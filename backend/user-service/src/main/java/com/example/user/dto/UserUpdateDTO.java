package com.example.user.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private String department;
    private String position;
    private Integer status;
}