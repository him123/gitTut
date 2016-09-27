package com.bigbang.superteam.leave_attendance;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.LeaveHistoryAdapter;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.dataObjs.LeaveHistoryModel;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by USER 3 on 4/9/2015.
 */
public class LeaveStatusActivity extends BaseActivity {

    @InjectView(R.id.listvwLeaveStatus)
    ListView listVWLeaveStatus;
    @InjectView(R.id.progressBar)
    ProgressBar pBar;
    @InjectView(R.id.tvNoHistory)
    TextView tvNoHisory;

    LeaveHistoryAdapter leaveHistoryAdapter;
    public static ArrayList<LeaveHistoryModel> listLeaveHistory;
    LeaveHistoryModel leaveHistoryModel;

    SQLiteHelper helper;
    String TAG = "LeaveStatusActivity Activity";
    public static SQLiteDatabase db = null;
    private TransparentProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leavestatus);
        ButterKnife.inject(this);
        pd = new TransparentProgressDialog(LeaveStatusActivity.this, R.drawable.progressdialog,false);
        Init();
    }

    @OnClick(R.id.btnExportExcel)
    void createCSV() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int currentYear = calendar.get(Calendar.YEAR);
        int result =Util.createCSVFileForLeave(listLeaveHistory,""+currentYear,LeaveStatusActivity.this);
        if(result==1)
        {
            Toast.makeText(LeaveStatusActivity.this,"Your file has been created successfully",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(LeaveStatusActivity.this,"Some error occurred while creating file",Toast.LENGTH_LONG).show();

        }
    }

    @OnClick(R.id.btnExportPdf)
    void createPdf(){
       /* int result =Util.createCSVFileForAttendance(listAttendHistory);
        if(result==1)
        {
            Toast.makeText(getActivity(),"Your file has been created successfully",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getActivity(),"Some error occurred while creating file",Toast.LENGTH_LONG).show();

        }*/
    }
    private void Init()
    {
        helper = new SQLiteHelper(this, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        listLeaveHistory = new ArrayList<LeaveHistoryModel>();
        //fetchDataFromDB();
        if(Util.isOnline(LeaveStatusActivity.this)){
            new getLeaveStatus().execute();
        }else{
            Toast.makeText(LeaveStatusActivity.this, LeaveStatusActivity.this.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            String User_ID = Util.ReadSharePrefrence(LeaveStatusActivity.this, Constant.SHRED_PR.KEY_USERID);
            reload(Util.ReadFile(getCacheDir()+"/LeaveHistory", User_ID + ".txt"));
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (read(Constant.SHRED_PR.KEY_RELOAD).equals("1")){
            write(Constant.SHRED_PR.KEY_RELOAD, "0");
            if(Util.isOnline(LeaveStatusActivity.this)){
                new getLeaveStatus().execute();
            }else{
                Toast.makeText(LeaveStatusActivity.this, LeaveStatusActivity.this.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                String User_ID = Util.ReadSharePrefrence(LeaveStatusActivity.this, Constant.SHRED_PR.KEY_USERID);
                reload(Util.ReadFile(getCacheDir()+"/LeaveHistory", User_ID + ".txt"));
            }
        }
    }

    class getLeaveStatus extends AsyncTask<Void, String, String> {
        String response;
       // ProgressDialog pd;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(pd!=null){
                pd.show();
            }
        }
        @Override
        protected String doInBackground(Void... params) {

            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            params1.add(new BasicNameValuePair("MemberID", "" + ""+ Util.ReadSharePrefrence(LeaveStatusActivity.this, Constant.SHRED_PR.KEY_USERID)));

            Log.e("params1", ">>" + params1);

            response = Util.makeServiceCall(Constant.URL
                    + "leaveStatus",1,params1,getApplicationContext());

            Log.e(TAG,""+response);
            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            String User_ID = Util.ReadSharePrefrence(LeaveStatusActivity.this, Constant.SHRED_PR.KEY_USERID);
            Util.WriteFile(getCacheDir()+"/LeaveHistory", User_ID + ".txt", result);
            reload(result);

        }
    }
    private  void reload(String result){
        try {
            JSONObject jObj = new JSONObject(result);
            String status = jObj.optString("status");
            if(pd!=null){
                pd.dismiss();
            }
            listLeaveHistory.clear();
            if (status.equals("Success")) {

                JSONArray jsonArray = jObj.getJSONArray("data");
                for(int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    leaveHistoryModel = new LeaveHistoryModel();
                    leaveHistoryModel.setUserId("" + jsonObject.optInt("userId"));
                    leaveHistoryModel.setApprovalId("" + jsonObject.optString("uniqueKey"));
                    leaveHistoryModel.setStartDate("" + jsonObject.optString("strDate"));
                    leaveHistoryModel.setLeaveDay(""+jsonObject.optString("leaveDay"));
                  //  leaveHistoryModel.setEndDate("" + jsonObject.optString("endDate"));
                    leaveHistoryModel.setReason(""+jsonObject.optString("reason"));
                    leaveHistoryModel.setImageUrl(""+jsonObject.optString("picture"));
                    leaveHistoryModel.setLeaveStatus(""+jsonObject.optString("status"));
                    leaveHistoryModel.setUserName(""+jsonObject.optString("firstName")+" "+jsonObject.optString("lastName"));
                    leaveHistoryModel.setLeaveType(""+jsonObject.optString("type"));

                    listLeaveHistory.add(leaveHistoryModel);
                }
            } else {
                Toast.makeText(LeaveStatusActivity.this, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
            }

            if (listLeaveHistory.size() <= 0) {
                tvNoHisory.setVisibility(View.VISIBLE);
            } else {
                tvNoHisory.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            if(pd!=null){
                pd.dismiss();
            }
        }
        leaveHistoryAdapter = new LeaveHistoryAdapter(LeaveStatusActivity.this, listLeaveHistory);
        listVWLeaveStatus.setAdapter(leaveHistoryAdapter);
    }

}
