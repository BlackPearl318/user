package com.example.user.dto.request;

import com.example.user.enums.user.Gender;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * 修改个人资料请求
 */
@Data
public class ResetProfileRequest {

    /**
     * 昵称
     */
    @Size(min = 1, max = 50, message = "昵称长度必须在1-50字符之间")
    private String name;

    /**
     * 性别
     */
    @NotNull(message = "性别不能为空")
    private Gender gender;

    /**
     * 出生日期
     */
    @Past(message = "出生日期必须是过去时间")
    private LocalDate dateOfBirth;

    /**
     * 个人简介
     */
    @Size(max = 500, message = "个人简介不能超过500字")
    private String biography;
}