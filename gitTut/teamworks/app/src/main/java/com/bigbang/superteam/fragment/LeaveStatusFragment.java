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
import com.bigbang.superteam.adapter.LeaveStatusOfUsersAdapter;
import com.bigbang.superteam.dataObjs.LeaveHistoryModel;
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
 * Created by USER 7 on 7/10/2015.
 */
public class LeaveStatusFragment extends Fragment {

    public static Context mContext;
    @InjectView(R.id.listvwLeaveStatus)
    ListView listVWLeaveStatus;
    @InjectView(R.id.progressBar)
    ProgressBar pBar;
    @InjectView(R.id.linearTop)
    LinearLayout linearTop;
    LeaveStatusOfUsersAdapter leaveHistoryAdapter;
    public static ArrayList<LeaveHistoryModel> listLeaveHistory;
    LeaveHistoryModel leaveHistoryModel;
    public static String uId;

    String TAG = "LeaveStatusActivity Activity";

    private TransparentProgressDialog pd;

    public static LeaveStatusFragment newInstance(String string, Context ctx,String userID) {
        mContext = ctx;
        uId =  userID;
        LeaveStatusFragment f = new LeaveStatusFragment();
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //   String message = getArguments().getString(EXTRA_MESSAGE);
        View v;
        v = new View(mContext);
        v = inflater.inflate(R.layout.activity_leavestatus, container, false);
        ButterKnife.inject(this, v);
        pd = new TransparentProgressDialog(getActivity(), R.drawable.progressdialog,false);
        Init();

        return v;

    }
    private void Init()
    {
        linearTop.setVisibility(View.GONE);
        listLeaveHistory = new ArrayList<LeaveHistoryModel>();

        if(Util.isOnline(getActivity())){
            new getLeaveStatus().execute();
        }else{
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            reload(Util.ReadFile(getActivity().getCacheDir()+"/LeaveHistory", uId + ".txt"));
        }


    }
    @OnClick(R.id.btnExportExcel)
    void createCSV() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int currentYear = calendar.get(Calendar.YEAR);
        int result =Util.createCSVFileForLeave(listLeaveHistory,""+currentYear,getActivity());
        if(result==1)
        {
            Toast.makeText(getActivity(),"Your file has been created successfully",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getActivity(),"Some error occurred while creating file",Toast.LENGTH_LONG).show();

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
    class getLeaveStatus extends AsyncTask<Void, String, String> {
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
            params1.add(new BasicNameValuePair("MemberID", ""+uId));

            Log.e("params1", ">>" + params1);

            response = Util.makeServiceCall(Constant.URL
                    + "leaveStatus",1,params1,getActivity());

            Log.e(TAG,""+response);
            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            Util.WriteFile(getActivity().getCacheDir()+"/LeaveHistory", uId + ".txt", result);
            reload(result);

        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if(Util.isOnline(getActivity())){
            new getLeaveStatus().execute();
        }else{
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            reload(Util.ReadFile(getActivity().getCacheDir()+"/LeaveHistory", uId + ".txt"));
        }

    }

    private  void reload(String result){
        try {
            JSONObject jObj = new JSONObject(result);
            String status = jObj.optString("status");
            if(pd!=null){
                pd.dismiss();
            }
            //pd.dismiss();
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
                    //leaveHistoryModel.setEndDate("" + jsonObject.optString("endDate"));
                    leaveHistoryModel.setReason(""+jsonObject.optString("reason"));
                    leaveHistoryModel.setImageUrl(""+jsonObject.optString("picture"));
                    leaveHistoryModel.setLeaveStatus(""+jsonObject.optString("status"));
                    leaveHistoryModel.setUserName(""+jsonObject.optString("firstName")+" "+jsonObject.optString("lastName"));
                    leaveHistoryModel.setLeaveType(""+jsonObject.optString("type"));
                    //leaveHistoryModel.setActivityName("LeaveStatusFragment");
                    leaveHistoryModel.setTransactionId("TransactionID");

                    listLeaveHistory.add(leaveHistoryModel);
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
        leaveHistoryAdapter = new LeaveStatusOfUsersAdapter(getActivity(), listLeaveHistory,false);
        listVWLeaveStatus.setAdapter(leaveHistoryAdapter);
    }
}
