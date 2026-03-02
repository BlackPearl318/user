package com.user.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.user.enums.notification.NotificationType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("user_notifications")
public class UserNotification {

    @TableId(value = "id" , type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;
    @TableField("message")
    private String message;
    @TableField("is_read")
    private boolean isRead;

    @TableField("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Timestamp createdAt;

    @TableField("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Timestamp updatedAt;

    @TableField("type")
    private NotificationType type;
}

