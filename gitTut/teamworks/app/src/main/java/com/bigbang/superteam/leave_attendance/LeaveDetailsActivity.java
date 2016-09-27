package com.bigbang.superteam.leave_attendance;

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
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.Privileges;
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
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by USER 7 on 6/23/2015.
 */
public class LeaveDetailsActivity extends BaseActivity {

    @InjectView(R.id.tvStartDate)
    TextView tvStartDate;
    @InjectView(R.id.tvleaveDay)
    TextView tvleaveDay;
    @InjectView(R.id.tvTitle)
    TextView tvLeaveType;
    @InjectView(R.id.tvDescription)
    TextView tvReason;
   @InjectView(R.id.tvLeaveStatus)
    TextView tvLeaveStatus;
    @InjectView(R.id.btn_withdraw)
    Button btnLeaveWithDraw;
    @InjectView(R.id.linarBtn)
    LinearLayout linearButtons;
    @InjectView(R.id.tvUserName)
    TextView tvUserName;
    @InjectView(R.id.imgWhite)
    ImageView imgWhite;
    @InjectView(R.id.imgLogo)
    ImageView imgLogo;
    @InjectView(R.id.tvContactImg)
    TextView tvContactImg;

    Intent intent;

    static SQLiteHelper helper;
    public static SQLiteDatabase db = null;
    int activityFrom;

    private String leaveType,leaveStatus,startDate,reason,leaveId,userId,rollId,transactionID,adminID,imgUrl,userName,leaveDay;
    String TAG= "LeaveDetailsActivity";

    private TransparentProgressDialog pd;
    private Handler h;
    private Runnable r;

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options,options1;

