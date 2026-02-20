package com.example.user.dto;

import java.sql.Date;

// 用户修改个人资料的数据传输对象
public class ResetProfileRequest {

    private String name;
    private String gender;
    private Date dateOfBirth;
    private String biography;

    public ResetProfileRequest() {
    }

    public ResetProfileRequest(String name, String gender, Date dateOfBirth, String biography) {
        this.name = name;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.biography = biography;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }
}
