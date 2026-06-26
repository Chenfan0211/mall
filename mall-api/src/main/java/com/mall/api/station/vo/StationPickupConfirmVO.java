package com.mall.api.station.vo;

import jakarta.validation.constraints.Min;

public class StationPickupConfirmVO {

    @Min(value = 1, message = "核销数量必须大于0")
    private Long pickedQty;

    private Long operatorId;

    private String remark;

    public Long getPickedQty() {
        return pickedQty;
    }

    public void setPickedQty(final Long pickedQty) {
        this.pickedQty = pickedQty;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(final Long operatorId) {
        this.operatorId = operatorId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(final String remark) {
        this.remark = remark;
    }
}
