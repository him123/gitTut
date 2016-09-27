package com.bigbang.superteam.task.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.bigbang.superteam.DB.TeamWorksDBDAO;
import com.bigbang.superteam.task.model.TaskChat;
import com.bigbang.superteam.task.model.TaskModel;
import com.bigbang.superteam.util.SQLiteHelper;

import java.util.ArrayList;

/**
 * Created by User on 9/3/2016.
 */
public class TaskChatDAO extends TeamWorksDBDAO {

    public TaskChatDAO(Context contxt) {
        super(contxt);
    }

    public TaskChatDAO() {
        super();
    }

    public long save(TaskChat taskChat) {

        long status;
        ContentValues values = new ContentValues();

        values.put(SQLiteHelper.TM_C_TASKID, taskChat.taskID);
        values.put(SQLiteHelper.TM_C_TASKEDIT_ID, taskChat.TaskEditID);
        values.put(SQLiteHelper.TM_C_CHAT_TYPE, taskChat.chatType);
        values.put(SQLiteHelper.TM_C_STATUS, taskChat.chatStatus);
        values.put(SQLiteHelper.TM_C_MESSAGE, taskChat.message);
        values.put(SQLiteHelper.TM_C_DTYPE, taskChat.dataType);
        values.put(SQLiteHelper.TM_C_TRANSACTIONID, taskChat.transactionID);
        values.put(SQLiteHelper.TS_EX_CREATED_ON, taskChat.createdOn);
        values.put(SQLiteHelper.TM_C_CREATEDBYID, taskChat.createdById);
        values.put(SQLiteHelper.TM_C_CREATEDBYNAME, taskChat.createdByName);
        values.put(SQLiteHelper.TM_C_APPROVEDBYID, taskChat.approvedById);
        values.put(SQLiteHelper.TM_C_APPROVEDBYNAME, taskChat.approvedByName);
        values.put(SQLiteHelper.TA_ATTACHMENT_PATH, taskChat.attachmentPath);
        values.put(SQLiteHelper.TM_C_LASTMODIFIEDBY, taskChat.lastModifiedBy);

        status = database.insert(SQLiteHelper.TABLE_TASK_CHAT, null, values);
        if (status != -1)
            Log.d("Insert Task Master", "*********************************************successfull");
        else
            Log.d("Insert Task Master", "*********************************************unsuccessfull");

        return status;
    }

    public long update(TaskChat taskChat) {

        long status;
        ContentValues values = new ContentValues();

        values.put(SQLiteHelper.TM_C_TASKID, taskChat.taskID);
        values.put(SQLiteHelper.TM_C_TASKEDIT_ID, taskChat.TaskEditID);
        values.put(SQLiteHelper.TM_C_CHAT_TYPE, taskChat.chatType);
        values.put(SQLiteHelper.TM_C_STATUS, taskChat.chatStatus);
        values.put(SQLiteHelper.TM_C_MESSAGE, taskChat.message);
        values.put(SQLiteHelper.TM_C_DTYPE, taskChat.dataType);
        values.put(SQLiteHelper.TM_C_TRANSACTIONID, taskChat.transactionID);
        values.put(SQLiteHelper.TS_EX_CREATED_ON, taskChat.createdOn);
        values.put(SQLiteHelper.TM_C_CREATEDBYID, taskChat.createdById);
        values.put(SQLiteHelper.TM_C_CREATEDBYNAME, taskChat.createdByName);
        values.put(SQLiteHelper.TM_C_APPROVEDBYID, taskChat.approvedById);
        values.put(SQLiteHelper.TM_C_APPROVEDBYNAME, taskChat.approvedByName);
        values.put(SQLiteHelper.TA_ATTACHMENT_PATH, taskChat.attachmentPath);
        values.put(SQLiteHelper.TM_C_LASTMODIFIEDBY, taskChat.lastModifiedBy);

        status = database.update(SQLiteHelper.TABLE_TASK_CHAT, values, SQLiteHelper.TK_ID + " = ?", new String[]{taskChat.id + ""});
//        status = database.update(SQLiteHelper.TABLE_TASK_MASTER, values, SQLiteHelper.TK_ID + " = ?", new String[]{taskChat.id + ""});
        if (status != -1)
            Log.d("Insert Task Master", "*********************************************successfull");
        else
            Log.d("Insert Task Master", "*********************************************unsuccessfull");

        return status;
    }

    public ArrayList<TaskChat> getTaskChatData(int taskId) {
        ArrayList<TaskChat> taskList = new ArrayList<>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_TASK_CHAT,
                new String[]{
                        SQLiteHelper.TM_C_TASKID,//0
                        SQLiteHelper.TM_C_TASKEDIT_ID,//1
                        SQLiteHelper.TM_C_CHAT_TYPE,//2
                        SQLiteHelper.TM_C_STATUS,//3
                        SQLiteHelper.TM_C_MESSAGE,//4
                        SQLiteHelper.TM_C_DTYPE,//5
                        SQLiteHelper.TM_C_TRANSACTIONID,//6
                        SQLiteHelper.TS_EX_CREATED_ON,//7
                        SQLiteHelper.TM_C_CREATEDBYID,//8
                        SQLiteHelper.TM_C_CREATEDBYNAME,//9
                        SQLiteHelper.TM_C_APPROVEDBYID,//10
                        SQLiteHelper.TM_C_APPROVEDBYNAME,//11
                        SQLiteHelper.TA_LAST_MODIFIED_BY,//12
                        SQLiteHelper.TA_ATTACHMENT_PATH//13

                }, SQLiteHelper.TM_C_TASKID + "=" + taskId, null, null,
                null, null);

        while (cursor.moveToNext()) {

            TaskChat taskChat = new TaskChat();

            taskChat.taskID = cursor.getInt(0);
            taskChat.TaskEditID = cursor.getInt(1);
            taskChat.chatType = cursor.getString(2);
            taskChat.chatStatus = cursor.getString(3);
            taskChat.message = cursor.getString(4);
            taskChat.dataType = cursor.getString(5);
            taskChat.transactionID = cursor.getString(6);
            taskChat.createdOn = cursor.getString(7);
            taskChat.createdById = cursor.getInt(8);
            taskChat.createdByName = cursor.getString(9);
            taskChat.approvedById = cursor.getInt(10);
            taskChat.approvedByName = cursor.getString(11);
            taskChat.lastModifiedBy = cursor.getInt(12);
            taskChat.attachmentPath = cursor.getString(13);

            taskList.add(taskChat);
        }
        return taskList;
    }

    public long updateAttachment(String transactionId, String attachmentPath) {
        ContentValues values = new ContentValues();

        values.put(SQLiteHelper.TA_ATTACHMENT_PATH, attachmentPath);

        long result = database.update(SQLiteHelper.TABLE_TASK_CHAT, values,
                SQLiteHelper.TM_C_TRANSACTIONID + " = ?",
                new String[]{transactionId + ""});
        Log.d("Update Result:", "=" + result);
        return result;
    }


    public int delete(int local_id) {
        return database.delete(SQLiteHelper.TABLE_TASK_MASTER, "id =?",
                new String[]{local_id + ""});
    }
}
