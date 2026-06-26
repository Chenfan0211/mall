package com.mall.common.result;

public enum CommonErrorCode implements ErrorCode {

    SUCCESS("0", "成功"),
    PARAM_ERROR("400", "参数错误"),
    UNAUTHORIZED("401", "未登录或登录已失效"),
    FORBIDDEN("403", "无权限访问"),
    NOT_FOUND("404", "数据不存在"),
    CONFLICT("409", "数据状态冲突"),
    SYSTEM_ERROR("500", "系统异常");

    private final String code;
    private final String message;

    CommonErrorCode(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
