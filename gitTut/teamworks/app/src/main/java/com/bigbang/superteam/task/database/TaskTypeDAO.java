package com.bigbang.superteam.task.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.bigbang.superteam.DB.TeamWorksDBDAO;
import com.bigbang.superteam.task.model.TaskType;
import com.bigbang.superteam.util.SQLiteHelper;

import java.util.ArrayList;

/**
 * Created by User on 9/5/2016.
 */
public class TaskTypeDAO extends TeamWorksDBDAO {
    public TaskTypeDAO(Context contxt) {
        super(contxt);
    }

    public TaskTypeDAO() {
        super();
    }

    public long save(TaskType taskType) {

        long status;
        ContentValues values = new ContentValues();

        values.put(SQLiteHelper.TT_TASK_TYPE_ID, taskType.tasktypeid);
        values.put(SQLiteHelper.TT_TASK_TYPE, taskType.tasktype);

        status = database.insert(SQLiteHelper.TABLE_TASK_TYPE, null, values);
        if (status != -1)
            Log.d("Insert Task Type", "*********************************************successfull");
        else
            Log.d("Insert Task Type", "*********************************************unsuccessfull");

        return status;
    }

    public ArrayList<TaskType> getTaskTypeData() {
        ArrayList<TaskType> taskTypeList = new ArrayList<TaskType>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_TASK_MEMBER,
                new String[]{
                        SQLiteHelper.TT_TASK_TYPE_ID,//0
                        SQLiteHelper.TT_TASK_TYPE,//1
                }, null, null, null,
                null, null);

        while (cursor.moveToNext()) {
            TaskType taskType = new TaskType();

            taskType.tasktypeid = cursor.getString(0);
            taskType.tasktype = cursor.getString(1);

            taskTypeList.add(taskType);
        }
        return taskTypeList;
    }
}
