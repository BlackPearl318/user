package com.example.common.util.limit.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;


import javax.servlet.http.HttpServletRequest;

// 设备指纹工具类
@Component
public class DeviceFingerprintUtil {

    public static String fingerprint(HttpServletRequest request) {
        String ip = getClientIp(request);
        String ua = request.getHeader("User-Agent");
        return DigestUtils.sha256Hex(ip + "|" + ua);
    }

    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isBlank()) {
            return ip.split(",")[0];
        }
        return request.getRemoteAddr();
    }
}

