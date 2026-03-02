package com.example.forum.enums.mediafile;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MediaStatus {

    NORMAL(0),
    DELETED(1),
    VIOLATION(2);

    @EnumValue
    @JsonValue
    private final Integer code;

    MediaStatus(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
