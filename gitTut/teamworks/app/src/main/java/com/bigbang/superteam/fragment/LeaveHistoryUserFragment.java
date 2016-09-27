package com.bigbang.superteam.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.LeaveHistoryAdapter;
import com.bigbang.superteam.adapter.LeaveStatusOfUsersAdapter;
import com.bigbang.superteam.dataObjs.LeaveBalanceModel;
import com.bigbang.superteam.dataObjs.LeaveHistoryModel;
import com.bigbang.superteam.leave_attendance.ApplyLeaveActivity;
import com.bigbang.superteam.leave_attendance.UpdateLeaveBalanceActivity;
import com.bigbang.superteam.util.Constant;
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
 * Created by USER 7 on 11/3/2015.
 */
public class LeaveHistoryUserFragment extends Fragment {

    public static Context mContext;
    public static ArrayList<LeaveHistoryModel> listLeaveHistory;
    public static ArrayList<LeaveHistoryModel> listLeaveHistoryAdmin;
    static String userID = "ds";
    @InjectView(R.id.listviewLeave)
    ListView listviewLeave;
    @InjectView(R.id.tvSickLeave)
    TextView tvSickLeave;
    @InjectView(R.id.tvCasualLeave)
    TextView tvCasualLeave;
    @InjectView(R.id.tvPaidLeave)
    TextView tvPaidLeave;
    @InjectView(R.id.tvOptionalLeave)
    TextView tvOptionalLeave;
    @InjectView(R.id.tvNoHistory)
    TextView tvNoHisory;
    @InjectView(R.id.rlApplyLeave)
    RelativeLayout relativeLeaveApply;
    @InjectView(R.id.btn_updateBalance)
    Button btnUpdateBalance;

    LeaveBalanceModel modelLeaveBalance;
    ArrayList<LeaveBalanceModel> listLeaveBalance;
    LeaveHistoryAdapter leaveHistoryAdapter;
    LeaveHistoryModel leaveHistoryModel;
    LeaveStatusOfUsersAdapter leaveHistoryAdapteAdminr;
    String TAG = "LeaveHistoryUserFragment";
    private TransparentProgressDialog pd;
    static int fromActivity;

    Activity activity;
    MyReceiver myReceiver;


//    String roleId = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_ROLE_ID);

    public static LeaveHistoryUserFragment newInstance(String userId, Context ctx, int activity) {
        mContext = ctx;
        LeaveHistoryUserFragment f = new LeaveHistoryUserFragment();
        // activityName = activityNm;
        fromActivity = activity;
        userID = userId;
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_leave_user_history, container, false);
        // v = inflater.inflate(R.layout.activity_admin_attendance_history, container, false);
        ButterKnife.inject(this, v);

