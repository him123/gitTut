package com.bigbang.superteam.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.RelativeLayout;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by USER 7 on 11/3/2015.
 */
public class LeaveHistoryAdminFragment extends Fragment {

    public static Context mContext;
    @InjectView(R.id.list_leave_history)
    ListView listVWLeaveStatus;
    @InjectView(R.id.tvNoHistory)
    TextView tvNoHistory;
    @InjectView(R.id.tvDate1)
    TextView tvDate;
    @InjectView(R.id.tvDate2)
    TextView tvYear;
    @InjectView(R.id.relativeRightArrow)
    RelativeLayout relativeNext;

    LeaveStatusOfUsersAdapter leaveHistoryAdapter;
    public static ArrayList<LeaveHistoryModel> listLeaveHistory;
    LeaveHistoryModel leaveHistoryModel;

    SQLiteHelper helper;
    String TAG = "LeaveHistoryFragment ";
    public static SQLiteDatabase db = null;


    String[] monthName;
    boolean isAPICalling = false;

    Calendar calendar, startCalendar;
    int curMonth, curYear, curDate;

    private TransparentProgressDialog pd;

    SimpleDateFormat sdf1 = new SimpleDateFormat(" MMM" + ",");
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");

    Activity activity;
    MyReceiver myReceiver;

    public static LeaveHistoryAdminFragment newInstance(String string, Context ctx) {
        mContext = ctx;
        LeaveHistoryAdminFragment f = new LeaveHistoryAdminFragment();
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_admin_leavehistory, container, false);
        // v = inflater.inflate(R.layout.activity_admin_attendance_history, container, false);
        ButterKnife.inject(this, v);

        activity = getActivity();

        final Typeface mFont = Typeface.createFromAsset(activity.getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) v.getRootView();
        Util.setAppFont(mContainer, mFont);

        pd = new TransparentProgressDialog(activity, R.drawable.progressdialog,false);


        Init();

