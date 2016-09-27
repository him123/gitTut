package com.bigbang.superteam.leave_attendance;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.GPSTracker;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by USER 3 on 4/9/2015.
 */
public class ManualAttendanceActivity extends BaseActivity {
    @InjectView(R.id.btn_submit)
    Button btnSubmit;
    @InjectView(R.id.tvDate)
    TextView tvDate;
    @InjectView(R.id.tvFullDay)
    TextView tvFullDay;
    @InjectView(R.id.tvHalfDay)
    TextView tvHalfDay;
    @InjectView(R.id.edDescription)
    EditText edReason;
    @InjectView(R.id.tvUserName)
    TextView tvUserName;
    @InjectView(R.id.imgWhite)
    ImageView imgWhite;
    @InjectView(R.id.imgLogo)
    ImageView imgLogo;
    @InjectView(R.id.tvContactImg)
    TextView tvContactImg;

  /*  @InjectView(R.id.spnrAttType)
    Spinner spnrAttendanceType;*/


    String TAG = "ManualAttendanceActivity";
    String locationType = "Office";
    double curLat, curLng;
    public static final int END_DATE = 105;
    public static final int START_DATE = 106;

    Calendar calendar;
    Calendar startCalendar, endCalendar;

    static SQLiteHelper helper;
    public static SQLiteDatabase db = null;
    Context context = this;

    private TransparentProgressDialog pd;

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options, options1;

