package com.bigbang.superteam.leave_attendance;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.AttendanceDeatilsUserAdapterNew;
import com.bigbang.superteam.adapter.AttendanceDetailsAdapterNew;
import com.bigbang.superteam.common.BaseActivity;
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
import butterknife.OnTouch;

/**
 * Created by USER 7 on 5/28/2015.
 */
public class AttendanceDetailsActivity extends BaseActivity {

    @InjectView(R.id.tvNoHistory)
    TextView tvNoHistory;
    @InjectView(R.id.tvDate1)
    TextView tvDate;
    @InjectView(R.id.tvDate2)
    TextView tvYear;
    @InjectView(R.id.tvTitle)
    TextView tvTitle;
    @InjectView(R.id.list_user_attendance_history)
    ListView lvAttendanceHis;
    @InjectView(R.id.ll_settings_box)
    LinearLayout linearSettingBox;
    @InjectView(R.id.rl_CSV)
    RelativeLayout relativeCSV;
    @InjectView(R.id.rl_location )
    RelativeLayout relativeLocation;
    @InjectView(R.id.relativeRightArrow)
    RelativeLayout relativeNext;


    AttendanceDetailsAdapterNew atndHistoryAdapter;
    AttendanceDeatilsUserAdapterNew attendanceDetailsUserAdapter;
    public static ArrayList<AttendanceHistoryModel> listAttendHistory;
    AttendanceHistoryModel attendanceHistoryModel;

    SQLiteHelper helper;
    String TAG = "AttendanceList Activity ";
    public static SQLiteDatabase db = null;

    String userId, uName, activityName;

    boolean isAPICalling = false;
    Calendar calendar, startCalendar;
    int curMonth, curYear, curDate;

    String[] monthName;
    public static final int START_DATE = 106;

    SimpleDateFormat sdf1 = new SimpleDateFormat(" MMM" + ",");
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy");

