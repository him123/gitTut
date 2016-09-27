package com.bigbang.superteam.customer_vendor;

import android.content.Intent;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.admin.SelectLocationActivity;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.model.Address;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.GPSTracker;
import com.bigbang.superteam.util.Util;
import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.InjectView;
import butterknife.OnClick;

public class AddressActivity extends BaseActivity {

    @InjectView(R.id.tvType)
    TextView tvType;
    @InjectView(R.id.tvTitle)
    TextView tvTitle;
    @InjectView(R.id.tvLatLng)
    TextView tvLatLng;
    @InjectView(R.id.et_permadd1)
    EditText etPermAdd1;
    @InjectView(R.id.et_permadd2)
    EditText etPermAdd2;
    @InjectView(R.id.et_city)
    EditText etPermCity;
    @InjectView(R.id.et_state)
    EditText etPermState;
    @InjectView(R.id.et_country)
    EditText etPermCountry;
    @InjectView(R.id.et_pincode)
    EditText etPermPincode;
    @InjectView(R.id.spinnerType)
    Spinner spinnerType;
    @InjectView(R.id.rl_next)
    RelativeLayout rl_next;
    @InjectView(R.id.rlLocation)
    RelativeLayout rlLocation;
    @InjectView(R.id.progressBar)
    ProgressBar progressBar;
    @InjectView(R.id.icon_done)
    ImageButton icon_done;

    String CreateType = "1";
    Address address;
    int position;

