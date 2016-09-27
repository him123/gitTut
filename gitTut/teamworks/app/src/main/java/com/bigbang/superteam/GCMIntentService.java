/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bigbang.superteam;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.GPSTracker;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.Util;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Date;

/**
 * {@link android.app.IntentService} responsible for handling GCM messages.
 */

public class GCMIntentService extends GCMBaseIntentService {

    @SuppressWarnings("hiding")
    private static final String TAG = "GCMIntentService";
    //public static String SENDER_ID = "729515637394";
    public static String SENDER_ID = "226511241952";
    Context context;
    double curLat, curLng;
    boolean isCheckIn = false;
    String time, date;
    int month, year, dayOfMonth;

    public GCMIntentService() {
        super(SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered: regId = " + registrationId);
        ServerUtilities.register(context, registrationId);

        if (registrationId.length() > 0)
            Util.WriteSharePrefrence(context, Constant.SHRED_PR.KEY_GCM_ID, "" + registrationId);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            Util.WriteSharePrefrence(context, Constant.SHRED_PR.KEY_GCM_ID, "");
            ServerUtilities.unregister(context, registrationId);
        } else {
            // This callback results from the call to unregister made on
            // ServerUtilities when the registration to the server failed.
            Log.i(TAG, "Ignoring unregister callback");
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent) {

        String message = getString(R.string.gcm_message);
        SQLiteHelper helper = new SQLiteHelper(context, Constant.DatabaseName);
        this.context = context;
        SQLiteDatabase db = helper.openDatabase();

        Log.i(TAG, ",");

        // Extract the payload from the message
        final Bundle extras = intent.getExtras();
        boolean isLogged = Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_IS_LOGGEDIN).equals("true");
        String roleId = Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID);

