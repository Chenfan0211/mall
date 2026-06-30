package com.mall.common.web;

import com.mall.common.exception.BusinessException;
import com.mall.common.result.CommonErrorCode;
import com.mall.common.result.Result;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(final BusinessException exception) {
        return Result.fail(exception.getErrorCode(), exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
        final String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ":" + error.getDefaultMessage())
                .collect(Collectors.joining(";"));
        return Result.fail(CommonErrorCode.PARAM_ERROR, message);
    }

    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(final BindException exception) {
        final String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ":" + error.getDefaultMessage())
                .collect(Collectors.joining(";"));
        return Result.fail(CommonErrorCode.PARAM_ERROR, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(final ConstraintViolationException exception) {
        return Result.fail(CommonErrorCode.PARAM_ERROR, exception.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleHttpMessageNotReadableException(final HttpMessageNotReadableException exception) {
        return Result.fail(CommonErrorCode.PARAM_ERROR, "请求参数格式错误");
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthenticationException.class)
    public Result<Void> handleAuthenticationException(final AuthenticationException exception) {
        return Result.fail(CommonErrorCode.UNAUTHORIZED);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public Result<Void> handleAccessDeniedException(final AccessDeniedException exception) {
        return Result.fail(CommonErrorCode.FORBIDDEN, "无权限访问");
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(final Exception exception) {
        log.error("[通用异常处理] - 系统异常: errorMsg={}", exception.getMessage(), exception);
        return Result.fail(CommonErrorCode.SYSTEM_ERROR);
    }
}
