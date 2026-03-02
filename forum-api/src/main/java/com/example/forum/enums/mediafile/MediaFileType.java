package com.example.forum.enums.mediafile;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MediaFileType {

    IMAGE("image"),
    VIDEO("video");

    @EnumValue
    @JsonValue
    private final String code;

    MediaFileType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
