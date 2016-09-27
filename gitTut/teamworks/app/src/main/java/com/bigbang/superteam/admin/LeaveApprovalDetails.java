package com.bigbang.superteam.admin;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.Privileges;
import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.LeaveDatesAdapter;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by USER 7 on 1/21/2016.
 */
public class LeaveApprovalDetails extends BaseActivity {


    @InjectView(R.id.imgWhite)
    ImageView imgWhite;
    @InjectView(R.id.imgLogo)
    ImageView imgLogo;
    @InjectView(R.id.tvContactImg)
    TextView tvContactImg;
    @InjectView(R.id.linarBtn)
    LinearLayout linearButtons;
    @InjectView(R.id.btn_withdraw)
    Button btnLeaveWithDraw;
    @InjectView(R.id.listviewLeaves)
    ListView listviewLeaves;
    @InjectView(R.id.tvUserName)
    TextView tvUserName;
    @InjectView(R.id.tvDate)
    TextView tvDate;
    @InjectView(R.id.tvTitle)
    TextView tvLeaveType;
    @InjectView(R.id.tvDescription)
    TextView tvReason;
    @InjectView(R.id.tvLeaveStatus)
    TextView tvLeaveDay;

    Intent intent;

    static SQLiteHelper helper;
    public static SQLiteDatabase db = null;
    int activityFrom;

    private String leaveStatus, startDate, reason, leaveId, userId, rollId, transactionID, adminID, imgUrl;
    String TAG = "LeaveApprovalDetails";
    String data, firstName, lastName, picture;

    private TransparentProgressDialog pd;
    private Handler h;
    private Runnable r;

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options, options1;

    JSONObject jsonObject;

