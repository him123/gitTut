package com.bigbang.superteam.leave_attendance;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

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
 * Created by USER 3 on 4/9/2015.
 */
public class ApplyLeaveActivity extends BaseActivity {


    @InjectView(R.id.spnrLeaveType)
    Spinner spinnerLeave;
    @InjectView(R.id.tvFromDate)
    TextView tvStartDate;
    @InjectView(R.id.tvTillDate)
    TextView tvEndDate;
    @InjectView(R.id.edDescription)
    EditText tvReason;
    @InjectView(R.id.tvUserName)
    TextView tvUserName;
    @InjectView(R.id.imgWhite)
    ImageView imgWhite;
    @InjectView(R.id.imgLogo)
    ImageView imgLogo;
    @InjectView(R.id.tvContactImg)
    TextView tvContactImg;
    @InjectView(R.id.tvFullDay)
    TextView tvFullDay;
    @InjectView(R.id.tvHalfDay)
    TextView tvHalfDay;
    @InjectView(R.id.tvTillTitle)
    TextView tvEndDateTitle;

    //

    String TAG = "ApplyLeaveActivity";
    static SQLiteHelper helper;
    public static SQLiteDatabase db = null;
    JSONObject jObject;
    boolean isFirstTime = false;

    ArrayList<String> listLeaves;
    public static final int END_DATE = 105;
    public static final int START_DATE = 106;
    public static final int START_TIME = 107;
    public static final int END_TIME = 108;

    Calendar calendar;
    Calendar startCalendar, endCalendar;
    int curMonth, curYear, curDate;
    ListView lv;
    private TransparentProgressDialog pd;

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options, options1;

