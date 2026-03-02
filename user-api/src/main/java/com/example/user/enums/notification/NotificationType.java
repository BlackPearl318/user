package com.example.user.enums.notification;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 用户通知类型枚举
 */
public enum NotificationType {

    /**
     * 系统通知
     */
    SYSTEM(1, "系统通知", "系统发送的重要通知"),

    /**
     * 私信/消息
     */
    MESSAGE(2, "私信", "用户之间的私信消息"),

    /**
     * 评论通知
     */
    COMMENT(3, "评论", "用户评论了你的内容"),

    /**
     * 回复通知
     */
    REPLY(4, "回复", "用户回复了你的评论"),

    /**
     * 点赞/喜欢
     */
    LIKE(5, "点赞", "用户喜欢了你的内容"),

    /**
     * 关注通知
     */
    FOLLOW(6, "关注", "用户关注了你"),

    /**
     * 提及通知
     */
    MENTION(7, "提及", "用户在内容中提到了你"),

    /**
     * 提醒通知
     */
    REMINDER(8, "提醒", "系统提醒"),

    /**
     * 警告通知
     */
    WARNING(9, "警告", "违反规则警告"),

    /**
     * 活动通知
     */
    ACTIVITY(10, "活动", "活动相关通知"),

    /**
     * 订单通知
     */
    ORDER(11, "订单", "订单状态变更通知"),

    /**
     * 安全通知
     */
    SECURITY(12, "安全", "账户安全相关通知"),

    /**
     * 其他通知
     */
    OTHER(0, "其他", "其他类型通知"),

    /**
     * 未知类型（用于容错）
     */
    UNKNOWN(-1, "未知", "未知的通知类型");

    @EnumValue
    @JsonValue
    private final Integer code;

    private final String name;

    private final String description;

    NotificationType(Integer code, String name, String description) {
        this.code = code;
        this.name = name;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
