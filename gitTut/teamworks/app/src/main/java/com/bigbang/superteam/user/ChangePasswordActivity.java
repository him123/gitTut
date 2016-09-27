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

public class ChangePasswordActivity extends BaseActivity {

    @InjectView(R.id.et_old_password)
    EditText etOldPassword;
    @InjectView(R.id.et_new_password)
    EditText etNewPassword;
    @InjectView(R.id.et_confirm_password)
    EditText etConfirmPassword;
    TransparentProgressDialog progressDialog;

    private static final String TAG = ChangePasswordActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
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

        String oldPassword = getText(etOldPassword);
        String newPassword = getText(etNewPassword);
        if (isValidate()) {
            if (Util.isOnline(getApplicationContext())) {
                changePassword(oldPassword, newPassword);
            } else {
                toast(getResources().getString(R.string.network_error));
            }
        }
    }

    private void init() {

        progressDialog = new TransparentProgressDialog(ChangePasswordActivity.this, R.drawable.progressdialog, false);

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);
    }


    private void changePassword(String oldPassword, final String newPassword) {
        if (progressDialog != null) progressDialog.show();
        RestClient.getCommonService3().changePassword(read(Constant.SHRED_PR.KEY_USERID), oldPassword, newPassword, read(Constant.SHRED_PR.KEY_TOKEN),
                new Callback<Response>() {
                    @Override
                    public void success(Response rc, Response response) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();

                        try {
                            JSONObject jObj = new JSONObject(Util.getString(response.getBody().in()));
                            String status = jObj.optString("status");
                            if (status.equals(Constant.InvalidToken)) {
                                TeamWorkApplication.LogOutClear(ChangePasswordActivity.this);
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

    public boolean isValidate() {
        if (isEmpty(getText(etOldPassword))) {
            toast(R.string.PleaseEnterPassword);
            return false;
        }
        if (isEmpty(getText(etNewPassword))) {
            toast(R.string.PleaseEnterPassword);
            return false;
        }
        if (isEmpty(getText(etConfirmPassword))) {
            toast(R.string.PleaseEnterPassword);
            return false;
        }
        if (!getText(etNewPassword).equals(getText(etConfirmPassword))) {
            toast(R.string.PasswordDontMatch);
            return false;
        }
        return true;
    }

}