    JSONObject jsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_details);
        ButterKnife.inject(this);
        pd = new TransparentProgressDialog(LeaveDetailsActivity.this, R.drawable.progressdialog,false);
        Init();
    }
    private void Init()
    {
        intent = getIntent();
        startDate=intent.getStringExtra("startDate");
       // endDate=  intent.getStringExtra("endDate");
        reason=intent.getStringExtra("reason");
        leaveType= intent.getStringExtra("leaveType");
        leaveStatus=  intent.getStringExtra("leaveStatus");
        leaveId=intent.getStringExtra("leaveId");
        userId = intent.getStringExtra("userID");
        transactionID = intent.getStringExtra("TransactionID");
        adminID = intent.getStringExtra("adminId");
        leaveDay = intent.getStringExtra("leaveDay");
        //activityName = intent.getStringExtra("activityName");
        String data = intent.getStringExtra("data");
        imgUrl = intent.getStringExtra("imgUrl");
        userName = intent.getStringExtra("userName");
        activityFrom = intent.getIntExtra("fromActivity",0);

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

       if(activityFrom== Constant.FROM_APPROVAL_ADAPTER){
           try{
               jsonObject = new JSONObject(data);
               tvUserName.setText(""+jsonObject.optString("firstName")+" "+jsonObject.optString("lastName"));
               tvContactImg.setText("" + jsonObject.optString("firstName").toUpperCase());

           }catch (Exception e){
               e.printStackTrace();
           }
       }else{
           tvUserName.setText(""+userName);
           tvContactImg.setText("" + userName.toUpperCase());
       }

        imageLoader.displayImage(imgUrl,imgLogo , options);

        if ((imgUrl.length())>5){

                tvContactImg.setVisibility(View.GONE);
        }else{
            tvContactImg.setVisibility(View.VISIBLE);
        }

        String startDt = Util.dateFormatWithGMT(""+startDate);
        tvStartDate.setText(startDt);
        tvleaveDay.setText("("+leaveDay+" day"+")");


        /*String endDt = Util.dateFormatWithGMT(""+endDate);
        tvEndDate.setText(endDt);*/
        tvLeaveType.setText(leaveType);
        tvReason.setText(reason);
       tvLeaveStatus.setText(leaveStatus);
        rollId = read(Constant.SHRED_PR.KEY_ROLE_ID);

        if(rollId.equals("1")|| rollId.equals("2")){
            if(leaveStatus.equals("Pending")){
                linearButtons.setVisibility(View.VISIBLE);
            }else{
                linearButtons.setVisibility(View.GONE);
            }

            btnLeaveWithDraw.setVisibility(View.GONE);
        }else if(rollId.equals("4")){
            linearButtons.setVisibility(View.GONE);
            if(leaveStatus.equals("Pending")||leaveStatus.equals("Approved"))
            {

                btnLeaveWithDraw.setVisibility(View.VISIBLE);
                // btnLeaveWithDraw.setVisibility(View.VISIBLE);
                /*int isDateGreater = Util.calculateDateDifference(""+startDate);
                if(isDateGreater==0){
                    btnLeaveWithDraw.setVisibility(View.GONE);
                }else{
                    btnLeaveWithDraw.setVisibility(View.VISIBLE);
                }*/
            }
            else
            {
                btnLeaveWithDraw.setVisibility(View.GONE);
            }



        }else if(rollId.equals("3")){
            if (activityFrom==Constant.FROM_LEAVE_HISTORY_ADAPTER) {
                linearButtons.setVisibility(View.GONE);
                if(leaveStatus.equals("Pending")||leaveStatus.equals("Approved"))
                {
                   // btnLeaveWithDraw.setVisibility(View.VISIBLE);
                    /*int isDateGreater = Util.calculateDateDifference(""+startDate);
                    if(isDateGreater==0){
                        btnLeaveWithDraw.setVisibility(View.GONE);
                    }else{
                        btnLeaveWithDraw.setVisibility(View.VISIBLE);
                    }*/
                    btnLeaveWithDraw.setVisibility(View.VISIBLE);
                }
                else
                {
                    btnLeaveWithDraw.setVisibility(View.GONE);
                }
            } else {
                if(Privileges.APPROVE_LEAVE_REQUEST){
                    if(leaveStatus.equals("Pending")){
                        linearButtons.setVisibility(View.VISIBLE);
                    }else{
                        linearButtons.setVisibility(View.GONE);
                    }

                }else{
                    linearButtons.setVisibility(View.GONE);
                }

                btnLeaveWithDraw.setVisibility(View.GONE);
            }

        }


    }

    @OnClick(R.id.btn_withdraw)
    void applyLeaveWithDraw() {
        if(Util.isOnline(LeaveDetailsActivity.this)){
            new leaveWithdraw().execute();
        }else{
            try{
                JSONObject jObj = new JSONObject();
                ArrayList<String> list = new ArrayList<String>();
                list.add(startDate);

                JSONArray jsArray = new JSONArray(list);
                Log.e(TAG,"Json array value is:- "+jsArray.toString().replace("\\",""));

                jObj.put("UserID", "" +userId);
                jObj.put("LeaveDates", ""+jsArray.toString().replace("\\",""));
                jObj.put("Status", ""+leaveStatus);

                String strJson = jObj.toString();
                String actionName = "withdrawLeaves";
                String methodType = "2";

                Util.insertIntoOffline(strJson, methodType, actionName,LeaveDetailsActivity.this);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }
    @OnClick(R.id.btn_acceptApproval)
    void leaveAccept() {
        if(Util.isOnline(LeaveDetailsActivity.this)){
            new LeaveApproveOrReject("true").execute();
        }else{
            Toast.makeText(LeaveDetailsActivity.this, LeaveDetailsActivity.this.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }

    }
    @OnClick(R.id.btn_rejectApproval)
    void leaveReject() {
        if(Util.isOnline(LeaveDetailsActivity.this)){
            new LeaveApproveOrReject("false").execute();
        }else{
            Toast.makeText(LeaveDetailsActivity.this, LeaveDetailsActivity.this.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.rlBack)
    void backPress() {
       finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }

    class leaveWithdraw extends AsyncTask<Void, String, String> {
        String response;
        //ProgressDialog pd;
   @Override
        protected void onPreExecute() {
       super.onPreExecute();
           /* pd = new ProgressDialog(LeaveDetailsActivity.this);
            pd.show();*/
      if(pd!=null){
          pd.show();
      }
   }
        @Override
        protected String doInBackground(Void... params) {

            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            ArrayList<String> list = new ArrayList<String>();
            list.add(startDate);

            JSONArray jsArray = new JSONArray(list);
            Log.e(TAG,"Json array value is:- "+jsArray.toString().replace("\\",""));

            params1.add(new BasicNameValuePair("UserID", userId));
            params1.add(new BasicNameValuePair("LeaveDates", ""+jsArray.toString().replace("\\","")));
            params1.add(new BasicNameValuePair("Status", ""+leaveStatus));
           // params1.add(new BasicNameValuePair("AdminID", ""+Util.ReadSharePrefrence(LeaveDetailsActivity.this, Constant.SHRED_PR.KEY_USERID)));


            Log.e("params1", ">>" + params1);
          /*  response = Util.postData(params1, Constant.URL
                    + "leaveWithdrawn");*/

            response = Util.makeServiceCall(Constant.URL+"withdrawLeaves",2,params1,getApplicationContext());

            Log.v(TAG, "" + response);
            return response;
        }
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if(pd!=null)
                pd.dismiss();
            try {
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");
                Toast.makeText(LeaveDetailsActivity.this, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
                if (status.equals("Success")) {
                     tvLeaveStatus.setText("Withdrawn");
                    btnLeaveWithDraw.setVisibility(View.GONE);

                   /* Intent i = new Intent(LeaveDetailsActivity.this,LeaveStatusActivity.class);
                    startActivity(i);*/
                    write(Constant.SHRED_PR.KEY_RELOAD, "1");
                    finish();
                }
               /* else{
                    JSONObject jData = jObj.getJSONObject("data");
                    String state = jData.optString("RemoveId");
                    if(state.equals("1"))
                    {
                        Toast.makeText(LeaveDetailsActivity.this, "You can't withdraw this leave as it is already cancelled or withdrawn", Toast.LENGTH_LONG).show();
                       *//*Intent i = new Intent(LeaveDetailsActivity.this,LeaveStatusActivity.class);
                        startActivity(i);*//*
                        write(Constant.SHRED_PR.KEY_RELOAD, "1");
                        finish();
                    }
                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
/*
    public void removeFromDataBase(String approvalID) {
        helper = new SQLiteHelper(LeaveDetailsActivity.this, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();
        String whereClause = "id" + "=?";
        String[] whereArgs = new String[]{"" + approvalID};

        int isDelete = db.delete(Constant.ApprovalsTable, whereClause, whereArgs);
        write(Constant.SHRED_PR.KEY_RELOAD, "1");

        finish();
    }
*/


    public void removeFromDataBase(String approvalID) {
        Log.v(TAG, "***** Inside removeFromDataBase");
        // String whereClause = "id" + "=?";
        String whereClause = "TransactionID" + "=?";
        String[] whereArgs = new String[]{"" + approvalID};

        int isDelete = Util.getDb(LeaveDetailsActivity.this).delete(Constant.ApprovalsTable, whereClause, whereArgs);
        write(Constant.SHRED_PR.KEY_RELOAD, "1");

        finish();

        //removeFromArrayList(index);
    }
    class LeaveApproveOrReject extends AsyncTask<Void, String, String> {

        String actionName;
        String response;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(pd!=null){
                pd.show();
            }
        }
        public LeaveApproveOrReject(String action) {
            this.actionName = action;
        }
        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            ArrayList<String> list = new ArrayList<String>();

            list.add(startDate);

            JSONArray jsArray = new JSONArray(list);
            Log.e(TAG,"Json array value is:- "+jsArray.toString().replace("\\",""));

            params1.add(new BasicNameValuePair("MemberID", "" +userId));
            params1.add(new BasicNameValuePair("TransactionID", "" + transactionID));
            params1.add(new BasicNameValuePair("LeaveDates", ""+jsArray.toString().replace("\\","")));
            params1.add(new BasicNameValuePair("Approve", "" +actionName));

            Log.e("params1", ">>" + params1);
           /* response = Util.postData(params1, Constant.URL
                    + actionName);*/
            response = Util.makeServiceCall(Constant.URL + "approveLeaves", 2, params1, LeaveDetailsActivity.this);
            Log.e(TAG,"** response is:- "+response);
            return response;
        }
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if(pd!=null)
                pd.dismiss();
            try {
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");
                Toast.makeText(LeaveDetailsActivity.this, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
                if (status.equals("Success")) {

                    //removeFromDataBase(transactionID);

                    int approvalStatus=1;
                    if(actionName.equals("true")) approvalStatus=2;
                    else if (actionName.equals("false")) approvalStatus=3;
                    updateApprovalsDB( approvalStatus);

                   // write(Constant.SHRED_PR.KEY_RELOAD, "1");
                   /* Intent i = new Intent(LeaveDetailsActivity.this,ApprovalList_Activity.class);
                    startActivity(i);*/
                    //finish();
                }
               /* else{
                    JSONObject jData = jObj.getJSONObject("data");
                    String state = jData.optString("RemoveId");

                }*/
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateApprovalsDB(int approvalStatus) {
        ContentValues values = new ContentValues();
        values.put("Status", "" + approvalStatus);
        db.update(Constant.ApprovalsTable, values, "TransactionID like \"" +transactionID + "\"", null);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }

}
