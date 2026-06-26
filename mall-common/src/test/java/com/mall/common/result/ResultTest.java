package com.mall.common.result;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class ResultTest {

    @Test
    void success_hasSuccessCodeAndData() {
        final Result<String> result = Result.success("ok");

        assertEquals("0", result.getCode());
        assertEquals("成功", result.getMessage());
        assertEquals("ok", result.getData());
    }

    @Test
    void fail_hasErrorCodeAndNoData() {
        final Result<String> result = Result.fail(CommonErrorCode.PARAM_ERROR);

        assertEquals("400", result.getCode());
        assertEquals("参数错误", result.getMessage());
        assertNull(result.getData());
    }
}
