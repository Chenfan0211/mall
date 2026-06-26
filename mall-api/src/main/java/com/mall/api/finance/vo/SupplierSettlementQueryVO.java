package com.mall.api.finance.vo;

import com.mall.common.page.PageQuery;
import java.time.LocalDate;

public class SupplierSettlementQueryVO extends PageQuery {

    private String keyword;
    private Long supplierId;
    private Long status;
    private LocalDate periodStartDate;
    private LocalDate periodEndDate;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(final String keyword) {
        this.keyword = keyword;
    }

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(final Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }

    public LocalDate getPeriodStartDate() {
        return periodStartDate;
    }

    public void setPeriodStartDate(final LocalDate periodStartDate) {
        this.periodStartDate = periodStartDate;
    }

    public LocalDate getPeriodEndDate() {
        return periodEndDate;
    }

    public void setPeriodEndDate(final LocalDate periodEndDate) {
        this.periodEndDate = periodEndDate;
    }
}
