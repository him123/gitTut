package com.bigbang.superteam.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
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
import com.bigbang.superteam.adapter.AttendanceHistoryAdminAdapterNew;
import com.bigbang.superteam.dataObjs.AttendanceHistoryModel;
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
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by USER 7 on 11/3/2015.
 */
public class AttendanceHistoryAdminFragment extends Fragment {

    public static Context mContext;

    /* @InjectView(R.id.spinnerDay)
     Spinner spinnerDay;
     @InjectView(R.id.spinnerMonth)
     Spinner spinnerMonth;
     @InjectView(R.id.spinnerYear)
     Spinner spinnerYear;*/
    @InjectView(R.id.list_attendance_history)
    ListView lvAttendanceHis;
    @InjectView(R.id.tvNoHistory)
    TextView tvNoHistory;
    @InjectView(R.id.tvDate1)
    TextView tvDate;
    @InjectView(R.id.tvDate2)
    TextView tvYear;
    @InjectView(R.id.relativeRightArrow)
    RelativeLayout relativeNext;

    Activity activity;

    AttendanceHistoryAdminAdapterNew atndHistoryAdapter;
    public static ArrayList<AttendanceHistoryModel> listAttendHistory;
    AttendanceHistoryModel attendanceHistoryModel;

    SQLiteHelper helper;
    String TAG = "AttendanceList Activity ";
    public static SQLiteDatabase db = null;
    boolean isAPICalling = false;
    String[] monthName;

    private TransparentProgressDialog pd;

    public static final int START_DATE = 106;

    Calendar calendar, startCalendar;
    int curMonth, curYear, curDate;

    SimpleDateFormat sdf1 = new SimpleDateFormat(" d" + " MMM" + ",");
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");


    public static AttendanceHistoryAdminFragment newInstance(String string, Context ctx) {
        mContext = ctx;
        AttendanceHistoryAdminFragment f = new AttendanceHistoryAdminFragment();
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_admin_attendance_history, container, false);
        // v = inflater.inflate(R.layout.activity_admin_attendance_history, container, false);
        ButterKnife.inject(this, v);
        activity = getActivity();
        final Typeface mFont = Typeface.createFromAsset(activity.getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) v.getRootView();
        Util.setAppFont(mContainer, mFont);

        pd = new TransparentProgressDialog(activity, R.drawable.progressdialog, false);

        Init();

