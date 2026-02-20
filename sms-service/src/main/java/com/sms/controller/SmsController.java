package com.sms.controller;

import com.example.common.util.limit.api.RateLimit;
import com.example.common.util.limit.type.RateLimitType;
import com.example.common.util.result.BaseResponse;
import com.example.common.util.result.ErrorCode;
import com.example.common.util.result.ResultUtils;

import com.example.sms.dto.SmsRequest;
import com.sms.exception.SmsBizException;
import com.sms.service.SmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sms")
public class SmsController {

    private final SmsService smsService;

    @Autowired
    public SmsController(SmsService smsService) {
        this.smsService = smsService;
    }

    @PostMapping("/send")
    @RateLimit(type = RateLimitType.DEVICE, maxRequests = 5, windowSeconds = 60 * 60 * 24)
    public BaseResponse<?> sendSms(@RequestBody SmsRequest request) {
        try {
            smsService.sendSmsCode(request.getPhone());
            return ResultUtils.success("验证码发送成功");
        } catch (SmsBizException e) {
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR);
        }
    }

    @PostMapping("/verify")
    @RateLimit(type = RateLimitType.DEVICE, maxRequests = 5, windowSeconds = 60 * 60 * 24)
    public BaseResponse<?> verifySms(@RequestBody SmsRequest request) {
        try {
            boolean result = smsService.verifySmsCode(request.getPhone(), request.getCode());
            if (result) {
                return ResultUtils.success("验证码验证成功");
            } else {
                return ResultUtils.error(ErrorCode.OPERATION_ERROR,"验证失败");
            }
        } catch (SmsBizException e) {
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR,"验证失败");
        }
    }

}
