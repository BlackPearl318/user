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
    public String create(Long userId, Long tenantId) {
        String tokenId = UUID.randomUUID().toString();
        // 存储格式改为 userId:tenantId，例如 "12345:1"
        String value = userId + ":" + tenantId;

        redisTemplate.opsForValue().set(
                key(tokenId),
                value,
                ttlDays,
                TimeUnit.DAYS
        );
        return tokenId;
    }

    /** 校验 Refresh Token，返回 userId */
    public String[] verifyAndGetInfo(String tokenId) {
        String value = redisTemplate.opsForValue().get(key(tokenId));
        if (value == null) {
            return null;
        }
        return value.split(":"); // 返回 [userId, tenantId]
    }

    /** 轮换 Refresh Token */
    public String rotate(String oldTokenId) {
        String[] info = verifyAndGetInfo(oldTokenId);
        if (info == null) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "Refresh Token 无效");
        }
        redisTemplate.delete(key(oldTokenId));
        // 使用原有的信息重新创建
        return create(Long.valueOf(info[0]), Long.valueOf(info[1]));
    }

    /** 主动失效（退出登录） */
    public void revoke(String tokenId) {
        redisTemplate.delete(key(tokenId));
    }

    private String key(String id) {
        return "refresh:" + id;
    }
}