        final Typeface mFont = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) v.getRootView();
        Util.setAppFont(mContainer, mFont);

        activity = getActivity();

        pd = new TransparentProgressDialog(activity, R.drawable.progressdialog, true);
        listLeaveBalance = new ArrayList<LeaveBalanceModel>();
        listLeaveHistory = new ArrayList<LeaveHistoryModel>();

        if (Util.isOnline(activity)) {
            new getLeaveBalance().execute();
        } else {
            Toast.makeText(activity, activity.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
           // String User_ID = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
            //  reload(Util.ReadFile(getActivity().getCacheDir() + "/LeaveBalance", User_ID + ".txt"));
        }


        if (fromActivity == Constant.FROM_ADMIN_DASHBOARD) {
            Log.e(TAG, "Inside if Condition !!!!!!!!!!!!!!!!");
            relativeLeaveApply.setVisibility(View.GONE);
        } else {
            Log.e(TAG, "Inside else Condition ===========");
            relativeLeaveApply.setVisibility(View.VISIBLE);
        }

        if(Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_ROLE_ID).equals("1") || Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_ROLE_ID).equals("2"))
        {
            btnUpdateBalance.setVisibility(View.VISIBLE);
        }else{
            btnUpdateBalance.setVisibility(View.GONE);
        }
        return v;

    }


    @Override
    public void onResume() {
        super.onResume();

        if (Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_RELOAD).equals("1")) {
            Util.WriteSharePrefrence(activity, Constant.SHRED_PR.KEY_RELOAD, "0");
            callAPI();
        }

        try {
            myReceiver = new MyReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(getActivity().ACTIVITY_SERVICE);
            getActivity().registerReceiver(myReceiver, intentFilter);
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    private void callAPI() {
        if (Util.isOnline(activity)) {
            new getLeaveBalance().execute();
        } else {
            Toast.makeText(activity, activity.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            // String User_ID = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
            //  reload(Util.ReadFile(getActivity().getCacheDir() + "/LeaveBalance", User_ID + ".txt"));
        }
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        try {
            getActivity().unregisterReceiver(myReceiver);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            callAPI();
        }
    }

    @OnClick(R.id.rlApplyLeave)
    @SuppressWarnings("unused")
    public void ApplyLeave(View view) {
        Intent intent1 = new Intent(activity, ApplyLeaveActivity.class);
        startActivity(intent1);
        activity.overridePendingTransition(R.anim.enter_from_left,
                R.anim.hold_bottom);
    }
    @OnClick(R.id.btn_updateBalance)
    public void UpdateBalance(View view) {
        Intent intent11 = new Intent(activity, UpdateLeaveBalanceActivity.class);
        intent11.putExtra("userID",""+userID);
        Log.e(TAG,"User id is:-  "+userID);
        startActivity(intent11);
        activity.overridePendingTransition(R.anim.enter_from_left,
                R.anim.hold_bottom);
    }

    private void reload(String result) {
        try {
            JSONObject jObj = new JSONObject(result);
            String status = jObj.optString("status");

            listLeaveBalance.clear();
            if (status.equals("Success")) {
                JSONArray jsonArray = jObj.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    modelLeaveBalance = new LeaveBalanceModel();

                   // modelLeaveBalance.setLeaveId("" + jsonObject.optInt("leaveTypeId"));
                    modelLeaveBalance.setLeaveType("" + jsonObject.optString("LeaveType"));
                    modelLeaveBalance.setNumberOfLeaves("" + jsonObject.optDouble("TotalLeaves"));
                    modelLeaveBalance.setAvailableLeaves("" + jsonObject.optDouble("LeaveBalance"));
                    modelLeaveBalance.setUsedLeaves("" + jsonObject.optInt("LeavesTaken"));

                    listLeaveBalance.add(modelLeaveBalance);
                }
                setLeaveBalance();
            } else {
                if (pd != null) {
                    pd.dismiss();
                }
                Toast.makeText(activity, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (pd != null) {
                pd.dismiss();
            }
        }
      /*  leaveBalanceAdapter = new LeaveBalanceAdapter(LeaveBalanceActivity.this, listLeaveBalance, pBar);
        listVWLeaveBalance.setAdapter(leaveBalanceAdapter);*/
    }

    private void setLeaveBalance() {
        String totalLeaves, remainingLeaves;
        for (int i = 0; i < listLeaveBalance.size(); i++) {
            if (listLeaveBalance.get(i).getLeaveType().equals("Sick Leave")) {
                totalLeaves = listLeaveBalance.get(i).getNumberOfLeaves();
                remainingLeaves = listLeaveBalance.get(i).getAvailableLeaves();
                tvSickLeave.setText("" + remainingLeaves + "/" + totalLeaves);
            } else if (listLeaveBalance.get(i).getLeaveType().equals("Optional Leave")) {
                totalLeaves = listLeaveBalance.get(i).getNumberOfLeaves();
                remainingLeaves = listLeaveBalance.get(i).getAvailableLeaves();
                tvOptionalLeave.setText("" + remainingLeaves + "/" + totalLeaves);
            } else if (listLeaveBalance.get(i).getLeaveType().equals("Paid Leave")) {
                totalLeaves = listLeaveBalance.get(i).getNumberOfLeaves();
                remainingLeaves = listLeaveBalance.get(i).getAvailableLeaves();
                tvPaidLeave.setText("" + remainingLeaves + "/" + totalLeaves);
            } else if (listLeaveBalance.get(i).getLeaveType().equals("Casual Leave")) {
                totalLeaves = listLeaveBalance.get(i).getNumberOfLeaves();
                remainingLeaves = listLeaveBalance.get(i).getAvailableLeaves();
                tvCasualLeave.setText("" + remainingLeaves + "/" + totalLeaves);
            }
        }

        if (Util.isOnline(activity)) {
            new getLeaveStatus().execute();
        } else {
            Toast.makeText(activity, activity.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            //String User_ID = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
            //reload1(Util.ReadFile(getActivity().getCacheDir() + "/LeaveHistory", User_ID + ".txt"));
        }
    }

    private void reload1(String result) {
        try {
            JSONObject jObj = new JSONObject(result);
            String status = jObj.optString("status");
            if (pd != null) {
                pd.dismiss();
            }
            listLeaveHistory.clear();
            if (status.equals("Success")) {

                //JSONArray jsonArray = jObj.getJSONArray("data");
               // String data = jObj.optString("data");

                JSONArray jsonArray = jObj.getJSONArray("data");
              //  JSONObject jsonObj =  new JSONObject(data);
              //  JSONArray jsonArray = jsonObj.getJSONArray("Leaves");
                /*String userArray = jsonObj.optString("User");
                JSONObject userObj =  new JSONObject(userArray);*/
               // String userData =



                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String userArray = jsonObject.optString("user");
                    JSONObject jUserObject = new JSONObject(userArray);

                    leaveHistoryModel = new LeaveHistoryModel();
                    leaveHistoryModel.setUserId("" + jUserObject.optInt("userId"));
                    leaveHistoryModel.setApprovalId("" + jsonObject.optInt("leaveID"));
                    leaveHistoryModel.setStartDate("" + jsonObject.optString("date"));
                    leaveHistoryModel.setLeaveDay(""+jsonObject.optString("leaveDay"));
                    //leaveHistoryModel.setEndDate("" + jsonObject.optString("endDate"));
                    leaveHistoryModel.setReason("" + jsonObject.optString("reason"));
                   leaveHistoryModel.setImageUrl("" + jUserObject.optString("picture"));
                    leaveHistoryModel.setLeaveStatus("" + jsonObject.optString("status"));
                    leaveHistoryModel.setUserName("" + jUserObject.optString("firstName") + " " + jUserObject.optString("lastName"));
                    leaveHistoryModel.setLeaveType("" + jsonObject.optString("type"));
                    leaveHistoryModel.setTransactionId(""+jsonObject.optString("transactionID"));
                    leaveHistoryModel.setFromActivity(Constant.FROM_USER_DASHBOARD);

                    listLeaveHistory.add(leaveHistoryModel);
                }
            } else {
                Toast.makeText(activity, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
            }

            if (listLeaveHistory.size() <= 0) {
                tvNoHisory.setVisibility(View.VISIBLE);
            } else {
                tvNoHisory.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (pd != null) {
                pd.dismiss();
            }
        }

        if (fromActivity == Constant.FROM_ADMIN_DASHBOARD) {
            leaveHistoryAdapteAdminr = new LeaveStatusOfUsersAdapter(activity, listLeaveHistory,true);
            listviewLeave.setAdapter(leaveHistoryAdapteAdminr);
        } else {
            leaveHistoryAdapter = new LeaveHistoryAdapter(activity, listLeaveHistory);
            listviewLeave.setAdapter(leaveHistoryAdapter);
        }

    }

    @OnClick(R.id.relativeExport)
    void createCSV() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int currentYear = calendar.get(Calendar.YEAR);
       /* if(listLeaveHistory.size()>0){

        }else{
            Toast.makeText(getActivity(),"No leaves",Toast.LENGTH_LONG).show();
        }*/
        int result = Util.createCSVFileForLeave(listLeaveHistory, "" + currentYear, activity);
        if (result == 1) {
            Toast.makeText(activity, "Your file has been created successfully", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(activity, "Some error occurred while creating file", Toast.LENGTH_LONG).show();

        }
    }

    class getLeaveBalance extends AsyncTask<Void, String, String> {
        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pd != null) {
                pd.show();
            }

        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            // params1.add(new BasicNameValuePair("MemberID", "" + "" + Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID)));

            //if (activityName.equals("LeaveStatusAdminActivity")) {
            if (fromActivity == Constant.FROM_ADMIN_DASHBOARD) {
                params1.add(new BasicNameValuePair("MemberID", "" + userID));

            } else {
                params1.add(new BasicNameValuePair("MemberID", "" + "" + Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_USERID)));

            }
            Log.e("params1", ">>" + params1);

            response = Util.makeServiceCall(Constant.URL
                    + "getLeaveBalance", 1, params1, activity);
            Log.e(TAG, "" + response);
            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            String User_ID = Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_USERID);
            // Util.WriteFile(getActivity().getCacheDir() + "/LeaveBalance", User_ID + ".txt", result);
            reload(result);

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
            // if (activityName.equals("LeaveStatusAdminActivity")) {
            if (fromActivity == Constant.FROM_ADMIN_DASHBOARD) {
                params1.add(new BasicNameValuePair("MemberID", "" + userID));

            } else {
                params1.add(new BasicNameValuePair("MemberID", "" + "" + Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_USERID)));

            }

            Log.e("params1", ">>" + params1);

            response = Util.makeServiceCall(Constant.URL
                    + "getLeaveStatus", 1, params1, activity);

            Log.e(TAG, "" + response);
            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
           // String User_ID = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
            //Util.WriteFile(getActivity().getCacheDir()+"/LeaveHistory", User_ID + ".txt", result);
            reload1(result);

        }
    }

}
