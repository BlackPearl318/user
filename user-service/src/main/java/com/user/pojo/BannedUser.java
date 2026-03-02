package com.user.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("banned_users")
public class BannedUser {

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("ban_reason")
    private String banReason;

    @TableField("ban_start_time")
    private Timestamp banStartTime;

    @TableField("ban_end_time")
    private Timestamp banEndTime;

    @TableField("created_at")
    private Timestamp createdAt;

    @TableField("updated_at")
    private Timestamp updatedAt;
}

