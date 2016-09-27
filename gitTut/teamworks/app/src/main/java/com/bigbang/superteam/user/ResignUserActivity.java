package com.bigbang.superteam.user;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.R;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ResignUserActivity extends BaseActivity {

    @InjectView(R.id.etReason)
    EditText etReason;
    @InjectView(R.id.tvCount)
    TextView tvCount;
    TransparentProgressDialog progressDialog;
    boolean flagAutoResign = false;
    HashMap<String, String> hashMap = null;

    private static final String TAG = ResignUserActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resign_user);

        init();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            flagAutoResign = true;
            hashMap = (HashMap<String, String>) extras.getSerializable("map");
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

    private void init() {

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        progressDialog = new TransparentProgressDialog(ResignUserActivity.this, R.drawable.progressdialog, false);
        etReason.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                tvCount.setText("" + (200 - s.length()));
            }
        });

    }

    @OnClick(R.id.rlResign)
    @SuppressWarnings("unused")
    public void Resign(View view) {

        hideKeyboard();

        AlertDialog.Builder alert1 = new AlertDialog.Builder(ResignUserActivity.this);
        alert1.setTitle("" + Constant.AppNameSuper);
        alert1.setMessage(getResources().getString(R.string.confirm_resignation));
        alert1.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @SuppressLint("InlinedApi")
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        if (Util.isOnline(getApplicationContext())) {
                            if (flagAutoResign)
                                acceptCompanyInvite(getText(etReason));
                            else resign(getText(etReason));
                        } else {
                            toast(getResources().getString(R.string.network_error));
                        }
                    }
                }
        );
        alert1.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                    }
                }

        );
        alert1.create();
        alert1.show();

    }


    private void resign(String reason) {
        if (progressDialog != null) progressDialog.show();
        RestClient.getCommonService3().resign(read(Constant.SHRED_PR.KEY_USERID), read(Constant.SHRED_PR.KEY_COMPANY_ID), reason, Constant.AppName, read(Constant.SHRED_PR.KEY_TOKEN),
                new Callback<Response>() {
                    @Override
                    public void success(Response rc, Response response) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();

                        try {
                            JSONObject jObj = new JSONObject(Util.getString(response.getBody().in()));
                            String status = jObj.optString("status");
                            if (status.equals(Constant.InvalidToken)) {
                                TeamWorkApplication.LogOutClear(ResignUserActivity.this);
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
                                if (read(Constant.SHRED_PR.KEY_ROLE_ID).equals("1")) {
                                    TeamWorkApplication.LogOutClear(ResignUserActivity.this);
                                } else {
                                    finish();
                                    overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
                                }
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

    private void acceptCompanyInvite(String Description) {
        if (progressDialog != null) progressDialog.show();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("ID", hashMap.get("ID"));
            jsonObject.put("EmailID", hashMap.get("EmailID"));
            jsonObject.put("Name", hashMap.get("Name"));
            jsonObject.put("MobileNo", hashMap.get("MobileNo"));
            jsonObject.put("CreatedBy", hashMap.get("CreatedBy"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        String companyID = hashMap.get("Company_ID");
        String roleID = hashMap.get("role_id");
        String managerID = hashMap.get("managerID");

        RestClient.getCommonService().acceptCompanyInvite(Description, jsonObject.toString(), Constant.AppName, companyID, roleID, managerID, read(Constant.SHRED_PR.KEY_USERID), read(Constant.SHRED_PR.KEY_TOKEN),
                new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response1) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();

                        try {
                            JSONObject jObj = new JSONObject(Util.getString(response.getBody().in()));
                            String status = jObj.optString("status");
                            Log.e(TAG, "resonse is:- " + response);
                            if (status.equals(Constant.InvalidToken)) {
                                TeamWorkApplication.LogOutClear(ResignUserActivity.this);
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (response != null) {
                            try {
                                String json = Util.getString(response.getBody().in());
                                Log.e(TAG, ">>" + response);
                                JSONObject jsonObject = new JSONObject(json.toString());
                                String status = jsonObject.getString("status");
                                Toast.makeText(getApplicationContext(), "" + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                if (status.equals("Success")) {
                                    write(Constant.SHRED_PR.KEY_RELOAD, "1");
                                    finish();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();
                        Log.e(TAG, "" + error);
                        toast(getResources().getString(R.string.some_error));
                    }
                }

        );
    }
}
