package com.bigbang.superteam.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.SpinnerLocationAdapter;
import com.bigbang.superteam.leave_attendance.AttendanceDetailsActivity;
import com.bigbang.superteam.leave_attendance.ManualAttendanceActivity;
import com.bigbang.superteam.user.DefaultWorkLocation;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.LocationService;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemSelected;

/**
 * Created by USER 7 on 11/3/2015.
 */
public class AttendanceHistoryUserFragment extends Fragment {

    public static Context mContext;

    @InjectView(R.id.spnrLocation)
    Spinner spnrLocationList;
    @InjectView(R.id.spinner_vendorlocation)
    Spinner spnrVendorLocation;
    @InjectView(R.id.spinner_addressList)
    Spinner spinnerAddress;
    @InjectView(R.id.linear_vendorlocation)
    LinearLayout linearVendorLocation;
    @InjectView(R.id.tvDate1)
    TextView tvDate;
    @InjectView(R.id.tvDate2)
    TextView tvYear;
    @InjectView(R.id.tvTime1)
    TextView tvTime;
    @InjectView(R.id.tvTime2)
    TextView tvAm_Pm;
    @InjectView(R.id.tvClientVendorTitle)
    TextView tvClientVendorTitle;
    @InjectView(R.id.txt_sp_select)
    TextView txt_sp_select;

    String locationType = "Office";
    String addressId;
    String TAG = "AttendanceHistoryUserFragment";

    private TransparentProgressDialog pd;

    boolean isCheckIn = false;
    double curLat, curLng;
    Calendar calendar;

    ArrayList<HashMap<String, String>> clientsList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> vendorList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> addressList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> ListForSpinner = new ArrayList<>();
    Activity activity;

    SpinnerLocationAdapter adapter;

    public static AttendanceHistoryUserFragment newInstance(String string, Context ctx) {
        mContext = ctx;
        AttendanceHistoryUserFragment f = new AttendanceHistoryUserFragment();
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_attendance_user_fragment, container, false);
        // v = inflater.inflate(R.layout.activity_admin_attendance_history, container, false);
        ButterKnife.inject(this, v);
        activity = getActivity();
        final Typeface mFont = Typeface.createFromAsset(activity.getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) v.getRootView();
        Util.setAppFont(mContainer, mFont);

        pd = new TransparentProgressDialog(activity, R.drawable.progressdialog, false);
        // InitView(v);
        Init();
        getVendors();
        getCustomers();
        Log.e(TAG, "Inside onCreate");
        setDefaultLocation();

        getActivity().startService(new Intent(getActivity(), LocationService.class));
        spnrVendorLocation.setEnabled(false);