    private String isFullDay= "full";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applyleave);
        ButterKnife.inject(this);
        Init();
        pd = new TransparentProgressDialog(ApplyLeaveActivity.this, R.drawable.progressdialog,false);
       /* if (Util.isOnline(ApplyLeaveActivity.this)) {
            new getLeaveTypes().execute();
        }*/
      //  getLeavesFromDatabase();


    }


    @OnClick(R.id.btn_submit)
    void applyLeave() {

        if ((spinnerLeave != null && spinnerLeave.getSelectedItem() != null)) {
            if (tvStartDate.getText().length() > 0) {
                if (tvEndDate.getText().length() > 0) {
                    if (tvReason.getText().length() > 0) {
                        if (startCalendar.after(endCalendar) && isFullDay.equals("full")) {
                            Toast.makeText(getApplicationContext(), "Please enter end date which is greater than or equal to start date", Toast.LENGTH_SHORT).show();
                        } else {
                            if (Util.isOnline(ApplyLeaveActivity.this)) {
                                new applyForLeave().execute();
                            } else {
                                Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                                /*try {
                                    JSONObject jObj = new JSONObject();
                                    jObj.put("UserID", "" + Util.ReadSharePrefrence(ApplyLeaveActivity.this, Constant.SHRED_PR.KEY_USERID));
                                    jObj.put("Type", "" + spinnerLeave.getSelectedItem());
                                    String startDate = Util.locatToGMTNewFormat("" + tvStartDate.getText() + " 12:00:00");
                                    jObj.put("StartDate", "" + startDate);
                                    *//*String endDate = Util.locatToGMTNewFormat("" + tvEndDate.getText() + " 12:00:00");
                                    jObj.put("EndDate", "" + endDate);*//*
                                    if(isFullDay.equals("full")){
                                        String endDate = Util.locatToGMTNewFormat("" + tvEndDate.getText() + " 12:00:00");
                                        jObj.put("EndDate", "" + endDate);
                                    }else{
                                        jObj.put("EndDate", "" + startDate);
                                    }
                                    jObj.put("Reason", "" + tvReason.getText());
                                    jObj.put("LeaveDay", "" +isFullDay);
                                    String strJson = jObj.toString();
                                    String actionName = "applyLeave";
                                    String methodType = "2";

                                    Util.insertIntoOffline(strJson, methodType, actionName, ApplyLeaveActivity.this);
                                    finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }*/
                            }
                        }
                    } else {
                        Toast.makeText(ApplyLeaveActivity.this, getResources().getString(R.string.PleaseEnterReason), Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(ApplyLeaveActivity.this, getResources().getString(R.string.PleaseEnterEndDate), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(ApplyLeaveActivity.this, getResources().getString(R.string.PleaseEnterStartDate), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(ApplyLeaveActivity.this, getResources().getString(R.string.PleaseEnterLeave), Toast.LENGTH_LONG).show();
        }

    }

    @OnClick(R.id.tvFromDate)
    void selectStartDate() {
        showDialog(START_DATE);
    }

    @OnClick(R.id.tvTillDate)
    void selectEndDate() {
        showDialog(END_DATE);
    }

    @OnClick(R.id.rlBack)
    void backPressed() {
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }


    @OnClick(R.id.tvFullDay)
    void fullDayClick() {
        tvFullDay.setBackgroundResource(R.drawable.rectangle_fullday);
        tvHalfDay.setBackgroundResource(R.drawable.rectangle_halfday);
        tvHalfDay.setTextColor(getResources().getColor(R.color.gray));
        tvFullDay.setTextColor(getResources().getColor(R.color.white));

        tvEndDateTitle.setVisibility(View.VISIBLE);
        tvEndDate.setVisibility(View.VISIBLE);
        isFullDay = "full";
    }

    @OnClick(R.id.tvHalfDay)
    void HalfDayClick() {
        tvHalfDay.setBackgroundResource(R.drawable.rectangle_fullday);
        tvFullDay.setBackgroundResource(R.drawable.rectangle_halfday);
        tvFullDay.setTextColor(getResources().getColor(R.color.gray));
        tvHalfDay.setTextColor(getResources().getColor(R.color.white));
        isFullDay = "half";

        tvEndDateTitle.setVisibility(View.INVISIBLE);
        tvEndDate.setVisibility(View.INVISIBLE);
    }


    private void Init() {

        listLeaves = new ArrayList<String>();
        calendar = Calendar.getInstance();
        curMonth = calendar.get(calendar.MONTH);
        curYear = calendar.get(calendar.YEAR);
        curDate = calendar.get(calendar.DAY_OF_MONTH);

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(150))
                .showImageOnLoading(R.drawable.default_image).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        options1 = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.empty_profilepic).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.empty_profilepic).showImageOnFail(R.drawable.empty_profilepic).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();



        imageLoader.displayImage("", imgWhite, options1);
        imageLoader.displayImage("", imgLogo, options);

        tvUserName.setText("" + read(Constant.SHRED_PR.KEY_FIRSTNAME) + " " + read(Constant.SHRED_PR.KEY_LASTNAME));
        tvContactImg.setText("" + read(Constant.SHRED_PR.KEY_FIRSTNAME));
        String picture = "" + read(Constant.SHRED_PR.KEY_Picture);
        imageLoader.displayImage("" +picture, imgLogo, options);
        if (picture.length() > 5) {
            tvContactImg.setVisibility(View.GONE);
        } else {
            tvContactImg.setVisibility(View.VISIBLE);
        }

        tvStartDate.setText("" + Util.dateFormatLocal("" + curDate + "/" + (curMonth + 1) + "/" + curYear));
       /* tvStartDate.setText(""+curYear+"/"+(curMonth-1)+"/"+curDate);
        tvEndDate.setText(""+curYear+"/"+(curMonth-1)+"/"+curDate);*/
        tvEndDate.setText("" + Util.dateFormatLocal("" + curDate + "/" + (curMonth + 1) + "/" + curYear));
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();

       /* ArrayAdapter<String> adapterLeaves = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listLeaves);
        adapterLeaves.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLeave.setAdapter(adapterLeaves);*/
    }

    protected Dialog onCreateDialog(int id) {
        if (id == END_DATE) {


            DatePickerDialog d = new DatePickerDialog(ApplyLeaveActivity.this,
                    DatePickerDialog.THEME_HOLO_LIGHT, mytoDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
            //DatePickerDialog dialog = new DatePickerDialog(this, mytoDateListener, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DATE));
            //dialog.getDatePicker().setMinDate(new Date().getTime()-1000);
            return d;
        }
        if (id == START_DATE) {

            DatePickerDialog d = new DatePickerDialog(ApplyLeaveActivity.this,
                    DatePickerDialog.THEME_HOLO_LIGHT, myfromDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
            //DatePickerDialog dpd = new DatePickerDialog(this, myfromDateListener, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DATE));
            // dpd.getDatePicker().setMinDate(new Date().getTime()-1000);
            return d;
        }
       /* if (id == END_TIME) {
            return new TimePickerDialog(this, myToTimeListener, calendar.HOUR_OF_DAY, calendar.MINUTE, true);

        }
        if (id == START_TIME) {
            return new TimePickerDialog(this, myfromTimeListener, calendar.HOUR_OF_DAY, calendar.MINUTE, true);
        }*/
        return null;
    }

    private DatePickerDialog.OnDateSetListener mytoDateListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

            endCalendar.set(Calendar.YEAR, year);
            endCalendar.set(Calendar.MONTH, month);
            endCalendar.set(Calendar.DAY_OF_MONTH, day);

           // SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf1 = new SimpleDateFormat(" d" + " MMM"+","+" yyyy");
           // SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
            String sdate = sdf1.format(endCalendar.getTime());
            tvEndDate.setText(sdate);
           /* if (startCalendar.after(endCalendar)) {


                Toast.makeText(getApplicationContext(), "Please enter end date which is greater than or equal to start date", Toast.LENGTH_SHORT).show();
            } else {
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                String sdate = sdf1.format(endCalendar.getTime());
                tvEndDate.setText(sdate);
            }*/
        }
    };
    private DatePickerDialog.OnDateSetListener myfromDateListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            System.out.println();

            startCalendar.set(Calendar.YEAR, year);
            startCalendar.set(Calendar.MONTH, month);
            startCalendar.set(Calendar.DAY_OF_MONTH, day);

           // SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf1 = new SimpleDateFormat(" d" + " MMM"+","+" yyyy");
            String sdate = sdf1.format(startCalendar.getTime());
            tvStartDate.setText(sdate);

           /* if (startCalendar.after(endCalendar)) {
               *//* tvStartDate.setText("" + curDate + "/" + (curMonth + 1) + "/" + curYear);
                tvEndDate.setText("" + curDate + "/" + (curMonth + 1) + "/" + curYear);*//*
                Toast.makeText(getApplicationContext(), "Please enter end date which is greater than or equal to start date", Toast.LENGTH_SHORT).show();
            } else {
                SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                String sdate = sdf1.format(startCalendar.getTime());
                tvStartDate.setText(sdate);
            }*/


        }
    };
    TimePickerDialog.OnTimeSetListener myToTimeListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker timePicker, int i, int i2) {

            endCalendar.set(Calendar.HOUR_OF_DAY, i);
            endCalendar.set(Calendar.MINUTE, i2);
            if (startCalendar.before(endCalendar) || startCalendar.equals(endCalendar)) {
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd hh:mm");
                String sdate = sdf1.format(endCalendar.getTime());
                tvEndDate.setText(sdate);

            } else {
                tvStartDate.setText(getResources().getText(R.string.datetimeformat));
                tvEndDate.setText(getResources().getText(R.string.datetimeformat));
                Toast.makeText(getApplicationContext(), "Reselect Date and Time", Toast.LENGTH_SHORT).show();
            }
        }
    };
    TimePickerDialog.OnTimeSetListener myfromTimeListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker timePicker, int i, int i2) {

            startCalendar.set(Calendar.HOUR_OF_DAY, i);
            startCalendar.set(Calendar.MINUTE, i2);
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd hh:mm");
            String sdate = sdf1.format(startCalendar.getTime());
            tvStartDate.setText(sdate);
        }
    };

    class getLeaveTypes extends AsyncTask<Void, String, String> {
        String response;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(Void... params) {

            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            response = Util.makeServiceCall(Constant.URL
                    + "leaveType", 1, params1, getApplicationContext());

            Log.e("response", "<<" + response);
            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            listLeaves.clear();
            try {
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");
                // pBar.setVisibility(View.GONE);
                if (status.equals("Success")) {
                    JSONArray jsonArray = jObj.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        listLeaves.add("" + jsonArray.get(i));
                    }
                    insertIntoLeaveTable(listLeaves);
                } else {
                    Toast.makeText(ApplyLeaveActivity.this, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
                }
               /* ArrayAdapter<String> adapterLeaves = new ArrayAdapter<String>(ApplyLeaveActivity.this,
                        android.R.layout.simple_spinner_item, listLeaves);
                adapterLeaves.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerLeave.setAdapter(adapterLeaves);*/

            } catch (Exception e) {
                e.printStackTrace();
                // pBar.setVisibility(View.GONE);
            }
        }
    }

    class applyForLeave extends AsyncTask<Void, String, String> {
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
            params1.add(new BasicNameValuePair("UserID", "" + "" + Util.ReadSharePrefrence(ApplyLeaveActivity.this, Constant.SHRED_PR.KEY_USERID)));
            params1.add(new BasicNameValuePair("Type", "" + spinnerLeave.getSelectedItem()));
            String startDate = Util.locatToGMTNewFormat("" + tvStartDate.getText() + " 12:00:00");
            params1.add(new BasicNameValuePair("StartDate", "" + startDate));
            if(isFullDay.equals("full")){
                String endDate = Util.locatToGMTNewFormat("" + tvEndDate.getText() + " 12:00:00");
                params1.add(new BasicNameValuePair("EndDate", "" + endDate));
            }else{
                params1.add(new BasicNameValuePair("EndDate", "" + startDate));
            }
            params1.add(new BasicNameValuePair("Reason", "" + tvReason.getText()));
            params1.add(new BasicNameValuePair("LeaveDay", ""+isFullDay));
            Log.e("params1", ">>" + params1);
            String response = Util.makeServiceCall(Constant.URL + "applyLeave", 2, params1, getApplicationContext());

            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if(pd!=null){
                pd.dismiss();
            }
            try {
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");

                Toast.makeText(ApplyLeaveActivity.this, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
                if (status.equals("Success")) {
                    //String data = jObj.optString("data");
                    write(Constant.SHRED_PR.KEY_RELOAD, "1");
                    finish();
                    overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
                }

            } catch (Exception e) {
                e.printStackTrace();

            }
        }

    }

    private void insertIntoLeaveTable(ArrayList<String> leaveList) {
        Util.getDb(ApplyLeaveActivity.this).delete(Constant.LeaveTypes, null, null);
        ContentValues values = new ContentValues();
        for (int i = 0; i < leaveList.size(); i++) {
            values.put("leaveType", "" + leaveList.get(i));
            Util.getDb(ApplyLeaveActivity.this).insert(Constant.LeaveTypes, null, values);
        }
        if (isFirstTime == true) {
            getLeavesFromDatabase();

        }
        // Util.getDb(ApplyLeaveActivity.this).close();
    }

    private void getLeavesFromDatabase() {

        Cursor crsr = Util.getDb(ApplyLeaveActivity.this).rawQuery("select * from " + Constant.LeaveTypes, null);

        Log.e(""+TAG, ">>***************" + crsr.getCount());

        if (crsr != null) {
            isFirstTime = false;
            if (crsr.getCount() > 0) {
                crsr.moveToFirst();
                listLeaves.clear();
                do {
                    Log.e(TAG, ">>@@@@@@@@@@@@@@ Inside getCount>0 " + crsr.getCount());
                    String leavteName = crsr.getString(crsr.getColumnIndex("leaveType"));
                    listLeaves.add("" + leavteName);
                } while (crsr.moveToNext());
            } else {
                isFirstTime = true;
                Log.e(TAG, ">>@@@@@@@@@@@@@@<<<<<<<  Inside else part " + crsr.getCount());
                if(Util.isOnline(ApplyLeaveActivity.this)){
                    new getLeaveTypes().execute();
                }
            }
        }
        ArrayAdapter<String> adapterLeaves = new ArrayAdapter<String>(ApplyLeaveActivity.this,
                android.R.layout.simple_spinner_item, listLeaves);
        adapterLeaves.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLeave.setAdapter(adapterLeaves);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }
}