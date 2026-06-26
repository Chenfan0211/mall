package com.mall.api.system.vo;

import jakarta.validation.constraints.NotBlank;

public class DictItemQueryVO {

    @NotBlank(message = "字典编码不能为空")
    private String dictCode;

    private Long status;

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(final String dictCode) {
        this.dictCode = dictCode;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }
}
