package com.example.user.dto;

import com.example.user.enums.user.Gender;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserProfileDTO {
    private String name;
    private Gender gender;
    private LocalDate dateOfBirth;
    private String biography;
    private String avatar;
}
