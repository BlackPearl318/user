package com.example.forum.enums.postreport;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ReportStatus {

    PENDING(0),
    PROCESSED(1);

    @EnumValue
    @JsonValue
    private final Integer code;

    ReportStatus(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
