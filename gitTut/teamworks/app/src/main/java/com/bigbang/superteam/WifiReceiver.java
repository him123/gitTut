package com.bigbang.superteam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import com.bigbang.superteam.dataObjs.OfflineWork;
import com.bigbang.superteam.dataObjs.User_Location;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.GPSTracker;
import com.bigbang.superteam.util.LocationService;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by USER 8 on 5/9/2015.
 */
public class WifiReceiver extends BroadcastReceiver {

    public static SQLiteDatabase db = null;
    static SQLiteHelper helper;
    Context context;

    private static void DeleteFromDatabase(HashMap<String, String> hashMap) {
        // TODO Auto-generated method stub
        //delete from Offline Database
        int pk = Integer.parseInt(hashMap.get("pk"));
        int isDel = db.delete(User_Location.tableUser_Location_Offline, "pk=" + pk, null);
        Util.appendLog("Deleted row after sent data to server ID: " + pk);
    }

    private String getTime() {
        Calendar calendar = Calendar.getInstance();

        return " @ " + calendar.get(Calendar.DAY_OF_MONTH) + " / " + (calendar.get(Calendar.MONTH) + 1) + " == " + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE) + " : " + calendar.get(Calendar.SECOND);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("WifiReceiver", "Here..");

        Intent i = new Intent(context, LocationService.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(i);

        final ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        final android.net.NetworkInfo wifi = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final android.net.NetworkInfo mobile = connMgr
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        if (wifi.isAvailable() || mobile.isAvailable()) {
            // Do something
            Log.d("Netowk Available ", "Flag No 1");

            this.context = context;

            helper = new SQLiteHelper(context, Constant.DatabaseName);
            helper.createDatabase();
            db = helper.openDatabase();
            if (Util.isOnline(context)) {
                Log.e("Offline receiver", "Checking Offline Tables");
                OfflineWork.callApi(db, context);
            }

            String keyRoleID = Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID);
            if (Util.ReadSharePrefrence(context,
                    Constant.SHRED_PR.KEY_IS_LOGGEDIN).equals("true") && (keyRoleID.equals("3") || keyRoleID.equals("4"))) {
                if (Util.isOnline(context)) {
                    Cursor crsr = db.rawQuery("select * from " + User_Location.tableUser_Location_Offline, null);
                    if (crsr != null) {
                        Log.e("count", ">>" + crsr.getCount());
                        if (crsr.getCount() > 0) {
                            crsr.moveToFirst();
                            do {
                                HashMap<String, String> map = new HashMap<String, String>();
                                map.put("pk", "" + crsr.getString(crsr.getColumnIndex("pk")));
                                map.put("DateTime", "" + crsr.getString(crsr.getColumnIndex("DateTime")));
                                map.put("Latitude", "" + crsr.getString(crsr.getColumnIndex("Latitude")));
                                map.put("Longitude", "" + crsr.getString(crsr.getColumnIndex("Longitude")));
                                map.put("Location", "" + crsr.getString(crsr.getColumnIndex("Location")));
                                map.put("isGPSOn", "" + crsr.getString(crsr.getColumnIndex("isGPSOn")));
                                map.put("isMockLocation", "" + crsr.getString(crsr.getColumnIndex("isMockLocation")));

                                new UpdateLocation(map).execute();
                            } while (crsr.moveToNext());
                        }
                    }
                    if (crsr != null) crsr.close();
                }
            }


            if (Util.ReadSharePrefrence(context,
                    Constant.SHRED_PR.KEY_IS_LOGGEDIN).equals("true") && (keyRoleID.equals("3") || keyRoleID.equals("4"))) {
                if (Util.isOnline(context)) {
                    Cursor crsr = db.rawQuery("select * from " + Constant.OfflineDataLeaveAttendance, null);
                    if (crsr != null) {
                        Log.d("count", ">>" + crsr.getCount());
                        if (crsr.getCount() > 0) {
                            crsr.moveToFirst();
                            do {
                                String actionName = crsr.getString(crsr.getColumnIndex("action_name"));
                                String actionType = crsr.getString(crsr.getColumnIndex("method_type"));
                                int pk = crsr.getInt(crsr.getColumnIndex("id"));
                                String jsonStr = crsr.getString(crsr.getColumnIndex("jsondata"));

                                new offlineDataAsync(actionName, Integer.parseInt(actionType), jsonStr, pk).execute();

                            } while (crsr.moveToNext());
                        }
                    }
                    if (crsr != null) crsr.close();

                }
            }
        }
    }

    class UpdateLocation extends AsyncTask<Void, String, String> {

        HashMap<String, String> hashMap;
        GPSTracker gps;

        public UpdateLocation(HashMap<String, String> hashMap) {
            // TODO Auto-generated constructor stub
            this.hashMap = hashMap;
            gps = new GPSTracker(context);
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub
            String response = "";
            try {
//                hashMap.put("Location", "" + Util.getAddress(context, Double.parseDouble("" + hashMap.get("Latitude")), Double.parseDouble(hashMap.get("Longitude"))));

                JSONObject jsonObject = new JSONObject();

                jsonObject.put("date", "" + hashMap.get("DateTime"));
                jsonObject.put("latitude", "" + hashMap.get("Latitude"));
                jsonObject.put("longitude", "" + hashMap.get("Longitude"));
                jsonObject.put("location", "" + hashMap.get("Location"));
                jsonObject.put(User_Location.isGPSOn, "" + hashMap.get("isGPSOn"));
                jsonObject.put(User_Location.isMockLocation, "" + hashMap.get("isMockLocation"));

                List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
                params1.add(new BasicNameValuePair("JSON", jsonObject.toString()));

                Util.appendLog("WifiReceiver passed to server: " + jsonObject.toString() + getTime());

                Log.e("params1", ">>" + params1);
                response = Util.makeServiceCall(Constant.URL + "updateUserLocation", 2, params1, context);

                Util.appendLog(response+ getTime());
                JSONObject json = new JSONObject(response);

                if (!json.getString("status").equals("Success")) {
                    Util.appendLog("server error in adding local entry when internet comes"+ getTime());
                }
            } catch (Exception e) {
                Util.appendLog("Exception in adding local entry when internet comes " + e.getMessage()+ getTime());
                Log.e("", "Exception: " + e);
            }

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            Log.e("result", ">>" + result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.getString("status");
                if (status.equals("Success")) {
                    DeleteFromDatabase(hashMap);
                } else {
                    DeleteFromDatabase(hashMap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class offlineDataAsync extends AsyncTask<Void, String, String> {

        String jsonStr, actionName;
        int methodType, primaryKey;

        public offlineDataAsync(String actionNm, int type, String str, int primary) {
            // TODO Auto-generated constructor stub
            jsonStr = str;
            actionName = actionNm;
            methodType = type;
            primaryKey = primary;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);

            JSONObject jObject = null;
            try {
                jObject = new JSONObject(jsonStr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Iterator<?> keys = jObject.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                String value = null;
                if (jObject != null)
                    try {
                        value = jObject.getString(key);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                params1.add(new BasicNameValuePair(key, "" + value));
            }
            Log.e("params1", ">>" + params1);
            String response = Util.makeServiceCall(Constant.URL + actionName, methodType, params1, context);
            Log.e("response", "***>>" + response);
            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            try {
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");

                if (status.equals("Success") || status.equals("Fail")) {
                    removeFromOfflineTable(primaryKey);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void removeFromOfflineTable(int pk) {
        db.delete(Constant.OfflineDataLeaveAttendance, "id=" + pk, null);
    }
}
