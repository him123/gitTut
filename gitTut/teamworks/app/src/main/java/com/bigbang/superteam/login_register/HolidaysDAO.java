package com.bigbang.superteam.login_register;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.bigbang.superteam.DB.TeamWorksDBDAO;
import com.bigbang.superteam.model.Holidays;
import com.bigbang.superteam.util.SQLiteHelper;

import java.util.ArrayList;

/**
 * Created by User on 10/15/2015.
 */
public class HolidaysDAO extends TeamWorksDBDAO {

    public HolidaysDAO(Context contxt) {
        super(contxt);
    }

    public HolidaysDAO() {
        super();
    }

    public long save(Holidays holidays) {

        long status;
        ContentValues values = new ContentValues();

        values.put(SQLiteHelper.HO_ID, holidays.holidayId);
        values.put(SQLiteHelper.HO_DATE, holidays.holidayDate);
        values.put(SQLiteHelper.HO_NAME, holidays.holidayName);

        status = database.insert(SQLiteHelper.TABLE_HOLIDAYS, null, values);
        if (status != -1)
            Log.d("Insert Status", "*********************************************successfull");
        else
            Log.d("Insert Status", "*********************************************unsuccessfull");

        return status;
    }

    //USING query() method
    public ArrayList<Holidays> getHolidays() {
        ArrayList<Holidays> holidaysArr = new ArrayList<Holidays>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_HOLIDAYS,
                new String[]{
                        SQLiteHelper.HO_LOCAL_ID,
                        SQLiteHelper.HO_ID,
                        SQLiteHelper.HO_DATE,
                        SQLiteHelper.HO_NAME,
                }, null, null, null,
                null, null);

        while (cursor.moveToNext()) {
            Holidays holidays = new Holidays();

            holidays.localId = cursor.getString(0);
            holidays.holidayId = cursor.getString(1);
            holidays.holidayDate = cursor.getString(2).toString().replaceAll("00:00:00","");
            holidays.holidayName = cursor.getString(3);

            holidaysArr.add(holidays);
        }
        return holidaysArr;
    }

    public int delete(int local_id) {
        return database.delete(SQLiteHelper.TABLE_HOLIDAYS, "id =?",
                new String[]{local_id + ""});
    }

    public long update(Holidays holidays) {
        ContentValues values = new ContentValues();

        values.put(SQLiteHelper.HO_ID, holidays.holidayId);
        values.put(SQLiteHelper.HO_DATE, holidays.holidayDate);
        values.put(SQLiteHelper.HO_NAME, holidays.holidayName);

        long result = database.update(SQLiteHelper.TABLE_HOLIDAYS, values, "id =?",
                new String[]{holidays.localId + ""});
        Log.d("Update Result ", "=" + result);
        return result;

    }

    public int deleteAll() {
        Log.d("", "************* All Holidays has been deleted ");
        return database.delete(SQLiteHelper.TABLE_HOLIDAYS, null, null);
    }
}