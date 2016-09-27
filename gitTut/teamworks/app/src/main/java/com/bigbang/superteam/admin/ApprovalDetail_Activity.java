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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.ApprovalListAdapter;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.dataObjs.ApprovalModel;
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
 * Created by USER 3 on 4/15/2015.
 */
public class ApprovalDetail_Activity extends BaseActivity {
    @InjectView(R.id.tvType)
    TextView tvAttendanceType;
    @InjectView(R.id.tvDescription)
    TextView tvApprovalReason;
    @InjectView(R.id.tvUserName)
    TextView tvUserName;
    @InjectView(R.id.tvDate)
    TextView tvDate;
    @InjectView(R.id.btn_acceptApproval)
    Button acceptApprovalBtn;
    @InjectView(R.id.btn_rejectApproval)
    Button rejectApprovalBtn;
    @InjectView(R.id.imgWhite)
    ImageView imgWhite;
    @InjectView(R.id.imgLogo)
    ImageView imgLogo;
    @InjectView(R.id.tvContactImg)
    TextView tvContactImg;


    String approvalId, approvalType, userId, manualAttendance, approvalDetails,attendanceType,date;
    int clientVendorID;
    static SQLiteHelper helper;
    int type;
    public static SQLiteDatabase db = null;
    Intent i;
    ApprovalListAdapter approvalAdapter;
   // public static ArrayList<ApprovalModel> listApprovalPending;
    ApprovalModel approvalModel;
    String transactionID;
    JSONObject jsonObject;


    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options,options1;

    private TransparentProgressDialog pd;
    String TAG = "ApprovalDetail_Activity";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approvaldetail);
        ButterKnife.inject(this);

        pd = new TransparentProgressDialog(ApprovalDetail_Activity.this, R.drawable.progressdialog,false);
        helper = new SQLiteHelper(ApprovalDetail_Activity.this, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();
        Init();

    }

    private void Init()
    {
        i = getIntent();
        transactionID = i.getStringExtra("TransactionId");
        String data = i.getStringExtra("data");

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

        try{
            jsonObject = new JSONObject(data);
            manualAttendance = jsonObject.optString("manualAttendance");
           // locationType = jsonObject.optString("type");
            approvalId = ""+jsonObject.optInt("attendanceId");
            userId = ""+jsonObject.optInt("id");
            clientVendorID = jsonObject.optInt("clientVendorID");
            approvalDetails = ""+jsonObject.optString("reason");
            tvUserName.setText(""+jsonObject.optString("firstName") + " " + jsonObject.optString("lastName"));
            tvContactImg.setText("" + jsonObject.optString("firstName").toUpperCase());
            imageLoader.displayImage(""+jsonObject.optString("picture"),imgLogo , options);
            attendanceType = jsonObject.optString("manualAttendanceType");

            if ((jsonObject.optString("picture").length())>4){
                    tvContactImg.setVisibility(View.GONE);
            }else{
                tvContactImg.setVisibility(View.VISIBLE);
            }

            if(attendanceType.equals("full")){
                tvAttendanceType.setText("Full Day");
            }else{
                tvAttendanceType.setText("Half Day");
            }

             date = Util.dateFormatWithGMT("" + jsonObject.optString("date"));
            tvDate.setText(""+date);

           /// tvApprovalType.setText("" + approvalType);
            tvApprovalReason.setText("" + approvalDetails);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @OnClick(R.id.btn_acceptApproval) void acceptApproval() {

            if(Util.isOnline(ApprovalDetail_Activity.this)){
               // new attendanceApproveOrReject("CheckOut","acceptCheckOutAttendanceApproval").execute();
                new ApproveAttendance("true", false).execute();
            }else{
                Toast.makeText(ApprovalDetail_Activity.this, ApprovalDetail_Activity.this.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }

    }
    @OnClick(R.id.btn_rejectApproval) void rejectApproval() {

            if(Util.isOnline(ApprovalDetail_Activity.this)){
                //new attendanceApproveOrReject("CheckOut", "rejectCheckOutAttendanceApproval").execute();
                new ApproveAttendance("false", false).execute();
            }else{
                Toast.makeText(ApprovalDetail_Activity.this, ApprovalDetail_Activity.this.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            }

    }

    @OnClick(R.id.rlBack) void backPress() {
       finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }



    //new ApproveAttendance(index, "false", false).execute();
    class ApproveAttendance extends AsyncTask<Void, String, String> {


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

              //  JSONObject jData = new JSONObject(data.get(index).getData());

              /*  String strAddress = jData.optString("addressObject");
                JSONObject jAddress = new JSONObject(strAddress);*/
                List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
                params1.add(new BasicNameValuePair("MemberID", "" + userId));
                params1.add(new BasicNameValuePair("AttendanceID", "" +approvalId));
                params1.add(new BasicNameValuePair("AdminID", "" + Util.ReadSharePrefrence(ApprovalDetail_Activity.this, Constant.SHRED_PR.KEY_USERID)));
                params1.add(new BasicNameValuePair("TransactionID", "" +transactionID ));
                // params1.add(new BasicNameValuePair("AddressMasterID", "" + jData.optString("addressId")));
                // params1.add(new BasicNameValuePair("AddressMasterID", "" + jAddress.optString("AddressID")));
                params1.add(new BasicNameValuePair("Approve", "" +actionName));
                if (isChecked) {
                    params1.add(new BasicNameValuePair("IsPermanentLocation", "true"));
                } else {
                    params1.add(new BasicNameValuePair("IsPermanentLocation", "false"));
                }
                params1.add(new BasicNameValuePair("ClientID", "" + clientVendorID));
                // params1.add(new BasicNameValuePair("CompanyID", "" + jData.optString("companyId")));
                //params1.add(new BasicNameValuePair("AttendanceType", "" + jData.optString("attendanceType")));

                Log.e("params1", ">>" + params1);
                response = Util.makeServiceCall(Constant.URL + "approveAttendance", 2, params1, ApprovalDetail_Activity.this);
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
                Toast.makeText(ApprovalDetail_Activity.this, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
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

                }

            } catch (Exception e) {
                e.printStackTrace();
                //progresBar.setVisibility(View.GONE);
            }
        }
    }
}
