package com.example.user.enums.user;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Gender {
    MALE("男"),
    FEMALE("女"),
    UNKNOWN("未知");

    @EnumValue
    @JsonValue
    private final String name;

    Gender(String name){
        this.name = name;
    }
}
