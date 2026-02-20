package com.user.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.sql.Timestamp;

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
    private Integer status;

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
    private Boolean allowRegister;

    /** 创建时间 */
    @TableField("created_at")
    private Timestamp createdAt;

    /** 更新时间 */
    @TableField("updated_at")
    private Timestamp updatedAt;

    public Tenant() {
    }

    public Tenant(Long id, String name, String code, Integer status, String planType, Timestamp expireTime, Integer maxUsers, Long maxStorageMb, Boolean allowRegister, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.status = status;
        this.planType = planType;
        this.expireTime = expireTime;
        this.maxUsers = maxUsers;
        this.maxStorageMb = maxStorageMb;
        this.allowRegister = allowRegister;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public Timestamp getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Timestamp expireTime) {
        this.expireTime = expireTime;
    }

    public Integer getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(Integer maxUsers) {
        this.maxUsers = maxUsers;
    }

    public Long getMaxStorageMb() {
        return maxStorageMb;
    }

    public void setMaxStorageMb(Long maxStorageMb) {
        this.maxStorageMb = maxStorageMb;
    }

    public Boolean getAllowRegister() {
        return allowRegister;
    }

    public void setAllowRegister(Boolean allowRegister) {
        this.allowRegister = allowRegister;
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

