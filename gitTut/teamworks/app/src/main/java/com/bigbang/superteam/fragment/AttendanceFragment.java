package com.bigbang.superteam.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.SpinnerLocationAdapter;
import com.bigbang.superteam.leave_attendance.ApplyLeaveActivity;
import com.bigbang.superteam.leave_attendance.AttendanceDetailsActivity;
import com.bigbang.superteam.leave_attendance.LeaveBalanceActivity;
import com.bigbang.superteam.leave_attendance.LeaveStatusActivity;
import com.bigbang.superteam.leave_attendance.ManualAttendanceActivity;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.GPSTracker;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemSelected;

/**
 * Created by USER 3 on 4/8/2015.
 */
public class AttendanceFragment extends Fragment {


    @InjectView(R.id.btnAttendance)
    Button attendenceBtn;
    @InjectView(R.id.btnLeave)
    Button leaveBtn;
    @InjectView(R.id.progressBar)
    ProgressBar pBar;
    @InjectView(R.id.btnCheckIn)
    Button chekInBtn;
    @InjectView(R.id.btnCheckOut)
    Button checkOutBtn;
    @InjectView(R.id.btnManualRequest)
    Button manualRequestBtn;
    @InjectView(R.id.linearLayoutAttendance)
    LinearLayout attendanceLayout;
    @InjectView(R.id.linearLayoutLeave)
    LinearLayout leaveLayout;
    @InjectView(R.id.btnApplyforLeave)
    Button applyForLeaveBtn;
    @InjectView(R.id.btnLeaveStatus)
    Button leaveStatusBtn;
    @InjectView(R.id.btnLeaveBalance)
    Button leaveBalanceBtn;
    @InjectView(R.id.btnAttendanceHistory)
    Button historyBtn;

    @InjectView(R.id.spnrLocation)
    Spinner spnrLocationList;
    @InjectView(R.id.spinner_vendorlocation)
    Spinner spnrVendorLocation;
    @InjectView(R.id.spinnerAddresslist)
    Spinner spinnerAddress;
    @InjectView(R.id.linear_vendorlocation)
    LinearLayout linearVendorLocation;

    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    public static Context mContext;
    Location curLocation;
    String TAG = "AttendanceActivity";
    double curLat, curLng;
    boolean isCheckIn = false;
    static SQLiteHelper helper;
    public static SQLiteDatabase db = null;
    String time, date;

    String locationType = "Office";
    String addressId;

    ArrayList<HashMap<String, String>> clientsList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> vendorList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> addressList = new ArrayList<HashMap<String, String>>();

    private TransparentProgressDialog pd;

    public static Fragment newInstance(String string, Context ctx) {
        mContext = ctx;
        AttendanceFragment f = new AttendanceFragment();
        Bundle bdl = new Bundle(1);
        bdl.putString(EXTRA_MESSAGE, string);
        f.setArguments(bdl);
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String message = getArguments().getString(EXTRA_MESSAGE);
        View v;
        v = new View(mContext);
//        int levelNo = Integer.parseInt(message);
        v = inflater.inflate(R.layout.attendance_fragment, container, false);
        ButterKnife.inject(this, v);

        pd = new TransparentProgressDialog(getActivity(), R.drawable.progressdialog, false);
        // InitView(v);
        getVendors();
        getCustomers();
        Log.e(TAG, "Inside onCreate");
        setDefaultLocation();
        setViewAttendance();

        return v;
    }

    @OnClick(R.id.btnManualRequest)
    void startManual() {
        startManualRequest();
    }

    @OnClick(R.id.btnAttendance)
    void startViewAttendance() {
        setViewAttendance();
    }

