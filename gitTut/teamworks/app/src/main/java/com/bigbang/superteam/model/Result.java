package com.bigbang.superteam.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by USER 8 on 7/23/2015.
 */
public class Result {

    @Expose
    private String status;
    @Expose
    private List<User> data = new ArrayList<User>();
    @Expose
    private String message;

    /**
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return The data
     */
    public List<User> getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(List<User> data) {
        this.data = data;
    }

    /**
     * @return The message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

}
