package com.example.user.dto;

import com.example.user.enums.UserRoleType;
import org.springframework.stereotype.Component;

// 手机号登录时的数据传输对象
@Component
public class LoginByPhoneRequest {

    private String phone;
    private String code;
    private UserRoleType role;

    public LoginByPhoneRequest() {
    }

    public LoginByPhoneRequest(String phone, String code, UserRoleType role) {
        this.phone = phone;
        this.code = code;
        this.role = role;
    }

    public LoginByPhoneRequest(String phone) {
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public UserRoleType getRole() {
        return role;
    }

    public void setRole(UserRoleType role) {
        this.role = role;
    }
}
