package com.example.forum.enums.category;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CategoryStatus {

    NORMAL(0),
    DISABLED(1);

    @EnumValue
    @JsonValue
    private final Integer code;

    CategoryStatus(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
