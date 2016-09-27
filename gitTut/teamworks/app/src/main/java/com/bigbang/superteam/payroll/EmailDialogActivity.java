package com.bigbang.superteam.payroll;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.R;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.json.JSONObject;

import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EmailDialogActivity extends Activity {

    @InjectView(R.id.etEmailID)
    EditText etEmailID;
    @InjectView(R.id.btDownload)
    Button btDownload;
    @InjectView(R.id.btClose)
    Button btClose;

    Intent intent;
    String userID, emailID;
    String month, year;

    private TransparentProgressDialog progressDialog;

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_dialog);

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        ButterKnife.inject(this);

        handler = new Handler();

        progressDialog = new TransparentProgressDialog(this, R.drawable.progressdialog, false);

        intent = getIntent();
        userID = intent.getStringExtra("userID");

        month = intent.getStringExtra("month");
        year = intent.getStringExtra("year");
        emailID = intent.getStringExtra("email_ID");

        if(emailID != null){
            if(emailID.length() > 0)
                etEmailID.setText(emailID + "");
        }

    }

    @OnClick(R.id.btClose)
    void close(){
        finish();
    }

    @OnClick(R.id.btDownload)
    void download(){
        if(getText(etEmailID) != null){

            if(isValidate()) {

                if (Util.isOnline(this)) {
                    if (progressDialog != null)
                        progressDialog.show();

                    RestClient.getTeamWork().emailPayslip(Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_USERID),
                            Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_COMPANY_ID),
                            month + "",
                            year + "",
                            getText(etEmailID),
                            Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_TOKEN),
                            new Callback<Response>() {
                                @Override
                                public void success(Response response, Response response2) {
                                    try {
                                        Map<String, String> map = Util.readStatus(response);
                                        boolean isSuccess = map.get("status").equals("Success");

                                        String json = Util.getString(response.getBody().in());
                                        JSONObject jObj = new JSONObject(json);

                                        String status = jObj.optString("status");
                                        if (status.equals(Constant.InvalidToken)) {
                                            TeamWorkApplication.LogOutClear(EmailDialogActivity.this);
                                            return;
                                        }

                                        /*if (isSuccess) {
                                            Toast.makeText(EmailDialogActivity.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                                            finish();
                                        } else {
                                            Toast.makeText(EmailDialogActivity.this, jObj.getString("message"), Toast.LENGTH_SHORT).show();
                                        }*/
                                        progressDialog.dismiss();
                                    } catch (Exception e) {
                                        Log.d("", "Exception : " + e);
                                        progressDialog.dismiss();
                                    }
                                }

                                @Override
                                public void failure(RetrofitError error) {
                                    progressDialog.dismiss();
                                }
                            });

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EmailDialogActivity.this, "Payslip will be send to you via email shortly", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            finish();
                        }
                    }, 2000);

                } else {
                    Toast.makeText(this, Constant.network_error + "", Toast.LENGTH_SHORT).show();
                }
            }
        }else{
            Toast.makeText(this, "Please enter Email Id", Toast.LENGTH_SHORT).show();
        }

    }

    protected String getText(EditText eTxt) {
        return eTxt == null ? "" : eTxt.getText().toString().trim();
    }

    private boolean isValidate(){
        if (!getText(etEmailID).isEmpty()) {
            if(!android.util.Patterns.EMAIL_ADDRESS.matcher(getText(etEmailID)).matches()) {
                Toast.makeText(this, R.string.PleaseEnterEmail, Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return true;
    }

}
