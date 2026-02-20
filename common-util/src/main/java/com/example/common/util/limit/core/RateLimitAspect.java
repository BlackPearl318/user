package com.example.common.util.limit.core;

import com.example.common.util.limit.type.RateLimitType;
import com.example.common.util.limit.api.RateLimit;
import com.example.common.util.limit.exception.RateLimitException;
import com.example.common.util.limit.util.DeviceFingerprintUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

// 限流AOP
@Aspect
@Component
public class RateLimitAspect {

    private final RateLimitService rateLimitService;

    private final Map<RateLimitType, Function<ProceedingJoinPoint, String>> keyGenerators
            = new EnumMap<>(RateLimitType.class);

    private final ExpressionParser parser = new SpelExpressionParser();

    public RateLimitAspect(RateLimitService rateLimitService) {
        this.rateLimitService = rateLimitService;

        // 设备级限流
        keyGenerators.put(RateLimitType.DEVICE, jp -> {
            HttpServletRequest request = getRequest();
            return request != null
                    ? "device:" + DeviceFingerprintUtil.fingerprint(request)
                    : "device:unknown";
        });

        // IP 级限流
        keyGenerators.put(RateLimitType.IP, jp -> {
            HttpServletRequest request = getRequest();
            return request != null
                    ? "ip:" + DeviceFingerprintUtil.getClientIp(request)
                    : "ip:unknown";
        });

        // 用户级限流：强制要求使用 SpEL
        keyGenerators.put(RateLimitType.USERNAME, jp -> {
            throw new RateLimitException(
                    "RateLimitType.USERNAME 需要指定 keySpEL"
            );
        });
    }

    @Around("@annotation(rateLimit)")
    public Object around(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {

        if (!rateLimit.enabled()) {
            return joinPoint.proceed();
        }

        HttpServletRequest request = getRequest();
        if (request == null) {
            return joinPoint.proceed();
        }

        String key;

        // 1️⃣ 优先使用 SpEL
        if (!rateLimit.keySpEL().isBlank()) {
            key = parseSpEL(rateLimit.keySpEL(), joinPoint);
        } else {
            key = buildKey(rateLimit.type(), joinPoint);
        }

        // 2️⃣ key 合法性校验
        if (key == null || key.isBlank()) {
            throw new RateLimitException("RateLimit key 速率限制键解析为空值");
        }

        // 3️⃣ 添加业务前缀
        if (!rateLimit.keyPrefix().isBlank()) {
            key = rateLimit.keyPrefix() + ":" + key;
        }

        // 4️⃣ 执行限流
        rateLimitService.check(
                key,
                rateLimit.maxRequests(),
                rateLimit.windowSeconds()
        );

        return joinPoint.proceed();
    }

    private String buildKey(RateLimitType type, ProceedingJoinPoint joinPoint) {
        Function<ProceedingJoinPoint, String> generator = keyGenerators.get(type);
        if (generator == null) {
            throw new RateLimitException("不支持的速率限制类型 RateLimitType: " + type);
        }
        return generator.apply(joinPoint);
    }

    private String parseSpEL(String spel, ProceedingJoinPoint joinPoint) {
        StandardEvaluationContext context = new StandardEvaluationContext();
        Object[] args = joinPoint.getArgs();

        for (int i = 0; i < args.length; i++) {
            context.setVariable("p" + i, args[i]);
        }

        return parser.parseExpression(spel).getValue(context, String.class);
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs != null ? attrs.getRequest() : null;
    }
}
