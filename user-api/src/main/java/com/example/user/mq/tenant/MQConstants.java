package com.example.user.mq.tenant;

// RabbitMQ中的交换机和路由键
public interface MQConstants {
    // 交换机
    String USER_EXCHANGE = "user.exchange";

    // 路由键 (Routing Keys)
    String TENANT_CREATED_KEY = "tenant.created.routingKey";

    // 队列
    String TENANT_CREATED_QUEUE = "tenant.created.queue";
}
