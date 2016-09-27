package com.bigbang.superteam.task.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.bigbang.superteam.DB.TeamWorksDBDAO;
import com.bigbang.superteam.task.model.TaskMember;
import com.bigbang.superteam.task.model.TaskModel;
import com.bigbang.superteam.util.SQLiteHelper;

import java.util.ArrayList;

/**
 * Created by User on 9/3/2016.
 */
public class TaskDAO extends TeamWorksDBDAO {

    public TaskDAO(Context contxt) {
        super(contxt);
    }

    public TaskDAO() {
        super();
    }

    public long save(TaskModel taskModel) {

        long status;
        ContentValues values = new ContentValues();

        values.put(SQLiteHelper.TK_TASK_ID, taskModel.taskID);
        values.put(SQLiteHelper.TK_NAME, taskModel.name);
        values.put(SQLiteHelper.TK_DESC, taskModel.description);
        values.put(SQLiteHelper.TK_TYPE, taskModel.taskType);
        values.put(SQLiteHelper.TK_PRIORITY, taskModel.priority);
        values.put(SQLiteHelper.TK_START_TIME, taskModel.startTime);
        values.put(SQLiteHelper.TK_END_TIME, taskModel.endTime);
        values.put(SQLiteHelper.TK_ESTIMATE_TIME, taskModel.estimatedTime);
        values.put(SQLiteHelper.TK_STATUS, taskModel.status);
        values.put(SQLiteHelper.TK_ACTIVE, taskModel.active);
        values.put(SQLiteHelper.TK_BUDGET, taskModel.budget);
        values.put(SQLiteHelper.TK_LAST_MODIFIED_BY, taskModel.lastModifiedBy);
        values.put(SQLiteHelper.TK_LAST_MODIFIED, taskModel.lastModified);
        values.put(SQLiteHelper.TM_C_CREATEDBYNAME, taskModel.createdByName);
        values.put(SQLiteHelper.TK_ADDRESS_ID, taskModel.address_id);
        values.put(SQLiteHelper.TK_ADDRESSSTR, taskModel.addressStr);
        values.put(SQLiteHelper.TK_INVOICE_AMT, taskModel.invoiceAmount);
        values.put(SQLiteHelper.TK_INVOICE_DATE, taskModel.invoiceDate);
        values.put(SQLiteHelper.TK_PAST_HISTORY, taskModel.pastHistory);
        values.put(SQLiteHelper.TK_ADVANCE_PAID, taskModel.advancePaid);
        values.put(SQLiteHelper.TK_VENDOR_PREFERENCE, taskModel.vendorPreference);
        values.put(SQLiteHelper.TK_DAY_CODES, taskModel.daycodes);
        values.put(SQLiteHelper.TK_FREQUENCY, taskModel.frequency);
        values.put(SQLiteHelper.TK_OUTSTANDING_AMT, taskModel.outstandingAmount);
        values.put(SQLiteHelper.TK_CUSTOMER_TYPE, taskModel.customerType);

        status = database.insert(SQLiteHelper.TABLE_TASK_MASTER, null, values);
        if (status != -1)
            Log.d("Insert Task Master", "*********************************************successfull");
        else
            Log.d("Insert Task Master", "*********************************************unsuccessfull");

        return status;
    }


