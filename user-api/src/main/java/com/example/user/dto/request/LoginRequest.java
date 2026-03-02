package com.example.user.dto.request;

import com.example.user.enums.user.UserRoleType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
// 账号密码登录时的数据传输对象
public class LoginRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 8, max = 20, message = "用户名长度不正确")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 20, message = "密码长度不正确")
    private String password;

    @NotNull(message = "未知的用户")
    private UserRoleType role;
}
