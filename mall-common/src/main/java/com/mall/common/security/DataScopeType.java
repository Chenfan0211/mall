package com.mall.common.security;

public enum DataScopeType {

    ALL_PLATFORM(1L, "全平台"),
    CITY(2L, "城市"),
    SUPPLIER(3L, "供应商"),
    STATION(4L, "自提点"),
    WAREHOUSE(5L, "仓库"),
    DRIVER(6L, "司机"),
    LEADER(7L, "团长");

    private final Long code;
    private final String desc;

    DataScopeType(final Long code, final String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Long getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
