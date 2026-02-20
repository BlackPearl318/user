package com.example.common.util.result;

public enum ErrorCode {

    // ===== 成功 =====
    SUCCESS(0, "ok"),

    // ===== 400 参数 & 校验 =====
    PARAMS_ERROR(40000, "请求参数错误"),
    PARAMS_MISSING(40001, "请求参数缺失"),
    PARAMS_FORMAT_ERROR(40002, "参数格式错误"),
    PARAMS_INVALID(40003, "参数不合法"),
    VERIFY_CODE_ERROR (40005, "验证码匹配失败"),


    // ===== 401 认证 =====
    NOT_LOGIN_ERROR(40100, "未登录"),
    LOGIN_EXPIRED(40101, "登录已过期"),
    TOKEN_INVALID(40102, "登录凭证无效"),
    TOKEN_EXPIRED(40103, "登录凭证已过期"),

    // ===== 403 权限 & 状态 =====
    NO_AUTH_ERROR(40300, "无权限"),
    FORBIDDEN_ERROR(40301, "禁止访问"),
    ACCOUNT_DISABLED(40302, "账号已被禁用"),
    ACCOUNT_FROZEN(40303, "账号已被冻结"),

    // ===== 404 资源 =====
    NOT_FOUND_ERROR(40400, "请求数据不存在"),
    USER_NOT_FOUND(40401, "用户不存在"),
    RESOURCE_NOT_FOUND(40402, "资源不存在"),

    // ===== 409 状态冲突 =====
    STATUS_CONFLICT(40900, "资源状态冲突"),
    OPERATION_NOT_ALLOWED(40901, "当前状态不允许该操作"),

    // ===== 429 限流 =====
    TOO_MANY_REQUESTS(42900, "请求过于频繁，请稍后再试"),

    // ===== 500 系统 =====
    SYSTEM_ERROR(50000, "系统内部异常"),
    OPERATION_ERROR(50001, "操作失败"),
    DEPENDENCY_ERROR(50002, "依赖服务异常"),
    DATA_ACCESS_ERROR(50003, "数据访问异常");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
