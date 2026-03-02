package com.user.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.user.enums.user.UserRoleType;
import lombok.Data;

@Data
@TableName("user_roles")
public class UserRole {

    @TableId(value = "id" , type = IdType.AUTO)
    private Long id;

    private UserRoleType roleCode;
    private String description;
}

