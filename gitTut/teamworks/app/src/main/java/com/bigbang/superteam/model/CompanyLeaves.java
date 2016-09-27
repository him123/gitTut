package com.bigbang.superteam.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by USER 8 on 25-May-16.
 */
public class CompanyLeaves implements Parcelable {

    public String id;
    public String companyId;
    public String leaveType;
    public String noOfLeaves;
    public String leaveUpdateCyle;
    public String active;
    public String modifiedBy;
    public String lastModified;

    public CompanyLeaves() {
    }

    protected CompanyLeaves(Parcel in) {
        id = in.readString();
        companyId = in.readString();
        leaveType = in.readString();
        noOfLeaves = in.readString();
        leaveUpdateCyle = in.readString();
        active = in.readString();
        modifiedBy = in.readString();
        lastModified = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(companyId);
        dest.writeString(leaveType);
        dest.writeString(noOfLeaves);
        dest.writeString(leaveUpdateCyle);
        dest.writeString(active);
        dest.writeString(modifiedBy);
        dest.writeString(lastModified);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CompanyLeaves> CREATOR = new Parcelable.Creator<CompanyLeaves>() {
        @Override
        public CompanyLeaves createFromParcel(Parcel in) {
            return new CompanyLeaves(in);
        }

        @Override
        public CompanyLeaves[] newArray(int size) {
            return new CompanyLeaves[size];
        }
    };
}