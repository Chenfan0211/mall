package com.mall.api.user.dto;

public class UserStationDTO {

    private Long id;
    private String stationNo;
    private String stationName;
    private Long cityId;
    private String address;
    private String contactName;
    private String contactMobile;
    private String businessHours;
    private Long status;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getStationNo() {
        return stationNo;
    }

    public void setStationNo(final String stationNo) {
        this.stationNo = stationNo;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(final String stationName) {
        this.stationName = stationName;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(final Long cityId) {
        this.cityId = cityId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(final String address) {
        this.address = address;
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

    public String getBusinessHours() {
        return businessHours;
    }

    public void setBusinessHours(final String businessHours) {
        this.businessHours = businessHours;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(final Long status) {
        this.status = status;
    }
}
