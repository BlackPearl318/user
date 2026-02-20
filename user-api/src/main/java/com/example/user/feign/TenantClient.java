package com.example.user.feign;

import com.example.common.util.result.BaseResponse;
import com.example.user.dto.TenantDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "user-service")
public interface TenantClient {

    @GetMapping("/ten/getTenants")
    BaseResponse<List<TenantDTO>> getTenants();
}

