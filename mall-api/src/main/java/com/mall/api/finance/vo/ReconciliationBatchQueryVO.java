package com.mall.api.finance.vo;

import com.mall.common.page.PageQuery;
import java.time.LocalDate;

public class ReconciliationBatchQueryVO extends PageQuery {

    private String keyword;
    private Long channelType;
    private LocalDate reconcileDate;
    private Long status;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(final String keyword) {
        this.keyword = keyword;
    }

    public Long getChannelType() {
        return channelType;
    }

    public void setChannelType(final Long channelType) {
        this.channelType = channelType;
    }

    public LocalDate getReconcileDate() {
        return reconcileDate;
    }

    public void setReconcileDate(final LocalDate reconcileDate) {
        this.reconcileDate = reconcileDate;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }
}
