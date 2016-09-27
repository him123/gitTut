package com.bigbang.superteam.task.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.bigbang.superteam.DB.TeamWorksDBDAO;
import com.bigbang.superteam.task.model.Expense;
import com.bigbang.superteam.task.model.TaskChat;
import com.bigbang.superteam.task.model.TaskModel;
import com.bigbang.superteam.util.SQLiteHelper;

import java.util.ArrayList;

/**
 * Created by User on 9/3/2016.
 */
public class ExpenceDAO extends TeamWorksDBDAO {

    public ExpenceDAO(Context contxt) {
        super(contxt);
    }

    public ExpenceDAO() {
        super();
    }

    public long save(Expense expense) {

        long status;
        ContentValues values = new ContentValues();

        values.put(SQLiteHelper.TS_EX_ID, expense.expenseId);
        values.put(SQLiteHelper.TASK_ID, expense.taskID);
        values.put(SQLiteHelper.TS_TASK_CHAT_ID, expense.taskChatID);
        values.put(SQLiteHelper.TS_EX_AMOUNT, expense.expenseAmount);
        values.put(SQLiteHelper.TS_LASTMODIFIED_BY, expense.lastModifiedBy);

        status = database.insert(SQLiteHelper.TABLE_EXPENSE, null, values);
        if (status != -1)
            Log.d("Insert Task Master", "*********************************************successfull");
        else
            Log.d("Insert Task Master", "*********************************************unsuccessfull");

        return status;
    }

    public ArrayList<TaskModel> getTaskData() {
        ArrayList<TaskModel> taskList = new ArrayList<>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_TASK_MASTER,
                new String[]{
//                        SQLiteHelper.TK_ID,//0
                        SQLiteHelper.TK_TASK_ID,//1
//                        SQLiteHelper.TK_COMPANY_ID,//2
                        SQLiteHelper.TK_NAME,//3
                        SQLiteHelper.TK_DESC,//4
                        SQLiteHelper.TK_TYPE,//5
//                        SQLiteHelper.TK_LOCATION,//6
//                        SQLiteHelper.TK_LATITUDE,//7
//                        SQLiteHelper.TK_LONGITUDE,//8
//                        SQLiteHelper.TK_PRIORITY,//9
//                        SQLiteHelper.TK_START_TIME,//10
                        SQLiteHelper.TK_END_TIME,//11
//                        SQLiteHelper.TK_ESTIMATE_TIME,//12
//                        SQLiteHelper.TK_CREATED_BY,//13
                        SQLiteHelper.TK_STATUS,//14
                        SQLiteHelper.TK_PRIORITY
//                        SQLiteHelper.TK_ACTIVE,//15
//                        SQLiteHelper.TK_BUDGET,//16
//                        SQLiteHelper.TK_APPROVED_ON,//17
//                        SQLiteHelper.TK_APPROVED_BY,//18
//                        SQLiteHelper.TK_APPROVAL_NOTE,//19
//                        SQLiteHelper.TK_LAST_MODIFIED_BY,//20
//                        SQLiteHelper.TK_LAST_MODIFIED,//21
//                        SQLiteHelper.TK_CUST_VENDOR_ID,//22
//                        SQLiteHelper.TK_CUST_VENDOR_NAME,//23
//                        SQLiteHelper.TK_CUST_VENDOR_CONTACT,//24
//                        SQLiteHelper.TK_INVOICE_DATE,//25
//                        SQLiteHelper.TK_DUE_DATE,//26
//                        SQLiteHelper.TK_INVOICE_AMT,//27
//                        SQLiteHelper.TK_OUTSTANDING_AMT,//28
//                        SQLiteHelper.TK_PROJECT,//29
//                        SQLiteHelper.TK_FREQUENCY,//30
//                        SQLiteHelper.TK_DAY_CODES,//31
//                        SQLiteHelper.TK_CUSTOMER_TYPE,//32
//                        SQLiteHelper.TK_PAST_HISTORY,//33
//                        SQLiteHelper.TK_ADVANCE_PAID,//34
//                        SQLiteHelper.TK_VENDOR_PREFERENCE//35

                }, null, null, null,
                null, null);

