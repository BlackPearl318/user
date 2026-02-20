package com.example.user.dto;

import org.springframework.stereotype.Component;

// 封禁用户的数据传输对象
@Component
public class BanUserRequest {

    private String banReason;
    private Integer days;

    public BanUserRequest() {
    }

    public BanUserRequest(String banReason, Integer days) {
        this.banReason = banReason;
        this.days = days;
    }

    public String getBanReason() {
        return banReason;
    }

    public void setBanReason(String banReason) {
        this.banReason = banReason;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }
}
