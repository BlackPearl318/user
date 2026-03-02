package com.user.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.user.enums.user.Gender;
import lombok.Data;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;

@Data
@TableName("user_profiles")
public class UserProfile {

    @TableId("user_id")
    private Long userId;

    @TableField("name")
    private String name;
    @TableField("gender")
    private Gender gender;
    @TableField("date_of_birth")
    private LocalDate dateOfBirth;
    @TableField("biography")
    private String biography;
    @TableField("avatar")
    private String avatar;

    @TableField("created_at")
    private Timestamp createdAt;
    @TableField("updated_at")
    private Timestamp updatedAt;
}

