package com.bigbang.superteam.admin;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.dataObjs.Approval;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;
import butterknife.OnClick;

public class ResignationApprovalActivity extends BaseActivity {

    private String TAG = "ResignationApprovalActivity";
    @InjectView(R.id.img_profile_small)
    ImageView imgLogo;
    @InjectView(R.id.imgWhite)
    ImageView imgWhite;
    @InjectView(R.id.tvName)
    TextView tvName;
    @InjectView(R.id.tvProfileName)
    TextView tvTitle;
    @InjectView(R.id.desc)
    TextView tvDesc;
    Context mContext = this;
    Approval approval;
    TransparentProgressDialog progressDialog;
    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options, options1;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resignation_approval);

        init();

        approval = (Approval) getIntent().getSerializableExtra("Approval");

        if (approval != null) {
            tvTitle.setText("" + approval.getTitle());
            tvName.setText("" + approval.getTitle().toUpperCase());
            try {
                JSONObject jsonObject = new JSONObject(approval.getData());
                tvDesc.setText("" + jsonObject.optString("Description"));
            } catch (Exception e) {
                e.printStackTrace();
            }

            String logo = approval.getImage();
            imageLoader.displayImage(logo, imgLogo, options);

            if (logo.length() > 0) tvName.setVisibility(View.GONE);
            else tvName.setVisibility(View.VISIBLE);


        } else finish();

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


    @OnClick(R.id.rlAccept)
    @SuppressWarnings("unused")
    public void Accept(View view) {
        if(Util.isOnline(getApplicationContext())){
            new UserResignApproval("true", mContext).execute();
        }else{
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.rlReject)
    @SuppressWarnings("unused")
    public void Reject(View view) {
        if(Util.isOnline(getApplicationContext())){
            new UserResignApproval("false", mContext).execute();
        }else{
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }


    private void init() {
        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);
        progressDialog = new TransparentProgressDialog(ResignationApprovalActivity.this, R.drawable.progressdialog, false);

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(100))
                .showImageOnLoading(R.drawable.default_image).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        options1 = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.empty_profilepic).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.empty_profilepic).showImageOnFail(R.drawable.empty_profilepic).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        imageLoader.init(ImageLoaderConfiguration
                .createDefault(getApplicationContext()));

        imageLoader.displayImage("", imgWhite, options1);
        imageLoader.displayImage("", imgLogo, options);

    }

    class UserResignApproval extends AsyncTask<Void, String, String> {

        String Approved;
        String response;
        Context context;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog != null) {
                progressDialog.show();
            }
        }

        public UserResignApproval(String Approved, Context context) {
            this.Approved = Approved;
            this.context = context;
        }

        @Override
        protected String doInBackground(Void... params) {


            try {
                JSONObject jData = new JSONObject(approval.getData());

                List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
                params1.add(new BasicNameValuePair("OwnerID", "" + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID)));
                params1.add(new BasicNameValuePair("CompanyID", "" + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_COMPANY_ID)));
                params1.add(new BasicNameValuePair("MemberID", "" + approval.getUserId()));
                params1.add(new BasicNameValuePair("TransactionID", "" + approval.getTransactionID()));
                params1.add(new BasicNameValuePair("Accept", Approved));
                params1.add(new BasicNameValuePair("Application", Constant.AppName));
                response = Util.makeServiceCall(Constant.URL1 + "acceptRejectResignation", 2, params1, context);
                Log.d("params1", ">>" + params1);

                Log.v(TAG, "** response is:- " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (progressDialog != null) {
                if (progressDialog.isShowing()) progressDialog.dismiss();
            }

            try {
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");
                Toast.makeText(mContext, "" + jObj.getString("message"), Toast.LENGTH_SHORT).show();
                if (status.equals("Success")) {
                    int approvalStatus=1;
                    if(Approved.equals("true")){
                        approvalStatus=2;
                    }
                    else{
                        approvalStatus=3;
                    }
                    ContentValues values = new ContentValues();
                    values.put("Status", "" + approvalStatus);

                    write(Constant.SHRED_PR.KEY_RELOAD, "1");
                    db.update(Constant.ApprovalsTable, values, "TransactionID like \"" + approval.getTransactionID() + "\"", null);
                    finish();
                    overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
