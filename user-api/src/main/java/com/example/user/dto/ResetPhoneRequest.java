package com.example.user.dto;

import org.springframework.stereotype.Component;

@Component
public class ResetPhoneRequest {

    private String phone;
    private String code;

    public ResetPhoneRequest() {
    }

    public ResetPhoneRequest(String newPhone) {
        this.phone = newPhone;
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
}
