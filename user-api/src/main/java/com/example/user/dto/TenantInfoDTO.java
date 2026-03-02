package com.example.user.dto;

import com.example.user.enums.normal.YesNoStatus;
import com.example.user.enums.tenant.TenantStatus;
import lombok.Data;

import java.sql.Timestamp;

@Data
public class TenantInfoDTO {
    private String name;
    private String code;
    private TenantStatus status;
    private String planType;
    private Timestamp expireTime;
    private Integer maxUsers;
    private Long maxStorageMb;
    private YesNoStatus allowRegister;
}
