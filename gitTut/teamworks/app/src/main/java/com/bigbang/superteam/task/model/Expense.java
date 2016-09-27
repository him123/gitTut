package com.bigbang.superteam.task.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Himanshu on 9/14/2016.
 */
public class Expense implements Parcelable {

    public int id;
    public int expenseId;
    public int taskID;
    public int taskChatID;
    public double expenseAmount;
    public String expenseDate;
    public int lastModifiedBy;

    public Expense(Parcel in) {
        id = in.readInt();
        expenseId = in.readInt();
        taskID = in.readInt();
        taskChatID = in.readInt();
        expenseAmount = in.readDouble();
        lastModifiedBy = in.readInt();
        expenseDate = in.readString();
    }

    public Expense() {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(expenseId);
        dest.writeInt(taskID);
        dest.writeInt(taskChatID);
        dest.writeDouble(expenseAmount);
        dest.writeInt(lastModifiedBy);
        dest.writeString(expenseDate);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Expense> CREATOR = new Parcelable.Creator<Expense>() {
        @Override
        public Expense createFromParcel(Parcel in) {
            return new Expense(in);
        }

        @Override
        public Expense[] newArray(int size) {
            return new Expense[size];
        }
    };
}