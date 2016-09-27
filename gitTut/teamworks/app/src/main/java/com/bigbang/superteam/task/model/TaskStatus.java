package com.bigbang.superteam.task.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 9/5/2016.
 */
public class TaskStatus implements Parcelable {

    public int taskstatusid;
    public String taskstatus;
    public String description;
    public String lastmodified;

    public TaskStatus(){

    }

    protected TaskStatus(Parcel in) {
        taskstatusid = in.readInt();
        taskstatus = in.readString();
        description = in.readString();
        lastmodified = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(taskstatusid);
        dest.writeString(taskstatus);
        dest.writeString(description);
        dest.writeString(lastmodified);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TaskStatus> CREATOR = new Parcelable.Creator<TaskStatus>() {
        @Override
        public TaskStatus createFromParcel(Parcel in) {
            return new TaskStatus(in);
        }

        @Override
        public TaskStatus[] newArray(int size) {
            return new TaskStatus[size];
        }
    };
}
