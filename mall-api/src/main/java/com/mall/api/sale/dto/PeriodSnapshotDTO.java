package com.mall.api.sale.dto;

import java.time.LocalDateTime;

public class PeriodSnapshotDTO {

    private Long id;
    private Long periodId;
    private String periodNo;
    private Long snapshotNode;
    private String snapshotJson;
    private LocalDateTime createTime;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

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

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(final LocalDateTime createTime) {
        this.createTime = createTime;
    }
}
