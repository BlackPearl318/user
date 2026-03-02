package com.example.forum.enums.post;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum PostStatus {

    NORMAL(0),
    PENDING_REVIEW(1),
    DELETED(2),
    BANNED(3);

    @EnumValue
    @JsonValue
    private final Integer code;

    PostStatus(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
