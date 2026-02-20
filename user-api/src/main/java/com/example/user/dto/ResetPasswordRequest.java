package com.example.user.dto;

import org.springframework.stereotype.Component;

@Component
public class ResetPasswordRequest {

    private String phone;
    private String code;
    private String password1;
    private String password2;

    public ResetPasswordRequest() {
    }

    public ResetPasswordRequest(String phone, String code, String password1, String password2) {
        this.phone = phone;
        this.code = code;
        this.password1 = password1;
        this.password2 = password2;
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
}
