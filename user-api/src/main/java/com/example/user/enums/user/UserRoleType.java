package com.example.user.enums.user;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

// 用户权限
public enum UserRoleType {

    USER(1, "用户"),
    TENANT(2, "租户"),
    ADMIN(3, "管理员");

    @EnumValue
    @JsonValue
    private final int code;

    private final String message;

    UserRoleType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
