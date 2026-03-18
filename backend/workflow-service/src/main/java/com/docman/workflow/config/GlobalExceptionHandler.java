package com.docman.workflow.config;

import com.docman.workflow.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * 处理业务异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e) {
        return Result.error(e.getMessage());
    }
    
    /**
     * 处理参数校验异常
     */
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public Result<?> handleValidationException(jakarta.validation.ConstraintViolationException e) {
        return Result.error(400, e.getMessage());
    }
    
    /**
     * 处理其他异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        return Result.error("系统错误: " + e.getMessage());
    }
}