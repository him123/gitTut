package com.bigbang.superteam.dataObjs;

/**
 * Created by USER 3 on 19/06/2015.
 */
public class User {
    public static String PK = "pk";
    public static String USER_ID = "userID";
    public static String USER_IMAGE = "picture";
    public static String MOBILE = "mobileNo1";
    public static String EMAIL = "emailID";
    public static String ADDRESS = "permanentAddress";
    public static String FIRST_NAME = "firstName";
    public static String LAST_NAME = "lastName";
    public static String SELECTED = "selected";
    public static String DISABLED = "disabled";
    public int pk;
    public String mobileNo;
    public String EmailId;
    public String Address;
    public boolean selected;
    private String Name;
    private String firstName;
    private String lastName;
    private String User_Image;
    private int User_Id;

    public boolean disabled; // for project CC

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public String getUser_Image() {
        return User_Image;
    }

    public void setUser_Image(String user_Image) {
        User_Image = user_Image;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getName() {
        return firstName + " " + lastName;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getUser_Id() {
        return User_Id;
    }

    public void setUser_Id(int user_Id) {
        User_Id = user_Id;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getEmailId() {
        return EmailId;
    }

    public void setEmailId(String emailId) {
        EmailId = emailId;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

//    public static String COMPANY_ID = "Company_Id";
//    private int Company_ID;
//    public static String USER_TYPE = "User_Type";
//    private String User_Type;


}
