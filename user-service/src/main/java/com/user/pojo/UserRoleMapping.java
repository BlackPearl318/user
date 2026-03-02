package com.user.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_role_mapping")
public class UserRoleMapping {

    private Long userId;

    private Long roleId;
}

