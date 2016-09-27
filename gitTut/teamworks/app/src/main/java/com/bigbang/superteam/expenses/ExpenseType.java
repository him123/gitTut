package com.bigbang.superteam.expenses;

import android.os.Parcel;
import android.os.Parcelable;

import com.bigbang.superteam.model.User;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;

/**
 * Created by User on 7/30/2016.
 */
public class ExpenseType implements Parcelable {

    public Integer expenseId;
    public User user;
    public Integer companyID;
    public String startDate;
    public String endDate;
    public double totalExpenseRequested;
    public double totalExpenseApproved;
    public String requesterComment;
    public String approverComment;
    public User approvedBy;
    public String status;
    public String lastUpdatedBy;
    public ArrayList<ExpenseDetailList> expenseDetailsList;
    public String transactionID;

    protected ExpenseType(Parcel in) {
        expenseId = in.readByte() == 0x00 ? null : in.readInt();
        user = (User) in.readValue(User.class.getClassLoader());
        companyID = in.readByte() == 0x00 ? null : in.readInt();
        startDate = in.readString();
        endDate = in.readString();
        totalExpenseRequested = in.readDouble();
        totalExpenseApproved = in.readDouble();
        requesterComment = in.readString();
        approverComment = in.readString();
        approvedBy = (User) in.readValue(User.class.getClassLoader());
        status = in.readString();
        lastUpdatedBy = in.readString();
        if (in.readByte() == 0x01) {
            expenseDetailsList = new ArrayList<ExpenseDetailList>();
            in.readList(expenseDetailsList, ExpenseDetailList.class.getClassLoader());
        } else {
            expenseDetailsList = null;
        }
        transactionID = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (expenseId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(expenseId);
        }
        dest.writeValue(user);
        if (companyID == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(companyID);
        }
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeDouble(totalExpenseRequested);
        dest.writeDouble(totalExpenseApproved);
        dest.writeString(requesterComment);
        dest.writeString(approverComment);
        dest.writeValue(approvedBy);
        dest.writeString(status);
        dest.writeString(lastUpdatedBy);
        if (expenseDetailsList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(expenseDetailsList);
        }
        dest.writeString(transactionID);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ExpenseType> CREATOR = new Parcelable.Creator<ExpenseType>() {
        @Override
        public ExpenseType createFromParcel(Parcel in) {
            return new ExpenseType(in);
        }

        @Override
        public ExpenseType[] newArray(int size) {
            return new ExpenseType[size];
        }
    };
}
