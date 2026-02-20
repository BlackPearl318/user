package com.example.user.enums;

// 用户权限
public enum UserRoleType {

    USER(1, "用户"),
    TENANT(2, "租户"),
    ADMIN(3, "管理员");

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

    @Override
    public String toString() {
        return "UserRoleType{" +
                "code=" + code +
                ", message='" + message + '\'' +
                '}';
    }

    // 根据 code 获取枚举实例
    public static UserRoleType fromCode(int code) {
        for (UserRoleType status : UserRoleType.values()) {
            if (status.getCode() == code) {
                return status;
            }
        }
        return USER; // 如果没有匹配的 code，则返回 USER
    }
}
