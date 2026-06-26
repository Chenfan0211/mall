package com.mall.sale.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("sale_period_snapshot")
public class SalePeriodSnapshot extends BaseEntity {

    private Long periodId;
    private String periodNo;
    private Long snapshotNode;
    private String snapshotJson;

    public Long getPeriodId() {
        return periodId;
    }

    public void setPeriodId(final Long periodId) {
        this.periodId = periodId;
    }

    public String getPeriodNo() {
        return periodNo;
    }

    public void setPeriodNo(final String periodNo) {
        this.periodNo = periodNo;
    }

    public Long getSnapshotNode() {
        return snapshotNode;
    }

    public void setSnapshotNode(final Long snapshotNode) {
        this.snapshotNode = snapshotNode;
    }

    public String getSnapshotJson() {
        return snapshotJson;
    }

    public void setSnapshotJson(final String snapshotJson) {
        this.snapshotJson = snapshotJson;
    }
}
