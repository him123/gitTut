package com.bigbang.superteam.task.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 9/5/2016.
 */
public class TaskType implements Parcelable {

    public String tasktypeid;
    public String tasktype;

    public TaskType() {

    }

    protected TaskType(Parcel in) {
        tasktypeid = in.readString();
        tasktype = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(tasktypeid);
        dest.writeString(tasktype);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TaskType> CREATOR = new Parcelable.Creator<TaskType>() {
        @Override
        public TaskType createFromParcel(Parcel in) {
            return new TaskType(in);
        }

        @Override
        public TaskType[] newArray(int size) {
            return new TaskType[size];
        }
    };
}