        while (cursor.moveToNext()) {
            TaskModel taskModel = new TaskModel();

//            taskModel.id = cursor.getInt(0);
            taskModel.taskID = cursor.getInt(0);
//            taskModel.companyID = cursor.getInt(2);
            taskModel.name = cursor.getString(1);
            taskModel.description = cursor.getString(2);
            taskModel.taskType = cursor.getString(3);
//            taskModel.location = cursor.getString(6);
//            taskModel.latitude = cursor.getDouble(7);
//            taskModel.longitude = cursor.getDouble(8);
//            taskModel.priority = cursor.getString(9);
//            taskModel.startTime = cursor.getString(10);
            taskModel.endTime = cursor.getString(4);
//            taskModel.estimatedTime = cursor.getString(12);

            //get User Deatils --- database stores user id ----- get data from userid
            //taskModel.createdBy = cursor.getString(13);

            taskModel.status = cursor.getString(5);
            taskModel.priority = cursor.getString(6);
//            taskModel.active = Boolean.parseBoolean(cursor.getString(15));
//            taskModel.budget = cursor.getDouble(16);
//            taskModel.approvedOn = cursor.getString(17);

            //get User Deatils --- database stores user id ----- get data from userid
            //taskModel.approvedBy = cursor.getString(18);

//            taskModel.approvalNote = cursor.getString(19);
//            taskModel.lastModifiedBy = cursor.getInt(20);
//            taskModel.lastModified = cursor.getString(21);
//            taskModel.custVendID = cursor.getInt(22);
//            taskModel.custVendName = cursor.getString(23);
//            taskModel.custVendContact = cursor.getString(24);
//            taskModel.invoiceDate = cursor.getString(25);
//            taskModel.dueDate = cursor.getString(26);
//            taskModel.invoiceAmount = cursor.getDouble(27);
//            taskModel.outstandingAmount = cursor.getDouble(28);
//
//            //get Project Deatils --- database stores project id ----- get data from projectid
//            //taskModel.project = cursor.getString(29);
//
//            taskModel.frequency = cursor.getString(30);
//            taskModel.daycodes = cursor.getString(31);
//            taskModel.customerType = cursor.getString(32);
//            taskModel.pastHistory = cursor.getString(33);
//            taskModel.advancePaid = cursor.getDouble(34);
//            taskModel.vendorPreference = cursor.getString(35);

            taskList.add(taskModel);
        }
        return taskList;
    }

    public TaskModel getSingleTaskFromID(int taskId) {
        TaskModel taskModel = new TaskModel();

        Cursor cursor = database.query(SQLiteHelper.TABLE_TASK_MASTER,
                new String[]{
//                        SQLiteHelper.TK_ID,//0
                        SQLiteHelper.TK_TASK_ID,//1
//                        SQLiteHelper.TK_COMPANY_ID,//2
                        SQLiteHelper.TK_NAME,//3
                        SQLiteHelper.TK_DESC,//4
                        SQLiteHelper.TK_TYPE,//5
//                        SQLiteHelper.TK_LOCATION,//6
//                        SQLiteHelper.TK_LATITUDE,//7
//                        SQLiteHelper.TK_LONGITUDE,//8
//                        SQLiteHelper.TK_PRIORITY,//9
//                        SQLiteHelper.TK_START_TIME,//10
                        SQLiteHelper.TK_END_TIME,//11
//                        SQLiteHelper.TK_ESTIMATE_TIME,//12
//                        SQLiteHelper.TK_CREATED_BY,//13
                        SQLiteHelper.TK_STATUS,//14
                        SQLiteHelper.TK_PRIORITY
//                        SQLiteHelper.TK_ACTIVE,//15
//                        SQLiteHelper.TK_BUDGET,//16
//                        SQLiteHelper.TK_APPROVED_ON,//17
//                        SQLiteHelper.TK_APPROVED_BY,//18
//                        SQLiteHelper.TK_APPROVAL_NOTE,//19
//                        SQLiteHelper.TK_LAST_MODIFIED_BY,//20
//                        SQLiteHelper.TK_LAST_MODIFIED,//21
//                        SQLiteHelper.TK_CUST_VENDOR_ID,//22
//                        SQLiteHelper.TK_CUST_VENDOR_NAME,//23
//                        SQLiteHelper.TK_CUST_VENDOR_CONTACT,//24
//                        SQLiteHelper.TK_INVOICE_DATE,//25
//                        SQLiteHelper.TK_DUE_DATE,//26
//                        SQLiteHelper.TK_INVOICE_AMT,//27
//                        SQLiteHelper.TK_OUTSTANDING_AMT,//28
//                        SQLiteHelper.TK_PROJECT,//29
//                        SQLiteHelper.TK_FREQUENCY,//30
//                        SQLiteHelper.TK_DAY_CODES,//31
//                        SQLiteHelper.TK_CUSTOMER_TYPE,//32
//                        SQLiteHelper.TK_PAST_HISTORY,//33
//                        SQLiteHelper.TK_ADVANCE_PAID,//34
//                        SQLiteHelper.TK_VENDOR_PREFERENCE//35

                }, SQLiteHelper.TK_TASK_ID + "=" + taskId, null, null,
                null, null);

        while (cursor.moveToNext()) {

//            taskModel.id = cursor.getInt(0);
            taskModel.taskID = cursor.getInt(0);
//            taskModel.companyID = cursor.getInt(2);
            taskModel.name = cursor.getString(1);
            taskModel.description = cursor.getString(2);
            taskModel.taskType = cursor.getString(3);
//            taskModel.location = cursor.getString(6);
//            taskModel.latitude = cursor.getDouble(7);
//            taskModel.longitude = cursor.getDouble(8);
//            taskModel.priority = cursor.getString(9);
//            taskModel.startTime = cursor.getString(10);
            taskModel.endTime = cursor.getString(4);
//            taskModel.estimatedTime = cursor.getString(12);

            //get User Deatils --- database stores user id ----- get data from userid
            //taskModel.createdBy = cursor.getString(13);

            taskModel.status = cursor.getString(5);
            taskModel.priority = cursor.getString(6);
//            taskModel.active = Boolean.parseBoolean(cursor.getString(15));
//            taskModel.budget = cursor.getDouble(16);
//            taskModel.approvedOn = cursor.getString(17);

            //get User Deatils --- database stores user id ----- get data from userid
            //taskModel.approvedBy = cursor.getString(18);

//            taskModel.approvalNote = cursor.getString(19);
//            taskModel.lastModifiedBy = cursor.getInt(20);
//            taskModel.lastModified = cursor.getString(21);
//            taskModel.custVendID = cursor.getInt(22);
//            taskModel.custVendName = cursor.getString(23);
//            taskModel.custVendContact = cursor.getString(24);
//            taskModel.invoiceDate = cursor.getString(25);
//            taskModel.dueDate = cursor.getString(26);
//            taskModel.invoiceAmount = cursor.getDouble(27);
//            taskModel.outstandingAmount = cursor.getDouble(28);
//
//            //get Project Deatils --- database stores project id ----- get data from projectid
//            //taskModel.project = cursor.getString(29);
//
//            taskModel.frequency = cursor.getString(30);
//            taskModel.daycodes = cursor.getString(31);
//            taskModel.customerType = cursor.getString(32);
//            taskModel.pastHistory = cursor.getString(33);
//            taskModel.advancePaid = cursor.getDouble(34);
//            taskModel.vendorPreference = cursor.getString(35);

//            taskList.add(taskModel);
        }

        return taskModel;
    }


    public ArrayList<TaskModel> getTaskData(String priority) {
        ArrayList<TaskModel> taskList = new ArrayList<>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_TASK_MASTER,
                new String[]{
//                        SQLiteHelper.TK_ID,//0
                        SQLiteHelper.TK_TASK_ID,//1
//                        SQLiteHelper.TK_COMPANY_ID,//2
                        SQLiteHelper.TK_NAME,//3
                        SQLiteHelper.TK_DESC,//4
//                        SQLiteHelper.TK_TYPE,//5
//                        SQLiteHelper.TK_LOCATION,//6
//                        SQLiteHelper.TK_LATITUDE,//7
//                        SQLiteHelper.TK_LONGITUDE,//8
//                        SQLiteHelper.TK_PRIORITY,//9
//                        SQLiteHelper.TK_START_TIME,//10
//                        SQLiteHelper.TK_END_TIME,//11
//                        SQLiteHelper.TK_ESTIMATE_TIME,//12
//                        SQLiteHelper.TK_CREATED_BY,//13
                        SQLiteHelper.TK_STATUS,//14
//                        SQLiteHelper.TK_ACTIVE,//15
//                        SQLiteHelper.TK_BUDGET,//16
//                        SQLiteHelper.TK_APPROVED_ON,//17
//                        SQLiteHelper.TK_APPROVED_BY,//18
//                        SQLiteHelper.TK_APPROVAL_NOTE,//19
//                        SQLiteHelper.TK_LAST_MODIFIED_BY,//20
//                        SQLiteHelper.TK_LAST_MODIFIED,//21
//                        SQLiteHelper.TK_CUST_VENDOR_ID,//22
//                        SQLiteHelper.TK_CUST_VENDOR_NAME,//23
//                        SQLiteHelper.TK_CUST_VENDOR_CONTACT,//24
//                        SQLiteHelper.TK_INVOICE_DATE,//25
//                        SQLiteHelper.TK_DUE_DATE,//26
//                        SQLiteHelper.TK_INVOICE_AMT,//27
//                        SQLiteHelper.TK_OUTSTANDING_AMT,//28
//                        SQLiteHelper.TK_PROJECT,//29
//                        SQLiteHelper.TK_FREQUENCY,//30
//                        SQLiteHelper.TK_DAY_CODES,//31
//                        SQLiteHelper.TK_CUSTOMER_TYPE,//32
//                        SQLiteHelper.TK_PAST_HISTORY,//33
//                        SQLiteHelper.TK_ADVANCE_PAID,//34
//                        SQLiteHelper.TK_VENDOR_PREFERENCE//35
                }, SQLiteHelper.TK_PRIORITY + "=" + "'" + priority + "'", null, null,
                null, null);

        while (cursor.moveToNext()) {
            TaskModel taskModel = new TaskModel();

//            taskModel.id = cursor.getInt(0);
            taskModel.taskID = cursor.getInt(0);
//            taskModel.companyID = cursor.getInt(2);
            taskModel.name = cursor.getString(1);
            taskModel.description = cursor.getString(2);
//            taskModel.taskType = cursor.getString(5);
//            taskModel.location = cursor.getString(6);
//            taskModel.latitude = cursor.getDouble(7);
//            taskModel.longitude = cursor.getDouble(8);
//            taskModel.priority = cursor.getString(9);
//            taskModel.startTime = cursor.getString(10);
//            taskModel.endTime = cursor.getString(11);
//            taskModel.estimatedTime = cursor.getString(12);

            //get User Deatils --- database stores user id ----- get data from userid
            //taskModel.createdBy = cursor.getString(13);

            taskModel.status = cursor.getString(3);
//            taskModel.active = Boolean.parseBoolean(cursor.getString(15));
//            taskModel.budget = cursor.getDouble(16);
//            taskModel.approvedOn = cursor.getString(17);

            //get User Deatils --- database stores user id ----- get data from userid
            //taskModel.approvedBy = cursor.getString(18);

//            taskModel.approvalNote = cursor.getString(19);
//            taskModel.lastModifiedBy = cursor.getInt(20);
//            taskModel.lastModified = cursor.getString(21);
//            taskModel.custVendID = cursor.getInt(22);
//            taskModel.custVendName = cursor.getString(23);
//            taskModel.custVendContact = cursor.getString(24);
//            taskModel.invoiceDate = cursor.getString(25);
//            taskModel.dueDate = cursor.getString(26);
//            taskModel.invoiceAmount = cursor.getDouble(27);
//            taskModel.outstandingAmount = cursor.getDouble(28);
//
//            //get Project Deatils --- database stores project id ----- get data from projectid
//            //taskModel.project = cursor.getString(29);
//
//            taskModel.frequency = cursor.getString(30);
//            taskModel.daycodes = cursor.getString(31);
//            taskModel.customerType = cursor.getString(32);
//            taskModel.pastHistory = cursor.getString(33);
//            taskModel.advancePaid = cursor.getDouble(34);
//            taskModel.vendorPreference = cursor.getString(35);

            taskList.add(taskModel);
        }
        return taskList;
    }

    public int delete(int local_id) {
        return database.delete(SQLiteHelper.TABLE_TASK_MASTER, "id =?",
                new String[]{local_id + ""});
    }
}