    LeaveDatesAdapter datesAdapter;
    ArrayList<HashMap<String, String>> listLeaveDates = new ArrayList<>();
    ArrayList<String> listDates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leavedetails_approval);
        ButterKnife.inject(this);
        pd = new TransparentProgressDialog(LeaveApprovalDetails.this, R.drawable.progressdialog, false);
        helper = new SQLiteHelper(LeaveApprovalDetails.this, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();
        Init();
    }

    private void Init() {

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        intent = getIntent();

        imgUrl = intent.getStringExtra("imgUrl");
        leaveStatus = intent.getStringExtra("leaveStatus");
        userId = intent.getStringExtra("userID");
        transactionID = intent.getStringExtra("TransactionID");
        data = intent.getStringExtra("data");

        try {
            JSONObject jsonObj = new JSONObject(data);

            firstName = jsonObj.optString("firstName");
            lastName = jsonObj.optString("lastName");
            tvLeaveType.setText(""+jsonObj.optString("LeaveType"));
            tvContactImg.setText("" + jsonObj.optString("firstName").toUpperCase());

        } catch (Exception e) {
            e.printStackTrace();
        }

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

        imageLoader.displayImage(imgUrl, imgLogo, options);

        if ((imgUrl.length()) > 5) {

            tvContactImg.setVisibility(View.GONE);
        } else {
            tvContactImg.setVisibility(View.VISIBLE);
        }

        tvUserName.setText(""+firstName+" "+lastName);

        rollId = read(Constant.SHRED_PR.KEY_ROLE_ID);

        if (rollId.equals("1") || rollId.equals("2")) {
            if (leaveStatus.equals("Pending")) {
                linearButtons.setVisibility(View.VISIBLE);
            } else {
                linearButtons.setVisibility(View.GONE);
            }

            btnLeaveWithDraw.setVisibility(View.GONE);
        } else if (rollId.equals("4")) {
            linearButtons.setVisibility(View.GONE);
            if (leaveStatus.equals("Pending") || leaveStatus.equals("Approved")) {
                // btnLeaveWithDraw.setVisibility(View.VISIBLE);
                int isDateGreater = Util.calculateDateDifference("" + startDate);
                if (isDateGreater == 0) {
                    btnLeaveWithDraw.setVisibility(View.GONE);
                } else {
                    btnLeaveWithDraw.setVisibility(View.VISIBLE);
                }
            } else {
                btnLeaveWithDraw.setVisibility(View.GONE);
            }


        } else if (rollId.equals("3")) {
            if (activityFrom == Constant.FROM_LEAVE_HISTORY_ADAPTER) {
                linearButtons.setVisibility(View.GONE);
                if (leaveStatus.equals("Pending") || leaveStatus.equals("Approved")) {
                    // btnLeaveWithDraw.setVisibility(View.VISIBLE);
                    int isDateGreater = Util.calculateDateDifference("" + startDate);
                    if (isDateGreater == 0) {
                        btnLeaveWithDraw.setVisibility(View.GONE);
                    } else {
                        btnLeaveWithDraw.setVisibility(View.VISIBLE);
                    }
                } else {
                    btnLeaveWithDraw.setVisibility(View.GONE);
                }
            } else {
                if (Privileges.APPROVE_LEAVE_REQUEST) {
                    if (leaveStatus.equals("Pending")) {
                        linearButtons.setVisibility(View.VISIBLE);
                    } else {
                        linearButtons.setVisibility(View.GONE);
                    }

                } else {
                    linearButtons.setVisibility(View.GONE);
                }

                btnLeaveWithDraw.setVisibility(View.GONE);
            }

        }

        if (Util.isOnline(LeaveApprovalDetails.this)) {
            new getLeaveDetails().execute();
        } else {
            Toast.makeText(LeaveApprovalDetails.this, LeaveApprovalDetails.this.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }


    }

    @OnClick(R.id.btn_acceptApproval)
    void leaveAccept() {
        if (Util.isOnline(LeaveApprovalDetails.this)) {

            listDates.clear();
            for(int i = 0;i<listLeaveDates.size();i++){
                if(listLeaveDates.get(i).get("approved").equals("1")){
                    listDates.add(""+listLeaveDates.get(i).get("date"));
                }
            }

            if(listDates.size()<=0){
                Toast.makeText(LeaveApprovalDetails.this,"Please select at least one day",Toast.LENGTH_LONG).show();
            }else{
                JSONArray jsArray = new JSONArray(listDates);
                new LeaveApproveOrReject("true",""+jsArray.toString().replace("\\","")).execute();
            }

        } else {
            Toast.makeText(LeaveApprovalDetails.this, LeaveApprovalDetails.this.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }

    }

    @OnClick(R.id.btn_rejectApproval)
    void leaveReject() {
        if (Util.isOnline(LeaveApprovalDetails.this)) {
            listDates.clear();
            for(int i = 0;i<listLeaveDates.size();i++){
                if(listLeaveDates.get(i).get("approved").equals("1")){
                    listDates.add(""+listLeaveDates.get(i).get("date"));
                }
            }
            if(listDates.size()<=0){
                Toast.makeText(LeaveApprovalDetails.this,"Please select at least one day",Toast.LENGTH_LONG).show();
            }else{
                JSONArray jsArray = new JSONArray(listDates);
                new LeaveApproveOrReject("false",""+jsArray.toString().replace("\\","")).execute();
            }
        } else {
            Toast.makeText(LeaveApprovalDetails.this, LeaveApprovalDetails.this.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.rlBack)
    void backPress() {
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }

    class LeaveApproveOrReject extends AsyncTask<Void, String, String> {

        String actionName;
        String response,approvedLeaveDates;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pd != null) {
                pd.show();
            }
        }

        public LeaveApproveOrReject(String action,String leaves) {
            this.actionName = action;
            approvedLeaveDates = leaves;
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            params1.add(new BasicNameValuePair("MemberID", ""+userId));
            params1.add(new BasicNameValuePair("TransactionID", "" +transactionID));
            params1.add(new BasicNameValuePair("LeaveDates", ""+approvedLeaveDates));
            params1.add(new BasicNameValuePair("Approve", "" +actionName));

            Log.e("params1", ">>" + params1);
            response = Util.makeServiceCall(Constant.URL + "approveLeaves", 2, params1, LeaveApprovalDetails.this);

            Log.e(TAG, "** response is:- " + response);
            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (pd != null)
                pd.dismiss();
            try {
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");

                Toast.makeText(LeaveApprovalDetails.this, ""+jObj.optString("message"), Toast.LENGTH_LONG).show();
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
                    Log.e(TAG,"value of approval state is:- "+values.toString()+" transaction id is:- "+transactionID);

                    db.update(Constant.ApprovalsTable, values, "TransactionID like \"" + transactionID + "\"", null);
                    finish();
                    overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    class getLeaveDetails extends AsyncTask<Void, String, String> {

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
                params1.add(new BasicNameValuePair("MemberID", userId));
                params1.add(new BasicNameValuePair("TransactionID", transactionID));
                params1.add(new BasicNameValuePair("FirstName", firstName));
                params1.add(new BasicNameValuePair("LastName", lastName));
                params1.add(new BasicNameValuePair("Picture", imgUrl));

                Log.e("params1", ">>" + params1);
                response = Util.makeServiceCall(Constant.URL + "leaveRequestDetails", 1, params1, LeaveApprovalDetails.this);
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
                    JSONObject jsonObject1 = new JSONObject(data);
                    tvReason.setText(""+jsonObject1.optString("Reason"));
                     tvLeaveDay.setText(""+jsonObject1.optString("LeaveDay")+" day");

                   // tvReason.setText(""+jsonObject1.optString("Reason"));
                    JSONArray jsonArray = jsonObject1.getJSONArray("Dates");

                    Log.e(TAG, "----->>" + jsonArray.length());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        HashMap<String, String> hashMap = new HashMap<String, String>();

                        hashMap.put("date", "" + jsonArray.get(i));
                        hashMap.put("approved", "1");
                        Log.e(TAG, "=====>>>>>>>" +i);
                        listLeaveDates.add(i,hashMap);
                    }

                    if(listLeaveDates.size()>1){
                        tvDate.setVisibility(View.GONE);
                        listviewLeaves.setVisibility(View.VISIBLE);
                        datesAdapter = new LeaveDatesAdapter(LeaveApprovalDetails.this, listLeaveDates);
                        listviewLeaves.setAdapter(datesAdapter);
                        Util.setListViewHeightBasedOnChildren(listviewLeaves);
                    }else{
                        String startDt = Util.dateFormatWithGMT("" + listLeaveDates.get(0).get("date"));
                        tvDate.setText(startDt);
                        tvDate.setVisibility(View.VISIBLE);
                        listviewLeaves.setVisibility(View.INVISIBLE);
                    }
                } else {
                    Toast.makeText(LeaveApprovalDetails.this, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
                }

            } catch (Exception e) {
                e.printStackTrace();

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
