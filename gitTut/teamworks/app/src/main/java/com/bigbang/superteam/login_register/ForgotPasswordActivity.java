package com.bigbang.superteam.login_register;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.bigbang.superteam.R;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import java.util.Map;

import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ForgotPasswordActivity extends BaseActivity {

    @InjectView(R.id.et_mobile)
    EditText etMobile;
    TransparentProgressDialog progressDialog;

    private static final String TAG = ForgotPasswordActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        init();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
    }


    @OnClick(R.id.rlBack)
    @SuppressWarnings("unused")
    public void Back(View view) {

        hideKeyboard();
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
    }


    @OnClick(R.id.rlSave)
    @SuppressWarnings("unused")
    public void Reset(View view) {

        hideKeyboard();

        String Mobile = getText(etMobile);
        if (Mobile.length() == 10) {
            if (Util.isOnline(getApplicationContext())) {
                resetPassword(Mobile);
            } else {
                toast(getResources().getString(R.string.network_error));
            }
        } else {
            toast(getResources().getString(R.string.PleaseEnterMobile));
        }
    }

    private void init() {

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        progressDialog = new TransparentProgressDialog(ForgotPasswordActivity.this, R.drawable.progressdialog, true);

        etMobile.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etMobile.getText().length() == 1 && getText(etMobile).equals("0")) {
                    etMobile.setText("");
                    toast(getResources().getString(R.string.mobile_not_zero));
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void resetPassword(String Mobile) {
        if (progressDialog != null) progressDialog.show();
        RestClient.getCommonService3().resetPassword(Mobile, read(Constant.SHRED_PR.KEY_USERID), read(Constant.SHRED_PR.KEY_TOKEN),
                new Callback<Response>() {
                    @Override
                    public void success(Response rc, Response response) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();
                        try {
                            Log.e(TAG, Util.getString(response.getBody().in()));
                            Map<String, String> map = Util.readStatus(response);
                            boolean isSuccess = map.get("status").equals("Success");
                            toast(map.get("message"));
                            if (isSuccess) {
                                finish();
                                overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
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
