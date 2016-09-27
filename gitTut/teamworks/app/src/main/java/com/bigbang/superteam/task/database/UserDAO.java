package com.bigbang.superteam.task.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.bigbang.superteam.DB.TeamWorksDBDAO;
import com.bigbang.superteam.model.User;
import com.bigbang.superteam.util.SQLiteHelper;

import java.util.ArrayList;

/**
 * Created by User on 9/5/2016.
 */
public class UserDAO extends TeamWorksDBDAO {

    public UserDAO(Context contxt) {
        super(contxt);
    }

    public UserDAO() {
        super();
    }

    public long save(User user) {

        long status;
        ContentValues values = new ContentValues();

        values.put(SQLiteHelper.USER_ID, user.getID());
        values.put(SQLiteHelper.USER_USER_ID, user.getUserID());
        values.put(SQLiteHelper.USER_FIRST_NAME, user.getFirstName());
        values.put(SQLiteHelper.USER_LAST_NAME, user.getLastName());
        values.put(SQLiteHelper.USER_MOBILENO1, user.getMobileNo1());
        values.put(SQLiteHelper.USER_EMAIL_ID, user.getEmailID());
        values.put(SQLiteHelper.USER_PERMENANT_ADDRESS, user.getPermanentAddress().getAddressID());
        values.put(SQLiteHelper.USER_PICTURE, user.getPicture());
        values.put(SQLiteHelper.USER_TEMPORARY_ADDRESS, user.getTemporaryAddress().getAddressID());
        values.put(SQLiteHelper.USER_MOBILENO2, user.getMobileNo2());
        values.put(SQLiteHelper.USER_ROLE_ID, user.getRoleId());
        values.put(SQLiteHelper.USER_ROLE_DESC, user.getRole().getDesc());

        status = database.insert(SQLiteHelper.TABLE_USER, null, values);
        if (status != -1)
            Log.d("Insert Task Type", "*********************************************successfull");
        else
            Log.d("Insert Task Type", "*********************************************unsuccessfull");

        return status;
    }

    public ArrayList<User> getUserList() {
        ArrayList<User> userList = new ArrayList<User>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_USER,
                new String[]{
                        SQLiteHelper.USER_ID,//0
                        SQLiteHelper.USER_USER_ID,//1
                        SQLiteHelper.USER_FIRST_NAME, //2
                        SQLiteHelper.USER_LAST_NAME, //3
                        SQLiteHelper.USER_MOBILENO1, //4
                        SQLiteHelper.USER_EMAIL_ID, //5
                        SQLiteHelper.USER_PERMENANT_ADDRESS, //6
                        SQLiteHelper.USER_PICTURE, //7
                        SQLiteHelper.USER_TEMPORARY_ADDRESS, //8
                        SQLiteHelper.USER_MOBILENO2, //9
                        SQLiteHelper.USER_ROLE_ID, //10
                        SQLiteHelper.USER_ROLE_DESC, //11
                }, null, null, null,
                null, null);

        while (cursor.moveToNext()) {
            User user = new User();

            user.setID(cursor.getInt(0));
            user.setUserID(cursor.getInt(1));
            user.setFirstName(cursor.getString(2));
            user.setLastName(cursor.getString(3));
            user.setMobileNo1(cursor.getString(4));
            user.setEmailID(cursor.getString(5));
            //user.setPermanentAddress(cursor.getString(6));
            user.setPicture(cursor.getString(7));
            //user.setTemporaryAddress(cursor.getString(8));
            user.setMobileNo2(cursor.getString(9));
            user.setRoleId(cursor.getInt(10));
            //user.setRole(cursor.getString(11));

            userList.add(user);
        }
        return userList;
    }

    public ArrayList<User> getUserListByTask(int taskId) {
        ArrayList<User> userList = new ArrayList<User>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_USER,
                new String[]{
                        SQLiteHelper.USER_ID,//0
                        SQLiteHelper.USER_USER_ID,//1
                        SQLiteHelper.USER_FIRST_NAME, //2
                        SQLiteHelper.USER_LAST_NAME, //3
                        SQLiteHelper.USER_MOBILENO1, //4
                        SQLiteHelper.USER_EMAIL_ID, //5
                        SQLiteHelper.USER_PERMENANT_ADDRESS, //6
                        SQLiteHelper.USER_PICTURE, //7
                        SQLiteHelper.USER_TEMPORARY_ADDRESS, //8
                        SQLiteHelper.USER_MOBILENO2, //9
                        SQLiteHelper.USER_ROLE_ID, //10
                        SQLiteHelper.USER_ROLE_DESC, //11
                }, SQLiteHelper.TK_TASK_ID + "=" + taskId, null, null,
                null, null);

        while (cursor.moveToNext()) {
            User user = new User();

            user.setID(cursor.getInt(0));
            user.setUserID(cursor.getInt(1));
            user.setFirstName(cursor.getString(2));
            user.setLastName(cursor.getString(3));
            user.setMobileNo1(cursor.getString(4));
            user.setEmailID(cursor.getString(5));
            //user.setPermanentAddress(cursor.getString(6));
            user.setPicture(cursor.getString(7));
            //user.setTemporaryAddress(cursor.getString(8));
            user.setMobileNo2(cursor.getString(9));
            user.setRoleId(cursor.getInt(10));
            //user.setRole(cursor.getString(11));

            userList.add(user);
        }
        return userList;
    }
}
