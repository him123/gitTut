package com.bigbang.superteam.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by USER 8 on 28-Apr-16.
 */
public class Holidays implements Parcelable {

    public String localId;
    public String holidayId;
    public String holidayDate;
    public String holidayName;

    public Holidays() {
    }

    protected Holidays(Parcel in) {
        localId = in.readString();
        holidayId = in.readString();
        holidayDate = in.readString();
        holidayName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(localId);
        dest.writeString(holidayId);
        dest.writeString(holidayDate);
        dest.writeString(holidayName);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Holidays> CREATOR = new Parcelable.Creator<Holidays>() {
        @Override
        public Holidays createFromParcel(Parcel in) {
            return new Holidays(in);
        }

        @Override
        public Holidays[] newArray(int size) {
            return new Holidays[size];
        }
    };
}