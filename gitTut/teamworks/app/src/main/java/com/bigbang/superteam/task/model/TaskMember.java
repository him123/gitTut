package com.bigbang.superteam.task.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 9/2/2016.
 */
public class TaskMember implements Parcelable {
    public int id;
    public int userID;
    public int TaskID;
    public String memberType;
    public String taskRights;
    public boolean active;
    public String userName;
    public String contact_num;

    public TaskMember() {
    }

    protected TaskMember(Parcel in) {
        id = in.readInt();
        userID = in.readInt();
        TaskID = in.readInt();
        memberType = in.readString();
        taskRights = in.readString();
        active = in.readByte() != 0x00;
        userName = in.readString();
        contact_num = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(userID);
        dest.writeLong(TaskID);
        dest.writeString(memberType);
        dest.writeString(taskRights);
        dest.writeByte((byte) (active ? 0x01 : 0x00));
        dest.writeString(userName);
        dest.writeString(contact_num);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TaskMember> CREATOR = new Parcelable.Creator<TaskMember>() {
        @Override
        public TaskMember createFromParcel(Parcel in) {
            return new TaskMember(in);
        }

        @Override
        public TaskMember[] newArray(int size) {
            return new TaskMember[size];
        }
    };
}
