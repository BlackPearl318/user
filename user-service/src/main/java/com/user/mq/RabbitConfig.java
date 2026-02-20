package com.user.mq;

import com.example.user.mq.tenant.MQConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;

public class RabbitConfig {

    // 租户注册
    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(MQConstants.USER_EXCHANGE);
    }
    @Bean
    public Queue tenantCreated() {
        return new Queue(MQConstants.TENANT_CREATED_QUEUE);
    }
    @Bean
    public Binding binding(Queue tenantCreated, TopicExchange exchange) {
        return BindingBuilder.bind(tenantCreated).to(exchange).with(MQConstants.TENANT_CREATED_KEY);
    }



}
