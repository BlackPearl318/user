package com.example.sms.feign;

import com.example.common.util.result.BaseResponse;
import com.example.sms.dto.SmsRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "sms-service")
public interface SmsClient {

    //发送验证码
    @PostMapping("/sms/send")
    BaseResponse<?> sendSms(@RequestBody SmsRequest smsRequest);

    // 验证验证码
    @PostMapping("/sms/verify")
    BaseResponse<?> verifySms(@RequestBody SmsRequest smsRequest);

}
