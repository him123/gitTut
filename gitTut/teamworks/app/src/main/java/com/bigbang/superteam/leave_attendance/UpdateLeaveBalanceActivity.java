package com.bigbang.superteam.leave_attendance;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.dataObjs.LeaveBalanceModel;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by USER 7 on 1/22/2016.
 */
public class UpdateLeaveBalanceActivity extends BaseActivity {




    @InjectView(R.id.edSickLeave)
    EditText edSickLeave;
    @InjectView(R.id.edCasualLeave)
    EditText edCasualLeave;
    @InjectView(R.id.edPaidLeave)
    EditText edPaidLeave;
    @InjectView(R.id.edOptionalLeave)
    EditText edOptionalLeave;
    @InjectView(R.id.tvSickLeave)
    TextView tvSickLeave;
    @InjectView(R.id.tvCasualLeave)
    TextView tvCasualLeave;
    @InjectView(R.id.tvPaidLeave)
    TextView tvPaidLeave;
    @InjectView(R.id.tvOptionalLeave)
    TextView tvOptionalLeave;

    LeaveBalanceModel modelLeaveBalance;
    ArrayList<LeaveBalanceModel> listLeaveBalance;
    String TAG = "UpdateLeaveBalanceActivity";

    private TransparentProgressDialog pd;
Intent i;


    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_leave_balance);
        ButterKnife.inject(this);

        pd = new TransparentProgressDialog(UpdateLeaveBalanceActivity.this, R.drawable.progressdialog,false);
        i = getIntent();
        userID = i.getStringExtra("userID");
       /* if (Util.isOnline(UpdateLeaveBalanceActivity.this)) {
            new getLeaveTypes().execute();
        }*/
        //  getLeavesFromDatabase();
        Init();

    }


    @OnClick(R.id.btn_updateBalance)
    void updateBalance() {



       if (Util.isOnline(UpdateLeaveBalanceActivity.this)) {
            new updateLeaveBalance().execute();
        } else {
            Toast.makeText(UpdateLeaveBalanceActivity.this, UpdateLeaveBalanceActivity.this.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
        }
    }



    @OnClick(R.id.rlBack)
    void backPressed121() {
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }


    private void Init() {


        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        listLeaveBalance = new ArrayList<LeaveBalanceModel>();

        if (Util.isOnline(UpdateLeaveBalanceActivity.this)) {
            new getLeaveBalance().execute();
        } else {
            Toast.makeText(UpdateLeaveBalanceActivity.this, UpdateLeaveBalanceActivity.this.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            // String User_ID = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
            //  reload(Util.ReadFile(getActivity().getCacheDir() + "/LeaveBalance", User_ID + ".txt"));
        }


        edSickLeave.addTextChangedListener(new TextWatcher() {
            String oldText="";
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText=getText(edSickLeave);
                Log.e("newText",">>"+newText);
                if (newText.contains(".")) {
                    if(newText.length()>1){
                        String[] temp=newText.split("\\.");
                        Log.e("temp",">>"+temp[0]);
                        if(temp.length>1) {
                            if (!(temp[1].equals("5") || temp[1].equals("0") ) || temp[0].length()>2) {
                                edSickLeave.setText(oldText);
                                edSickLeave.setSelection(getText(edSickLeave).length());
                                //toast(getResources().getString(R.string.mobile_not_zero));
                            }
                        }
                    }else{
                        edSickLeave.setText(oldText);
                        edSickLeave.setSelection(getText(edSickLeave).length());
                    }

                }
                else{
                    if(newText.length()>2){
                        edSickLeave.setText(oldText);
                        edSickLeave.setSelection(getText(edSickLeave).length());
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldText=getText(edSickLeave);
                Log.e("oldText",">>"+oldText);
            }

            public void afterTextChanged(Editable s) {
            }
        });


        edCasualLeave.addTextChangedListener(new TextWatcher() {
            String oldText="";
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText=getText(edCasualLeave);
                Log.e("newText",">>"+newText);
                if (newText.contains(".")) {
                    if(newText.length()>1){
                    String[] temp=newText.split("\\.");
                    Log.e("temp",">>"+temp[0]);
                    if(temp.length>1) {
                        if (!(temp[1].equals("5") || temp[1].equals("0") ) || temp[0].length()>2) {
                            edCasualLeave.setText(oldText);
                            edCasualLeave.setSelection(getText(edCasualLeave).length());
                            //toast(getResources().getString(R.string.mobile_not_zero));
                        }
                    }
                    }else{
                        edCasualLeave.setText(oldText);
                        edCasualLeave.setSelection(getText(edCasualLeave).length());
                    }
                }
                else{
                    if(newText.length()>2){
                        edCasualLeave.setText(oldText);
                        edCasualLeave.setSelection(getText(edCasualLeave).length());
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldText=getText(edCasualLeave);
                Log.e("oldText",">>"+oldText);
            }

            public void afterTextChanged(Editable s) {
            }
        });


        edPaidLeave.addTextChangedListener(new TextWatcher() {
            String oldText="";
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText=getText(edPaidLeave);
                Log.e("newText",">>"+newText);
                if (newText.contains(".")) {
                    if(newText.length()>1){
                        String[] temp=newText.split("\\.");
                        Log.e("temp",">>"+temp[0]);
                        if(temp.length>1) {
                            if (!(temp[1].equals("5") || temp[1].equals("0") ) || temp[0].length()>2) {
                                edPaidLeave.setText(oldText);
                                edPaidLeave.setSelection(getText(edPaidLeave).length());
                                //toast(getResources().getString(R.string.mobile_not_zero));
                            }
                        }
                    }
                    else{
                        edPaidLeave.setText(oldText);
                        edPaidLeave.setSelection(getText(edPaidLeave).length());
                    }

                }
                else{
                    if(newText.length()>2){
                        edPaidLeave.setText(oldText);
                        edPaidLeave.setSelection(getText(edPaidLeave).length());
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldText=getText(edPaidLeave);
                Log.e("oldText",">>"+oldText);
            }

            public void afterTextChanged(Editable s) {
            }
        });


        edOptionalLeave.addTextChangedListener(new TextWatcher() {
            String oldText="";
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText=getText(edOptionalLeave);
                Log.e("newText",">>"+newText);
                if (newText.contains(".")) {
                    if(newText.length()>1){
                        String[] temp=newText.split("\\.");
                        Log.e("temp",">>"+temp[0]);
                        if(temp.length>1) {
                            if (!(temp[1].equals("5") || temp[1].equals("0") ) || temp[0].length()>2) {
                                edOptionalLeave.setText(oldText);
                                edOptionalLeave.setSelection(getText(edOptionalLeave).length());
                                //toast(getResources().getString(R.string.mobile_not_zero));
                            }
                        }
                    }
                    else{
                        edOptionalLeave.setText(oldText);
                        edOptionalLeave.setSelection(getText(edOptionalLeave).length());
                    }

                }
                else{
                    if(newText.length()>2){
                        edOptionalLeave.setText(oldText);
                        edOptionalLeave.setSelection(getText(edOptionalLeave).length());
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                oldText=getText(edOptionalLeave);
                Log.e("oldText",">>"+oldText);
            }

            public void afterTextChanged(Editable s) {
            }
        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
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
                params1.add(new BasicNameValuePair("MemberID", "" + userID));

            Log.e("params1", ">>" + params1);

            response = Util.makeServiceCall(Constant.URL
                    + "getLeaveBalance", 1, params1, UpdateLeaveBalanceActivity.this);
            Log.e(TAG, "" + response);
            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (pd != null) {
                pd.dismiss();
            }
            String User_ID = Util.ReadSharePrefrence(UpdateLeaveBalanceActivity.this, Constant.SHRED_PR.KEY_USERID);
            // Util.WriteFile(getActivity().getCacheDir() + "/LeaveBalance", User_ID + ".txt", result);
            reload(result);

        }

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
                Toast.makeText(UpdateLeaveBalanceActivity.this, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
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
                edSickLeave.setText(""+totalLeaves);
                tvSickLeave.setText("" + remainingLeaves + "/" + totalLeaves);
            } else if (listLeaveBalance.get(i).getLeaveType().equals("Optional Leave")) {
                totalLeaves = listLeaveBalance.get(i).getNumberOfLeaves();
                remainingLeaves = listLeaveBalance.get(i).getAvailableLeaves();
                edOptionalLeave.setText("" +totalLeaves);
                tvOptionalLeave.setText("" + remainingLeaves + "/" + totalLeaves);
            } else if (listLeaveBalance.get(i).getLeaveType().equals("Paid Leave")) {
                totalLeaves = listLeaveBalance.get(i).getNumberOfLeaves();
                remainingLeaves = listLeaveBalance.get(i).getAvailableLeaves();
                edPaidLeave.setText(""+totalLeaves);
                tvPaidLeave.setText("" + remainingLeaves + "/" + totalLeaves);
            } else if (listLeaveBalance.get(i).getLeaveType().equals("Casual Leave")) {
                totalLeaves = listLeaveBalance.get(i).getNumberOfLeaves();
                remainingLeaves = listLeaveBalance.get(i).getAvailableLeaves();
                edCasualLeave.setText("" +totalLeaves);
                tvCasualLeave.setText("" + remainingLeaves + "/" + totalLeaves);
            }
        }

    }

    class updateLeaveBalance extends AsyncTask<Void, String, String> {
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
            JSONArray jsonArray = new JSONArray();;
              JSONObject jsonObject = new JSONObject();
        try {
            if(edSickLeave.getText().length()<=0){
                jsonObject.put("Sick Leave","0.0");
            }else{
                jsonObject.put("Sick Leave",""+edSickLeave.getText());
            }
            if(edOptionalLeave.getText().length()<=0){
                jsonObject.put("Optional Leave","0.0");
            }else{
                jsonObject.put("Optional Leave",""+edOptionalLeave.getText());
            }

            if(edPaidLeave.getText().length()<=0){
                jsonObject.put("Paid Leave","0.0");
            }else{
                jsonObject.put("Paid Leave",""+edPaidLeave.getText());
            }

            if(edCasualLeave.getText().length()<=0){
                jsonObject.put("Casual Leave","0.0");
            }else{
                jsonObject.put("Casual Leave",""+edCasualLeave.getText());
            }
            Log.e(TAG,""+jsonArray.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
            params1.add(new BasicNameValuePair("MemberID", "" + userID));
            params1.add(new BasicNameValuePair("LeaveDetails", "" + jsonObject.toString()));

            Log.e("params1", ">>" + params1);
            response = Util.makeServiceCall(Constant.URL
                    + "updateUserLeaves", 2, params1, UpdateLeaveBalanceActivity.this);
            Log.e(TAG, "" + response);
            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            if (pd != null) {
                pd.dismiss();
            }
            try{
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");

                Toast.makeText(UpdateLeaveBalanceActivity.this, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
                if (status.equals("Success")) {
                    write(Constant.SHRED_PR.KEY_RELOAD, "1");
                    finish();
                    overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
                }
            }catch (Exception e){
                e.printStackTrace();
            }


        }

    }

}
