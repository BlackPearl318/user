package com.example.common.util.limit.core;

import com.example.common.util.limit.exception.RateLimitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

// 限流服务（核心）
@Component
public class RateLimitService {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public RateLimitService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void check(String key, int max, int windowSeconds) {
        String redisKey = "rate:limit:" + key;

        Long count = redisTemplate.opsForValue().increment(redisKey);

        if (count == 1) {
            redisTemplate.expire(redisKey, windowSeconds, TimeUnit.SECONDS);
        }

        if (count > max) {
            throw new RateLimitException("请求过于频繁，请稍后再试");
        }
    }

    public void reset(String key) {
        redisTemplate.delete("rate:limit:" + key);
    }
}


