package com.example.sms.dto;

public class SmsRequest {

    private String phone;      // 手机号
    private String code;       // 验证码

    public SmsRequest() {
    }

    public SmsRequest(String phone, String code) {
        this.phone = phone;
        this.code = code;
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
