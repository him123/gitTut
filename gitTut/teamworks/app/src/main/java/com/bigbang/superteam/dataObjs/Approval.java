package com.bigbang.superteam.dataObjs;

import java.io.Serializable;

/**
 * Created by USER 3 on 26/05/2015.
 */
public class Approval implements Serializable {
    int userId;
    String Type;
    String Title;
    String Description;
    String Image;
    String Date;
    String Id;
    String data;
    int pk;
    String reason;
    String endDate;

    int Status;
    String TransactionID;
    String Time;


    public static String ID = "id";
    public static String USERID = "userid";
    public static String TYPE = "type";
    public static String TITLE = "title";
    public static String DESCRIPTION = "description";
    public static String IMAGE = "img";
    public static String DATE = "date";
    public static String ENDDATE = "end_date";
    public static String REASON = "reason";
    public static String STATUS = "Status";
    public static String TRANSACTIONID = "TransactionID";
    public static String TIME = "Time";
    public static String DATA = "data";

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public String getTransactionID() {
        return TransactionID;
    }

    public void setTransactionID(String transactionID) {
        TransactionID = transactionID;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }
}
