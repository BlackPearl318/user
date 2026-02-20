package com.example.gateway.cache;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 租户注册表
// 启动预热 + 事件同步
@Component
public class TenantRegistry {

    private final Map<String, Long> tenantMap = new ConcurrentHashMap<>();

    public void register(String tenantCode, Long tenantId) {
        tenantMap.put(tenantCode, tenantId);
    }

    public Long getTenantId(String tenantCode) {
        return tenantMap.get(tenantCode);
    }
}

