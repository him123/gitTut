package com.bigbang.superteam.tracking;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.TeamMemberAdapter;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.GPSTracker;
import com.bigbang.superteam.util.SQLiteHelper;
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
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class TrackingActivity extends FragmentActivity {

    @InjectView(R.id.tvError)
    TextView tvError;

    ListView lvTeamMemebr, lvTeamMemebrFullScreen;
    GoogleMap map;
    TransparentProgressDialog progressDialog;
    RelativeLayout MainRl, SettingsRl, rlSelectDate, CurrentLocationRl, SetTimePeriodRl, rlFilter, rlDate, rlViewMap, rlViewList, rlViewBoth, rlMap;
    LinearLayout SettingsBoxLl;
    Spinner spinnerDay, spinnerMonth, spinnerYear;
    TextView tvDate;
    int temp_selected;
    boolean isAPICalling = false;
    String TAG = "TrackingActivity";

    ArrayList<HashMap<String, String>> listTeamMembers = new ArrayList<HashMap<String, String>>();
    ArrayList<Marker> markers = new ArrayList<Marker>();
    ArrayList<LatLng> Points = new ArrayList<LatLng>();
    ArrayList<HashMap<String, String>> markerTitles = new ArrayList<HashMap<String, String>>();

    SQLiteHelper helper;
    public static SQLiteDatabase db = null;
    GPSTracker gps;

    ArrayList<String> monthArray = new ArrayList<>();
    ArrayList<String> yearArray = new ArrayList<>();
    ArrayList<String> dateArray = new ArrayList<>();
    String[] monthName;
    ArrayList<String> listUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking);
        ButterKnife.inject(this);

        helper = new SQLiteHelper(this, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        gps = new GPSTracker(TrackingActivity.this);
        // Getting status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        // Showing status
        if (status == ConnectionResult.SUCCESS) {

            init();

            Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
            int curMonth = calendar.get(calendar.MONTH);
            int curYear = calendar.get(calendar.YEAR);
            int curDate = calendar.get(calendar.DAY_OF_MONTH);
            calculateSpinnerDates(curDate, curMonth, curYear);

            setDate();

            if (gps.canGetLocation()) {
                Double lat = gps.getLatitude();
                Double lng = gps.getLongitude();
                initMapOnCurrentLocation(lat, lng);
            } else {
//                gps.showSettingsAlert();
            }
        } else {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        }


    }


    @Override
    protected void onResume() {
        super.onResume();
//        gps = new GPSTracker(TrackingActivity.this);
//        if (!gps.canGetLocation()) gps.showSettingsAlert();
    }

    private void init() {
        progressDialog = new TransparentProgressDialog(TrackingActivity.this, R.drawable.progressdialog, true);
        MainRl = (RelativeLayout) findViewById(R.id.rl_main);
        SettingsRl = (RelativeLayout) findViewById(R.id.rl_settings);
        SettingsBoxLl = (LinearLayout) findViewById(R.id.ll_settings_box);
        rlSelectDate = (RelativeLayout) findViewById(R.id.rlSelectDate);
        CurrentLocationRl = (RelativeLayout) findViewById(R.id.rl_current_location);
        SetTimePeriodRl = (RelativeLayout) findViewById(R.id.rl_set_time_period);
        rlFilter = (RelativeLayout) findViewById(R.id.rl_filtr);

        rlMap = (RelativeLayout) findViewById(R.id.rl_map);
        rlViewMap = (RelativeLayout) findViewById(R.id.rl_view_map);
        rlViewList = (RelativeLayout) findViewById(R.id.rl_view_list);
        rlViewBoth = (RelativeLayout) findViewById(R.id.rl_view_both);

        rlDate = (RelativeLayout) findViewById(R.id.rl_date);
        tvDate = (TextView) findViewById(R.id.tv_date);

        map = ((MapFragment) getFragmentManager()
                .findFragmentById(R.id.map)).getMap();

        lvTeamMemebr = (ListView) findViewById(R.id.lv_team_members);
        lvTeamMemebrFullScreen = (ListView) findViewById(R.id.lv_team_members_full_screen);

        spinnerDay = (Spinner) findViewById(R.id.spinnerDay);
        spinnerMonth = (Spinner) findViewById(R.id.spinnerMonth);
        spinnerYear = (Spinner) findViewById(R.id.spinnerYear);


        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int currentYear = calendar.get(Calendar.YEAR);

        monthName = getResources().getStringArray(R.array.month_array);
        ArrayAdapter<String> adapterDate = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, dateArray);
        adapterDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(adapterDate);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, monthArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);

        ArrayAdapter<String> adapterYear = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, yearArray);
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(adapterYear);

        spinnerMonth.setSelection(calendar.get(calendar.MONTH));
        int curDate = calendar.get(calendar.DAY_OF_MONTH);
        Log.d(TAG, "Date of month is:- " + curDate);

        spinnerDay.setSelection(calendar.get(Calendar.DATE) - 1);
        String[] years = getResources().getStringArray(R.array.year_array);
        for (int i = 0; i < years.length; i++) {
            if (years[i].equals("" + currentYear)) {
                spinnerYear.setSelection(i);
            }
        }

        MainRl.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                SettingsBoxLl.setVisibility(View.GONE);
                return false;
            }
        });

        SettingsRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsBoxLl.setVisibility(View.VISIBLE);
            }
        });

        CurrentLocationRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SettingsBoxLl.setVisibility(View.GONE);
                Intent intent = new Intent(TrackingActivity.this, CurrentLocationActivity.class);
                startActivity(intent);
            }
        });

        SetTimePeriodRl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsBoxLl.setVisibility(View.GONE);
                Dialog dialog = onCreateDialogSingleChoice();
                dialog.show();
            }
        });

        rlSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsBoxLl.setVisibility(View.GONE);
                rlDate.setVisibility(View.VISIBLE);
            }
        });

        rlFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsBoxLl.setVisibility(View.GONE);
                rlDate.setVisibility(View.VISIBLE);
            }
        });

        rlViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsBoxLl.setVisibility(View.GONE);
                lvTeamMemebr.setVisibility(View.GONE);
                lvTeamMemebrFullScreen.setVisibility(View.GONE);
                rlMap.setVisibility(View.VISIBLE);
            }
        });

        rlViewList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsBoxLl.setVisibility(View.GONE);
                lvTeamMemebr.setVisibility(View.GONE);
                rlMap.setVisibility(View.GONE);
                lvTeamMemebrFullScreen.setVisibility(View.VISIBLE);
            }
        });

        rlViewBoth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsBoxLl.setVisibility(View.GONE);
                rlMap.setVisibility(View.VISIBLE);
                lvTeamMemebr.setVisibility(View.VISIBLE);
                lvTeamMemebrFullScreen.setVisibility(View.GONE);
            }
        });

        spinnerDay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rlDate.setVisibility(View.GONE);
                String strMonth = monthName[Integer.parseInt("" + spinnerMonth.getSelectedItemPosition())];
                String strDay = "" + spinnerDay.getSelectedItem() + Util.getDayNumberSuffix(Integer.parseInt("" + spinnerDay.getSelectedItem()));
                tvDate.setText(strDay + " " + strMonth);

                if (Util.isOnline(getApplicationContext())) {
                    if (!isAPICalling)
                        new getUserLocations().execute();
                } else {
                    Toast.makeText(getApplicationContext(), "" + Constant.network_error, Toast.LENGTH_SHORT).show();
//                    reload(Util.ReadFile(getCacheDir().toString() + "/Tracking", Constant.TrackingFile));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rlDate.setVisibility(View.GONE);
                String strMonth = monthName[Integer.parseInt("" + spinnerMonth.getSelectedItemPosition())];
                String strDay = "" + spinnerDay.getSelectedItem() + Util.getDayNumberSuffix(Integer.parseInt("" + spinnerDay.getSelectedItem()));
                tvDate.setText(strDay + " " + strMonth);

                if (!isAPICalling) {
                    setSpinnerDay();
                    if (Util.isOnline(getApplicationContext())) {
                        new getUserLocations().execute();
                    } else {
                        Toast.makeText(getApplicationContext(), "" + Constant.network_error, Toast.LENGTH_SHORT).show();
//                        reload(Util.ReadFile(getCacheDir().toString() + "/Tracking", Constant.TrackingFile));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                rlDate.setVisibility(View.GONE);
                String strMonth = monthName[Integer.parseInt("" + spinnerMonth.getSelectedItemPosition())];
                String strDay = "" + spinnerDay.getSelectedItem() + Util.getDayNumberSuffix(Integer.parseInt("" + spinnerDay.getSelectedItem()));
                tvDate.setText(strDay + " " + strMonth);

                if (!isAPICalling) {
                    setSpinnerMonth();
                    if (Util.isOnline(getApplicationContext())) {
                        new getUserLocations().execute();
                    } else {
                        Toast.makeText(getApplicationContext(), "" + Constant.network_error, Toast.LENGTH_SHORT).show();
//                        reload(Util.ReadFile(getCacheDir().toString() + "/Tracking", Constant.TrackingFile));
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (lvTeamMemebr.getVisibility() == View.GONE) {
                    lvTeamMemebr.setVisibility(View.VISIBLE);
                } else {
                    lvTeamMemebr.setVisibility(View.GONE);
                }
            }
        });
    }

    private void setDate() {

        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int currentYear = calendar.get(Calendar.YEAR);

        monthName = getResources().getStringArray(R.array.month_array);
        ArrayAdapter<String> adapterDate = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, dateArray);
        adapterDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(adapterDate);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, monthArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);

        ArrayAdapter<String> adapterYear = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, yearArray);
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(adapterYear);

        spinnerMonth.setSelection(calendar.get(calendar.MONTH));
        int curDate = calendar.get(calendar.DAY_OF_MONTH);
        Log.d(TAG, "Date of month is:- " + curDate);

        spinnerDay.setSelection(calendar.get(Calendar.DATE) - 1);
        String[] years = getResources().getStringArray(R.array.year_array);
        for (int i = 0; i < years.length; i++) {
            if (years[i].equals("" + currentYear)) {
                spinnerYear.setSelection(i);
            }
        }

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


    class getUserLocations extends AsyncTask<Void, String, String> {

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            isAPICalling = true;
            if (progressDialog != null) progressDialog.show();
        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(Void... params) {
            // TODO Auto-generated method stub

//            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
//            String date = sdf1.format(new Date());

            String date = spinnerYear.getSelectedItem() + "-" + (spinnerMonth.getSelectedItemPosition() + 1) + "-" + ((spinnerDay.getSelectedItem()));
            String startDate = Util.locatToUTC(spinnerYear.getSelectedItem() + "/" + (spinnerMonth.getSelectedItemPosition() + 1) + "/" + ((spinnerDay.getSelectedItem()) + " " + Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_COMPANY_STARTTIME)));
            String endDate = Util.locatToUTC(spinnerYear.getSelectedItem() + "/" + (spinnerMonth.getSelectedItemPosition() + 1) + "/" + ((spinnerDay.getSelectedItem()) + " " + Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_COMPANY_ENDTIME)));

            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            params1.add(new BasicNameValuePair("AdminID", "" + Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID)));
            params1.add(new BasicNameValuePair("CompanyID", "" + Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_COMPANY_ID)));
            params1.add(new BasicNameValuePair("StartDate", "" + startDate));
            params1.add(new BasicNameValuePair("EndDate", "" + endDate));
            Log.e("params1", ">>" + params1);
            String response = Util.makeServiceCall(Constant.URL
                    + "getUserLocations", 1, params1, getApplicationContext());

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            if (progressDialog != null)
                if (progressDialog.isShowing()) progressDialog.dismiss();
            Log.e("result", ">>" + result);
