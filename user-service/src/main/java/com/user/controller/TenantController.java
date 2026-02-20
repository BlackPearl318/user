package com.user.controller;

import com.example.common.context.tenant.TenantContext;
import com.example.common.util.result.BaseResponse;
import com.example.common.util.result.ResultUtils;
import com.example.user.dto.TenantInfoDTO;
import com.example.user.dto.TenantPlanRequest;
import com.example.user.dto.TenantRegisterRequest;
import com.example.user.dto.TenantDTO;
import com.example.user.enums.TenantPlan;
import com.user.pojo.Tenant;
import com.user.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ten")
public class TenantController {

    private final TenantService tenantService;

    @Autowired
    public TenantController(TenantService tenantService) {
        this.tenantService = tenantService;
    }

    // 获取全部的租户映射信息
    @GetMapping("/getTenants")
    public BaseResponse<List<TenantDTO>> getTenants(){
        return ResultUtils.success(tenantService.getTenants());
    }

    // 获取租户信息
    @GetMapping("/getTenantInfo")
    public BaseResponse<?> getTenantInfo(){
        String tenantId = TenantContext.getTenantId();
        TenantInfoDTO tenantInfo = tenantService.getTenantInfo(Long.valueOf(tenantId));
        return ResultUtils.success(tenantInfo);
    }

    // 租户手机号注册
    @PostMapping("/register")
    public BaseResponse<?> register(@RequestBody TenantRegisterRequest request){
        // 获取参数
        String tName = request.gettName();
        String phone = request.getPhone();
        String password1 = request.getPassword1();
        String password2 = request.getPassword2();
        String code = request.getCode();

        String path = tenantService.register(tName, phone, password1, password2, code);

        return ResultUtils.success(path);
    }


    // 租户更改套餐
    @PutMapping("/tenantPlan")
    public BaseResponse<?> tenantPlan(@RequestBody TenantPlanRequest request){
        String tenantId = TenantContext.getTenantId();
        tenantService.setTenantPlan(Long.valueOf(tenantId), TenantPlan.fromCode(request.getPlan()));
        return ResultUtils.success("更改套餐成功");
    }


}
