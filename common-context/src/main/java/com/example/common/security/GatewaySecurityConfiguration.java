package com.example.common.security;

import com.example.common.security.filter.GatewayAuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewaySecurityConfiguration {

    // 此时 GatewayAuthFilter 类上不需要 @Component
    @Bean
    public GatewayAuthFilter gatewayAuthFilter() {
        return new GatewayAuthFilter(); // 在这里由 Bean 方法创建，Spring 会处理注入
    }

    @Bean
    public FilterRegistrationBean<GatewayAuthFilter> gatewayContextFilter() {
        FilterRegistrationBean<GatewayAuthFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(gatewayAuthFilter()); // 调用上面的 Bean 方法
        bean.setOrder(-110);
        return bean;
    }
}

