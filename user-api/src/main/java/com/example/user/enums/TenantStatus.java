package com.example.user.enums;

public enum TenantStatus {

    NOT_ACTIVATE(0, "未激活"),
    ACTIVATE(1, "已激活");

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

    @Override
    public String toString() {
        return "TenantStatus{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }

    // 根据 code 获取枚举实例
    public static TenantStatus fromCode(int code) {
        for (TenantStatus status : TenantStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return NOT_ACTIVATE;
    }

}
