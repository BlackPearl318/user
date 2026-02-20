package com.example.common.context;

import com.example.common.context.filter.ContextFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ContextAutoConfiguration {

    @Bean
    public ContextFilter contextFilter() {
        return new ContextFilter(); // 由 Bean 方法创建，Spring 会处理注入
    }

    @Bean
    public FilterRegistrationBean<ContextFilter> userContextFilter() {
        FilterRegistrationBean<ContextFilter> bean = new FilterRegistrationBean<>();
        bean.setFilter(contextFilter());
        bean.setOrder(-100); // 执行优先级
        return bean;
    }
}

