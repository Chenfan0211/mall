package com.mall.common.exception;

import com.mall.common.result.CommonErrorCode;
import com.mall.common.result.ErrorCode;

public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(final String message) {
        super(message);
        this.errorCode = CommonErrorCode.SYSTEM_ERROR;
    }

    public BusinessException(final ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public BusinessException(final ErrorCode errorCode, final String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
