package com.docman.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 修改密码请求
 */
@Data
@Schema(description = "修改密码请求")
public class ChangePasswordRequest {
    
    @NotBlank(message = "原密码不能为空")
    @Schema(description = "原密码")
    private String oldPassword;
    
    @NotBlank(message = "新密码不能为空")
    @Schema(description = "新密码")
    private String newPassword;
}