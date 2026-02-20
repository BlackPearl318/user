package com.example.user.enums;

// 通知类型
public enum NotificationType {

    SECURITY(1,"安全通知"),
    PROFILE(2,"资料更新"),
    SYSTEM(100,"系统通知");

    private final int code;
    private final String message;

    NotificationType(int code, String message) {
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
        return "NotificationType{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }

    // 根据 code 获取枚举实例
    public static NotificationType fromCode(int code) {
        for (NotificationType status : NotificationType.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return SYSTEM; // 默认返回 SYSTEM
    }
}
