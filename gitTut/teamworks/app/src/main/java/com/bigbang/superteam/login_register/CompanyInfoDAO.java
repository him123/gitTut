package com.bigbang.superteam.login_register;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import com.bigbang.superteam.DB.TeamWorksDBDAO;
import com.bigbang.superteam.dataObjs.CompanyInfo;
import com.bigbang.superteam.model.Company_info;
import com.bigbang.superteam.util.SQLiteHelper;

/**
 * Created by User on 10/15/2015.
 */
public class CompanyInfoDAO extends TeamWorksDBDAO {

    //    MyProfile myProfile;
    CompanyInfo companyInfo;

//    private static final String WHERE_ID_EQUALS = DatabaseHelper.KEY_LOCAL_ID
//            + " =?";
//
//    private static final String WHERE_SELECTED_LOCATION_SERVER_ID_EQUALS = DatabaseHelper.SELECTED_LOCATION_SERVER_ID
//            + " =?";
//    private static final String WHERE_SELECTED_LOCATION_LOCAL_ID_EQUALS = DatabaseHelper.SELECTED_LOCATION_LOCAL_ID
//            + " =?";

    public CompanyInfoDAO(Context contxt) {
        super(contxt);
    }

    public CompanyInfoDAO() {
        super();
    }

    public long save(Company_info company_info) {

        long status;
        ContentValues values = new ContentValues();

        Log.d("", "======= Check owner name : " + company_info.name);

        values.put(SQLiteHelper.CI_COMP_ID, company_info.companyid);
        values.put(SQLiteHelper.CI_DDCSN, company_info.deducution);
        values.put(SQLiteHelper.CI_DDSN1, company_info.deducutionHour1);
        values.put(SQLiteHelper.CI_DDSN2, company_info.deducutionHour2);
        values.put(SQLiteHelper.CI_DDSN3, company_info.deducutionHour3);
        values.put(SQLiteHelper.CI_DDSN4, company_info.deducutionHour4);
        values.put(SQLiteHelper.CI_DDSN4ON, company_info.deducutionHour4On);
        values.put(SQLiteHelper.CI_UID, company_info.userid);
        values.put(SQLiteHelper.CI_BSAL, company_info.basicSalary);
        values.put(SQLiteHelper.CI_HRA, company_info.hra);
        values.put(SQLiteHelper.CI_CONVEY, company_info.conveyance);
        values.put(SQLiteHelper.CI_MEDI, company_info.medical);
        values.put(SQLiteHelper.CI_TELE, company_info.telephone);
        values.put(SQLiteHelper.CI_ITA, company_info.lta);
        values.put(SQLiteHelper.CI_SPINCENTIVE, company_info.specialIncentive);
        values.put(SQLiteHelper.CI_OTHALLWNC, company_info.otherAllownace);
        values.put(SQLiteHelper.CI_PFEMPEE, company_info.pfEmployee);
        values.put(SQLiteHelper.CI_PFEMPER, company_info.pfEmployer);
        values.put(SQLiteHelper.CI_PROTAX, company_info.profTax);
        values.put(SQLiteHelper.CI_TDS, company_info.tds);
        values.put(SQLiteHelper.CI_OTHERDED, company_info.otherDeduction);
        values.put(SQLiteHelper.CI_NOTILEVEL, company_info.notificationLevel);
        values.put(SQLiteHelper.CI_NAME, company_info.name);
        values.put(SQLiteHelper.CI_WORKDAYS, company_info.workingDays);
        values.put(SQLiteHelper.CI_STIME, company_info.startTime);
        values.put(SQLiteHelper.CI_ETIME, company_info.endTime);
        values.put(SQLiteHelper.CI_PROLLCYCLE, company_info.payrollCycle);
        values.put(SQLiteHelper.CI_PROLLST, company_info.payrollStart);
        values.put(SQLiteHelper.CI_PROLLEND, company_info.payrollEnd);
        values.put(SQLiteHelper.CI_SALBRTYPE, company_info.salaryBreakupType);
        values.put(SQLiteHelper.CI_ISPROLLENABLED, company_info.isPayrollEnabled);
        values.put(SQLiteHelper.CI_ISREGUALLOWED, company_info.isRegularizationAllowed);
        values.put(SQLiteHelper.CI_ISLATEDDCSNON, company_info.isLateDeductionOn);
        values.put(SQLiteHelper.CI_AUTOLIVUPDATE, company_info.autoLeaveUpdate);

        Log.d("Insert Status", "*********************************************BEFORE");
        status = database.insert(SQLiteHelper.TABLE_COMP_INFO, null, values);
        if (status != -1)
            Log.d("Insert Status", "*********************************************successfull");
        else
            Log.d("Insert Status", "*********************************************unsuccessfull");

        return status;
    }

//    public long updateLiveID(int id, int local_id) {
//        ContentValues values = new ContentValues();
//
//        values.put(DatabaseHelper.KEY_SERVER_ID, id);
//        values.put(DatabaseHelper.IS_UPDATE, 0);
//
//        long result = database.update(DatabaseHelper.TABLE_MY_PROFILE, values, WHERE_ID_EQUALS,
//                new String[]{local_id + ""});
//        Log.d("Update Result ", "=" + result);
//        return result;
//    }
//
//    public long updateLocationLocalIDFromServerID(int location_local_id) {
//        ContentValues values = new ContentValues();
//
//        values.put(DatabaseHelper.SELECTED_LOCATION_LOCAL_ID, location_local_id);
//
//        long result = database.update(DatabaseHelper.TABLE_MY_PROFILE, values, WHERE_ID_EQUALS,
//                new String[]{"1"});
//        Log.d("Update Result ", "=" + result);
//        return result;
//    }
//
//    public long updateSelectedLocationLocalIDFromServerId(int local_id, int server_id) {
//        ContentValues values = new ContentValues();
//
//        values.put(DatabaseHelper.SELECTED_LOCATION_SERVER_ID, server_id);
//
//        long result = database.update(DatabaseHelper.TABLE_MY_PROFILE, values, WHERE_SELECTED_LOCATION_LOCAL_ID_EQUALS,
//                new String[]{local_id + ""});
//        Log.d("Update Result ", "=" + result);
//        return result;
//    }
//
//
//    public long update(MyProfile myProfile) {
//        ContentValues values = new ContentValues();
//
//        values.put(DatabaseHelper.OWNER_NAME, myProfile.owner_name);
//        values.put(DatabaseHelper.SELECTED_LOCATION_LOCAL_ID, myProfile.selected_location_local);
//        values.put(DatabaseHelper.SELECTED_LOCATION_SERVER_ID, myProfile.selected_location_server);
//        values.put(DatabaseHelper.PASSWORD, myProfile.pwd);
//        values.put(DatabaseHelper.IS_UPDATE, myProfile.is_update);
//
//        long result = database.update(DatabaseHelper.TABLE_MY_PROFILE, values, WHERE_ID_EQUALS,
//                new String[]{myProfile.local_id + ""});
//        Log.d("Update Result ", "=" + result);
//        return result;
//    }
//
//    public int getSelectedLocationServerID() {
//        int server_id = 0;
//
//        Cursor cursor = database.query(DatabaseHelper.TABLE_MY_PROFILE, new String[]{
//                        DatabaseHelper.SELECTED_LOCATION_SERVER_ID
//                },
//                null, null, null, null, null);
//
//        if (cursor.moveToNext()) {
//            server_id = cursor.getInt(0);
//        }
//        return server_id;
//    }
//
//    //Getting local location selected id
//    public int getSelectedLocationLocalID() {
//        int local_id = 0;
//
//        Cursor cursor = database.query(DatabaseHelper.TABLE_MY_PROFILE, new String[]{
//                        DatabaseHelper.SELECTED_LOCATION_LOCAL_ID
//                },
//                null, null, null, null, null);
//
//        if (cursor.moveToNext()) {
//            local_id = cursor.getInt(0);
//        }
//        return local_id;
//    }
//
//    public MyProfile getMyProfile() {
//
//        myProfile = new MyProfile();
//        Cursor cursor = database.query(DatabaseHelper.TABLE_MY_PROFILE, new String[]{
//                        DatabaseHelper.KEY_LOCAL_ID,
//                        DatabaseHelper.KEY_SERVER_ID,
//                        DatabaseHelper.OWNER_NAME,
//                        DatabaseHelper.SELECTED_LOCATION_LOCAL_ID,
//                        DatabaseHelper.SELECTED_LOCATION_SERVER_ID,
//                        DatabaseHelper.PASSWORD,
//                        DatabaseHelper.IS_UPDATE
//                },
//                null, null, null, null, null);
//
//        if (cursor.moveToNext()) {
//            myProfile.local_id = cursor.getInt(0);
//            myProfile.server_id = cursor.getInt(1);
//            myProfile.owner_name = cursor.getString(2);
//            myProfile.selected_location_local = cursor.getInt(3);
//            myProfile.selected_location_server = cursor.getInt(4);
//            myProfile.pwd = cursor.getString(5);
//            myProfile.is_update = cursor.getInt(6);
//        }
//
//        return myProfile;
//    }
//
//    public MyProfile getProfilePendingToSync() {
//        myProfile = new MyProfile();
//        Cursor cursor = database.query(DatabaseHelper.TABLE_MY_PROFILE, new String[]{
//                        DatabaseHelper.KEY_LOCAL_ID,
//                        DatabaseHelper.KEY_SERVER_ID,
//                        DatabaseHelper.OWNER_NAME,
//                        DatabaseHelper.SELECTED_LOCATION_LOCAL_ID,
//                        DatabaseHelper.SELECTED_LOCATION_SERVER_ID,
//                        DatabaseHelper.PASSWORD,
//                        DatabaseHelper.IS_UPDATE
//                },
//                "is_update=" + 1, null, null, null, null);
//
//        if (cursor.moveToNext()) {
//            myProfile.local_id = cursor.getInt(0);
//            myProfile.server_id = cursor.getInt(1);
//            myProfile.owner_name = cursor.getString(2);
//            myProfile.selected_location_local = cursor.getInt(3);
//            myProfile.selected_location_server = cursor.getInt(4);
//            myProfile.pwd = cursor.getString(5);
//            myProfile.is_update = cursor.getInt(6);
//        }
//
//        return myProfile;
//    }
//
//    public MyProfile getProfileSelectedLocationServerID() {
//        myProfile = new MyProfile();
//        Cursor cursor = database.query(DatabaseHelper.TABLE_MY_PROFILE, new String[]{
//                        DatabaseHelper.KEY_SERVER_ID,
//                },
//                "is_update=" + 1, null, null, null, null);
//
//        if (cursor.moveToNext()) {
//            myProfile.server_id = cursor.getInt(0);
//        }
//
//        return myProfile;
//    }
}
