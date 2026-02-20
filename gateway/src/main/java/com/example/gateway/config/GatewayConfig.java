package com.example.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.header.StaticServerHttpHeadersWriter;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter.Mode;

import java.time.Duration;

@Configuration
@EnableWebFluxSecurity
public class GatewayConfig {

    // 安全防护
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        // 创建自定义安全头集合
        HttpHeaders securityHeaders = new HttpHeaders();
//        securityHeaders.add("Content-Security-Policy", "default-src 'self'; script-src 'self'");
        securityHeaders.add("Referrer-Policy", "strict-origin-when-cross-origin");
        securityHeaders.add("Permissions-Policy", "geolocation=(), camera=()");
        securityHeaders.add("Cache-Control", "no-store, max-age=0");
        securityHeaders.add("Content-Security-Policy",
                "default-src 'none'; " +
                        "script-src 'self' 'strict-dynamic'; " +
                        "style-src 'self' 'unsafe-inline'; " +
                        "img-src 'self' data:; " +
                        "connect-src 'self'; " +
                        "frame-ancestors 'none'; " +
                        "form-action 'self';"
        );

        return http
                .authorizeExchange(exchanges -> exchanges
                        .anyExchange().permitAll()
                )
                .headers(headers -> headers
                        // 基础安全头
                        .contentTypeOptions(options -> {})
                        .xssProtection(xss -> {}) // 启用 XSS 防护
                        .hsts(hsts -> hsts
                                .includeSubdomains(true)
                                .maxAge(Duration.ofDays(365))
                        )
                        .frameOptions(frame -> frame
                                .mode(Mode.DENY)
                        )
                        // 添加自定义安全头
                        .cache(ServerHttpSecurity.HeaderSpec.CacheSpec::disable) // 禁用默认缓存头
                        .writer(new StaticServerHttpHeadersWriter(securityHeaders))
                )
                .csrf().disable()
                .build();
    }

}
