package com.example.common.context.tenant;

public final class TenantContext {

    private static final ThreadLocal<String> TENANT_ID = new ThreadLocal<>();

    public static void setTenantId(String tenantId) {
        TENANT_ID.set(tenantId);
    }

    public static String getTenantId() {
        return TENANT_ID.get();
    }

    public static String requireTenantId() {
        String tenantId = TENANT_ID.get();
        if (tenantId == null) {
            throw new IllegalStateException("租户信息缺失");
        }
        return tenantId;
    }

    public static void clear() {
        TENANT_ID.remove();
    }

    private TenantContext() {}
}

