package com.bigbang.superteam.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.slidingmap.HeaderAdapter;
import com.bigbang.superteam.slidingmap.LockableRecyclerView;
import com.bigbang.superteam.slidingmap.SlidingUpPanelLayout;
import com.bigbang.superteam.tracking.CurrentLocationActivity;
import com.bigbang.superteam.tracking.UserDailyReportActivity;
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

/**
 * Created by USER 8 on 02-Nov-15.
 */
public class TrackingFragment extends Fragment implements
        SlidingUpPanelLayout.PanelSlideListener, OnMapReadyCallback, HeaderAdapter.ItemClickListener {

    @InjectView(R.id.tvError)
    TextView tvError;

    private LockableRecyclerView lvTeamMemebr;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;

    private View mTransparentView;
    private View mWhiteSpaceView;

    private HeaderAdapter mHeaderAdapter;
    private SupportMapFragment mMapFragment;
    GoogleMap mMap;
    TransparentProgressDialog progressDialog;
    String TAG = "TrackingFragment";

    ArrayList<HashMap<String, String>> listTeamMembers = new ArrayList<HashMap<String, String>>();
    ArrayList<Marker> markers = new ArrayList<Marker>();
    ArrayList<LatLng> Points = new ArrayList<LatLng>();
    ArrayList<HashMap<String, String>> markerTitles = new ArrayList<HashMap<String, String>>();

    GPSTracker gps;
    ArrayList<String> listUsers = new ArrayList<>();
    String selectedDate = "";
    Calendar calendar;
    int curMonth, curYear, curDate;
    MyReceiver myReceiver;
    Activity activity;

    static android.support.v4.app.FragmentManager fragmentManager;

    public static Fragment newInstance(android.support.v4.app.FragmentManager fragmentManager1) {
        TrackingFragment fragment = new TrackingFragment();
        fragmentManager = fragmentManager1;
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        View rootView = inflater.inflate(R.layout.fragment_tracking, container, false);
        ButterKnife.inject(this, rootView);

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

        if (fragmentManager != null) {
            mMapFragment = SupportMapFragment.newInstance();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.mapContainer, mMapFragment, "map");
            fragmentTransaction.commit();

            setMap();
            Init();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            myReceiver = new MyReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(activity.ACTIVITY_SERVICE);
            activity.registerReceiver(myReceiver, intentFilter);
        } catch (Exception e) {
            // TODO: handle exception
        }
        Log.e("selectedDate", ">>" + selectedDate);

    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        try {
            activity.unregisterReceiver(myReceiver);
        } catch (Exception e) {
            // TODO: handle exception
        }
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
    public void onItemClicked(int pos) {
        //mSlidingUpPanelLayout.collapsePane();
        Log.e("pos", ">>" + pos);
        if (pos > 0) {
            int position = pos - 1;
            if (listTeamMembers.size() > position) {
                Intent intent = new Intent(activity, UserDailyReportActivity.class);
                intent.putExtra("UserName", "" + listTeamMembers.get(position).get("FirstName") + " " + listTeamMembers.get(position).get("LastName"));
                intent.putExtra("UserID", "" + listTeamMembers.get(position).get("UserID"));
                intent.putExtra("day", "" + listTeamMembers.get(position).get("day"));
                intent.putExtra("month", "" + listTeamMembers.get(position).get("month"));
                intent.putExtra("year", "" + listTeamMembers.get(position).get("year"));
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.enter_from_left,
                        R.anim.hold_bottom);
            }
        } else {
            expandMap();
            mSlidingUpPanelLayout.collapsePane();
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(false);

        Log.e("mMap1", ">>" + mMap);


        if (mMap != null) {

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    Log.e("setOnMapClickListener", "here");
                    expandMap();
                }
            });

            if (gps.canGetLocation()) {
                Double lat = gps.getLatitude();
                Double lng = gps.getLongitude();
                initMapOnCurrentLocation(lat, lng);
            }

            lvTeamMemebr.setItemAnimator(null);
            final LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            lvTeamMemebr.setLayoutManager(layoutManager);
            mHeaderAdapter = new HeaderAdapter(activity, listTeamMembers, this, false);
            lvTeamMemebr.setAdapter(mHeaderAdapter);

            if (Util.isOnline(activity)) {
                new getUserLocations().execute();
            } else
                Toast.makeText(activity, Constant.network_error, Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.rlCurrentLocation)
    @SuppressWarnings("unused")
    public void CurrentLocation(View view) {
        Intent intent = new Intent(activity, CurrentLocationActivity.class);
        intent.putExtra("MemberID", "0");
        startActivity(intent);
        activity.overridePendingTransition(R.anim.enter_from_bottom,
                R.anim.hold_bottom);
    }

    private void Init() {

        progressDialog = new TransparentProgressDialog(activity, R.drawable.progressdialog, true);

        try {
            calendar = Calendar.getInstance();
            curMonth = calendar.get(calendar.MONTH);
            curYear = calendar.get(calendar.YEAR);
            curDate = calendar.get(calendar.DAY_OF_MONTH);

            selectedDate = "" + curYear + "/" + (curMonth + 1) + "/" + curDate;

        } catch (Exception e) {
            e.printStackTrace();
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
                gps.showSettingsAlert();
            }

        } else {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, activity, requestCode);
            dialog.show();
        }
    }

    private void setMap() {
        mMapFragment.getMapAsync(this);
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            Bundle extras = arg1.getExtras();
            if (extras != null) {

                try {
                    String selectedDate1 = extras.getString("trackingDate");
                    if (selectedDate1 != null) {
                        if (!selectedDate1.equals(selectedDate)) {
                            selectedDate = selectedDate1;

                            if (mMap != null) {
                                if (Util.isOnline(activity)) {
                                    new getUserLocations().execute();
                                } else {
                                    Toast.makeText(activity, Constant.network_error, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                setMap();
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void collapseMap() {
        Log.e("collapseMap", "here...");
        if (listTeamMembers.size() > 2) {
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
        if (listTeamMembers.size() > 2) {
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
                    .target(latlng) // Sets the center of the mMap to Mountain
                    // View
                    .zoom(15) // Sets the zoom
                    .bearing(90) // Sets the orientation of the camera to east
                    .tilt(30) // Sets the tilt of the camera to 30 degrees
                    .build(); // Creates a CameraPosition from the builder
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
            params1.add(new BasicNameValuePair("AdminID", "" + Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_USERID)));
            params1.add(new BasicNameValuePair("CompanyID", "" + Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_ID)));
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
//            Util.WriteFile(activity.getCacheDir().toString() + "/Tracking", Constant.TrackingFile, result);
            reload(result);
        }
    }

    private void reload(String result) {
        int day = -1, month = -1, year = -1;

        String errMessage = "";
        markerTitles.clear();
        listUsers.clear();
        listTeamMembers.clear();
        markers.clear();
        Points.clear();
        mMap.clear();

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

                    hm.put("UserID", "" + jsonObject1.optString("userid"));
                    hm.put("FirstName", "" + jsonObject1.optString("FirstName"));
                    hm.put("LastName", "" + jsonObject1.optString("LastName"));
                    hm.put("ImageURL", "" + jsonObject1.optString("imageURL"));
                    hm.put("Time", "" + jsonObject1.optString("time"));
                    if (jsonObject1.getBoolean("isGPSOn")) {
                        hm.put("Location", "" + jsonObject1.getString("location"));
                    } else {
                        hm.put("Location", "" + "GPS disabled.");
                    }
                    hm.put("date", "" + jsonObject1.optString("date"));
                    hm.put("date", "" + Util.utcToLocalTime(hm.get("date")));
                    hm.put("Latitude", "" + jsonObject1.optDouble("latitude"));
                    hm.put("Longitude", "" + jsonObject1.optDouble("longitude"));
                    hm.put("isGPSOn", "" + jsonObject1.optBoolean("isGPSOn"));

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
                    }

                    flag = true;
                    Double Latitude = jsonObject1.getDouble("latitude");
                    Double Longitude = jsonObject1.getDouble("longitude");

//                    if (Latitude == 0.0 && Longitude == 0.0) flag = false;

                    for (int j = 0; j < listUsers.size(); j++) {
                        if (listUsers.get(j).equals(hm.get("UserID"))) flag = false;
                    }

                    Log.e("UserID:", ">>" + hm.get("UserID") + ":" + flag);
                    if (flag) {
                        listUsers.add(hm.get("UserID"));
                        listTeamMembers.add(hm);
                        if (jsonObject1.getDouble("latitude") != 0.0 && jsonObject1.getDouble("longitude") != 0.0) {
                            loadMap(jsonObject1.getDouble("latitude"), jsonObject1.getDouble("longitude"), "" + hm.get("FirstName"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (listTeamMembers.size() == 0)
            tvError.setVisibility(View.VISIBLE);
        else tvError.setVisibility(View.GONE);

        tvError.setText("" + errMessage);

        lvTeamMemebr.setItemAnimator(null);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        lvTeamMemebr.setLayoutManager(layoutManager);
        mHeaderAdapter = new HeaderAdapter(activity, listTeamMembers, this, false);
        lvTeamMemebr.setAdapter(mHeaderAdapter);
        collapseMap();

    }

    public void loadMap(double latitude, double longitude, String title) {
        // TODO Auto-generated method stub

        if (mMap != null) {

            mMap.getUiSettings().setZoomControlsEnabled(false);
            mMap.getUiSettings().setCompassEnabled(false);

            HashMap<String, String> hashMap = new HashMap<>();
            String[] titles = title.split("\\s+");
            hashMap.put("title", "" + titles[0]);
            markerTitles.add(hashMap);
            Double lat = Double.parseDouble(String.format("%.2f", latitude));
            Double lng = Double.parseDouble(String.format("%.2f", longitude));

            LatLng latlng = new LatLng(latitude, longitude);
            LatLng latlng_ = new LatLng(lat, lng);

            boolean flag = true;
            for (int i = 0; i < Points.size(); i++) {
                if (latlng_.equals(Points.get(i))) {
                    markerTitles.get(i).put("title", "" + markerTitles.get(i).get("title") + "," + titles[0]);
                    Log.e("Title:", ">>" + markerTitles.get(i).get("title"));
                    IconGenerator tc = new IconGenerator(activity);
                    Bitmap bmp = tc.makeIcon("" + markerTitles.get(i).get("title"));
                    markers.get(i).setIcon(BitmapDescriptorFactory
                            .fromBitmap(bmp));
                    flag = false;
                }
            }

            if (flag) {
                IconGenerator tc = new IconGenerator(activity);
                Bitmap bmp = tc.makeIcon("" + titles[0]);
                Marker marker1 = mMap.addMarker(new MarkerOptions()
                        .position(latlng).icon(
                                BitmapDescriptorFactory
                                        .fromBitmap(bmp)));
                markers.add(marker1);
                Points.add(latlng_);

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
        int padding = 50; // offset from edges of the mMap in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.moveCamera(cu);
        mMap.animateCamera(cu);
        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));

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


}