    //UPDATE
    public long update(TaskModel taskModel) {

        long status;
        ContentValues values = new ContentValues();

        values.put(SQLiteHelper.TK_TASK_ID, taskModel.taskID);
        values.put(SQLiteHelper.TK_NAME, taskModel.name);
        values.put(SQLiteHelper.TK_DESC, taskModel.description);
        values.put(SQLiteHelper.TK_TYPE, taskModel.taskType);
        values.put(SQLiteHelper.TK_PRIORITY, taskModel.priority);
        values.put(SQLiteHelper.TK_START_TIME, taskModel.startTime);
        values.put(SQLiteHelper.TK_END_TIME, taskModel.endTime);
        values.put(SQLiteHelper.TK_ESTIMATE_TIME, taskModel.estimatedTime);
        values.put(SQLiteHelper.TK_STATUS, taskModel.status);
        values.put(SQLiteHelper.TK_ACTIVE, taskModel.active);
        values.put(SQLiteHelper.TK_BUDGET, taskModel.budget);
        values.put(SQLiteHelper.TK_LAST_MODIFIED_BY, taskModel.lastModifiedBy);
        values.put(SQLiteHelper.TK_LAST_MODIFIED, taskModel.lastModified);
        values.put(SQLiteHelper.TM_C_CREATEDBYNAME, taskModel.createdByName);
        values.put(SQLiteHelper.TK_ADDRESS_ID, taskModel.address_id);
        values.put(SQLiteHelper.TK_ADDRESSSTR, taskModel.addressStr);
        values.put(SQLiteHelper.TK_INVOICE_AMT, taskModel.invoiceAmount);
        values.put(SQLiteHelper.TK_INVOICE_DATE, taskModel.invoiceDate);
        values.put(SQLiteHelper.TK_PAST_HISTORY, taskModel.pastHistory);
        values.put(SQLiteHelper.TK_ADVANCE_PAID, taskModel.advancePaid);
        values.put(SQLiteHelper.TK_VENDOR_PREFERENCE, taskModel.vendorPreference);
        values.put(SQLiteHelper.TK_DAY_CODES, taskModel.daycodes);
        values.put(SQLiteHelper.TK_FREQUENCY, taskModel.frequency);
        values.put(SQLiteHelper.TK_OUTSTANDING_AMT, taskModel.outstandingAmount);
        values.put(SQLiteHelper.TK_CUSTOMER_TYPE, taskModel.customerType);

        //Update task_id in attachment table


        status = database.update(SQLiteHelper.TABLE_TASK_MASTER, values, SQLiteHelper.TK_ID + " = ?", new String[]{taskModel.id + ""});
        if (status != -1)
            Log.d("Update Task Master", "*********************************************successfull");
        else
            Log.d("Update Task Master", "*********************************************unsuccessfull");

        return status;
    }

    public ArrayList<TaskModel> getTaskData3() {
        ArrayList<TaskModel> taskList = new ArrayList<>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_TASK_MASTER,
                new String[]{
                        SQLiteHelper.TK_TASK_ID,//1
                        SQLiteHelper.TK_NAME,//1
                        SQLiteHelper.TK_DESC,//2
                        SQLiteHelper.TK_TYPE,//3
                        SQLiteHelper.TK_PRIORITY,//4
                        SQLiteHelper.TK_START_TIME,//5
                        SQLiteHelper.TK_END_TIME,//6
                        SQLiteHelper.TK_ESTIMATE_TIME,//7
                        SQLiteHelper.TK_STATUS,//8
                        SQLiteHelper.TK_ACTIVE,//9
                        SQLiteHelper.TK_BUDGET,//10
                        SQLiteHelper.TK_LAST_MODIFIED_BY,//11
                        SQLiteHelper.TK_LAST_MODIFIED,//12
                        SQLiteHelper.TM_C_CREATEDBYNAME,//13
                        SQLiteHelper.TK_ADDRESSSTR,//14
                        SQLiteHelper.TK_INVOICE_AMT,//15
                        SQLiteHelper.TK_INVOICE_DATE,//16
                        SQLiteHelper.TK_PAST_HISTORY,//17
                        SQLiteHelper.TK_ADVANCE_PAID,//18
                        SQLiteHelper.TK_VENDOR_PREFERENCE,//19
                        SQLiteHelper.TK_DAY_CODES,//20
                        SQLiteHelper.TK_FREQUENCY,//21
                        SQLiteHelper.TK_OUTSTANDING_AMT,//22
                        SQLiteHelper.TK_CUSTOMER_TYPE//23


                }, null, null, null,
                null, null);