        return v;

    }

    private void Init()
    {

        listLeaveHistory = new ArrayList<LeaveHistoryModel>();
        monthName = getResources().getStringArray(R.array.month_array);

        startCalendar = Calendar.getInstance();
        calendar = Calendar.getInstance();
        curMonth = calendar.get(calendar.MONTH);
        curYear = calendar.get(calendar.YEAR);
        curDate = calendar.get(calendar.DAY_OF_MONTH);


        startCalendar.set(Calendar.YEAR, curYear);
        startCalendar.set(Calendar.MONTH, curMonth);
        startCalendar.set(Calendar.DAY_OF_MONTH, curDate);

        /*if(startCalendar.before(calendar)){
            relativeNext.setVisibility(View.VISIBLE);
        }else{
            relativeNext.setVisibility(View.INVISIBLE);
        }*/

        setDisplayData();
        int currentYear = calendar.get(Calendar.YEAR);


    }


    public void setDisplayData() {
        int month, year, day;
        /*SimpleDateFormat sdf1 = new SimpleDateFormat(" MMM" + ",");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");*/
        String sdate = sdf1.format(startCalendar.getTime());
        tvDate.setText(sdate);
        tvYear.setText("" + sdf2.format(startCalendar.getTime()));

        if (Util.isOnline(activity.getApplicationContext())) {
            if (!isAPICalling) {
                new getLeaveStatus().execute();
            }
        }else{
            month = startCalendar.get(calendar.MONTH);
            year = startCalendar.get(calendar.YEAR);
            String date = year + "/" + (month + 1);

            Toast.makeText(activity, activity.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
           // reload(Util.ReadFile(activity.getCacheDir()+"/LeaveHistory",  Constant.LeaveFile +"_"+date+ ".txt"));
        }
    }

    private DatePickerDialog.OnDateSetListener myfromDateListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

            startCalendar.set(Calendar.YEAR, year);
            startCalendar.set(Calendar.MONTH, month);
            startCalendar.set(Calendar.DAY_OF_MONTH, day);

           /* if(startCalendar.before(calendar)){
                relativeNext.setVisibility(View.VISIBLE);
            }else{
                relativeNext.setVisibility(View.INVISIBLE);
            }
*/
            setDisplayData();

        }
    };


    class getLeaveStatus extends AsyncTask<Void, String, String> {
        String response;
        int month,year;

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

            month = startCalendar.get(calendar.MONTH);
            year = startCalendar.get(calendar.YEAR);
            String date = year + "/" + (month + 1);
            //String date = (String) spinnerYear.getSelectedItem()+ "/" + (spinnerMonth.getSelectedItemPosition() + 1);
            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
          //  params1.add(new BasicNameValuePair("AdminID", "" + ""+ Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_USERID)));
            params1.add(new BasicNameValuePair("Month", ""+(month+1)));
            params1.add(new BasicNameValuePair("Year", ""+year));


            Log.e("params1", ">>" + params1);

            response = Util.makeServiceCall(Constant.URL
                    + "getAllUserLeaves",1,params1,activity);

            Log.e(TAG,""+response);
            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
           // String date = (String) spinnerYear.getSelectedItem()+ "_" + (spinnerMonth.getSelectedItemPosition() + 1);
            String date = year + "/" + (month + 1);
//            Util.WriteFile(activity.getCacheDir()+"/LeaveHistory", Constant.LeaveFile +"_"+date+".txt", result);
            reload(result);
        }

    }



    @Override
    public void onResume() {
        super.onResume();

        callAPI();
        try {
            myReceiver = new MyReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(getActivity().ACTIVITY_SERVICE);
            getActivity().registerReceiver(myReceiver, intentFilter);
        } catch (Exception e) {
            // TODO: handle exception
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

    public void callAPI(){
        if(Util.isOnline(activity)){
            new getLeaveStatus().execute();
        }else{
            Toast.makeText(activity, activity.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            // String date = (String) spinnerYear.getSelectedItem()+ "_" + (spinnerMonth.getSelectedItemPosition() + 1);
            int month = startCalendar.get(calendar.MONTH);
            int year = startCalendar.get(calendar.YEAR);
            String date = year + "/" + (month + 1);
            // reload(Util.ReadFile(activity.getCacheDir()+"/LeaveHistory",  Constant.LeaveFile +"_"+date+ ".txt"));
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

                    String userArray = jsonObject.optString("user");
                    JSONObject jUserObject = new JSONObject(userArray);

                    leaveHistoryModel = new LeaveHistoryModel();
                    leaveHistoryModel.setUserId("" + jUserObject.optInt("userId"));
                    leaveHistoryModel.setApprovalId("" + jsonObject.optInt("leaveID"));
                    leaveHistoryModel.setStartDate("" + jsonObject.optString("date"));
                    leaveHistoryModel.setLeaveDay(""+jsonObject.optString("leaveDay"));
                    //leaveHistoryModel.setEndDate("" + jsonObject.optString("endDate"));
                    leaveHistoryModel.setReason(""+jsonObject.optString("reason"));
                    leaveHistoryModel.setImageUrl(""+jUserObject.optString("picture"));
                    leaveHistoryModel.setLeaveStatus(""+jsonObject.optString("status"));
                    leaveHistoryModel.setUserName(""+jUserObject.optString("firstName")+" "+jUserObject.optString("lastName"));
                    leaveHistoryModel.setLeaveType(""+jsonObject.optString("type"));
                    leaveHistoryModel.setTransactionId(""+jsonObject.optString("transactionID"));
                    leaveHistoryModel.setFromActivity(Constant.FROM_ADMIN_DASHBOARD);

                    listLeaveHistory.add(leaveHistoryModel);
                }
            } else {
                Toast.makeText(activity, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
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
        leaveHistoryAdapter = new LeaveStatusOfUsersAdapter(activity, listLeaveHistory,true);
        listVWLeaveStatus.setAdapter(leaveHistoryAdapter);
    }


    @OnClick(R.id.relativeDate)
    void openDatePicker() {
        Log.e(TAG,"INside relativeDate click ********");
        DatePickerDialog d = new DatePickerDialog(activity,
                DatePickerDialog.THEME_HOLO_LIGHT, myfromDateListener, calendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DATE));
        ((ViewGroup) d.getDatePicker()).findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
        d.show();
    }

    @OnClick(R.id.relativeLeftArrow)
    void getPreviousDate() {

        startCalendar.add(startCalendar.MONTH, -1);

        /*if(startCalendar.before(calendar)){
            relativeNext.setVisibility(View.VISIBLE);
            Log.e(TAG,"@@@@@@@@@***************!!!!!!!!!!!!!");
        }else{
            relativeNext.setVisibility(View.INVISIBLE);
            Log.e(TAG,"======================!!!!!!!!!!!!!!!!!!!!************");
        }*/
        String sdate = sdf1.format(startCalendar.getTime());
        tvDate.setText(sdate);
        tvYear.setText(""+sdf2.format(startCalendar.getTime()));

        setDisplayData();
        //formattedDate = df.format(startCalendar.getTime());

        Log.v("PREVIOUS DATE : ", sdate);

    }

    @OnClick(R.id.relativeRightArrow)
    void getNextDate() {

        startCalendar.add(startCalendar.MONTH, 1);
       /* if(startCalendar.before(calendar)){
            relativeNext.setVisibility(View.VISIBLE);
        }else{
            relativeNext.setVisibility(View.INVISIBLE);
        }*/
        String sdate = sdf1.format(startCalendar.getTime());
        tvDate.setText(sdate);
        tvYear.setText(""+sdf2.format(startCalendar.getTime()));

        setDisplayData();
        //formattedDate = df.format(startCalendar.getTime());

        Log.v("PREVIOUS DATE : ", sdate);


    }
}
