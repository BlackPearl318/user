package com.example.user.enums;

public enum UserStatus {

    DEACTIVATING(0, "注销中"),
    SAFE(1, "安全"),
    FROZEN(2, "冻结"),
    BANNED(3, "封禁"),
    WARNING(4, "警示"),
    DANGEROUS(5, "危险"),
    DELETED(6, "已删除"),
    UNKNOWN(7, "不存在");

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

    @Override
    public String toString() {
        return "UserStatus{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }

    // 根据 code 获取枚举实例
    public static UserStatus fromCode(int code) {
        for (UserStatus status : UserStatus.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return UNKNOWN; // 如果没有匹配的 code，则返回 UNKNOWN
    }
}

