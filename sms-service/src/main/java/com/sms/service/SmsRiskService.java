package com.sms.service;

import com.sms.exception.SmsBizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class SmsRiskService {

    private final StringRedisTemplate redisTemplate;

    @Autowired
    public SmsRiskService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 检查手机号是否超限并记录发送次数
     */
    public void checkAndRecord(String phone) {
        String lockKey = "sms:lock:" + phone;
        String countKey = "sms:count:" + phone;

        // 1. 是否已被封禁
        if (Boolean.TRUE.equals(redisTemplate.hasKey(lockKey))) {
            throw new SmsBizException("验证码发送过于频繁，请稍后再试");
        }

        // 2. 发送次数 +1（原子操作）
        Long count = redisTemplate.opsForValue().increment(countKey);

        // 3. 首次发送，设置统计周期
        if (count != null && count == 1) {
            redisTemplate.expire(countKey, 24, TimeUnit.HOURS);
        }

        // 4. 超限封禁
        if (count != null && count > 5) {
            redisTemplate.opsForValue().set(lockKey, "locked", 24, TimeUnit.HOURS);
            throw new SmsBizException("验证码发送次数已达上限，手机号已被暂时封禁");
        }
    }
}
