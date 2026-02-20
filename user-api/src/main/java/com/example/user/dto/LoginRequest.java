package com.example.user.dto;

import com.example.user.enums.UserRoleType;

// 账号密码登录时的数据传输对象
public class LoginRequest {

    private String username;
    private String password;
    private UserRoleType role;

    public LoginRequest(String username, String password, UserRoleType role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public LoginRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRoleType getRole() {
        return role;
    }

    public void setRole(UserRoleType role) {
        this.role = role;
    }
}
