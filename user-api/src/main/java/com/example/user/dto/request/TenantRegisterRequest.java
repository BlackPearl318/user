package com.example.user.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 租户注册请求 DTO
 * 创建租户
 */
@Data
public class TenantRegisterRequest {

    /**
     * 租户名称
     */
    @NotBlank(message = "租户名称不能为空")
    @Size(min = 2, max = 50, message = "租户名称长度必须在 2-50 字符之间")
    private String tName;

    /**
     * 租户手机号
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 登录密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 20, message = "密码长度必须在 8-20 位之间")
    private String password1;

    /**
     * 确认密码
     */
    @NotBlank(message = "确认密码不能为空")
    @Size(min = 8, max = 20, message = "密码长度必须在 8-20 位之间")
    private String password2;

    /**
     * 短信验证码
     */
    @NotBlank(message = "验证码不能为空")
    @Size(min = 6, max = 6, message = "验证码长度不正确")
    private String code;
}