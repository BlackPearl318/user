package com.example.user.dto.request;

import com.example.user.enums.user.UserRoleType;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 * 手机号登录请求 DTO
 */
@Data
public class LoginByPhoneRequest {

    /**
     * 手机号
     * 中国大陆手机号校验：1开头 + 10位数字
     */
    @NotBlank(message = "手机号不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;

    /**
     * 短信验证码
     */
    @NotBlank(message = "验证码不能为空")
    @Size(min = 6, max = 6, message = "验证码长度不正确")
    private String code;

    /**
     * 用户角色
     */
    @NotNull(message = "未知的用户角色")
    private UserRoleType role;
}