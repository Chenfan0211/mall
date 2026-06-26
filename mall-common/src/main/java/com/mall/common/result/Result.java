package com.mall.common.result;

import java.io.Serializable;

public class Result<T> implements Serializable {

    private String code;
    private String message;
    private T data;
    private String traceId;

    public static <T> Result<T> success(final T data) {
        final Result<T> result = new Result<>();
        result.setCode(CommonErrorCode.SUCCESS.getCode());
        result.setMessage(CommonErrorCode.SUCCESS.getMessage());
        result.setData(data);
        return result;
    }

    public static <T> Result<T> fail(final ErrorCode errorCode) {
        final Result<T> result = new Result<>();
        result.setCode(errorCode.getCode());
        result.setMessage(errorCode.getMessage());
        return result;
    }

    public static <T> Result<T> fail(final ErrorCode errorCode, final String message) {
        final Result<T> result = new Result<>();
        result.setCode(errorCode.getCode());
        result.setMessage(message);
        return result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(final T data) {
        this.data = data;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(final String traceId) {
        this.traceId = traceId;
    }
}
