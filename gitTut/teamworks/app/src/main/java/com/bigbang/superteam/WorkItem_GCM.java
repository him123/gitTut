package com.bigbang.superteam;

import android.app.AlarmManager;
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
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.admin.AdminDashboardNewActivity;
import com.bigbang.superteam.dataObjs.Approval;
import com.bigbang.superteam.dataObjs.Attachment;
import com.bigbang.superteam.dataObjs.NotificationInfo;
import com.bigbang.superteam.dataObjs.Project;
import com.bigbang.superteam.dataObjs.WorkItem;
import com.bigbang.superteam.dataObjs.WorkTransaction;
import com.bigbang.superteam.fragment.ActiveTasksFragment;
import com.bigbang.superteam.fragment.ArchivedTasksFragment;
import com.bigbang.superteam.fragment.PendingTasksFragment;
import com.bigbang.superteam.fragment.ProjectFragment;
import com.bigbang.superteam.manager.ManagerDashboardNewActivity;
import com.bigbang.superteam.model.Address;
import com.bigbang.superteam.model.User;
import com.bigbang.superteam.task.database.TaskDAO;
import com.bigbang.superteam.task.database.TaskMemberDAO;
import com.bigbang.superteam.task.model.TaskMember;
import com.bigbang.superteam.task.model.TaskModel;
import com.bigbang.superteam.user.UserDashboardNewActivity;
import com.bigbang.superteam.util.AlarmReceiver;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.Util;
import com.bigbang.superteam.workitem.HandleResponseWorkItems;
import com.bigbang.superteam.workitem.UpdateWorkActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by USER 3 on 29/05/2015.
 */
