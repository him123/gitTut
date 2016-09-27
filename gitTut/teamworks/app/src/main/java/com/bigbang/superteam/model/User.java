package com.bigbang.superteam.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by USER 8 on 7/23/2015.
 */
public class User implements Serializable {

    @Expose
    private Integer id;
    @Expose
    private Integer UserID;
    @Expose
    private String FirstName;
    @Expose
    private String LastName;
    @Expose
    private String MobileNo1;
    @Expose
    private Boolean EmailVerified;
    @Expose
    private String MobileNo2;
    @Expose
    private String EmailID;
    @Expose
    private Address permanentAddress;
    @Expose
    private Address temporaryAddress;
    @Expose
    private String DeviceID;
    @Expose
    private String otpGenerateTime;
    @Expose
    private String SecurityToken;
    @Expose
    private Manager Manager;
    @Expose
    private Integer InvalidLoginAttempt;
    @Expose
    private Role role;
    @Expose
    private Company company;
    @Expose
    private String picture;
    @Expose
    private Boolean published = false;
    @Expose
    private Integer roleId;

    private boolean checked;

    public boolean isChecked() {
        return checked;
    }
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    private boolean disabled;
    public boolean isDisabled() {
        return disabled;
    }
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    /**
     * @return The id
     */
    public Integer getID() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setID(Integer id) {
        this.id = id;
    }
    /**
     * @return The UserID
     */
    public Integer getUserID() {
        return UserID;
    }

    /**
     * @param UserID The UserID
     */
    public void setUserID(Integer UserID) {
        this.UserID = UserID;
    }

    /**
     * @return The FirstName
     */
    public String getFirstName() {
        return FirstName;
    }

    /**
     * @param FirstName The FirstName
     */
    public void setFirstName(String FirstName) {
        this.FirstName = FirstName;
    }

    /**
     * @return The LastName
     */
    public String getLastName() {
        return LastName;
    }

    /**
     * @param LastName The LastName
     */
    public void setLastName(String LastName) {
        this.LastName = LastName;
    }

    /**
     * @return The MobileNo1
     */
    public String getMobileNo1() {
        return MobileNo1;
    }

    /**
     * @param MobileNo1 The MobileNo1
     */
    public void setMobileNo1(String MobileNo1) {
        this.MobileNo1 = MobileNo1;
    }

    /**
     * @return The EmailVerified
     */
    public Boolean getEmailVerified() {
        return EmailVerified;
    }

    /**
     * @param EmailVerified The EmailVerified
     */
    public void setEmailVerified(Boolean EmailVerified) {
        this.EmailVerified = EmailVerified;
    }

    /**
     * @return The MobileNo2
     */
    public String getMobileNo2() {
        return MobileNo2;
    }

    /**
     * @param MobileNo2 The MobileNo2
     */
    public void setMobileNo2(String MobileNo2) {
        this.MobileNo2 = MobileNo2;
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
     * @return The permanentAddress
     */
    public Address getPermanentAddress() {
        return permanentAddress;
    }

    /**
     * @param permanentAddress The permanentAddress
     */
    public void setPermanentAddress(Address permanentAddress) {
        this.permanentAddress = permanentAddress;
    }

    /**
     * @return The temporaryAddress
     */
    public Address getTemporaryAddress() {
        return temporaryAddress;
    }

    /**
     * @param temporaryAddress The temporaryAddress
     */
    public void setTemporaryAddress(Address temporaryAddress) {
        this.temporaryAddress = temporaryAddress;
    }

    /**
     * @return The DeviceID
     */
    public String getDeviceID() {
        return DeviceID;
    }

    /**
     * @param DeviceID The DeviceID
     */
    public void setDeviceID(String DeviceID) {
        this.DeviceID = DeviceID;
    }

    /**
     * @return The otpGenerateTime
     */
    public String getOtpGenerateTime() {
        return otpGenerateTime;
    }

    /**
     * @param otpGenerateTime The otpGenerateTime
     */
    public void setOtpGenerateTime(String otpGenerateTime) {
        this.otpGenerateTime = otpGenerateTime;
    }

    /**
     * @return The SecurityToken
     */
    public String getSecurityToken() {
        return SecurityToken;
    }

    /**
     * @param SecurityToken The SecurityToken
     */
    public void setSecurityToken(String SecurityToken) {
        this.SecurityToken = SecurityToken;
    }

    /**
     * @return The Manager
     */
    public com.bigbang.superteam.model.Manager getManager() {
        return Manager;
    }

    /**
     * @param Manager The Manager
     */
    public void setManager(com.bigbang.superteam.model.Manager Manager) {
        this.Manager = Manager;
    }

    /**
     * @return The InvalidLoginAttempt
     */
    public Integer getInvalidLoginAttempt() {
        return InvalidLoginAttempt;
    }

    /**
     * @param InvalidLoginAttempt The InvalidLoginAttempt
     */
    public void setInvalidLoginAttempt(Integer InvalidLoginAttempt) {
        this.InvalidLoginAttempt = InvalidLoginAttempt;
    }

    /**
     * @return The role
     */
    public Role getRole() {
        return role;
    }

    /**
     * @param role The role
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * @return The company
     */
    public Company getCompany() {
        return company;
    }

    /**
     * @param company The company
     */
    public void setCompany(Company company) {
        this.company = company;
    }

    /**
     * @return The picture
     */
    public String getPicture() {
        return picture;
    }

    /**
     * @param picture The picture
     */
    public void setPicture(String picture) {
        this.picture = picture;
    }

    /**
     * @return The published
     */
    public Boolean getPublished() {
        return published;
    }

    /**
     * @param published The published
     */
    public void setPublished(Boolean published) {
        this.published = published;
    }

    /**
     * @return The RoleID
     */
    public Integer getRoleId() {
        return roleId;
    }

    /**
     * @param roleId The UserID
     */
    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

}
