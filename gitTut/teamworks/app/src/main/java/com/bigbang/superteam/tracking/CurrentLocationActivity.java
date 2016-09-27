package com.bigbang.superteam.tracking;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bigbang.superteam.GCMIntentService;
import com.bigbang.superteam.R;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.GPSTracker;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class CurrentLocationActivity extends FragmentActivity {

    GoogleMap map;
    TransparentProgressDialog progressDialog;
    ArrayList<Marker> markers = new ArrayList<Marker>();
    ArrayList<LatLng> Points = new ArrayList<LatLng>();
    ArrayList<HashMap<String, String>> markerTitles = new ArrayList<HashMap<String, String>>();
    String uniqueKey = "";
    String lastSync;

    MyReceiver myReceiver;
    GPSTracker gps;
    private Handler handler;
    long timePeriod = 1 * 5 * 1000; // 5 sec
    ArrayList<String> listUsers = new ArrayList<>();
    String MemberID = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_location);
        ButterKnife.inject(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            MemberID = extras.getString("MemberID");
        }

        gps = new GPSTracker(CurrentLocationActivity.this);
        // Getting status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        // Showing status
        if (status == ConnectionResult.SUCCESS) {
            init();

            markers.clear();
            Points.clear();

            if (gps.canGetLocation()) {
                Double lat = gps.getLatitude();
                Double lng = gps.getLongitude();
                initMapOnCurrentLocation(lat, lng);
            } else {
                gps.showSettingsAlert();
            }

            Handler hn = new Handler();
            hn.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Util.isOnline(getApplicationContext()))
                        new getUserCurrentLocations().execute();
                    else
                        Toast.makeText(getApplicationContext(), "" + Constant.network_error, Toast.LENGTH_SHORT).show();
                }
            }, 1000);
        } else {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
    }

    @OnClick(R.id.rlBack)
    @SuppressWarnings("unused")
    public void Back(View view) {
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
    }

    private void init() {

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        handler = new Handler();
        progressDialog = new TransparentProgressDialog(CurrentLocationActivity.this, R.drawable.progressdialog, true);
        map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMap();

        lastSync = Util.locatToUTC(Util.sdf.format(new Date()));
    }

    private void initMapOnCurrentLocation(double latitude, double longitude) {
        if (map != null) {

            map.getUiSettings().setZoomControlsEnabled(false);
            map.getUiSettings().setCompassEnabled(false);

            LatLng latlng = new LatLng(latitude, longitude);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
            map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latlng) // Sets the center of the map to Mountain
                            // View
                    .zoom(15) // Sets the zoom
                    .bearing(90) // Sets the orientation of the camera to east
                    .tilt(30) // Sets the tilt of the camera to 30 degrees
                    .build(); // Creates a CameraPosition from the builder
            map.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        gps = new GPSTracker(CurrentLocationActivity.this);
        if (!gps.canGetLocation()) gps.showSettingsAlert();

        try {
            myReceiver = new MyReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(GCMIntentService.ACTIVITY_SERVICE);
            registerReceiver(myReceiver, intentFilter);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        try {
            unregisterReceiver(myReceiver);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }


    class getUserCurrentLocations extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            try {
                if (progressDialog != null) progressDialog.show();
            } catch (Exception e) {
                Log.d("", "*** Exception: " + e);
            }

        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            String date = sdf1.format(new Date());

            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            params1.add(new BasicNameValuePair("AdminID", "" + Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID)));
            params1.add(new BasicNameValuePair("MemberID", "" + MemberID));
            Log.e("params1", ">>" + params1);
            String response = Util.makeServiceCall(Constant.URL + "getUserCurrentLocations", 1, params1, getApplicationContext());

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
                    uniqueKey = jsonObject.optString("uniqueKey");
                    handler.postDelayed(runnable, timePeriod);
                } else {

                    if (progressDialog != null)
                        if (progressDialog.isShowing()) progressDialog.dismiss();

                    Toast.makeText(getApplicationContext(), "" + jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                    finish();
                    overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (Util.isOnline(getApplicationContext()))
                new getNotifications().execute();

            handler.postDelayed(runnable, timePeriod);
        }
    };

    public class getNotifications extends AsyncTask<Void, String, String> {


        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            String NotificationType, response;

            NotificationType = "" + Constant.UPDATE_CURRENT_LOCATION;
            params1.add(new BasicNameValuePair("Application", "" + Constant.AppName));
            params1.add(new BasicNameValuePair("Type", NotificationType));
            params1.add(new BasicNameValuePair("LastSynchTime", "" + lastSync));
            Log.e("params1", ">>" + params1);
            response = Util.makeServiceCall(Constant.URL1 + "getNotifications", 1, params1, getApplicationContext());
            return response;


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            lastSync = Util.locatToUTC(Util.sdf.format(new Date()));

            if (progressDialog != null)
                if (progressDialog.isShowing()) progressDialog.dismiss();

            if (s != null) {
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray1 = jsonObject.getJSONArray("data");
                    Log.e("Approvals:", ">>" + jsonArray1);
                    for (int i = 0; i < jsonArray1.length(); i++) {
                        JSONObject jObj = jsonArray1.getJSONObject(i);
                        String TransactionID = "" + jObj.optString("TransactionID");

                        if (TransactionID.equals(uniqueKey)) {

                            JSONObject jsonObject1 = jObj.optJSONObject("data");
                            String MemberID = "" + jsonObject1.getString("MemberID");
                            String latitude = "" + jsonObject1.getString("latitude");
                            String longitude = "" + jsonObject1.getString("longitude");
                            String FirstName = "" + jsonObject1.getString("FirstName");
                            String LastName = "" + jsonObject1.getString("LastName");

                            boolean flag = true;
                            for (int j = 0; j < listUsers.size(); j++) {
                                if (listUsers.get(j).equals(MemberID)) flag = false;
                            }

                            if (flag) {
                                listUsers.add(MemberID);

                                if (progressDialog != null)
                                    if (progressDialog.isShowing()) progressDialog.dismiss();

                                try {
                                    Double Latitude = Double.parseDouble(latitude);
                                    Double Longitude = Double.parseDouble(longitude);

                                    if (Latitude != 0.0 && Longitude != 0.0)
                                        loadMap(Double.parseDouble(latitude),
                                                Double.parseDouble(longitude), "" + FirstName);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            Bundle extras = arg1.getExtras();
            if (extras != null) {

                String latitude = extras.getString("latitude");
                String longitude = extras.getString("longitude");
                String FirstName = extras.getString("FirstName");
                String LastName = extras.getString("LastName");
                String uniqueKey_ = extras.getString("uniqueKey");
                String MemberID = extras.getString("MemberID");

                if (uniqueKey.equals(uniqueKey_)) {

                    boolean flag = true;
                    for (int j = 0; j < listUsers.size(); j++) {
                        if (listUsers.get(j).equals(MemberID)) flag = false;
                    }

                    if (flag) {
                        listUsers.add(MemberID);

                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();

                        try {
                            Double Latitude = Double.parseDouble(latitude);
                            Double Longitude = Double.parseDouble(longitude);

                            if (Latitude != 0.0 && Longitude != 0.0)
                                loadMap(Double.parseDouble(latitude),
                                        Double.parseDouble(longitude), "" + FirstName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

            }
        }
    }

    public void loadMap(double latitude, double longitude, String title) {
        // TODO Auto-generated method stub

        if (map != null) {

            map.getUiSettings().setZoomControlsEnabled(false);
            map.getUiSettings().setCompassEnabled(false);

            HashMap<String, String> hashMap = new HashMap<>();
            String[] titles = title.split("\\s+");
            hashMap.put("title", "" + titles[0]);
            markerTitles.add(hashMap);
            latitude = Double.parseDouble(String.format("%.2f", latitude));
            longitude = Double.parseDouble(String.format("%.2f", longitude));

            LatLng latlng = new LatLng(latitude, longitude);

            boolean flag = true;
            for (int i = 0; i < Points.size(); i++) {
                if (latlng.equals(Points.get(i))) {
                    markerTitles.get(i).put("title", "" + markerTitles.get(i).get("title") + "," + titles[0]);
                    Log.e("Title:", ">>" + markerTitles.get(i).get("title"));
                    IconGenerator tc = new IconGenerator(this);
                    Bitmap bmp = tc.makeIcon("" + markerTitles.get(i).get("title"));
                    markers.get(i).setIcon(BitmapDescriptorFactory
                            .fromBitmap(bmp));
                    flag = false;
                }
            }

            if (flag) {
                IconGenerator tc = new IconGenerator(this);
                Bitmap bmp = tc.makeIcon("" + titles[0]);
                Marker marker1 = map.addMarker(new MarkerOptions()
                        .position(latlng).icon(
                                BitmapDescriptorFactory
                                        .fromBitmap(bmp)));
                markers.add(marker1);
                Points.add(latlng);

                Handler hn = new Handler();
                hn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setMarkers();
                    }
                }, 1000);
            }

        }
    }

    private void setMarkers() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 50; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.moveCamera(cu);
        map.animateCamera(cu);
        //map.animateCamera(CameraUpdateFactory.zoomTo(15));

        if (Points.size() == 1) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(Points.get(0), 15));
            map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
            CameraPosition cameraPosition1 = new CameraPosition.Builder()
                    .target(Points.get(0))
                    .zoom(15)
                    .bearing(90)
                    .tilt(30)
                    .build();
            map.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition1));
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        handler.removeCallbacks(runnable, null);
    }

}