        return v;

    }


    private void Init() {
        helper = new SQLiteHelper(activity, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        listAttendHistory = new ArrayList<AttendanceHistoryModel>();
       /* Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int currentYear = calendar.get(Calendar.YEAR);*/
        startCalendar = Calendar.getInstance();
        calendar = Calendar.getInstance();
        curMonth = calendar.get(calendar.MONTH);
        curYear = calendar.get(calendar.YEAR);
        curDate = calendar.get(calendar.DAY_OF_MONTH);


        startCalendar.set(Calendar.YEAR, curYear);
        startCalendar.set(Calendar.MONTH, curMonth);
        startCalendar.set(Calendar.DAY_OF_MONTH, curDate);
        if (startCalendar.before(calendar)) {
            relativeNext.setVisibility(View.VISIBLE);
        } else {
            relativeNext.setVisibility(View.INVISIBLE);
        }

        setDisplayData();

        monthName = getResources().getStringArray(R.array.month_array);
    }


    @OnClick(R.id.relativeDate)
    void openDatePicker() {
        Log.e(TAG, "INside relativeDate click ********");
        // activity.showDialog(START_DATE);

      /*  MyTimePickerDialog timePickerDialog = new MyTimePickerDialog(AdvancedCDT.this,
                TimePickerDialog.THEME_HOLO_DARK, timeSetListener, 0, 0, true);*/
        DatePickerDialog d = new DatePickerDialog(activity,
                DatePickerDialog.THEME_HOLO_LIGHT, myfromDateListener, startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DATE));
        d.getDatePicker().setMaxDate(new Date().getTime());
        d.show();
    }

    private DatePickerDialog.OnDateSetListener myfromDateListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

            Log.e(TAG, "Date and year is:- @@@@@@@@@@@@  " + year + " ***** " + month + "  =====" + day);

            startCalendar.set(Calendar.YEAR, year);
            startCalendar.set(Calendar.MONTH, month);
            startCalendar.set(Calendar.DAY_OF_MONTH, day);
            if (startCalendar.before(calendar)) {
                relativeNext.setVisibility(View.VISIBLE);
            } else {
                relativeNext.setVisibility(View.INVISIBLE);
            }
            setDisplayData();

        }
    };

    public void setDisplayData() {
        int month, year, day;

        String sdate = sdf1.format(startCalendar.getTime());
        tvDate.setText(sdate);
        tvYear.setText("" + sdf2.format(startCalendar.getTime()));

        if (Util.isOnline(activity.getApplicationContext())) {

            new getAllUserRecords().execute();
        } else {
            month = startCalendar.get(calendar.MONTH);
            year = startCalendar.get(calendar.YEAR);
            day = startCalendar.get(calendar.DAY_OF_MONTH);
            String date = "" + year + "/" + (month + 1) + "/" + day;

            reload(Util.ReadFile(activity.getCacheDir() + "/AttendanceHistory", Constant.AttendanceFile + "_" + date + ".txt"));
        }
    }

    protected Dialog onCreateDialog(int id) {
        if (id == START_DATE) {
            DatePickerDialog dialog = new DatePickerDialog(activity, DatePickerDialog.THEME_HOLO_LIGHT, myfromDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
            dialog.getDatePicker().setMaxDate(new Date().getTime());
            return dialog;
        }
        return null;
    }

/*
    DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year,
                              int monthOfYear, int dayOfMonth) {
            curYear = year;
            curMonth = monthOfYear;
            curDate = dayOfMonth;

        }
    };*/


    class getAllUserRecords extends AsyncTask<Void, String, String> {

        String response;
        int year, month, day;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pd != null) {
                pd.show();
            }
            isAPICalling = true;
        }


        @Override
        protected String doInBackground(Void... params) {

            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            month = startCalendar.get(calendar.MONTH);
            year = startCalendar.get(calendar.YEAR);
            day = startCalendar.get(calendar.DAY_OF_MONTH);
            //String date = (String) spinnerYear.getSelectedItem()+ "/" + (spinnerMonth.getSelectedItemPosition() + 1) + "/" +spinnerDay.getSelectedItem();
            String date = "" + year + "/" + (month + 1) + "/" + day;
            params1.add(new BasicNameValuePair("Date", date));
            params1.add(new BasicNameValuePair("AdminID", "" + Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_USERID)));

            Log.e("params1", ">>" + params1);
            response = Util.makeServiceCall(Constant.URL
                    + "getAllUserAttendanceHistory", 1, params1, activity);

            Log.e("result:- ", ">>" + response);
            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            month = startCalendar.get(calendar.MONTH);
            year = startCalendar.get(calendar.YEAR);
            day = startCalendar.get(calendar.DAY_OF_MONTH);
            String date = "" + year + "/" + (month + 1) + "/" + day;
            //  String date = (String) spinnerYear.getSelectedItem()+ "_" + (spinnerMonth.getSelectedItemPosition() + 1) + "_" +spinnerDay.getSelectedItem();
            //  Util.WriteFile(activity.getCacheDir()+"/AttendanceHistory", Constant.AttendanceFile+"_"+date+".txt", result);
            reload(result);
        }
    }

    private void reload(String result1) {

        isAPICalling = false;
        listAttendHistory.clear();

        try {
            JSONObject jObj = new JSONObject(result1);
            String status = jObj.optString("status");
            if (pd != null) {
                pd.dismiss();
            }

            if (status.equals("Success")) {
                JSONArray jArry = jObj.getJSONArray("data");
                for (int i = 0; i < jArry.length(); i++) {
                    JSONObject jsonObj = jArry.getJSONObject(i);
                    attendanceHistoryModel = new AttendanceHistoryModel();

                    String userArray = jsonObj.optString("user");
                    JSONObject jUserObject = new JSONObject(userArray);

                    attendanceHistoryModel.setUserId("" + jUserObject.optInt("userId"));
                    attendanceHistoryModel.setNewTimeIn("" + jsonObj.optString("timeIn"));
                    attendanceHistoryModel.setNewTimeOut("" + jsonObj.optString("timeOut"));
                    attendanceHistoryModel.setTimeIN("" + jsonObj.optString("checkInTime"));
                    attendanceHistoryModel.setTimeOut("" + jsonObj.optString("checkOutTime"));
                    attendanceHistoryModel.setUpdateTimeIn("" + jsonObj.optString("updatedTimeIn"));
                    attendanceHistoryModel.setUpdateTimeOut("" + jsonObj.optString("updatedTimeOut"));
                    attendanceHistoryModel.setCheckInApprovalState(jsonObj.optBoolean("checkInApproved"));
                    attendanceHistoryModel.setCheckOutApprovalState(jsonObj.optBoolean("checkOutApproved"));
                    attendanceHistoryModel.setManualApprovalState(jsonObj.optBoolean("manualApproved"));
                    attendanceHistoryModel.setImageUrl("" + jUserObject.optString("picture"));
                    attendanceHistoryModel.setIsPresent(jsonObj.optBoolean("present"));
                    attendanceHistoryModel.setIsManualAttendance("" + jsonObj.optString("manualAttendance"));
                    attendanceHistoryModel.setRequestDate("" + jsonObj.optString("attendanceDate"));
                    attendanceHistoryModel.setUserName("" + jUserObject.optString("firstName") + " " + jUserObject.optString("lastName"));
                    attendanceHistoryModel.setAttendanceID("" + jsonObj.optString("attendanceId"));

                    listAttendHistory.add(attendanceHistoryModel);
                }
            } else {
                Toast.makeText(activity, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            if (pd != null) {
                pd.dismiss();
            }
        }
        if (listAttendHistory.size() <= 0) {
            tvNoHistory.setVisibility(View.VISIBLE);
        } else {
            tvNoHistory.setVisibility(View.GONE);
        }
        atndHistoryAdapter = new AttendanceHistoryAdminAdapterNew(activity, listAttendHistory);
        lvAttendanceHis.setAdapter(atndHistoryAdapter);
    }

    @OnClick(R.id.relativeLeftArrow)
    void getPreviousDate() {

        startCalendar.add(startCalendar.DATE, -1);

        if (startCalendar.before(calendar)) {
            relativeNext.setVisibility(View.VISIBLE);
            Log.e(TAG, "@@@@@@@@@***************!!!!!!!!!!!!!");
        } else {
            relativeNext.setVisibility(View.INVISIBLE);
            Log.e(TAG, "======================!!!!!!!!!!!!!!!!!!!!************");
        }
        String sdate = sdf1.format(startCalendar.getTime());
        tvDate.setText(sdate);
        tvYear.setText("" + sdf2.format(startCalendar.getTime()));

        setDisplayData();
        //formattedDate = df.format(startCalendar.getTime());

        Log.v("PREVIOUS DATE : ", sdate);

    }

    @OnClick(R.id.relativeRightArrow)
    void getNextDate() {

        startCalendar.add(startCalendar.DATE, 1);
        if (startCalendar.before(calendar)) {
            relativeNext.setVisibility(View.VISIBLE);
        } else {
            relativeNext.setVisibility(View.INVISIBLE);
        }
        String sdate = sdf1.format(startCalendar.getTime());
        tvDate.setText(sdate);
        tvYear.setText("" + sdf2.format(startCalendar.getTime()));

        setDisplayData();
        //formattedDate = df.format(startCalendar.getTime());

        Log.v("PREVIOUS DATE : ", sdate);

    }
}
