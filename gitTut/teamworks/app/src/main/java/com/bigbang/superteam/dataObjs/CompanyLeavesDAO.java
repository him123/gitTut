package com.bigbang.superteam.dataObjs;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.bigbang.superteam.DB.TeamWorksDBDAO;
import com.bigbang.superteam.model.CompanyLeaves;
import com.bigbang.superteam.model.Holidays;
import com.bigbang.superteam.util.SQLiteHelper;

import java.util.ArrayList;

/**
 * Created by User on 10/15/2015.
 */
public class CompanyLeavesDAO extends TeamWorksDBDAO {

    public CompanyLeavesDAO(Context contxt) {
        super(contxt);
    }

    public CompanyLeavesDAO() {
        super();
    }

    public long save(CompanyLeaves companyLeaves) {

        long status;
        ContentValues values = new ContentValues();

        values.put(SQLiteHelper.CL_COMP_ID, companyLeaves.companyId);
        values.put(SQLiteHelper.CL_LV_TYPE, companyLeaves.leaveType);
        values.put(SQLiteHelper.CL_NOOF_LV, companyLeaves.noOfLeaves);
        values.put(SQLiteHelper.CL_LV_UPDATE_CY, companyLeaves.leaveUpdateCyle);
        values.put(SQLiteHelper.CL_ACTIVE, companyLeaves.active);
        values.put(SQLiteHelper.CL_MODIFIED_BY, companyLeaves.modifiedBy);
//        values.put(SQLiteHelper.CL_LAST_MODIFIED, companyLeaves.lastModified);

        status = database.insert(SQLiteHelper.TABLE_COMP_LEAVES, null, values);
        if (status != -1)
            Log.d("Insert Status", "*********************************************successfull");
        else
            Log.d("Insert Status", "*********************************************unsuccessfull");

        return status;
    }

    //USING query() method
    public ArrayList<CompanyLeaves> getCompLeaves() {
        ArrayList<CompanyLeaves> companyLeavesArrayList = new ArrayList<CompanyLeaves>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_COMP_LEAVES,
                new String[]{
                        SQLiteHelper.CL_COMP_ID,
                        SQLiteHelper.CL_LV_TYPE,
                        SQLiteHelper.CL_NOOF_LV,
                        SQLiteHelper.CL_LV_UPDATE_CY,
                        SQLiteHelper.CL_MODIFIED_BY,
//                        SQLiteHelper.CL_LAST_MODIFIED,
                }, null, null, null,
                null, null);

        while (cursor.moveToNext()) {
            CompanyLeaves companyLeaves = new CompanyLeaves();

            companyLeaves.companyId = cursor.getString(0);
            companyLeaves.leaveType = cursor.getString(1);
            companyLeaves.noOfLeaves = cursor.getString(2);
            companyLeaves.leaveUpdateCyle = cursor.getString(3);
            companyLeaves.modifiedBy = cursor.getString(4);
//            companyLeaves.lastModified = cursor.getString(5);


            companyLeavesArrayList.add(companyLeaves);
        }
        return companyLeavesArrayList;
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
        return database.delete(SQLiteHelper.TABLE_COMP_LEAVES, null, null);
    }
}