        while (cursor.moveToNext()) {
            TaskModel taskModel = new TaskModel();

            taskModel.taskID = cursor.getInt(0);
            taskModel.name = cursor.getString(1);
            taskModel.description = cursor.getString(2);
            taskModel.taskType = cursor.getString(3);
            taskModel.priority = cursor.getString(4);
            taskModel.startTime = cursor.getString(5);
            taskModel.endTime = cursor.getString(6);
            taskModel.estimatedTime = cursor.getString(7);
            taskModel.status = cursor.getString(8);
            taskModel.active = Boolean.parseBoolean(cursor.getString(9));
            taskModel.budget = cursor.getDouble(10);
            taskModel.lastModifiedBy = cursor.getInt(11);
            taskModel.lastModified = cursor.getString(12);
            taskModel.createdByName = cursor.getString(13);
            taskModel.addressStr = cursor.getString(14);
            taskModel.invoiceAmount = cursor.getDouble(15);
            taskModel.invoiceDate = cursor.getString(16);
            taskModel.pastHistory = cursor.getString(17);
            taskModel.advancePaid = cursor.getDouble(18);
            taskModel.vendorPreference = cursor.getString(19);
            taskModel.daycodes = cursor.getString(20);
            taskModel.frequency = cursor.getString(21);
            taskModel.outstandingAmount = cursor.getDouble(22);
            taskModel.customerType = cursor.getString(23);

            taskList.add(taskModel);
        }
        return taskList;
    }


    public TaskModel getSingleTaskFromID(int taskId) {
        TaskModel taskModel = new TaskModel();

        Cursor cursor = database.query(SQLiteHelper.TABLE_TASK_MASTER,
                new String[]{
                        SQLiteHelper.TK_TASK_ID,//1
                        SQLiteHelper.TK_NAME,//1
                        SQLiteHelper.TK_DESC,//2
                        SQLiteHelper.TK_TYPE,//3
                        SQLiteHelper.TK_PRIORITY,//4
                        SQLiteHelper.TK_START_TIME,//5
                        SQLiteHelper.TK_END_TIME,//6
                        SQLiteHelper.TK_ESTIMATE_TIME,//7
                        SQLiteHelper.TK_STATUS,//8
                        SQLiteHelper.TK_ACTIVE,//9
                        SQLiteHelper.TK_BUDGET,//10
                        SQLiteHelper.TK_LAST_MODIFIED_BY,//11
                        SQLiteHelper.TK_LAST_MODIFIED,//12
                        SQLiteHelper.TM_C_CREATEDBYNAME,//13
                        SQLiteHelper.TK_ADDRESSSTR,//14
                        SQLiteHelper.TK_INVOICE_AMT,//15
                        SQLiteHelper.TK_INVOICE_DATE,//16
                        SQLiteHelper.TK_PAST_HISTORY,//17
                        SQLiteHelper.TK_ADVANCE_PAID,//18
                        SQLiteHelper.TK_VENDOR_PREFERENCE,//19
                        SQLiteHelper.TK_DAY_CODES,//20
                        SQLiteHelper.TK_FREQUENCY,//21
                        SQLiteHelper.TK_OUTSTANDING_AMT,//21
                        SQLiteHelper.TK_CUSTOMER_TYPE//22

                }, SQLiteHelper.TK_TASK_ID + "=" + taskId, null, null,
                null, null);

        while (cursor.moveToNext()) {

            taskModel.taskID = cursor.getInt(0);
            taskModel.name = cursor.getString(1);
            taskModel.description = cursor.getString(2);
            taskModel.taskType = cursor.getString(3);
            taskModel.priority = cursor.getString(4);
            taskModel.startTime = cursor.getString(5);
            taskModel.endTime = cursor.getString(6);
            taskModel.estimatedTime = cursor.getString(7);
            taskModel.status = cursor.getString(8);
            taskModel.active = Boolean.parseBoolean(cursor.getString(9));
            taskModel.budget = cursor.getDouble(10);
            taskModel.lastModifiedBy = cursor.getInt(11);
            taskModel.lastModified = cursor.getString(12);
            taskModel.createdByName = cursor.getString(13);
            taskModel.addressStr = cursor.getString(14);
            taskModel.invoiceAmount = cursor.getDouble(15);
            taskModel.invoiceDate = cursor.getString(16);
            taskModel.pastHistory = cursor.getString(17);
            taskModel.advancePaid = cursor.getDouble(18);
            taskModel.vendorPreference = cursor.getString(19);
            taskModel.daycodes = cursor.getString(20);
            taskModel.frequency = cursor.getString(21);
            taskModel.outstandingAmount = cursor.getDouble(22);
            taskModel.customerType = cursor.getString(23);

        }

        return taskModel;
    }


    public int delete(int local_id) {
        return database.delete(SQLiteHelper.TABLE_TASK_MASTER, "id =?",
                new String[]{local_id + ""});
    }
}