//            Util.WriteFile(getCacheDir().toString() + "/Tracking", Constant.TrackingFile, result);
            reload(result);
        }
    }

    private void reload(String result) {
        int day = -1, month = -1, year = -1;

        String errMessage = "";
        listUsers.clear();
        listTeamMembers.clear();
        markers.clear();
        Points.clear();
        map.clear();

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
                    hm.put("Location", "" + jsonObject1.getString("location"));
                    hm.put("date", "" + jsonObject1.getString("date"));
                    hm.put("date", "" + Util.utcToLocalTime(hm.get("date")));
                    hm.put("Latitude", "" + jsonObject1.getDouble("latitude"));
                    hm.put("Longitude", "" + jsonObject1.getDouble("longitude"));

                    try {
                        if (hm.get("Location").length() == 0 || hm.get("Location").equals("null")) {
                            hm.put("Location", "" + Util.getAddress(getApplicationContext(), Double.parseDouble("" + hm.get("Latitude")), Double.parseDouble(hm.get("Longitude"))));
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

                    Double Latitude = jsonObject1.getDouble("latitude");
                    Double Longitude = jsonObject1.getDouble("longitude");

                    if (Latitude == 0.0 && Longitude == 0.0) flag = false;

                    for (int j = 0; j < listUsers.size(); j++) {
                        if (listUsers.get(j).equals(hm.get("UserID"))) flag = false;
                    }

                    if (flag) {
                        listUsers.add(hm.get("UserID"));
                        listTeamMembers.add(hm);
                        loadMap(jsonObject1.getDouble("latitude"), jsonObject1.getDouble("longitude"), "" + hm.get("FirstName"));
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
        lvTeamMemebr.setAdapter(new TeamMemberAdapter(TrackingActivity.this, listTeamMembers, true));
        lvTeamMemebrFullScreen.setAdapter(new TeamMemberAdapter(TrackingActivity.this, listTeamMembers, true));
        isAPICalling = false;
        Log.e("Day Month year:", ">>" + day + month + year);
        if (day != -1 && month != -1 && year != -1)
            calculateSpinnerDates(day, month, year);

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
            Double lat = Double.parseDouble(String.format("%.2f", latitude));
            Double lng = Double.parseDouble(String.format("%.2f", longitude));

            LatLng latlng = new LatLng(latitude, longitude);
            LatLng latlng_ = new LatLng(lat, lng);

            boolean flag = true;
            for (int i = 0; i < Points.size(); i++) {
                if (latlng_.equals(Points.get(i))) {
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
        int padding = 50; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        map.moveCamera(cu);
        map.animateCamera(cu);
        //map.animateCamera(CameraUpdateFactory.zoomTo(15));

        if (Points.size() == 1) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(Points.get(0), 15));
            map.animateCamera(CameraUpdateFactory.zoomTo(10), 2000, null);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(Points.get(0))
                    .zoom(15)
                    .bearing(90)
                    .tilt(30)
                    .build();
            map.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }

    }

    public Dialog onCreateDialogSingleChoice() {
        temp_selected = Integer.parseInt(Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_TIME_PERIOD_TRACKING));
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("" + getResources().getString(R.string.select_time_period))
                .setSingleChoiceItems(getResources().getStringArray(R.array.time_period_array), temp_selected, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        temp_selected = which;
                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                if (temp_selected == 0) {

                                } else {
                                    Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_TIME_PERIOD_TRACKING, "" + temp_selected);
                                }
                            }
                        }

                )
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {

                            }
                        }
                );

        return builder.create();
    }


    private void calculateSpinnerDates(int curDate, int curMonth, int curYear) {

        try {
            monthArray.clear();
            yearArray.clear();
            dateArray.clear();

            int[] years1 = getResources().getIntArray(R.array.year_array_int);
            monthName = getResources().getStringArray(R.array.month_array);

            for (int i = 0; i < 12; i++) {
                if ((curMonth) >= i) {
                    // monthArray.add("" + i);
                    monthArray.add("" + monthName[i]);

                } else {
                    break;
                }
            }
            for (int j = 1; j <= years1.length; j++) {
                if (curYear >= (years1[j])) {
                    yearArray.add("" + years1[j]);
                } else {
                    break;
                }
            }
            for (int k = 1; k <= 31; k++) {
                if (curDate >= k) {
                    dateArray.add("" + k);
                } else {
                    break;
                }
            }


            spinnerMonth.setSelection(curMonth - 1);
            spinnerDay.setSelection(curDate - 1);
            String[] years = getResources().getStringArray(R.array.year_array);
            for (int i = 0; i < years.length; i++) {
                if (years[i].equals("" + curYear)) {
                    spinnerYear.setSelection(i);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void setSpinnerMonth() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int curYear = calendar.get(calendar.YEAR);
        int curMonth = calendar.get(calendar.MONTH);

        if (curYear > Integer.parseInt("" + spinnerYear.getSelectedItem())) {
            monthArray.clear();
            for (int i = 0; i < 12; i++) {
                // monthArray.add("" + i);
                monthArray.add("" + monthName[i]);
            }

        } else if (curYear == Integer.parseInt("" + spinnerYear.getSelectedItem())) {
            monthArray.clear();
            for (int i = 0; i < 12; i++) {
                if ((curMonth) >= i) {
                    //monthArray.add("" + i);
                    monthArray.add("" + monthName[i]);
                } else {
                    break;
                }
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, monthArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);
        spinnerMonth.setSelection(calendar.get(calendar.MONTH));

        setSpinnerDay();
    }

    private void setSpinnerDay() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int curYear = calendar.get(calendar.YEAR);
        int curMonth = calendar.get(calendar.MONTH);
        int curDate = calendar.get(calendar.DAY_OF_MONTH);
        int selectedMonth = Integer.parseInt("" + spinnerMonth.getSelectedItemId());

        if (curYear > Integer.parseInt("" + spinnerYear.getSelectedItem())) {
            monthArray.clear();
            for (int i = 0; i < 12; i++) {
                // monthArray.add("" + i);
                monthArray.add("" + monthName[i]);
            }
            setDates(selectedMonth + 1);
        } else {
            if ((curMonth) > selectedMonth) {
                setDates(selectedMonth + 1);
            } else {
                dateArray.clear();
                for (int k = 1; k <= 31; k++) {
                    if (curDate >= k) {
                        dateArray.add("" + k);
                    } else {
                        break;
                    }
                }
            }
        }

        ArrayAdapter<String> adapterDate = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, dateArray);
        adapterDate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDay.setAdapter(adapterDate);
        spinnerDay.setSelection(calendar.get(Calendar.DATE) - 1);

    }

    private void setDates(int selectedMonth) {
        if (selectedMonth == 1 || selectedMonth == 3 || selectedMonth == 5 || selectedMonth == 7 || selectedMonth == 8 || selectedMonth == 10 || selectedMonth == 12) {
            dateArray.clear();
            for (int i = 1; i <= 31; i++) {
                dateArray.add("" + i);
            }
        } else if (selectedMonth == 2) {
            dateArray.clear();
            for (int i = 1; i <= 28; i++) {
                dateArray.add("" + i);
            }
        } else {
            dateArray.clear();
            for (int i = 1; i <= 30; i++) {
                dateArray.add("" + i);
            }
        }
    }

}
