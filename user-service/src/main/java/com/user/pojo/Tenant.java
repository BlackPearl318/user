package com.user.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.user.enums.normal.YesNoStatus;
import com.example.user.enums.tenant.TenantStatus;
import lombok.Data;

import java.sql.Timestamp;

@Data
@TableName("tenant")
public class Tenant {

    /** 租户ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 租户名称（论坛站点名） */
    @TableField("name")
    private String name;

    /** 租户唯一标识（用于路由 / 注册 / 子域名） */
    @TableField("code")
    private String code;

    /** 租户状态：0-禁用 1-正常 2-过期 */
    @TableField("status")
    private TenantStatus status;

    /** 套餐类型：FREE / PRO / ENTERPRISE */
    @TableField("plan_type")
    private String planType;

    /** 到期时间 */
    @TableField("expire_time")
    private Timestamp expireTime;

    /** 最大用户数 */
    @TableField("max_users")
    private Integer maxUsers;

    /** 最大存储空间(MB) */
    @TableField("max_storage_mb")
    private Long maxStorageMb;

    /** 是否允许注册 */
    @TableField("allow_register")
    private YesNoStatus allowRegister;

    /** 创建时间 */
    @TableField("created_at")
    private Timestamp createdAt;

    /** 更新时间 */
    @TableField("updated_at")
    private Timestamp updatedAt;
}

