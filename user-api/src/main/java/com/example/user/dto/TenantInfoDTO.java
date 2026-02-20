package com.example.user.dto;

import java.sql.Timestamp;

public class TenantInfoDTO {

    private String name;
    private String code;
    private Integer status;
    private String planType;
    private Timestamp expireTime;
    private Integer maxUsers;
    private Long maxStorageMb;
    private Boolean allowRegister;

    public TenantInfoDTO() {
    }

    public TenantInfoDTO(String name, String code, Integer status, String planType, Timestamp expireTime, Integer maxUsers, Long maxStorageMb, Boolean allowRegister) {
        this.name = name;
        this.code = code;
        this.status = status;
        this.planType = planType;
        this.expireTime = expireTime;
        this.maxUsers = maxUsers;
        this.maxStorageMb = maxStorageMb;
        this.allowRegister = allowRegister;
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
}
