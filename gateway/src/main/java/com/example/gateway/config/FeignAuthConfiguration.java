package com.example.gateway.config;

import com.example.gateway.util.HmacUtil;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.UUID;

// feign配置
@Configuration
public class FeignAuthConfiguration {

    @Value("${gatewaySignature.secret}")
    private String SECRET;

    /**
     * gateway的feign配置
     * 确保通过 Feign 发出的每一个请求，都带上与 GatewaySignatureFilter 逻辑完全一致的签名头
     * 解决微服务内部安全校验机制
     */
    @Bean
    public RequestInterceptor feignSignatureInterceptor() {
        return template -> {
            // 获取请求信息
            String method = template.method();
            String path = template.path();

            String timestamp = String.valueOf(System.currentTimeMillis());
            String nonce = UUID.randomUUID().toString().replace("-", "");

            // 2. 构造签名文本
            String signText = String.join("\n",
                    method,
                    path,
                    timestamp,
                    nonce
            );

            // 生成签名
            String signature = HmacUtil.sign(signText, SECRET);

            // 填充 Header
            template.header("X-Gateway-Timestamp", timestamp);
            template.header("X-Gateway-Nonce", nonce);
            template.header("X-Gateway-Signature", signature);
            template.header("X-Gateway-Path", path);
        };
    }
}
