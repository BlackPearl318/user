package com.example.gateway.filter;

import com.example.gateway.cache.TenantRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(1) // 在 JWT Filter 之前
public class TenantFilter implements WebFilter {

    private static final Logger log = LoggerFactory.getLogger(TenantFilter.class);

    private final TenantRegistry tenantRegistry;

    @Autowired
    public TenantFilter(TenantRegistry tenantRegistry) {
        this.tenantRegistry = tenantRegistry;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        String host = exchange.getRequest().getHeaders().getFirst(HttpHeaders.HOST);
        String tenantCode = resolveTenantCode(host);

        // 本地测试使用
        if (isLocalHost(host)) {
            tenantCode = "local";
        }

        if (tenantCode == null) {
            log.warn("无法解析租户子域名, host={}", host);
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
            return exchange.getResponse().setComplete();
        }

        Long tenantId = tenantRegistry.getTenantId(tenantCode);
        if (tenantId == null) {
            tenantId = -1L; // 默认不存在的拒户id
        }

        // 注入 Header
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .headers(h -> h.remove("X-Tenant-Id"))
                .header("X-Tenant-Id", tenantId.toString())
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    /** 域名格式 : tenantA.xxx.com -> tenantA */
    private String resolveTenantCode(String host) {
        if (host == null) return null;
        String[] parts = host.split("\\.");
        return parts.length >= 3 ? parts[0] : null;
    }


    /**
     * 本地测试使用
     * @param host 判断是否为回环地址
     * @return 返回
     */
    private boolean isLocalHost(String host) {
        if (host == null) return false;
        return host.contains("localhost")
                || host.startsWith("127.0.0.1");
    }

}

