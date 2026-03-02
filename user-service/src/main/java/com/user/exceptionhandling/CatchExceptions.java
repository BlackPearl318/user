package com.user.exceptionhandling;

import com.example.common.util.result.BaseResponse;
import com.example.common.util.result.BusinessException;
import com.example.common.util.result.ErrorCode;
import com.example.common.util.result.ResultUtils;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;


import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

// 统一异常处理
@RestControllerAdvice
public class CatchExceptions {

    /**
     * 业务异常
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> doBusinessException(BusinessException e){
        return ResultUtils.error(e.getCode(), e.getMessage());
    }

    /**
     * @RequestBody 参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<?> handleMethodArgumentNotValid(MethodArgumentNotValidException e){

        String errorMsg = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));

        return ResultUtils.error(ErrorCode.PARAMS_INVALID, errorMsg);
    }

    /**
     * @RequestParam / @PathVariable 参数校验异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public BaseResponse<?> handleConstraintViolation(ConstraintViolationException e){

        String errorMsg = e.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));

        return ResultUtils.error(ErrorCode.PARAMS_INVALID, errorMsg);
    }

}