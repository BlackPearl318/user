package com.example.user.enums.normal;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum YesNoStatus {

    NO(0),
    YES(1);

    @EnumValue
    @JsonValue
    private final Integer code;

    YesNoStatus(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
