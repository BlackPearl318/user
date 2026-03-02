package com.example.user.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TenantPlanRequest {

    @NotBlank(message = "套餐类型不能为空")
    private String plan;
}
