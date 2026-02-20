package com.example.common.util.limit.api;

import com.example.common.util.limit.type.RateLimitType;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 限流维度（IP/DEVICE/USERNAME 等）
     */
    RateLimitType type() default RateLimitType.DEVICE;

    /**
     * 时间窗口（秒）
     */
    int windowSeconds() default 60;

    /**
     * 最大请求次数
     */
    int maxRequests() default 10;

    /**
     * 是否启用（便于灰度 / 开关）
     */
    boolean enabled() default true;

    /**
     * 自定义 key 前缀，方便不同接口使用不同限流
     */
    String keyPrefix() default "";

    /**
     * SpEL 表达式生成 key，例如 "#p0.username" 表示方法第一个参数的 username
     */
    String keySpEL() default "";
}
