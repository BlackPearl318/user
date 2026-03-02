package com.user.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.user.enums.user.UserStatus;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("users")
public class User {

    @TableId(value = "id",type = IdType.AUTO)
    private Long id;

    @TableField("username")
    private String username;

    @TableField("password")
    private String password;

    @TableField("email")
    private String email;

    @TableField("phone")
    private String phone;

    @TableField("status")
    private UserStatus status;

    @TableField("created_at")
    private Timestamp createdAt;

    @TableField("updated_at")
    private Timestamp updatedAt;

    @TableField("frozen_until")
    private Timestamp frozenUntil;

    @TableField("tenant_id")
    private Long tenantId;
}

