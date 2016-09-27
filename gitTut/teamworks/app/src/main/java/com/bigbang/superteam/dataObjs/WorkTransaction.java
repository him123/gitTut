package com.bigbang.superteam.dataObjs;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.util.Log;

import com.bigbang.superteam.util.Constant;

/**
 * Created by USER 3 on 24/04/2015.
 */
public class WorkTransaction {
    public static final String TEXT = "Text";
    public static final String IMAGE = "Image";
    public static final String SOUND = "Sound";
    public static String TRANSACTION_CODE = "Transaction_Code";
    public static String USER_CODE = "User_Code";
    public static String TASK_CODE = "Task_Code";
    public static String MESSAGE = "Message_Text";
    public static String MESSAGE_TYPE = "Message_Type";
    public static String UPDATE_TYPE = "Update_Type";
    public static String MESSAGE_LINK = "Message_Link";
    public static String AMOUNT = "Amount";
    public static String DISCRIPTION = "Discription";
    public static String START_DATE = "Start_Date";
    public static String END_DATE = "End_Date";
    public static String REASON = "Reason";
    public static String STATUS = "Status";
    public static String INVOICE_DATE = "Invoice_Date";
    public static String DELEGATE_TO = "Delegate_To";
    public static String CREATED_ON = "Created_On";

    public String linkPath;
    public String amount;
    public String Discription;
    public String start_date;
    public String end_date;
    public String reason;
    public String status;
    public String invoice_date;
    public String delegate_to;
    public MediaPlayer mp;
    private int tr_code;
    private int user_code;
    private int task_code;
    private String message;
    private String message_type;
    private String update_type;
    private String link;
    private String created_on;

    public String getCreated_on() {
        return created_on;
    }

    public void setCreated_on(String created_on) {
        this.created_on = created_on;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getInvoice_date() {
        return invoice_date;
    }

    public void setInvoice_date(String invoice_date) {
        this.invoice_date = invoice_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String getDelegate_to() {
        return delegate_to;
    }

    public void setDelegate_to(String delegate_to) {
        this.delegate_to = delegate_to;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getDiscription() {
        return Discription;
    }

    public void setDiscription(String discription) {
        Discription = discription;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getLinkPath() {
        return linkPath;
    }

    public void setLinkPath(String linkPath) {
        this.linkPath = linkPath;
    }

    public int getTr_code() {
        return tr_code;
    }

    public void setTr_code(int tr_code) {
        this.tr_code = tr_code;
    }

    public int getUser_code() {
        return user_code;
    }

    public void setUser_code(int user_code) {
        this.user_code = user_code;
    }

    public int getTask_code() {
        return task_code;
    }

    public void setTask_code(int task_code) {
        this.task_code = task_code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public String getUpdate_type() {
        return update_type;
    }

    public void setUpdate_type(String update_type) {
        this.update_type = update_type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }


    public void saveInDb(SQLiteDatabase db) {
        ContentValues taskValues = new ContentValues();
        taskValues.put(WorkTransaction.TRANSACTION_CODE, getTr_code());
        taskValues.put(WorkTransaction.TASK_CODE, getTask_code());
        taskValues.put(WorkTransaction.USER_CODE, getUser_code());
        taskValues.put(WorkTransaction.MESSAGE, getDiscription());
        taskValues.put(WorkTransaction.MESSAGE_LINK, getLink());
        taskValues.put(WorkTransaction.MESSAGE_TYPE, getMessage_type());
        taskValues.put(WorkTransaction.UPDATE_TYPE, getUpdate_type());

        taskValues.put(WorkTransaction.START_DATE, getStart_date());
        taskValues.put(WorkTransaction.END_DATE, getEnd_date());
        taskValues.put(WorkTransaction.DELEGATE_TO, getDelegate_to());
        taskValues.put(WorkTransaction.DISCRIPTION, getDiscription());
        taskValues.put(WorkTransaction.AMOUNT, getAmount());
        taskValues.put(WorkTransaction.INVOICE_DATE, getInvoice_date());
        taskValues.put(WorkTransaction.STATUS, getStatus());
        taskValues.put(WorkTransaction.REASON, getReason());
        taskValues.put(WorkTransaction.CREATED_ON, getCreated_on());
        db.insert(Constant.WorkTransaction, null, taskValues);
        Log.d("Work Transaction", " Transaction Saved in Db, id:" + getTr_code() + " " + getDiscription());
    }
}
