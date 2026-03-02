package com.user.controller;

import com.example.common.context.tenant.TenantContext;
import com.example.common.context.user.UserContext;
import com.example.common.util.result.BaseResponse;
import com.example.common.util.result.ResultUtils;
import com.example.user.dto.*;
import com.example.user.dto.request.ResetPasswordRequest;
import com.example.user.dto.request.ResetPhoneRequest;
import com.example.user.dto.request.UserRegisterRequest;
import com.user.service.RegistrationService;
import com.user.service.UserQueryService;
import com.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserQueryService userQueryService;
    private final RegistrationService registrationService;

    @Autowired
    public UserController(UserService userService,
                          UserQueryService userQueryService,
                          RegistrationService registrationService) {
        this.userService = userService;
        this.userQueryService = userQueryService;
        this.registrationService = registrationService;
    }

    // 普通用户手机号注册
    @PostMapping("/register")
    public BaseResponse<?> register(@Validated @RequestBody UserRegisterRequest request){
        // 获取参数
        String phone = request.getPhone();
        String password1 = request.getPassword1();
        String password2 = request.getPassword2();
        String code = request.getCode();

        // 获取租户id
        String tenantId = TenantContext.getTenantId();

        registrationService.registerUser(Long.valueOf(tenantId), phone, password1, password2, code);

        return ResultUtils.success("注册成功");
    }

    // 找回密码 只有tenantId没有userId
    @PutMapping("/resetPass")
    public BaseResponse<?> resetPass(@Validated @RequestBody ResetPasswordRequest rpRequest) {

        // 获取租户id
        String tenantId = TenantContext.getTenantId();

        // 修改密码
        userService.resetPass(
                Long.valueOf(tenantId),
                rpRequest.getPhone(),
                rpRequest.getCode(),
                rpRequest.getPassword1(),
                rpRequest.getPassword2());

        return ResultUtils.success("找回成功");
    }

    // 修改绑定手机
    @PutMapping("/resetPhone")
    public BaseResponse<?> resetPhone(@Validated @RequestBody ResetPhoneRequest cpRequest){

        // 手机号
        String phone = cpRequest.getPhone();
        // 输入的验证码
        String code = cpRequest.getCode();

        // 获取用户id
        String userId = UserContext.getUserId();
        // 获取租户id
        String tenantId = TenantContext.getTenantId();

        // 绑定
        userService.bindPhone(Long.valueOf(userId), Long.valueOf(tenantId), phone, code);

        return ResultUtils.success("修改绑定手机成功");
    }

    // 获取某租户下的所有用户
    @GetMapping("/getUsers")
    public BaseResponse<?> getUsers(){
        String userId = UserContext.getUserId();
        String tenantId = TenantContext.getTenantId();
        List<UserDTO> users = userQueryService.getUsers(Long.valueOf(tenantId), Long.valueOf(userId));
        return ResultUtils.success(users);
    }



    // 测试
    @PostMapping("/test")
    public BaseResponse<?> test(){

        // 获取用户id
        String userId = UserContext.getUserId();

        String tenantId = TenantContext.getTenantId();

        return ResultUtils.success("用户id" + userId + "租户id" + tenantId);
    }

}
