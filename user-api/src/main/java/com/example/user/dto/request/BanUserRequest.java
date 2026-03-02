package com.example.user.dto.request;

import lombok.Data;

import javax.validation.constraints.*;

/**
 * 封禁用户请求
 */
@Data
public class BanUserRequest {

    /**
     * 封禁原因
     */
    @NotBlank(message = "封禁原因不能为空")
    @Size(max = 200, message = "封禁原因不能超过200字")
    private String banReason;

    /**
     * 封禁天数
     * -1 表示永久封禁
     */
    @NotNull(message = "封禁天数不能为空")
    @Min(value = -1, message = "封禁天数非法")
    @Max(value = 3650, message = "封禁天数不能超过10年")
    private Integer days;
}