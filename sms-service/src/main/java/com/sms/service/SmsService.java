package com.sms.service;

import com.sms.exception.SmsBizException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsService.class);

    private final SmsRiskService smsRiskService;
    private final StringRedisTemplate redisTemplate;

    private static final int CODE_EXPIRE_MINUTES = 5; // 验证码有效期
    private static final int MAX_FAIL_COUNT = 5;      // 最大错误次数
    private static final int FAIL_EXPIRE = 60 * 60;   // 失败次数统计有效期 1 小时

    @Autowired
    public SmsService(SmsRiskService smsRiskService, StringRedisTemplate redisTemplate) {
        this.smsRiskService = smsRiskService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 发送验证码
     */
    public void sendSmsCode(String phone) {

        // 1. 检查风控锁（长期封禁）
        String lockKey = "sms:lock:" + phone;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(lockKey))) {
            throw new SmsBizException("手机号已被暂时封禁，请稍后再试");
        }

        // 2. 风控检查并记录发送次数
        smsRiskService.checkAndRecord(phone);

        // 3. 生成验证码
        String code = generateCode();

        // 4. 保存到 Redis
        String codeKey = "sms:code:" + phone;
        redisTemplate.opsForValue().set(codeKey, code, CODE_EXPIRE_MINUTES, TimeUnit.MINUTES);

        // 5. 调用第三方发送接口（这里用日志代替）
        log.info("向 {} 发送验证码 [{}]", phone, code);
    }

    /**
     * 验证短信验证码（累计失败次数模型）
     */
    public boolean verifySmsCode(String phone, String code) {

        String lockKey = "sms:lock:" + phone;
        String codeKey = "sms:code:" + phone;
        String failKey = "sms:fail:" + phone;

        // 1. 是否已被封禁
        if (Boolean.TRUE.equals(redisTemplate.hasKey(lockKey))) {
            throw new SmsBizException("手机号已被封禁，请稍后再试");
        }

        // 2. 获取验证码
        String cachedCode = redisTemplate.opsForValue().get(codeKey);
        if (cachedCode == null) {
            throw new SmsBizException("验证码不存在或已过期");
        }

        // 3. 验证失败
        if (!cachedCode.equals(code)) {
            Long failCount = redisTemplate.opsForValue().increment(failKey, 1);

            // 第一次失败时设置统计周期
            if (failCount != null && failCount == 1) {
                redisTemplate.expire(failKey, FAIL_EXPIRE, TimeUnit.SECONDS);
            }

            // 超过最大失败次数 → 封禁手机号
            if (failCount != null && failCount >= MAX_FAIL_COUNT) {
                redisTemplate.opsForValue().set(lockKey, "1", 1, TimeUnit.HOURS);
                redisTemplate.delete(failKey);
                throw new SmsBizException("验证码错误次数过多，手机号已被封禁");
            }

            throw new SmsBizException("验证码错误");
        }

        // 4. 验证成功，清理数据
        redisTemplate.delete(codeKey);
        redisTemplate.delete(failKey);

        return true;
    }


    private String generateCode() {
        int code = 100000 + ThreadLocalRandom.current().nextInt(900000);
        return String.valueOf(code);
    }
}
