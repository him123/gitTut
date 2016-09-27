package com.bigbang.superteam;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.bigbang.superteam.admin.AdminDashboardNewActivity;
import com.bigbang.superteam.leave_attendance.AttendanceDetailsActivity;
import com.bigbang.superteam.manager.ManagerDashboardNewActivity;
import com.bigbang.superteam.user.UserDashboardNewActivity;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.GPSTracker;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by USER 7 on 6/6/2015.
 */
public class Leave_Attendance_GCM {

    static SQLiteHelper helper;
    public static SQLiteDatabase db = null;
    static String TAG = "Leave_Attendance_GCM";
    double curLat, curLng;

    public static void insertIntoLeaveApprovals(JSONObject jsonObject, String type, Context context, String gcmMessage, boolean notificationFlag) {
        helper = new SQLiteHelper(context, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        try {
            String data = jsonObject.optString("data");
            JSONObject jsonObj = new JSONObject(data);

            ContentValues values = new ContentValues();
            values.put("userid", "" + jsonObj.optInt("ID"));
            values.put("title", "" + jsonObj.optString("firstName") + " " + jsonObj.optString("lastName"));
            values.put("description", "" + jsonObj.optString("LeaveType"));
            values.put("type", "" + type);
            values.put("img", "" + jsonObj.optString("picture"));
            values.put("data", "" + jsonObj.toString());
            values.put("Status", "" + jsonObject.optInt("Status"));
            values.put("TransactionId", "" + jsonObject.optString("TransactionID"));
            values.put("Time", "" + jsonObject.optString("LastModified"));

            String TransactionID = jsonObject.optString("TransactionID");
            int type1 = jsonObject.optInt("Type");

            Cursor cursor = null;
            boolean flag = true;
            try {
                if (TransactionID.length() > 0) {
                    cursor = db.rawQuery("select * from " + Constant.ApprovalsTable + " where TransactionID like '" + TransactionID + "' AND type like '" + type1 + "'", null);
                    if (cursor != null && cursor.getCount() > 0) flag = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) cursor.close();
            }

            if (flag) {
                db.insert(Constant.ApprovalsTable, null, values);
            } else {
                db.update(Constant.ApprovalsTable, values, "TransactionID like '" + TransactionID + "'", null);
            }

            if (jsonObject.optInt("Status") != 1) notificationFlag = false;

            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (notificationFlag) {
            String roleId = Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID);
            if (Arrays.asList("1", "2").contains(roleId)) {
                Util.generateNotificationFragment(context, gcmMessage, AdminDashboardNewActivity.class, 3);
            } else if (Arrays.asList("3").contains(roleId)) {
                Util.generateNotificationFragment(context, gcmMessage, ManagerDashboardNewActivity.class, 3);
            } else if (Arrays.asList("4").contains(roleId)) {
                Util.generateNotificationFragment(context, gcmMessage, UserDashboardNewActivity.class, 3);
            }
        }

    }

    public static void removeFromDataBase(String approvalID, Context context, JSONObject jsonObject) {
        helper = new SQLiteHelper(context, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();
        Log.v(TAG, "***** Inside removeFromDataBase");
        String whereClause = "id" + "=?";
        String[] whereArgs = new String[]{"" + approvalID};

        int isDelete = db.delete(Constant.ApprovalsTable, whereClause, whereArgs);
        Log.e(TAG, "***** Inside removeFromDataBase and is de;et value is:- " + isDelete);
        if (!(isDelete == 1)) {
            try {
                if ((jsonObject.getInt("Type")) == Constant.LEAVE_WITHDRAW_TO_ADMIN) {
                    Log.v(TAG, "***** Inside (jsonObject.getInt) == Constant.LEAVE_WITHDRAW_TO_ADMIN)");
                    LeaveWithDrawn_TO_ADMIN(context, jsonObject, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void removeUsingTransactionId(String approvalID, Context context, JSONObject jsonObject) {
        helper = new SQLiteHelper(context, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();
        Log.v(TAG, "***** Inside removeFromDataBase");
        String whereClause = "TransactionID" + "=?";
        String[] whereArgs = new String[]{"" + approvalID};

        int isDelete = db.delete(Constant.ApprovalsTable, whereClause, whereArgs);
        Log.e(TAG, "***** Inside removeFromDataBase and is de;et value is:- " + isDelete);
        if (!(isDelete == 1)) {
            try {
                if ((jsonObject.getInt("Type")) == Constant.LEAVE_WITHDRAW_TO_ADMIN) {
                    Log.v(TAG, "***** Inside (jsonObject.getInt) == Constant.LEAVE_WITHDRAW_TO_ADMIN)");
                    LeaveWithDrawn_TO_ADMIN(context, jsonObject, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void removeFromTransactionId(String approvalID, Context context) {
        Log.v(TAG, "***** Inside removeFromDataBase");
        helper = new SQLiteHelper(context, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();
        String whereClause = "TransactionID" + "=?";
        String[] whereArgs = new String[]{"" + approvalID};

        int isDelete = Util.getDb(context).delete(Constant.ApprovalsTable, whereClause, whereArgs);

        Log.v(TAG, "***** Inside removeFromDataBase after delete **********");

    }

    public static void insertIntoApprovals(JSONObject jObject, String type, Context context, boolean notificationFlag, String gcmMsg) {
        helper = new SQLiteHelper(context, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();


        //String attendanceType = jObj.optString("AttendanceType");
        String data = jObject.optString("data");
        try {
            JSONObject jsonObj = new JSONObject(data);
            String date1, description = "";
            String isManualAttendance = jsonObj.optString("manualAttendanceType");
            date1 = jsonObj.optString("date");
            if (isManualAttendance.equals("half")) {
                description = "Manual Attendance Half Day";
            } else if (isManualAttendance.equals("full")) {
                description = "Manual Attendance Full Day";
            }
            ContentValues values = new ContentValues();
            values.put("id", "" + jsonObj.optString("attendanceId"));
            values.put("userid", "" + jsonObj.optInt("id"));
            values.put("title", "" + jsonObj.optString("firstName") + " " + jsonObj.optString("lastName"));
            values.put("description", "" + description);
            values.put("type", "" + type);
            values.put("img", "" + jsonObj.optString("picture"));
            values.put("date", "" + date1);
            values.put("Status", "" + jObject.optInt("Status"));
            values.put("data", "" + jsonObj.toString());
            values.put("TransactionId", "" + jObject.optString("TransactionID"));
            values.put("Time", "" + jObject.optString("LastModified"));


            String TransactionID = jObject.optString("TransactionID");
            int type1 = jObject.optInt("Type");

            Cursor cursor = null;
            boolean flag = true;
            try {
                if (TransactionID.length() > 0) {
                    cursor = db.rawQuery("select * from " + Constant.ApprovalsTable + " where TransactionID like '" + TransactionID + "' AND type like '" + type1 + "'", null);
                    if (cursor != null && cursor.getCount() > 0) flag = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) cursor.close();
            }

            if (flag) {
                db.insert(Constant.ApprovalsTable, null, values);
            } else {
                db.update(Constant.ApprovalsTable, values, "TransactionID like '" + TransactionID + "'", null);
            }

            db.close();

            if (jObject.optInt("Status") != 1) notificationFlag = false;
            if (notificationFlag) {
                String roleId = Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    Util.generateNotificationFragment(context, gcmMsg, AdminDashboardNewActivity.class, 3);
                } else if (Arrays.asList("3").contains(roleId)) {
                    Util.generateNotificationFragment(context, gcmMsg, ManagerDashboardNewActivity.class, 3);
                } else if (Arrays.asList("4").contains(roleId)) {
                    Util.generateNotificationFragment(context, gcmMsg, UserDashboardNewActivity.class, 3);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertUpdateTimeValue(JSONObject jsonObject, String type, Context context, String gcmMessage, boolean notificationFlag) {
        helper = new SQLiteHelper(context, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        try {
            String data = jsonObject.optString("data");
            JSONObject jsonObj = new JSONObject(data);

            String message = jsonObject.optString("message");

            ContentValues values = new ContentValues();
            values.put("id", "" + jsonObj.optString("attendanceID"));
            values.put("userid", "" + jsonObj.optInt("id"));
            //  values.put("title", "" + jsonObj.optString("userName"));
            values.put("title", "" + jsonObj.optString("firstName") + " " + jsonObj.optString("lastName"));
            values.put("description", "" + message);
            values.put("type", "" + type);
            values.put("img", "" + jsonObj.optString("picture"));
            values.put("date", "" + jsonObj.optString("newTimeIn"));
            values.put("end_date", "" + jsonObj.optString("newTimeOut"));
            values.put("data", "" + jsonObj.toString());
            values.put("Status", "" + jsonObject.optInt("Status"));
            values.put("TransactionId", "" + jsonObject.optString("TransactionID"));
            values.put("Time", "" + jsonObject.optString("LastModified"));


            String TransactionID = jsonObject.optString("TransactionID");
            int type1 = jsonObject.optInt("Type");

            Cursor cursor = null;
            boolean flag = true;
            try {
                if (TransactionID.length() > 0) {
                    cursor = db.rawQuery("select * from " + Constant.ApprovalsTable + " where TransactionID like '" + TransactionID + "' AND type like '" + type1 + "'", null);
                    if (cursor != null && cursor.getCount() > 0) flag = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) cursor.close();
            }

            if (flag) {
                db.insert(Constant.ApprovalsTable, null, values);
            } else {
                db.update(Constant.ApprovalsTable, values, "TransactionID like '" + TransactionID + "'", null);
            }

            if (jsonObject.optInt("Status") != 1) notificationFlag = false;

            db.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        if (notificationFlag) {

            String roleId = Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID);
            if (Arrays.asList("1", "2").contains(roleId)) {
                Util.generateNotificationFragment(context, gcmMessage, AdminDashboardNewActivity.class, 3);
            } else if (Arrays.asList("3").contains(roleId)) {
                Util.generateNotificationFragment(context, gcmMessage, ManagerDashboardNewActivity.class, 3);
            } else if (Arrays.asList("4").contains(roleId)) {
                Util.generateNotificationFragment(context, gcmMessage, UserDashboardNewActivity.class, 3);
            }
        }
    }

    class checkInAsync extends AsyncTask<Void, String, String> {
        Context context;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        public checkInAsync(Context mContext) {
            this.context = mContext;
        }

        @Override
        protected String doInBackground(Void... params) {

            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            params1.add(new BasicNameValuePair("UserID", "" + Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_USERID)));
            params1.add(new BasicNameValuePair("CompanyID", "" + Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_COMPANY_ID)));
            params1.add(new BasicNameValuePair("Latitude", "" + curLat));
            params1.add(new BasicNameValuePair("Longitude", "" + curLng));
            params1.add(new BasicNameValuePair("AddressMasterID", "0"));
            params1.add(new BasicNameValuePair("ClientVendorID", "0"));

            Log.e("params1", ">>" + params1);
            String response = Util.makeServiceCall(Constant.URL + "checkIn", 2, params1, context);
            Log.e("response", "<<" + response);
            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");
                if (status.equals("Success")) {
                    //JSONObject jData = jObj.getJSONObject("data");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class checkOutAsync extends AsyncTask<Void, String, String> {

        Context context;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        public checkOutAsync(Context mContext) {
            this.context = mContext;
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            params1.add(new BasicNameValuePair("UserID", "" + Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_USERID)));
            params1.add(new BasicNameValuePair("CompanyID", "" + Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_COMPANY_ID)));
            params1.add(new BasicNameValuePair("Latitude", "" + curLat));
            params1.add(new BasicNameValuePair("Longitude", "" + curLng));
            params1.add(new BasicNameValuePair("AddressMasterID", "0"));
            params1.add(new BasicNameValuePair("ClientVendorID", "0"));

            Log.e("params1", ">>" + params1);
         /*   String response = Util.postData(params1, Constant.URL
                    + "checkOut");*/
            String response = Util.makeServiceCall(Constant.URL + "checkOut", 2, params1, context);
            Log.e(TAG, "response is:- " + response);
            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            try {
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");

                if (status.equals("Success")) {
                    JSONObject jData = jObj.getJSONObject("data");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void getCurrentLatLong(Context context, boolean isCheckIn) {
        GPSTracker gps = new GPSTracker(context);
        curLat = gps.getLatitude();
        curLng = gps.getLongitude();
        Log.e(TAG, "Inside getCurrentLatLong ");
        if (Util.isOnline(context)) {
            if (isCheckIn) {
                Log.e(TAG, "Inside isCheckIn true");
                new checkInAsync(context).execute();

            } else {
                new checkOutAsync(context).execute();
            }
        } else {
            Toast.makeText(context, R.string.network_error, Toast.LENGTH_LONG).show();
        }
    }

    public static void AttendanceApprove_TO_User_Notification(Context mContext, JSONObject jData, boolean notificationFlag) {

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();

        try {

            String displayMsg = jData.optString("message");
            String data = jData.optString("data");
            JSONObject jsonObj = new JSONObject(data);

           /* String adminData = jsonObj.optString("adminObject");
            JSONObject jAdminObject = new JSONObject(adminData);*/

            ContentValues values = new ContentValues();
            values.put("Type", "" + jData.optInt("Type"));
            values.put("User_Id", "" + jsonObj.optInt("UserID"));
            values.put("Title", "" + jsonObj.optString("FirstName") + " " + jsonObj.optString("LastName"));
            values.put("Image_Url", "" + jsonObj.optString("Picture"));
            values.put("Description", "" + displayMsg);
            values.put("Extra", "" + jsonObj);
            values.put("Time", "" + jData.optString("LastModified"));
            values.put("TransactionID", "" + jData.optString("TransactionID"));
            db.insert(Constant.NotificationTable, null, values);

            if (notificationFlag) {
                Util.generateNotification(mContext, displayMsg, AttendanceDetailsActivity.class);

                /*String roleId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    generateNotificationFragment(mContext, displayMsg, AdminDashboardNewActivity.class, 2);
                } else if (Arrays.asList("3").contains(roleId)) {
                    generateNotificationFragment(mContext, displayMsg, ManagerDashboardNewActivity.class, 2);
                } else if (Arrays.asList("4").contains(roleId)) {
                    generateNotificationFragment(mContext, displayMsg, UserDashboardNewActivity.class, 2);
                }*/
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void LeaveWithDrawn_TO_ADMIN(Context mContext, JSONObject jData, boolean notificationFlag) {

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();
        Log.e(TAG, "Inside LeaveWithDrawn_TO_ADMIN");
        try {

            String displayMsg = jData.optString("message");
            String data = jData.optString("data");
            JSONObject jsonObj = new JSONObject(data);

            ContentValues values = new ContentValues();
            values.put("Type", "" + jData.optInt("Type"));
            values.put("User_Id", "" + jsonObj.optInt("userId"));
            values.put("Title", "" + jsonObj.optString("firstName") + " " + jsonObj.optString("lastName"));
            values.put("Image_Url", "" + jsonObj.optString("picture"));
            values.put("Description", "" + displayMsg);
            values.put("Extra", "" + jsonObj.toString());
//            values.put("Time", "" + jData.optString("LastModified"));
            values.put("TransactionID", "" + jData.optString("TransactionID"));
            db.insert(Constant.NotificationTable, null, values);


            Log.e(TAG, "Inside LeaveWithDrawn_TO_ADMIN 1111111111111");
            if (notificationFlag) {
                //generateNotification(mContext, displayMsg, null);

                String roleId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, displayMsg, AdminDashboardNewActivity.class, 2);
                } else if (Arrays.asList("3").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, displayMsg, ManagerDashboardNewActivity.class, 2);
                } else if (Arrays.asList("4").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, displayMsg, UserDashboardNewActivity.class, 2);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void LeaveApproval_To_User(Context mContext, JSONObject jData, boolean notificationFlag) {

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();

        try {

            String displayMsg = jData.optString("message");
            String data = jData.optString("data");
            JSONObject jsonObj = new JSONObject(data);

            ContentValues values = new ContentValues();
            values.put("Type", "" + jData.optInt("Type"));
            values.put("User_Id", "" + jsonObj.optInt("UserID"));
            values.put("Title", "" + jsonObj.optString("FirstName") + " " + jsonObj.optString("LastName"));
            values.put("Image_Url", "" + jsonObj.optString("Picture"));
            values.put("Description", "" + displayMsg);
            values.put("Extra", "" + jsonObj);
            values.put("Time", "" + jData.optString("LastModified"));
            values.put("TransactionID", "" + jData.optString("TransactionID"));
            db.insert(Constant.NotificationTable, null, values);

            if (notificationFlag) {
                //generateNotification(mContext, displayMsg, null);
                String roleId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, displayMsg, AdminDashboardNewActivity.class, 2);
                } else if (Arrays.asList("3").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, displayMsg, ManagerDashboardNewActivity.class, 2);
                } else if (Arrays.asList("4").contains(roleId)) {
                    Util.generateNotificationFragment(mContext, displayMsg, UserDashboardNewActivity.class, 2);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void TimeUpdate_To_User(Context mContext, JSONObject jData, boolean notificationFlag) {
        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();


        try {
            String displayMsg = jData.optString("message");
            String data = jData.optString("data");
            JSONObject jsonObj = new JSONObject(data);

            String adminData = jsonObj.optString("adminObject");
            JSONObject jAdminObject = new JSONObject(adminData);

            Log.e(TAG, "Inside TimeUpdate_To_User before insert ");
            ContentValues values = new ContentValues();
            values.put("Type", "" + jData.optInt("Type"));
            values.put("User_Id", "" + jsonObj.optInt("userId"));
            values.put("Title", "" + jAdminObject.optString("firstName") + " " + jAdminObject.optString("lastName"));
            values.put("Image_Url", "" + jAdminObject.optString("picture"));
            values.put("Description", "" + displayMsg);
            values.put("Extra", "" + jsonObj);
            values.put("Time", "" + jData.optString("LastModified"));
            values.put("TransactionID", "" + jData.optString("TransactionID"));
            db.insert(Constant.NotificationTable, null, values);


            if (notificationFlag) {
                Util.generateNotification(mContext, displayMsg, AttendanceDetailsActivity.class);

               /* String roleId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    generateNotificationFragment(mContext, displayMsg, AdminDashboardNewActivity.class, 2);
                } else if (Arrays.asList("3").contains(roleId)) {
                    generateNotificationFragment(mContext, displayMsg, ManagerDashboardNewActivity.class, 2);
                } else if (Arrays.asList("4").contains(roleId)) {
                    generateNotificationFragment(mContext, displayMsg, UserDashboardNewActivity.class, 2);
                }*/
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void insertIntoClientVendor(JSONObject jObject, String type, Context context, boolean isShow) {
        helper = new SQLiteHelper(context, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        //String attendanceType = jObj.optString("AttendanceType");
        String data = jObject.optString("data");
        try {
            JSONObject jsonObj = new JSONObject(data);

            ContentValues values = new ContentValues();
            values.put("id", "" + jsonObj.optString("attendanceId"));
            values.put("userid", "" + jsonObj.optInt("id"));
            values.put("title", "" + jsonObj.optString("firstName") + " " + jsonObj.optString("lastName"));
            values.put("description", "Attendance request ");
            values.put("type", "" + type);
            values.put("img", "" + jsonObj.optString("picture"));
            values.put("date", "" + jsonObj.optString("date"));
            values.put("Status", "" + jObject.optInt("Status"));
            values.put("data", "" + jsonObj.toString());
            values.put("TransactionId", "" + jObject.optString("TransactionID"));
            values.put("Time", "" + jObject.optString("LastModified"));

            String TransactionID = jObject.optString("TransactionID");
            int type1 = jObject.optInt("Type");

            Cursor cursor = null;
            boolean flag = true;
            try {
                if (TransactionID.length() > 0) {
                    cursor = db.rawQuery("select * from " + Constant.ApprovalsTable + " where TransactionID like '" + TransactionID + "' AND type like '" + type1 + "'", null);
                    if (cursor != null && cursor.getCount() > 0) flag = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) cursor.close();
            }

            if (flag) {
                db.insert(Constant.ApprovalsTable, null, values);
            } else {
                db.update(Constant.ApprovalsTable, values, "TransactionID like '" + TransactionID + "'", null);
            }

            if (jObject.optInt("Status") != 1) isShow = false;

            db.close();


            if (isShow) {
                String roleId = Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    Util.generateNotificationFragment(context, "Attendance request", AdminDashboardNewActivity.class, 3);
                } else if (Arrays.asList("3").contains(roleId)) {
                    Util.generateNotificationFragment(context, "Attendance request", ManagerDashboardNewActivity.class, 3);
                } else if (Arrays.asList("4").contains(roleId)) {
                    Util.generateNotificationFragment(context, "Attendance request", UserDashboardNewActivity.class, 3);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
