package com.example.user.enums.user;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum UserStatus {

    DEACTIVATING(0, "注销中"),
    SAFE(1, "安全"),
    FROZEN(2, "冻结"),
    BANNED(3, "封禁"),
    WARNING(4, "警示"),
    DANGEROUS(5, "危险"),
    DELETED(6, "已删除"),
    UNKNOWN(7, "不存在");

    @EnumValue
    @JsonValue
    private final int code;

    private final String message;

    UserStatus(int code, String message) {
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

