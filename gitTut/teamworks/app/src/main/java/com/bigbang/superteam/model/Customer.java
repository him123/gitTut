package com.bigbang.superteam.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER 8 on 7/24/2015.
 */
public class Customer implements Serializable {

    @Expose
    private Integer ID;
    @Expose
    private String Name;
    @Expose
    private String MobileNo;
    @Expose
    private String LandlineNo;
    @Expose
    private String EmailID;
    @Expose
    private Integer OwnerID;
    @Expose
    private Integer CompanyTypeID;
    @Expose
    private Integer CreatedBy;
    @Expose
    private Integer CompanyID;
    @Expose
    private String Type;
    @Expose
    private String Description;
    @Expose
    private ArrayList<Address> AddressList = new ArrayList<Address>();
    @SerializedName("Key_Customer_Vendor")
    @Expose
    private Boolean KeyCustomerVendor;
    @Expose
    private Boolean Transactional;
    @Expose
    private Boolean isActive;
    @Expose
    private String Logo;
    private  boolean selected;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * @return The ID
     */
    public Integer getID() {
        return ID;
    }

    /**
     * @param ID The ID
     */
    public void setID(Integer ID) {
        this.ID = ID;
    }

    /**
     * @return The Name
     */
    public String getName() {
        return Name;
    }

    /**
     * @param Name The Name
     */
    public void setName(String Name) {
        this.Name = Name;
    }

    /**
     * @return The MobileNo
     */
    public String getMobileNo() {
        return MobileNo;
    }

    /**
     * @param MobileNo The MobileNo
     */
    public void setMobileNo(String MobileNo) {
        this.MobileNo = MobileNo;
    }

    /**
     * @return The LandlineNo
     */
    public String getLandlineNo() {
        return LandlineNo;
    }

    /**
     * @param LandlineNo The LandlineNo
     */
    public void setLandlineNo(String LandlineNo) {
        this.LandlineNo = LandlineNo;
    }

    /**
     * @return The EmailID
     */
    public String getEmailID() {
        return EmailID;
    }

    /**
     * @param EmailID The EmailID
     */
    public void setEmailID(String EmailID) {
        this.EmailID = EmailID;
    }

    /**
     * @return The OwnerID
     */
    public Integer getOwnerID() {
        return OwnerID;
    }

    /**
     * @param OwnerID The OwnerID
     */
    public void setOwnerID(Integer OwnerID) {
        this.OwnerID = OwnerID;
    }

    /**
     * @return The CompanyTypeID
     */
    public Integer getCompanyTypeID() {
        return CompanyTypeID;
    }

    /**
     * @param CompanyTypeID The CompanyTypeID
     */
    public void setCompanyTypeID(Integer CompanyTypeID) {
        this.CompanyTypeID = CompanyTypeID;
    }

    /**
     * @return The CreatedBy
     */
    public Integer getCreatedBy() {
        return CreatedBy;
    }

    /**
     * @param CreatedBy The CreatedBy
     */
    public void setCreatedBy(Integer CreatedBy) {
        this.CreatedBy = CreatedBy;
    }

    /**
     * @return The CompanyID
     */
    public Integer getCompanyID() {
        return CompanyID;
    }

    /**
     * @param CompanyID The CompanyID
     */
    public void setCompanyID(Integer CompanyID) {
        this.CompanyID = CompanyID;
    }

    /**
     * @return The Type
     */
    public String getType() {
        return Type;
    }

    /**
     * @param Type The Type
     */
    public void setType(String Type) {
        this.Type = Type;
    }

    /**
     * @return The Description
     */
    public String getDescription() {
        return Description;
    }

    /**
     * @param Description The Description
     */
    public void setDescription(String Description) {
        this.Description = Description;
    }

    /**
     * @return The AddressList
     */
    public ArrayList<Address> getAddressList() {
        return AddressList;
    }

    /**
     * @param AddressList The AddressList
     */
    public void setAddressList(ArrayList<Address> AddressList) {
        this.AddressList = AddressList;
    }

    public void setAddressList(String json) {
        try {
            Gson gson = new Gson();
            java.lang.reflect.Type listOfTestObject = new TypeToken<List<Address>>() {
            }.getType();
            this.AddressList = gson.fromJson(json, listOfTestObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return The KeyCustomerVendor
     */
    public Boolean getKeyCustomerVendor() {
        return KeyCustomerVendor;
    }

    /**
     * @param KeyCustomerVendor The Key_Customer_Vendor
     */
    public void setKeyCustomerVendor(Boolean KeyCustomerVendor) {
        this.KeyCustomerVendor = KeyCustomerVendor;
    }

    /**
     * @return The Transactional
     */
    public Boolean getTransactional() {
        return Transactional;
    }

    /**
     * @param Transactional The Transactional
     */
    public void setTransactional(Boolean Transactional) {
        this.Transactional = Transactional;
    }

    /**
     * @return The isActive
     */
    public Boolean getIsActive() {
        return isActive;
    }

    /**
     * @param isActive The isActive
     */
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    /**
     * @return The Logo
     */
    public String getLogo() {
        return Logo;
    }

    /**
     * @param Logo The Logo
     */
    public void setLogo(String Logo) {
        this.Logo = Logo;
    }


}
