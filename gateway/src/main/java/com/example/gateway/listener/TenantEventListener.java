package com.example.gateway.listener;

import com.example.gateway.cache.TenantRegistry;
import com.example.user.mq.tenant.MQConstants;
import com.example.user.mq.tenant.TenantCreatedEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TenantEventListener {

    private final TenantRegistry tenantRegistry;

    @Autowired
    public TenantEventListener(TenantRegistry tenantRegistry) {
        this.tenantRegistry = tenantRegistry;
    }

    // 监听新的租户信息，用MQ事件 增量同步租户注册表
    @RabbitListener(queues = MQConstants.TENANT_CREATED_QUEUE)
    public void onTenantCreated(TenantCreatedEvent event) {
        tenantRegistry.register(event.getTenantCode(), event.getTenantId());
    }
}

