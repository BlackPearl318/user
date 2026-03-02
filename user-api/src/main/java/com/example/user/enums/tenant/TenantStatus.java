package com.example.user.enums.tenant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TenantStatus {

    NOT_ACTIVATE(0, "未激活"),
    ACTIVATE(1, "已激活");

    @EnumValue
    @JsonValue
    private final int code;

    private final String message;

    TenantStatus(int code, String message) {
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
