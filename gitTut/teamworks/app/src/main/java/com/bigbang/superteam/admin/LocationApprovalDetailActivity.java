
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
import android.widget.ToggleButton;

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


public class LocationApprovalDetailActivity extends BaseActivity {

    @InjectView(R.id.tvClientName)
    TextView tvClientName;
    @InjectView(R.id.tvDescription)
    TextView tvAddress;
    @InjectView(R.id.tvDate)
    TextView tvDate;
    @InjectView(R.id.tvTitle)
    TextView tvType;
    @InjectView(R.id.tvUserName)
    TextView tvUserName;
    @InjectView(R.id.tvTime)
    TextView tvTime;
    @InjectView(R.id.imgWhite)
    ImageView imgWhite;
    @InjectView(R.id.imgLogo)
    ImageView imgLogo;
    @InjectView(R.id.tvContactImg)
    TextView tvContactImg;
    @InjectView(R.id.toggleBtn)
    ToggleButton toggleBtn;


    Intent intent;
    String strJson, transactionId, strAddress, attendanceType,clientVendorID,attendanceId,firstName,lastName,picture;
    int userID;
    String TAG = "LocationApprovalDetailActivity";
    JSONObject jData;

    private TransparentProgressDialog pd;
    public static SQLiteDatabase db = null;

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options,options1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locationapproval_details);
        ButterKnife.inject(this);

        pd = new TransparentProgressDialog(LocationApprovalDetailActivity.this, R.drawable.progressdialog, false);

        SQLiteHelper helper = new SQLiteHelper(LocationApprovalDetailActivity.this, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        Init();
    }

    private void Init() {

        intent = getIntent();
        strJson = intent.getStringExtra("data");
        transactionId = intent.getStringExtra("TransactionId");
        Log.e(TAG, "Transaction id is:-    ========  " + transactionId);
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

        imageLoader.init(ImageLoaderConfiguration
                .createDefault(getApplicationContext()));

        imageLoader.displayImage("", imgWhite, options1);
        imageLoader.displayImage("", imgLogo, options);
        try {
            JSONObject jData = new JSONObject(strJson);
            Log.e(TAG,"!!!!!!!!!!!!!!!!>>>>>>>>>>>>>>>>> "+jData.toString());
            clientVendorID= jData.optString("clientVendorID");
            attendanceId = jData.optString("attendanceId");
            firstName = jData.optString("firstName");
            lastName = jData.optString("lastName");
            picture = jData.optString("picture");
            userID = jData.optInt("id");

            /*String addressData = "" + jData.optString("addressObject");
            JSONObject jsonObject = new JSONObject(addressData);
            tvClientName.setText("" + jData.optString("clientVendorName"));
            attendanceType = "" + jData.optString("attendanceType");
            if (attendanceType.equals("normalcheckin")) {
                tvType.setText("CheckIn Request");
                //tvTimeDisplay.setText("Time In");
                tvTime.setText("" + Util.gmtToLocalTime("" + jData.optString("timeIn")));
            } else {
                tvType.setText("CheckOut Request");
                //tvTimeDisplay.setText("Time Out");
                tvTime.setText("" + Util.gmtToLocalTime("" + jData.optString("timeOut")));
            }
            tvUserName.setText(""+jData.optString("firstName") +" "+jData.optString("lastName"));
            tvContactImg.setText("" + jData.optString("firstName").toUpperCase());
            imageLoader.displayImage(""+jData.optString("picture"),imgLogo , options);*/

          /*  if ((jData.optString("picture").length())>4){

                    tvContactImg.setVisibility(View.GONE);
                    Log.e(TAG,"Length of picture is:- "+jData.optString("picture"));
            }else{
                tvContactImg.setVisibility(View.VISIBLE);
                //imgLogo.setVisibility(View.GONE);
            }*/

           /* String date = Util.dateFormatWithGMT("" + jData.optString("strDate"));
            tvDate.setText(date);*/
           // tvAddress.setText("" + jsonObject.optString("AddressLine1") + "," + "\n" + jsonObject.optString("AddressLine2") + "," + "\n" + jsonObject.optString("City") + "," +"\n"+ jsonObject.optString("State") + "," +"\n"+ jsonObject.optString("Country"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (Util.isOnline(LocationApprovalDetailActivity.this)) {
                new GetDetails().execute();
        }else{
            Toast.makeText(LocationApprovalDetailActivity.this, LocationApprovalDetailActivity.this.getResources().getString(R.string.network_error), Toast.LENGTH_LONG).show();
        }

    }

    @OnClick(R.id.btn_acceptApproval)
    void AcceptApprovals() {
        if (Util.isOnline(LocationApprovalDetailActivity.this)) {
            if (toggleBtn.isChecked() == true) {
               // new ClientVendorAcceptOrReject("acceptCheckInAttendanceApproval", true).execute();
                new ApproveAttendance("true", true).execute();
            } else {
               // new ClientVendorAcceptOrReject("acceptCheckInAttendanceApproval", false).execute();
                new ApproveAttendance("true", false).execute();
            }
        } else {
            Toast.makeText(LocationApprovalDetailActivity.this, LocationApprovalDetailActivity.this.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.btn_rejectApproval)
    void RejectApprovals() {
        if (Util.isOnline(LocationApprovalDetailActivity.this)) {
            if (toggleBtn.isChecked() == true) {
                //new ClientVendorAcceptOrReject("rejectCheckInAttendanceApproval", true).execute();
                new ApproveAttendance("false", true).execute();
            } else {
               // new ClientVendorAcceptOrReject("rejectCheckInAttendanceApproval", false).execute();
                new ApproveAttendance("false", false).execute();
            }
        } else {
            Toast.makeText(LocationApprovalDetailActivity.this, LocationApprovalDetailActivity.this.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.rlBack)
    void backButton() {
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
                 params1.add(new BasicNameValuePair("TransactionID", transactionId));
                params1.add(new BasicNameValuePair("AttendanceID",attendanceId ));
                params1.add(new BasicNameValuePair("ClientID", clientVendorID));
                params1.add(new BasicNameValuePair("FirstName", firstName));
                params1.add(new BasicNameValuePair("LastName", lastName));
                params1.add(new BasicNameValuePair("Picture", picture));

                Log.e("params1", ">>" + params1);
                response = Util.makeServiceCall(Constant.URL + "getCheckInOutDetails", 1, params1, LocationApprovalDetailActivity.this);
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
                    Toast.makeText(LocationApprovalDetailActivity.this, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

public void setData(String data){
    try{
        JSONObject jObj = new JSONObject(data);
        String addressData = "" + jObj.optString("Address");
        JSONObject jsonAddress = new JSONObject(addressData);
        tvClientName.setText("" + jObj.optString("ClientName"));
        attendanceType = "" + jObj.optString("Type");
        tvTime.setText("" + Util.gmtToLocalTime("" +jObj.optString("Time")));
        if (attendanceType.equals("CheckOut")) {
            tvType.setText("CheckOut Request");
        } else {
            tvType.setText("CheckIn Request");
        }
        String date = Util.dateFormatWithGMT("" + jObj.optString("Date"));
        tvDate.setText(date);

        tvAddress.setText("" + jsonAddress.optString("AddressLine1") + "," + "\n" + jsonAddress.optString("AddressLine2") + "," + "\n" + jsonAddress.optString("City") + "," +"\n"+ jsonAddress.optString("State") + "," +"\n"+ jsonAddress.optString("Country"));

        tvUserName.setText(""+jObj.optString("firstName") +" "+jObj.optString("lastName"));
        tvContactImg.setText("" + jObj.optString("firstName").toUpperCase());
        imageLoader.displayImage(""+jObj.optString("picture"),imgLogo , options);

          if ((jObj.optString("picture").length())>4){

                    tvContactImg.setVisibility(View.GONE);
                    Log.e(TAG,"Length of picture is:- "+jObj.optString("picture"));
            }else{
                tvContactImg.setVisibility(View.VISIBLE);
                //imgLogo.setVisibility(View.GONE);
            }
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
                params1.add(new BasicNameValuePair("MemberID", ""+userID));
                params1.add(new BasicNameValuePair("AttendanceID", attendanceId));
                params1.add(new BasicNameValuePair("AdminID", "" + Util.ReadSharePrefrence(LocationApprovalDetailActivity.this, Constant.SHRED_PR.KEY_USERID)));
                params1.add(new BasicNameValuePair("TransactionID", ""+transactionId));
                params1.add(new BasicNameValuePair("Approve", "" +actionName));
                if (isChecked) {
                    params1.add(new BasicNameValuePair("IsPermanentLocation", "true"));
                } else {
                    params1.add(new BasicNameValuePair("IsPermanentLocation", "false"));
                }
                params1.add(new BasicNameValuePair("ClientID", clientVendorID));

                Log.e("params1", ">>" + params1);
                response = Util.makeServiceCall(Constant.URL + "approveAttendance", 2, params1, LocationApprovalDetailActivity.this);
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
                Toast.makeText(LocationApprovalDetailActivity.this, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
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

                    db.update(Constant.ApprovalsTable, values, "TransactionID like \"" + transactionId + "\"", null);
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
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }
}