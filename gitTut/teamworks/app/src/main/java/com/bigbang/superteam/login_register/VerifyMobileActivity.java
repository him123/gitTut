package com.bigbang.superteam.login_register;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.GCMIntentService;
import com.bigbang.superteam.R;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class VerifyMobileActivity extends BaseActivity {

    @InjectView(R.id.tvTitle)
    TextView tvTitle;
    @InjectView(R.id.et_otp)
    EditText etOTP;
    TransparentProgressDialog progressDialog;

    MyReceiver myReceiver;

    String mobile;

    private static final String TAG = VerifyMobileActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_mobile);

        init();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            mobile = extras.getString("mobile");
        }

        tvTitle.setText("We've sent a 6 digit code to "+mobile+". Please enter the code below to verify the mobile");

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
    

    @Override
    protected void onResume() {
        super.onResume();

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

    private void init() {

        progressDialog = new TransparentProgressDialog(VerifyMobileActivity.this, R.drawable.progressdialog, false);

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();

        Util.setAppFont(mContainer, mFont);
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            Bundle extras = arg1.getExtras();
            if (extras != null) {

                String OTP = extras.getString("OTP");
                etOTP.setText(OTP);
                //rlVerify.performClick();

            }
        }
    }

    @OnClick(R.id.rl_verify)
    @SuppressWarnings("unused")
    public void Verify(View view) {
        Util.hideKeyboard(VerifyMobileActivity.this);

        String otp = etOTP.getText().toString().trim();
        if (otp.length() > 0) {
            if (Util.isOnline(getApplicationContext())) {
                verifyChangeMobileNo(otp);
            } else {
                toast(getResources().getString(R.string.network_error));
            }
        } else {
            toast(getResources().getString(R.string.PleaseEnterOTP));
        }
    }

    @OnClick(R.id.rl_regenerate)
    @SuppressWarnings("unused")
    public void Regenerate(View view) {
        if (Util.isOnline(getApplicationContext())) {
            new generateOTP().execute();
        } else {
            toast(getResources().getString(R.string.network_error));
        }
    }

    @OnClick(R.id.rlCancel)
    @SuppressWarnings("unused")
    public void Cancel(View view) {
        if (progressDialog != null) progressDialog.show();
        RestClient.getCommonService3().cancelChangeMobile(mobile, read(Constant.SHRED_PR.KEY_USERID), read(Constant.SHRED_PR.KEY_TOKEN),
                new Callback<Response>() {
                    @Override
                    public void success(Response rc, Response response) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();

                        try {
                            JSONObject jObj = new JSONObject(Util.getString(response.getBody().in()));
                            String status = jObj.optString("status");
                            if (status.equals(Constant.InvalidToken)) {
                                TeamWorkApplication.LogOutClear(VerifyMobileActivity.this);
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            Log.e(TAG, Util.getString(response.getBody().in()));
                            Map<String, String> map = Util.readStatus(response);
                            boolean isSuccess = map.get("status").equals("Success");
                            toast(map.get("message"));
                            if (isSuccess) {
                                finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();
                    }
                });
    }

    private void verifyChangeMobileNo(String otp) {
        if (progressDialog != null) progressDialog.show();
        RestClient.getCommonService3().verifyChangeMobileNo(read(Constant.SHRED_PR.KEY_USERID), mobile, otp, read(Constant.SHRED_PR.KEY_COMPANY_ID), "TeamWorks", read(Constant.SHRED_PR.KEY_TOKEN),
                new Callback<Response>() {
                    @Override
                    public void success(Response rc, Response response) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();

                        try {
                            JSONObject jObj = new JSONObject(Util.getString(response.getBody().in()));
                            String status = jObj.optString("status");
                            if (status.equals(Constant.InvalidToken)) {
                                TeamWorkApplication.LogOutClear(VerifyMobileActivity.this);
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            Log.e(TAG, Util.getString(response.getBody().in()));
                            Map<String, String> map = Util.readStatus(response);
                            boolean isSuccess = map.get("status").equals("Success");
                            toast(map.get("message"));
                            if (isSuccess) {
                                finish();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();
                    }
                });
    }

    class verifyMobile extends AsyncTask<Void, String, String> {

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

            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
            params1.add(new BasicNameValuePair("ID", "" + Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID)));
            params1.add(new BasicNameValuePair("OTP", "" + etOTP.getText().toString().trim()));

            Log.e("params1", ">>" + params1);
            String response = Util.makeServiceCall(Constant.URL1 + "verifyMobile", 2, params1, getApplicationContext());

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            if (progressDialog != null)
                if (progressDialog.isShowing()) progressDialog.dismiss();
            Log.e("result", ">>" + result);

            try {
                JSONObject jsonObject = new JSONObject(result);

                Toast.makeText(getApplicationContext(), "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                String status = jsonObject.getString("status");
                if (status.equals("Success")) {
                    finish();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    class generateOTP extends AsyncTask<Void, String, String> {

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

            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
            params1.add(new BasicNameValuePair("UserID", "" + Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_USERID)));
            params1.add(new BasicNameValuePair("MobileNo", "" + mobile));

            Log.e("params1", ">>" + params1);
            String response = Util.makeServiceCall(Constant.URL1 + "generateOtp", 1, params1, getApplicationContext());

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            if (progressDialog != null)
                if (progressDialog.isShowing()) progressDialog.dismiss();
            Log.e("result", ">>" + result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                Toast.makeText(getApplicationContext(), "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


}