    private TransparentProgressDialog pd;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_attendance_details);
        ButterKnife.inject(this);

        pd = new TransparentProgressDialog(AttendanceDetailsActivity.this, R.drawable.progressdialog,false);

        Intent i1 = getIntent();


        userId = i1.getStringExtra("userId");
        uName = i1.getStringExtra("userName");
        activityName = i1.getStringExtra("activityName");

       if(activityName==null){
           userId = Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_USERID);
           uName = "Attendance History";
           activityName = Constant.FROM_USER_DASHBOARD + "";
       }

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);


        Init();
    }
    private void Init() {


        helper = new SQLiteHelper(this, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        tvTitle.setText("Attendance History");


        listAttendHistory = new ArrayList<AttendanceHistoryModel>();

        monthName = getResources().getStringArray(R.array.month_array);

        //int currentYear = calendar.get(Calendar.YEAR);

        startCalendar = Calendar.getInstance();
        calendar = Calendar.getInstance();
        curMonth = calendar.get(calendar.MONTH);
        curYear = calendar.get(calendar.YEAR);
        curDate = calendar.get(calendar.DAY_OF_MONTH);


        startCalendar.set(Calendar.YEAR, curYear);
        startCalendar.set(Calendar.MONTH, curMonth);
        startCalendar.set(Calendar.DAY_OF_MONTH, curDate);

        if(startCalendar.before(calendar)){
            relativeNext.setVisibility(View.VISIBLE);
        }else{
            relativeNext.setVisibility(View.INVISIBLE);
        }

        setDisplayData();


        // spinnerYear.setSelection(calendar.get(calendar.YEAR));
    }

    @OnClick(R.id.rl_CSV)
      void createCSV() {
        int month,year,day;
        month = startCalendar.get(calendar.MONTH);
        year = startCalendar.get(calendar.YEAR);
        int result = Util.createCSVFileForAttendance(listAttendHistory,""+(month+1),""+year,AttendanceDetailsActivity.this);
        linearSettingBox.setVisibility(View.GONE);
        if (result == 1) {
            Toast.makeText(AttendanceDetailsActivity.this, "Your file has been created successfully", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(AttendanceDetailsActivity.this, "Some error occurred while creating file", Toast.LENGTH_LONG).show();

        }
    }

    @OnClick(R.id.rl_location)
    void openSavedLocation() {
        linearSettingBox.setVisibility(View.GONE);
        Intent i = new Intent(AttendanceDetailsActivity.this,ClientVendorAddressListActivity.class);
        i.putExtra("userId",""+userId);
        startActivity(i);
        overridePendingTransition(R.anim.hold_top, R.anim.enter_from_left);
    }

    @OnClick(R.id.rl_settings)
    void openMenu() {
        linearSettingBox.setVisibility(View.VISIBLE);
        if(Util.ReadSharePrefrence(AttendanceDetailsActivity.this, Constant.SHRED_PR.KEY_ROLE_ID).equals("4")){
            relativeLocation.setVisibility(View.GONE);
        }else if (Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_ROLE_ID).equals("3")) {
            if (activityName.equals("AttendanceFragment")) {
                relativeLocation.setVisibility(View.GONE);
            } else {
                relativeLocation.setVisibility(View.VISIBLE);
            }
        }
    }

    @OnTouch(R.id.relativeMain)
    boolean onTouch() {
        linearSettingBox.setVisibility(View.GONE);
        return true;
    }

    @OnClick(R.id.rlBack)
    void backPressed() {
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }

    @OnClick(R.id.relativeDate)
    void openDatePicker() {
        DatePickerDialog d = new DatePickerDialog(AttendanceDetailsActivity.this,
                DatePickerDialog.THEME_HOLO_LIGHT, myfromDateListener, startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DATE));
        ((ViewGroup) d.getDatePicker()).findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
        d.getDatePicker().setMaxDate(new Date().getTime());
        d.show();

    }

    public void setDisplayData() {
        int month, year, day;

        String sdate = sdf1.format(startCalendar.getTime());
        tvDate.setText(sdate);
        tvYear.setText("" + sdf2.format(startCalendar.getTime()));

        if (Util.isOnline(AttendanceDetailsActivity.this)) {
            new getUserRecords().execute();
        } else {
            month = startCalendar.get(calendar.MONTH);
            year = startCalendar.get(calendar.YEAR);
            String date = year + "/" + (month + 1);

            Toast.makeText(AttendanceDetailsActivity.this, AttendanceDetailsActivity.this.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
            //reload(Util.ReadFile(AttendanceDetailsActivity.this.getCacheDir() + "/AttendanceHistory", Constant.AttendanceFile + "_" + date + ".txt"));
        }
    }

    private DatePickerDialog.OnDateSetListener myfromDateListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

            startCalendar.set(Calendar.YEAR, year);
            startCalendar.set(Calendar.MONTH, month);
            startCalendar.set(Calendar.DAY_OF_MONTH, day);
            if(startCalendar.before(calendar)){
                relativeNext.setVisibility(View.VISIBLE);
            }else{
                relativeNext.setVisibility(View.INVISIBLE);
            }

            setDisplayData();

        }
    };

    class getUserRecords extends AsyncTask<Void, String, String> {

        int index;
        String attendanceType;
        String response;
        int month,year;

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
            String date = year + "/" + (month + 1);

            //String date = (String) spinnerYear.getSelectedItem() + "/" + (spinnerMonth.getSelectedItemPosition() + 1);
            params1.add(new BasicNameValuePair("MemberID", "" + userId));
            params1.add(new BasicNameValuePair("Month", "" +(month+1)));
            params1.add(new BasicNameValuePair("Year", "" +year));
            Log.e("params1", ">>" + params1);
            response = Util.makeServiceCall(Constant.URL
                    + "getAttendanceHistory", 1, params1, getApplicationContext());
            Log.e("response", "<<" + response);

            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            String date = year + "/" + (month + 1);
            //String date = (String) spinnerYear.getSelectedItem() + "_" + (spinnerMonth.getSelectedItemPosition() + 1);
            //Util.WriteFile(getCacheDir() + "/AttendanceHistory", userId + "_" + date + ".txt", result);
            reload(result);
        }
    }


    private void reload(String result) {
        isAPICalling = false;
        listAttendHistory.clear();

        try {

            tvNoHistory.setVisibility(View.GONE);
            JSONObject jObj = new JSONObject(result);
            String status = jObj.optString("status");
            if (pd != null) {
                pd.dismiss();
            }

            if (status.equals("Success")) {
                JSONArray jArry = jObj.getJSONArray("data");
                for (int i = 0; i < jArry.length(); i++) {
                    final JSONObject jsonObj = jArry.getJSONObject(i);

                    //JSONArray userArray = jsonObj.getJSONArray("user");

                    String userArray = jsonObj.optString("user");
                    JSONObject jUserObject = new JSONObject(userArray);

                    attendanceHistoryModel = new AttendanceHistoryModel();

                    attendanceHistoryModel.setUserId("" + jUserObject.optInt("userID"));
                    attendanceHistoryModel.setTimeIN("" + jsonObj.optString("checkInTime"));
                    attendanceHistoryModel.setNewTimeIn("" + jsonObj.optString("timeIn"));
                    attendanceHistoryModel.setNewTimeOut("" +jsonObj.optString("timeOut"));
                    attendanceHistoryModel.setTimeOut("" +jsonObj.optString("checkOutTime"));
                    attendanceHistoryModel.setIsPresent(jsonObj.optBoolean("present"));
                    attendanceHistoryModel.setCheckInApprovalState(jsonObj.optBoolean("checkInApproved"));
                    attendanceHistoryModel.setCheckOutApprovalState(jsonObj.optBoolean("checkOutApproved"));
                    attendanceHistoryModel.setIsManualAttendance("" + jsonObj.optString("manualAttendance"));
                    attendanceHistoryModel.setManualApprovalState(jsonObj.optBoolean("manualApproved"));
                    attendanceHistoryModel.setRequestDate("" + jsonObj.optString("attendanceDate"));
                    attendanceHistoryModel.setUserName("" + jUserObject.optString("firstName")+" " + jUserObject.optString("lastName"));
                    attendanceHistoryModel.setUpdateTimeIn(""+jsonObj.optString("updatedTimeIn"));
                    attendanceHistoryModel.setUpdateTimeOut(""+jsonObj.optString("updatedTimeOut"));
                    attendanceHistoryModel.setJsonObj(""+jsonObj.toString());
                    attendanceHistoryModel.setImageUrl("" + jUserObject.optString("picture"));

                    listAttendHistory.add(attendanceHistoryModel);
                    // insertUpdateTimeValue(jsonObj,AttendanceDetailsActivity.this);
                }

            } else {
                Toast.makeText(AttendanceDetailsActivity.this, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
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
        if (Util.ReadSharePrefrence(AttendanceDetailsActivity.this, Constant.SHRED_PR.KEY_ROLE_ID).equals("1") || Util.ReadSharePrefrence(AttendanceDetailsActivity.this, Constant.SHRED_PR.KEY_ROLE_ID).equals("2")) {
            atndHistoryAdapter = new AttendanceDetailsAdapterNew(AttendanceDetailsActivity.this, listAttendHistory);
            lvAttendanceHis.setAdapter(atndHistoryAdapter);
        } else if (Util.ReadSharePrefrence(AttendanceDetailsActivity.this, Constant.SHRED_PR.KEY_ROLE_ID).equals("4")) {
            attendanceDetailsUserAdapter = new AttendanceDeatilsUserAdapterNew(AttendanceDetailsActivity.this, listAttendHistory);
            lvAttendanceHis.setAdapter(attendanceDetailsUserAdapter);
        } else if (Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_ROLE_ID).equals("3")) {
            if (activityName.equals(""+Constant.FROM_USER_DASHBOARD)) {
                attendanceDetailsUserAdapter = new AttendanceDeatilsUserAdapterNew(AttendanceDetailsActivity.this, listAttendHistory);
                lvAttendanceHis.setAdapter(attendanceDetailsUserAdapter);
            } else {
                atndHistoryAdapter = new AttendanceDetailsAdapterNew(AttendanceDetailsActivity.this, listAttendHistory);
                lvAttendanceHis.setAdapter(atndHistoryAdapter);
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (read(Constant.SHRED_PR.KEY_RELOAD).equals("1")) {
            write(Constant.SHRED_PR.KEY_RELOAD, "0");
            setDisplayData();
            Init();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }

    @OnClick(R.id.relativeLeftArrow)
    void getPreviousDate() {

        startCalendar.add(startCalendar.MONTH, -1);

        if(startCalendar.before(calendar)){
            relativeNext.setVisibility(View.VISIBLE);
            Log.e(TAG,"@@@@@@@@@***************!!!!!!!!!!!!!");
        }else{
            relativeNext.setVisibility(View.INVISIBLE);
            Log.e(TAG,"======================!!!!!!!!!!!!!!!!!!!!************");
        }
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
        if(startCalendar.before(calendar)){
            relativeNext.setVisibility(View.VISIBLE);
        }else{
            relativeNext.setVisibility(View.INVISIBLE);
        }
        String sdate = sdf1.format(startCalendar.getTime());
        tvDate.setText(sdate);
        tvYear.setText(""+sdf2.format(startCalendar.getTime()));

        setDisplayData();
        //formattedDate = df.format(startCalendar.getTime());

        Log.v("PREVIOUS DATE : ", sdate);


    }
}