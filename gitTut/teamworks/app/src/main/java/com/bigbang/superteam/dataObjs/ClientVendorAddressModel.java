package com.bigbang.superteam.dataObjs;

/**
 * Created by USER 7 on 8/22/2015.
 */
public class ClientVendorAddressModel {

    String addresId;
    int clientVendorId;
    String addressData;
    String clientName;
    String locationType;
    String userId;
    String CompanyID;

    public String getCompanyID() {
        return CompanyID;
    }

    public void setCompanyID(String companyID) {
        CompanyID = companyID;
    }

    public String getUserId() {

        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLocationType() {
        return locationType;
    }

    public void setLocationType(String locationType) {
        this.locationType = locationType;
    }
    public String getAddresId() {
        return addresId;
    }

    public void setAddresId(String addresId) {
        this.addresId = addresId;
    }

    public int getClientVendorId() {
        return clientVendorId;
    }

    public void setClientVendorId(int clientVendorId) {
        this.clientVendorId = clientVendorId;
    }

    public String getAddressData() {
        return addressData;
    }

    public void setAddressData(String addressData) {
        this.addressData = addressData;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
}
