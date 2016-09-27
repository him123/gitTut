package com.bigbang.superteam.task.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by User on 9/5/2016.
 */
public class TaskAttachment implements Parcelable {

    public int id;
    public int attachmentid;
    public int taskid;
    public String path;
    public String lastmodifiedby;
    public String lastmodified;
    public String taskupdateid;

    public TaskAttachment() {

    }

    public TaskAttachment(Parcel in) {
        id = in.readInt();
        attachmentid = in.readInt();
        taskid = in.readInt();
        path = in.readString();
        lastmodifiedby = in.readString();
        lastmodified = in.readString();
        taskupdateid = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeLong(attachmentid);
        dest.writeLong(taskid);
        dest.writeString(path);
        dest.writeString(lastmodifiedby);
        dest.writeString(lastmodified);
        dest.writeString(taskupdateid);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TaskAttachment> CREATOR = new Parcelable.Creator<TaskAttachment>() {
        @Override
        public TaskAttachment createFromParcel(Parcel in) {
            return new TaskAttachment(in);
        }

        @Override
        public TaskAttachment[] newArray(int size) {
            return new TaskAttachment[size];
        }
    };
}
