/**
 * Copyright 2015-present Amberfog
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bigbang.superteam.slidingmap;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.tracking.CurrentLocationActivity;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.GPSTracker;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class MainFragment extends Fragment implements
        SlidingUpPanelLayout.PanelSlideListener, HeaderAdapter.ItemClickListener, OnMapReadyCallback {

    Activity activity;
    private LockableRecyclerView lvTeamMemebr;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    private View mTransparentView;
    private View mWhiteSpaceView;

    private HeaderAdapter mHeaderAdapter;
    private SupportMapFragment mMapFragment;

    private GoogleMap mMap;

    @InjectView(R.id.tvError)
    TextView tvError;
    @InjectView(R.id.rlTrackingDate)
    RelativeLayout rlTrackingDate;
    @InjectView(R.id.tvTrackingDate)
    TextView tvTrackingDate;
    @InjectView(R.id.tvTrackingYear)
    TextView tvTrackingYear;
    @InjectView(R.id.rlCurrentLocation)
    RelativeLayout rlCurrentLocation;

    TextView txtUserName;

    static String UserName = "", UserId = "";

    String TAG = "UserDailyReportActivity";
    static String selectedDate = "";
    Calendar calendar;
    static int curMonth, curYear, curDate;
    ArrayList<String> listTime = new ArrayList<>();
    static Bundle extras;
    GPSTracker gps;

    TransparentProgressDialog progressDialog;

    ArrayList<HashMap<String, String>> listLocations = new ArrayList<HashMap<String, String>>();
    ArrayList<Marker> markers = new ArrayList<Marker>();
    ArrayList<LatLng> Points = new ArrayList<LatLng>();
    ArrayList<HashMap<String, String>> markerTitles = new ArrayList<HashMap<String, String>>();

    static android.support.v4.app.FragmentManager fragmentManager;

    public static MainFragment newInstance(Bundle b, android.support.v4.app.FragmentManager fragmentManager1) {
        MainFragment f = new MainFragment();
        Bundle args = new Bundle();
        fragmentManager = fragmentManager1;
        extras = b;
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, rootView);

        activity = getActivity();
        final Typeface mFont = Typeface.createFromAsset(activity.getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) rootView.getRootView();
        Util.setAppFont(mContainer, mFont);

        lvTeamMemebr = (LockableRecyclerView) rootView.findViewById(android.R.id.list);
        lvTeamMemebr.setOverScrollMode(ListView.OVER_SCROLL_NEVER);

        mSlidingUpPanelLayout = (SlidingUpPanelLayout) rootView.findViewById(R.id.slidingLayout);
        mSlidingUpPanelLayout.setEnableDragViewTouchEvents(true);

        int mapHeight = getResources().getDimensionPixelSize(R.dimen.map_height_new);
        Log.e(TAG, "Map height is:- " + ">>>> " + mapHeight);
        mSlidingUpPanelLayout.setPanelHeight(mapHeight); // you can use different height here
        mSlidingUpPanelLayout.setScrollableView(lvTeamMemebr, mapHeight);

        mSlidingUpPanelLayout.setPanelSlideListener(this);

        // transparent view at the top of ListView
        mTransparentView = rootView.findViewById(R.id.transparentView);
        mWhiteSpaceView = rootView.findViewById(R.id.whiteSpaceView);

        collapseMap();

        mSlidingUpPanelLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mSlidingUpPanelLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                mSlidingUpPanelLayout.onPanelDragged(0);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.mapContainer, mMapFragment, "map");
        fragmentTransaction.commit();

        mMapFragment.getMapAsync(this);

        Init();

    }


    @Override
    public void onResume() {
        super.onResume();
        // In case Google Play services has since become available.
        //setUpMapIfNeeded();
        gps = new GPSTracker(activity);
        if (!gps.canGetLocation()) gps.showSettingsAlert();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        mMap.setMyLocationEnabled(false);

        Log.e("mMap1", ">>" + mMap);

        if (gps.canGetLocation()) {
            Double lat = gps.getLatitude();
            Double lng = gps.getLongitude();
            initMapOnCurrentLocation(lat, lng);
        }

        lvTeamMemebr.setItemAnimator(null);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lvTeamMemebr.setLayoutManager(layoutManager);
        mHeaderAdapter = new HeaderAdapter(activity, listLocations, this, false);
        lvTeamMemebr.setAdapter(mHeaderAdapter);

        if (Util.isOnline(activity)) {
            new getUserLocations().execute();
        } else
            Toast.makeText(activity, Constant.network_error, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPanelSlide(View view, float v) {
    }

    @Override
    public void onPanelCollapsed(View view) {
        expandMap();
    }

    @Override
    public void onPanelExpanded(View view) {
        collapseMap();
    }

    @Override
    public void onPanelAnchored(View view) {

    }


    @Override
    public void onItemClicked(int position) {
        mSlidingUpPanelLayout.collapsePane();
    }

    @OnClick(R.id.rlCurrentLocation)
    @SuppressWarnings("unused")
    public void CurrentLocation(View view) {
        Intent intent = new Intent(activity, CurrentLocationActivity.class);
        intent.putExtra("MemberID", "" + UserId);
        startActivity(intent);
        activity.overridePendingTransition(R.anim.enter_from_bottom,
                R.anim.hold_bottom);
    }

    @OnClick(R.id.rlTrackingDate)
    void selectDate() {
        try {

            calendar = Calendar.getInstance();
            curMonth = calendar.get(calendar.MONTH);
            curYear = calendar.get(calendar.YEAR);
            curDate = calendar.get(calendar.DAY_OF_MONTH);

            String date = "" + tvTrackingDate.getText().toString() + tvTrackingYear.getText().toString();
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM, yyyy");
            date = sdf1.format(sdf2.parse(date));

            String[] dates = date.split("/");
            calendar.set(Calendar.YEAR, Integer.parseInt(dates[2]));
            calendar.set(Calendar.MONTH, Integer.parseInt(dates[1]) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dates[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }

        DatePickerDialog dialog = new DatePickerDialog(activity, DatePickerDialog.THEME_HOLO_LIGHT, mytoDateListener, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DATE));
        dialog.getDatePicker().setMaxDate(new Date().getTime());
        dialog.show();
    }

    @OnClick(R.id.rlBack)
    @SuppressWarnings("unused")
    public void Back(View view) {
        activity.finish();
        activity.overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }


    private void Init() {

        progressDialog = new TransparentProgressDialog(activity, R.drawable.progressdialog, true);

        txtUserName = (TextView) activity.findViewById(R.id.tv_team_member);

        if (extras != null) {
            UserName = extras.getString("UserName");
            UserId = extras.getString("UserID");
            curDate = Integer.parseInt(extras.getString("day"));
            curMonth = Integer.parseInt(extras.getString("month"));
            curYear = Integer.parseInt(extras.getString("year"));
            selectedDate = curYear + "/" + curMonth + "/" + curDate;

            try {
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
                SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM, ");
                SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy");
                tvTrackingDate.setText("" + sdf2.format(sdf1.parse(selectedDate)).toUpperCase());
                tvTrackingYear.setText("" + sdf3.format(sdf1.parse(selectedDate)));

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        if (Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_USERID).equals(UserId)) {
            rlCurrentLocation.setVisibility(View.GONE);
        } else {
            rlCurrentLocation.setVisibility(View.VISIBLE);
        }

        gps = new GPSTracker(activity);
        // Getting status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity.getBaseContext());
        // Showing status
        if (status == ConnectionResult.SUCCESS) {

            if (gps.canGetLocation()) {
                Double lat = gps.getLatitude();
                Double lng = gps.getLongitude();
                initMapOnCurrentLocation(lat, lng);
            } else {
//                gps.showSettingsAlert();
            }

            txtUserName.setText("" + UserName);

        } else {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, activity, requestCode);
            dialog.show();
        }
    }

    private void collapseMap() {
        Log.e("collapseMap", "here...");
        if (listLocations.size() > 2) {
            if (mHeaderAdapter != null) {
                mHeaderAdapter.showSpace();
            }
            Log.e("mHeaderAdapter", "" + mHeaderAdapter);
            mTransparentView.setVisibility(View.GONE);
//        if (mMap != null && mLocation != null) {
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mLocation, 11f), 1000, null);
//        }
            lvTeamMemebr.setScrollingEnabled(true);
        }
    }

    private void expandMap() {
        Log.e("expandMap", "here...");
        if (listLocations.size() > 2) {
            if (mHeaderAdapter != null) {
                mHeaderAdapter.hideSpace();
            }
            Log.e("mHeaderAdapter", "" + mHeaderAdapter);
            mTransparentView.setVisibility(View.INVISIBLE);
            if (mMap != null) {
                mMap.animateCamera(CameraUpdateFactory.zoomTo(14f), 1000, null);
            }
            lvTeamMemebr.setScrollingEnabled(false);
        }
    }

    private void initMapOnCurrentLocation(double latitude, double longitude) {
        if (mMap != null) {

            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setCompassEnabled(false);

            LatLng latlng = new LatLng(latitude, longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latlng) // Sets the center of the map to Mountain
                    // View
                    .zoom(15) // Sets the zoom
                    .bearing(90) // Sets the orientation of the camera to east
                    .tilt(30) // Sets the tilt of the camera to 30 degrees
                    .build(); // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

        }
    }


    private DatePickerDialog.OnDateSetListener mytoDateListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

            try {
                String selectedDate1 = "" + year + "/" + (month + 1) + "/" + day;

                if (!selectedDate1.equals(selectedDate)) {
                    selectedDate = selectedDate1;
                    SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
                    SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM, ");
                    SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy");
                    tvTrackingDate.setText("" + sdf2.format(sdf1.parse(selectedDate)).toUpperCase());
                    tvTrackingYear.setText("" + sdf3.format(sdf1.parse(selectedDate)));

                    if (Util.isOnline(activity)) {
                        new getUserLocations().execute();
                    } else
                        Toast.makeText(activity, Constant.network_error, Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };


    public void loadMap(double latitude, double longitude, String title) {
        // TODO Auto-generated method stub

        if (mMap != null) {

            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setCompassEnabled(false);

            Double lat = Double.parseDouble(String.format("%.4f", latitude));
            Double lng = Double.parseDouble(String.format("%.4f", longitude));

            LatLng latlng = new LatLng(latitude, longitude);
            LatLng latlng_ = new LatLng(lat, lng);

            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("title", "" + title);
            hashMap.put("latitude", "" + latitude);
            hashMap.put("longitude", "" + longitude);
            hashMap.put("marker_pos", "-1");
            markerTitles.add(hashMap);


            boolean flag = true;

            if (flag) {
                IconGenerator tc = new IconGenerator(activity);
                Bitmap bmp = tc.makeIcon("" + title);
                Marker marker1 = mMap.addMarker(new MarkerOptions()
                        .position(latlng).icon(
                                BitmapDescriptorFactory
                                        .fromBitmap(bmp)));
                markers.add(marker1);
                markerTitles.get(markerTitles.size() - 1).put("marker_pos", "" + (markers.size() - 1));
                //Points.add(latlng_);

                Handler hn = new Handler();
                hn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setMarkers();
                    }
                }, 1000);
            }

            try {
                for (int i = 0; i < markerTitles.size()-1; i++) {

                    Double lat1 = Double.parseDouble(String.format("%.4f", Double.parseDouble(markerTitles.get(i).get("latitude"))));
                    Double lng1 = Double.parseDouble(String.format("%.4f", Double.parseDouble(markerTitles.get(i).get("longitude"))));
                    LatLng latlng1 = new LatLng(lat1, lng1);

                    if (latlng_.equals(latlng1)) {

                        String[] markerTitle = markerTitles.get(i).get("title").split(",");
                        Log.e("markerTitles", ">>" + markerTitles.get(i).get("title") + "::::" + i);
                        if (markerTitle.length == 3) {
                            markerTitles.get(i).put("title", "" + markerTitle[0] + "," + markerTitle[1] + ",.." +
                                    "" + title);
                        } else {
                            markerTitles.get(i).put("title", "" + markerTitles.get(i).get("title") + "," + title);
                        }


                        Log.e("Title:", ">>" + markerTitles.get(i).get("title"));
                        IconGenerator tc = new IconGenerator(activity);
                        Bitmap bmp = tc.makeIcon("" + markerTitles.get(i).get("title"));
                        int markerPos = Integer.parseInt(markerTitles.get(i).get("marker_pos"));
                        markers.get(markerPos).setIcon(BitmapDescriptorFactory
                                .fromBitmap(bmp));
                        flag = false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("", "Exception inside reload 1/1 : " + e);
            }
        }
        // ******************** //
    }

    private void setMarkers() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 50; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.moveCamera(cu);
        mMap.animateCamera(cu);

        if (Points.size() == 1) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Points.get(0), 15));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(Points.get(0))
                    .zoom(15)
                    .bearing(90)
                    .tilt(30)
                    .build();
            mMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }

    }


    class getUserLocations extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            if (progressDialog != null) progressDialog.show();
        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

            String startDate = Util.locatToUTC(selectedDate + " " + Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_STARTTIME));
            String endDate = Util.locatToUTC(selectedDate + " " + Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_ENDTIME));

            try {
                Date endDate1 = Util.sdf.parse(endDate);
                Calendar c = Calendar.getInstance();
                c.setTime(endDate1);
                c.add(Calendar.MINUTE, 10); // Add 10 min
                System.out.println(c.getTime());
                endDate = Util.sdf.format(c.getTime());
            } catch (Exception e) {
                e.printStackTrace();
            }

            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            params1.add(new BasicNameValuePair("CompanyID", "" + Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_ID)));
            params1.add(new BasicNameValuePair("MemberID", "" + UserId));
            params1.add(new BasicNameValuePair("StartDate", "" + startDate));
            params1.add(new BasicNameValuePair("EndDate", "" + endDate));
            Log.e("params1", ">>" + params1);
            String response = Util.makeServiceCall(Constant.URL
                    + "getUserLocations", 1, params1, activity);

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (progressDialog != null)
                if (progressDialog.isShowing()) progressDialog.dismiss();
            Log.e("result", ">>" + result);
//            Util.WriteFile(activity.getCacheDir().toString() + "/Tracking", UserId + ".txt", result);
            reload(result);
        }
    }

    private void reload(String result) {
        int day = -1, month = -1, year = -1;
        String errMessage = "";

        markerTitles.clear();
        listLocations.clear();
        Points.clear();
        markers.clear();
        mMap.clear();
        listTime.clear();

        try {
            JSONObject jsonObject = new JSONObject(result);
            String status = jsonObject.getString("status");
            errMessage = jsonObject.optString("message");
            if (status.equals("Success")) {
                JSONArray jDataArray = jsonObject.getJSONArray("data");
                Log.e("jDataArray", ">>" + jDataArray.length());
                for (int i = 0; i < jDataArray.length(); i++) {

                    JSONObject jsonObject1 = jDataArray.getJSONObject(i);
                    HashMap<String, String> hm = new HashMap<String, String>();

                    hm.put("UserID", "" + jsonObject1.getInt("userid"));
                    hm.put("FirstName", "" + jsonObject1.getString("FirstName"));
                    hm.put("LastName", "" + jsonObject1.getString("LastName"));
                    hm.put("ImageURL", "" + jsonObject1.getString("imageURL"));
                    hm.put("Time", "" + jsonObject1.getString("time"));
                    if (jsonObject1.getBoolean("isGPSOn")) {
                        hm.put("Location", "" + jsonObject1.getString("location"));
                    } else {
                        hm.put("Location", "" + "GPS disabled.");
                    }
                    hm.put("date", "" + jsonObject1.getString("date"));
                    hm.put("date", "" + Util.utcToLocalTime(hm.get("date")));
                    hm.put("Latitude", "" + jsonObject1.getDouble("latitude"));
                    hm.put("Longitude", "" + jsonObject1.getDouble("longitude"));
                    hm.put("isGPSOn", "" + jsonObject1.getBoolean("isGPSOn"));

                    try {
                        if (hm.get("Location").length() == 0 || hm.get("Location").equals("null")) {
                            hm.put("Location", "" + Util.getAddress(activity, Double.parseDouble("" + hm.get("Latitude")), Double.parseDouble(hm.get("Longitude"))));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    hm.put("day", "");
                    hm.put("month", "");
                    hm.put("year", "");
                    Log.e("date", ">>" + hm.get("date"));

                    try {
                        // SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
                        Date newDate = Util.sdf.parse("" + hm.get("date"));

                        SimpleDateFormat sdfDay = new SimpleDateFormat("dd");
                        SimpleDateFormat sdfMonth = new SimpleDateFormat("MM");
                        SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");

                        hm.put("day", "" + sdfDay.format(newDate));
                        hm.put("month", "" + sdfMonth.format(newDate));
                        hm.put("year", "" + sdfYear.format(newDate));

                        Log.e("day", ">>" + hm.get("day"));
                        Log.e("month", ">>" + hm.get("month"));
                        Log.e("year", ">>" + hm.get("year"));

                        day = Integer.parseInt(hm.get("day"));
                        month = Integer.parseInt(hm.get("month"));
                        year = Integer.parseInt(hm.get("year"));

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("", "Exception inside reload 1 : " + e);
                    }


                    boolean flag = false;
                    String strTime = "" + hm.get("date");
                    try {
                        //SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                        SimpleDateFormat dateFormatOutput = new SimpleDateFormat("mm");
                        Date date = Util.sdf.parse(strTime);
                        strTime = dateFormatOutput.format(date);
                        if (Integer.parseInt(strTime) % 30 == 0) flag = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("", "Exception inside reload 2 : " + e);
                    }

                    flag = true;
//                    Double Latitude = jsonObject1.getDouble("latitude");
//                    Double Longitude = jsonObject1.getDouble("longitude");

//                    if (Latitude == 0.0 && Longitude == 0.0) flag = false;


                    for (int j = 0; j < listTime.size(); j++) {
                        if (listTime.get(j).equals(hm.get("date"))) flag = false;
                    }

                    if (flag) {
                        listTime.add(hm.get("date"));

                        listLocations.add(hm);
                        String time = hm.get("date");
                        try {
                            //SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm:ss");
                            SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm a");
                            Date date = Util.sdf.parse(time);
                            time = sdf2.format(date);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (jsonObject1.getDouble("latitude") != 0.0 && jsonObject1.getDouble("longitude") != 0.0) {
                            loadMap(jsonObject1.getDouble("latitude"), jsonObject1.getDouble("longitude"), "" + time);
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("", "Exception inside reload 3 : " + e);
        }

        if (listLocations.size() == 0)
            tvError.setVisibility(View.VISIBLE);
        else tvError.setVisibility(View.GONE);

        tvError.setText("" + errMessage);

        lvTeamMemebr.setItemAnimator(null);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lvTeamMemebr.setLayoutManager(layoutManager);
        mHeaderAdapter = new HeaderAdapter(activity, listLocations, this, false);
        lvTeamMemebr.setAdapter(mHeaderAdapter);
        collapseMap();

    }

    private void addImageView(LinearLayout layout) {
        ImageView imageView = new ImageView(activity);
        imageView.setImageResource(R.drawable.user_icon);
        layout.addView(imageView);
    }

}
