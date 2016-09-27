package com.bigbang.superteam.fragment;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.LeaveStatusOfUsersAdapter;
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
import butterknife.OnItemSelected;

/**
 * Created by USER 7 on 7/8/2015.
 */
public class LeaveHistoryFragment extends Fragment {

    @InjectView(R.id.listvwLeaveStatus)
    ListView listVWLeaveStatus;
    @InjectView(R.id.progressBar)
    ProgressBar pBar;
    @InjectView(R.id.spinnerMonth)
    Spinner spinnerMonth;
    @InjectView(R.id.spinnerYear)
    Spinner spinnerYear;
    @InjectView(R.id.tvNoHistory)
    TextView tvNoHistory;


    LeaveStatusOfUsersAdapter leaveHistoryAdapter;
    public static ArrayList<LeaveHistoryModel> listLeaveHistory;
    LeaveHistoryModel leaveHistoryModel;

    SQLiteHelper helper;
    String TAG = "LeaveHistoryFragment ";
    public static SQLiteDatabase db = null;

    ArrayList<String> monthArray;
    ArrayList<String> yearArray;
    String[] monthName;
    boolean isAPICalling = false;

    private TransparentProgressDialog pd;

    public static Context mContext;
    public static LeaveHistoryFragment newInstance(String string, Context ctx) {
        mContext = ctx;
        LeaveHistoryFragment f = new LeaveHistoryFragment();
        return f;
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //   String message = getArguments().getString(EXTRA_MESSAGE);
        View v;
        v = new View(mContext);
        v = inflater.inflate(R.layout.activity_leave_history_admin, container, false);
        ButterKnife.inject(this, v);
        pd = new TransparentProgressDialog(getActivity(), R.drawable.progressdialog,false);
        monthArray = new ArrayList<String>();
        yearArray = new ArrayList<String>();
        calculateSpinnerDates();
        Init();
        return v;
    }
    private void Init()
    {


        listLeaveHistory = new ArrayList<LeaveHistoryModel>();
        monthName = getResources().getStringArray(R.array.month_array);
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        spinnerMonth.setSelection(calendar.get(Calendar.MONTH));
        int currentYear = calendar.get(Calendar.YEAR);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, monthArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);

