package com.mall.api.supplier.vo;

import com.mall.common.page.PageQuery;

public class SupplierContactQueryVO extends PageQuery {

    private Long supplierId;
    private Long contactType;
    private Long status;

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(final Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getContactType() {
        return contactType;
    }

    public void setContactType(final Long contactType) {
        this.contactType = contactType;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }
}
