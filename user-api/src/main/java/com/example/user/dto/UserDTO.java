package com.example.user.dto;

import java.sql.Timestamp;

public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private Integer status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp frozenUntil;

    public UserDTO() {
    }

    public UserDTO(Long id, String username, String email, String phone, Integer status, Timestamp createdAt, Timestamp updatedAt, Timestamp frozenUntil) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.frozenUntil = frozenUntil;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public Timestamp getFrozenUntil() {
        return frozenUntil;
    }

    public void setFrozenUntil(Timestamp frozenUntil) {
        this.frozenUntil = frozenUntil;
    }
}