        ArrayAdapter<String> adapterYear = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, yearArray);
        adapterYear.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(adapterYear);
        spinnerMonth.setSelection(calendar.get(calendar.MONTH));
        String[] years = getResources().getStringArray(R.array.year_array);
        for (int i = 0; i < years.length; i++) {
            if (years[i].equals("" + currentYear)) {
                spinnerYear.setSelection(i);
            }
        }
        //fetchDataFromDB();
        //new getLeaveStatus().execute();
    }


    class getLeaveStatus extends AsyncTask<Void, String, String> {
        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(pd!=null){
                pd.show();
            }
            isAPICalling = true;

        }
        @Override
        protected String doInBackground(Void... params) {

            String date = (String) spinnerYear.getSelectedItem()+ "/" + (spinnerMonth.getSelectedItemPosition() + 1);
            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            params1.add(new BasicNameValuePair("AdminID", "" + ""+ Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID)));
            params1.add(new BasicNameValuePair("Date", ""+date));

            Log.e("params1", ">>" + params1);

            response = Util.makeServiceCall(Constant.URL
                    + "getAllUserLeaveHistory",1,params1,getActivity());

            Log.e(TAG,""+response);
            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            String date = (String) spinnerYear.getSelectedItem()+ "_" + (spinnerMonth.getSelectedItemPosition() + 1);
            Util.WriteFile(getActivity().getCacheDir()+"/LeaveHistory", Constant.LeaveFile +"_"+date+".txt", result);
            reload(result);
        }

    }


    private void calculateSpinnerDates() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int curMonth = calendar.get(calendar.MONTH);
        int curYear = calendar.get(calendar.YEAR);
        int curDate = calendar.get(calendar.DAY_OF_MONTH);
        int[] years1 = getResources().getIntArray(R.array.year_array_int);
        monthName = getResources().getStringArray(R.array.month_array);

        for (int i = 0; i < 12; i++) {
           /* if ((curMonth) >= i) {

                // monthArray.add("" + i);
                monthArray.add("" + monthName[i]);

            } else {
                break;
            }*/

            monthArray.add("" + monthName[i]);
        }
        for (int j = 1; j <= years1.length; j++) {
            if (curYear >= (years1[j])) {
                yearArray.add("" + years1[j]);
            } else {
                break;
            }
        }

    }
    @OnItemSelected(R.id.spinnerMonth)
    void onItemSelected(int position) {
        // TODO ...
        if (Util.isOnline(getActivity().getApplicationContext())) {
            if (!isAPICalling) {
                new getLeaveStatus().execute();
            }
        }else{
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            String date = (String) spinnerYear.getSelectedItem()+ "_" + (spinnerMonth.getSelectedItemPosition() + 1);
            reload(Util.ReadFile(getActivity().getCacheDir()+"/LeaveHistory",  Constant.LeaveFile +"_"+date+ ".txt"));
        }
    }
    @OnItemSelected(R.id.spinnerYear)
    void onItemSelectedYear(int position) {
        // TODO ...
        if (!isAPICalling) {
            setSpinnerMonth();
            if (Util.isOnline(getActivity().getApplicationContext())) {
                new getLeaveStatus().execute();
            }else{
                //Toast.makeText(getActivity(),R.string.network_error,Toast.LENGTH_LONG).show();
                String date = (String) spinnerYear.getSelectedItem()+ "_" + (spinnerMonth.getSelectedItemPosition() + 1);
                reload(Util.ReadFile(getActivity().getCacheDir()+"/LeaveHistory",  Constant.LeaveFile +"_"+date+ ".txt"));
            }
        }
    }

   /* @OnClick(R.id.btnExportExcel)
    void createCSV() {
        int result =Util.createCSVFileForLeave(listLeaveHistory);
        if(result==1)
        {
            Toast.makeText(getActivity(),"Your file has been created successfully",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getActivity(),"Some error occurred while creating file",Toast.LENGTH_LONG).show();

        }
    }*/

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


    private void setSpinnerMonth() {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int curYear = calendar.get(calendar.YEAR);
        int curMonth = calendar.get(calendar.MONTH);

        if (curYear > Integer.parseInt("" + spinnerYear.getSelectedItem())) {
            monthArray.clear();
            for (int i = 0; i < 12; i++) {
                // monthArray.add("" + i);
                monthArray.add("" + monthName[i]);
            }

        } else if (curYear == Integer.parseInt("" + spinnerYear.getSelectedItem())) {
            monthArray.clear();
            for (int i = 0; i < 12; i++) {
                if ((curMonth) >= i) {
                    //monthArray.add("" + i);
                    monthArray.add("" + monthName[i]);
                } else {
                    break;
                }
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, monthArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);
        spinnerMonth.setSelection(calendar.get(calendar.MONTH));
    }

    @Override
    public void onResume() {
        super.onResume();
        if(Util.isOnline(getActivity())){
            new getLeaveStatus().execute();
        }else{
            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            String date = (String) spinnerYear.getSelectedItem()+ "_" + (spinnerMonth.getSelectedItemPosition() + 1);
            reload(Util.ReadFile(getActivity().getCacheDir()+"/LeaveHistory",  Constant.LeaveFile +"_"+date+ ".txt"));
        }

    }

    private void reload(String result) {

        try {
            JSONObject jObj = new JSONObject(result);
            String status = jObj.optString("status");
            if(pd!=null){
                pd.dismiss();
            }
            isAPICalling = false;
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
                 //   leaveHistoryModel.setActivityName("LeaveHistoryFragment");

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
        if (listLeaveHistory.size() <= 0) {
            tvNoHistory.setVisibility(View.VISIBLE);
        } else {
            tvNoHistory.setVisibility(View.GONE);
        }
        leaveHistoryAdapter = new LeaveStatusOfUsersAdapter(getActivity(), listLeaveHistory,false);
        listVWLeaveStatus.setAdapter(leaveHistoryAdapter);
    }
}
