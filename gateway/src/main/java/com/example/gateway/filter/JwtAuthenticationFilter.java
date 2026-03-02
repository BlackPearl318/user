package com.example.gateway.filter;

import com.example.gateway.util.JwtVerifier;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(2) // 必须在 Tenant Filter之后
public class JwtAuthenticationFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtVerifier jwtVerifier;
    @Autowired
    public JwtAuthenticationFilter(JwtVerifier jwtVerifier) {
        this.jwtVerifier = jwtVerifier;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        // 1. 放行 OPTIONS 预检
        if (exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            return chain.filter(exchange);
        }

        // 2. 放行白名单（登录、注册等）
        String path = exchange.getRequest().getURI().getPath();
        if (path.startsWith("/api/auth/")) {
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        // 3. 非白名单接口，如果没有 Token，直接拒绝！
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("拒绝无令牌访问私有接口: {}", path);
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete(); // 终止请求，不发往后端
        }

        try {
            Claims claims = jwtVerifier.verify(authHeader.substring(7));
            String userId = claims.getSubject();
            // 1. 提取 JWT 里的租户 ID
            Object tidClaim = claims.get("tid");
            String tenantIdFromJwt = tidClaim != null ? tidClaim.toString() : null;

            ServerHttpRequest.Builder builder = exchange.getRequest().mutate();

            // 2. 注入/覆盖 User-Id
            builder.header("X-User-Id", userId);

            // 3. 安全核心：如果 JWT 包含租户 ID，强制覆盖 Header
            // 哪怕之前的 TenantFilter 解析了子域名，这里也以 JWT 签名为准
            if (tenantIdFromJwt != null) {
                builder.headers(h -> h.remove("X-Tenant-Id")); // 清除域名解析的结果
                builder.header("X-Tenant-Id", tenantIdFromJwt);
            }

            return chain.filter(exchange.mutate().request(builder.build()).build());

        } catch (JwtException e) {
            log.warn("JWT 校验失败: {}", e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }
}



