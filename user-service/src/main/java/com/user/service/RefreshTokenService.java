package com.user.service;

import com.example.common.util.result.BusinessException;
import com.example.common.util.result.ErrorCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;

    @Value("${refresh.token.ttl-days:30}")
    private long ttlDays;
    @Autowired
    public RefreshTokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /** 创建 Refresh Token */
    public String create(String userId) {
        String tokenId = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(
                key(tokenId),
                userId,
                ttlDays,
                TimeUnit.DAYS
        );
        return tokenId;
    }

    /** 校验 Refresh Token，返回 userId */
    public String verify(String tokenId) {
        return redisTemplate.opsForValue().get(key(tokenId));
    }

    /** 轮换 Refresh Token */
    public String rotate(String oldTokenId) {
        String userId = verify(oldTokenId);
        if (userId == null) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "Refresh Token 无效");
        }
        redisTemplate.delete(key(oldTokenId));
        return create(userId);
    }

    /** 主动失效（退出登录） */
    public void revoke(String tokenId) {
        redisTemplate.delete(key(tokenId));
    }

    private String key(String id) {
        return "refresh:" + id;
    }
}


