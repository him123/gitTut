package com.bigbang.superteam.workitem;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.bigbang.superteam.Privileges;
import com.bigbang.superteam.R;
import com.bigbang.superteam.WorkItem_GCM;
import com.bigbang.superteam.dataObjs.OfflineWork;
import com.bigbang.superteam.dataObjs.Project;
import com.bigbang.superteam.dataObjs.WorkItem;
import com.bigbang.superteam.dataObjs.WorkTransaction;
import com.bigbang.superteam.fragment.ActiveTasksFragment;
import com.bigbang.superteam.fragment.ArchivedTasksFragment;
import com.bigbang.superteam.fragment.PendingTasksFragment;
import com.bigbang.superteam.fragment.ProjectFragment;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by USER 3 on 09/07/2015.
 */
public class HandleResponseWorkItems {
    public static void HandleCreateWorkItemResponse(String FilePath, String ApiName, HashMap<String, String> toSendHashMap, HashMap<String, String> localMap, String response, SQLiteDatabase db, Context context, int offlineId) {
        Log.e("HandleResponseWorkItems", "11111111 HandleCreateWorkItemResponse");
        int localId = Integer.parseInt(toSendHashMap.get("workitemid"));
        int WorkItemId = 0;
        try {
            JSONObject json = new JSONObject(response);
            if (json.getString("status").equals("Success")) {
                WorkItemId = Integer.parseInt(json.getString("workitemid"));
            } else {
//                Toast.makeText(context, json.getString("message"), Toast.LENGTH_SHORT).show();
                if (offlineId == 0) {
                    db.delete(Constant.WorkItemTable, WorkItem.TASK_ID + " = ?", new String[]{localId + ""});
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            if (offlineId <= 0) {
                OfflineWork.StoreData(FilePath, toSendHashMap, localMap, ApiName, db);
            }
        }
        if (WorkItemId == 0) {
            db.delete(Constant.WorkItemTable, WorkItem.TASK_ID + " = ?", new String[]{localId + ""});
        } else {
            ContentValues taskValues = new ContentValues();
            taskValues.put(WorkItem.TASK_ID, "" + WorkItemId);
            taskValues.put(WorkItem.USER_CODE, "" + toSendHashMap.get("userid"));
            taskValues.put(WorkItem.TASK_NAME, "" + toSendHashMap.get("name"));
            taskValues.put(WorkItem.DESCRIPTION, "" + toSendHashMap.get("description"));
            taskValues.put(WorkItem.BUDGET, "" + toSendHashMap.get("budget"));
            taskValues.put(WorkItem.PRIORITY, "" + toSendHashMap.get("priority"));
            taskValues.put(WorkItem.ATTACHMENTS, "" + toSendHashMap.get("attachmentlist"));
            taskValues.put(WorkItem.WORK_LOCATION, "" + toSendHashMap.get("location"));
            taskValues.put(WorkItem.WORK_LONGITUDE, "" + toSendHashMap.get("longitude"));
            taskValues.put(WorkItem.WORK_LATITUDE, "" + toSendHashMap.get("latitude"));
            taskValues.put(WorkItem.START_DATE, "" + toSendHashMap.get("startdatetime"));
            taskValues.put(WorkItem.END_DATE, "" + toSendHashMap.get("enddatetime"));
            taskValues.put(WorkItem.ESTIMATED_TIME, "" + toSendHashMap.get("estimatedtime"));
            taskValues.put(WorkItem.ASSIGNED_TO, "" + toSendHashMap.get("tolist"));
            taskValues.put(WorkItem.CC_TO, "" + toSendHashMap.get("cclist"));
            taskValues.put(WorkItem.TASK_TYPE, "" + toSendHashMap.get("worktype"));

            taskValues.put(WorkItem.PROJECT_CODE, toSendHashMap.get("projectid"));
            taskValues.put(WorkItem.TASK_AFTER, toSendHashMap.get("dependenton"));

            taskValues.put(WorkItem.FREQUENCY, "" + toSendHashMap.get("frequency"));
            taskValues.put(WorkItem.DAYCODES_SELECTED, "" + toSendHashMap.get("daycodes"));
            taskValues.put(WorkItem.CUSTOMER_NAME, "" + toSendHashMap.get("customername"));
            taskValues.put(WorkItem.CUSTOMER_CONTACT, "" + toSendHashMap.get("customerno"));
            taskValues.put(WorkItem.CUSTOMER_TYPE, "" + toSendHashMap.get("customertype"));
            taskValues.put(WorkItem.DUE_DATE, "" + toSendHashMap.get("duedate"));
            taskValues.put(WorkItem.PAST_HISTORY, "" + toSendHashMap.get("pasthistory"));
            taskValues.put(WorkItem.VENDOR_PREFERENCE, "" + toSendHashMap.get("vendorpreferrance"));
            taskValues.put(WorkItem.VENDOR_NAME, "" + toSendHashMap.get("vendorname"));
            taskValues.put(WorkItem.ADVANCE_PAID, "" + toSendHashMap.get("advancepaid"));
            taskValues.put(WorkItem.INVOICE_AMOUNT, "" + toSendHashMap.get("invoiceamt"));
            taskValues.put(WorkItem.INVOICE_DATE, "" + toSendHashMap.get("invoicedate"));
            taskValues.put(WorkItem.OUTSTANDING_AMT, "" + toSendHashMap.get("outstandingamt"));
//            taskValues.put(WorkItem.TASK_IMAGE, "" + localMap.get("taskimage"));
            Log.e("Privilage", "Approval for work item req: " + Privileges.APPROVAL_REQ_FOR_NEW_WORKITEM);
            if (Privileges.APPROVAL_REQ_FOR_NEW_WORKITEM) {
                taskValues.put(WorkItem.STATUS, "Pending");
            } else {
                taskValues.put(WorkItem.STATUS, "Approved");
            }

            if (checkifAlreadyInWorkItem(db, WorkItemId)) {
                //db.update(Constant.WorkItemTable, taskValues, WorkItem.TASK_ID + " = ?", new String[]{WorkItemId + ""});
                db.delete(Constant.WorkItemTable, WorkItem.TASK_ID + " = ?", new String[]{localId + ""});
            } else
                db.update(Constant.WorkItemTable, taskValues, WorkItem.TASK_ID + " = ?", new String[]{localId + ""});

            Log.e("inserted", "Work item Uploaded");
            deleteFromOffline(db, offlineId);
            ////// If Work type is project then return to create work item and if success then finish

            int alarmId = new Random().nextInt(Integer.MAX_VALUE); /* Dynamically assign alarm ids for multiple alarms */

            Calendar calendar = Calendar.getInstance();
            Calendar currentTime = Calendar.getInstance();
            currentTime.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);

            Date xDate = HandleResponseWorkItems.convertToDate(Util.utcToLocalTime(toSendHashMap.get("enddatetime")));
            calendar.setTime(xDate);
            Log.e("Calendar Date", "Date: " + calendar.getTime());

        }

        //// Update work item list if work item is created and need to update the list showing there.
        if (ActiveTasksFragment.Active) {
            Intent intent = new Intent("workitem_created");
            intent.putExtra("message", "data");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }

    private static boolean checkifAlreadyInWorkItem(SQLiteDatabase db, int workItemId) {
        Cursor crsr = db.rawQuery("select * from " + Constant.WorkItemTable + " where " + WorkItem.TASK_ID + "=" + workItemId, null);
        if (crsr != null && crsr.getCount() > 0) {
            crsr.close();
            return true;
        }
        crsr.close();
        return false;
    }

    private static void deleteFromOffline(SQLiteDatabase db, int offlineId) {
        if (offlineId != 0) {
            db.delete(OfflineWork.OFFLINETABLE, OfflineWork.ID + " = ?", new String[]{offlineId + ""});
            Log.d("Removed", " Removed Entry from Offline Table");
        }
    }

//    public static void HandleAttachmentResponse(HashMap<String, String> toSendHashMap, HashMap<String, String> localHashMap, String resp, SQLiteDatabase db, Context context, int offlineId) {
//        JSONObject job = null;
//        try {
//            job = new JSONObject(resp);
//            if (job.getString("status").equals("Success")) {
//                ContentValues attachmentValue = new ContentValues();
//                attachmentValue.put("Attachment_Id", job.getJSONObject("data").getString("attachmentid"));
//                attachmentValue.put("Uploaded_Downloaded", "true");
//                db.update(Constant.AttachmentTable, attachmentValue, "Attachment_Id" + " = ?", new String[]{localHashMap.get("localId") + ""});
//                Log.e("Data Updated", "for id=" + job.getJSONObject("data").getString("attachmentid"));
//                deleteFromOffline(db, offlineId);
//
//            } else {
//                Toast.makeText(context, job.getString("message"), Toast.LENGTH_SHORT).show();
//                db.delete(Constant.AttachmentTable, "Attachment_Id" + " = ?", new String[]{localHashMap.get("localId") + ""});
//                deleteFromOffline(db, offlineId);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    public static void handleTaskChatResponse(){

    }

    public static void HandleWorkItemUpdateResponse(String FilePath, String ApiName, HashMap<String, String> hashMap, HashMap<String, String> localMap, String resp, SQLiteDatabase db, Context context, int offLineId) {

        Log.e("HandleResponseWorkItems", "222222 HandleWorkItemUpdateResponse");
        JSONObject json = null;
        String updateType = "";
        String message = "";
        String status = "";
        try {
            json = new JSONObject(resp);
            if (json.getString("status").equals("Success")) {

                message = json.getString("message");
                ContentValues taskValues = new ContentValues();

                taskValues.put(WorkTransaction.TRANSACTION_CODE, Integer.parseInt(json.getString("workitemupdateid")));
                taskValues.put(WorkTransaction.TASK_CODE, hashMap.get("workitemid"));
                taskValues.put(WorkTransaction.USER_CODE, hashMap.get("userid"));
                taskValues.put(WorkTransaction.MESSAGE, hashMap.get("description"));
                taskValues.put(WorkTransaction.MESSAGE_LINK, localMap.get("filelink"));
                taskValues.put(WorkTransaction.MESSAGE_TYPE, hashMap.get("type"));
                taskValues.put(WorkTransaction.UPDATE_TYPE, hashMap.get("updatetype"));
                updateType = hashMap.get("updatetype");
                taskValues.put(WorkTransaction.START_DATE, hashMap.get("startdate"));
                taskValues.put(WorkTransaction.END_DATE, hashMap.get("enddate"));
                taskValues.put(WorkTransaction.DELEGATE_TO, hashMap.get("delegateto"));
                taskValues.put(WorkTransaction.DISCRIPTION, hashMap.get("description"));
                taskValues.put(WorkTransaction.AMOUNT, hashMap.get("expenseamt"));
                taskValues.put(WorkTransaction.INVOICE_DATE, hashMap.get("expensedate"));
                if (json.getString("workitemstatus") != null && json.getString("workitemstatus").equalsIgnoreCase("Delegated")) {
                    status = json.getString("workitemstatus");
                } else {
                    status = hashMap.get("status");
                }
                taskValues.put(WorkTransaction.STATUS, status);
                taskValues.put(WorkTransaction.REASON, hashMap.get("reason"));
                taskValues.put(WorkTransaction.CREATED_ON, Util.locatToUTC(
                        Util.GetDate()));
                Log.e("UpdtRepsonceHndlng", " old Id :" + hashMap.get("updateid") + " new Id :" + Integer.parseInt(json.getString("workitemupdateid")));

                if (checkifAlready(db, Integer.parseInt(json.getString("workitemupdateid")))) {
                    db.update(Constant.WorkTransaction, taskValues, WorkTransaction.TRANSACTION_CODE + " = ?", new String[]{json.getString("workitemupdateid") + ""});
                    db.delete(Constant.WorkTransaction, WorkTransaction.TRANSACTION_CODE + " = ?", new String[]{hashMap.get("updateid") + ""});
                } else
                    db.update(Constant.WorkTransaction, taskValues, WorkTransaction.TRANSACTION_CODE + " = ?", new String[]{hashMap.get("updateid") + ""});

                deleteFromOffline(db, offLineId);

                if (status.equals("Approved")) {
                    Toast.makeText(context, context.getString(R.string.workupdate_inserted), Toast.LENGTH_SHORT).show();
                } else if (status.equalsIgnoreCase("Delegated")) {
                    if (!message.equals("")) {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                    }
                    ContentValues taskValuesUpdate = new ContentValues();
                    taskValuesUpdate.put(WorkItem.STATUS, context.getResources().getString(R.string.delegated));
                    taskValuesUpdate.put(WorkItem.ASSIGNED_TO, hashMap.get("delegateto"));
                    int workid = Integer.parseInt(hashMap.get("workitemid"));
                    WorkItem_GCM.getDb(context).update(Constant.WorkItemTable, taskValuesUpdate, WorkItem.TASK_ID + " = ?", new String[]{workid + ""});
                } else {

                    //MODIFY_WORKITEM = Util.checkUserPrivilage(context, "26");

                    if (Privileges.APPROVAL_REQ_FOR_WORKITEM_UPDATE) {
                        Toast.makeText(context, context.getString(R.string.workitem_update_has_been_sent_for_approval), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, context.getString(R.string.workupdate_inserted), Toast.LENGTH_SHORT).show();
                    }
                }

                if (updateType.equals("Done") && status.equals("Approved")) {
                    ContentValues ValuesUpdate = new ContentValues();
                    int workid = Integer.parseInt(hashMap.get("workitemid"));
                    ValuesUpdate.put(WorkItem.STATUS, "Done");
                    db.update(Constant.WorkItemTable, ValuesUpdate, WorkItem.TASK_ID + " = ?", new String[]{workid + ""});

                }
                // If task is of postponed then need to upgrade workitem table but in case it is approved
                if (updateType.equals("Postponed") && status.equals("Approved")) {
                    ContentValues taskValuesUpdate = new ContentValues();
                    taskValuesUpdate.put(WorkItem.START_DATE, hashMap.get("startdate"));
                    taskValuesUpdate.put(WorkItem.END_DATE, hashMap.get("enddate"));
                    int workid = Integer.parseInt(hashMap.get("workitemid"));
                    db.update(Constant.WorkItemTable, taskValuesUpdate, WorkItem.TASK_ID + " = ?", new String[]{workid + ""});
                }

            } else {
                Log.e("Work Update:", "Error : not succeed");
                Toast.makeText(context, json.getString("message"), Toast.LENGTH_SHORT).show();
                deleteFromOffline(db, offLineId);
                db.delete(Constant.WorkTransaction, WorkTransaction.TRANSACTION_CODE + " = ?", new String[]{hashMap.get("updateid") + ""});

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            if (offLineId <= 0) {
                OfflineWork.StoreData(FilePath, hashMap, localMap, ApiName, db);
            }
        }

        //// Because if notification arrives before response then this will handle it and no duplicates will get gen.
        if (UpdateWorkActivity.Active) {
            Intent intent = new Intent("message_aaya");
            intent.putExtra("message", "data");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

    }

    private static boolean checkifAlready(SQLiteDatabase db, int workitemupdateid) {
        Cursor crsr = db.rawQuery("select * from " + Constant.WorkTransaction + " where " + WorkTransaction.TRANSACTION_CODE + "=" + workitemupdateid, null);
        if (crsr != null && crsr.getCount() > 0) {
            crsr.close();
            return true;
        }
        if (crsr != null) crsr.close();
        return false;
    }

    public static void HandleCreateProject(String filePath, String ApiName, HashMap<String, String> hashMap, HashMap<String, String> localMap, String resp, SQLiteDatabase db, Context context, int offLineId) {
        int projectId = 0;
        Log.e("HandleResponse", "HandleCreateProject function");
        try {
            JSONObject json = new JSONObject(resp);
            if (json.getString("status").equals("Success")) {
                projectId = Integer.parseInt(json.getString("projectid"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            if (offLineId == 0) {
                OfflineWork.StoreData(filePath, hashMap, localMap, ApiName, db);
            }
        }
        Log.e("Project id =", "" + projectId);
        if (projectId <= 0) {
            db.delete(Constant.ProjectTable, Project.PROJECT_ID + " = ?", new String[]{hashMap.get("projectid") + ""});
        } else {
            ContentValues projectValues = new ContentValues();
            projectValues.put(Project.PROJECT_ID, projectId);
            projectValues.put(Project.USER_CODE, hashMap.get("userid"));
            projectValues.put(Project.PROJECT_NAME, hashMap.get("name"));
            projectValues.put(Project.DESCRIPTION, hashMap.get("description"));
            projectValues.put(Project.PRIORITY, hashMap.get("priority"));
            projectValues.put(Project.PROJECT_ID, "" + projectId);
            projectValues.put(Project.START_DATE, hashMap.get("startdatetime"));
            projectValues.put(Project.END_DATE, hashMap.get("enddatetime"));
            projectValues.put(Project.ESTIMATED_TIME, hashMap.get("estimatedtime"));
            projectValues.put(Project.ASSIGNED_TO, hashMap.get("tolist"));
            projectValues.put(Project.CC_TO, hashMap.get("cclist"));
            projectValues.put(Project.PROJECT_IMAGE, localMap.get("projectImage"));
            projectValues.put(Project.OWNER, hashMap.get("ownerid"));
            String roleId = Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID);
            Log.e("Project Privilage", "::" + Privileges.APPROVAL_REQ_FOR_NEW_PROJECT);

            if (Privileges.APPROVAL_REQ_FOR_NEW_PROJECT)
                projectValues.put(Project.STATUS, "Pending");
            else projectValues.put(Project.STATUS, "Approved");

            if (checkifalreadyInProjects(db, projectId)) {
                Log.e("create", "update +delete");
                db.update(Constant.ProjectTable, projectValues, Project.PROJECT_ID + " = ?", new String[]{projectId + ""});
                db.delete(Constant.ProjectTable, Project.PROJECT_ID + " = ?", new String[]{hashMap.get("projectid") + ""});
            } else {
                Log.e("create", "update ");
                db.update(Constant.ProjectTable, projectValues, Project.PROJECT_ID + " = ?", new String[]{hashMap.get("projectid") + ""});
            }
            deleteFromOffline(db, offLineId);
        }
        if (ProjectFragment.Active) {
            Intent intent = new Intent("project_created");
            intent.putExtra("message", "data");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

    }

    private static boolean checkifalreadyInProjects(SQLiteDatabase db, int projectId) {
        Cursor crsr = db.rawQuery("select * from " + Constant.ProjectTable + " where " + Project.PROJECT_ID + "=" + projectId, null);
        if (crsr != null && crsr.getCount() > 0) {
            crsr.close();
            return true;
        }
        crsr.close();
        return false;
    }

    public static Date convertToDate(String dateString) {
        Date convertedDate = new Date();
        try {
            convertedDate = Util.sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }

    public static void HandleReopenWorkItem(HashMap<String, String> hashMap, HashMap<String, String> localMap, String o, Context mContext) {
        Log.e("HandleResponseWorkItems", "333333333 HandleReopenWorkItem");

        JSONObject json = null;
        try {

            SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
            helper.createDatabase();
            SQLiteDatabase db = helper.openDatabase();

            json = new JSONObject(o);
            if (json.getString("status").equals("Success")) {
                ContentValues taskValues = new ContentValues();
                taskValues.put(WorkItem.STATUS, "Approved");
                Log.e("HandleResponseWorkItems", "work item id local is:- " + WorkItem.TASK_ID + "   **** workItem ID new is:- " + json.optString("workitemid"));
                db.update(Constant.WorkItemTable, taskValues, WorkItem.TASK_ID + " = ?", new String[]{json.optString("workitemid") + ""});
                if (ActiveTasksFragment.Active || ArchivedTasksFragment.Active || PendingTasksFragment.Active) {
                    Intent intent = new Intent("workitem_history");
                    intent.putExtra("message", "data");
                    LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
                }
            } else
                Toast.makeText(mContext, json.getString("message"), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
