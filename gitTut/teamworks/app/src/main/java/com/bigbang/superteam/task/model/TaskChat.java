package com.bigbang.superteam.task.model;

import android.media.MediaPlayer;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Himanshu on 9/14/2016.
 */
public class TaskChat implements Parcelable {

    public int id;
    public int taskID;
    public int TaskEditID;
    public String chatType;
    public String chatStatus;
    public String message;
    public String transactionID;
    public int createdById;
    public String createdByName;
    public String createdOn;
    public int approvedById;
    public String approvedByName;
    public int lastModifiedBy;
    public String startTime;
    public String endTime;
    public Expense expense;
    public String dataType;
    public String attachmentPath;
    public ArrayList<Integer> queryTo;
    public MediaPlayer mp;

    public TaskChat() {

    }

    protected TaskChat(Parcel in) {
        id = in.readInt();
        taskID = in.readInt();
        TaskEditID = in.readInt();
        chatType = in.readString();
        chatStatus = in.readString();
        message = in.readString();
        transactionID = in.readString();
        createdById = in.readInt();
        createdByName = in.readString();
        createdOn = in.readString();
        approvedById = in.readInt();
        approvedByName = in.readString();
        lastModifiedBy = in.readInt();
        startTime = in.readString();
        endTime = in.readString();
        expense = (Expense) in.readValue(Expense.class.getClassLoader());
        dataType = in.readString();
        attachmentPath = in.readString();
        if (in.readByte() == 0x01) {
            queryTo = new ArrayList<Integer>();
            in.readList(queryTo, Integer.class.getClassLoader());
        } else {
            queryTo = null;
        }
        mp = (MediaPlayer) in.readValue(MediaPlayer.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(taskID);
        dest.writeInt(TaskEditID);
        dest.writeString(chatType);
        dest.writeString(chatStatus);
        dest.writeString(message);
        dest.writeString(transactionID);
        dest.writeInt(createdById);
        dest.writeString(createdByName);
        dest.writeString(createdOn);
        dest.writeInt(approvedById);
        dest.writeString(approvedByName);
        dest.writeInt(lastModifiedBy);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeValue(expense);
        dest.writeString(dataType);
        dest.writeString(attachmentPath);
        if (queryTo == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(queryTo);
        }
        dest.writeValue(mp);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TaskChat> CREATOR = new Parcelable.Creator<TaskChat>() {
        @Override
        public TaskChat createFromParcel(Parcel in) {
            return new TaskChat(in);
        }

        @Override
        public TaskChat[] newArray(int size) {
            return new TaskChat[size];
        }
    };
}