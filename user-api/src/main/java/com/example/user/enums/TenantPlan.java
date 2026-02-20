package com.example.user.enums;

import java.time.Duration;

public enum TenantPlan {

    /**
     * 免费版
     */
    FREE(
            "FREE",
            "免费版",
            50,                 // 最大用户数
            1024L,              // 最大存储空间 MB (1GB)
            true,               // 是否允许注册
            Duration.ofDays(0)  // 永不过期
    ),

    /**
     * 专业版
     */
    PRO(
            "PRO",
            "专业版",
            500,
            50_000L,             // 50GB
            true,
            Duration.ofDays(365)
    ),

    /**
     * 企业版
     */
    ENTERPRISE(
            "ENTERPRISE",
            "企业版",
            10_000,
            500_000L,           // 500GB
            true,
            Duration.ofDays(365 * 3L)
    );

    /**
     * 存储到数据库中的 code
     */
    private final String code;

    /**
     * 中文描述
     */
    private final String description;

    /**
     * 最大用户数
     */
    private final int maxUsers;

    /**
     * 最大存储空间（MB）
     */
    private final long maxStorageMb;

    /**
     * 是否允许开放注册
     */
    private final boolean allowRegister;

    /**
     * 套餐有效期
     */
    private final Duration validDuration;

    TenantPlan(
            String code,
            String description,
            int maxUsers,
            long maxStorageMb,
            boolean allowRegister,
            Duration validDuration
    ) {
        this.code = code;
        this.description = description;
        this.maxUsers = maxUsers;
        this.maxStorageMb = maxStorageMb;
        this.allowRegister = allowRegister;
        this.validDuration = validDuration;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxUsers() {
        return maxUsers;
    }

    public long getMaxStorageMb() {
        return maxStorageMb;
    }

    public boolean isAllowRegister() {
        return allowRegister;
    }

    public Duration getValidDuration() {
        return validDuration;
    }

    /**
     * 根据 code 解析套餐
     */
    public static TenantPlan fromCode(String code) {
        for (TenantPlan plan : values()) {
            if (plan.code.equalsIgnoreCase(code)) {
                return plan;
            }
        }
        throw new IllegalArgumentException("未知租户套餐类型: " + code);
    }
}

