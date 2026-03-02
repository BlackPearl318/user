package com.example.forum.enums.mediafile;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum RefType {

    POST("post"),
    COMMENT("comment");

    @EnumValue         // MyBatis-Plus 持久化时使用这个值
    @JsonValue         // 返回给前端时使用这个值
    private final String code;

    RefType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
