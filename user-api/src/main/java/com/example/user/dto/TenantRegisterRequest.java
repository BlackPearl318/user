package com.example.user.dto;

import org.springframework.stereotype.Component;

// 租户注册用传输数据
@Component
public class TenantRegisterRequest {

    private String tName;
    private String phone;
    private String password1;
    private String password2;
    private String code;

    public TenantRegisterRequest() {
    }

    public TenantRegisterRequest(String tName, String phone, String password1, String password2, String code) {
        this.tName = tName;
        this.phone = phone;
        this.password1 = password1;
        this.password2 = password2;
        this.code = code;
    }

    public String gettName() {
        return tName;
    }

    public void settName(String tName) {
        this.tName = tName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
