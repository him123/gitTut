package com.bigbang.superteam.user;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.R;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.login_register.VerifyMobileActivity;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.json.JSONObject;

import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ChangeMobileActivity extends BaseActivity {

    @InjectView(R.id.et_old_mobile)
    EditText etOldMobile;
    @InjectView(R.id.et_new_mobile)
    EditText etNewMobile;
    TransparentProgressDialog progressDialog;

    private static final String TAG = ChangeMobileActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_mobile);

        init();
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

    @OnClick(R.id.rlSave)
    @SuppressWarnings("unused")
    public void Save(View view) {

        hideKeyboard();

        String oldMobile = getText(etOldMobile);
        String newMobile = getText(etNewMobile);
        if (oldMobile.length() == 10 && newMobile.length() == 10) {
            if (Util.isOnline(getApplicationContext())) {
                changeMobileNo(oldMobile, newMobile);
            } else {
                toast(getResources().getString(R.string.network_error));
            }
        } else {
            toast(getResources().getString(R.string.PleaseEnterMobile));
        }
    }

    private void init() {

        progressDialog = new TransparentProgressDialog(ChangeMobileActivity.this, R.drawable.progressdialog, false);

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        etOldMobile.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etOldMobile.getText().length() == 1 && getText(etOldMobile).equals("0")) {
                    etOldMobile.setText("");
                    toast(getResources().getString(R.string.mobile_not_zero));
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

        etNewMobile.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etNewMobile.getText().length() == 1 && getText(etNewMobile).equals("0")) {
                    etNewMobile.setText("");
                    toast(getResources().getString(R.string.mobile_not_zero));
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });

    }

    private void changeMobileNo(String oldMobile, final String newMobile) {
        if (progressDialog != null) progressDialog.show();
        RestClient.getCommonService3().changeMobileNo(read(Constant.SHRED_PR.KEY_USERID), oldMobile, newMobile, read(Constant.SHRED_PR.KEY_TOKEN),
                new Callback<Response>() {
                    @Override
                    public void success(Response rc, Response response) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();

                        try {
                            JSONObject jObj = new JSONObject(Util.getString(response.getBody().in()));
                            String status = jObj.optString("status");
                            if (status.equals(Constant.InvalidToken)) {
                                TeamWorkApplication.LogOutClear(ChangeMobileActivity.this);
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
                                Intent intent = new Intent(ChangeMobileActivity.this, VerifyMobileActivity.class);
                                intent.putExtra("mobile", newMobile);
                                startActivity(intent);
                                finish();
                                overridePendingTransition(R.anim.enter_from_left,
                                        R.anim.hold_bottom);
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

}
