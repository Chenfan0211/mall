package com.mall.api.station.vo;

import com.mall.common.page.PageQuery;
import java.time.LocalDate;

public class StationOrderQueryVO extends PageQuery {

    private String keyword;
    private Long stationId;
    private Long leaderId;
    private Long tradeStatus;
    private Long payStatus;
    private Long fulfillStatus;
    private LocalDate expectedPickupDate;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(final String keyword) {
        this.keyword = keyword;
    }

    public Long getStationId() {
        return stationId;
    }

    public void setStationId(final Long stationId) {
        this.stationId = stationId;
    }

    public Long getLeaderId() {
        return leaderId;
    }

    public void setLeaderId(final Long leaderId) {
        this.leaderId = leaderId;
    }

    public Long getTradeStatus() {
        return tradeStatus;
    }

    public void setTradeStatus(final Long tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    public Long getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(final Long payStatus) {
        this.payStatus = payStatus;
    }

    public Long getFulfillStatus() {
        return fulfillStatus;
    }

    public void setFulfillStatus(final Long fulfillStatus) {
        this.fulfillStatus = fulfillStatus;
    }

    public LocalDate getExpectedPickupDate() {
        return expectedPickupDate;
    }

    public void setExpectedPickupDate(final LocalDate expectedPickupDate) {
        this.expectedPickupDate = expectedPickupDate;
    }
}