    @OnClick(R.id.btnLeave)
    void startLeaveView() {
        setViewLeave();
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

    @OnClick(R.id.btnAttendanceHistory)
    void showAttendance() {
        startAttendanceHistory();
    }

    @OnClick(R.id.btnApplyforLeave)
    void applyForLeave() {
        startApplyforLeave();
    }

    @OnClick(R.id.btnLeaveStatus)
    void leaveStatus() {
        startLeaveStatus();
    }

    @OnClick(R.id.btnLeaveBalance)
    void leaveBalance() {
        startLeaveBalance();
    }


    @OnItemSelected(R.id.spnrLocation)
    void onItemSelected(int position) {
        locationType = "" + spnrLocationList.getSelectedItem();
        if (locationType.equals("Office")) {
            linearVendorLocation.setVisibility(View.GONE);
            getAddress(0);
        } else if (locationType.equals("Vendor")) {
            linearVendorLocation.setVisibility(View.VISIBLE);
            if (vendorList.size() == 0) {
                Toast.makeText(getActivity(), "No vendors available", Toast.LENGTH_SHORT).show();
                linearVendorLocation.setVisibility(View.GONE);
                spnrLocationList.setSelection(0);
            }
            spnrVendorLocation.setAdapter(new SpinnerLocationAdapter(getActivity(), R.layout.listrow_spinner, vendorList));
            getAddress(1);
        } else if (locationType.equals("Customer")) {
            linearVendorLocation.setVisibility(View.VISIBLE);
            if (clientsList.size() == 0) {
                Toast.makeText(getActivity(), "No Clients available", Toast.LENGTH_SHORT).show();
                linearVendorLocation.setVisibility(View.GONE);
                spnrLocationList.setSelection(0);
            }
            spnrVendorLocation.setAdapter(new SpinnerLocationAdapter(getActivity(), R.layout.listrow_spinner, clientsList));
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

  /*  @OnItemSelected(R.id.spinnerAddresslist)
    void onItemSelected2(int position) {
        final HashMap<String, String> user = addressList.get(position);
        addressId = user.get("ID");

        Log.v(TAG, "Address Id is :- " + addressId);
    }*/

    private void startAttendanceHistory() {

        Intent attendanceHistory = new Intent(mContext, AttendanceDetailsActivity.class);
        attendanceHistory.putExtra("userName", "Attendance History");
        attendanceHistory.putExtra("userId", "" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID));
        attendanceHistory.putExtra("activityName", "AttendanceFragment");
        startActivity(attendanceHistory);
    }

    private void startLeaveStatus() {
        Intent leaveStatus = new Intent(mContext, LeaveStatusActivity.class);
        startActivity(leaveStatus);
    }

    private void startLeaveBalance() {
        Intent leaveStatus = new Intent(mContext, LeaveBalanceActivity.class);
        startActivity(leaveStatus);
    }

    private void startApplyforLeave() {
        Intent applyforLeaveIntent = new Intent(mContext, ApplyLeaveActivity.class);
        startActivity(applyforLeaveIntent);
    }

    private void startManualRequest() {
        Intent manualAttendanceIntent = new Intent(mContext, ManualAttendanceActivity.class);
        startActivity(manualAttendanceIntent);
    }

    /// Sets Leave View
    private void setViewLeave() {
        leaveLayout.setVisibility(View.VISIBLE);
        attendanceLayout.setVisibility(View.GONE);
        attendenceBtn.setBackgroundColor(mContext.getResources().getColor(R.color.btnColor));
        leaveBtn.setBackgroundColor(mContext.getResources().getColor(R.color.btnPressedColor));
    }

    /// Sets Attendance View
    private void setViewAttendance() {
        leaveLayout.setVisibility(View.GONE);
        attendanceLayout.setVisibility(View.VISIBLE);
        attendenceBtn.setBackgroundColor(mContext.getResources().getColor(R.color.btnPressedColor));
        leaveBtn.setBackgroundColor(mContext.getResources().getColor(R.color.btnColor));
    }


    public void getCurrentLatLong() {
        GPSTracker gps = new GPSTracker(getActivity());
        if (gps.canGetLocation()) {
            curLat = gps.getLatitude();
            curLng = gps.getLongitude();
            if (Util.isOnline(getActivity())) {
                if (isCheckIn) {
                    //if (linearVendorLocation.getVisibility() == View.VISIBLE) {
                    //if (spnrVendorLocation != null && spnrVendorLocation.getSelectedItem() != null) {
                    new checkInAsync().execute();
                            /*if (spinnerAddress != null && spinnerAddress.getSelectedItem() != null) {
                                new checkInAsync().execute();
                            } else {
                                Toast.makeText(getActivity(), "Please select Address", Toast.LENGTH_SHORT).show();
                            }*/
                        /*} else {
                            Toast.makeText(getActivity(), "Please select client or vendor", Toast.LENGTH_SHORT).show();
                        }*/
                    //}

                } else {

                    // if (spnrVendorLocation != null && spnrVendorLocation.getSelectedItem() != null) {
                       /* if (spinnerAddress != null && spinnerAddress.getSelectedItem() != null) {
                            new checkOutAsync().execute();
                        } else {
                            Toast.makeText(getActivity(), "Please select Address", Toast.LENGTH_SHORT).show();
                        }*/
                    new checkOutAsync().execute();
                  /*  } else {
                        Toast.makeText(getActivity(), "Please select client or vendor", Toast.LENGTH_SHORT).show();
                    }*/
                }
            } else {
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }

        } else {
            Log.i(TAG, "Location Not Available!");
            Toast.makeText(getActivity(), "Location is not available, please try again!!", Toast.LENGTH_SHORT).show();
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
            params1.add(new BasicNameValuePair("UserID", "" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID)));
            params1.add(new BasicNameValuePair("CompanyID", "" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_COMPANY_ID)));
            params1.add(new BasicNameValuePair("Latitude", "" + curLat));
            params1.add(new BasicNameValuePair("Longitude", "" + curLng));
            params1.add(new BasicNameValuePair("Type", "" + spnrLocationList.getSelectedItem()));
            params1.add(new BasicNameValuePair("AddressMasterID", "" + addressList.get(spinnerAddress.getSelectedItemPosition()).get("ID")));

            if (spnrLocationList.getSelectedItem().toString().equals("Vendor")) {
                params1.add(new BasicNameValuePair("ClientVendorID", "" + vendorList.get(spnrVendorLocation.getSelectedItemPosition()).get("ID")));

            } else if (spnrLocationList.getSelectedItem().toString().equals("Customer")) {
                params1.add(new BasicNameValuePair("ClientVendorID", "" + clientsList.get(spnrVendorLocation.getSelectedItemPosition()).get("ID")));

            }else if (spnrLocationList.getSelectedItem().toString().equals("Office")) {
                params1.add(new BasicNameValuePair("ClientVendorID", "0"));
            }
            Log.e("params1", ">>" + params1);
            String response = Util.makeServiceCall(Constant.URL + "checkIn", 2, params1, getActivity());
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
                Toast.makeText(getActivity(), "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
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
            params1.add(new BasicNameValuePair("UserID", "" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID)));
            params1.add(new BasicNameValuePair("CompanyID", "" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_COMPANY_ID)));
            params1.add(new BasicNameValuePair("Latitude", "" + curLat));
            params1.add(new BasicNameValuePair("Longitude", "" + curLng));
           // params1.add(new BasicNameValuePair("Type", "" + spnrLocationList.getSelectedItem()));
            //params1.add(new BasicNameValuePair("AddressMasterID", addressId));
            params1.add(new BasicNameValuePair("AddressMasterID", "" + addressList.get(spinnerAddress.getSelectedItemPosition()).get("ID")));
            if (spnrLocationList.getSelectedItem().toString().equals("Vendor")) {
                params1.add(new BasicNameValuePair("ClientVendorID", "" + vendorList.get(spnrVendorLocation.getSelectedItemPosition()).get("ID")));
            } else if (spnrLocationList.getSelectedItem().toString().equals("Customer")) {
                params1.add(new BasicNameValuePair("ClientVendorID", "" + clientsList.get(spnrVendorLocation.getSelectedItemPosition()).get("ID")));
            }else if (spnrLocationList.getSelectedItem().toString().equals("Office")) {
                params1.add(new BasicNameValuePair("ClientVendorID", "0"));
            }

            Log.e("params1", ">>" + params1);
         /*   String response = Util.postData(params1, Constant.URL
                    + "checkOut");*/
            String response = Util.makeServiceCall(Constant.URL + "checkOut", 2, params1, getActivity());
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
                Toast.makeText(getActivity(), "" + jObj.getString("message"), Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void getCustomers() {
        clientsList.clear();
        Cursor cursor = Util.getDb(getActivity()).rawQuery("select * from " + Constant.tableCustomers, null);
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
            //Toast.makeText(getActivity(), "No Client address is available", Toast.LENGTH_SHORT).show();
            linearVendorLocation.setVisibility(View.GONE);
            spnrLocationList.setSelection(0);

        } else {
            spnrVendorLocation.setAdapter(new SpinnerLocationAdapter(getActivity(), R.layout.listrow_spinner, clientsList));
            getAddress(2);
        }
    }

    private void getVendors() {
        vendorList.clear();
        Cursor cursor = Util.getDb(getActivity()).rawQuery("select * from " + Constant.tableVendors, null);
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
            //Toast.makeText(getActivity(), "No Vendor address is available", Toast.LENGTH_SHORT).show();
            linearVendorLocation.setVisibility(View.GONE);
            spnrLocationList.setSelection(0);

        } else {
            spnrVendorLocation.setAdapter(new SpinnerLocationAdapter(getActivity(), R.layout.listrow_spinner, vendorList));
            getAddress(1);
        }

    }

