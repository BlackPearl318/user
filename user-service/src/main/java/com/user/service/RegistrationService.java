package com.user.service;

import com.example.common.util.result.BaseResponse;
import com.example.common.util.result.BusinessException;
import com.example.common.util.result.ErrorCode;
import com.example.sms.dto.SmsRequest;
import com.example.sms.feign.SmsClient;
import com.example.user.dto.TenantInfoDTO;
import com.example.user.enums.tenant.TenantStatus;
import com.example.user.enums.user.UserRoleType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class RegistrationService {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class); // 日志

    private final UserService userService;
    private final TenantService tenantService;
    private final UserQueryService userQueryService;
    private final SmsClient smsClient;

    @Autowired
    public RegistrationService(UserService userService,
                               TenantService tenantService,
                               UserQueryService userQueryService,
                               SmsClient smsClient) {
        this.userService = userService;
        this.tenantService = tenantService;
        this.userQueryService = userQueryService;
        this.smsClient = smsClient;
    }

    /**
     * 场景 A：普通用户注册
     */
    public void registerUser(Long tenantId, String phone, String password1, String password2, String code) {
        // 获取租户信息
        TenantInfoDTO tenantInfo = tenantService.getTenantInfo(tenantId);
        if(!(tenantInfo.getStatus().equals(TenantStatus.ACTIVATE))){
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "资源不可用");
        }

        // 是否超出租户最大用户限额
        if(tenantInfo.getMaxUsers() <= userQueryService.countByTenantId(tenantId)){
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "资源不可用");
        }

        // 检查是否已经注册
        if(userService.existsByPhone(phone, tenantId)){
            throw new BusinessException(ErrorCode.STATUS_CONFLICT, "该手机号已被注册");
        }

        // 密码一致性校验（本地校验，避免无意义远程调用）
        if (!Objects.equals(password1, password2)) {
            throw new BusinessException(ErrorCode.DATA_ACCESS_ERROR, "两次密码输入不一致");
        }

        // 验证短信验证码（事务外，远程调用）
        BaseResponse<?> smsResult;
        try {
            smsResult = smsClient.verifySms(new SmsRequest(phone, code));
        } catch (Exception e) {
            logger.error("调用 sms-service 校验验证码失败，phone={}", phone, e);
            throw new BusinessException(ErrorCode.DEPENDENCY_ERROR);
        }

        if (smsResult.getCode() != 0) {
            throw new BusinessException(ErrorCode.VERIFY_CODE_ERROR);
        }

        // 进入事务，执行真正注册
        doUserRegister(tenantId, phone, password1);
    }

    /**
     * 场景 B：租户注册
     */
    public String registerTenant(String tName, String phone, String password1, String password2, String code) {
        // 检查手机号是否已被占用（全局校验）
        // 租户注册不允许手机号在全系统内重复
        if (userService.existsByPhoneGlobal(phone)) {
            throw new BusinessException(ErrorCode.STATUS_CONFLICT, "该手机号已注册，请直接登录或更换手机号");
        }

        // 密码一致性校验
        if (!Objects.equals(password1, password2)) {
            throw new BusinessException(ErrorCode.DATA_ACCESS_ERROR, "两次密码输入不一致");
        }

        // 验证短信验证码（远程调用）
        BaseResponse<?> smsResult;
        try {
            smsResult = smsClient.verifySms(new SmsRequest(phone, code));
        } catch (Exception e) {
            logger.error("调用 sms-service 校验验证码失败，phone={}", phone, e);
            throw new BusinessException(ErrorCode.DEPENDENCY_ERROR);
        }

        if (smsResult.getCode() != 0) {
            throw new BusinessException(ErrorCode.VERIFY_CODE_ERROR);
        }

        // 进入事务，执行真正注册
        return doTenantRegister(tName, phone, password1);
    }

    public void doUserRegister(Long tenantId, String phone, String plainPassword){
        userService.doRegister(tenantId, phone, plainPassword, UserRoleType.USER);
    }

    public String doTenantRegister(String tName, String phone, String password) {
        return tenantService.doRegister(tName, phone, password);
    }

}
