package com.bigbang.superteam.alarm;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.dataObjs.User_Location;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.LocationService;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by USER 8 on 5/9/2015.
 */
public class UserTrackingReceiverIntentService extends IntentService {

    public static final String TAG = "UserTrackingReceiverIntentService";
    Context context;

    public UserTrackingReceiverIntentService() {
        super("UserTrackingReceiverIntentService");
    }

    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    LocationManager locationManager;
    Date date = new Date();
    //
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    String DateTime = sdf.format(date);

    private void insertIntoDatabase(String DateTime, String Location,
                                    String Latitude, String Longitude) {
        // TODO Auto-generated method stub
        try {
            Util.appendLog("Local Entry");
            ContentValues values = new ContentValues();
            //testing comment
            values.put(User_Location.DateTime, "" + DateTime);
            values.put(User_Location.Latitude, "" + Latitude);
            values.put(User_Location.Longitude, "" + Longitude);
            values.put(User_Location.Location, "" + Location);

            if (checkNetWorkProviders()) {
                values.put(User_Location.isGPSOn, "true");
            } else {
                values.put(User_Location.isGPSOn, "false");
            }
            values.put(User_Location.isMockLocation, "false");
            Log.e("Location", ">> LOCAL DB ENTRY" + Location);

            SQLiteDatabase db = null;

            SQLiteHelper helper = new SQLiteHelper(UserTrackingReceiverIntentService.this, Constant.DatabaseName);
            helper.createDatabase();
            db = helper.openDatabase();

            boolean flagInsert = true;
            Cursor crsr = db.rawQuery("select " + User_Location.DateTime + " from " + User_Location.tableUser_Location_Offline + " where " + User_Location.DateTime + " = \"" + DateTime + "\"", null);
            if (crsr != null && crsr.getCount() > 0) flagInsert = false;

            if (flagInsert) db.insert(User_Location.tableUser_Location_Offline, null, values);

            crsr.close();
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
            Util.appendLog("Exception insertIntoDatabase() : " + e.getMessage());
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onHandleIntent(Intent intent) {
        this.context = this;

        if (!Util.isMyServiceRunning(LocationService.class, context)) {
            context.startService(new Intent(context, LocationService.class));
        }

        Calendar calendar = Calendar.getInstance();

        Util.appendLog("Tracking Alarm Called on: " + calendar.get(Calendar.DAY_OF_MONTH) + " / " + (calendar.get(Calendar.MONTH) + 1) + " == " + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE) + " : " + calendar.get(Calendar.SECOND) + " App Ver: " + Constant.APP_VERSION);

        //********************************** SETTING NEXT ALARM *********************************************
        Intent intentWakeFullBroacastReceiver = new Intent(context, SimpleWakefulReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 1001, intentWakeFullBroacastReceiver, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);

        if (calendar.get(Calendar.MINUTE) >= 0 && calendar.get(Calendar.MINUTE) < 30) {
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, 30);
            calendar.set(Calendar.SECOND, 0);
        } else if (calendar.get(Calendar.MINUTE) >= 30) {
            if (calendar.get(Calendar.HOUR_OF_DAY) == 23) {
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            } else {
                calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + 1);
            }
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        }

        //MARSHMALLOW OR ABOVE
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Build.MANUFACTURER.equals("GiONEE") && Build.MODEL.equals("S_plus")) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC,
                        calendar.getTimeInMillis(), sender);
                Util.appendLog(" *** GiONEE DEVICE(Intent Service) *** ");
//                        Toast.makeText(context, "This GiONEE", Toast.LENGTH_SHORT).show();
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(), sender);
            }
            //AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), sender);
            //alarmManager.setAlarmClock(alarmClockInfo, sender);
            Util.appendLog("UTIL M next track set @: " + calendar.get(Calendar.DAY_OF_MONTH) + " / " + (calendar.get(Calendar.MONTH) + 1) + " == " + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE) + " : " + calendar.get(Calendar.SECOND));
        }
        //LOLLIPOP 21 OR ABOVE
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), sender);
            alarmManager.setAlarmClock(alarmClockInfo, sender);
            Util.appendLog("L next track set @: " + calendar.get(Calendar.DAY_OF_MONTH) + " / " + (calendar.get(Calendar.MONTH) + 1) + " == " + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE) + " : " + calendar.get(Calendar.SECOND));
        }
        //KITKAT 19 OR ABOVE
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), sender);
            Util.appendLog("K next track set @: " + calendar.get(Calendar.DAY_OF_MONTH) + " / " + (calendar.get(Calendar.MONTH) + 1) + " == " + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE) + " : " + calendar.get(Calendar.SECOND));
        }
        //FOR BELOW KITKAT ALL DEVICES
        else {
            alarmManager.set(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(), sender);
            Util.appendLog("R next track set @: " + calendar.get(Calendar.DAY_OF_MONTH) + " / " + (calendar.get(Calendar.MONTH) + 1) + " == " + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE) + " : " + calendar.get(Calendar.SECOND));
        }

