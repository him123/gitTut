package com.bigbang.superteam.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by USER 8 on 8/6/2015.
 */
public class InvitedUser implements Serializable {


    @Expose
    private Integer ID;
    @Expose
    private String EmailID;
    @Expose
    private String Name;
    @Expose
    private String MobileNo;
    @Expose
    private Integer CreatedBy;
    @Expose
    private Boolean Active;
    @Expose
    private Role roles;
    @Expose
    private String Status;
    @Expose
    private Manager manager;

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
     * @return The Active
     */
    public Boolean getActive() {
        return Active;
    }

    /**
     * @param Active The Active
     */
    public void setActive(Boolean Active) {
        this.Active = Active;
    }

    /**
     * @return The roles
     */
    public Role getRole() {
        return roles;
    }

    /**
     * @param roles The roles
     */
    public void setRole(Role roles) {
        this.roles = roles;
    }

    /**
     * @return The Status
     */
    public String getStatus() {
        return Status;
    }

    /**
     * @param Status The Status
     */
    public void setStatus(String Status) {
        this.Status = Status;
    }

    /**
     * @return The manager
     */
    public Manager getManager() {
        return manager;
    }

    /**
     * @param manager The manager
     */
    public void setManager(Manager manager) {
        this.manager = manager;
    }

}