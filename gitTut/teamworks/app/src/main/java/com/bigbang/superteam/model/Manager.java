package com.bigbang.superteam.model;

import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by USER 8 on 7/23/2015.
 */
public class Manager implements Serializable {

    @Expose
    private Integer UserID;
    @Expose
    private String FirstName;
    @Expose
    private String LastName;
    @Expose
    private Boolean EmailVerified;
    @Expose
    private Integer InvalidLoginAttempt;

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

}