public class WorkItem_GCM {
    ///////////////////////////////////////////////////////////////////////////
    /////////////////////NEW ITEMS HANDLING FUNCTIONS//////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    /*Funtion: Insert Work Item into Table
               Generate Notification
               Set Alarm
    Calls:     GCMIntentService.java, onMessage
    Extra Details: */
    public static void HandleNewWorkItem(JSONObject jsonObject, Context mContext, boolean isNotify) {
        Log.e("TAG", "New Work Item Received");

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();

        ContentValues taskValues = new ContentValues();
        try {
            JSONObject jobj = jsonObject.getJSONObject("data");
            taskValues.put(WorkItem.TASK_ID, "" + jobj.optString("workitemid"));
            taskValues.put(WorkItem.USER_CODE, "" + jobj.optString("userid"));
            taskValues.put(WorkItem.TASK_NAME, "" + jobj.optString("name"));
            taskValues.put(WorkItem.DESCRIPTION, "" + jobj.optString("description"));
            taskValues.put(WorkItem.BUDGET, "" + jobj.optString("budget"));
            taskValues.put(WorkItem.PRIORITY, "" + jobj.optString("priority"));
            taskValues.put(WorkItem.ATTACHMENTS, "" + jobj.optString("attachmentlist"));
            taskValues.put(WorkItem.WORK_LOCATION, "" + jobj.optString("location"));
            taskValues.put(WorkItem.WORK_LONGITUDE, "" + jobj.optString("longitude"));
            taskValues.put(WorkItem.WORK_LATITUDE, "" + jobj.optString("latitude"));
            taskValues.put(WorkItem.START_DATE, "" + jobj.optString("startdatetime"));
            taskValues.put(WorkItem.END_DATE, "" + jobj.optString("enddatetime"));
            taskValues.put(WorkItem.ESTIMATED_TIME, "" + jobj.optString("estimatedtime"));
            taskValues.put(WorkItem.ASSIGNED_TO, "" + jobj.optString("tolist"));
            taskValues.put(WorkItem.CC_TO, "" + jobj.optString("cclist"));
            taskValues.put(WorkItem.TASK_TYPE, "" + jobj.optString("worktype"));
            taskValues.put(WorkItem.PROJECT_CODE, "" + jobj.optString("projectid"));
            taskValues.put(WorkItem.TASK_AFTER, "" + jobj.optString("dependenton"));

            taskValues.put(WorkItem.FREQUENCY, "" + jobj.optString("frequency"));

            taskValues.put(WorkItem.DAYCODES_SELECTED, "" + jobj.optString("daycodes"));
            taskValues.put(WorkItem.CUSTOMER_NAME, "" + jobj.optString("customername"));
            taskValues.put(WorkItem.CUSTOMER_CONTACT, "" + jobj.optString("customerno"));
            taskValues.put(WorkItem.CUSTOMER_TYPE, "" + jobj.optString("customertype"));
            taskValues.put(WorkItem.DUE_DATE, "" + jobj.optString("duedate"));
            taskValues.put(WorkItem.PAST_HISTORY, "" + jobj.optString("pasthistory"));
            taskValues.put(WorkItem.VENDOR_PREFERENCE, "" + jobj.optString("vendorpreferrance"));
            taskValues.put(WorkItem.VENDOR_NAME, "" + jobj.optString("vendorname"));
            taskValues.put(WorkItem.ADVANCE_PAID, "" + jobj.optString("advancepaid"));
            taskValues.put(WorkItem.INVOICE_AMOUNT, "" + jobj.optString("invoiceamt"));
            taskValues.put(WorkItem.INVOICE_DATE, "" + jobj.optString("invoicedate"));
            taskValues.put(WorkItem.OUTSTANDING_AMT, "" + jobj.optString("outstandingamt"));
            taskValues.put(WorkItem.TASK_IMAGE, "" + jobj.optString("imageURL"));
            taskValues.put(WorkItem.STATUS, "" + jobj.optString("status"));

            Cursor crsr = db.rawQuery("select * from " + Constant.WorkItemTable + " where " + WorkItem.TASK_ID + " = " + jobj.optString("workitemid"), null);
            if (crsr != null && crsr.getCount() > 0) {
                db.update(Constant.WorkItemTable, taskValues, WorkItem.TASK_ID + " = ?", new String[]{jobj.optString("workitemid") + ""});
            } else {
                db.insert(Constant.WorkItemTable, null, taskValues);
            }
            if (crsr != null) crsr.close();

            if (isNotify) {
                if (ActiveTasksFragment.Active || ArchivedTasksFragment.Active || PendingTasksFragment.Active) {
                    Intent intent = new Intent("workitem_created");
                    intent.putExtra("message", "data");
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                }
//                if (jsonObject.optString("message").contains("approved") ||
//                        (jsonObject.optString("message").contains("reopened") &&
//                                jobj.optString("status").equals("Approved") ||
//                                jobj.optString("status").equals("assigned"))) {

                String roleId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    generateNotificationFragment(mContext, jsonObject.optString("message"), AdminDashboardNewActivity.class, 0);
                } else if (Arrays.asList("3").contains(roleId)) {
                    generateNotificationFragment(mContext, jsonObject.optString("message"), ManagerDashboardNewActivity.class, 0);
                } else if (Arrays.asList("4").contains(roleId)) {
                    generateNotificationFragment(mContext, jsonObject.optString("message"), UserDashboardNewActivity.class, 0);
                }
//                }
            }

            crsr.close();

            // Sending information for notification table
            NotificationInfo noti = new NotificationInfo();
            noti.setId(Integer.parseInt(jobj.optString("workitemid")));
            noti.setTitle(jobj.optString("firstName") + " " + jobj.optString("lastName"));
            noti.setDescription("" + jsonObject.optString("message"));
            noti.setImage_Url("" + jobj.optString("picture"));
            noti.setType("" + jsonObject.optString("Type"));
            noti.setTime(jsonObject.optString("LastModified"));
            noti.setTransactionID(jsonObject.optString("TransactionID"));
            noti.setUser_Id(jobj.optString("userid"));
            Util.sendNotificationDetails(mContext, noti);

            ////// Setting Alarm
//            int alarmId = new Random().nextInt(Integer.MAX_VALUE); /* Dynamically assign alarm ids for multiple alarms */

            Calendar calendar = Calendar.getInstance();
            Calendar currentTime = Calendar.getInstance();

            Date xDate = HandleResponseWorkItems.convertToDate(Util.utcToLocalTime(jobj.optString("enddatetime")));
            calendar.setTime(xDate);
            //  calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
            Log.e("Calendar Date", "Date: " + calendar.getTime());
            if (calendar.getTime().after(currentTime.getTime())) {
                ///////

                try {
                    int alarmId = jobj.optInt("workitemid");

                    AlarmManager am = (AlarmManager) mContext
                            .getSystemService(mContext.ALARM_SERVICE);
                    currentTime.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
                    if (calendar.getTime().after(currentTime.getTime())) {

                        Intent i = new Intent(mContext, AlarmReceiver.class);
                        i.putExtra("message", jobj.optString("name") + " \n" + mContext.getString(R.string.less_than_one_hr_remains));
                        i.putExtra(WorkItem.TASK_ID, "" + jobj.optString("workitemid"));
                        i.setAction("com.bigbang.teamworks.WORK_ITEM");

                        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
                        PendingIntent sender = PendingIntent.getBroadcast(mContext, alarmId, i, 0);
                        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 10000, sender);
                        Log.e("Alarm", "Calendar Setted at " + calendar.getTime().toString());
                    }

                    Intent i = new Intent(mContext, AlarmReceiver.class);
                    i.putExtra("message", jobj.optString("name") + " \n" + mContext.getString(R.string.Time_Out_Work_Item));
                    i.putExtra(WorkItem.TASK_ID, "" + jobj.optString("workitemid"));
                    i.setAction("com.bigbang.teamworks.WORK_ITEM");
                    PendingIntent sender = PendingIntent.getBroadcast(mContext, alarmId, i, 0);
                    calendar.setTime(xDate);
                    am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 10000, sender);
                    Log.e("Alarm", "Calendar Setted at " + calendar.getTime().toString());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Remove From Approval
            if (!jobj.optString("status").equals("Pending")) {
                RemoveFromApproval(getDb(mContext), Integer.parseInt(jobj.optString("workitemid")), "" + Constant.CREATE_NEWWORKITEM);
            }

            ////////////////
            // Sending broadcast so that alerts can be displayed
            if (UpdateWorkActivity.Active) {
                Intent intent = new Intent("message_aaya");
                intent.putExtra("message", "data");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
     /*Funtion: Insert Work Item Update into Table
               Generate Notification
               Updates work item as per delegate, postpone or done
    Calls:     GCMIntentService.java, onMessage(2)
    Extra Details: It will affect both work item update as well as work item table */

    public static void HandleWorkItemUpdate(JSONObject jobj, Context mContext, boolean isNotify) {
        ContentValues taskValues = new ContentValues();
        ContentValues alertValues = new ContentValues();
        String updateType = "";
        String messageType = "", message = "";
        int id = 0;
        /// If any Attachments like File or Image then
        JSONObject attachmentObj = null;
        try {
            attachmentObj = jobj.optJSONObject("data").optJSONObject("attachmentdata");
            if (attachmentObj != null) {
                String path = attachmentObj.optString("path");
                taskValues.put(WorkTransaction.MESSAGE_LINK, path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Else Check for status if any as there isnt any status in image or files and insert it.
        try {
            JSONObject dataObj = jobj.getJSONObject("data");
            taskValues.put(WorkTransaction.STATUS, dataObj.optString("status"));
        } catch (JSONException e) {
            e.printStackTrace();
            taskValues.put(WorkTransaction.STATUS, "Pending");
        }
        // Insert Data into table depends on different conditions
        Log.e("Status update", " received :" + taskValues.getAsString(WorkTransaction.STATUS));
        try {
            JSONObject dataObj = jobj.getJSONObject("data");
            message = dataObj.optString("description");
            messageType = dataObj.optString("type");
            updateType = dataObj.optString("updatetype");
            taskValues.put(WorkTransaction.UPDATE_TYPE, updateType);

            taskValues.put(WorkTransaction.MESSAGE, message);
            taskValues.put(WorkTransaction.MESSAGE_TYPE, messageType);
            taskValues.put(WorkTransaction.TRANSACTION_CODE, dataObj.optString("workitemupdateid"));
            taskValues.put(WorkTransaction.TASK_CODE, dataObj.optString("workitemid"));
            taskValues.put(WorkTransaction.USER_CODE, dataObj.optString("userid"));
            taskValues.put(WorkTransaction.DISCRIPTION, message);
            taskValues.put(WorkTransaction.START_DATE, dataObj.optString("startdate"));
            taskValues.put(WorkTransaction.END_DATE, dataObj.optString("enddate"));
            taskValues.put(WorkTransaction.DELEGATE_TO, dataObj.optString("delegateto"));
            taskValues.put(WorkTransaction.AMOUNT, dataObj.optString("expenseamt"));
            taskValues.put(WorkTransaction.INVOICE_DATE, dataObj.optString("expensedate"));
            taskValues.put(WorkTransaction.CREATED_ON, dataObj.optString("createdon"));


            //// Putting data into alerts value for displaying it
            alertValues.put("Id", dataObj.optString("workitemid"));
            alertValues.put("Type", "Work Update");
            alertValues.put("Description", "Work Item Updated");
            try {
                id = Integer.parseInt(dataObj.optString("workitemid"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            /// If update type querry then need to set asked to
            if (updateType.equals("Query")) {
                taskValues.put(WorkTransaction.DELEGATE_TO, dataObj.optString("queryto"));
            }

            //if update type is delegate and no current user in the list then made current item as pending status. so no updates could be done.


            if (updateType.equals("Delegate")) {
                if (dataObj.optString("status").equals(mContext.getString(R.string.approved))) {
                    if (dataObj.optString("queryto").contains("" + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID))) {
                        ContentValues taskValuesUpdate = new ContentValues();
                        taskValuesUpdate.put(WorkItem.STATUS, mContext.getResources().getString(R.string.approved));
                        taskValuesUpdate.put(WorkItem.ASSIGNED_TO, dataObj.optString("delegateto"));
                        int workid = Integer.parseInt(dataObj.optString("workitemid"));
                        getDb(mContext).update(Constant.WorkItemTable, taskValuesUpdate, WorkItem.TASK_ID + " = ?", new String[]{workid + ""});
                    } else {
                        ContentValues taskValuesUpdate = new ContentValues();
                        taskValuesUpdate.put(WorkItem.STATUS, mContext.getResources().getString(R.string.delegated));
                        taskValuesUpdate.put(WorkItem.ASSIGNED_TO, dataObj.optString("delegateto"));
                        int workid = Integer.parseInt(dataObj.optString("workitemid"));
                        getDb(mContext).update(Constant.WorkItemTable, taskValuesUpdate, WorkItem.TASK_ID + " = ?", new String[]{workid + ""});
                    }
                }
            }


            /// Finding work item from table if found then need to update details or insert it
            Cursor crsr = getDb(mContext).rawQuery("select * from " + Constant.WorkTransaction + " where " + WorkTransaction.TRANSACTION_CODE + " = " + dataObj.optString("workitemupdateid"), null);
            if (crsr != null && crsr.getCount() > 0) {
                Log.e("updatenotificaiton", " in update mode");
                crsr.moveToFirst();
                getDb(mContext).update(Constant.WorkTransaction, taskValues, WorkTransaction.TRANSACTION_CODE + " = ?", new String[]{dataObj.optString("workitemupdateid") + ""});
            } else {
                getDb(mContext).insert(Constant.WorkTransaction, null, taskValues);
                // If update work is not active or if active then same work item not opened then insert alerts and broadcast in case list is open
                if (isNotify) {
                    if (!UpdateWorkActivity.Active || (id != UpdateWorkActivity.WORK_ID)) {
                        Log.e("pk", ">>" + getDb(mContext).insert(Constant.AlertsTable, null, alertValues));
                    }
                }
            }
            crsr.close();

            // If task is of postponed then need to upgrade workitem table but in case it is approved
            if (updateType.equals("Postponed")) {
                if (dataObj.optString("status").equals("Approved")) {
                    ContentValues taskValuesUpdate = new ContentValues();
                    taskValuesUpdate.put(WorkItem.START_DATE, dataObj.optString("startdate"));
                    taskValuesUpdate.put(WorkItem.END_DATE, dataObj.optString("enddate"));
                    int workid = Integer.parseInt(dataObj.optString("workitemid"));
                    getDb(mContext).update(Constant.WorkItemTable, taskValuesUpdate, WorkItem.TASK_ID + " = ?", new String[]{workid + ""});
                }
            }
            //// Update value of work item when task is done
            if (updateType.equals("Done")) {
                if (dataObj.optString("status").equals("Approved")) {
                    ContentValues ValuesUpdate = new ContentValues();
                    int workid = Integer.parseInt(dataObj.optString("workitemid"));
                    ValuesUpdate.put(WorkItem.STATUS, "Done");
                    getDb(mContext).update(Constant.WorkItemTable, ValuesUpdate, WorkItem.TASK_ID + " = ?", new String[]{workid + ""});
                }
            }
            String notiText = "Task Update :";
            notiText = jobj.optString("message");
            // If activity already open then send broadcast else generate notification

            int Status = jobj.optInt("Status");
            if (Status != 1) {// Sending information for notification table
                NotificationInfo noti = new NotificationInfo();
                noti.setId(Integer.parseInt(dataObj.optString("workitemid")));
                noti.setTitle(dataObj.optString("firstName") + " " + dataObj.optString("lastName"));
                noti.setDescription("" + jobj.optString("message"));
                noti.setImage_Url("" + dataObj.optString("picture"));
                noti.setType("" + jobj.optString("Type"));
//            noti.setTime(Util.GetDate());
                noti.setTime(jobj.optString("LastModified"));
                noti.setTransactionID(jobj.optString("TransactionID"));
                noti.setUser_Id(dataObj.optString("userid"));
                Util.sendNotificationDetails(mContext, noti);
                Log.e("jsontype", "Type:" + noti.getType() + " trID:" + noti.getTransactionID());
            }
            if (!dataObj.optString("status").equals("Pending")) {
                RemoveFromApproval(getDb(mContext), Integer.parseInt(dataObj.optString("workitemupdateid")), "" + Constant.UPDATE_WORKITEM);
                //RemoveFromApproval(getDb(mContext), Integer.parseInt(dataObj.optString("workitemid")), "" + Constant.EDIT_WORKITEM_APPROVAL);
            }

            if (isNotify) {
                if (UpdateWorkActivity.Active) {
                    Intent intent = new Intent("message_aaya");
                    intent.putExtra("message", "data");
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                } else {
                    generateUpdateNotification(mContext, notiText, jobj.getJSONObject("data").getInt("workitemid"), UpdateWorkActivity.class);
                }

                if (ActiveTasksFragment.Active || ArchivedTasksFragment.Active || PendingTasksFragment.Active) {
                    Intent intent = new Intent("workitem_created");
                    intent.putExtra("message", "data");
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void HandleAttachment(JSONObject job, Context mContext) {
        ContentValues taskValues = new ContentValues();
        try {
            JSONObject dataObj = job.getJSONObject("data");
            taskValues.put(Attachment.ATTACHMENT_ID, dataObj.optString("attachmentid"));
            taskValues.put(Attachment.PATH, dataObj.optString("path"));
            taskValues.put(Attachment.USER_ID, dataObj.optString("userid"));
            taskValues.put(Attachment.WORKITEM_ID, dataObj.optString("workitemid"));
            taskValues.put(Attachment.UPLOADEDDOWNLOADED, "false");

            String ATTACHMENT_ID = dataObj.optString("attachmentid");
            boolean flag = true;
            try {
                if (ATTACHMENT_ID.length() > 0) {
                    Cursor cursor = getDb(mContext).rawQuery("select * from " + Constant.AttachmentTable + " where " + Attachment.ATTACHMENT_ID + " like \"" + ATTACHMENT_ID + "\"", null);
                    Log.e("cursor", ">>" + cursor);
                    if (cursor != null && cursor.getCount() > 0) flag = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.e("AttachmentTable", ">>" + flag + ":" + ATTACHMENT_ID);
            if (flag)
                getDb(mContext).insert(Constant.AttachmentTable, null, taskValues);
            else
                getDb(mContext).update(Constant.AttachmentTable, taskValues, Attachment.ATTACHMENT_ID + " like \"" + ATTACHMENT_ID + "\"", null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void HandleNewProject(JSONObject jObj, Context context, Boolean flag, boolean isNotify) {
        try {
            ContentValues projectValues = new ContentValues();
            JSONObject data = jObj.getJSONObject("data");
            projectValues.put(Project.USER_CODE, data.optString("creatorid"));
            projectValues.put(Project.PROJECT_NAME, data.optString("name"));
            projectValues.put(Project.DESCRIPTION, data.optString("description"));
            projectValues.put(Project.PRIORITY, data.optString("priority"));
            projectValues.put(Project.PROJECT_ID, data.optString("projectid"));
            projectValues.put(Project.START_DATE, data.optString("startdatetime"));
            projectValues.put(Project.END_DATE, data.optString("enddatetime"));
            projectValues.put(Project.ESTIMATED_TIME, data.optString("estimateddatetime"));
            projectValues.put(Project.ASSIGNED_TO, data.optString("tolist"));
            projectValues.put(Project.CC_TO, data.optString("cclist"));
            projectValues.put(Project.PROJECT_IMAGE, data.optString("imageURL"));
            projectValues.put(Project.STATUS, data.optString("status"));
            projectValues.put(Project.OWNER, data.optString("ownerid"));

//            TeamWorkApplication.db.insert(Constant.ProjectTable, null, projectValues);
            Cursor crsr = getDb(context).rawQuery("select * from " + Constant.ProjectTable + " where " + Project.PROJECT_ID + " = " + data.optString("projectid"), null);
            if (crsr != null && crsr.getCount() > 0) {
                crsr.moveToFirst();
                getDb(context).update(Constant.ProjectTable, projectValues, Project.PROJECT_ID + " = ?", new String[]{data.optString("projectid") + ""});
            } else {
                getDb(context).insert(Constant.ProjectTable, null, projectValues);
            }
            crsr.close();
            // Remove From Approval
            if (!data.optString("status").equals("Pending")) {
                RemoveFromApproval(getDb(context), Integer.parseInt(data.optString("projectid")), "" + Constant.CREATE_NEWPRJECT);
            }
            if (flag)
            // Sending information for notification table
            {
                NotificationInfo noti = new NotificationInfo();
                noti.setId(Integer.parseInt(data.optString("projectid")));
                noti.setTitle(data.optString("firstName") + " " + data.optString("lastName"));
                noti.setDescription("" + jObj.optString("message"));
                noti.setImage_Url("" + data.optString("picture"));
                noti.setType("" + jObj.optString("Type"));
                noti.setTime(jObj.optString("LastModified"));
                noti.setTransactionID(jObj.optString("TransactionID"));
                noti.setUser_Id(data.optString("creatorid"));
                Util.sendNotificationDetails(context, noti);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (ProjectFragment.Active) {
            Intent intent = new Intent("project_created");
            intent.putExtra("message", "data");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

        if (isNotify) {
            try {

                String roleId = Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    generateNotificationFragment(context, jObj.optString("message"), AdminDashboardNewActivity.class, 1);
                } else if (Arrays.asList("3").contains(roleId)) {
                    generateNotificationFragment(context, jObj.optString("message"), ManagerDashboardNewActivity.class, 1);
                } else if (Arrays.asList("4").contains(roleId)) {
                    generateNotificationFragment(context, jObj.optString("message"), UserDashboardNewActivity.class, 1);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public static void HandleModifiedProject(JSONObject jObj, Context context, Boolean flag, boolean isNotify) {
        try {
            ContentValues projectValues = new ContentValues();
            JSONObject data = jObj.getJSONObject("data");
            projectValues.put(Project.USER_CODE, data.optString("creatorid"));
            projectValues.put(Project.PROJECT_NAME, data.optString("name"));
            projectValues.put(Project.DESCRIPTION, data.optString("description"));
            projectValues.put(Project.PRIORITY, data.optString("priority"));
            projectValues.put(Project.PROJECT_ID, data.optString("projectid"));
            projectValues.put(Project.START_DATE, data.optString("startdatetime"));
            projectValues.put(Project.END_DATE, data.optString("enddatetime"));
            projectValues.put(Project.ESTIMATED_TIME, data.optString("estimateddatetime"));
            projectValues.put(Project.ASSIGNED_TO, data.optString("tolist"));
            projectValues.put(Project.CC_TO, data.optString("cclist"));
            projectValues.put(Project.PROJECT_IMAGE, data.optString("imageURL"));
            projectValues.put(Project.STATUS, data.optString("status"));
            projectValues.put(Project.OWNER, data.optString("ownerid"));

            Log.e("ProjectTable", ">>" + data.optString("projectid"));
            Cursor crsr = getDb(context).rawQuery("select * from " + Constant.ProjectTable + " where " + Project.PROJECT_ID + " = " + data.optString("projectid"), null);
            if (crsr != null && crsr.getCount() > 0) {
                Log.e("crsr", ">>" + crsr.getCount());
                crsr.moveToFirst();
                getDb(context).update(Constant.ProjectTable, projectValues, Project.PROJECT_ID + " = ?", new String[]{data.optString("projectid") + ""});
            } else {
                getDb(context).insert(Constant.ProjectTable, null, projectValues);
            }
            crsr.close();
            // Remove From Approval
            if (!data.optString("status").equals("Pending")) {
                RemoveFromApproval(getDb(context), Integer.parseInt(data.optString("projectid")), "" + Constant.CREATE_NEWPRJECT);
            }
            if (flag)
            // Sending information for notification table
            {
                NotificationInfo noti = new NotificationInfo();
                noti.setId(Integer.parseInt(data.optString("projectid")));
                noti.setTitle(data.optString("firstName") + " " + data.optString("lastName"));
                noti.setDescription("" + jObj.optString("message"));
                noti.setImage_Url("" + data.optString("picture"));
                noti.setType("" + jObj.optString("Type"));
                noti.setTime(jObj.optString("LastModified"));
                noti.setTransactionID(jObj.optString("TransactionID"));
                noti.setUser_Id(data.optString("creatorid"));
                Util.sendNotificationDetails(context, noti);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (isNotify) {
            try {

                String roleId = Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    generateNotificationFragment(context, jObj.optString("message"), AdminDashboardNewActivity.class, 1);
                } else if (Arrays.asList("3").contains(roleId)) {
                    generateNotificationFragment(context, jObj.optString("message"), ManagerDashboardNewActivity.class, 1);
                } else if (Arrays.asList("4").contains(roleId)) {
                    generateNotificationFragment(context, jObj.optString("message"), UserDashboardNewActivity.class, 1);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (ProjectFragment.Active) {
            Intent intent = new Intent("project_created");
            intent.putExtra("message", "data");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    /////////////////////APPROVAL REQUEST FUNCTIONS//////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    /// Approval Request for New Project
    public static void HandleNewProjectapproval(JSONObject jObj, Context context, boolean isNotify) {
        // Add into database of project

        try {
            JSONObject jsonobj = jObj.getJSONObject("data");
            ContentValues taskValues = new ContentValues();
            taskValues.put(Approval.ID, jsonobj.optString("projectid"));
            taskValues.put(Approval.IMAGE, jsonobj.optString("picture"));
            taskValues.put(Approval.USERID, jsonobj.optString("creatorid"));
            taskValues.put(Approval.TITLE, jsonobj.optString("firstName") + " " + jsonobj.optString("lastName"));
            taskValues.put(Approval.DESCRIPTION, jObj.optString("message"));
            taskValues.put(Approval.TYPE, "" + Constant.APPROVAL_NEWPRJECT);
            taskValues.put(Approval.TIME, "" + jObj.optString("LastModified"));
            taskValues.put(Approval.TRANSACTIONID, "" + jObj.optString("TransactionID"));
            taskValues.put(Approval.STATUS, "" + jObj.optString("Status"));

            String TransactionID = jObj.optString("TransactionID");
            int type = jObj.optInt("Type");

            boolean flag = true;
            try {
                if (TransactionID.length() > 0) {
                    Cursor cursor = getDb(context).rawQuery("select * from " + Constant.ApprovalsTable + " where TransactionID like \"" + TransactionID + "\" AND type like \"" + type + "\"", null);
                    if (cursor != null && cursor.getCount() > 0) flag = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (flag) {
                getDb(context).insert(Constant.ApprovalsTable, null, taskValues);
            } else {
                getDb(context).update(Constant.ApprovalsTable, taskValues, "TransactionID like \"" + TransactionID + "\"", null);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (ProjectFragment.Active) {
            Intent intent = new Intent("project_created");
            intent.putExtra("message", "data");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

        if (jObj.optInt("Status") == 1) HandleNewProject(jObj, context, false, false);
        else isNotify = false;

        if (isNotify) {
            String msg = null;
            try {
                msg = jObj.optString("message");
                try {

                    String roleId = Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID);
                    if (Arrays.asList("1", "2").contains(roleId)) {
                        generateNotificationFragment(context, context.getString(R.string.approval_required_for_new_project) + " " + msg, AdminDashboardNewActivity.class, 3);
                    } else if (Arrays.asList("3").contains(roleId)) {
                        generateNotificationFragment(context, context.getString(R.string.approval_required_for_new_project) + " " + msg, ManagerDashboardNewActivity.class, 3);
                    } else if (Arrays.asList("4").contains(roleId)) {
                        generateNotificationFragment(context, context.getString(R.string.approval_required_for_new_project) + " " + msg, UserDashboardNewActivity.class, 3);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    // Approval Request for Work Item Update
    public static void HandleWorkUpdateApproval(JSONObject jsonObject, Context mContext, boolean isNotify) {
        String msg = null;
        JSONObject jObj = null;
        try {
            msg = jsonObject.optString("message");
            jObj = jsonObject.getJSONObject("data");
            ContentValues taskValues = new ContentValues();
            taskValues.put(Approval.ID, jObj.optString("workitemupdateid"));
            taskValues.put(Approval.USERID, jObj.optString("userid"));
            taskValues.put(Approval.TITLE, jObj.optString("firstName") + " " + jObj.optString("lastName"));
            taskValues.put(Approval.IMAGE, jObj.optString("picture"));
            taskValues.put(Approval.DESCRIPTION, jsonObject.optString("message"));
            taskValues.put(Approval.TYPE, "" + Constant.APPROVE_WORKITEMUPDATE); //"WorkItemUpdate"
            taskValues.put(Approval.TIME, "" + jsonObject.optString("LastModified"));
            taskValues.put(Approval.TRANSACTIONID, "" + jsonObject.optString("TransactionID"));
            taskValues.put(Approval.STATUS, "" + jsonObject.optString("Status"));
            Cursor crsr = getDb(mContext).rawQuery("select Task_Image from " + Constant.WorkItemTable + " where " + WorkItem.TASK_ID + " = " + jObj.optString("workitemid"), null);
            crsr.close();

            String TransactionID = jsonObject.optString("TransactionID");
            int type = jsonObject.optInt("Type");

            boolean flag = true;
            try {
                if (TransactionID.length() > 0) {
                    Cursor cursor = getDb(mContext).rawQuery("select * from " + Constant.ApprovalsTable + " where TransactionID like \"" + TransactionID + "\" AND type like \"" + type + "\"", null);
                    if (cursor != null && cursor.getCount() > 0) flag = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (flag) {
                getDb(mContext).insert(Constant.ApprovalsTable, null, taskValues);
            } else {
                getDb(mContext).update(Constant.ApprovalsTable, taskValues, "TransactionID like \"" + TransactionID + "\"", null);
            }

            if (jsonObject.optInt("Status") != 1) isNotify = false;
            if (isNotify) {
                String roleId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    generateNotificationFragment(mContext, mContext.getString(R.string.approval_required_for_workitem_update) + ": " + msg, AdminDashboardNewActivity.class, 3);
                } else if (Arrays.asList("3").contains(roleId)) {
                    generateNotificationFragment(mContext, mContext.getString(R.string.approval_required_for_workitem_update) + ": " + msg, ManagerDashboardNewActivity.class, 3);
                } else if (Arrays.asList("4").contains(roleId)) {
                    generateNotificationFragment(mContext, mContext.getString(R.string.approval_required_for_workitem_update) + ": " + msg, UserDashboardNewActivity.class, 3);
                }

                Intent iAttendance = new Intent();
                iAttendance.setAction(mContext.ACTIVITY_SERVICE);
                mContext.sendBroadcast(iAttendance);
            }

        } catch (JSONException e1) {
            e1.printStackTrace();
        }

    }

    //Approval Request for Edit Work Item
    public static void HandleEditWorkItemApproval(JSONObject jsonObj, Context mContext, boolean showNotification) {
        Log.e("jsonObj", ">>" + jsonObj);
        String msg = null;
        JSONObject jObj = null;
        try {
            msg = jsonObj.optString("message");
            jObj = jsonObj.getJSONObject("data");
            ContentValues taskValues = new ContentValues();
            taskValues.put(Approval.ID, jObj.optString("workitemid"));
            taskValues.put(Approval.IMAGE, jObj.optString("picture"));
            taskValues.put(Approval.USERID, jObj.optString("userid"));
            taskValues.put(Approval.TITLE, jObj.optString("firstName") + " " + jObj.optString("lastName"));
            taskValues.put(Approval.DESCRIPTION, jsonObj.optString("message"));
            taskValues.put(Approval.TYPE, "" + jsonObj.optInt("Type")); //"WorkItemUpdate"
            taskValues.put(Approval.TIME, "" + jsonObj.optString("LastModified"));
            taskValues.put(Approval.TRANSACTIONID, "" + jsonObj.optString("TransactionID"));
            taskValues.put(Approval.STATUS, "" + jsonObj.optString("Status"));
            taskValues.put("data", "" + jsonObj.optJSONObject("data"));

            String TransactionID = jsonObj.optString("TransactionID");
            int type = jsonObj.optInt("Type");

            boolean flag = true;
            try {
                if (TransactionID.length() > 0) {
                    Cursor cursor = getDb(mContext).rawQuery("select * from " + Constant.ApprovalsTable + " where TransactionID like \"" + TransactionID + "\" AND type like \"" + type + "\"", null);
                    if (cursor != null && cursor.getCount() > 0) flag = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (flag) {
                getDb(mContext).insert(Constant.ApprovalsTable, null, taskValues);
            } else {
                getDb(mContext).update(Constant.ApprovalsTable, taskValues, "TransactionID like \"" + TransactionID + "\"", null);
            }

            if (jsonObj.optInt("Status") != 1) showNotification = false;
            if (showNotification) {
                String roleId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    generateNotificationFragment(mContext, mContext.getString(R.string.approval_required_for_workitem_modification) + ": " + msg, AdminDashboardNewActivity.class, 3);
                } else if (Arrays.asList("3").contains(roleId)) {
                    generateNotificationFragment(mContext, mContext.getString(R.string.approval_required_for_workitem_modification) + ": " + msg, ManagerDashboardNewActivity.class, 3);
                } else if (Arrays.asList("4").contains(roleId)) {
                    generateNotificationFragment(mContext, mContext.getString(R.string.approval_required_for_workitem_modification) + ": " + msg, UserDashboardNewActivity.class, 3);
                }
            }
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        Intent iAttendance = new Intent();
        iAttendance.setAction(mContext.ACTIVITY_SERVICE);
        mContext.sendBroadcast(iAttendance);
    }

    //// Approval Request for New Work Item
    public static void HandleNewWorkItemApproval(JSONObject jsonObject, Context mContext, boolean isNotify) {
        //We are going to insert into approvals table
        // as well as work items table
        // as well as notifications table

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();

        String msg = null;
        JSONObject jObj = null;
        try {
            msg = jsonObject.optString("message");
            jObj = jsonObject.getJSONObject("data");
            ContentValues taskValues = new ContentValues();
            taskValues.put(Approval.ID, jObj.optString("workitemid"));
            taskValues.put(Approval.USERID, jObj.optString("userid"));
            taskValues.put(Approval.TITLE, jObj.optString("firstName") + " " + jObj.optString("lastName"));
            taskValues.put(Approval.DESCRIPTION, jsonObject.optString("message"));
            taskValues.put(Approval.TYPE, "" + Constant.CREATE_NEWWORKITEM);//"WorkItemCreate"
            taskValues.put(Approval.IMAGE, jObj.optString("picture"));
            taskValues.put(Approval.TIME, "" + jsonObject.optString("LastModified"));
            taskValues.put(Approval.TRANSACTIONID, "" + jsonObject.optString("TransactionID"));
            taskValues.put(Approval.STATUS, "" + jsonObject.optString("Status"));

            String TransactionID = jsonObject.optString("TransactionID");
            int type = jsonObject.optInt("Type");

            boolean flag = true;
            try {
                if (TransactionID.length() > 0) {
                    Cursor cursor = getDb(mContext).rawQuery("select * from " + Constant.ApprovalsTable + " where TransactionID like \"" + TransactionID + "\" AND type like \"" + type + "\"", null);
                    if (cursor != null && cursor.getCount() > 0) flag = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            Log.e("flag:16", ">>" + flag + ":" + TransactionID);
            if (flag) {
                db.insert(Constant.ApprovalsTable, null, taskValues);
            } else {
                db.update(Constant.ApprovalsTable, taskValues, "TransactionID like \"" + TransactionID + "\"", null);
            }

            //// Show in work items
            if (jsonObject.optInt("Status") == 1) InsertIntoWorkItem(jObj, mContext);
            else isNotify = false;

            if (isNotify) {
                if (ActiveTasksFragment.Active || ArchivedTasksFragment.Active || PendingTasksFragment.Active) {
                    Intent intent = new Intent("workitem_created");
                    intent.putExtra("message", "data");
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                }

                try {
                    String roleId = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_ROLE_ID);
                    if (Arrays.asList("1", "2").contains(roleId)) {
                        generateNotificationFragment(mContext, msg, AdminDashboardNewActivity.class, 3);
                    } else if (Arrays.asList("3").contains(roleId)) {
                        generateNotificationFragment(mContext, msg, ManagerDashboardNewActivity.class, 3);
                    } else if (Arrays.asList("4").contains(roleId)) {
                        generateNotificationFragment(mContext, msg, UserDashboardNewActivity.class, 3);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent iAttendance = new Intent();
                iAttendance.setAction(mContext.ACTIVITY_SERVICE);
                mContext.sendBroadcast(iAttendance);
            }

        } catch (JSONException e1) {
            e1.printStackTrace();
        }

    }

    public static void HandleEditProjectApproval(JSONObject jsonObject, Context context, boolean isNotify) {

        String msg = null;
        JSONObject jObj = null;
        try {
            msg = jsonObject.optString("message");
            jObj = jsonObject.getJSONObject("data");
            ContentValues taskValues = new ContentValues();
            taskValues.put(Approval.ID, jObj.optString("projectid"));
//            taskValues.put(Approval.USERID, jsonObject.optString("UserID"));
            taskValues.put(Approval.USERID, jObj.optString("userid"));
            taskValues.put(Approval.TITLE, jObj.optString("firstName") + " " + jObj.optString("lastName"));
            taskValues.put(Approval.DESCRIPTION, jsonObject.optString("message"));
            taskValues.put(Approval.TYPE, "" + Constant.EDIT_PROJECT_APPROVAL); //"WorkItemUpdate"
            taskValues.put(Approval.STATUS, "" + jsonObject.optString("Status"));
            taskValues.put(Approval.IMAGE, jObj.optString("picture"));
            taskValues.put("Time", "" + jsonObject.optString("LastModified"));
            taskValues.put("TransactionID", "" + jsonObject.optString("TransactionID"));
            taskValues.put("data", "" + jsonObject.optJSONObject("data"));

            String TransactionID = jsonObject.optString("TransactionID");
            int type = jsonObject.optInt("Type");

            boolean flag = true;
            try {
                if (TransactionID.length() > 0) {
                    Cursor cursor = getDb(context).rawQuery("select * from " + Constant.ApprovalsTable + " where TransactionID like \"" + TransactionID + "\" AND type like \"" + type + "\"", null);
                    if (cursor != null && cursor.getCount() > 0) flag = false;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (flag) {
                getDb(context).insert(Constant.ApprovalsTable, null, taskValues);
            } else {
                getDb(context).update(Constant.ApprovalsTable, taskValues, "TransactionID like \"" + TransactionID + "\"", null);
            }

            if (jsonObject.optInt("Status") != 1) isNotify = false;
            if (isNotify) {
                String roleId = Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    generateNotificationFragment(context, context.getString(R.string.approval_required_for_project_modification) + ": " + msg, AdminDashboardNewActivity.class, 3);
                } else if (Arrays.asList("3").contains(roleId)) {
                    generateNotificationFragment(context, context.getString(R.string.approval_required_for_project_modification) + ": " + msg, ManagerDashboardNewActivity.class, 3);
                } else if (Arrays.asList("4").contains(roleId)) {
                    generateNotificationFragment(context, context.getString(R.string.approval_required_for_project_modification) + ": " + msg, UserDashboardNewActivity.class, 3);
                }

                Intent iAttendance = new Intent();
                iAttendance.setAction(context.ACTIVITY_SERVICE);
                context.sendBroadcast(iAttendance);
            }

        } catch (JSONException e1) {
            e1.printStackTrace();
        }

    }

    /// Remove from Approvals table
    public static void RemoveFromApproval(SQLiteDatabase db, int id, String type) {
        try {
            //db.delete(Constant.ApprovalsTable, Approval.ID + "=" + id + " AND " + Approval.TYPE + " like " + "\'" + type + "\'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    /////////////////////APPROVAL REJECTION FUNCTIONS//////////////////////////
    ///////////////////////////////////////////////////////////////////////////
    public static void HandleNewProjectRejection(JSONObject jObj, Context context, boolean isNotify) {

        String msg = null;
        try {
            JSONObject jobj = jObj.getJSONObject("data");
            msg = jobj.optString("projectid");
            ContentValues taskValues = new ContentValues();
            taskValues.put(Project.STATUS, "Rejected");
            getDb(context).update(Constant.ProjectTable, taskValues, Project.PROJECT_ID + " = ?", new String[]{msg + ""});
            RemoveFromApproval(getDb(context), Constant.CREATE_NEWPRJECT, msg);


            NotificationInfo noti = new NotificationInfo();
            noti.setId(Integer.parseInt(jobj.optString("projectid")));
            noti.setTitle(jobj.optString("firstName") + " " + jobj.optString("lastName"));
            noti.setDescription("" + jObj.optString("message"));
            noti.setImage_Url("" + jobj.optString("picture"));
            noti.setType("" + jObj.optString("Type"));
//            noti.setTime(Util.GetDate());
            noti.setTime(jObj.optString("LastModified"));
            noti.setTransactionID(jObj.optString("TransactionID"));

            noti.setUser_Id(jobj.optString("creatorid"));
            Util.sendNotificationDetails(context, noti);

            if (ProjectFragment.Active) {
                Intent intent = new Intent("project_created");
                intent.putExtra("message", "data");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }

            if (isNotify) {
                String roleId = Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    generateNotificationFragment(context, jObj.optString("message"), AdminDashboardNewActivity.class, 1);
                } else if (Arrays.asList("3").contains(roleId)) {
                    generateNotificationFragment(context, jObj.optString("message"), ManagerDashboardNewActivity.class, 1);
                } else if (Arrays.asList("4").contains(roleId)) {
                    generateNotificationFragment(context, jObj.optString("message"), UserDashboardNewActivity.class, 1);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void HandleNewWorkItemRejection(JSONObject jobj, Context context, boolean isNotify) {

        try {
            JSONObject jObjData = jobj.getJSONObject("data");
            String msg = jObjData.optString("workitemid");
            ContentValues taskValues = new ContentValues();
            taskValues.put(WorkItem.STATUS, "Rejected");
            getDb(context).update(Constant.WorkItemTable, taskValues, WorkItem.TASK_ID + " = ?", new String[]{msg + ""});
            // Sending information for notification table
            NotificationInfo noti = new NotificationInfo();
            noti.setId(Integer.parseInt(jObjData.optString("workitemid")));
            noti.setTitle(jObjData.optString("firstName") + " " + jObjData.optString("lastName"));
            noti.setDescription("" + jobj.optString("message"));
            noti.setImage_Url("" + jObjData.optString("picture"));
            noti.setType("" + jobj.optString("Type"));
            noti.setTime(jobj.optString("LastModified"));
            noti.setTransactionID(jobj.optString("TransactionID"));

            noti.setUser_Id(jObjData.optString("userid"));
            Util.sendNotificationDetails(context, noti);

            String notiTitle = jobj.optString("message");

            if (isNotify) {
                String roleId = Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    generateNotificationFragment(context, "" + notiTitle, AdminDashboardNewActivity.class, 0);
                } else if (Arrays.asList("3").contains(roleId)) {
                    generateNotificationFragment(context, "" + notiTitle, ManagerDashboardNewActivity.class, 0);
                } else if (Arrays.asList("4").contains(roleId)) {
                    generateNotificationFragment(context, "" + notiTitle, UserDashboardNewActivity.class, 0);
                }
            }

            Intent intent = new Intent("workitem_created");
            intent.putExtra("message", "data");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            RemoveFromApproval(getDb(context), Constant.CREATE_NEWWORKITEM, jObjData.optString("workitemid"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void HandleWorkUpdateRejection(String message, Context mContext, boolean isNotify) {
        JSONObject jObj = null, main = null;
        try {
            main = new JSONObject("" + message);
            jObj = main.getJSONObject("data");
            String msg = main.optString("message");
            String TRANSACTION_CODE = jObj.optString("workitemupdateid");
            ContentValues taskValues = new ContentValues();
            taskValues.put(WorkTransaction.STATUS, "Rejected");
            getDb(mContext).update(Constant.WorkTransaction, taskValues, WorkTransaction.TRANSACTION_CODE + " = ?", new String[]{TRANSACTION_CODE + ""});
            // Sending information for notification table
            NotificationInfo noti = new NotificationInfo();
            noti.setId(Integer.parseInt(jObj.optString("workitemupdateid")));
            noti.setTitle(jObj.optString("firstName") + " " + jObj.optString("lastName"));
            noti.setDescription("" + main.optString("message"));
            noti.setImage_Url("" + jObj.optString("picture"));

            noti.setType("" + main.optString("Type"));
//            noti.setTime(Util.GetDate());
            noti.setTime(main.optString("LastModified"));
            noti.setTransactionID(main.optString("TransactionID"));

            noti.setUser_Id(jObj.optString("userid"));
            Util.sendNotificationDetails(mContext, noti);
            if (UpdateWorkActivity.Active) {
                Intent intent = new Intent("message_aaya");
                intent.putExtra("message", "data");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            } else {
                if (isNotify)
                    generateUpdateNotification(mContext, msg, jObj.optInt("workitemid"), UpdateWorkActivity.class);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void HandleEditProjectRejected(JSONObject jObj, Context context, boolean isNotify) {
        Log.e("Project Rejected", "" + jObj.toString());
        String id = null, message = "";
        try {
            JSONObject jobj = jObj.getJSONObject("data");
            id = jobj.optString("projectid");
            message = jObj.optString("message");
//            ContentValues taskValues = new ContentValues();
//            taskValues.put(Project.STATUS, "Rejected");
            // getDb(context).update(Constant.ProjectTable, taskValues, Project.PROJECT_ID + " = ?", new String[]{msg + ""});
            //RemoveFromApproval(getDb(context), Constant.EDIT_PROJECT_APPROVAL, id);
            // Sending information for notification table
            NotificationInfo noti = new NotificationInfo();
            noti.setId(Integer.parseInt(id));
            noti.setTitle(jobj.optString("firstName") + " " + jobj.optString("lastName"));
            noti.setDescription("" + jObj.optString("message"));
            noti.setImage_Url("" + jobj.optString("picture"));
            noti.setType("" + jObj.optString("Type"));
//            noti.setTime(Util.GetDate());
            noti.setTime(jObj.optString("LastModified"));
            noti.setTransactionID(jObj.optString("TransactionID"));

            noti.setUser_Id(jobj.optString("creatorid"));
            Util.sendNotificationDetails(context, noti);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (isNotify) {
            String roleId = Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID);
            if (Arrays.asList("1", "2").contains(roleId)) {
                generateNotificationFragment(context, message, AdminDashboardNewActivity.class, 1);
            } else if (Arrays.asList("3").contains(roleId)) {
                generateNotificationFragment(context, message, ManagerDashboardNewActivity.class, 1);
            } else if (Arrays.asList("4").contains(roleId)) {
                generateNotificationFragment(context, message, UserDashboardNewActivity.class, 1);
            }
        }

    }

    ///////////////////////////////////////////////////////////////////////////
    /////////////////////UTILITY FUNCTIONS/////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////

    public static SQLiteDatabase getDb(Context mContext) {
        if (TeamWorkApplication.db == null) {
            SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
            helper.createDatabase();
            TeamWorkApplication.db = helper.openDatabase();
        }

        return TeamWorkApplication.db;
    }

    public static void InsertIntoWorkItem(JSONObject jobj, Context mContext) {

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();

        ContentValues taskValues = new ContentValues();
        try {
            Log.e("Inside workItemGCM", "workitemId is:- " + jobj.optString("workitemid"));
            taskValues.put(WorkItem.TASK_ID, "" + jobj.optString("workitemid"));
            taskValues.put(WorkItem.USER_CODE, "" + jobj.optString("userid"));
            taskValues.put(WorkItem.TASK_NAME, "" + jobj.optString("name"));
            taskValues.put(WorkItem.DESCRIPTION, "" + jobj.optString("description"));
            taskValues.put(WorkItem.BUDGET, "" + jobj.optString("budget"));
            taskValues.put(WorkItem.PRIORITY, "" + jobj.optString("priority"));
            taskValues.put(WorkItem.ATTACHMENTS, "" + jobj.optString("attachmentlist"));
            taskValues.put(WorkItem.WORK_LOCATION, "" + jobj.optString("location"));
            taskValues.put(WorkItem.WORK_LONGITUDE, "" + jobj.optString("longitude"));
            taskValues.put(WorkItem.WORK_LATITUDE, "" + jobj.optString("latitude"));
            taskValues.put(WorkItem.START_DATE, "" + jobj.optString("startdatetime"));
            taskValues.put(WorkItem.END_DATE, "" + jobj.optString("enddatetime"));
            taskValues.put(WorkItem.ESTIMATED_TIME, "" + jobj.optString("estimatedtime"));
            taskValues.put(WorkItem.ASSIGNED_TO, "" + jobj.optString("tolist"));
            taskValues.put(WorkItem.CC_TO, "" + jobj.optString("cclist"));
            taskValues.put(WorkItem.TASK_TYPE, "" + jobj.optString("worktype"));
            taskValues.put(WorkItem.PROJECT_CODE, "" + jobj.optInt("projectid"));
            taskValues.put(WorkItem.TASK_AFTER, "" + jobj.optString("dependenton"));
            taskValues.put(WorkItem.FREQUENCY, "" + jobj.optString("frequency"));
            taskValues.put(WorkItem.DAYCODES_SELECTED, "" + jobj.optString("daycodes"));
            taskValues.put(WorkItem.CUSTOMER_NAME, "" + jobj.optString("customername"));
            taskValues.put(WorkItem.CUSTOMER_CONTACT, "" + jobj.optString("customerno"));
            taskValues.put(WorkItem.CUSTOMER_TYPE, "" + jobj.optString("customertype"));
            taskValues.put(WorkItem.DUE_DATE, "" + jobj.optString("duedate"));
            taskValues.put(WorkItem.PAST_HISTORY, "" + jobj.optString("pasthistory"));
            taskValues.put(WorkItem.VENDOR_PREFERENCE, "" + jobj.optString("vendorpreferrance"));
            taskValues.put(WorkItem.VENDOR_NAME, "" + jobj.optString("vendorname"));
            taskValues.put(WorkItem.ADVANCE_PAID, "" + jobj.optString("advancepaid"));
            taskValues.put(WorkItem.INVOICE_AMOUNT, "" + jobj.optString("invoiceamt"));
            taskValues.put(WorkItem.INVOICE_DATE, "" + jobj.optString("invoicedate"));
            taskValues.put(WorkItem.OUTSTANDING_AMT, "" + jobj.optString("outstandingamt"));
            taskValues.put(WorkItem.TASK_IMAGE, "" + jobj.optString("imageURL"));
            taskValues.put(WorkItem.STATUS, "" + jobj.optString("status"));

            Cursor crsr = getDb(mContext).rawQuery("select * from " + Constant.WorkItemTable + " where " + WorkItem.TASK_ID + " = " + jobj.optString("workitemid"), null);

            if (crsr != null && crsr.getCount() > 0) {
                Log.e("Work Item>>>>>>>>>> ", "Updated id is:- " + jobj.optString("workitemid"));
                db.update(Constant.WorkItemTable, taskValues, WorkItem.TASK_ID + " = ?", new String[]{jobj.optString("workitemid") + ""});
            } else {
                Log.e("Work Item *************", "Inserted id is:- " + jobj.optString("workitemid"));
                db.insert(Constant.WorkItemTable, null, taskValues);
            }
            crsr.close();
            if (!jobj.optString("status").equals("Pending")) {
                RemoveFromApproval(getDb(mContext), Integer.parseInt(jobj.optString("workitemid")), "" + Constant.CREATE_NEWWORKITEM);
            }
        } catch (Exception e) {
            Log.e("Inside catch", "!!!!!!!!!!!!!!!!!!!");
            e.printStackTrace();
        }
    }

    public static void InsertIntoTaskModel(JSONObject jsonObject, Context mContext) {

        try {
            JSONArray jsonArray = jsonObject.getJSONArray("data");
//                                if (jsonArray.length() >= 0) return;

            TaskDAO taskDAO = new TaskDAO(mContext);
            TaskMemberDAO taskMemberDAO = new TaskMemberDAO(mContext);

            TaskModel taskModel = null;

            Log.d("", "Inside GET_WORKITEM switch case ***");
            for (int i = 0; i < jsonArray.length(); i++) {
                Log.d("", "Inside for loop i is:- " + i);
//                                    WorkItem_GCM.InsertIntoWorkItem((JSONObject) jsonArray.get(i), LoginActivity.this);

                taskModel = new TaskModel();

                taskModel.taskID = jsonArray.getJSONObject(i).getInt("taskID");
                taskModel.companyID = jsonArray.getJSONObject(i).getInt("companyID");
                taskModel.name = jsonArray.getJSONObject(i).getString("name");
                taskModel.description = jsonArray.getJSONObject(i).getString("description");
                taskModel.taskType = jsonArray.getJSONObject(i).getString("taskType");
                taskModel.status = jsonArray.getJSONObject(i).getString("status");

                //Address
                Address address = new Address();
                address.setAddressID(jsonArray.getJSONObject(i).getJSONObject("address").getInt("AddressID"));
                address.setCountry(jsonArray.getJSONObject(i).getJSONObject("address").getString("Country"));
                address.setState(jsonArray.getJSONObject(i).getJSONObject("address").getString("State"));
                address.setCity(jsonArray.getJSONObject(i).getJSONObject("address").getString("City"));
                address.setAddressLine1(jsonArray.getJSONObject(i).getJSONObject("address").getString("AddressLine1"));
                address.setAddressLine2(jsonArray.getJSONObject(i).getJSONObject("address").getString("AddressLine1"));
                address.setPincode(jsonArray.getJSONObject(i).getJSONObject("address").getString("Pincode"));
                address.setLattitude(jsonArray.getJSONObject(i).getJSONObject("address").getString("Lattitude"));
                address.setLongitude(jsonArray.getJSONObject(i).getJSONObject("address").getString("Longitude"));

                taskModel.address = address;

                taskModel.priority = jsonArray.getJSONObject(i).getString("priority");
                taskModel.startTime = jsonArray.getJSONObject(i).getString("startTime");
                taskModel.endTime = jsonArray.getJSONObject(i).getString("endTime");
                taskModel.estimatedTime = jsonArray.getJSONObject(i).getString("estimatedTime");

                //Task Member Json Object
                taskModel.taskRight = jsonArray.getJSONObject(i).getString("taskRight");

                //Created_by JSON object
                User user = new User();
                user.setUserID(jsonArray.getJSONObject(i).getJSONObject("createdBy").getInt("userId"));
                taskModel.createdByName = jsonArray.getJSONObject(i).getJSONObject("createdBy").getString("firstName");

                taskModel.status = jsonArray.getJSONObject(i).getString("status");
                taskModel.active = jsonArray.getJSONObject(i).getBoolean("active");
                taskModel.budget = jsonArray.getJSONObject(i).getDouble("budget");

                //Approved_by JSON object
                User user2 = new User();
                user2.setUserID(jsonArray.getJSONObject(i).getJSONObject("approvedBy").getInt("userId"));
                taskModel.approvedBy = user2;

                TaskMember taskMember;
                //Assigned_to list should be in task member table
                JSONArray arrAssignedUsers = jsonArray.getJSONObject(i).getJSONArray("assignedToList");
                if (arrAssignedUsers.length() != 0)
                    for (int j = 0; j < arrAssignedUsers.length(); j++) {
                        taskMember = new TaskMember();

                        taskMember.TaskID = jsonArray.getJSONObject(i).getInt("taskID");
                        taskMember.userID = arrAssignedUsers.getJSONObject(j).getInt("userId");
                        taskMember.memberType = Constant.MemberTypeAssigned;

                        taskMemberDAO.save(taskMember);
                    }

                //CC to list should be in task member table
                JSONArray arrCCUsers = jsonArray.getJSONObject(i).getJSONArray("ccList");
                if (arrCCUsers.length() != 0)
                    for (int j = 0; j < arrCCUsers.length(); j++) {
                        taskMember = new TaskMember();

                        taskMember.TaskID = jsonArray.getJSONObject(i).getInt("taskID");
                        taskMember.userID = arrCCUsers.getJSONObject(j).getInt("userId");
                        taskMember.memberType = Constant.MemberTypeCC;

                        taskMemberDAO.save(taskMember);
                    }

                //CC to list should be in task member table
                JSONArray arrCustVend = jsonArray.getJSONObject(i).getJSONArray("taskCustVend");
                if (arrCustVend.length() != 0)
                    for (int j = 0; j < arrCCUsers.length(); j++) {
                        taskMember = new TaskMember();

                        taskMember.TaskID = jsonArray.getJSONObject(i).getInt("taskID");
                        taskMember.userID = arrCCUsers.getJSONObject(j).getInt("userId");
                        taskMember.memberType = Constant.MemberTypeCC;

                        taskMemberDAO.save(taskMember);
                    }

                taskDAO.save(taskModel);
            }
        } catch (Exception e) {
            Log.e("", "Exception: " + e);
        }
    }


    public static void InsertIntoWorkItemUpdate(JSONObject jobj, Context mContext) {
        ContentValues taskValues = new ContentValues();
        Log.e("In Insert", jobj.toString());
        String updateType = "";
        String messageType = "", message = "";
        /// If any Attachments like File or Image then
        JSONObject attachmentObj = null;
        try {
            attachmentObj = jobj.getJSONObject("attachmentdata");

            attachmentObj.getString("name");
            attachmentObj.optString("name");

            if (attachmentObj != null) {
                String path = attachmentObj.optString("path");
                taskValues.put(WorkTransaction.MESSAGE_LINK, path);
                Log.e("Noti", "Path: " + path);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Insert Data into table depends on different conditions
        try {
            JSONObject dataObj = jobj;
            Log.e("Status update", " received :" + dataObj.optString("status"));
            message = dataObj.optString("description");
            messageType = dataObj.optString("type");  //Sound, text, image
            updateType = dataObj.optString("updatetype");
            taskValues.put(WorkTransaction.UPDATE_TYPE, updateType);
            taskValues.put(WorkTransaction.MESSAGE, message);
            taskValues.put(WorkTransaction.MESSAGE_TYPE, messageType);
            taskValues.put(WorkTransaction.TRANSACTION_CODE, dataObj.optString("workitemupdateid"));
            taskValues.put(WorkTransaction.TASK_CODE, dataObj.optString("workitemid"));
            taskValues.put(WorkTransaction.USER_CODE, dataObj.optString("userid"));
            taskValues.put(WorkTransaction.DISCRIPTION, message);
            taskValues.put(WorkTransaction.START_DATE, dataObj.optString("startdate"));
            taskValues.put(WorkTransaction.END_DATE, dataObj.optString("enddate"));
            taskValues.put(WorkTransaction.DELEGATE_TO, dataObj.optString("delegateto"));
            taskValues.put(WorkTransaction.AMOUNT, dataObj.optString("expenseamt"));
            taskValues.put(WorkTransaction.INVOICE_DATE, dataObj.optString("expensedate"));
            taskValues.put(WorkTransaction.MESSAGE_LINK, dataObj.optString("path"));
            taskValues.put(WorkTransaction.STATUS, dataObj.optString("status"));
            taskValues.put(WorkTransaction.CREATED_ON, dataObj.optString("createdon"));

            if (updateType.equals("Query")) {
                taskValues.put(WorkTransaction.DELEGATE_TO, dataObj.optString("queryto"));
            }
            try {
                if (updateType.equals("Delegate")) {
                    if (dataObj.optString("status").equals(mContext.getString(R.string.approved))) {
                        if (dataObj.optString("queryto").contains("" + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID))) {
                            ContentValues taskValuesUpdate = new ContentValues();
                            taskValuesUpdate.put(WorkItem.ASSIGNED_TO, dataObj.optString("delegateto"));
                            int workid = Integer.parseInt(dataObj.optString("workitemid"));
                            getDb(mContext).update(Constant.WorkItemTable, taskValuesUpdate, WorkItem.TASK_ID + " = ?", new String[]{workid + ""});
                        } else {
                            ContentValues taskValuesUpdate = new ContentValues();
                            taskValuesUpdate.put(WorkItem.STATUS, mContext.getResources().getString(R.string.delegated));
                            taskValuesUpdate.put(WorkItem.ASSIGNED_TO, dataObj.optString("delegateto"));
                            int workid = Integer.parseInt(dataObj.optString("workitemid"));
                            getDb(mContext).update(Constant.WorkItemTable, taskValuesUpdate, WorkItem.TASK_ID + " = ?", new String[]{workid + ""});
                        }
                    }
                }

                if (updateType.equals("Postponed")) {
                    if (dataObj.optString("status").equals("Approved")) {
                        ContentValues taskValuesUpdate = new ContentValues();
                        taskValuesUpdate.put(WorkItem.START_DATE, dataObj.optString("startdate"));
                        taskValuesUpdate.put(WorkItem.END_DATE, dataObj.optString("enddate"));
                        int workid = Integer.parseInt(dataObj.optString("workitemid"));
                        getDb(mContext).update(Constant.WorkItemTable, taskValuesUpdate, WorkItem.TASK_ID + " = ?", new String[]{workid + ""});
                    }
                }
                //// Update value of work item when task is done
                if (updateType.equals("Done")) {
                    if (dataObj.optString("status").equals("Approved")) {
                        ContentValues ValuesUpdate = new ContentValues();
                        int workid = Integer.parseInt(dataObj.optString("workitemid"));
                        ValuesUpdate.put(WorkItem.STATUS, "Done");
                        getDb(mContext).update(Constant.WorkItemTable, ValuesUpdate, WorkItem.TASK_ID + " = ?", new String[]{workid + ""});
                    }
                }
                if (!dataObj.optString("status").equals("Pending")) {
                    RemoveFromApproval(getDb(mContext), Integer.parseInt(dataObj.optString("workitemupdateid")), "" + Constant.UPDATE_WORKITEM);
                    //RemoveFromApproval(getDb(mContext), Integer.parseInt(dataObj.optString("workitemid")), "" + Constant.EDIT_WORKITEM_APPROVAL);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            /// Finding work item from table if found then need to update details or insert it
            Cursor crsr = getDb(mContext).rawQuery("select * from " + Constant.WorkTransaction + " where " + WorkTransaction.TRANSACTION_CODE + " = " + dataObj.optString("workitemupdateid"), null);
            if (crsr != null && crsr.getCount() > 0) {
                getDb(mContext).update(Constant.WorkTransaction, taskValues, WorkTransaction.TRANSACTION_CODE + " = ?", new String[]{dataObj.optString("workitemupdateid") + ""});
            } else {
                getDb(mContext).insert(Constant.WorkTransaction, null, taskValues);
            }
            crsr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void generateUpdateNotification(Context context, String title, int taskCode, Class<?> cls) {
//        int icon = R.drawable.icon;
        long when = System.currentTimeMillis();
//        NotificationManager notificationManager = (NotificationManager) context
//                .getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notification = new Notification(icon, title, when);
//        Intent notificationIntent = new Intent(context, cls);
//        //  notificationIntent.removeExtra(WorkItem.TASK_ID);
//        notificationIntent.putExtra(WorkItem.TASK_ID, taskCode);
//        // set intent so it does not start a new activity
////        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
////                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//
//        PendingIntent intent = PendingIntent.getActivity(context, 0,
//                notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//        notification.setLatestEventInfo(context, Constant.AppNameSuper, title, intent);
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//        // Play default notification sound
//        notification.defaults |= Notification.DEFAULT_SOUND;
//        notificationManager.notify(Util.getMaxId(context), notification);

        int icon = R.drawable.icon;
        int id = (int) System.currentTimeMillis();
//        int mNotificationId = Integer.parseInt(String.valueOf(System.currentTimeMillis()));
        long[] pattern = {0, 200, 0};


        int roleID = Integer.parseInt(read(Constant.SHRED_PR.KEY_ROLE_ID, context));

        switch (roleID) {
            case 1:
                cls = AdminDashboardNewActivity.class;
                break;
            case 2:
                cls = AdminDashboardNewActivity.class;
                break;
            case 3:
                cls = ManagerDashboardNewActivity.class;
                break;
            case 4:
                cls = UserDashboardNewActivity.class;
                break;
            default:
                break;
        }


        Intent notificationIntent = new Intent(context, cls);


        // set intent so it does not start a new activity
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        notificationIntent.removeExtra(WorkItem.TASK_ID);
        notificationIntent.putExtra(WorkItem.TASK_ID, taskCode);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        Notification notification = mBuilder.setSmallIcon(R.drawable.icon_bell).setTicker(title).setWhen(id)
                .setAutoCancel(true)
                .setWhen(when)
                .setContentTitle(Constant.AppNameSuper)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                .setContentIntent(intent)
                .setVibrate(pattern)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon))
                .setContentText(title).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
    }

    public static void generateNotificationFragment(Context context, String title, Class<?> cls, int fragmentNo) {
//        int icon = R.drawable.icon;
        long when = System.currentTimeMillis();
//        NotificationManager notificationManager = (NotificationManager) context
//                .getSystemService(Context.NOTIFICATION_SERVICE);
//        Notification notification = new Notification(icon, title, when);
//        // String title = context.optString(R.string.app_name);
//        Intent notificationIntent = new Intent(context, cls);
//
//        notificationIntent.putExtra("pageno", fragmentNo);
//        // set intent so it does not start a new activity
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
//                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//        PendingIntent intent = PendingIntent.getActivity(context, 0,
//                notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
//        notification.setLatestEventInfo(context, Constant.AppNameSuper, title, intent);
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//        // Play default notification sound
//        notification.defaults |= Notification.DEFAULT_SOUND;
//        int id = Util.getMaxId(context);
//        Log.e("Id for notificatoin :", " id :=" + id);
//        notificationManager.notify(id, notification);


        int icon = R.drawable.icon;
        int id = (int) System.currentTimeMillis();
        long[] pattern = {0, 200, 0};


        int roleID = Integer.parseInt(read(Constant.SHRED_PR.KEY_ROLE_ID, context));

        switch (roleID) {
            case 1:
                cls = AdminDashboardNewActivity.class;
                break;
            case 2:
                cls = AdminDashboardNewActivity.class;
                break;
            case 3:
                cls = ManagerDashboardNewActivity.class;
                break;
            case 4:
                cls = UserDashboardNewActivity.class;
                break;
            default:
                break;
        }

        Intent notificationIntent = new Intent(context, cls);
        // set intent so it does not start a new activity
        //notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.putExtra("pageno", fragmentNo);
        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        Notification notification = mBuilder.setSmallIcon(R.drawable.icon_bell).setTicker(title).setWhen(id)
                .setAutoCancel(true)
                .setWhen(when)
                .setContentTitle(Constant.AppNameSuper)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                .setContentIntent(intent)
                .setVibrate(pattern)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon))
                .setContentText(title).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(id, notification);
    }

    public static String read(String key, Context context) {
        return Util.ReadSharePrefrence(context, key);
    }

    public static void InsertIntoProjects(JSONObject jsonObject, Context mContext) {
        try {
            ContentValues projectValues = new ContentValues();
            JSONObject data = jsonObject;
            projectValues.put(Project.USER_CODE, data.optString("creatorid"));
            projectValues.put(Project.PROJECT_NAME, data.optString("name"));
            projectValues.put(Project.DESCRIPTION, data.optString("description"));
            projectValues.put(Project.PRIORITY, data.optString("priority"));
            projectValues.put(Project.PROJECT_ID, data.optString("projectid"));
            projectValues.put(Project.START_DATE, data.optString("startdatetime"));
            projectValues.put(Project.END_DATE, data.optString("enddatetime"));
            projectValues.put(Project.ESTIMATED_TIME, data.optString("estimateddatetime"));
            projectValues.put(Project.ASSIGNED_TO, data.optString("tolist"));
            projectValues.put(Project.CC_TO, data.optString("cclist"));
            projectValues.put(Project.PROJECT_IMAGE, data.optString("imageURL"));
            projectValues.put(Project.STATUS, data.optString("status"));
            projectValues.put(Project.OWNER, data.optString("ownerid"));

//            TeamWorkApplication.db.insert(Constant.ProjectTable, null, projectValues);
            Cursor crsr = getDb(mContext).rawQuery("select * from " + Constant.ProjectTable + " where " + Project.PROJECT_ID + " = " + data.optString("projectid"), null);
            if (crsr != null && crsr.getCount() > 0) {
                crsr.moveToFirst();
                getDb(mContext).update(Constant.ProjectTable, projectValues, Project.PROJECT_ID + " = ?", new String[]{data.optString("projectid") + ""});
                Log.e("Project", "Updated");
            } else {
                getDb(mContext).insert(Constant.ProjectTable, null, projectValues);
                Log.e("Project", "Inserted");
            }
            crsr.close();
            if (!data.optString("status").equals("Pending")) {
                RemoveFromApproval(getDb(mContext), Integer.parseInt(data.optString("projectid")), "" + Constant.CREATE_NEWPRJECT);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void InsertIntoAttachments(JSONObject jsonObject, Context context) {
        ContentValues taskValues = new ContentValues();
        try {
            JSONObject dataObj = jsonObject;
            taskValues.put(Attachment.ATTACHMENT_ID, dataObj.optString("attachmentid"));
            taskValues.put(Attachment.PATH, dataObj.optString("path"));
            taskValues.put(Attachment.USER_ID, dataObj.optString("userid"));
            taskValues.put(Attachment.WORKITEM_ID, dataObj.optString("workitemid"));
            taskValues.put(Attachment.UPLOADEDDOWNLOADED, "false");
            getDb(context).insert(Constant.AttachmentTable, null, taskValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void HandleEditProjectApproved(JSONObject jObj, Context context, Boolean Noti) {
        Log.e("Project Approved", "" + jObj.toString());
        try {
            ContentValues projectValues = new ContentValues();
            JSONObject data = jObj.getJSONObject("data");
            projectValues.put(Project.USER_CODE, data.optString("creatorid"));
            projectValues.put(Project.PROJECT_NAME, data.optString("name"));
            projectValues.put(Project.DESCRIPTION, data.optString("description"));
            projectValues.put(Project.PRIORITY, data.optString("priority"));
            projectValues.put(Project.PROJECT_ID, data.optString("projectid"));
            projectValues.put(Project.START_DATE, data.optString("startdatetime"));
            projectValues.put(Project.END_DATE, data.optString("enddatetime"));
            projectValues.put(Project.ESTIMATED_TIME, data.optString("estimateddatetime"));
            projectValues.put(Project.ASSIGNED_TO, data.optString("tolist"));
            projectValues.put(Project.CC_TO, data.optString("cclist"));
            projectValues.put(Project.PROJECT_IMAGE, data.optString("imageURL"));
            projectValues.put(Project.STATUS, data.optString("status"));
            projectValues.put(Project.OWNER, data.optString("ownerid"));

//            TeamWorkApplication.db.insert(Constant.ProjectTable, null, projectValues);
            Cursor crsr = getDb(context).rawQuery("select * from " + Constant.ProjectTable + " where " + Project.PROJECT_ID + " = " + data.optString("projectid"), null);
            if (crsr != null && crsr.getCount() > 0) {
                crsr.moveToFirst();
                getDb(context).update(Constant.ProjectTable, projectValues, Project.PROJECT_ID + " = ?", new String[]{data.optString("projectid") + ""});
            }
            crsr.close();

            if (Noti) {
                NotificationInfo noti = new NotificationInfo();
                noti.setId(Integer.parseInt(data.optString("projectid")));
                noti.setTitle(data.optString("firstName") + " " + data.optString("lastName"));
                noti.setDescription("" + jObj.optString("message"));
                noti.setImage_Url("" + data.optString("picture"));
                noti.setType("" + jObj.optString("Type"));
//            noti.setTime(Util.GetDate());
                noti.setTime(jObj.optString("LastModified"));
                noti.setTransactionID(jObj.optString("TransactionID"));
                noti.setUser_Id(data.optString("creatorid"));
                Util.sendNotificationDetails(context, noti);

                String roleId = Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID);
                if (Arrays.asList("1", "2").contains(roleId)) {
                    generateNotificationFragment(context, jObj.optString("message"), AdminDashboardNewActivity.class, 1);
                } else if (Arrays.asList("3").contains(roleId)) {
                    generateNotificationFragment(context, jObj.optString("message"), ManagerDashboardNewActivity.class, 1);
                } else if (Arrays.asList("4").contains(roleId)) {
                    generateNotificationFragment(context, jObj.optString("message"), UserDashboardNewActivity.class, 1);
                }
            }

            if (ProjectFragment.Active) {
                Intent intent = new Intent("project_created");
                intent.putExtra("message", "data");
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static void HandleEditWorkItemApproved(JSONObject jsonObject, Context context) {

        SQLiteHelper helper = new SQLiteHelper(context, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();

        ContentValues taskValues = new ContentValues();
        try {
            JSONObject jobj = jsonObject.getJSONObject("data");
            taskValues.put(WorkItem.TASK_ID, "" + jobj.optString("workitemid"));
            taskValues.put(WorkItem.USER_CODE, "" + jobj.optString("userid"));
            taskValues.put(WorkItem.TASK_NAME, "" + jobj.optString("name"));
            taskValues.put(WorkItem.DESCRIPTION, "" + jobj.optString("description"));
            taskValues.put(WorkItem.BUDGET, "" + jobj.optString("budget"));
            taskValues.put(WorkItem.PRIORITY, "" + jobj.optString("priority"));
            taskValues.put(WorkItem.ATTACHMENTS, "" + jobj.optString("attachmentlist"));
            taskValues.put(WorkItem.WORK_LOCATION, "" + jobj.optString("location"));
            taskValues.put(WorkItem.WORK_LONGITUDE, "" + jobj.optString("longitude"));
            taskValues.put(WorkItem.WORK_LATITUDE, "" + jobj.optString("latitude"));
            taskValues.put(WorkItem.START_DATE, "" + jobj.optString("startdatetime"));
            taskValues.put(WorkItem.END_DATE, "" + jobj.optString("enddatetime"));
            taskValues.put(WorkItem.ESTIMATED_TIME, "" + jobj.optString("estimatedtime"));
            taskValues.put(WorkItem.ASSIGNED_TO, "" + jobj.optString("tolist"));
            taskValues.put(WorkItem.CC_TO, "" + jobj.optString("cclist"));
            taskValues.put(WorkItem.TASK_TYPE, "" + jobj.optString("worktype"));
            taskValues.put(WorkItem.PROJECT_CODE, "" + jobj.optString("projectid"));
            taskValues.put(WorkItem.TASK_AFTER, "" + jobj.optString("dependenton"));

            taskValues.put(WorkItem.FREQUENCY, "" + jobj.optString("frequency"));

            taskValues.put(WorkItem.DAYCODES_SELECTED, "" + jobj.optString("daycodes"));
            taskValues.put(WorkItem.CUSTOMER_NAME, "" + jobj.optString("customername"));
            taskValues.put(WorkItem.CUSTOMER_CONTACT, "" + jobj.optString("customerno"));
            taskValues.put(WorkItem.CUSTOMER_TYPE, "" + jobj.optString("customertype"));
            taskValues.put(WorkItem.DUE_DATE, "" + jobj.optString("duedate"));
            taskValues.put(WorkItem.PAST_HISTORY, "" + jobj.optString("pasthistory"));
            taskValues.put(WorkItem.VENDOR_PREFERENCE, "" + jobj.optString("vendorpreferrance"));
            taskValues.put(WorkItem.VENDOR_NAME, "" + jobj.optString("vendorname"));
            taskValues.put(WorkItem.ADVANCE_PAID, "" + jobj.optString("advancepaid"));
            taskValues.put(WorkItem.INVOICE_AMOUNT, "" + jobj.optString("invoiceamt"));
            taskValues.put(WorkItem.INVOICE_DATE, "" + jobj.optString("invoicedate"));
            taskValues.put(WorkItem.OUTSTANDING_AMT, "" + jobj.optString("outstandingamt"));
            taskValues.put(WorkItem.TASK_IMAGE, "" + jobj.optString("imageURL"));
            taskValues.put(WorkItem.STATUS, "" + jobj.optString("status"));

            Cursor crsr = db.rawQuery("select * from " + Constant.WorkItemTable + " where " + WorkItem.TASK_ID + " = " + jobj.optString("workitemid"), null);
            if (crsr != null && crsr.getCount() > 0) {
                db.update(Constant.WorkItemTable, taskValues, WorkItem.TASK_ID + " = ?", new String[]{jobj.optString("workitemid") + ""});

                if (UpdateWorkActivity.Active) {
                    Intent intent = new Intent("message_aaya");
                    intent.putExtra("message", "data");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            } else {
                db.insert(Constant.WorkItemTable, null, taskValues);
            }
            if (crsr != null) crsr.close();

            // Sending information for notification table
            NotificationInfo noti = new NotificationInfo();
            noti.setId(Integer.parseInt(jobj.optString("workitemid")));
            noti.setTitle(jobj.optString("firstName") + " " + jobj.optString("lastName"));
            noti.setDescription("" + jsonObject.optString("message"));
            noti.setImage_Url("" + jobj.optString("picture"));
            noti.setType("" + jsonObject.optString("Type"));
            noti.setTime(jsonObject.optString("LastModified"));
            noti.setTransactionID(jsonObject.optString("TransactionID"));
            noti.setUser_Id(jobj.optString("userid"));
            Util.sendNotificationDetails(context, noti);

            ////// Setting Alarm
//            int alarmId = new Random().nextInt(Integer.MAX_VALUE); /* Dynamically assign alarm ids for multiple alarms */

            Calendar calendar = Calendar.getInstance();
            Calendar currentTime = Calendar.getInstance();

            Date xDate = HandleResponseWorkItems.convertToDate(Util.utcToLocalTime(jobj.optString("enddatetime")));
            calendar.setTime(xDate);
            //  calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
            Log.e("Calendar Date", "Date: " + calendar.getTime());
            if (calendar.getTime().after(currentTime.getTime())) {
                ///////

                try {
                    int alarmId = jobj.optInt("workitemid");

                    AlarmManager am = (AlarmManager) context
                            .getSystemService(context.ALARM_SERVICE);
                    currentTime.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
                    if (calendar.getTime().after(currentTime.getTime())) {

                        Intent i = new Intent(context, AlarmReceiver.class);
                        i.putExtra("message", jobj.optString("name") + " \n" + context.getString(R.string.less_than_one_hr_remains));
                        i.putExtra(WorkItem.TASK_ID, "" + jobj.optString("workitemid"));
                        i.setAction("com.bigbang.teamworks.WORK_ITEM");

                        PendingIntent sender = PendingIntent.getBroadcast(context, alarmId, i, 0);

                        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) - 1);
                        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 10000, sender);
                        Log.e("Alarm", "Calendar Setted at " + calendar.getTime().toString());
                    }

                    Intent i = new Intent(context, AlarmReceiver.class);
                    i.putExtra("message", jobj.optString("name") + " \n" + context.getString(R.string.Time_Out_Work_Item));
                    i.putExtra(WorkItem.TASK_ID, "" + jobj.optString("workitemid"));
                    i.setAction("com.bigbang.teamworks.WORK_ITEM");
                    PendingIntent sender = PendingIntent.getBroadcast(context, alarmId, i, 0);
                    calendar.setTime(xDate);
                    am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis() + 10000, sender);
                    Log.e("Alarm", "Calendar Setted at " + calendar.getTime().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
