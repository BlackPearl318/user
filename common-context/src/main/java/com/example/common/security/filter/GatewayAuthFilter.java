package com.example.common.security.filter;

import com.example.common.security.util.HmacUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class GatewayAuthFilter extends OncePerRequestFilter {

    @Value("${gatewaySignature.secret}")
    private String SECRET;

    @Value("${gatewaySignature.allowedTimeDrift}")
    private long ALLOWED_TIME_DRIFT;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String timestamp = request.getHeader("X-Gateway-Timestamp");
        String nonce = request.getHeader("X-Gateway-Nonce");
        String signature = request.getHeader("X-Gateway-Signature");
        String gatewayPath = request.getHeader("X-Gateway-Path");

        if (timestamp == null || nonce == null || signature == null || gatewayPath == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "非法来源请求");
            return;
        }

        long ts;
        try {
            ts = Long.parseLong(timestamp);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "时间戳非法");
            return;
        }

        if (Math.abs(System.currentTimeMillis() - ts) > ALLOWED_TIME_DRIFT) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "请求过期");
            return;
        }

        String signText = String.join("\n",
                request.getMethod(),
                gatewayPath,
                timestamp,
                nonce
        );

        String expected = HmacUtil.sign(signText, SECRET);

        if (!expected.equals(signature)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "签名不合法");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
