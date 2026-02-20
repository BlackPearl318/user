package com.user.exceptionhandling;

import com.example.common.util.limit.exception.RateLimitException;
import com.example.common.util.result.BaseResponse;
import com.example.common.util.result.BusinessException;
import com.example.common.util.result.ErrorCode;
import com.example.common.util.result.ResultUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

//异常处理
@ControllerAdvice
public class CatchExceptions {

    //指定统一捕获的异常类型
    @ExceptionHandler(BusinessException.class)
    @ResponseBody
    public BaseResponse<?> doBusinessException(BusinessException e){
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(RateLimitException.class)
    @ResponseBody
    public BaseResponse<?> doRateLimitException(RateLimitException e){
        return ResultUtils.error(ErrorCode.OPERATION_ERROR, e.getMessage());
    }
}