    private void getAddress(int type) {
        addressList.clear();
        if (type == 0) {

            try {
                //Log.e("AddressList", ">>" + Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_COMPANY_AddressList));
                JSONArray jsonArray = new JSONArray(Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_COMPANY_AddressList));
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
                    // Toast.makeText(getActivity(), "No Client address is available", Toast.LENGTH_SHORT).show();
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
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (addressList.size() == 0) {

                //Toast.makeText(getActivity(), "No Vendor address is available", Toast.LENGTH_SHORT).show();
                linearVendorLocation.setVisibility(View.GONE);
                spnrLocationList.setSelection(0);
            }
        }

        spinnerAddress.setAdapter(new SpinnerLocationAdapter(getActivity(), R.layout.listrow_spinner, addressList));

    }

    private void setDefaultLocation() {


        String locationDetails = "" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USER_LOCATION_DETAILS);

        try {
            JSONObject jsonObject = new JSONObject(locationDetails);

            final int locationType = jsonObject.optInt("LocationType");
            final int clientVendorId = jsonObject.optInt("ClientVendorID");
            final int addressMasterId = jsonObject.optInt("AddressID");

            if (locationType==3) {
                spnrLocationList.setSelection(1);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e(TAG, "outside of vendor loop:- " + vendorList.size());
                        for (int i = 0; i < vendorList.size(); i++) {
                            Log.e(TAG, "Inside vendor loop id is:- " + vendorList.get(i).get("ID"));
                            if (vendorList.get(i).get("ID").equals(""+clientVendorId)) {
                                Log.e(TAG, "Inside vendor loop id is:- " + i + "**************  " + vendorList.get(i).get("ID"));
                                spnrVendorLocation.setSelection(i);
                            }
                        }
                    }
                }, 500);

            } else if (locationType==2) {
                spnrLocationList.setSelection(2);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < clientsList.size(); i++) {
                            Log.e(TAG, "Inside client loop id is:- " + clientsList.get(i).get("ID"));
                            if (clientsList.get(i).get("ID").equals(""+clientVendorId)) {
                                Log.e(TAG, "Inside Client loop id is:- " + clientVendorId + "**************  " + clientsList.get(i).get("ID"));
                                spnrVendorLocation.setSelection(i);
                            }
                        }
                    }
                }, 500);

            } else {
                spnrLocationList.setSelection(0);
            }

            for (int i = 0; i < addressList.size(); i++) {
                if (addressList.get(i).get("ID").equals(""+addressMasterId))
                    spinnerAddress.setSelection(i);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {

     /* getVendors();
        getCustomers();*/
        setDefaultLocation();
        //setViewAttendance();

        Log.e(TAG, "Inside onResume");
        super.onResume();

    }
}
