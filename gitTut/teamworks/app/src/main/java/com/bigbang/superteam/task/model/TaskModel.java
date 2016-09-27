package com.bigbang.superteam.task.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.bigbang.superteam.model.Address;
import com.bigbang.superteam.model.Customer;
import com.bigbang.superteam.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 9/2/2016.
 */
public class TaskModel implements Parcelable {

    public TaskModel() {
    }

    public int id;
    public int taskID;
    public int companyID;
    public String name;
    public String description;
    public String taskType;
    public Address address;
    public String priority;
    public String startTime;
    public String endTime;
    public String estimatedTime;
    public String status;
    public boolean active;
    public double budget;
    public User approvedBy;
    public int lastModifiedBy;
    public String lastModified;
    public List<User> assignedToList;
    public List<User> ccList;
    public List<String> attachments;
    public TaskChat taskChats;
    public String taskRight;
    public double invoiceAmount;
    public double outstandingAmount;
    public double advancePaid;
    public List<Customer> taskCustVend;
    public int alart_counter;

    public String invoiceDate;
    public String dueDate;
    public String pastHistory;
    public String vendorPreference;
    public String customerType;
    public String daycodes;
    public String frequency;
    public String createdByName;
    public int address_id;
    public String addressStr;
    public int createdById;

    protected TaskModel(Parcel in) {
        id = in.readInt();
        taskID = in.readInt();
        companyID = in.readInt();
        name = in.readString();
        description = in.readString();
        taskType = in.readString();
        address = (Address) in.readValue(Address.class.getClassLoader());
        priority = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        estimatedTime = in.readString();
        status = in.readString();
        active = in.readByte() != 0x00;
        budget = in.readDouble();
        approvedBy = (User) in.readValue(User.class.getClassLoader());
        lastModifiedBy = in.readInt();
        lastModified = in.readString();
        if (in.readByte() == 0x01) {
            assignedToList = new ArrayList<User>();
            in.readList(assignedToList, User.class.getClassLoader());
        } else {
            assignedToList = null;
        }
        if (in.readByte() == 0x01) {
            ccList = new ArrayList<User>();
            in.readList(ccList, User.class.getClassLoader());
        } else {
            ccList = null;
        }
        if (in.readByte() == 0x01) {
            attachments = new ArrayList<String>();
            in.readList(attachments, String.class.getClassLoader());
        } else {
            attachments = null;
        }
        taskChats = (TaskChat) in.readValue(TaskChat.class.getClassLoader());
        taskRight = in.readString();
        invoiceAmount = in.readDouble();
        outstandingAmount = in.readDouble();
        advancePaid = in.readDouble();
        if (in.readByte() == 0x01) {
            taskCustVend = new ArrayList<Customer>();
            in.readList(taskCustVend, Customer.class.getClassLoader());
        } else {
            taskCustVend = null;
        }
        alart_counter = in.readInt();
        invoiceDate = in.readString();
        dueDate = in.readString();
        pastHistory = in.readString();
        vendorPreference = in.readString();
        customerType = in.readString();
        daycodes = in.readString();
        frequency = in.readString();
        createdByName = in.readString();
        address_id = in.readInt();
        addressStr = in.readString();
        createdById = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(taskID);
        dest.writeInt(companyID);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(taskType);
        dest.writeValue(address);
        dest.writeString(priority);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(estimatedTime);
        dest.writeString(status);
        dest.writeByte((byte) (active ? 0x01 : 0x00));
        dest.writeDouble(budget);
        dest.writeValue(approvedBy);
        dest.writeInt(lastModifiedBy);
        dest.writeString(lastModified);
        if (assignedToList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(assignedToList);
        }
        if (ccList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(ccList);
        }
        if (attachments == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(attachments);
        }
        dest.writeValue(taskChats);
        dest.writeString(taskRight);
        dest.writeDouble(invoiceAmount);
        dest.writeDouble(outstandingAmount);
        dest.writeDouble(advancePaid);
        if (taskCustVend == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(taskCustVend);
        }
        dest.writeInt(alart_counter);
        dest.writeString(invoiceDate);
        dest.writeString(dueDate);
        dest.writeString(pastHistory);
        dest.writeString(vendorPreference);
        dest.writeString(customerType);
        dest.writeString(daycodes);
        dest.writeString(frequency);
        dest.writeString(createdByName);
        dest.writeInt(address_id);
        dest.writeString(addressStr);
        dest.writeInt(createdById);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<TaskModel> CREATOR = new Parcelable.Creator<TaskModel>() {
        @Override
        public TaskModel createFromParcel(Parcel in) {
            return new TaskModel(in);
        }

        @Override
        public TaskModel[] newArray(int size) {
            return new TaskModel[size];
        }
    };
}