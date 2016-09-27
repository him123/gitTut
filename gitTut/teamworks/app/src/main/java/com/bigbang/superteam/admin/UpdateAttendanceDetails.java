package com.bigbang.superteam.admin;

import android.content.ContentValues;
import android.content.Intent;
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

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by USER 7 on 7/22/2015.
 */
public class UpdateAttendanceDetails extends BaseActivity {

    @InjectView(R.id.tvNewTimeOut)
    TextView tvNewTimeOut;
    @InjectView(R.id.tvNewTimeIn)
    TextView tvNewTimeIn;
    @InjectView(R.id.tvOldTimeIn)
    TextView tvOldTimeIn;
    @InjectView(R.id.tvOldTimeOut)
    TextView tvOldTimeOut;
    @InjectView(R.id.tvNewTimeOutTitle)
    TextView tvNewTimeOutTitle;
    @InjectView(R.id.tvOldTimeOutTitle)
    TextView tvOldTimeOutTitle;
    @InjectView(R.id.tvDate)
    TextView tvDate;
    @InjectView(R.id.tvDescription)
    TextView tvReason;
    @InjectView(R.id.imgWhite)
    ImageView imgWhite;
    @InjectView(R.id.imgLogo)
    ImageView imgLogo;
    @InjectView(R.id.tvContactImg)
    TextView tvContactImg;
    @InjectView(R.id.tvUserName)
    TextView tvUserName;

    Intent intent;
    String uniqueKey;
    String userId,transactionID,attendanceType;
    String TAG = "UpdateAttendanceDeatils";
    JSONObject jData;
    String firstName,lastName,picture;
    boolean checkInApproved,checkOutApproved,isPresent;
    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options,options1;