//        init();

        String latitude = intent.getStringExtra("lat");
        String longitude = intent.getStringExtra("lon");

        Util.appendLog("Latitude: " + latitude + " Longitude: " + longitude);

        DateTime = intent.getStringExtra("date");

        if (DateTime == null || DateTime.isEmpty()) {
            Date date = new Date();
            //
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            DateTime = sdf.format(date);
            DateTime = Util.locatToUTC(DateTime);
        }

        if (Util.ReadSharePrefrence(context,
                Constant.SHRED_PR.KEY_IS_LOGGEDIN).equals("true") &&
                !Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("1")
                && !Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("2")) {
            try {
                SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy/MM/dd");
                Date timeNow = sdfTime.parse(sdfTime.format(new Date()) + ":00");
                Date officeStartTime = sdfTime.parse(sdfDate.format(new Date()) + " " + Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_TRACKING_STARTTIME));
                Date officeEndTime = sdfTime.parse(sdfDate.format(new Date()) + " " + Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_TRACKING_ENDTIME));

                if (((timeNow.compareTo(officeStartTime) > 0) || (timeNow.compareTo(officeStartTime) == 0)) &&
                        ((timeNow.compareTo(officeEndTime) < 0) || (timeNow.compareTo(officeEndTime) == 0))) {
                    if (Util.isOnline(UserTrackingReceiverIntentService.this)) {
                        //NET ON
                        //GPS ON Or OFF
                        directCallAPIWithoutAsync(latitude, longitude, "");
                    } else {
                        //NET OFF
                        //GPS ON or OFF
                        insertIntoDatabase(DateTime, "", latitude, longitude);
                    }
                } else {
                    Util.appendLog("Tracking OFF: " + calendar.get(Calendar.DAY_OF_MONTH) + " / " + calendar.get(Calendar.MONTH) + " == " + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE) + " : " + calendar.get(Calendar.SECOND));
                }
            } catch (Exception e) {
                Log.e("", "Exception: " + e);
                Util.appendLog("Exception onHandleIntent() : " + e.getMessage());
                if (Util.isOnline(context)) {
                    TeamWorkApplication.LogOutClear();
                }
            }
        }

        Util.registerHeartbeatReceiver(context);
        SimpleWakefulReceiver.completeWakefulIntent(intent);
//        gps.stopUsingGPS();
    }

//    private void init() {
//        try {
//            User_ID = Util.ReadSharePrefrence(UserTrackingReceiverIntentService.this, Constant.SHRED_PR.KEY_USERID);
//        } catch (Exception e) {
//            Log.e("", "Exception: " + e);
//            Util.appendLog("Exception Init() : " + e.getMessage());
//        }
//    }

    /*
    *Checking for network provider Wifi/Network
     */
    public boolean checkNetWorkProviders() {
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);

        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);// Internet

        // getting network status
        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);//SIM car service provider

        if (!isGPSEnabled && !isNetworkEnabled) {
            // no network provider is enabled
            return false;
        } else {
            return true;
        }
    }

    //Calling API -- Location update server
    private void directCallAPIWithoutAsync(String latitude, String longitude, String location) {
        String response = "";
        try {
            Util.appendLog("API Called");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("date", "" + DateTime);
            jsonObject.put("latitude", "" + latitude);
            jsonObject.put("longitude", "" + longitude);
            jsonObject.put("location", location);
            if (checkNetWorkProviders()) {
                jsonObject.put(User_Location.isGPSOn, "true");
            } else {
                jsonObject.put(User_Location.isGPSOn, "false");
            }
            jsonObject.put(User_Location.isMockLocation, "false");

            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            params1.add(new BasicNameValuePair("JSON", jsonObject.toString()));

            Util.appendLog("Req passed to server: " + jsonObject.toString());

            Log.e("params1", ">>" + params1);

            response = Util.makeServiceCall(Constant.URL + "updateUserLocation", 2, params1, context);

            Util.appendLog(response);
            Log.e("response", ">>" + response);

            //java.net.UnknownHostException: Unable to resolve host "ec2-52-88-183-168.us-west-2.compute.amazonaws.com": No address associated with hostname
            if (response == null || response.equals("")) {
//                insertIntoDatabase(DateTime, latitude, longitude, location); //this was big mistack
                insertIntoDatabase(DateTime, "", latitude, longitude);
            }

            JSONObject json = new JSONObject(response);

            if (!json.getString("status").equals("Success")) {
                insertIntoDatabase(DateTime, "", latitude, longitude);
                Util.appendLog("server error in adding local entry when internet comes");
            }

        } catch (Exception e) {
            Util.appendLog("Exception directCallAPIWithoutAsync() : " + e.getMessage());
            insertIntoDatabase(DateTime, "", latitude, longitude);
            e.printStackTrace();
        }
    }
}
