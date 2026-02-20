package com.example.gateway.bootstrap;

import com.example.common.util.result.BaseResponse;
import com.example.gateway.cache.TenantRegistry;

import com.example.user.dto.TenantDTO;
import com.example.user.feign.TenantClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TenantBootstrap implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(TenantBootstrap.class);

    private final TenantClient tenantClient;
    private final TenantRegistry tenantRegistry;

    @Autowired
    public TenantBootstrap(TenantClient tenantClient, TenantRegistry tenantRegistry) {
        this.tenantClient = tenantClient;
        this.tenantRegistry = tenantRegistry;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Gateway启动预热：开始加载租户映射信息...");

        try {
            // 调用 Feign 接口
            BaseResponse<List<TenantDTO>> tenants =  tenantClient.getTenants();

            // 校验响应状态
            if (tenants == null) {
                log.error("租户信息加载失败: 响应为空或状态异常");
                return;
            }

            List<TenantDTO> tenantList = tenants.getData();

            // 判空处理
            if (tenantList == null || tenantList.isEmpty()) {
                log.warn("User服务未返回任何租户数据");
                return;
            }

            // 注册到缓存
            tenantList.forEach(t -> tenantRegistry.register(t.getCode(), t.getId()));

            log.info("Gateway启动预热完成：共缓存 {} 个租户", tenantList.size());

        } catch (Exception e) {
            // 异常兜底：千万不要因为 User 服务挂了，导致 Gateway 启动崩溃
            // 这里选择只是打印错误，允许 Gateway 继续启动
            log.error("Gateway启动预热异常：无法连接 User-Service 获取租户信息", e);
        }
    }
}

