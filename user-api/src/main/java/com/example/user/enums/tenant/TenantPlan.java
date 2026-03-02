package com.example.user.enums.tenant;

import com.example.common.util.result.BusinessException;
import com.example.common.util.result.ErrorCode;
import com.example.user.enums.normal.YesNoStatus;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum TenantPlan {

    /**
     * 免费版
     */
    FREE(
            "FREE",
            "免费版",
            50,                 // 最大用户数
            1024L,              // 最大存储空间 MB (1GB)
            YesNoStatus.YES,               // 是否允许注册
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
            YesNoStatus.YES,
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
            YesNoStatus.YES,
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
    private final YesNoStatus allowRegister;

    /**
     * 套餐有效期
     */
    private final Duration validDuration;


    private static final Map<String, TenantPlan> CACHE =
            Arrays.stream(values())
                    .collect(Collectors.toMap(
                            p -> p.code.toUpperCase(),
                            p -> p
                    ));

    TenantPlan(
            String code,
            String description,
            int maxUsers,
            long maxStorageMb,
            YesNoStatus allowRegister,
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

    public YesNoStatus isAllowRegister() {
        return allowRegister;
    }

    public Duration getValidDuration() {
        return validDuration;
    }

    /**
     * 根据 code 解析套餐
     */
    public static TenantPlan fromCode(String code) {
        if (code == null) {
            throw new BusinessException(ErrorCode.PARAMS_INVALID, "套餐不能为空");
        }

        TenantPlan plan = CACHE.get(code.toUpperCase());
        if (plan == null) {
            throw new BusinessException(ErrorCode.PARAMS_INVALID, "未知套餐类型");
        }
        return plan;
    }
}

