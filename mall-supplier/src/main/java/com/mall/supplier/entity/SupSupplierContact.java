package com.mall.supplier.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("sup_supplier_contact")
public class SupSupplierContact extends BaseEntity {

    private Long supplierId;
    private String contactName;
    private String contactMobile;
    private Long contactType;
    private Long defaultFlag;
    private Long status;

    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(final Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(final String contactName) {
        this.contactName = contactName;
    }

    public String getContactMobile() {
        return contactMobile;
    }

    public void setContactMobile(final String contactMobile) {
        this.contactMobile = contactMobile;
    }

    public Long getContactType() {
        return contactType;
    }

    public void setContactType(final Long contactType) {
        this.contactType = contactType;
    }

    public Long getDefaultFlag() {
        return defaultFlag;
    }

    public void setDefaultFlag(final Long defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }
}
