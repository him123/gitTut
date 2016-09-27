package com.bigbang.superteam.task.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.bigbang.superteam.DB.TeamWorksDBDAO;
import com.bigbang.superteam.task.model.TaskStatus;
import com.bigbang.superteam.util.SQLiteHelper;

/**
 * Created by User on 9/5/2016.
 */
public class TaskStatusDAO extends TeamWorksDBDAO {
    public TaskStatusDAO(Context contxt) {
        super(contxt);
    }

    public TaskStatusDAO() {
        super();
    }

    public long save(TaskStatus taskStatus) {

        long status;
        ContentValues values = new ContentValues();

        values.put(SQLiteHelper.TS_TASK_STATUS_ID, taskStatus.taskstatusid);
        values.put(SQLiteHelper.TS_TASK_STATUS, taskStatus.taskstatus);
        values.put(SQLiteHelper.TS_DESCRIPTION, taskStatus.description);
        values.put(SQLiteHelper.TS_LASTMODIFIED, taskStatus.lastmodified);

        status = database.insert(SQLiteHelper.TABLE_TASK_STATUS, null, values);
        if (status != -1)
            Log.d("Insert Task Status", "*********************************************successfull");
        else
            Log.d("Insert Task status", "*********************************************unsuccessfull");

        return status;
    }

    public TaskStatus getTaskStatusData(){
        TaskStatus taskStatus = new TaskStatus();

        Cursor cursor = database.query(SQLiteHelper.TABLE_TASK_MEMBER,
                new String[]{
                        SQLiteHelper.TS_TASK_STATUS_ID,//0
                        SQLiteHelper.TS_TASK_STATUS,//1
                        SQLiteHelper.TS_DESCRIPTION,//2
                        SQLiteHelper.TS_LASTMODIFIED,//3
                }, null, null, null,
                null, null);

        while (cursor.moveToNext()) {
            taskStatus.taskstatusid = cursor.getInt(0);
            taskStatus.taskstatus = cursor.getString(1);
            taskStatus.description = cursor.getString(2);
            taskStatus.lastmodified = cursor.getString(3);
        }
        return taskStatus;
    }

}