        if (extras != null && isLogged) {
            System.out.println("RESPONSE:" + extras.get("message"));
            message = "" + extras.get("message");

            try {
                JSONObject jsonObject = new JSONObject("" + message);
                int type = jsonObject.getInt("Type");
                String userID = jsonObject.optString("UserID");
                String TransactionID = "" + jsonObject.optString("TransactionID");
                String LastModified = "" + jsonObject.optString("LastModified");
                int Status = jsonObject.optInt("Status");
                String CompanyID = "" + jsonObject.optString("CompanyID");
                String CurrentCompanyID = read(Constant.SHRED_PR.KEY_COMPANY_ID);
                Log.e("TAG", "TransactionID:" + TransactionID + ">>" + "type:" + type);

                if (type == Constant.PROVIDE_CURRENT_LOCATION) {
                    if (CompanyID.equals(CurrentCompanyID)) {
                        GPSTracker gps = new GPSTracker(context);
                        if (gps.canGetLocation()) {

                            Double lat = gps.getLatitude();
                            Double lng = gps.getLongitude();

                            if (lat != 0.0 && lng != 0.0) {
                                JSONObject jsonObject1 = jsonObject.optJSONObject("data");
                                String AdminID = jsonObject1.optString("AdminID");
                                String uniqueKey = jsonObject1.optString("uniqueKey");
                                if (Arrays.asList("3", "4").contains(roleId)) {
                                    if (Util.isOnline(context)) {

                                        CommonGCM.updateUserCurrentLoactionAPI(context, gps.getLatitude(), gps.getLongitude(), AdminID, uniqueKey);
                                    }
                                }
                            }
                        }
                    }
                } else if (type == Constant.AUTO_CHECKIN) {
                    if (Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("3") ||
                            Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("4")) {
                        isCheckIn = true;
                        //getCurrentLatLong();
                        Log.e(TAG, "Inside AUTO_CHECKIN");
                        Leave_Attendance_GCM leave_attendance_gcm = new Leave_Attendance_GCM();
                        leave_attendance_gcm.getCurrentLatLong(context, isCheckIn);
                    }

                } else if (type == Constant.AUTO_CHECKOUT) {
                    if (Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("3") ||
                            Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("4")) {
                        Intent i = new Intent(GCMIntentService.this, MyDialog.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(i);
                    }
                } else if(type == Constant.USER_PAYSLIP){
                    if (Integer.parseInt(roleId) > 1) {
                        CommonGCM.InviteUserPayroll(context, message, true);
                    }
                } else if(type == Constant.ADMIN_PAYROLL){
                    if (roleId.equalsIgnoreCase("1") || roleId.equalsIgnoreCase("2")) {
                        CommonGCM.InviteUserPayroll(context, message, true);
                    }
                } else {

                    Cursor cursor = null, cursor1 = null;
                    if (userID.equals(read(Constant.SHRED_PR.KEY_USERID)) && (CompanyID.equals(CurrentCompanyID) || CompanyID.equals("0"))) {
                        boolean flag = true;
                        try {
                            if (TransactionID.length() > 0) {
                                cursor = db.rawQuery("select * from " + Constant.NotificationTable + " where TransactionID like \"" + TransactionID + "\" AND Type like \"" + type + "\"", null);
                                if (cursor != null && cursor.getCount() > 0) flag = false;
                                if (flag) {
                                    cursor1 = db.rawQuery("select * from " + Constant.ApprovalsTable + " where TransactionID like \"" + TransactionID + "\" AND type like \"" + type + "\"", null);
                                    if (cursor1 != null && cursor1.getCount() > 0) {
                                        if (Status == 1)
                                            flag = false;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if (cursor != null) cursor.close();
                            if (cursor1 != null) cursor1.close();
                        }

                        try {
                            if (flag) {
                                if (isApproval(type)) {
                                    Date date1 = Util.sdf.parse(LastModified);
                                    Date date2 = Util.sdf.parse(read(Constant.SHRED_PR.KEY_LASTSYNC_APPROVAL_TIME));
                                    Log.e("date1:date2", ">>" + date1 + ":" + date2);
                                    if (date1.before(date2) || date1.equals(date2)) flag = false;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        Log.e("flag:", ">>" + flag);
                        if (flag) {
                            switch (type) {

                                //Tracking:
                                case Constant.UPDATE_CURRENT_LOCATION:
                                    JSONObject jsonObject1 = jsonObject.optJSONObject("data");
                                    String latitude = "" + jsonObject1.getString("latitude");
                                    String longitude = "" + jsonObject1.getString("longitude");
                                    String FirstName = "" + jsonObject1.getString("FirstName");
                                    String LastName = "" + jsonObject1.getString("LastName");
                                    String uniqueKey = "" + jsonObject1.getString("uniqueKey");
                                    String MemberID = "" + jsonObject1.getString("MemberID");

                                    Intent intent1 = new Intent();
                                    // intent.setAction(MY_ACTION);
                                    intent1.setAction(ACTIVITY_SERVICE);
                                    intent1.putExtra("latitude", ""
                                            + latitude);
                                    intent1.putExtra("longitude", ""
                                            + longitude);
                                    intent1.putExtra("FirstName", ""
                                            + FirstName);
                                    intent1.putExtra("LastName", ""
                                            + LastName);
                                    intent1.putExtra("uniqueKey", ""
                                            + uniqueKey);
                                    intent1.putExtra("MemberID", ""
                                            + MemberID);
                                    sendBroadcast(intent1);
                                    break;


                                //Attendance & Leave:
                                case Constant.ATTENDANCE_APPROVAL_TO_ADMIN:
                                    if (!Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("4")) {
                                        String data = jsonObject.optString("data");
                                        JSONObject jsonObj = new JSONObject(data);
                                        String displayMsg = jsonObject.optString("message");
                                        int status = jsonObject.optInt("Status");
                                        String approvalId = jsonObj.optString("attendanceId");
                                        Leave_Attendance_GCM.insertIntoApprovals(jsonObject, "" + type, context, true, displayMsg);

                                        Intent iAttendance = new Intent();
                                        iAttendance.setAction(ACTIVITY_SERVICE);
                                        iAttendance.putExtra("pos", "1");
                                        sendBroadcast(iAttendance);
                                    }
                                    break;
                                case Constant.ATTENDANCE_APPROVAL_TO_USER:
                                    if (Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("3") ||
                                            Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("4")) {
                                        Leave_Attendance_GCM.AttendanceApprove_TO_User_Notification(context, jsonObject, true);
                                    }


                                    break;
                                case Constant.AUTO_CHECKIN:
                                    if (Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("3") ||
                                            Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("4")) {
                                        isCheckIn = true;
                                        Log.e(TAG, "Inside AUTO_CHECKIN");
                                        Leave_Attendance_GCM leave_attendance_gcm = new Leave_Attendance_GCM();
                                        leave_attendance_gcm.getCurrentLatLong(context, isCheckIn);
                                    }
                                    break;
                                case Constant.AUTO_CHECKOUT:
                                    if (Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("3") ||
                                            Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("4")) {
                                        Intent i = new Intent(GCMIntentService.this, MyDialog.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(i);
                                    }
                                    break;
                                case Constant.LEAVE_APPROVAL_TO_ADMIN:
                                    if (!Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("4")) {
                                        String data = jsonObject.optString("data");
                                        String displayMsg = jsonObject.optString("message");

                                        Leave_Attendance_GCM.insertIntoLeaveApprovals(jsonObject, "" + type, context, displayMsg, true);

                                        Intent iAttendance = new Intent();
                                        iAttendance.setAction(context.ACTIVITY_SERVICE);
                                        iAttendance.putExtra("pos", "1");
                                        context.sendBroadcast(iAttendance);

                                    }
                                    break;
                                case Constant.LEAVE_APPROVAL_TO_USER:
                                    if (Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("3") || Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("4")) {
                                        String displayMsg = jsonObject.optString("message");
                                        String data = jsonObject.optString("data");
                                        JSONObject jsonObj = new JSONObject(data);
                                        Leave_Attendance_GCM.LeaveApproval_To_User(context, jsonObject, true);
                                    }
                                    break;
                                case Constant.LEAVE_WITHDRAW_TO_ADMIN:
                                    if (!Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("4")) {
                                        String data = jsonObject.optString("data");
                                        JSONObject jsonObj = new JSONObject(data);
                                        String approvalId = jsonObject.optString("TransactionID");
                                        //Leave_Attendance_GCM.removeFromDataBase(approvalId, context, jsonObject);

                                        //  String approvalId = jsonObject.optString("TransactionID");
                                        Leave_Attendance_GCM.removeUsingTransactionId(approvalId, context, jsonObject);

                                        Intent iAttendance = new Intent();
                                        iAttendance.setAction(context.ACTIVITY_SERVICE);
                                        iAttendance.putExtra("pos", "1");
                                        context.sendBroadcast(iAttendance);
                                    }
                                    break;
                                case Constant.LEAVE_CANCELLED_TO_ADMIN:
                                    if (!Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("4")) {
                                        String data = jsonObject.optString("data");
                                        String displayMsg = jsonObject.optString("message");
                                        JSONObject jsonObj = new JSONObject(data);
                                        String approvalId = jsonObject.optString("TransactionID");
                                        Leave_Attendance_GCM.removeUsingTransactionId(approvalId, context, jsonObject);

                                    }
                                    break;
                                case Constant.UPDATETIME_APPROVAL_TO_ADMIN:
                                    if (!Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("4")) {
                                        String displayMsg = jsonObject.optString("message");
                                        String data = jsonObject.optString("data");
                                        JSONObject jsonObj = new JSONObject(data);
                                        int status = jsonObject.optInt("Status");

                                        String approvalId = jsonObj.optString("attendanceId");
                                        Leave_Attendance_GCM.insertUpdateTimeValue(jsonObject, "" + type, context, displayMsg, true);

                                        Intent iAttendance = new Intent();
                                        iAttendance.setAction(context.ACTIVITY_SERVICE);
                                        iAttendance.putExtra("pos", "1");
                                        context.sendBroadcast(iAttendance);
                                    }
                                    break;
                                case Constant.UPDATETIME_APPROVAL_TO_USER:
                                    if (Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("3") || Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("4")) {
                                        // Log.e(TAG, "INside UPDATETIME_APPROVAL_TO_USER 1111");
                                        // Leave_Attendance_GCM.TimeUpdate_To_User(context, jsonObject, true);

                                        Leave_Attendance_GCM.AttendanceApprove_TO_User_Notification(context, jsonObject, true);

                                        // Log.e(TAG, "INside UPDATETIME_APPROVAL_TO_USER 322222");
                                    }
                                    break;
                                case Constant.ATTENDANCE_CUSTOMER_VENDOR_TO_ADMIN:
                                    if (!Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("4")) {
                                        String data = jsonObject.optString("data");
                                        JSONObject jsonObj = new JSONObject(data);
                                        int status = jsonObject.optInt("Status");
                                        String approvalId = jsonObject.optString("TransactionID");
                                        // String displayMsg = jsonObject.optString("message");
                                        Leave_Attendance_GCM.insertIntoClientVendor(jsonObject, "" + type, context, true);

                                        Intent iAttendance = new Intent();
                                        iAttendance.setAction(ACTIVITY_SERVICE);
                                        iAttendance.putExtra("pos", "1");
                                        sendBroadcast(iAttendance);
                                    }
                                    break;
                                case Constant.ATTENDANCE_CUSTOMER_VENDOR_TO_USER:
                                    if (Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("3") || Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("4")) {
                                        Leave_Attendance_GCM.AttendanceApprove_TO_User_Notification(context, jsonObject, true);
                                    }
                                    break;

                                case Constant.LEAVE_UPDATE_TO_USER:
                                    if (Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("3") || Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("4")) {
                                        String displayMsg = jsonObject.optString("message");
                                        String data = jsonObject.optString("data");
                                        JSONObject jsonObj = new JSONObject(data);
                                        Leave_Attendance_GCM.LeaveApproval_To_User(context, jsonObject, true);
                                    }
                                    break;
                                //WorkItem & Projects:
                                case Constant.NEW_WORKITEM:
                                    WorkItem_GCM.HandleNewWorkItem(jsonObject, context, true);
                                    break;
                                case Constant.ATTACHMENTS:
                                    WorkItem_GCM.HandleAttachment(jsonObject, context);
                                    break;
                                case Constant.CREATE_NEWPRJECT:
                                    WorkItem_GCM.HandleNewProject(jsonObject, context, true, true);
                                    break;
                                case Constant.MODIFIED_PROJECT:
                                    WorkItem_GCM.HandleModifiedProject(jsonObject, context, true, true);
                                    break;
                                case Constant.UPDATE_WORKITEM:
                                    WorkItem_GCM.HandleWorkItemUpdate(jsonObject, context, true);
                                    break;
                                case Constant.CREATE_NEWWORKITEM:
                                    WorkItem_GCM.HandleNewWorkItemApproval(jsonObject, context, true);

                                    Intent i5 = new Intent();
                                    i5.setAction(ACTIVITY_SERVICE);
                                    i5.putExtra("pos", "1");
                                    sendBroadcast(i5);

                                    break;
                                case Constant.EDIT_WORKITEM_APPROVAL:
                                    WorkItem_GCM.HandleEditWorkItemApproval(jsonObject, context, true);

                                    Intent i1 = new Intent();
                                    i1.setAction(ACTIVITY_SERVICE);
                                    i1.putExtra("pos", "1");
                                    sendBroadcast(i1);

                                    break;
                                case Constant.APPROVE_WORKITEMUPDATE:
                                    if (Status == 1) {
                                        WorkItem_GCM.HandleWorkItemUpdate(jsonObject, context, true);
                                    }
                                    WorkItem_GCM.HandleWorkUpdateApproval(jsonObject, context, true);

                                    Intent i2 = new Intent();
                                    i2.setAction(ACTIVITY_SERVICE);
                                    i2.putExtra("pos", "1");
                                    sendBroadcast(i2);

                                    break;
                                case Constant.APPROVAL_NEWPRJECT:
                                    WorkItem_GCM.HandleNewProjectapproval(jsonObject, context, true);

                                    Intent i3 = new Intent();
                                    i3.setAction(ACTIVITY_SERVICE);
                                    i3.putExtra("pos", "1");
                                    sendBroadcast(i3);

                                    break;
                                case Constant.REJECT_WORKITEMUPDATE:
                                    WorkItem_GCM.HandleWorkUpdateRejection(message, context, true);
                                    break;
                                case Constant.REJECT_NEWWORKITEM:
                                    WorkItem_GCM.HandleNewWorkItemRejection(jsonObject, context, true);
                                    break;
                                case Constant.REJECT_NEWPRJECT:
                                    WorkItem_GCM.HandleNewProjectRejection(jsonObject, context, true);
                                    break;
                                case Constant.EDIT_PROJECT_APPROVAL:
                                    WorkItem_GCM.HandleEditProjectApproval(jsonObject, context, true);

                                    Intent i4 = new Intent();
                                    i4.setAction(ACTIVITY_SERVICE);
                                    i4.putExtra("pos", "1");
                                    sendBroadcast(i4);

                                    break;
                                case Constant.PROJECT_APPROVED:
                                    WorkItem_GCM.HandleEditProjectApproved(jsonObject, context, true);
                                    break;
                                case Constant.PROJECT_REJECTED:
                                    WorkItem_GCM.HandleEditProjectRejected(jsonObject, context, true);
                                    break;
                                case Constant.WORKITEM_MODIFIED:
                                    WorkItem_GCM.HandleEditWorkItemApproved(jsonObject, context);
                                    break;
                                //CommonServices:
                                case Constant.AUTO_LOGOUT:
                                    Log.d("", "**************************** LOG OUT *******************");
                                    TeamWorkApplication.LogOutClear();
                                    break;
                                case Constant.CHANGE_MOBILE_APPROVAL_TO_ADMIN:
                                    if (Arrays.asList("1", "2").contains(roleId)) {
                                        CommonGCM.ChangeMobileApprovalToAdmin(context, message, true);
                                    }

                                    Intent intent2 = new Intent();
                                    intent2.setAction(context.ACTIVITY_SERVICE);
                                    intent2.putExtra("pos", "1");
                                    context.sendBroadcast(intent2);

                                    break;
                                case Constant.USER_RESIGN_APPROVAL_TO_ADMIN:
                                    if (Arrays.asList("1", "2").contains(roleId)) {
                                        CommonGCM.UserResignApprovalToAdmin(context, message, true);
                                    }

                                    Intent intent3 = new Intent();
                                    intent3.setAction(context.ACTIVITY_SERVICE);
                                    intent3.putExtra("pos", "1");
                                    context.sendBroadcast(intent3);

                                    break;
                                case Constant.CHANGE_MOBILE_APPROVAL_TO_USER:
                                    if (!roleId.equals("1")) {
                                        CommonGCM.ChangeMobileApprovalToUser(context, message, true);
                                    }
                                    break;
                                case Constant.USER_RESIGN_APPROVAL_TO_USER:
                                    if (!roleId.equals("1")) {
                                        CommonGCM.UserResignApprovalToUser(context, message, true);
                                    }
                                    break;
                                case Constant.UPDATE_CUSTOMER_APPROVAL_TO_ADMIN:
                                    if (Arrays.asList("1", "2").contains(roleId)) {
                                        CommonGCM.UpdateCustomerApprovalToAdmin(context, message, true);
                                    }

                                    Intent intent4 = new Intent();
                                    intent4.setAction(context.ACTIVITY_SERVICE);
                                    intent4.putExtra("pos", "1");
                                    context.sendBroadcast(intent4);

                                    break;
                                case Constant.UPDATE_CUSTOMER_APPROVAL_TO_USER:
                                    if (Arrays.asList("3", "4").contains(roleId)) {
                                        CommonGCM.UpdateCustomerApprovalToUser(context, message, true);
                                    }
                                    break;
                                case Constant.NEW_USER_ADDED:
                                    CommonGCM.NEW_USER_ADDED(context, message, true);
                                    break;
                                case Constant.NEW_REPORTING_MEMBER:
                                    CommonGCM.NEW_REPORTING_MEMBER(context, message, true);
                                    break;
                                case Constant.USER_INVITATION_TO_ADMIN:
                                    CommonGCM.USER_INVITATION_TO_ADMIN(context, message, true);
                                    break;
                                case Constant.DELETE_USER:
                                    CommonGCM.DELETE_USER(context, message, true);
                                    break;
                                case Constant.DELETE_COMPANY:
                                    CommonGCM.DELETE_COMPANY(context, message, true);
                                    break;
                                case Constant.INVITE_USER:
                                    CommonGCM.InviteUser(context, message, true);
                                    break;
                                case Constant.UPDATE_ROLE_OR_MANAGER:
                                    CommonGCM.UPDATE_ROLE_MANAGER(context, message, true);
                                    break;
                                case Constant.UPDATE_COMPANYINFO:
                                    //if (read(Constant.SHRED_PR.KEY_ROLE_ID).equals("1") || read(Constant.SHRED_PR.KEY_ROLE_ID).equals("2"))
                                    write(Constant.SHRED_PR.KEY_COMPANY_SETUP_DATA, "" + jsonObject.optString("data"));
                                    break;
                                case Constant.GET_COMPANY_PRIVILAGES:
                                    if (read(Constant.SHRED_PR.KEY_ROLE_ID).equals("1") || read(Constant.SHRED_PR.KEY_ROLE_ID).equals("2"))
                                        write(Constant.SHRED_PR.KEY_COMPANY_PRIVILAGES, "" + message);
                                    break;
                                case Constant.GET_USER_PRIVILAGES_MANAGER:
                                    if (read(Constant.SHRED_PR.KEY_ROLE_ID).equals("3")) {
                                        write(Constant.SHRED_PR.KEY_USER_PRIVILAGES, "" + message);
                                        Privileges.Init(context);
                                    }
                                    break;
                                case Constant.GET_USER_PRIVILAGES_TEAMMEMBER:
                                    if (read(Constant.SHRED_PR.KEY_ROLE_ID).equals("4")) {
                                        write(Constant.SHRED_PR.KEY_USER_PRIVILAGES, "" + message);
                                        Privileges.Init(context);
                                    }
                                    break;
                                case Constant.UPDATE_USER_COMPANYINFO:
//                                    JSONObject jsondata = jsonObject.optJSONObject("data");
//                                    write(Constant.SHRED_PR.KEY_COMPANY_STARTTIME, jsondata.optString("starttime"));
//                                    write(Constant.SHRED_PR.KEY_COMPANY_ENDTIME, jsondata.optString("endtime"));
                                    JSONObject mainObj = new JSONObject(message);

                                    CommonGCM.updateCompanyInfo(context, mainObj);

                                    break;
                                case Constant.UPDATE_COMPANY:
                                    CommonGCM.updateCompany(context, message, false);
                                    break;
                                case Constant.UPDATE_CUSTOMER_VENDOR:
                                    CommonGCM.updateCustomerVendor(context, message, false);
                                    break;
                                case Constant.UPDATE_USER:
                                    CommonGCM.UPDATE_USER(context, message);
                                    break;
                                case Constant.CHANGE_DEVICE_REQUEST:
                                    if (Arrays.asList("1", "2").contains(roleId)) {
                                        CommonGCM.changeDeviceReq(context, message, true);
                                    }
                                    Intent intent45 = new Intent();
                                    intent45.setAction(context.ACTIVITY_SERVICE);
                                    intent45.putExtra("pos", "1");
                                    context.sendBroadcast(intent45);

                                    break;
                                case Constant.CHANGE_DEVICE_ACK:
                                    CommonGCM.CHANGE_DEVICE_ACK(context, message, true);
                                    break;

                                case Constant.EXPENSE_NOTIFICATION:
                                    if (!roleId.equals("1")) {
                                        CommonGCM.UserExpenseNotification(context, message, true);
                                    }
                                    break;
                                case Constant.EXPENSE_APPROVAL:
                                    if (Arrays.asList("1", "2").contains(roleId)) {
                                        CommonGCM.UserExpenseApprovalToAdmin(context, message, true);
                                    }

                                    Intent intent5 = new Intent();
                                    intent5.setAction(context.ACTIVITY_SERVICE);
                                    intent5.putExtra("pos", "1");
                                    context.sendBroadcast(intent5);

                                    break;

                                default:
//                                    generateNotification(context, message);
                                    break;
                            }
                        }
                    } else
                        Log.e("user id:", ">> Not Matched");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        // notifies user
        Util.generateNotification(context, message, null);
    }

    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
        Log.i(TAG, "Received recoverable error: " + errorId);
        return super.onRecoverableError(context, errorId);
    }

    private boolean isApproval(int type) {

        if (type == Constant.APPROVAL_NEWPRJECT) return true;
        if (type == Constant.CREATE_NEWWORKITEM) return true;
        if (type == Constant.EDIT_PROJECT_APPROVAL) return true;
        if (type == Constant.APPROVE_WORKITEMUPDATE) return true;
        if (type == Constant.EDIT_WORKITEM_APPROVAL) return true;
        if (type == Constant.CHANGE_MOBILE_APPROVAL_TO_ADMIN) return true;
        if (type == Constant.USER_RESIGN_APPROVAL_TO_ADMIN) return true;
        if (type == Constant.ATTENDANCE_APPROVAL_TO_ADMIN) return true;
        if (type == Constant.LEAVE_APPROVAL_TO_ADMIN) return true;
        if (type == Constant.UPDATETIME_APPROVAL_TO_ADMIN) return true;
        if (type == Constant.UPDATE_CUSTOMER_APPROVAL_TO_ADMIN) return true;
        if (type == Constant.ATTENDANCE_CUSTOMER_VENDOR_TO_ADMIN) return true;
        if (type == Constant.CHANGE_DEVICE_REQUEST) return true;
        if (type == Constant.EXPENSE_APPROVAL) return true;

        return false;
    }

    protected String read(String key) {
        return Util.ReadSharePrefrence(this, key);
    }

    protected void write(String key, String val) {
        Util.WriteSharePrefrence(this, key, val);
    }

}
