package com.bigbang.superteam.Application;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.bigbang.superteam.Privileges;
import com.bigbang.superteam.dataObjs.OfflineWork;
import com.bigbang.superteam.login_register.LoginActivity;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.AlarmReceiver;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import java.util.Calendar;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by USER 3 on 28/05/2015.
 */
public class TeamWorkApplication extends Application {

    public static SQLiteDatabase db = null;
    SQLiteHelper helper;
    static Context context;
    String TAG = "TeamWorkApplication";
//    Random rand;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;

        context.sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
        context.sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));

        InitDatabase();
//        rand = new Random();
        if (Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_IS_LOGGEDIN).equals("true")) {
            Privileges.Init(this);
        }

        try {
            // Mint.initAndStartSession(context, "35a686e3");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (db != null)
            db.close();
    }

    private void InitDatabase() {
        helper = new SQLiteHelper(this, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();
    }

    public static void logout(final Activity act, final TransparentProgressDialog progressDialog) {
        if (progressDialog != null) progressDialog.show();
        RestClient.getCommonService3().logout(Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_USERID), Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_TOKEN), Constant.AppName,
                new Callback<Response>() {
                    @Override
                    public void success(Response rc, Response response) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();
                        try {
                            TeamWorkApplication.LogOutClear(act);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();
                    }
                });
    }

    public static void LogOutClear(Activity act) {
        SQLiteHelper db = new SQLiteHelper(context);
        db.clearTables();

        Util.unregisterUserTrackingReceiver(context);
        try {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent updateServiceIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingUpdateIntent = PendingIntent.getService(context, 0, updateServiceIntent, 0);
            // Cancel alarms
            alarmManager.cancel(pendingUpdateIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Util.WriteSharePrefrence(context, Constant.SHRED_PR.KEY_IS_LOGGEDIN, "false");
        Util.WriteSharePrefrence(context, Constant.SHRED_PR.KEY_IS_COMPANY_CREATED, "false");
        Util.WriteSharePrefrence(context, Constant.SHRED_PR.KEY_IS_COMPANY_SETUP, "false");
        Util.WriteSharePrefrenceForBoolean(context, Constant.SHRED_PR.KEY_PAYROLL_ACTIVE, false);

        Intent i = new Intent(context,
                LoginActivity.class);
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        Calendar calendar = Calendar.getInstance();
        Util.appendLog("*******************");
        Util.appendLog("Logout at: " + calendar.get(Calendar.DAY_OF_MONTH) + " / " + (calendar.get(Calendar.MONTH) + 1) + " == " + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE) + " : " + calendar.get(Calendar.SECOND));
        Util.appendLog("Tracking OFF");
        Util.appendLog("*******************");

        //Tracking alarm cancel
        Util.unregisterUserTrackingReceiver(context);

        context.startActivity(i);
    }

    public static void LogOutClear() {
        SQLiteHelper db = new SQLiteHelper(context.getApplicationContext());
        db.clearTables();

        try {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent updateServiceIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingUpdateIntent = PendingIntent.getService(context, 0, updateServiceIntent, 0);
            // Cancel alarms
            alarmManager.cancel(pendingUpdateIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Util.WriteSharePrefrence(context, Constant.SHRED_PR.KEY_IS_LOGGEDIN, "false");
        Util.WriteSharePrefrence(context, Constant.SHRED_PR.KEY_IS_COMPANY_CREATED, "false");
        Util.WriteSharePrefrence(context, Constant.SHRED_PR.KEY_IS_COMPANY_SETUP, "false");
        Util.WriteSharePrefrenceForBoolean(context, Constant.SHRED_PR.KEY_PAYROLL_ACTIVE, false);

        Intent i = new Intent(context,
                LoginActivity.class);
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }

        Calendar calendar = Calendar.getInstance();
        Util.appendLog("Logout @: " + calendar.get(Calendar.DAY_OF_MONTH) + " / " + (calendar.get(Calendar.MONTH) + 1) + " == " + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE) + " : " + calendar.get(Calendar.SECOND));
        Util.appendLog("Tracking STOP: " + calendar.get(Calendar.DAY_OF_MONTH) + " / " + (calendar.get(Calendar.MONTH) + 1) + " == " + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE) + " : " + calendar.get(Calendar.SECOND));

        Util.unregisterUserTrackingReceiver(context);
        context.startActivity(i);
    }

    public static void LogInClear(Context context) {

        try {

            SQLiteHelper helper = new SQLiteHelper(context, Constant.DatabaseName);
            helper.createDatabase();
            SQLiteDatabase db = helper.openDatabase();

            db.delete(Constant.ApprovalAttendanceDetail, null, null);
            db.delete(Constant.ApprovalsTable, null, null);
            db.delete(Constant.AttachmentTable, null, null);
            db.delete(Constant.NotificationTable, null, null);
            db.delete(Constant.AlertsTable, null, null);
            db.delete(Constant.ProjectTable, null, null);
            db.delete(Constant.WorkItemTable, null, null);
            db.delete(Constant.tableUsers, null, null);
            db.delete(Constant.WorkTransaction, null, null);
            db.delete(OfflineWork.OFFLINETABLE, null, null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
