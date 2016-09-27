package com.bigbang.superteam.user;

import android.app.Dialog;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.impl.entity.StrictContentLengthStrategy;
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
 * Created by USER 7 on 8/3/2015.
 */


public class DefaultWorkLocation extends BaseActivity {

    @InjectView(R.id.tvSetAddress)
    TextView tvPermAddress;
    @InjectView(R.id.spnrLocation)
    Spinner spnrLocationList;
    @InjectView(R.id.spinner_vendorlocation)
    Spinner spnrVendorLocation;
    @InjectView(R.id.spinner_addressList)
    Spinner spinnerAddress;
    @InjectView(R.id.linear_vendorlocation)
    LinearLayout linearVendorLocation;
    @InjectView(R.id.tvClientVendorTitle)
    TextView tvClientVendorTitle;
    @InjectView(R.id.txt_sp_select)
    TextView txt_sp_select;


    String locationType = "Office";
    String addressId;
    String TAG = "DefaultWorkLocation";

    ArrayList<HashMap<String, String>> clientsList = new ArrayList<>();
    ArrayList<HashMap<String, String>> vendorList = new ArrayList<>();
    ArrayList<HashMap<String, String>> addressList = new ArrayList<>();
    ArrayList<HashMap<String, String>> ListForSpinner = new ArrayList<>();


    private TransparentProgressDialog pd;
    // boolean flag = true;

    SpinnerLocationAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_location);
        ButterKnife.inject(this);

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        pd = new TransparentProgressDialog(DefaultWorkLocation.this, R.drawable.progressdialog, false);
        tvClientVendorTitle.setText("Customer/" + "\n" + "Vendor");

        getVendors();
        getCustomers();
        setDefaultLocation();


        spnrVendorLocation.setEnabled(false);

        txt_sp_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                toast("selected textview");
                showDialog(ListForSpinner);
            }
        });
    }

    String tmpTextCustomer;
    ArrayList<HashMap<String, String>> tmpListCust = new ArrayList<>();

    private void showDialog(final ArrayList<HashMap<String, String>> List) {

        final Dialog dialog = new Dialog(this);

        View view = getLayoutInflater().inflate(R.layout.dialog_main, null);

        final ListView lv = (ListView) view.findViewById(R.id.custom_list);

        final EditText edt_search_di = (EditText) view.findViewById(R.id.edt_seach_di);


        adapter = new SpinnerLocationAdapter(DefaultWorkLocation.this, R.layout.listrow_spinner, List);

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
                            adapter = new SpinnerLocationAdapter(DefaultWorkLocation.this, R.layout.listrow_spinner, tmpListCust);
                            lv.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                            adapter = new SpinnerLocationAdapter(DefaultWorkLocation.this, R.layout.listrow_spinner, List);
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

    @OnItemSelected(R.id.spinner_addressList)
    void onItemSelected3(int position) {
        tvPermAddress.setText(buildAddress(addressList.get(position).get("AddressList")));
    }

    public void setSpinText(Spinner spin, String text) {
        for (int i = 0; i < spin.getAdapter().getCount(); i++) {
            if (spin.getAdapter().getItem(i).toString().contains(text)) {
                spin.setSelection(i);
            }
        }
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
                Toast.makeText(DefaultWorkLocation.this, "No vendors available", Toast.LENGTH_SHORT).show();
                linearVendorLocation.setVisibility(View.GONE);
                spnrLocationList.setSelection(0);
            }

            spnrVendorLocation.setAdapter(new SpinnerLocationAdapter(DefaultWorkLocation.this, R.layout.listrow_spinner, vendorList));
            getAddress(1);
        } else if (locationType.equals("Customer")) {
            ListForSpinner.addAll(clientsList);
            linearVendorLocation.setVisibility(View.VISIBLE);
            if (clientsList.size() == 0) {
                Toast.makeText(DefaultWorkLocation.this, "No Clients available", Toast.LENGTH_SHORT).show();
                linearVendorLocation.setVisibility(View.GONE);
                spnrLocationList.setSelection(0);
            }
//            ListForSpinner.clear();

            spnrVendorLocation.setAdapter(new SpinnerLocationAdapter(DefaultWorkLocation.this, R.layout.listrow_spinner, clientsList));
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

    @OnClick(R.id.btnSave)
    void saveLocation() {
        if (Util.isOnline(DefaultWorkLocation.this))
            new setDefaultLocation().execute();
        else {
            Toast.makeText(DefaultWorkLocation.this, DefaultWorkLocation.this.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.rlBack)
    void backPressed() {
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }

    private String buildAddress(String address) {
        StringBuffer sb = new StringBuffer();
        sb.append("Address:").append("\n");
        if (address != null) {
            try {
                JSONObject jsonObject = new JSONObject(address);
                sb.append(jsonObject.optString("AddressLine1")).append(",").append("\n");
                sb.append(jsonObject.optString("AddressLine2")).append(",").append("\n");
                sb.append(jsonObject.optString("City")).append("-").append(jsonObject.optString("Pincode")).append(",").append("\n");
                sb.append(jsonObject.optString("State")).append(",").append("\n");
                sb.append(jsonObject.optString("Country"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    class setDefaultLocation extends AsyncTask<Void, String, String> {
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
            params1.add(new BasicNameValuePair("UserID", "" + Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID)));
            params1.add(new BasicNameValuePair("CompanyID", "" + Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_COMPANY_ID)));
            params1.add(new BasicNameValuePair("LocationType", "" + spnrLocationList.getSelectedItem()));
            if (spnrLocationList.getSelectedItemPosition() == 2)
                params1.add(new BasicNameValuePair("ClientVendorID", "" + clientsList.get(spnrVendorLocation.getSelectedItemPosition()).get("ID")));
            else if (spnrLocationList.getSelectedItemPosition() == 1)
                params1.add(new BasicNameValuePair("ClientVendorID", "" + vendorList.get(spnrVendorLocation.getSelectedItemPosition()).get("ID")));
            else if (spnrLocationList.getSelectedItemPosition() == 0)
                params1.add(new BasicNameValuePair("ClientVendorID", "0"));

            final HashMap<String, String> user = addressList.get(spinnerAddress.getSelectedItemPosition());
            addressId = user.get("ID");
            params1.add(new BasicNameValuePair("AddressMasterID", "" + addressList.get(spinnerAddress.getSelectedItemPosition()).get("ID")));
            Log.v(TAG, "Address Id is :- " + addressId);
            Log.e("params1", ">***>" + params1);
         /*   String response = Util.postData(params1, Constant.URL
                    + "checkOut");*/
            String response = Util.makeServiceCall(Constant.URL + "setDefaultSettings", 2, params1, getApplicationContext());
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
                Toast.makeText(getApplicationContext(), "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
                String status = jObj.optString("status");
                if (status.equals("Success")) {
                    finish();

                    JSONObject jsonObject = jObj.optJSONObject("data");

                    write(Constant.SHRED_PR.KEY_USER_LOCATION_DETAILS, "" + jsonObject.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();


            }
        }
    }

    private void getCustomers() {
        clientsList.clear();
        Cursor cursor = Util.getDb(DefaultWorkLocation.this).rawQuery("select * from " + Constant.tableCustomers, null);
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
            adapter = new SpinnerLocationAdapter(DefaultWorkLocation.this, R.layout.listrow_spinner, clientsList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spnrVendorLocation.setAdapter(adapter);
            getAddress(2);
        }
    }

    private void getVendors() {
        vendorList.clear();
        Cursor cursor = Util.getDb(DefaultWorkLocation.this).rawQuery("select * from " + Constant.tableVendors, null);
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
            spnrVendorLocation.setAdapter(new SpinnerLocationAdapter(DefaultWorkLocation.this, R.layout.listrow_spinner, vendorList));
            getAddress(1);
        }

    }

    private void getAddress(int type) {
        addressList.clear();
        if (type == 0) {

            try {
                //Log.e("AddressList", ">>" + Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_COMPANY_AddressList));
                JSONArray jsonArray = new JSONArray(Util.ReadSharePrefrence(DefaultWorkLocation.this, Constant.SHRED_PR.KEY_COMPANY_AddressList));
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

        spinnerAddress.setAdapter(new SpinnerLocationAdapter(DefaultWorkLocation.this, R.layout.listrow_spinner, addressList));

        try {
            String locationDetails = "" + Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USER_LOCATION_DETAILS);
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

        String locationDetails = "" + Util.ReadSharePrefrence(DefaultWorkLocation.this, Constant.SHRED_PR.KEY_USER_LOCATION_DETAILS);

        try {
            JSONObject jsonObject = new JSONObject(locationDetails);

            final int locationType = jsonObject.optInt("LocationType");
            final int clientVendorId = jsonObject.optInt("ClientVendorID");

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

}