    Double latitude, longitude;
    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            CreateType = extras.getString("Create");
            if (!CreateType.equals("1"))
                position = extras.getInt("position");
        }

        gps = new GPSTracker(AddressActivity.this);
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            if (CreateType.equals("1")) {
                write(Constant.SHRED_PR.KEY_TEMP_LATITUDE, "" + latitude);
                write(Constant.SHRED_PR.KEY_TEMP_LONGITUDE, "" + longitude);
                write(Constant.SHRED_PR.KEY_RELOAD, "1");
            }
        }

        init();

        if (CreateType.equals("1")) {
            tvTitle.setText(getResources().getString(R.string.add_location));
        } else {
            setData();
            if (CreateType.equals("2")) {
                tvTitle.setText(getResources().getString(R.string.location));

                rl_next.setVisibility(View.GONE);
                rlLocation.setVisibility(View.GONE);

                etPermAdd1.setFocusableInTouchMode(false);
                etPermAdd2.setFocusableInTouchMode(false);
                etPermCity.setFocusableInTouchMode(false);
                etPermState.setFocusableInTouchMode(false);
                etPermCountry.setFocusableInTouchMode(false);
                etPermPincode.setFocusableInTouchMode(false);
                spinnerType.setFocusableInTouchMode(false);
                spinnerType.setFocusable(false);
                spinnerType.setEnabled(false);

            } else {
                tvTitle.setText(getResources().getString(R.string.update_location));
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }

    @OnClick(R.id.rlBack)
    @SuppressWarnings("unused")
    public void Back(View view) {
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }

//    @OnClick(R.id.icon_done)
//    @SuppressWarnings("unused")
//    public void done(View view) {
////        finish();
////        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
//    }


    @OnClick(R.id.rlLocation)
    @SuppressWarnings("unused")
    public void Location(View view) {
        startActivity(new Intent(AddressActivity.this, SelectLocationActivity.class));
        overridePendingTransition(R.anim.enter_from_bottom,
                R.anim.hold_bottom);
    }


    @OnClick(R.id.icon_done)
    @SuppressWarnings("unused")
    public void Save(View view) {

        hideKeyboard();
        if (isValidate()) {
            //Address address1 = new Address();
            if (address == null) address = new Address();
            address.setAddressLine1(getText(etPermAdd1));
            address.setAddressLine2(getText(etPermAdd2));
            address.setCity(getText(etPermCity));
            address.setState(getText(etPermState));
            address.setCountry(getText(etPermCountry));
            address.setLattitude("" + latitude);
            address.setLongitude("" + longitude);
            address.setType("" + (spinnerType.getSelectedItemPosition() + 2));
            address.setPincode(getText(etPermPincode));
            Gson gson = new Gson();
            write(Constant.SHRED_PR.KEY_TEMP_ADDRESS, gson.toJson(address));
            write(Constant.SHRED_PR.KEY_TEMP_POSITION, "" + position);
            write(Constant.SHRED_PR.KEY_TEMP_CREATE_TYPE, CreateType);
            write(Constant.SHRED_PR.KEY_RELOAD, "1");
            finish();
            overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
        }
    }


    @Override
    public void onResume() {
        super.onResume();

        gps = new GPSTracker(AddressActivity.this);
        if (!gps.canGetLocation()) gps.showSettingsAlert();

        if (read(Constant.SHRED_PR.KEY_RELOAD).equals("1")) {
            write(Constant.SHRED_PR.KEY_RELOAD, "0");
            if (read(Constant.SHRED_PR.KEY_TEMP_LATITUDE).length() > 0 && read(Constant.SHRED_PR.KEY_TEMP_LONGITUDE).length() > 0) {
                latitude = Double.parseDouble(read(Constant.SHRED_PR.KEY_TEMP_LATITUDE));
                longitude = Double.parseDouble(read(Constant.SHRED_PR.KEY_TEMP_LONGITUDE));
                Log.e("latitude", ">>" + latitude);
                Log.e("longitude", ">>" + longitude);

                new AsyncTask<Void, Void, List<android.location.Address>>() {

                    @Override
                    protected List<android.location.Address> doInBackground(Void... params) {
                        Geocoder geocoder = new Geocoder(AddressActivity.this, Locale.getDefault());

                        try {
                            List<android.location.Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
                            return addressList;
                        } catch (IOException e) {
                            Log.e("RegisterAct", "exp==" + e);
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(List<android.location.Address> addressList) {
                        super.onPostExecute(addressList);

                        try {
                            if (addressList != null && addressList.size() > 0) {

                                String strAddressLine1 = addressList.get(0).getAddressLine(0);
                                String strAddressLine2 = addressList.get(0).getAddressLine(1);
                                String state = addressList.get(0).getAdminArea();
                                String city = addressList.get(0).getSubAdminArea();
                                String countryName = addressList.get(0).getCountryName();
                                String postalCode = addressList.get(0).getPostalCode();
                                //countryName = countryName.replace(" ", "");
                                //state = state.replace(" ", "");

                                if (city == null) {
                                    city = addressList.get(0).getLocality();
                                }

                                tvLatLng.setText("Latitude: " + String.format("%.4f", latitude) + "\nLongitude: " + String.format("%.4f", longitude));
                                etPermAdd1.setText(strAddressLine1);
                                etPermAdd2.setText(strAddressLine2);
                                etPermCity.setText(city);
                                etPermState.setText(state);
                                etPermCountry.setText(countryName);
                                etPermPincode.setText(postalCode);

                            }
                        } catch (Exception e) {
                            Log.e("RegisterAct", "exp==" + e);
                        }

                    }
                }.execute();

            }
        }
    }

    private void init() {

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        etPermPincode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {

                    Util.hideKeyboard(AddressActivity.this);
                    if (Util.isOnline(getApplicationContext())) {
                        new searchPincode(etPermPincode.getText().toString().trim()).execute();
                    }

                    return true;
                }
                return false;
            }
        });

    }

    private void setData() {

        Bundle data = getIntent().getExtras();
        address = (Address) data.getSerializable("address");

        spinnerType.setSelection(Integer.parseInt("" + address.getType()) - 2);

        try {
            latitude = Double.parseDouble(address.getLattitude());
            longitude = Double.parseDouble(address.getLongitude());
            tvLatLng.setText("Latitude: " + String.format("%.4f", latitude) + "\nLongitude: " + String.format("%.4f", longitude));
        } catch (Exception e) {
            e.printStackTrace();
        }

        etPermPincode.setText(address.getPincode());
        etPermAdd1.setText(address.getAddressLine1());
        etPermAdd2.setText(address.getAddressLine2());
        etPermCity.setText(address.getCity());
        etPermState.setText(address.getState());
        etPermCountry.setText(address.getCountry());

        if (address.getType().equals("1")) {
            spinnerType.setVisibility(View.GONE);
            tvType.setText(getResources().getString(R.string.permanent_address));
        }
    }

    class searchPincode extends AsyncTask<Void, String, String> {

        String strPincode;

        private searchPincode(String strPincode) {
            this.strPincode = strPincode;
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

            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
            params1.add(new BasicNameValuePair("Pincode", "" + strPincode));

            Log.e("params1", ">>" + params1);
            String response = Util.makeServiceCall(Constant.URL1 + "searchPincode", 2, params1, getApplicationContext());

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
                    etPermCity.setText("" + new JSONObject(jsonObject.optString("data")).optString("city"));
                    etPermState.setText("" + new JSONObject(jsonObject.optString("data")).optString("state"));
                    etPermCountry.setText("" + new JSONObject(jsonObject.optString("data")).optString("country"));
                } else {
                    Toast.makeText(getApplicationContext(), "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public boolean isValidate() {

        if (etPermAdd1.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.PleaseEnterAdd1), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etPermCity.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.PleaseEnterCity), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etPermState.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.PleaseEnterState), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etPermCountry.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.PleaseEnterCountry), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (etPermPincode.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.PleaseEnterPincode), Toast.LENGTH_SHORT).show();
            return false;
        }
        if (latitude == 0.0 && longitude == 0.0) {
            Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.err_lat_lng), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


}
