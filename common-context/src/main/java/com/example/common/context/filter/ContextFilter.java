package com.example.common.context.filter;

import com.example.common.context.tenant.TenantContext;
import com.example.common.context.tenant.TenantHeader;
import com.example.common.context.user.UserContext;
import com.example.common.context.user.UserHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String userId = request.getHeader(UserHeaders.USER_ID);

        String tenantId = request.getHeader(TenantHeader.TENANT_ID);

        if (userId != null && !userId.isBlank()) {
            UserContext.setUserId(userId);
        }

        if (tenantId != null && !tenantId.isBlank()) {
            TenantContext.setTenantId(tenantId);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            UserContext.clear();
            TenantContext.clear();
        }
    }
}

