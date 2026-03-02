package com.example.user.dto;

import com.example.user.enums.user.UserStatus;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private UserStatus status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp frozenUntil;
}
