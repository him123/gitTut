package com.bigbang.superteam.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
 * Created by USER 7 on 7/10/2015.
 */
public class LeaveBalanceFragment  extends Fragment {

    public static Context mContext;

    @InjectView(R.id.listvwLeaveBalance)
    ListView listVWLeaveBalance;
    @InjectView(R.id.progressBar)
    ProgressBar pBar;
    @InjectView(R.id.linearTopLayout)
    LinearLayout linearTop;

    String TAG = "LeaveBalanceActivity";
    LeaveBalanceModel modelLeaveBalance;
    ArrayList<LeaveBalanceModel> listLeaveBalance;
    LeaveBalanceAdapter leaveBalanceAdapter;

    public static String uId;

    private TransparentProgressDialog pd;

    public static LeaveBalanceFragment newInstance(String string, Context ctx,String userId) {
        mContext = ctx;
        uId = userId;
        LeaveBalanceFragment f = new LeaveBalanceFragment();
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //   String message = getArguments().getString(EXTRA_MESSAGE);
        View v;
        v = new View(mContext);
        v = inflater.inflate(R.layout.activity_leave_balance, container, false);
        ButterKnife.inject(this, v);

        pd = new TransparentProgressDialog(getActivity(), R.drawable.progressdialog,false);
        listLeaveBalance = new ArrayList<LeaveBalanceModel>();
           linearTop.setVisibility(View.GONE);
        if(Util.isOnline(getActivity())){
            new getLeaveBalance().execute();
        }else{
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            reload(Util.ReadFile(getActivity().getCacheDir()+"/LeaveBalance", uId + ".txt"));
        }


        return v;

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
            params1.add(new BasicNameValuePair("MemberID",""+uId));
            Log.e("params1", ">>" + params1);

            response = Util.makeServiceCall(Constant.URL
                    + "leaveBalance",1,params1,getActivity());
            Log.e(TAG,""+response);
            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
           // String User_ID = Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID);
            Util.WriteFile(getActivity().getCacheDir()+"/LeaveBalance", uId + ".txt", result);
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
                for(int i=0;i<jsonArray.length();i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    modelLeaveBalance = new LeaveBalanceModel();

                    modelLeaveBalance.setLeaveId("" + jsonObject.optInt("leaveId"));
                    modelLeaveBalance.setLeaveType("" + jsonObject.optString("type"));
                    modelLeaveBalance.setNumberOfLeaves("" + jsonObject.optInt("totalBalance"));
                    modelLeaveBalance.setAvailableLeaves(""+jsonObject.optInt("balance"));
                    modelLeaveBalance.setUsedLeaves(""+jsonObject.optInt("takenBalance"));

                    listLeaveBalance.add(modelLeaveBalance);
                }

            } else {
                Toast.makeText(getActivity(), "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(pd!=null){
                pd.dismiss();
            }
        }
        leaveBalanceAdapter = new LeaveBalanceAdapter(getActivity(), listLeaveBalance,pBar);
        listVWLeaveBalance.setAdapter(leaveBalanceAdapter);
    }

}

