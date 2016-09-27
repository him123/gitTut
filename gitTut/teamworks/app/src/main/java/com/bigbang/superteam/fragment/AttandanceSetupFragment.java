package com.bigbang.superteam.fragment;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bigbang.superteam.R;
import com.bigbang.superteam.login_register.CompanySetupActivity;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.CustomTimePickerDialog;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class AttandanceSetupFragment extends Fragment {

    static final int TIME_DIALOG_ID = 999;
    private static final String TAG = "AttandanceSetupFragment";
    static int FROM = Constant.FROM_LOGIN;
    private static Map<Integer, Integer> ID_CLASS = new HashMap<>();
    @InjectView(R.id.rl_mon)
    RelativeLayout rl_mon;
    @InjectView(R.id.rl_tue)
    RelativeLayout rl_tue;
    @InjectView(R.id.rl_wed)
    RelativeLayout rl_wed;
    @InjectView(R.id.rl_thu)
    RelativeLayout rl_thu;
    @InjectView(R.id.rl_fri)
    RelativeLayout rl_fri;
    @InjectView(R.id.rl_sat)
    RelativeLayout rl_sat;
    @InjectView(R.id.rl_sun)
    RelativeLayout rl_sun;
    @InjectView(R.id.tvMon)
    TextView tvMon;
    @InjectView(R.id.tvTue)
    TextView tvTue;
    @InjectView(R.id.tvWed)
    TextView tvWed;
    @InjectView(R.id.tvThu)
    TextView tvThu;
    @InjectView(R.id.tvFri)
    TextView tvFri;
    @InjectView(R.id.tvSat)
    TextView tvSat;
    @InjectView(R.id.tvSun)
    TextView tvSun;
    @InjectView(R.id.rl_next)
    RelativeLayout rl_next;
    @InjectView(R.id.rl_bottom)
    RelativeLayout rl_bottom;
    @InjectView(R.id.rl_start_time)
    RelativeLayout rl_start_time;
    @InjectView(R.id.rl_end_time)
    RelativeLayout rl_end_time;
    @InjectView(R.id.tv_start_time)
    TextView tv_start_time;
    @InjectView(R.id.tv_end_time)
    TextView tv_end_time;
    @InjectView(R.id.tvNext)
    TextView tvNext;

    @InjectView(R.id.etMinimumHours)
    EditText etMinimumHours;
    @InjectView(R.id.etMinimumMinutes)
    EditText etMinimumMinutes;

    @InjectView(R.id.etAVGHours)
    EditText etAVGHours;
    @InjectView(R.id.etAVGMinutes)
    EditText etAVGMinutes;

    @InjectView(R.id.tv_start_time_tracking)
    TextView tv_start_time_tracking;
    @InjectView(R.id.tv_end_time_tracking)
    TextView tv_end_time_tracking;

    @InjectView(R.id.cbSaturday1)
    CheckBox cbSaturday1;
    @InjectView(R.id.switchSaturday1)
    RelativeLayout switchSaturday1;
    @InjectView(R.id.txtSat1H)
    TextView txtSat1H;
    @InjectView(R.id.txtSat1F)
    TextView txtSat1F;
    boolean sat1 = false;

    @InjectView(R.id.cbSaturday2)
    CheckBox cbSaturday2;
    @InjectView(R.id.switchSaturday2)
    RelativeLayout switchSaturday2;
    @InjectView(R.id.txtSat2H)
    TextView txtSat2H;
    @InjectView(R.id.txtSat2F)
    TextView txtSat2F;
    boolean sat2 = false;

    @InjectView(R.id.cbSaturday3)
    CheckBox cbSaturday3;
    @InjectView(R.id.switchSaturday3)
    RelativeLayout switchSaturday3;
    @InjectView(R.id.txtSat3H)
    TextView txtSat3H;
    @InjectView(R.id.txtSat3F)
    TextView txtSat3F;
    boolean sat3 = false;

    @InjectView(R.id.cbSaturday4)
    CheckBox cbSaturday4;
    @InjectView(R.id.switchSaturday4)
    RelativeLayout switchSaturday4;
    @InjectView(R.id.txtSat4H)
    TextView txtSat4H;
    @InjectView(R.id.txtSat4F)
    TextView txtSat4F;
    boolean sat4 = false;

    @InjectView(R.id.cbSaturday5)
    CheckBox cbSaturday5;
    @InjectView(R.id.switchSaturday5)
    RelativeLayout switchSaturday5;
    @InjectView(R.id.txtSat5H)
    TextView txtSat5H;
    @InjectView(R.id.txtSat5F)
    TextView txtSat5F;
    boolean sat5 = false;

    @InjectView(R.id.rl_saturdays)
    RelativeLayout rl_saturdays;
    @InjectView(R.id.rl_saturday_policy)
    RelativeLayout rl_saturday_policy;

    @InjectView(R.id.bt_saturday_policy_enabled)
    ToggleButton bt_saturday_policy_enabled;

    @InjectView(R.id.rl_main_container)
    ScrollView rl_main_container;

    TransparentProgressDialog progressDialog;
    int[] working_days = {1, 1, 1, 1, 1, 0, 0};
    Calendar startCalendar, endCalendar, startCalendatTracking, endCalendarTracking;
    TimePickerDialog.OnTimeSetListener startTimeListener, endTimeListener, startTimeTrackingListener, endTimeTrackingListener;
    //int mHourStart, mMinuteStart, mHourEnd, mMinuteEnd;
    String roleId = "";
    Activity activity;

    // TODO: Rename and change types and number of parameters
    public static AttandanceSetupFragment newInstance(int from) {
        FROM = from;
        AttandanceSetupFragment fragment = new AttandanceSetupFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        activity = getActivity();
        View view = inflater.inflate(R.layout.fragment_attendance_setup, container, false);
        final Typeface mFont = Typeface.createFromAsset(activity.getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) view.getRootView();
        Util.setAppFont(mContainer, mFont);
        ButterKnife.inject(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();

        roleId = read(Constant.SHRED_PR.KEY_ROLE_ID);
        if (Arrays.asList("3", "4").contains(roleId)) {
            rl_bottom.setVisibility(View.GONE);

            rl_mon.setClickable(false);
            rl_tue.setClickable(false);
            rl_wed.setClickable(false);
            rl_thu.setClickable(false);
            rl_fri.setClickable(false);
            rl_sat.setClickable(false);
            rl_sun.setClickable(false);

            tv_start_time.setClickable(false);
            tv_end_time.setClickable(false);
            tv_start_time_tracking.setClickable(false);
            tv_end_time_tracking.setClickable(false);

            bt_saturday_policy_enabled.setClickable(false);

            cbSaturday1.setClickable(false);
            cbSaturday2.setClickable(false);
            cbSaturday3.setClickable(false);
            cbSaturday4.setClickable(false);
            cbSaturday5.setClickable(false);

            switchSaturday1.setClickable(false);
            switchSaturday2.setClickable(false);
            switchSaturday3.setClickable(false);
            switchSaturday4.setClickable(false);
            switchSaturday5.setClickable(false);

            etMinimumHours.setEnabled(false);
            etMinimumHours.setFocusable(false);
            etMinimumMinutes.setEnabled(false);
            etMinimumMinutes.setFocusable(false);

            etAVGHours.setEnabled(false);
            etAVGHours.setFocusable(false);
            etAVGMinutes.setEnabled(false);
            etAVGMinutes.setFocusable(false);

        }

        setupDefault();
    }

    @OnClick(R.id.bt_saturday_policy_enabled)
    @SuppressWarnings("unused")
    public void Payroll(View view) {
        if (bt_saturday_policy_enabled.isChecked()) {
            rl_saturdays.setVisibility(View.VISIBLE);
            //Scroll to last
            rl_main_container.post(new Runnable() {
                @Override
                public void run() {
                    rl_main_container.fullScroll(View.FOCUS_DOWN);
                }
            });
        } else {
            rl_saturdays.setVisibility(View.GONE);
        }
    }


    @OnClick(R.id.rl_next)
    @SuppressWarnings("unused")
    public void Next(View view) {

        if (isValidate()) {
            HashMap<String, String> hashMap = new HashMap<>();

            String strWorkingDays = "";
            for (int i = 0; i < working_days.length; i++) {
                switch (i) {
                    case 0:
                        if (working_days[i] == 1) {//0
                            if (strWorkingDays.length() == 0) strWorkingDays = "Mon";
                            else strWorkingDays = strWorkingDays + ",Mon";
                        }
                        break;
                    case 1:
                        if (working_days[i] == 1) {//1
                            if (strWorkingDays.length() == 0) strWorkingDays = "Tue";
                            else strWorkingDays = strWorkingDays + ",Tue";
                        }
                        break;
                    case 2:
                        if (working_days[i] == 1) {//2
                            if (strWorkingDays.length() == 0) strWorkingDays = "Wed";
                            else strWorkingDays = strWorkingDays + ",Wed";
                        }
                        break;
                    case 3:
                        if (working_days[i] == 1) {//3
                            if (strWorkingDays.length() == 0) strWorkingDays = "Thu";
                            else strWorkingDays = strWorkingDays + ",Thu";
                        }
                        break;
                    case 4:
                        if (working_days[i] == 1) {//4
                            if (strWorkingDays.length() == 0) strWorkingDays = "Fri";
                            else strWorkingDays = strWorkingDays + ",Fri";
                        }
                        break;
                    case 5:
                        if (working_days[i] == 1) {//5
                            if (strWorkingDays.length() == 0) strWorkingDays = "Sat";
                            else strWorkingDays = strWorkingDays + ",Sat";
                        }
                        break;
                    case 6:
                        if (working_days[i] == 1) {//6
                            if (strWorkingDays.length() == 0) strWorkingDays = "Sun";
                            else strWorkingDays = strWorkingDays + ",Sun";
                        }
                        break;
                }
            }

            StringBuilder sbSturdayPolicy = new StringBuilder();
            String stSaturdayPolicy = "";
            if (working_days[5] == 1) {
                if (bt_saturday_policy_enabled.isChecked()) {
                    if (cbSaturday1.isChecked()) {
                        if (sat1)
                            sbSturdayPolicy.append("half,");
                        else
                            sbSturdayPolicy.append("full,");
                    } else {
                        sbSturdayPolicy.append("off,");
                    }

                    if (cbSaturday2.isChecked()) {
                        if (sat2)
                            sbSturdayPolicy.append("half,");
                        else
                            sbSturdayPolicy.append("full,");
                    } else {
                        sbSturdayPolicy.append("off,");
                    }

                    if (cbSaturday3.isChecked()) {
                        if (sat3)
                            sbSturdayPolicy.append("half,");
                        else
                            sbSturdayPolicy.append("full,");
                    } else {
                        sbSturdayPolicy.append("off,");
                    }

                    if (cbSaturday4.isChecked()) {
                        if (sat4)
                            sbSturdayPolicy.append("half,");
                        else
                            sbSturdayPolicy.append("full,");
                    } else {
                        sbSturdayPolicy.append("off,");
                    }

                    if (cbSaturday5.isChecked()) {
                        if (sat5)
                            sbSturdayPolicy.append("half");
                        else
                            sbSturdayPolicy.append("full");
                    } else {
                        sbSturdayPolicy.append("off");
                    }

                    stSaturdayPolicy = sbSturdayPolicy.toString();
                }
            }


            SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
            String startTime = sdf2.format(startCalendar.getTime());
            String endTime = sdf2.format(endCalendar.getTime());
            String startTimeTracking;
            String endTimeTracking;
            //Tracking start time
            if (tv_start_time_tracking.getText().toString().equals("")) {
                startTimeTracking = "";
            } else {
                startTimeTracking = sdf2.format(startCalendatTracking.getTime());
            }

            //Tracking end time
            if (tv_end_time_tracking.getText().toString().equals("")) {
                endTimeTracking = "";
            } else {
                endTimeTracking = sdf2.format(endCalendarTracking.getTime());
            }

            hashMap.put("workingDays", strWorkingDays);
            hashMap.put("startTime", startTime + ":00");
            hashMap.put("endTime", endTime + ":00");
            hashMap.put("trackingStartTime", startTimeTracking + ":00");
            hashMap.put("trakingEndTime", endTimeTracking + ":00");

            JSONObject jobjUpdateComp = new JSONObject();

            try {
                jobjUpdateComp.put("companyid", Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_COMPANY_ID));
                jobjUpdateComp.put("userid", Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_USERID));
                jobjUpdateComp.put("workingDays", strWorkingDays);
                jobjUpdateComp.put("startTime", startTime + ":00");
                jobjUpdateComp.put("endTime", endTime + ":00");
                jobjUpdateComp.put("AutoLeaveUpdate", Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_AUTO_LEAVE_UPDATE));
                jobjUpdateComp.put("payrollEnabled", Util.ReadSharePrefrenceBoolean(activity, Constant.SHRED_PR.KEY_PAYROLL_ACTIVE));
                jobjUpdateComp.put("minimumWorkingTime", getTextInt(etMinimumHours) + ":" + getTextInt(etMinimumMinutes));
                jobjUpdateComp.put("avgWorkingTime", getTextInt(etAVGHours) + ":" + getTextInt(etAVGMinutes));
                jobjUpdateComp.put("saturdayPolicy", bt_saturday_policy_enabled.isChecked() + "");
                jobjUpdateComp.put("workingSaturday", stSaturdayPolicy);
                jobjUpdateComp.put("trackingStartTime", startTimeTracking + ":00");
                jobjUpdateComp.put("trakingEndTime", endTimeTracking + ":00");

            } catch (Exception e) {
                Log.d("", "Exception : " + e);
            }

            Log.i("Attendance JSON", "*******************" + jobjUpdateComp + "*******************");

            if (Util.isOnline(activity)) {
                try {
                    new updateCompanyInfo(jobjUpdateComp).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(activity,
                        Constant.network_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*@OnClick(R.id.cbSaturday1)
    public void CbSelection(View view) {
        if (view.isSelected()) {
            switchSaturday1.setBackgroundColor(getResources().getColor(R.color.green_box_bg));
        } else {
            switchSaturday1.setBackgroundColor(getResources().getColor(R.color.bg_slave_option));
        }
    }*/

    @OnClick({R.id.rl_mon, R.id.rl_tue,
            R.id.rl_wed, R.id.rl_thu,
            R.id.rl_fri, R.id.rl_sat,
            R.id.rl_sun})
    @SuppressWarnings("unused")
    public void selectDay(View view) {
        if (working_days[ID_CLASS.get(view.getId())] == 0) {
            working_days[ID_CLASS.get(view.getId())] = 1;
            view.setBackgroundColor(getResources().getColor(R.color.green_header));

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); ++i) {
                View nextChild = ((ViewGroup) view).getChildAt(i);
                if (nextChild instanceof TextView) {
                    ((TextView) nextChild).setTextColor(getResources().getColor(R.color.white));
                }
            }

            //For sturday policy
            if (ID_CLASS.get(view.getId()) == 5) {
                rl_saturday_policy.setVisibility(View.VISIBLE);
            }
        } else {
            working_days[ID_CLASS.get(view.getId())] = 0;
            view.setBackgroundColor(getResources().getColor(R.color.green_box_bg));

            for (int i = 0; i < ((ViewGroup) view).getChildCount(); ++i) {
                View nextChild = ((ViewGroup) view).getChildAt(i);
                if (nextChild instanceof TextView) {
                    ((TextView) nextChild).setTextColor(getResources().getColor(R.color.gray));
                }
            }

            //For saturday policy
            if (ID_CLASS.get(view.getId()) == 5) {
                rl_saturday_policy.setVisibility(View.GONE);
            }
        }

    }

    @OnClick(R.id.rl_start_time_)
    @SuppressWarnings("unused")
    public void setStartDate(View view) {
        try{
            if (Arrays.asList("1", "2").contains(roleId)) {
                CustomTimePickerDialog tpd = new CustomTimePickerDialog(activity, startTimeListener, startCalendar.get(Calendar.HOUR_OF_DAY), startCalendar.get(Calendar.MINUTE), true);
                tpd.show();
            }
        }catch (Exception e){
            Log.e("","Exception : "+e);
        }

    }

    @OnClick(R.id.rl_end_time_)
    @SuppressWarnings("unused")
    public void setEndDate(View view) {
        try{
            if (Arrays.asList("1", "2").contains(roleId)) {
                CustomTimePickerDialog tpd = new CustomTimePickerDialog(activity, endTimeListener, endCalendar.get(Calendar.HOUR_OF_DAY), endCalendar.get(Calendar.MINUTE), true);
                tpd.show();
            }
        }catch (Exception e){
            Log.e("","Exception: "+e);
        }

    }

    @OnClick(R.id.rl_start_time_tracking_main)
    @SuppressWarnings("unused")
    public void setStartDateTracking(View view) {
        try {
            if (Arrays.asList("1", "2").contains(roleId)) {
                CustomTimePickerDialog tpd = new CustomTimePickerDialog(activity, startTimeTrackingListener, startCalendatTracking.get(Calendar.HOUR_OF_DAY), startCalendatTracking.get(Calendar.MINUTE), true);
                tpd.show();
            }
        }catch (Exception e){
            Log.e("","Exception: "+e);
        }

    }

    @OnClick(R.id.rl_end_time_tracking_main)
    @SuppressWarnings("unused")
    public void setEndDateTracking(View view) {
        try{
            if (Arrays.asList("1", "2").contains(roleId)) {
                CustomTimePickerDialog tpd = new CustomTimePickerDialog(activity, endTimeTrackingListener, endCalendarTracking.get(Calendar.HOUR_OF_DAY), endCalendarTracking.get(Calendar.MINUTE), true);
                tpd.show();
            }
        }catch (Exception e){
            Log.e("","Exception : "+e);
        }
    }

    private void init() {

        ID_CLASS.put(R.id.rl_mon, 0);
        ID_CLASS.put(R.id.rl_tue, 1);
        ID_CLASS.put(R.id.rl_wed, 2);
        ID_CLASS.put(R.id.rl_thu, 3);
        ID_CLASS.put(R.id.rl_fri, 4);
        ID_CLASS.put(R.id.rl_sat, 5);
        ID_CLASS.put(R.id.rl_sun, 6);

        progressDialog = new TransparentProgressDialog(activity, R.drawable.progressdialog, false);
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();

        startCalendatTracking = Calendar.getInstance();
        endCalendarTracking = Calendar.getInstance();

        startTimeListener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker timePicker, int i, int i2) {

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.HOUR_OF_DAY, i);
                calendar1.set(Calendar.MINUTE, i2);

                Calendar calendar2 = Calendar.getInstance();
                calendar2.set(Calendar.HOUR_OF_DAY, endCalendar.get(Calendar.HOUR_OF_DAY));
                calendar2.set(Calendar.MINUTE, endCalendar.get(Calendar.MINUTE));

                if (calendar1.before(calendar2)) {
                    startCalendar.set(Calendar.HOUR_OF_DAY, i);
                    startCalendar.set(Calendar.MINUTE, i2);
                    SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm a");
                    String sdate = sdf1.format(startCalendar.getTime());
                    tv_start_time.setText("" + sdate);
                } else {
                    Toast.makeText(activity, "" + activity.getResources().getString(R.string.start_time_less), Toast.LENGTH_SHORT).show();
                }
            }
        };

        endTimeListener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker timePicker, int i, int i2) {

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.HOUR_OF_DAY, i);
                calendar1.set(Calendar.MINUTE, i2);

                Calendar calendar2 = Calendar.getInstance();
                calendar2.set(Calendar.HOUR_OF_DAY, startCalendar.get(Calendar.HOUR_OF_DAY));
                calendar2.set(Calendar.MINUTE, startCalendar.get(Calendar.MINUTE));

                if (calendar1.after(calendar2)) {
                    endCalendar.set(Calendar.HOUR_OF_DAY, i);
                    endCalendar.set(Calendar.MINUTE, i2);
                    SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm a");
                    String sdate = sdf1.format(endCalendar.getTime());
                    tv_end_time.setText("" + sdate);
                } else {
                    Toast.makeText(activity, "" + activity.getResources().getString(R.string.end_time_greater), Toast.LENGTH_SHORT).show();
                }
            }
        };

        startTimeTrackingListener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker timePicker, int i, int i2) {

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.HOUR_OF_DAY, i);
                calendar1.set(Calendar.MINUTE, i2);

                Calendar calendar2 = Calendar.getInstance();
                calendar2.set(Calendar.HOUR_OF_DAY, endCalendarTracking.get(Calendar.HOUR_OF_DAY));
                calendar2.set(Calendar.MINUTE, endCalendarTracking.get(Calendar.MINUTE));

                if (calendar1.before(calendar2)) {
                    startCalendatTracking.set(Calendar.HOUR_OF_DAY, i);
                    startCalendatTracking.set(Calendar.MINUTE, i2);
                    SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm a");
                    String sdate = sdf1.format(startCalendatTracking.getTime());
                    tv_start_time_tracking.setText("" + sdate);
                } else {
                    Toast.makeText(activity, "" + activity.getResources().getString(R.string.start_time_less), Toast.LENGTH_SHORT).show();
                }
            }
        };

        endTimeTrackingListener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker timePicker, int i, int i2) {

                Calendar calendar1 = Calendar.getInstance();
                calendar1.set(Calendar.HOUR_OF_DAY, i);
                calendar1.set(Calendar.MINUTE, i2);

                Calendar calendar2 = Calendar.getInstance();
                calendar2.set(Calendar.HOUR_OF_DAY, startCalendatTracking.get(Calendar.HOUR_OF_DAY));
                calendar2.set(Calendar.MINUTE, startCalendatTracking.get(Calendar.MINUTE));

                if (calendar1.after(calendar2)) {
                    endCalendarTracking.set(Calendar.HOUR_OF_DAY, i);
                    endCalendarTracking.set(Calendar.MINUTE, i2);
                    SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm a");
                    String sdate = sdf1.format(endCalendarTracking.getTime());
                    tv_end_time_tracking.setText("" + sdate);
                } else {
                    Toast.makeText(activity, "" + activity.getResources().getString(R.string.end_time_greater), Toast.LENGTH_SHORT).show();
                }
            }
        };


        switchCLicks();
    }

    private void switchCLicks() {
        switchSaturday1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sat1) {
                    sat1 = false;
                    txtSat1F.setVisibility(View.VISIBLE);
                    txtSat1H.setVisibility(View.INVISIBLE);
                } else {
                    sat1 = true;
                    txtSat1F.setVisibility(View.INVISIBLE);
                    txtSat1H.setVisibility(View.VISIBLE);
                }
            }
        });

        switchSaturday2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sat2) {
                    sat2 = false;
                    txtSat2F.setVisibility(View.VISIBLE);
                    txtSat2H.setVisibility(View.INVISIBLE);
                } else {
                    sat2 = true;
                    txtSat2F.setVisibility(View.INVISIBLE);
                    txtSat2H.setVisibility(View.VISIBLE);
                }
            }
        });

        switchSaturday3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sat3) {
                    sat3 = false;
                    txtSat3F.setVisibility(View.VISIBLE);
                    txtSat3H.setVisibility(View.INVISIBLE);
                } else {
                    sat3 = true;
                    txtSat3F.setVisibility(View.INVISIBLE);
                    txtSat3H.setVisibility(View.VISIBLE);
                }
            }
        });

        switchSaturday4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sat4) {
                    sat4 = false;
                    txtSat4F.setVisibility(View.VISIBLE);
                    txtSat4H.setVisibility(View.INVISIBLE);
                } else {
                    sat4 = true;
                    txtSat4F.setVisibility(View.INVISIBLE);
                    txtSat4H.setVisibility(View.VISIBLE);
                }
            }
        });

        switchSaturday5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sat5) {
                    sat5 = false;
                    txtSat5F.setVisibility(View.VISIBLE);
                    txtSat5H.setVisibility(View.INVISIBLE);
                } else {
                    sat5 = true;
                    txtSat5F.setVisibility(View.INVISIBLE);
                    txtSat5H.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setupDefault() {

        working_days[0] = 0;
        working_days[1] = 0;
        working_days[2] = 0;
        working_days[3] = 0;
        working_days[4] = 0;
        working_days[5] = 0;
        working_days[6] = 0;

        rl_mon.setBackgroundColor(getResources().getColor(R.color.green_box_bg));
        rl_tue.setBackgroundColor(getResources().getColor(R.color.green_box_bg));
        rl_wed.setBackgroundColor(getResources().getColor(R.color.green_box_bg));
        rl_thu.setBackgroundColor(getResources().getColor(R.color.green_box_bg));
        rl_fri.setBackgroundColor(getResources().getColor(R.color.green_box_bg));
        rl_sat.setBackgroundColor(getResources().getColor(R.color.green_box_bg));
        rl_sun.setBackgroundColor(getResources().getColor(R.color.green_box_bg));

        tvMon.setTextColor(getResources().getColor(R.color.gray));
        tvTue.setTextColor(getResources().getColor(R.color.gray));
        tvWed.setTextColor(getResources().getColor(R.color.gray));
        tvThu.setTextColor(getResources().getColor(R.color.gray));
        tvFri.setTextColor(getResources().getColor(R.color.gray));
        tvSat.setTextColor(getResources().getColor(R.color.gray));
        tvSun.setTextColor(getResources().getColor(R.color.gray));

        try {
            JSONObject jsonObject = new JSONObject(read(Constant.SHRED_PR.KEY_COMPANY_SETUP_DATA));
            Log.i("jsonObject", "::" + jsonObject);
            String workingDays = jsonObject.optString("workingDays");

            String[] days = workingDays.split(",");
            for (int i = 0; i < days.length; i++) {
                if (days[i].contains("Mon")) {
                    rl_mon.setBackgroundColor(getResources().getColor(R.color.green_header));
                    tvMon.setTextColor(getResources().getColor(R.color.white));
                    working_days[0] = 1;
                } else if (days[i].contains("Tue")) {
                    rl_tue.setBackgroundColor(getResources().getColor(R.color.green_header));
                    tvTue.setTextColor(getResources().getColor(R.color.white));
                    working_days[1] = 1;
                } else if (days[i].contains("Wed")) {
                    rl_wed.setBackgroundColor(getResources().getColor(R.color.green_header));
                    tvWed.setTextColor(getResources().getColor(R.color.white));
                    working_days[2] = 1;
                } else if (days[i].contains("Thu")) {
                    rl_thu.setBackgroundColor(getResources().getColor(R.color.green_header));
                    tvThu.setTextColor(getResources().getColor(R.color.white));
                    working_days[3] = 1;
                } else if (days[i].contains("Fri")) {
                    rl_fri.setBackgroundColor(getResources().getColor(R.color.green_header));
                    tvFri.setTextColor(getResources().getColor(R.color.white));
                    working_days[4] = 1;
                } else if (days[i].contains("Sat")) {
                    rl_sat.setBackgroundColor(getResources().getColor(R.color.green_header));
                    tvSat.setTextColor(getResources().getColor(R.color.white));
                    working_days[5] = 1;
                } else if (days[i].contains("Sun")) {
                    rl_sun.setBackgroundColor(getResources().getColor(R.color.green_header));
                    tvSun.setTextColor(getResources().getColor(R.color.white));
                    working_days[6] = 1;
                }
            }

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm a");
            SimpleDateFormat sdf2 = new SimpleDateFormat("HH");
            SimpleDateFormat sdf3 = new SimpleDateFormat("mm");

            String startTime = "" + jsonObject.optString("startTime");
            String endTime = "" + jsonObject.optString("endTime");
            write(Constant.SHRED_PR.KEY_COMPANY_STARTTIME, jsonObject.optString("startTime"));
            write(Constant.SHRED_PR.KEY_COMPANY_ENDTIME, jsonObject.optString("endTime"));
            int mHourStart = Integer.parseInt(sdf2.format(sdf.parse(startTime)));
            int mMinuteStart = Integer.parseInt(sdf3.format(sdf.parse(startTime)));
            int mHourEnd = Integer.parseInt(sdf2.format(sdf.parse(endTime)));
            int mMinuteEnd = Integer.parseInt(sdf3.format(sdf.parse(endTime)));
            startTime = sdf1.format(sdf.parse(startTime));
            endTime = sdf1.format(sdf.parse(endTime));

            startCalendar.set(Calendar.HOUR_OF_DAY, mHourStart);
            startCalendar.set(Calendar.MINUTE, mMinuteStart);
            endCalendar.set(Calendar.HOUR_OF_DAY, mHourEnd);
            endCalendar.set(Calendar.MINUTE, mMinuteEnd);

            tv_start_time.setText("" + startTime);
            tv_end_time.setText("" + endTime);

            String startTimeTracking = "" + jsonObject.optString("trackingStartTime");
            String endTimeTracking = "" + jsonObject.optString("trakingEndTime");
            int mHourStartTracking = Integer.parseInt(sdf2.format(sdf.parse(startTimeTracking)));
            int mMinuteStartTracking = Integer.parseInt(sdf3.format(sdf.parse(startTimeTracking)));
            int mHourEndTracking = Integer.parseInt(sdf2.format(sdf.parse(endTimeTracking)));
            int mMinuteEndTracking = Integer.parseInt(sdf3.format(sdf.parse(endTimeTracking)));
            startTimeTracking = sdf1.format(sdf.parse(startTimeTracking));
            endTimeTracking = sdf1.format(sdf.parse(endTimeTracking));

            startCalendatTracking.set(Calendar.HOUR_OF_DAY, mHourStartTracking);
            startCalendatTracking.set(Calendar.MINUTE, mMinuteStartTracking);
            endCalendarTracking.set(Calendar.HOUR_OF_DAY, mHourEndTracking);
            endCalendarTracking.set(Calendar.MINUTE, mMinuteEndTracking);

            tv_start_time_tracking.setText("" + startTimeTracking);
            tv_end_time_tracking.setText("" + endTimeTracking);

            String minimumTime = jsonObject.optString("minimumWorkingTime");
            if (minimumTime != null && minimumTime.length() > 0) {
                String hrs = minimumTime.substring(0, minimumTime.indexOf(":"));
                String minutes = minimumTime.substring(minimumTime.indexOf(":") + 1, minimumTime.length());

                etMinimumHours.setText(hrs + "");
                etMinimumMinutes.setText(minutes + "");
            }

            String avgTime = jsonObject.optString("avgWorkingTime");
            if (avgTime != null && avgTime.length() > 0) {
                String hrs = avgTime.substring(0, avgTime.indexOf(":"));
                String minutes = avgTime.substring(avgTime.indexOf(":") + 1, avgTime.length());

                etAVGHours.setText(hrs + "");
                etAVGMinutes.setText(minutes + "");
            }

            if (working_days[5] == 1) {
                rl_saturday_policy.setVisibility(View.VISIBLE);
                boolean sp = jsonObject.optBoolean("saturdayPolicy");
                if (sp) {
                    rl_saturdays.setVisibility(View.VISIBLE);
                    bt_saturday_policy_enabled.setChecked(true);

                    //set saturdays value  off,full,off,full,off---->input
                    String strSaturdays = jsonObject.optString("workingSaturday");
                    String[] saturdays = strSaturdays.split(",");

                    for (int i = 0; i < saturdays.length; i++) {
                        switch (i) {
                            case 0:
                                if (saturdays[i].equalsIgnoreCase("off")) {
                                    cbSaturday1.setChecked(false);
                                } else {
                                    cbSaturday1.setChecked(true);
                                    if (saturdays[i].equalsIgnoreCase("full")) {
                                        sat1 = false;
                                        txtSat1F.setVisibility(View.VISIBLE);
                                        txtSat1H.setVisibility(View.INVISIBLE);
                                    } else if (saturdays[i].equalsIgnoreCase("half")) {
                                        sat1 = true;
                                        txtSat1F.setVisibility(View.INVISIBLE);
                                        txtSat1H.setVisibility(View.VISIBLE);
                                    }
                                }
                                break;

                            case 1:
                                if (saturdays[i].equalsIgnoreCase("off")) {
                                    cbSaturday2.setChecked(false);
                                } else {
                                    cbSaturday2.setChecked(true);
                                    if (saturdays[i].equalsIgnoreCase("full")) {
                                        sat2 = false;
                                        txtSat2F.setVisibility(View.VISIBLE);
                                        txtSat2H.setVisibility(View.INVISIBLE);
                                    } else if (saturdays[i].equalsIgnoreCase("half")) {
                                        sat2 = true;
                                        txtSat2F.setVisibility(View.INVISIBLE);
                                        txtSat2H.setVisibility(View.VISIBLE);
                                    }
                                }
                                break;

                            case 2:
                                if (saturdays[i].equalsIgnoreCase("off")) {
                                    cbSaturday3.setChecked(false);
                                } else {
                                    cbSaturday3.setChecked(true);
                                    if (saturdays[i].equalsIgnoreCase("full")) {
                                        sat3 = false;
                                        txtSat3F.setVisibility(View.VISIBLE);
                                        txtSat3H.setVisibility(View.INVISIBLE);
                                    } else if (saturdays[i].equalsIgnoreCase("half")) {
                                        sat3 = true;
                                        txtSat3F.setVisibility(View.INVISIBLE);
                                        txtSat3H.setVisibility(View.VISIBLE);
                                    }
                                }
                                break;

                            case 3:
                                if (saturdays[i].equalsIgnoreCase("off")) {
                                    cbSaturday4.setChecked(false);
                                } else {
                                    cbSaturday4.setChecked(true);
                                    if (saturdays[i].equalsIgnoreCase("full")) {
                                        sat4 = false;
                                        txtSat4F.setVisibility(View.VISIBLE);
                                        txtSat4H.setVisibility(View.INVISIBLE);
                                    } else if (saturdays[i].equalsIgnoreCase("half")) {
                                        sat4 = true;
                                        txtSat4F.setVisibility(View.INVISIBLE);
                                        txtSat4H.setVisibility(View.VISIBLE);
                                    }
                                }
                                break;

                            case 4:
                                if (saturdays[i].equalsIgnoreCase("off")) {
                                    cbSaturday5.setChecked(false);
                                } else {
                                    cbSaturday5.setChecked(true);
                                    if (saturdays[i].equalsIgnoreCase("full")) {
                                        sat5 = false;
                                        txtSat5F.setVisibility(View.VISIBLE);
                                        txtSat5H.setVisibility(View.INVISIBLE);
                                    } else if (saturdays[i].equalsIgnoreCase("half")) {
                                        sat5 = true;
                                        txtSat5F.setVisibility(View.INVISIBLE);
                                        txtSat5H.setVisibility(View.VISIBLE);
                                    }
                                }
                                break;
                        }
                    }

                } else {
                    rl_saturdays.setVisibility(View.GONE);
                    bt_saturday_policy_enabled.setChecked(false);
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    boolean isValidate() {

        int count = 0;
        for (int i = 0; i < working_days.length; i++) {
            if (working_days[i] == 1) count++;
        }
        if (count == 0) {
            toast(getResources().getString(R.string.select_workingdays));
            return false;
        }

        if (startCalendar.after(endCalendar)) {
            toast(getResources().getString(R.string.start_time_less));
            return false;
        }

        if (startCalendatTracking.after(endCalendarTracking)) {
            toast(getResources().getString(R.string.start_time_less));
            return false;
        }

        if (getTextInt(etMinimumHours) > 23) {
            toast(getResources().getString(R.string.min_hrs_less_than));
            return false;
        }

        if (getTextInt(etMinimumMinutes) > 59) {
            toast(getResources().getString(R.string.min_minutes_less_than));
            return false;
        }

        if (getTextInt(etAVGHours) > 23) {
            toast(getResources().getString(R.string.avg_hrs_less_than));
            return false;
        }

        if (getTextInt(etAVGMinutes) > 59) {
            toast(getResources().getString(R.string.avg_minutes_less_than));
            return false;
        }

        return true;
    }

    protected void toast(CharSequence text) {
        Toast.makeText(activity, text, Toast.LENGTH_SHORT).show();
    }

    protected void toast(int resId) {
        toast(this.getResources().getText(resId));
    }

    protected String getText(EditText eTxt) {
        return eTxt == null ? "" : eTxt.getText().toString().trim();
    }

    protected int getTextInt(EditText eTxt) {
        if (eTxt.getText() != null && eTxt.getText().length() > 0) {
            return Integer.parseInt(eTxt.getText().toString().trim());
        } else {
            return 0;
        }
    }

    protected boolean isEmpty(String s) {
        return s == null || s.isEmpty();
    }

    protected String read(String key) {
        return Util.ReadSharePrefrence(activity, key);
    }

    protected void write(String key, String val) {
        Util.WriteSharePrefrence(activity, key, val);
    }

    private void setBlank() {
        if (etMinimumHours.getText().toString().trim().equals("")) {
            etMinimumHours.setText("00");
        }

        if (etMinimumMinutes.getText().toString().trim().equals("")) {
            etMinimumMinutes.setText("00");
        }

        if (etAVGHours.getText().toString().trim().equals("")) {
            etAVGHours.setText("00");
        }

        if (etAVGMinutes.getText().toString().trim().equals("")) {
            etAVGMinutes.setText("00");
        }
    }

    public class updateCompanyInfo extends AsyncTask<Void, String, String> {

        JSONObject jobj;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (progressDialog != null) progressDialog.show();

            setBlank();
        }

        public updateCompanyInfo(JSONObject jobj) {
            this.jobj = jobj;
        }

        @Override
        protected String doInBackground(Void... params) {
            String resp = "";

            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
//            params1.add(new BasicNameValuePair("JSON", read(Constant.SHRED_PR.KEY_COMPANY_SETUP_DATA)));
            params1.add(new BasicNameValuePair("JSON", jobj.toString()));
//            params1.add(new BasicNameValuePair("name", Util.ReadSharePrefrence(getActivity(), Constant.SHRED_PR.KEY_COMPANY_NAME)));
            Log.e("params1", ">>" + params1);
            String response = Util.makeServiceCall(Constant.URL + "updateCompanyInfo", 2, params1, activity);

            return response;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.e("Response", ">>" + result);
            if (progressDialog != null)
                if (progressDialog.isShowing()) progressDialog.dismiss();

            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = jsonObject.getString("status");
                if (status.equals("Success")) {
                    JSONObject jsondata = jsonObject.optJSONObject("data");
                    write(Constant.SHRED_PR.KEY_COMPANY_SETUP_DATA, "" + jsonObject.optString("data"));
                    write(Constant.SHRED_PR.KEY_COMPANY_STARTTIME, jsondata.optString("startTime"));
                    write(Constant.SHRED_PR.KEY_COMPANY_ENDTIME, jsondata.optString("endTime"));
                    if (FROM == Constant.FROM_DASHBOARD) {
                        activity.finish();
                        activity.overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
                    } else {
                        CompanySetupActivity.currentPosition++;
                        CompanySetupActivity.pager.setCurrentItem(CompanySetupActivity.currentPosition);
                    }
                }
                Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
