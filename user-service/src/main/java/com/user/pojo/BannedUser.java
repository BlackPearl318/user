package com.user.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.sql.Timestamp;

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

    public BannedUser() {
    }

    public BannedUser(Long userId, String banReason, Timestamp banStartTime, Timestamp banEndTime) {
        this.userId = userId;
        this.banReason = banReason;
        this.banStartTime = banStartTime;
        this.banEndTime = banEndTime;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBanReason() {
        return banReason;
    }

    public void setBanReason(String banReason) {
        this.banReason = banReason;
    }

    public Timestamp getBanStartTime() {
        return banStartTime;
    }

    public void setBanStartTime(Timestamp banStartTime) {
        this.banStartTime = banStartTime;
    }

    public Timestamp getBanEndTime() {
        return banEndTime;
    }

    public void setBanEndTime(Timestamp banEndTime) {
        this.banEndTime = banEndTime;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}

