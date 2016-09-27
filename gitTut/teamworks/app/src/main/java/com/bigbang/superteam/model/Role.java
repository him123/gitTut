package com.bigbang.superteam.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by USER 8 on 7/23/2015.
 */
public class Role implements Serializable {

    @Expose
    private Integer id;
    @Expose
    private String desc;
    @Expose
    private Integer applicationID;
    @Expose
    private Integer companyID;

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * @param desc The desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * @return The applicationID
     */
    public Integer getApplicationID() {
        return applicationID;
    }

    /**
     * @param applicationID The applicationID
     */
    public void setApplicationID(Integer applicationID) {
        this.applicationID = applicationID;
    }

    /**
     * @return The companyID
     */
    public Integer getCompanyID() {
        return companyID;
    }

    /**
     * @param companyID The companyID
     */
    public void setCompanyID(Integer companyID) {
        this.companyID = companyID;
    }

}
