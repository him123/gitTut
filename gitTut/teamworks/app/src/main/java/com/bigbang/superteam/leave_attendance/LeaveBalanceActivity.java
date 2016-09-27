package com.bigbang.superteam.leave_attendance;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.LeaveBalanceAdapter;
import com.bigbang.superteam.dataObjs.LeaveBalanceModel;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 7 on 6/9/2015.
 */
public class LeaveBalanceActivity extends Activity {

    @InjectView(R.id.listvwLeaveBalance)
    ListView listVWLeaveBalance;
    @InjectView(R.id.progressBar)
    ProgressBar pBar;

    String TAG = "LeaveBalanceActivity";
    LeaveBalanceModel modelLeaveBalance;
    ArrayList<LeaveBalanceModel> listLeaveBalance;
    LeaveBalanceAdapter leaveBalanceAdapter;
    private TransparentProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_balance);
        ButterKnife.inject(this);
        pd = new TransparentProgressDialog(LeaveBalanceActivity.this, R.drawable.progressdialog,true);
        listLeaveBalance = new ArrayList<LeaveBalanceModel>();

        if (Util.isOnline(LeaveBalanceActivity.this)) {
            new getLeaveBalance().execute();
        } else {
            Toast.makeText(LeaveBalanceActivity.this, LeaveBalanceActivity.this.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            String User_ID = Util.ReadSharePrefrence(LeaveBalanceActivity.this, Constant.SHRED_PR.KEY_USERID);
            reload(Util.ReadFile(getCacheDir()+"/LeaveBalance", User_ID + ".txt"));
        }


    }


    class getLeaveBalance extends AsyncTask<Void, String, String> {
        String response;

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
            params1.add(new BasicNameValuePair("MemberID", "" + "" + Util.ReadSharePrefrence(LeaveBalanceActivity.this, Constant.SHRED_PR.KEY_USERID)));
            Log.e("params1", ">>" + params1);

            response = Util.makeServiceCall(Constant.URL
                    + "leaveBalance", 1, params1,getApplicationContext());
            Log.e(TAG, "" + response);
            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            String User_ID = Util.ReadSharePrefrence(LeaveBalanceActivity.this, Constant.SHRED_PR.KEY_USERID);
            Util.WriteFile(getCacheDir()+"/LeaveBalance", User_ID + ".txt", result);
            reload(result);

        }

    }

    private void reload(String result) {
        try {
            JSONObject jObj = new JSONObject(result);
            String status = jObj.optString("status");
            if(pd!=null){
                pd.dismiss();
            }
            listLeaveBalance.clear();
            if (status.equals("Success")) {
                JSONArray jsonArray = jObj.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    modelLeaveBalance = new LeaveBalanceModel();

                    modelLeaveBalance.setLeaveId("" + jsonObject.optInt("leaveId"));
                    modelLeaveBalance.setLeaveType("" + jsonObject.optString("type"));
                    modelLeaveBalance.setNumberOfLeaves("" + jsonObject.optInt("totalBalance"));
                    modelLeaveBalance.setAvailableLeaves("" + jsonObject.optInt("balance"));
                    modelLeaveBalance.setUsedLeaves("" + jsonObject.optInt("takenBalance"));

                    listLeaveBalance.add(modelLeaveBalance);
                }
            } else {
                Toast.makeText(LeaveBalanceActivity.this, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(pd!=null){
                pd.dismiss();
            }
        }
        leaveBalanceAdapter = new LeaveBalanceAdapter(LeaveBalanceActivity.this, listLeaveBalance, pBar);
        listVWLeaveBalance.setAdapter(leaveBalanceAdapter);
    }
}
