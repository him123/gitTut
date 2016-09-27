package com.bigbang.superteam.user;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.R;
import com.bigbang.superteam.common.BaseActivity;
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

public class ResetPasswordForMember extends BaseActivity {

    @InjectView(R.id.etPassword)
    EditText etPassword;
    @InjectView(R.id.etConfirmPassword)
    EditText etConfirmPassword;
    TransparentProgressDialog progressDialog;

    String userID;
    private static final String TAG = ResetPasswordForMember.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_for_member);

        init();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userID = extras.getString("userid");
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

    @OnClick(R.id.rlResetPassword)
    @SuppressWarnings("unused")
    public void ResetPassword(View view) {

        hideKeyboard();

        if (isValidate()) {
            if (Util.isOnline(getApplicationContext())) {
                resetPassword(getText(etPassword));
            } else {
                toast(getResources().getString(R.string.network_error));
            }
        }
    }

    private void init() {
        progressDialog = new TransparentProgressDialog(ResetPasswordForMember.this, R.drawable.progressdialog, false);

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);
    }

    private void resetPassword(String password) {
        if (progressDialog != null) progressDialog.show();
        RestClient.getCommonService3().resetPasswordForMember(read(Constant.SHRED_PR.KEY_USERID), userID, read(Constant.SHRED_PR.KEY_COMPANY_ID), password, read(Constant.SHRED_PR.KEY_USERID), read(Constant.SHRED_PR.KEY_TOKEN),
                new Callback<Response>() {
                    @Override
                    public void success(Response rc, Response response) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();

                        try {
                            JSONObject jObj = new JSONObject(Util.getString(response.getBody().in()));
                            String status = jObj.optString("status");
                            if (status.equals(Constant.InvalidToken)) {
                                TeamWorkApplication.LogOutClear(ResetPasswordForMember.this);
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
                                overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
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

    private boolean isValidate() {

        if (isEmpty(getText(etPassword))) {
            toast(getResources().getString(R.string.enter_password));
            return false;
        }
        if (isEmpty(getText(etConfirmPassword))) {
            toast(getResources().getString(R.string.enter_confirm_password));
            return false;
        }
        if (!getText(etPassword).equals(getText(etConfirmPassword))) {
            toast(getResources().getString(R.string.password_not_match));
            return false;
        }
        return true;
    }

}
