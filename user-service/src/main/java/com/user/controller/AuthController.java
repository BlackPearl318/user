package com.user.controller;

import com.example.common.context.tenant.TenantContext;
import com.example.common.util.limit.api.RateLimit;
import com.example.common.util.limit.type.RateLimitType;

import com.example.common.util.result.BaseResponse;
import com.example.common.util.result.ErrorCode;
import com.example.common.util.result.ResultUtils;

import com.example.user.dto.LoginByPhoneRequest;
import com.example.user.dto.LoginRequest;
import com.example.user.enums.UserRoleType;
import com.user.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${basePath.auth}")
    private String path; // 基础路径
    @Value("${refresh.token.name}")
    private String refreshToken; // refreshToken的name

    private final AuthService authService;
    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthController(AuthService authService,
                          UserService userService,
                          JwtService jwtService,
                          RefreshTokenService refreshTokenService) {
        this.authService = authService;
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    /**
     * 手机号登录
     * 所有角色登录时都需要指定tenantId
     * @param loginRequest 数据请求DTO
     * @param response 响应体
     * @return BaseResponse
     */
    @PostMapping("/login")
    @RateLimit(type = RateLimitType.DEVICE, maxRequests = 5, windowSeconds = 60 * 60 * 24)
    public BaseResponse<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();
        UserRoleType role = loginRequest.getRole();

        // 获取租户id
        String tenantId = TenantContext.getTenantId();

        authService.login(Long.valueOf(tenantId), username, password, role);

        Long userId = userService.getUserId(Long.valueOf(tenantId), username);

        String accessToken = jwtService.generate(userId.toString(), Map.of());
        String refreshTokenId = refreshTokenService.create(userId.toString());

        ResponseCookie cookie = ResponseCookie.from(refreshToken, refreshTokenId)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path(path)
                .maxAge(Duration.ofDays(30))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResultUtils.success(accessToken);
    }


    /**
     * 手机号登录
     * 所有角色登录时都需要指定tenantId
     * 登录时需要指定tenantId以实现数据隔离
     * @param lbpRequest 数据请求DTO
     * @param response 响应体
     * @return BaseResponse
     */
    @PostMapping("/loginByPhone")
    @RateLimit(type = RateLimitType.DEVICE, maxRequests = 5, windowSeconds = 60 * 60 * 24)
    public BaseResponse<?> loginByPhone(@RequestBody LoginByPhoneRequest lbpRequest, HttpServletResponse response) {
        // 获取参数
        String phone = lbpRequest.getPhone();
        String code = lbpRequest.getCode();
        UserRoleType role = lbpRequest.getRole();

        // 获取租户id
        String tenantId = TenantContext.getTenantId();

        // 获取用户id
        Long userId = userService.getUserId(Long.valueOf(tenantId), phone);

        // 用户的登录状态
        authService.loginByPhone(Long.valueOf(tenantId), phone, code, role);

        // 生成短token和refreshToken
        String accessToken = jwtService.generate(userId.toString(), Map.of());
        String refreshTokenId = refreshTokenService.create(userId.toString());

        // 返回refreshToken
        ResponseCookie cookie = ResponseCookie.from(refreshToken, refreshTokenId)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path(path)
                .maxAge(Duration.ofDays(30))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResultUtils.success(accessToken);
    }

    // 刷新令牌
    @PostMapping("/refresh")
    @RateLimit(type = RateLimitType.DEVICE, maxRequests = 500, windowSeconds = 60 * 60 * 24)
    public BaseResponse<?> refresh(@CookieValue(value = "${refresh.token.name}", required = false) String refreshTokenId,
                          HttpServletResponse response) {

        if (refreshTokenId == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "无refreshToken");
        }

        String userId = refreshTokenService.verify(refreshTokenId);
        if (userId == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "refreshToken错误");
        }

        String newRefreshTokenId = refreshTokenService.rotate(refreshTokenId);
        String newAccessToken = jwtService.generate(userId, Map.of());

        ResponseCookie cookie = ResponseCookie.from(refreshToken, newRefreshTokenId)
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path(path)
                .maxAge(Duration.ofDays(30))
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResultUtils.success(newAccessToken);
    }


    //退出登录
    @PostMapping("/logout")
    public BaseResponse<?> logout(
            @CookieValue(value = "${refresh.token.name}", required = false) String refreshTokenId,
            HttpServletResponse response) {

        if (refreshTokenId != null) {
            authService.logout(refreshTokenId);
        }

        // 清除浏览器 refresh_token
        ResponseCookie clearCookie = ResponseCookie.from(refreshToken, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Lax")
                .path(path)
                .maxAge(0)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, clearCookie.toString());

        return ResultUtils.success("退出登录成功");
    }


}
