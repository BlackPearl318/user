package com.example.gateway.filter;

import com.example.gateway.util.HmacUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

// 全局过滤器
@Component
public class GatewaySignatureFilter implements GlobalFilter, Ordered {

    @Value("${gatewaySignature.secret}")
    private String SECRET;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        ServerHttpRequest request = exchange.getRequest();

        // ★ 原始外部路径（RewritePath 之前）
        String rawPath = request.getURI().getRawPath();

        String timestamp = String.valueOf(System.currentTimeMillis());
        String nonce = UUID.randomUUID().toString().replace("-", "");

        // ★ 不包含 body
        String signText = String.join("\n",
                request.getMethodValue(),
                rawPath,
                timestamp,
                nonce
        );

        String signature = HmacUtil.sign(signText, SECRET);

        ServerHttpRequest newRequest = request.mutate()
                .header("X-Gateway-Timestamp", timestamp)
                .header("X-Gateway-Nonce", nonce)
                .header("X-Gateway-Signature", signature)
                // 关键：把原始路径透传给微服务
                .header("X-Gateway-Path", rawPath)
                .build();

        return chain.filter(exchange.mutate().request(newRequest).build());
    }

    @Override
    public int getOrder() {
        return 200;
    }
}