    private String isFullDay = "full";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manualattendance);
        ButterKnife.inject(this);
        pd = new TransparentProgressDialog(ManualAttendanceActivity.this, R.drawable.progressdialog, false);
        Init();

    }

    private void Init() {

        calendar = Calendar.getInstance(TimeZone.getDefault());
        int curMonth = calendar.get(calendar.MONTH);
        int curYear = calendar.get(calendar.YEAR);
        int curDate = calendar.get(calendar.DAY_OF_MONTH);
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        // tvDate.setText("" + curDate + "/" + (curMonth + 1) + "/" + curYear);
        tvDate.setText("" + Util.dateFormatLocal("" + curDate + "/" + (curMonth + 1) + "/" + curYear));


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

        imageLoader.init(ImageLoaderConfiguration
                .createDefault(getApplicationContext()));

        imageLoader.displayImage("", imgWhite, options1);
        imageLoader.displayImage("", imgLogo, options);

        tvUserName.setText("" + read(Constant.SHRED_PR.KEY_FIRSTNAME) + " " + read(Constant.SHRED_PR.KEY_LASTNAME));
        tvContactImg.setText("" + read(Constant.SHRED_PR.KEY_FIRSTNAME));
        String picture = "" + read(Constant.SHRED_PR.KEY_Picture);
        imageLoader.displayImage("" + picture, imgLogo, options);
        if (picture.length() > 4) {
            tvContactImg.setVisibility(View.GONE);
        } else {
            tvContactImg.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.tvDate)
    void selectCheckInDate() {
        showDialog(START_DATE);
    }

    @OnClick(R.id.rlBack)
    void backPressed() {
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }

    @OnClick(R.id.btn_submit)
    void manualChechkIn() {
        if (!(tvDate.getText().toString().isEmpty())) {
            if (!edReason.getText().toString().isEmpty()) {
                getCurrentLatLong();
            } else {
                Toast.makeText(ManualAttendanceActivity.this, "Please enter reason", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(ManualAttendanceActivity.this, "Please fill up all details", Toast.LENGTH_LONG).show();
        }
    }

    @OnClick(R.id.tvFullDay)
    void fullDayClick() {
        tvFullDay.setBackgroundResource(R.drawable.rectangle_fullday);
        tvHalfDay.setBackgroundResource(R.drawable.rectangle_halfday);
        tvHalfDay.setTextColor(getResources().getColor(R.color.gray));
        tvFullDay.setTextColor(getResources().getColor(R.color.white));
        isFullDay = "full";
    }

    @OnClick(R.id.tvHalfDay)
    void HalfDayClick() {
        tvHalfDay.setBackgroundResource(R.drawable.rectangle_fullday);
        tvFullDay.setBackgroundResource(R.drawable.rectangle_halfday);
        tvFullDay.setTextColor(getResources().getColor(R.color.gray));
        tvHalfDay.setTextColor(getResources().getColor(R.color.white));
        isFullDay = "half";
    }

    public void getCurrentLatLong() {
        GPSTracker gps = new GPSTracker(ManualAttendanceActivity.this);
        if (gps.canGetLocation()) {
            curLat = gps.getLatitude();
            curLng = gps.getLongitude();
            if (Util.isOnline(ManualAttendanceActivity.this)) {
                new manualCheckInAsync().execute();
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                /*try {
                    JSONObject jObj = new JSONObject();

                    jObj.put("UserID", "" + Util.ReadSharePrefrence(ManualAttendanceActivity.this, Constant.SHRED_PR.KEY_USERID));
                    jObj.put("Reason", "" + edReason.getText().toString());
                    String date = Util.locatToGMTNewFormat("" + tvDate.getText() + " 12:00:00");
                    jObj.put("Date", "" +date);
                    jObj.put("ManualAttendanceType", "" +isFullDay);
                    String strJson = jObj.toString();
                    String actionName = "manualAttendance";
                    String methodType = "2";

                    Util.insertIntoOffline(strJson, methodType, actionName, context);
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
            }
        } else {
            Toast.makeText(ManualAttendanceActivity.this, "Location is not available, please try again!!", Toast.LENGTH_SHORT).show();
        }
    }

    class manualCheckInAsync extends AsyncTask<Void, String, String> {

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
            params1.add(new BasicNameValuePair("UserID", "" + Util.ReadSharePrefrence(ManualAttendanceActivity.this, Constant.SHRED_PR.KEY_USERID)));
            // params1.add(new BasicNameValuePair("Type", "" + locationType));
            params1.add(new BasicNameValuePair("Reason", "" + edReason.getText().toString()));
            String date = Util.locatToGMTNewFormat("" + tvDate.getText() + " 12:00:00");
            //  params1.add(new BasicNameValuePair("CheckInTime", "" + tvCheckIn.getText().toString()));
            params1.add(new BasicNameValuePair("Date", date));
            params1.add(new BasicNameValuePair("ManualAttendanceType", "" + isFullDay));

            Log.e("params1", ">>" + params1);
            String response = Util.makeServiceCall(Constant.URL + "manualAttendance", 2, params1, getApplicationContext());
            Log.e("response", "***>>" + response);
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
                Toast.makeText(ManualAttendanceActivity.this, "" + jObj.optString("message"), Toast.LENGTH_LONG).show();
                if (status.equals("Success")) {
                    //Toast.makeText(ManualAttendanceActivity.this, "" + jObj.optString("message"), Toast.LENGTH_LONG).show();
                   /* Intent in = new Intent(ManualAttendanceActivity.this, UserDashboardActivity.class);
                    startActivity(in);*/
                    finish();
                    overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
    }

    protected Dialog onCreateDialog(int id) {
        if (id == START_DATE) {
            DatePickerDialog dialog = new DatePickerDialog(this, DatePickerDialog.THEME_HOLO_LIGHT, myfromDateListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
            dialog.getDatePicker().setMaxDate(new Date().getTime());
            return dialog;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myfromDateListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            System.out.println();

            startCalendar.set(Calendar.YEAR, year);
            startCalendar.set(Calendar.MONTH, month);
            startCalendar.set(Calendar.DAY_OF_MONTH, day);

            //SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf1 = new SimpleDateFormat(" d" + " MMM" + "," + " yyyy");
            String sdate = sdf1.format(startCalendar.getTime());
            tvDate.setText(sdate);
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }
}
