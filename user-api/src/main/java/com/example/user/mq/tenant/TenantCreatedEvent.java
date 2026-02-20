package com.example.user.mq.tenant;

public class TenantCreatedEvent {
    private Long tenantId;
    private String tenantCode;
    private String tenantName;

    public TenantCreatedEvent(Long tenantId, String tenantCode, String tenantName) {
        this.tenantId = tenantId;
        this.tenantCode = tenantCode;
        this.tenantName = tenantName;
    }

    public TenantCreatedEvent() {
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public String getTenantCode() {
        return tenantCode;
    }

    public void setTenantCode(String tenantCode) {
        this.tenantCode = tenantCode;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
}