    private TransparentProgressDialog pd;
    public static SQLiteDatabase db = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatetime_deatil);
        ButterKnife.inject(this);

        pd = new TransparentProgressDialog(UpdateAttendanceDetails.this, R.drawable.progressdialog,false);

        SQLiteHelper helper = new SQLiteHelper(UpdateAttendanceDetails.this, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        Init();
    }

    private void Init() {

        intent = getIntent();
        String strJson = intent.getStringExtra("data");
        transactionID = intent.getStringExtra("TransactionId");

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(150))
                .showImageOnLoading(R.drawable.default_image).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        options1 = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.empty_profilepic).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.empty_profilepic).showImageOnFail(R.drawable.empty_profilepic).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        try{
            jData  = new JSONObject(strJson);

            uniqueKey = jData.optString("attendanceID");
            firstName = jData.optString("firstName");
            lastName = jData.optString("lastName");
            picture = jData.optString("picture");
            userId = ""+jData.optInt("id");

            if (Util.isOnline(UpdateAttendanceDetails.this)) {
                new GetDetails().execute();
            }else{
                Toast.makeText(UpdateAttendanceDetails.this, UpdateAttendanceDetails.this.getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
            }

      /*


            // String date = Util.gmtToLocal("" + data.get(index).getDate());
            uniqueKey = jData.optString("attendanceId");
            userId = ""+jData.optInt("userId");*/
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @OnClick(R.id.btn_acceptApproval)
    void AcceptApprovals() {
        if(Util.isOnline(UpdateAttendanceDetails.this)){
            //new UpdateTimeAccept("acceptUpdateCheckInOutTimeApproval").execute();
            new ApproveAttendance("true", false).execute();

        }else{
            Toast.makeText(UpdateAttendanceDetails.this, UpdateAttendanceDetails.this.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_rejectApproval)
    void RejectApprovals() {
        if(Util.isOnline(UpdateAttendanceDetails.this)){
           // new UpdateTimeReject("rejectUpdateCheckInOutApproval").execute();
            new ApproveAttendance("false", false).execute();
        }else{
            Toast.makeText(UpdateAttendanceDetails.this, UpdateAttendanceDetails.this.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.rlBack)
    void backPress() {
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }



    class GetDetails extends AsyncTask<Void, String, String> {

        int index;
        String actionName;
        String response;
        boolean isChecked;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progresBar.setVisibility(View.VISIBLE);
            if (pd != null) {
                pd.show();
            }
        }

        @Override
        protected String doInBackground(Void... params) {

            try {

                List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
                params1.add(new BasicNameValuePair("TransactionID", transactionID));
                params1.add(new BasicNameValuePair("AttendanceID",uniqueKey ));
              //  params1.add(new BasicNameValuePair("ClientID", clientVendorID));
                params1.add(new BasicNameValuePair("FirstName", firstName));
                params1.add(new BasicNameValuePair("LastName", lastName));
                params1.add(new BasicNameValuePair("Picture", picture));

                Log.e("params1", ">>" + params1);
                response = Util.makeServiceCall(Constant.URL + "getUpdateDetails", 1, params1, UpdateAttendanceDetails.this);
                Log.e(TAG, "** response is:- " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (pd != null) {
                pd.dismiss();
            }
            try {
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");
                if (status.equals("Success")) {
                    String data = jObj.optString("data");
                    setData(data);
                }else{
                    Toast.makeText(UpdateAttendanceDetails.this, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    public void setData(String data){

        try{
            JSONObject jObj = new JSONObject(data);

            tvOldTimeIn.setText(Util.gmtToLocalTime(""+jObj.optString("Old CheckIn")));
            tvOldTimeOut.setText(Util.gmtToLocalTime(""+jObj.optString("Old CheckOut")));
            tvNewTimeIn.setText(Util.gmtToLocalTime(""+jObj.optString("New CheckIn")));
            tvNewTimeOut.setText(Util.gmtToLocalTime(""+jObj.optString("New CheckOut")));
            checkInApproved = jObj.optBoolean("checkInApproved");
            checkOutApproved = jObj.optBoolean("checkOutApproved");

            if(checkInApproved){
                tvNewTimeIn.setTextColor(UpdateAttendanceDetails.this.getResources().getColor(R.color.gray));
            }else{
                tvNewTimeIn.setTextColor(UpdateAttendanceDetails.this.getResources().getColor(R.color.black));
            }
            if(checkOutApproved){
                tvNewTimeOut.setTextColor(UpdateAttendanceDetails.this.getResources().getColor(R.color.gray));
            }else{
                tvNewTimeOut.setTextColor(UpdateAttendanceDetails.this.getResources().getColor(R.color.black));
            }
            tvUserName.setText(""+jObj.optString("firstName")+" "+jObj.optString("lastName"));
            tvContactImg.setText("" + jObj.optString("firstName").toUpperCase());

            tvDate.setText(Util.dateFormatWithGMT(""+jObj.optString("Date")));
            tvReason.setText(""+jObj.optString("Reason"));

            imageLoader.init(ImageLoaderConfiguration
                    .createDefault(getApplicationContext()));

            imageLoader.displayImage("", imgWhite, options1);
            imageLoader.displayImage("", imgLogo, options);
            imageLoader.displayImage(""+jData.optString("picture"),imgLogo , options);
            if ((jData.optString("picture").length())>4){
                tvContactImg.setVisibility(View.GONE);
                Log.e(TAG,"Length of picture is:- "+jData.optString("picture"));
            }else{
                tvContactImg.setVisibility(View.VISIBLE);
                //imgLogo.setVisibility(View.GONE);
            }

            if (jObj.optString("Old CheckOut").length()==0) {
                tvNewTimeOut.setVisibility(View.GONE);
                tvNewTimeOutTitle.setVisibility(View.GONE);
                tvOldTimeOut.setVisibility(View.GONE);
                tvOldTimeOutTitle.setVisibility(View.GONE);
            } else {
                tvOldTimeOut.setVisibility(View.VISIBLE);
                tvOldTimeOutTitle.setVisibility(View.VISIBLE);
                tvNewTimeOut.setVisibility(View.VISIBLE);
                tvNewTimeOutTitle.setVisibility(View.VISIBLE);
            }
            /*isPresent = jData.optBoolean("present");
            if(isPresent){
                /*//*linearOldTimeOut.setVisibility(View.GONE);
                //linearNewTimeOut.setVisibility(View.GONE);*//*/
                tvNewTimeOut.setVisibility(View.GONE);
                tvNewTimeOutTitle.setVisibility(View.GONE);
                tvOldTimeOut.setVisibility(View.GONE);
                tvOldTimeOutTitle.setVisibility(View.GONE);
            }else{
                tvOldTimeOut.setVisibility(View.VISIBLE);
                tvOldTimeOutTitle.setVisibility(View.VISIBLE);
                tvNewTimeOut.setVisibility(View.VISIBLE);
                tvNewTimeOutTitle.setVisibility(View.VISIBLE);
            }*/
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    class ApproveAttendance extends AsyncTask<Void, String, String> {

        int index;
        String actionName;
        String response;
        boolean isChecked;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // progresBar.setVisibility(View.VISIBLE);
            if (pd != null) {
                pd.show();
            }
        }

        public ApproveAttendance(String action, boolean isChecked) {

            this.actionName = action;
            this.isChecked = isChecked;
        }

        @Override
        protected String doInBackground(Void... params) {

            try {

                List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
                params1.add(new BasicNameValuePair("MemberID", ""+userId));
                params1.add(new BasicNameValuePair("AttendanceID", uniqueKey));
                params1.add(new BasicNameValuePair("AdminID", "" + Util.ReadSharePrefrence(UpdateAttendanceDetails.this, Constant.SHRED_PR.KEY_USERID)));
                params1.add(new BasicNameValuePair("TransactionID", transactionID));
                params1.add(new BasicNameValuePair("Approve", "" +actionName));
                if (isChecked) {
                    params1.add(new BasicNameValuePair("IsPermanentLocation", "true"));
                } else {
                    params1.add(new BasicNameValuePair("IsPermanentLocation", "false"));
                }
                params1.add(new BasicNameValuePair("ClientID","0"));

                Log.e("params1", ">>" + params1);
                response = Util.makeServiceCall(Constant.URL + "approveAttendance", 2, params1, UpdateAttendanceDetails.this);
                Log.e(TAG, "** response is:- " + response);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (pd != null) {
                pd.dismiss();
            }
            try {
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");
                //progresBar.setVisibility(View.GONE);
                Toast.makeText(UpdateAttendanceDetails.this, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
                if (status.equals("Success")) {

                    int approvalStatus=1;
                    if(actionName.equals("true")){
                        approvalStatus=2;
                    }
                    else if(actionName.equals("false")){
                        approvalStatus=3;
                    }
                    ContentValues values = new ContentValues();
                    values.put("Status", "" + approvalStatus);

                    db.update(Constant.ApprovalsTable, values, "TransactionID like \"" + transactionID + "\"", null);
                    finish();
                    overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);

                }else if(status.equals("Fail")){
                    write(Constant.SHRED_PR.KEY_RELOAD, "1");
                    finish();
                    overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
                }
            } catch (Exception e) {
                e.printStackTrace();
                //progresBar.setVisibility(View.GONE);
            }
        }
    }
}
