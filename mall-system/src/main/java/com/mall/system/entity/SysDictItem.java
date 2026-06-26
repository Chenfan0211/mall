package com.mall.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.mall.common.domain.BaseEntity;

@TableName("sys_dict_item")
public class SysDictItem extends BaseEntity {

    private String dictCode;
    private String itemCode;
    private String itemName;
    private Long itemValue;
    private Long sortNo;
    private Long status;

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(final String dictCode) {
        this.dictCode = dictCode;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(final String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(final String itemName) {
        this.itemName = itemName;
    }

    public Long getItemValue() {
        return itemValue;
    }

    public void setItemValue(final Long itemValue) {
        this.itemValue = itemValue;
    }

    public Long getSortNo() {
        return sortNo;
    }

    public void setSortNo(final Long sortNo) {
        this.sortNo = sortNo;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }
}
