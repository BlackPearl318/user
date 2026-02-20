package com.user.service;

import com.example.common.util.result.BaseResponse;
import com.example.common.util.result.BusinessException;
import com.example.common.util.result.ErrorCode;
import com.example.sms.dto.SmsRequest;
import com.example.sms.feign.SmsClient;
import com.example.user.enums.UserRoleType;
import com.example.user.enums.UserStatus;

import com.user.mapper.*;
import com.user.pojo.*;
import com.user.utils.PasswordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class); // 日志

    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final UserRoleService userRoleService;
    private final SmsClient smsClient;


    @Autowired
    public AuthService(UserService userService,
                       RefreshTokenService refreshTokenService,
                       UserRoleService userRoleService, SmsClient smsClient) {
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
        this.userRoleService = userRoleService;
        this.smsClient = smsClient;
    }



    /**
     * 账号(或手机号)密码登录(用户，租户，管理员)
     * 登录时需要指定tenantId以实现数据隔离
     * @param tenantId 租户id
     * @param username 普通用户的账号或者手机号
     * @param password 密码
     * @param role 权限
     */
    public void login(Long tenantId, String username, String password, UserRoleType role) {
        // 判断输入的数据格式
        if(username == null || username.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_INVALID);
        }
        if(password == null || password.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_INVALID);
        }

        //获取用户id
        Long userId = userService.getUserId(tenantId, username);
        if (userId == null) {
            // 找不到userId证明没有该用户
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        // 判断用户权限
        UserRoleType userRole = userRoleService.getUserRole(userId);
        if(!(userRole.equals(role))){
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED);
        }
        // 判断用户是否已被删除
        if(userService.getUserStatus(userId).equals(UserStatus.DELETED)){
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        // 判断用户是否被封禁
        if(userService.getUserStatus(userId).equals(UserStatus.BANNED)){
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
        }
        // 查询用户是否绑定了手机号
        if (userService.getUserPhone(userId) == null) {
            // 需要绑定手机号
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED);
        }
        // 检查用户是否被冻结
        if (userService.isUserFrozen(userId)) {
            throw new BusinessException(ErrorCode.ACCOUNT_FROZEN);
        }

        // 判断密码是否输入正确
        boolean check = this.checkPassword(userId, password);
        if(check){
            // 检查用户是否处于注销中状态
            if(userService.getUserStatus(userId).equals(UserStatus.DEACTIVATING)){
                // 取消用户的注销中状态
                this.cancelUserDeletion(userId);
            }
        }else{
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
    }
    //验证用户输入的密码是否正确
    private boolean checkPassword(Long userId, String password){
        String hashPassword = userService.getPass(userId);
        return PasswordUtils.check(password, hashPassword);
    }


    //手机号登录
    public void loginByPhone(Long tenantId, String phone, String code, UserRoleType role){
        // 判断输入的数据格式
        if(phone == null || phone.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_INVALID);
        }
        if(code == null || code.isEmpty()){
            throw new BusinessException(ErrorCode.PARAMS_INVALID);
        }

        // 获取用户id
        Long userId = userService.getUserId(tenantId, phone);
        if(userId == null){
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }

        // 判断用户权限
        UserRoleType userRole = userRoleService.getUserRole(userId);
        if(!(userRole.equals(role))){
            throw new BusinessException(ErrorCode.OPERATION_NOT_ALLOWED);
        }
        // 判断用户是否被封禁
        if(userService.getUserStatus(userId).equals(UserStatus.BANNED)){
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
        }
        // 检查用户是否被冻结
        if (userService.isUserFrozen(userId)) {
            throw new BusinessException(ErrorCode.ACCOUNT_FROZEN);
        }

        // 检查验证码是否输入正确
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

        // 检查用户是否处于注销中状态
        if(userService.getUserStatus(userId).equals(UserStatus.DEACTIVATING)){
            // 取消用户的注销中状态
            this.cancelUserDeletion(userId);
        }
    }


    //退出登录
    public void logout(String tokenId) {

        if (tokenId == null || tokenId.isBlank()) {
            return;
        }

        // 使当前会话的 Refresh Token 失效
        refreshTokenService.revoke(tokenId);
    }



    // 注销账号
    public void deleteAccount(String tokenId){
//        // 检查数据
//        if(tokenId == null || tokenId.isEmpty()){
//            return;
//        }
//        // 获取用户id
//        String userId = refreshTokenService.verify(tokenId);
//        //修改用户状态，进入注销中状态
//        this.initiateUserDeletion(Long.valueOf(userId));
//        // 清除redis里该用户的refreshToken
//        refreshTokenService.revoke(tokenId);
    }


    // 用户进入注销状态
    public void initiateUserDeletion(Long userId) {
        userService.updateUserStatus(userId, UserStatus.DEACTIVATING);
    }

    // 取消用户注销状态
    public void cancelUserDeletion(Long userId) {
        //修改用户状态,需要先评估一下用户的状态
        UserStatus status = userService.confirmSomeUserStatus(userId);
        userService.updateUserStatus(userId, status);
    }
}
