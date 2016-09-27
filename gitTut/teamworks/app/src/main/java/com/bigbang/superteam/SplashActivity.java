package com.bigbang.superteam;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;

import com.bigbang.superteam.admin.AdminDashboardNewActivity;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.login_register.CompanySetupActivity;
import com.bigbang.superteam.login_register.InvitationActivity;
import com.bigbang.superteam.login_register.LoginActivity;
import com.bigbang.superteam.manager.ManagerDashboardNewActivity;
import com.bigbang.superteam.user.UserDashboardNewActivity;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.Util;
import com.crittercism.app.Crittercism;

import java.io.File;
import java.util.HashMap;

public class SplashActivity extends BaseActivity {

    SQLiteHelper helper;
    public static SQLiteDatabase db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StartAlarmToKeepGCMConnect();

        getApplicationContext().sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
        getApplicationContext().sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));

        Crittercism.initialize(getApplicationContext(), "5534db878172e25e679069a3");
        write(Constant.SHRED_PR.KEY_RELOAD, "0");

        //Creating directory if not exist
        checkAndCreateDirectory();

        Log.e("KEY_IS_LOGGEDIN", ">>" + read(Constant.SHRED_PR.KEY_IS_LOGGEDIN));
        //Check if user is already logged in or not
        if (read(Constant.SHRED_PR.KEY_IS_LOGGEDIN).equals("true")) {
            String roleId = Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_ROLE_ID);
            if (roleId.equals("1")) {
                if (read(Constant.SHRED_PR.KEY_COMPANY_ID).equals("0")) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("from", "" + Constant.FROM_LOGIN);
                    startActivityWithData(InvitationActivity.class, hashMap);

                } else if (!read(Constant.SHRED_PR.KEY_IS_COMPANY_CREATED).equals("true")) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("position", "0");
                    hashMap.put("from", "" + Constant.FROM_LOGIN);
                    startActivityWithData(CompanySetupActivity.class, hashMap);
                } else if (!read(Constant.SHRED_PR.KEY_IS_COMPANY_SETUP).equals("true")) {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("position", "1");
                    startActivityWithData(CompanySetupActivity.class, hashMap);
                } else {
                    startActivity(AdminDashboardNewActivity.class);
                }
            } else if (roleId.equals("2")) {
                startActivity((AdminDashboardNewActivity.class));
            } else if (roleId.equals("3")) {
                startActivity((ManagerDashboardNewActivity.class));
            } else {
                startActivity((UserDashboardNewActivity.class));
            }
            finish();
        } else {

            setContentView(R.layout.activity_splash);

            final Typeface mFont = Typeface.createFromAsset(getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) findViewById(android.R.id.content).getRootView();
            Util.setAppFont(mContainer, mFont);

            Handler hn = new Handler();
            hn.postDelayed(new Runnable() {
                @Override
                public void run() {

                    Util.GCM(SplashActivity.this);

                    helper = new SQLiteHelper(SplashActivity.this, Constant.DatabaseName);
                    helper.createDatabase();
                    db = helper.openDatabase();

                    if (Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_TIME_PERIOD_TRACKING).toString().length() == 0)
                        Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_TIME_PERIOD_TRACKING, "2");

                }
            }, 500);

            Handler hn1 = new Handler();
            hn1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent in = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(in);
                    finish();
                }
            }, 2000);
        }
    }

    private void checkAndCreateDirectory() {
        File direct = new File(Environment.getExternalStorageDirectory() + "/" + Constant.AppNameSuper);
        if (!direct.exists()) {
            direct.mkdirs();
        }
    }

    private void StartAlarmToKeepGCMConnect() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent gcmKeepAliveIntent = new Intent("com.gmail.npnster.ourlatitude.gcmKeepAlive");
        PendingIntent gcmKeepAlivePendingIntent = PendingIntent.getBroadcast(SplashActivity.this, 0, gcmKeepAliveIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, 4 * 60 * 1000, gcmKeepAlivePendingIntent);
    }
}
