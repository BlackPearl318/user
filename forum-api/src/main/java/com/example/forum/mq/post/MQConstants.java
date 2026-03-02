package com.example.forum.mq.post;

// RabbitMQ中的交换机和路由键
public interface MQConstants {
    // 交换机
    String POST_EXCHANGE = "post.exchange";

    // 路由键 (Routing Keys)
    String POST_CREATED_KEY = "post.created.routingKey";

    // 队列
    String POST_CREATED_QUEUE = "post.created.queue";
}
