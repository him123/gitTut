package com.bigbang.superteam.leave_attendance;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by USER 7 on 9/25/2015.
 */
public class UpdateTimeActivity extends BaseActivity {


    @InjectView(R.id.btn_submit)
    Button btnUpdate;
    @InjectView(R.id.tvTimeIN)
    TextView tvCheckIn;
    @InjectView(R.id.tvTimeOut)
    TextView tvCheckOut;
    @InjectView(R.id.tvDate)
    TextView tvDate;
    @InjectView(R.id.edDescription)
    EditText edReason;
    @InjectView(R.id.linearTimeOut)
    LinearLayout linerTimeOut;
    @InjectView(R.id.tvUserName)
    TextView tvUserName;
    @InjectView(R.id.imgWhite)
    ImageView imgWhite;
    @InjectView(R.id.imgLogo)
    ImageView imgLogo;
    @InjectView(R.id.tvContactImg)
    TextView tvContactImg;


    public static final int START_TIME = 107;
    public static final int END_TIME = 108;


    Calendar startCalendar, endCalendar, calendar;
    TimePickerDialog.OnTimeSetListener myToTimeListener, myfromTimeListener;

    private TransparentProgressDialog pd;
    Intent i;
    String tempTimeIn, tempTimeOut, oldTimeIn, oldTimeOut, newTimeIn, newTimeOut,updateTimeIN,updateTimeOut,attendanceID;
    String TAG = "  UpdateTimeActivity  ";

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options, options1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updatetime);
        ButterKnife.inject(this);
        pd = new TransparentProgressDialog(UpdateTimeActivity.this, R.drawable.progressdialog, false);
        Init();

    }

    private void Init() {

        i = getIntent();
        String data = i.getStringExtra("data");

        startCalendar = Calendar.getInstance(TimeZone.getDefault());
        endCalendar = Calendar.getInstance(TimeZone.getDefault());
        calendar = Calendar.getInstance(TimeZone.getDefault());

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
        if (picture.length() > 4) {
            tvContactImg.setVisibility(View.GONE);
        } else {
            tvContactImg.setVisibility(View.VISIBLE);
        }

        try {
            JSONObject jsonObject = new JSONObject(data);
            String date = Util.dateFormatWithGMT("" + jsonObject.optString("attendanceDate"));
            tvDate.setText(date);
            oldTimeIn = jsonObject.optString("checkInTime");
            oldTimeOut = jsonObject.optString("checkOutTime");
            newTimeIn = jsonObject.optString("timeIn");
            newTimeOut = jsonObject.optString("timeOut");
            tempTimeIn = jsonObject.optString("timeIn");
            tempTimeOut = jsonObject.optString("timeIn");
            updateTimeIN = jsonObject.optString("updatedTimeIn");
            updateTimeOut = jsonObject.optString("updatedTimeOut");
            attendanceID = jsonObject.optString("id");

            if (jsonObject.optBoolean("present") || (oldTimeOut.length()==0)) {
                linerTimeOut.setVisibility(View.INVISIBLE);
            } else {
                linerTimeOut.setVisibility(View.VISIBLE);
            }

            if(newTimeIn.length()>0){
                tvCheckIn.setText(Util.gmtToLocalTime("" + jsonObject.optString("timeIn")));
            }else{
                tvCheckIn.setText(Util.gmtToLocalTime("" + jsonObject.optString("checkInTime")));
            }

            if(newTimeOut.length()>0){
                tvCheckOut.setText(Util.gmtToLocalTime("" + jsonObject.optString("timeOut")));
            }else{
                tvCheckOut.setText(Util.gmtToLocalTime(""+jsonObject.optString("checkOutTime")));
            }


            String am_pm;

            String[] startTime = tvCheckIn.getText().toString().split(":");

            String[] tempTime = startTime[1].toString().split(" ");
            am_pm = tempTime[1];
            if (am_pm.toLowerCase().equals("pm")) {
                startCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTime[0]));
                if (!startTime[0].equals("12"))
                    startCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTime[0]) + 12);

            } else {
                startCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTime[0]));
                if (startTime[0].equals("12"))
                    startCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTime[0]) - 12);

            }

           // startCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTime[0]));
            startCalendar.set(Calendar.MINUTE, Integer.parseInt(tempTime[0]));

            String[] endTime = tvCheckOut.getText().toString().split(":");
            //endCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTime[0]));
            String[] tempTime1 = endTime[1].toString().split(" ");
            am_pm = tempTime1[1];

            if (am_pm.toLowerCase().equals("pm")) {
                endCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTime[0]));
                if (!endTime[0].equals("12"))
                    endCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTime[0]) + 12);

            } else {
                endCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTime[0]));
                if (endTime[0].equals("12"))
                    endCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTime[0]) - 12);

            }
            //endCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTime[0]));
            endCalendar.set(Calendar.MINUTE, Integer.parseInt(tempTime1[0]));

        } catch (Exception e) {
            e.printStackTrace();
        }

        myToTimeListener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker timePicker, int i, int i2) {

                endCalendar.set(calendar.HOUR_OF_DAY, i);
                endCalendar.set(calendar.MINUTE, i2);
                SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
                String sdate = sdf1.format(endCalendar.getTime());
               // tvCheckOut.setText(sdate);
                Log.e(TAG,"Inside myToTimeListener ***************** -> "+sdate);
                updateStatusTimeOut(sdate);

            }
        };
        myfromTimeListener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker timePicker, int i, int i2) {

                startCalendar.set(calendar.HOUR_OF_DAY, i);
                startCalendar.set(calendar.MINUTE, i2);
                SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
                String sdate = sdf1.format(startCalendar.getTime());

               // tvCheckIn.setText(sdate);

                Log.e(TAG,"Inside myfromTimeListener *****************#### -> "+sdate);
                updateStatusTimeIn(sdate);
                // Log.v(TAG,"Inside timelistner myfromTimeListener *************   "+sdate);
                // holder.tvCheckIn.setText(sdate);

            }
        };
    }

   protected void updateStatusTimeOut(String status) {

        String oldTime = Util.gmtToLocal(oldTimeOut);
        Log.e(TAG,"Inside updateStatusTimeOut old date is:-  "+oldTime);
        String newDate = oldTime + " " + status + ":00";

       Log.e(TAG,"Inside updateStatusTimeOut new date is11111 :-  "+newDate);
        String newTime = Util.locatToGMTTemp(newDate);
       Log.e(TAG,"Inside updateStatusTimeOut new date 222222 :-  "+newTime);
        //tempTimeIn = data.get(pos).getNewTimeIn();
      newTimeOut = newTime;
        //data.get(pos).setNewTimeOut(newTime);

       tvCheckOut.setText(Util.gmtToLocalTime12(newTime));
    }

    protected void updateStatusTimeIn(String status) {

        String oldTime = Util.gmtToLocal(oldTimeIn);
        Log.e(TAG,"Inside updateStatusTimeIn old date is:-  "+oldTime);
        String newDate = oldTime + " " + status + ":00";
        Log.e(TAG,"Inside updateStatusTimeIn new date is11111 :-  "+newDate);
        String newTime = Util.locatToGMTTemp(newDate);
        Log.e(TAG,"Inside updateStatusTimeIn new date 222222 :-  "+newTime);

        //tempTimeIn = data.get(pos).getNewTimeIn();
       newTimeIn = newTime;
        //data.get(pos).setNewTimeOut(newTime);

        tvCheckIn.setText(Util.gmtToLocalTime12(newTime));
    }


    @OnClick(R.id.rlBack)
    void backPressed() {
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }

    @OnClick(R.id.btn_submit)
    void updateTime() {
        if(edReason.getText().length()>0){
            if (linerTimeOut.getVisibility() == View.VISIBLE) {
                if (startCalendar.after(endCalendar)) {
                  Toast.makeText(UpdateTimeActivity.this, "CheckIn time should be lesser than or equal to checkOut time", Toast.LENGTH_SHORT).show();
                } else {
                    if (Util.isOnline(UpdateTimeActivity.this)) {
                        new updateAttendanceTime().execute();
                    } else {
                        try {
                            JSONObject jObj = new JSONObject();
                            jObj.put("UserID", "" + Util.ReadSharePrefrence(UpdateTimeActivity.this, Constant.SHRED_PR.KEY_USERID));
                            jObj.put("OldCheckInTime", oldTimeIn);
                            jObj.put("OldCheckOutTime", oldTimeOut);
                            jObj.put("NewCheckInTime", newTimeIn);
                            jObj.put("NewCheckOutTime", newTimeOut);
                            jObj.put("Reason", ""+edReason.getText());

                            String strJson = jObj.toString();
                            String actionName = "updateCheckInOutTime";
                            String methodType = "2";


                            Util.insertIntoOffline(strJson, methodType, actionName, UpdateTimeActivity.this);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                if (Util.isOnline(UpdateTimeActivity.this)) {
                    new updateAttendanceTime().execute();
                } else {
                    try {
                        JSONObject jObj = new JSONObject();
                        jObj.put("UserID", "" + Util.ReadSharePrefrence(UpdateTimeActivity.this, Constant.SHRED_PR.KEY_USERID));
                        jObj.put("OldCheckInTime", "" + oldTimeIn);
                        jObj.put("OldCheckOutTime", oldTimeOut);
                        jObj.put("NewCheckInTime", newTimeIn);
                        jObj.put("NewCheckOutTime", newTimeOut);
                        jObj.put("Reason", ""+edReason.getText());

                        String strJson = jObj.toString();
                        String actionName = "updateCheckInOutTime";
                        String methodType = "2";

                        Util.insertIntoOffline(strJson, methodType, actionName, UpdateTimeActivity.this);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }else{
            Toast.makeText(UpdateTimeActivity.this,"Please enter reason",Toast.LENGTH_SHORT).show();
        }
    }

   @OnClick(R.id.tvTimeIN)
    void updateTimeIn() {
        Dialog dialog = onCreateDialog(START_TIME, "" + tvCheckIn.getText());
        dialog.show();
    }

    @OnClick(R.id.tvTimeOut)
    void updateTimeOut() {
        Dialog dialog = onCreateDialog(END_TIME, "" + tvCheckOut.getText());
        dialog.show();
    }

    protected Dialog onCreateDialog(int id, String time) {

        if (id == END_TIME) {

            return new TimePickerDialog(UpdateTimeActivity.this, TimePickerDialog.THEME_HOLO_LIGHT, myToTimeListener, endCalendar.get(endCalendar.HOUR_OF_DAY), endCalendar.get(endCalendar.MINUTE), false);


        }
        if (id == START_TIME) {
            return new TimePickerDialog(UpdateTimeActivity.this, TimePickerDialog.THEME_HOLO_LIGHT,myfromTimeListener, startCalendar.get(startCalendar.HOUR_OF_DAY), startCalendar.get(startCalendar.MINUTE), false);
        }
        return null;
    }


    class updateAttendanceTime extends AsyncTask<Void, String, String> {
        String response;
        int pos;

        Context context;

        /* public updateAttendanceTime(int index, Context context) {
             this.pos = index;
             this.context = context;
         }
 */
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
            params1.add(new BasicNameValuePair("UserID", "" + "" + Util.ReadSharePrefrence(UpdateTimeActivity.this, Constant.SHRED_PR.KEY_USERID)));
           /* params1.add(new BasicNameValuePair("OldCheckInTime", oldTimeIn));
            params1.add(new BasicNameValuePair("OldCheckOutTime", oldTimeOut));*/
            //params1.add(new BasicNameValuePair("UpdateCheckIn", newTimeIn));
            if(newTimeOut.length()>0){
                params1.add(new BasicNameValuePair("UpdateCheckOut", newTimeOut));
            }else{
                params1.add(new BasicNameValuePair("UpdateCheckOut", oldTimeOut));
            }
            if(newTimeIn.length()>0){
                params1.add(new BasicNameValuePair("UpdateCheckIn", newTimeIn));
            }else{
                params1.add(new BasicNameValuePair("UpdateCheckIn", oldTimeIn));
            }
            params1.add(new BasicNameValuePair("AttendanceID", attendanceID));
            params1.add(new BasicNameValuePair("Reason", ""+edReason.getText()));

            Log.e("params1", ">>" + params1);
            response = Util.makeServiceCall(Constant.URL + "updateAttendance", 2, params1, getApplicationContext());

            Log.e(TAG, "response is:- >>>" + response);
            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (pd != null) {
                pd.dismiss();
            }
            try {
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");
                Toast.makeText(UpdateTimeActivity.this, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
                if (status.equals("Success")) {
                    // data.get(pos).setApprovalState("2");
                    Log.e(TAG, "Inside post execute !!!!!!!!!!!!!!!!!!");
                    write(Constant.SHRED_PR.KEY_RELOAD, "1");
                    finish();
                    overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
                } else {
                   /* data.get(pos).setNewTimeIn(tempTimeIn);
                    data.get(pos).setNewTimeOut(tempTimeOut);*/


                }

                Log.e(TAG, "Inside post execute @@@@@@@@@@!!!!!!!!!!!!!!!!!!");
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }

}
