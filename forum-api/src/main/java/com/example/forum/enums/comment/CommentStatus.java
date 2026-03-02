package com.example.forum.enums.comment;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CommentStatus {

    NORMAL(0),
    DELETED(1);

    @EnumValue
    @JsonValue
    private final Integer code;

    CommentStatus(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
