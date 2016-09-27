package com.bigbang.superteam.task.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.bigbang.superteam.DB.TeamWorksDBDAO;
import com.bigbang.superteam.model.User;
import com.bigbang.superteam.task.model.TaskMember;
import com.bigbang.superteam.util.SQLiteHelper;

import java.util.ArrayList;

/**
 * Created by User on 9/3/2016.
 */
public class TaskMemberDAO extends TeamWorksDBDAO {

    public TaskMemberDAO(Context contxt) {
        super(contxt);
    }

    public TaskMemberDAO() {
        super();
    }

    public long save(TaskMember taskMember) {

        long status;
        ContentValues values = new ContentValues();

        values.put(SQLiteHelper.TM_TASKID, taskMember.TaskID);
        values.put(SQLiteHelper.TM_USERID, taskMember.userID);
        values.put(SQLiteHelper.TM_USERNAME, taskMember.userName);
        values.put(SQLiteHelper.TM_MEMBER_TYPE, taskMember.memberType);
        values.put(SQLiteHelper.CL_ACTIVE, taskMember.active);
        values.put(SQLiteHelper.TM_CONTACT_NUM, taskMember.contact_num);

        status = database.insert(SQLiteHelper.TABLE_TASK_MEMBER, null, values);
        if (status != -1)
            Log.d("Insert Task Member", "*********************************************successfull");
        else
            Log.d("Insert Task Member", "*********************************************unsuccessfull");

        return status;
    }

    public TaskMember getTaskMemberData() {
        TaskMember taskMember = new TaskMember();

        Cursor cursor = database.query(SQLiteHelper.TABLE_TASK_MEMBER,
                new String[]{
                        SQLiteHelper.TM_ID,//0
                        SQLiteHelper.TM_USERID,//1
                        SQLiteHelper.TM_TASKID,//2
                        SQLiteHelper.TM_MEMBER_TYPE,//3
                        SQLiteHelper.TM_USERNAME,//3
                        SQLiteHelper.TM_TASK_RIGHT,//4
                        SQLiteHelper.TM_ACTIVE,//5
                }, null, null, null,
                null, null);

        while (cursor.moveToNext()) {
            taskMember.id = cursor.getInt(0);
            taskMember.userID = cursor.getInt(1);
            taskMember.TaskID = cursor.getInt(2);
            taskMember.memberType = cursor.getString(3);
            taskMember.userName = cursor.getString(4);
            taskMember.taskRights = cursor.getString(5);
            taskMember.active = Boolean.parseBoolean(cursor.getString(6));
        }
        return taskMember;
    }

    public ArrayList<TaskMember> getTaskMemberDataByTaskId(int taskId) {
        ArrayList<TaskMember> userList = new ArrayList<TaskMember>();


        Cursor cursor = database.query(SQLiteHelper.TABLE_TASK_MEMBER,
                new String[]{
                        SQLiteHelper.TM_ID,//0
                        SQLiteHelper.TM_USERID,//1
                        SQLiteHelper.TM_TASKID,//2
                        SQLiteHelper.TM_MEMBER_TYPE,//3
                        SQLiteHelper.TM_TASK_RIGHT,//4
                        SQLiteHelper.TM_ACTIVE,//5
                }, SQLiteHelper.TK_TASK_ID + "=" + taskId, null, null,
                null, null);

        while (cursor.moveToNext()) {
            TaskMember taskMember = new TaskMember();

            taskMember.id = cursor.getInt(0);
            taskMember.userID = cursor.getInt(1);
            taskMember.TaskID = cursor.getInt(2);
            taskMember.memberType = cursor.getString(3);
            taskMember.taskRights = cursor.getString(4);
            taskMember.active = Boolean.parseBoolean(cursor.getString(5));

            userList.add(taskMember);
        }
        return userList;
    }

    public ArrayList<TaskMember> getTaskMemberDataByTaskIdAndMemberType(int taskId, String memberType) {
        ArrayList<TaskMember> userList = new ArrayList<TaskMember>();


        Cursor cursor = database.query(SQLiteHelper.TABLE_TASK_MEMBER,
                new String[]{
                        SQLiteHelper.TM_ID,//0
                        SQLiteHelper.TM_USERID,//1
                        SQLiteHelper.TM_USERNAME,//2
                        SQLiteHelper.TM_CONTACT_NUM//2

                }, SQLiteHelper.TK_TASK_ID + "=" + taskId + " AND " + SQLiteHelper.TM_MEMBER_TYPE + "=" + "'" + memberType + "'", null, null,
                null, null);

        while (cursor.moveToNext()) {
            TaskMember taskMember = new TaskMember();

            taskMember.id = cursor.getInt(0);
            taskMember.userID = cursor.getInt(1);
            taskMember.userName = cursor.getString(2);
            taskMember.contact_num = cursor.getString(3);

            userList.add(taskMember);
        }
        return userList;
    }


    public int delete(int local_id) {
        return database.delete(SQLiteHelper.TABLE_TASK_MEMBER, "id =?",
                new String[]{local_id + ""});
    }
}