        txt_sp_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                toast("selected textview");
                showDialog(ListForSpinner);
            }
        });

        return v;
    }
    String tmpTextCustomer;
    ArrayList<HashMap<String, String>> tmpListCust = new ArrayList<>();

    private void showDialog(final ArrayList<HashMap<String, String>> List) {

        final Dialog dialog = new Dialog(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_main, null);

        final ListView lv = (ListView) view.findViewById(R.id.custom_list);

        final EditText edt_search_di = (EditText) view.findViewById(R.id.edt_seach_di);


        adapter = new SpinnerLocationAdapter(getActivity(), R.layout.listrow_spinner, List);

        lv.setAdapter(adapter);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);

        dialog.show();

        edt_search_di.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                adapter.filter(s.toString());
                tmpTextCustomer = "" + edt_search_di.getText().toString().trim();
                tmpListCust.clear();
                if (List != null) {
                    if (List.size() > 0) {
                        if (tmpTextCustomer.length() > 0) {
                            for (int i = 0; i < List.size(); i++) {
                                if (List.get(i).get("name").toLowerCase().contains(tmpTextCustomer.toLowerCase())) {
//                                    tmpListCust.clear();
                                    tmpListCust.add(List.get(i));
                                }
                            }
                            //set adapter here
                            adapter.notifyDataSetChanged();
                            adapter = new SpinnerLocationAdapter(getActivity(), R.layout.listrow_spinner, tmpListCust);
                            lv.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                            adapter = new SpinnerLocationAdapter(getActivity(), R.layout.listrow_spinner, List);
                            lv.setAdapter(adapter);
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                dialog.dismiss();

                if (locationType.equals("Office")) {
                } else if (locationType.equals("Vendor")) {
                    HashMap<String, String> map = new HashMap<String, String>();
                    map = (HashMap<String, String>) adapterView.getAdapter().getItem(i);

                    if (vendorList != null && vendorList.size() > 0) {
                        for (int j = 0; j < vendorList.size(); j++) {
                            if (vendorList.get(j).get("ID").equalsIgnoreCase(map.get("ID"))) {
                                spnrVendorLocation.setSelection(j);
                            }
                        }
                    }
                } else if (locationType.equals("Customer")) {
//                    String name = (String) adapterView.getItemAtPosition(i);
                    HashMap<String, String> map = new HashMap<String, String>();
                    map = (HashMap<String, String>) adapterView.getAdapter().getItem(i);

                    if (clientsList != null && clientsList.size() > 0) {
                        for (int j = 0; j < clientsList.size(); j++) {
                            if (clientsList.get(j).get("ID").equalsIgnoreCase(map.get("ID"))) {
                                spnrVendorLocation.setSelection(j);
                            }
                        }
                    }
                }
            }
        });
    }

    private void Init() {
        calendar = Calendar.getInstance();
        int month, year, day;
        month = calendar.get(calendar.MONTH);
        year = calendar.get(calendar.YEAR);
        day = calendar.get(calendar.DAY_OF_MONTH);

        SimpleDateFormat sdf1 = new SimpleDateFormat(" d" + " MMM" + ",");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");
        SimpleDateFormat sdf3 = new SimpleDateFormat(" hh:mm");
        SimpleDateFormat sdf4 = new SimpleDateFormat("a");

        tvClientVendorTitle.setText("Customer/" + "\n" + "Vendor");
        tvDate.setText(sdf1.format(calendar.getTime()));
        tvYear.setText("" + sdf2.format(calendar.getTime()));
        tvTime.setText("" + sdf3.format(calendar.getTime()));
        tvAm_Pm.setText("" + sdf4.format(calendar.getTime()));
    }

    private void getCustomers() {
        clientsList.clear();
        Cursor cursor = Util.getDb(activity).rawQuery("select * from " + Constant.tableCustomers, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("ID", "" + cursor.getString(cursor.getColumnIndex("ID")));
                    hashMap.put("name", "" + cursor.getString(cursor.getColumnIndex("Name")));
                    hashMap.put("AddressList", "" + cursor.getString(cursor.getColumnIndex("AddressList")));
                    clientsList.add(hashMap);
                } while (cursor.moveToNext());
            }
        }
        if (clientsList.size() == 0) {
            Log.e(TAG, "Inside getCustomers and client list==0");
            //Toast.makeText(activity, "No Client address is available", Toast.LENGTH_SHORT).show();
            linearVendorLocation.setVisibility(View.GONE);
            spnrLocationList.setSelection(0);

        } else {
            spnrVendorLocation.setAdapter(new SpinnerLocationAdapter(activity, R.layout.listrow_spinner, clientsList));
            getAddress(2);
        }
    }

    private void getVendors() {
        vendorList.clear();
        Cursor cursor = Util.getDb(activity).rawQuery("select * from " + Constant.tableVendors, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("ID", "" + cursor.getString(cursor.getColumnIndex("ID")));
                    hashMap.put("name", "" + cursor.getString(cursor.getColumnIndex("Name")));
                    hashMap.put("AddressList", "" + cursor.getString(cursor.getColumnIndex("AddressList")));
                    vendorList.add(hashMap);
                } while (cursor.moveToNext());
            }
        }
        if (vendorList.size() == 0) {
            Log.e(TAG, "Inside getCustomers and vendorList==0");
            //Toast.makeText(activity, "No Vendor address is available", Toast.LENGTH_SHORT).show();
            linearVendorLocation.setVisibility(View.GONE);
            spnrLocationList.setSelection(0);

        } else {
            spnrVendorLocation.setAdapter(new SpinnerLocationAdapter(activity, R.layout.listrow_spinner, vendorList));
            getAddress(1);
        }
    }

    private void getAddress(int type) {
        addressList.clear();
        if (type == 0) {
            try {
                //Log.e("AddressList", ">>" + Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_COMPANY_AddressList));
                JSONArray jsonArray = new JSONArray(Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_AddressList));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("ID", "" + jsonObject.optString("AddressID"));
                    hashMap.put("name", "" + jsonObject.optString("AddressLine1") + "," + jsonObject.optString("AddressLine2"));
                    hashMap.put("AddressList", "" + jsonObject);

                    addressList.add(hashMap);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type == 2) {
            try {
                JSONArray jsonArray = new JSONArray(clientsList.get(spnrVendorLocation.getSelectedItemPosition()).get("AddressList"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("ID", "" + jsonObject.optString("AddressID"));
                    hashMap.put("name", "" + jsonObject.optString("AddressLine1") + "," + jsonObject.optString("AddressLine2"));
                    hashMap.put("AddressList", "" + jsonObject);

                    addressList.add(hashMap);
                }
                if (addressList.size() == 0) {
                    // Toast.makeText(activity, "No Client address is available", Toast.LENGTH_SHORT).show();
                    linearVendorLocation.setVisibility(View.VISIBLE);
                    spnrLocationList.setSelection(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type == 1) {
            try {
                JSONArray jsonArray = new JSONArray(vendorList.get(spnrVendorLocation.getSelectedItemPosition()).get("AddressList"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("ID", "" + jsonObject.optString("AddressID"));
                    hashMap.put("name", "" + jsonObject.optString("AddressLine1") + "," + jsonObject.optString("AddressLine2"));
                    hashMap.put("AddressList", "" + jsonObject);

                    addressList.add(hashMap);
                }
                if (addressList.size() == 0) {
                    //Toast.makeText(activity, "No Vendor address is available", Toast.LENGTH_SHORT).show();
                    linearVendorLocation.setVisibility(View.GONE);
                    spnrLocationList.setSelection(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        Log.e("Address", ">>" + addressList.size());
        spinnerAddress.setAdapter(new SpinnerLocationAdapter(activity, R.layout.listrow_spinner, addressList));

        try {
            String locationDetails = "" + Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_USER_LOCATION_DETAILS);
            JSONObject jsonObject = new JSONObject(locationDetails);
            final int addressMasterId = jsonObject.optInt("AddressID");
            for (int i = 0; i < addressList.size(); i++) {
                Log.e(TAG, "Address list size :- " + addressList.size());
                if (addressList.get(i).get("ID").equals("" + addressMasterId))
                    spinnerAddress.setSelection(i);
                Log.e(TAG, "Address Id is:- " + addressMasterId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setDefaultLocation() {
        String locationDetails = "" + Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_USER_LOCATION_DETAILS);
        try {
            JSONObject jsonObject = new JSONObject(locationDetails);

            final int locationType = jsonObject.optInt("LocationType");
            final int clientVendorId = jsonObject.optInt("ClientVendorID");
            final int addressMasterId = jsonObject.optInt("AddressID");

            if (locationType == 3) {
                spnrLocationList.setSelection(1);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "outside of vendor loop:- " + vendorList.size());
                        for (int i = 0; i < vendorList.size(); i++) {
                            Log.e(TAG, "Inside vendor loop id is:- " + vendorList.get(i).get("ID"));
                            if (vendorList.get(i).get("ID").equals("" + clientVendorId)) {
                                Log.e(TAG, "Inside vendor loop id is:- " + i + "**************  " + vendorList.get(i).get("ID"));
                                spnrVendorLocation.setSelection(i);
                            }
                        }
                    }
                }, 500);
            } else if (locationType == 2) {
                spnrLocationList.setSelection(2);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < clientsList.size(); i++) {
                            Log.e(TAG, "Inside client loop id is:- " + clientsList.get(i).get("ID"));
                            if (clientsList.get(i).get("ID").equals("" + clientVendorId)) {
                                Log.e(TAG, "Inside Client loop id is:- " + clientVendorId + "**************  " + clientsList.get(i).get("ID"));
                                spnrVendorLocation.setSelection(i);
                            }
                        }
                    }
                }, 500);

            } else {
                spnrLocationList.setSelection(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        Log.e(TAG, "Inside onResume");

     /* getVendors();
        getCustomers();*/
        setDefaultLocation();
        //setViewAttendance();

    }

    @OnClick(R.id.btnCheckIn)
    void checkInMethod() {
        isCheckIn = true;
        getCurrentLatLong();
    }

    @OnClick(R.id.btnCheckOut)
    void checkOutMethod() {
        isCheckIn = false;
        getCurrentLatLong();
    }

    @OnClick(R.id.btnManualRequest)
    void manualAttendance() {
        Intent manualAttendanceIntent = new Intent(activity, ManualAttendanceActivity.class);
        startActivity(manualAttendanceIntent);
        activity.overridePendingTransition(R.anim.enter_from_left,
                R.anim.hold_bottom);
    }

    @OnClick(R.id.btnAttendanceHistory)
    void history() {
        Intent attendanceHistory = new Intent(activity, AttendanceDetailsActivity.class);
        attendanceHistory.putExtra("userName", "Attendance History");
        attendanceHistory.putExtra("userId", "" + Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_USERID));
        attendanceHistory.putExtra("activityName", "" + Constant.FROM_USER_DASHBOARD);

        startActivity(attendanceHistory);
        activity.overridePendingTransition(R.anim.enter_from_left,
                R.anim.hold_bottom);
    }

    @OnItemSelected(R.id.spnrLocation)
    void onItemSelected(int position) {
        ListForSpinner.clear();
        locationType = "" + spnrLocationList.getSelectedItem();
        if (locationType.equals("Office")) {
            linearVendorLocation.setVisibility(View.GONE);
            getAddress(0);
        } else if (locationType.equals("Vendor")) {
            ListForSpinner.addAll(vendorList);
            linearVendorLocation.setVisibility(View.VISIBLE);
            if (vendorList.size() == 0) {
                Toast.makeText(activity, "No vendors available", Toast.LENGTH_SHORT).show();
                linearVendorLocation.setVisibility(View.GONE);
                spnrLocationList.setSelection(0);
            }
            spnrVendorLocation.setAdapter(new SpinnerLocationAdapter(activity, R.layout.listrow_spinner, vendorList));
            getAddress(1);
        } else if (locationType.equals("Customer")) {
            ListForSpinner.addAll(clientsList);
            linearVendorLocation.setVisibility(View.VISIBLE);
            if (clientsList.size() == 0) {
                Toast.makeText(activity, "No Clients available", Toast.LENGTH_SHORT).show();
                linearVendorLocation.setVisibility(View.GONE);
                spnrLocationList.setSelection(0);
            }
            spnrVendorLocation.setAdapter(new SpinnerLocationAdapter(activity, R.layout.listrow_spinner, clientsList));
            getAddress(2);
        }
    }

    @OnItemSelected(R.id.spinner_vendorlocation)
    void onItemSelected1(int position) {
        locationType = "" + spnrLocationList.getSelectedItem();
        if (locationType.equals("Vendor")) {
            getAddress(1);
        } else if (locationType.equals("Customer")) {
            getAddress(2);
        }
    }


    public void getCurrentLatLong() {
//        GPSTracker gps = new GPSTracker(activity);
        String latitude = Util.ReadSharePrefrence(getActivity(), "track_lat");
        String longitude = Util.ReadSharePrefrence(getActivity(), "track_lng");

//        if (gps.canGetLocation()) {
//            curLat = gps.getLatitude();
//            curLng = gps.getLongitude();
        curLat = Double.parseDouble(latitude);
        curLng = Double.parseDouble(longitude);

        if (Util.isOnline(activity)) {
            if (isCheckIn) {
                new checkInAsync().execute();
            } else {
                new checkOutAsync().execute();
            }
//            } else {
//                Toast.makeText(activity, activity.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
//            }

        } else {
            Log.i(TAG, "Location Not Available!");
            Toast.makeText(activity, "Location is not available, please try again!!", Toast.LENGTH_SHORT).show();
        }

    }

    class checkInAsync extends AsyncTask<Void, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pd != null) {
                pd.show();
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            params1.add(new BasicNameValuePair("UserID", "" + Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_USERID)));
            params1.add(new BasicNameValuePair("CompanyID", "" + Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_ID)));
            params1.add(new BasicNameValuePair("Latitude", "" + curLat));
            params1.add(new BasicNameValuePair("Longitude", "" + curLng));
            //params1.add(new BasicNameValuePair("Type", "" + spnrLocationList.getSelectedItem()));
            params1.add(new BasicNameValuePair("AddressMasterID", "" + addressList.get(spinnerAddress.getSelectedItemPosition()).get("ID")));

            if (spnrLocationList.getSelectedItem().toString().equals("Vendor")) {
                params1.add(new BasicNameValuePair("ClientVendorID", "" + vendorList.get(spnrVendorLocation.getSelectedItemPosition()).get("ID")));

            } else if (spnrLocationList.getSelectedItem().toString().equals("Customer")) {
                params1.add(new BasicNameValuePair("ClientVendorID", "" + clientsList.get(spnrVendorLocation.getSelectedItemPosition()).get("ID")));

            } else if (spnrLocationList.getSelectedItem().toString().equals("Office")) {
                params1.add(new BasicNameValuePair("ClientVendorID", "0"));
            }
            Log.e("params1", ">>" + params1);
            String response = Util.makeServiceCall(Constant.URL + "checkIn", 2, params1, activity);
            Log.e("response", "<<" + response);

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (pd != null) {
                pd.dismiss();
            }
            try {
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");
                Toast.makeText(activity, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class checkOutAsync extends AsyncTask<Void, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pd != null) {
                pd.show();
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            params1.add(new BasicNameValuePair("UserID", "" + Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_USERID)));
            params1.add(new BasicNameValuePair("CompanyID", "" + Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_COMPANY_ID)));
            params1.add(new BasicNameValuePair("Latitude", "" + curLat));
            params1.add(new BasicNameValuePair("Longitude", "" + curLng));
            // params1.add(new BasicNameValuePair("Type", "" + spnrLocationList.getSelectedItem()));
            //params1.add(new BasicNameValuePair("AddressMasterID", addressId));
            params1.add(new BasicNameValuePair("AddressMasterID", "" + addressList.get(spinnerAddress.getSelectedItemPosition()).get("ID")));
            if (spnrLocationList.getSelectedItem().toString().equals("Vendor")) {
                params1.add(new BasicNameValuePair("ClientVendorID", "" + vendorList.get(spnrVendorLocation.getSelectedItemPosition()).get("ID")));
            } else if (spnrLocationList.getSelectedItem().toString().equals("Customer")) {
                params1.add(new BasicNameValuePair("ClientVendorID", "" + clientsList.get(spnrVendorLocation.getSelectedItemPosition()).get("ID")));

            } else if (spnrLocationList.getSelectedItem().toString().equals("Office")) {
                params1.add(new BasicNameValuePair("ClientVendorID", "0"));
            }

            Log.e("params1", ">>" + params1);
         /*   String response = Util.postData(params1, Constant.URL
                    + "checkOut");*/
            String response = Util.makeServiceCall(Constant.URL + "checkOut", 2, params1, activity);
            Log.e(TAG, "response is:- " + response);
            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (pd != null) {
                pd.dismiss();
            }
            try {
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");
                Toast.makeText(activity, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}