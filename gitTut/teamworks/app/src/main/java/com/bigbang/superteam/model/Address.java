package com.bigbang.superteam.model;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import java.io.Serializable;

/**
 * Created by USER 8 on 7/23/2015.
 */
public class Address implements Serializable {

    @Expose
    private Integer AddressID;
    @Expose
    private String Country;
    @Expose
    private String State;
    @Expose
    private String City;
    @Expose
    private String AddressLine1;
    @Expose
    private String AddressLine2;
    @Expose
    private String Pincode;
    @Expose
    private String Lattitude;
    @Expose
    private String Longitude;
    @Expose
    private String Type;


    public static Address getAddress(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Address.class);
    }


    /**
     * @return The AddressID
     */
    public Integer getAddressID() {
        return AddressID;
    }

    /**
     * @param AddressID The AddressID
     */
    public void setAddressID(Integer AddressID) {
        this.AddressID = AddressID;
    }

    /**
     * @return The Country
     */
    public String getCountry() {
        return Country;
    }

    /**
     * @param Country The Country
     */
    public void setCountry(String Country) {
        this.Country = Country;
    }

    /**
     * @return The State
     */
    public String getState() {
        return State;
    }

    /**
     * @param State The State
     */
    public void setState(String State) {
        this.State = State;
    }

    /**
     * @return The City
     */
    public String getCity() {
        return City;
    }

    /**
     * @param City The City
     */
    public void setCity(String City) {
        this.City = City;
    }

    /**
     * @return The AddressLine1
     */
    public String getAddressLine1() {
        return AddressLine1;
    }

    /**
     * @param AddressLine1 The AddressLine1
     */
    public void setAddressLine1(String AddressLine1) {
        this.AddressLine1 = AddressLine1;
    }

    /**
     * @return The AddressLine2
     */
    public String getAddressLine2() {
        return AddressLine2;
    }

    /**
     * @param AddressLine2 The AddressLine2
     */
    public void setAddressLine2(String AddressLine2) {
        this.AddressLine2 = AddressLine2;
    }

    /**
     * @return The Pincode
     */
    public String getPincode() {
        return Pincode;
    }

    /**
     * @param Pincode The Pincode
     */
    public void setPincode(String Pincode) {
        this.Pincode = Pincode;
    }

    /**
     * @return The Lattitude
     */
    public String getLattitude() {
        return Lattitude;
    }

    /**
     * @param Lattitude The Lattitude
     */
    public void setLattitude(String Lattitude) {
        this.Lattitude = Lattitude;
    }

    /**
     * @return The Longitude
     */
    public String getLongitude() {
        return Longitude;
    }

    /**
     * @param Longitude The Longitude
     */
    public void setLongitude(String Longitude) {
        this.Longitude = Longitude;
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

}
