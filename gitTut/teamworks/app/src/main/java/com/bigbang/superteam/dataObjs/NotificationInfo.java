package com.bigbang.superteam.dataObjs;

/**
 * Created by USER 3 on 05/06/2015.
 */
public class NotificationInfo {

    public static String TRANSACTION_ID = "TransactionID";
    public static String ID = "Id";
    public static String TITLE = "Title";
    public static String DESCRIPTION = "Description";
    public static String IMAGE_URL = "Image_Url";
    public static String TYPE = "Type";
    public static String TIME = "Time";
    public static String EXTRA = "Extra";
    public static String USER_ID = "User_Id";

    int id;
    String Title;
    String Description;
    String Image_Url;
    String Type;
    String Time;
    String Extra;
    String User_Id;
    String TransactionID;

    public String getTransactionID() {
        return TransactionID;
    }

    public void setTransactionID(String transactionID) {
        TransactionID = transactionID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getImage_Url() {
        return Image_Url;
    }

    public void setImage_Url(String image_Url) {
        Image_Url = image_Url;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getExtra() {
        return Extra;
    }

    public void setExtra(String extra) {
        Extra = extra;
    }

    public String getUser_Id() {
        return User_Id;
    }

    public void setUser_Id(String user_Id) {
        User_Id = user_Id;
    }


}
