package com.example.user.dto;

public class TenantPlanRequest {

    private String plan;

    public TenantPlanRequest() {
    }

    public TenantPlanRequest(String plan) {
        this.plan = plan;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }
}
