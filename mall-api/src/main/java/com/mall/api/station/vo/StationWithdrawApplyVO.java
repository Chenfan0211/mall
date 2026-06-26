package com.mall.api.station.vo;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class StationWithdrawApplyVO {

    @NotNull(message = "账户ID不能为空")
    private Long accountId;

    @NotNull(message = "提现金额不能为空")
    @DecimalMin(value = "0.00", inclusive = false, message = "提现金额必须大于0")
    private BigDecimal withdrawAmount;

    @NotBlank(message = "收款账户名不能为空")
    @Size(max = 128, message = "收款账户名不能超过128字")
    private String receiveAccountName;

    @NotBlank(message = "收款账号不能为空")
    @Size(max = 128, message = "收款账号不能超过128字")
    private String receiveAccountNo;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(final Long accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getWithdrawAmount() {
        return withdrawAmount;
    }

    public void setWithdrawAmount(final BigDecimal withdrawAmount) {
        this.withdrawAmount = withdrawAmount;
    }

    public String getReceiveAccountName() {
        return receiveAccountName;
    }

    public void setReceiveAccountName(final String receiveAccountName) {
        this.receiveAccountName = receiveAccountName;
    }

    public String getReceiveAccountNo() {
        return receiveAccountNo;
    }

    public void setReceiveAccountNo(final String receiveAccountNo) {
        this.receiveAccountNo = receiveAccountNo;
    }
}
