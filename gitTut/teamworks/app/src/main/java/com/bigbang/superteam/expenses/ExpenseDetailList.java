package com.bigbang.superteam.expenses;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 7/30/2016.
 */
public class ExpenseDetailList implements Parcelable{

    public Integer id;
    public Integer userID;
    public String expenseType;
    public double amountRequested;
    public double amountApproved;
    public String billNo;
    public String billAttachmentPath;

    protected ExpenseDetailList(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readInt();
        userID = in.readByte() == 0x00 ? null : in.readInt();
        expenseType = in.readString();
        amountRequested = in.readDouble();
        amountApproved = in.readDouble();
        billNo = in.readString();
        billAttachmentPath = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(id);
        }
        if (userID == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(userID);
        }
        dest.writeString(expenseType);
        dest.writeDouble(amountRequested);
        dest.writeDouble(amountApproved);
        dest.writeString(billNo);
        dest.writeString(billAttachmentPath);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ExpenseDetailList> CREATOR = new Parcelable.Creator<ExpenseDetailList>() {
        @Override
        public ExpenseDetailList createFromParcel(Parcel in) {
            return new ExpenseDetailList(in);
        }

        @Override
        public ExpenseDetailList[] newArray(int size) {
            return new ExpenseDetailList[size];
        }
    };
}
