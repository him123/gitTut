package com.bigbang.superteam.manager;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.expenses.ExpensesUserFragment;
import com.bigbang.superteam.fragment.ApprovalsFragment;
import com.bigbang.superteam.fragment.AttendanceHistoryAdminFragment;
import com.bigbang.superteam.fragment.AttendanceHistoryUserFragment;
import com.bigbang.superteam.fragment.ExportFragment;
import com.bigbang.superteam.fragment.LeaveHistoryAdminFragment;
import com.bigbang.superteam.fragment.LeaveHistoryUserFragment;
import com.bigbang.superteam.fragment.NotificationFragment;
import com.bigbang.superteam.fragment.ProjectFragment;
import com.bigbang.superteam.fragment.SettingsFragment;
import com.bigbang.superteam.fragment.TasksFragment;
import com.bigbang.superteam.fragment.TrackingFragment;
import com.bigbang.superteam.payroll.PayrollFragment;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.GPSTracker;
import com.bigbang.superteam.util.Util;
import com.bigbang.superteam.workitem.FilterTasksActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by USER 8 on 02-Nov-15.
 */
public class ManagerDashboardNewActivity extends FragmentActivity {

    @InjectView(R.id.frameLayout)
    FrameLayout frameLayout;
    @InjectView(R.id.tvTitle)
    TextView tvTitle;
    @InjectView(R.id.rlMenuBox)
    RelativeLayout rlMenuBox;

    //Task
    @InjectView(R.id.rlFilter)
    RelativeLayout rlFilter;

    //Attendance & Leaves
    @InjectView(R.id.rlAttendanceLeavesHeader)
    RelativeLayout rlAttendanceLeavesHeader;
    @InjectView(R.id.rlEveryone)
    RelativeLayout rlEveryone;
    @InjectView(R.id.viewAttendance)
    View viewAttendance;
    @InjectView(R.id.viewLeaves)
    View viewLeaves;
    @InjectView(R.id.tvAttendance)
    TextView tvAttendance;
    @InjectView(R.id.tvLeaves)
    TextView tvLeaves;
    @InjectView(R.id.imgEveryone)
    ImageButton imgEveryone;
    boolean flagEveryone = false;
    boolean flagLeave = false;

    //Tracking:
    @InjectView(R.id.rlTrackingDate)
    RelativeLayout rlTrackingDate;
    @InjectView(R.id.tvTrackingDate)
    TextView tvTrackingDate;
    @InjectView(R.id.tvTrackingYear)
    TextView tvTrackingYear;
    Calendar calendar;
    int curMonth, curYear, curDate;

    private static Map<Integer, Integer> ID_CLASS = new HashMap<>();

    static {
        ID_CLASS.put(R.id.rlTasks, 0);
        ID_CLASS.put(R.id.rlProjects, 1);
        ID_CLASS.put(R.id.rlAttendanceLeaves, 2);
        ID_CLASS.put(R.id.rlApprovals, 3);
        ID_CLASS.put(R.id.rlTracking, 4);
        ID_CLASS.put(R.id.rlNotifications, 5);
        ID_CLASS.put(R.id.rlSettings, 6);
        ID_CLASS.put(R.id.rlExport, 7);
        ID_CLASS.put(R.id.rlExpenses, 8);
        ID_CLASS.put(R.id.rlPayroll, 9);
    }

    FragmentTransaction fragmentTransaction;
    Fragment fragment;
    public static android.support.v4.app.FragmentManager fragmentManager;

    int selectedTab = -1;
    Animation animation, animation1;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_dashboardnew);
        ButterKnife.inject(this);

        int tab = getIntent().getIntExtra("pageno", 0);

        init();
        openMenu(tab);


        Util.registerUserTrackingReceiver(ManagerDashboardNewActivity.this);
        Util.registerHeartbeatReceiver(getApplicationContext());

    }

    @Override
    protected void onResume() {
        super.onResume();

        flag = false;
        GPSTracker gps = new GPSTracker(ManagerDashboardNewActivity.this);
        if (!gps.canGetLocation()) {
            gps.showSettingsAlert();
        }
    }

    @Override
    public void onBackPressed() {
        if (rlMenuBox.getVisibility() == View.VISIBLE) {
            rlMenuBox.setVisibility(View.GONE);
            rlMenuBox.startAnimation(animation1);
            frameLayout.setAlpha(1);
        } else {
            if (flag) {
                super.onBackPressed();
                finish();
            } else {
                toast(getResources().getString(R.string.back_press));
            }
            flag = true;
        }
    }


    @OnClick(R.id.rlMenu)
    @SuppressWarnings("unused")
    public void Menu(View view) {
        if (rlMenuBox.getVisibility() == View.VISIBLE) {
            rlMenuBox.setVisibility(View.GONE);
            rlMenuBox.startAnimation(animation1);
            frameLayout.setAlpha(1);
        } else {
            frameLayout.setAlpha(0.5f);
            rlMenuBox.setVisibility(View.VISIBLE);
            rlMenuBox.startAnimation(animation);
        }
    }

    @OnClick({R.id.rlTasks, R.id.rlProjects, R.id.rlTracking,
            R.id.rlAttendanceLeaves, R.id.rlApprovals,
            R.id.rlNotifications,
            R.id.rlSettings, R.id.rlExport, R.id.rlPayroll, R.id.rlExpenses})
    @SuppressWarnings("unused")
    public void openDetails(View view) {
        openMenu(ID_CLASS.get(view.getId()));
    }

    @OnClick(R.id.rlEveryone)
    @SuppressWarnings("unused")
    public void Everyone(View view) {

        if (flagEveryone) {
            flagEveryone = false;
            imgEveryone.setBackgroundResource(R.drawable.task_delegate);
        } else {
            flagEveryone = true;
            imgEveryone.setBackgroundResource(R.drawable.nav_everyone);
        }

        selectedTab=-1;
        openMenu(2);
    }

    @OnClick(R.id.rlAttendance)
    @SuppressWarnings("unused")
    public void Attendance(View view) {

        flagLeave = false;
        viewAttendance.setVisibility(View.VISIBLE);
        viewLeaves.setVisibility(View.GONE);
        tvAttendance.setTextColor(getResources().getColor(R.color.white));
        tvLeaves.setTextColor(getResources().getColor(R.color.light_gray));

        if (flagEveryone) {
            fragment = new AttendanceHistoryUserFragment();
        } else {
            fragment = new AttendanceHistoryAdminFragment();
        }
        FragmentManager fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment).commit();

        flag = false;
        if (rlMenuBox.getVisibility() == View.VISIBLE) {
            //close menu
            rlMenuBox.setVisibility(View.GONE);
            rlMenuBox.startAnimation(animation1);
        }
        frameLayout.setAlpha(1);
    }

    @OnClick(R.id.rlLeaves)
    @SuppressWarnings("unused")
    public void Leaves(View view) {

        flagLeave = true;
        viewAttendance.setVisibility(View.GONE);
        viewLeaves.setVisibility(View.VISIBLE);
        tvAttendance.setTextColor(getResources().getColor(R.color.light_gray));
        tvLeaves.setTextColor(getResources().getColor(R.color.white));

        if (flagEveryone) {
           // fragment = new LeaveHistoryUserFragment();
            fragment =new LeaveHistoryUserFragment().newInstance("",ManagerDashboardNewActivity.this, Constant.FROM_MANAGER_DASHBOARD);

        } else {
            fragment = new LeaveHistoryAdminFragment();
        }
        FragmentManager fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment).commit();

        flag = false;
        if (rlMenuBox.getVisibility() == View.VISIBLE) {
            //close menu
            rlMenuBox.setVisibility(View.GONE);
            rlMenuBox.startAnimation(animation1);
        }
        frameLayout.setAlpha(1);
    }


    @OnClick(R.id.rlTrackingDate)
    void selectDate() {
        try {

            calendar = Calendar.getInstance();
            curMonth = calendar.get(calendar.MONTH);
            curYear = calendar.get(calendar.YEAR);
            curDate = calendar.get(calendar.DAY_OF_MONTH);

            String date = "" + tvTrackingDate.getText().toString() + tvTrackingYear.getText().toString();
            SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM, yyyy");
            date = sdf1.format(sdf2.parse(date));

            String[] dates = date.split("/");
            calendar.set(Calendar.YEAR, Integer.parseInt(dates[2]));
            calendar.set(Calendar.MONTH, Integer.parseInt(dates[1]) - 1);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dates[0]));
        } catch (Exception e) {
            e.printStackTrace();
        }

        DatePickerDialog dialog = new DatePickerDialog(ManagerDashboardNewActivity.this,DatePickerDialog.THEME_HOLO_LIGHT,mytoDateListener, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DATE));
        dialog.getDatePicker().setMaxDate(new Date().getTime());
        dialog.show();
    }

    @OnClick(R.id.rlFilter)
    void Filter() {
        startActivity(new Intent(ManagerDashboardNewActivity.this, FilterTasksActivity.class));
        overridePendingTransition(R.anim.enter_from_bottom,
                R.anim.hold_bottom);
    }

    private void init() {

        fragmentManager = getSupportFragmentManager();

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.enter_from_left_view);
        animation1 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.exit_left_view);

        rlMenuBox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (rlMenuBox.getVisibility() == View.VISIBLE) {
                    rlMenuBox.setVisibility(View.GONE);
                    rlMenuBox.startAnimation(animation1);
                    frameLayout.setAlpha(1);
                }
                return false;
            }
        });

    }

    private void openMenu(int pos) {

        flag = false;
        if (rlMenuBox.getVisibility() == View.VISIBLE) {
            //close menu
            rlMenuBox.setVisibility(View.GONE);
            rlMenuBox.startAnimation(animation1);
        }
        frameLayout.setAlpha(1);

        if (selectedTab != pos) {
            selectedTab = pos;

            rlAttendanceLeavesHeader.setVisibility(View.GONE);
            rlTrackingDate.setVisibility(View.GONE);
            rlFilter.setVisibility(View.GONE);

            switch (pos) {
                case 0:
                    fragment = new TasksFragment().newInstance(fragmentManager);
                    tvTitle.setText(getResources().getString(R.string.tasks));
                    rlFilter.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    fragment = new ProjectFragment();
                    tvTitle.setText(getResources().getString(R.string.projects));
                    break;
                case 2:
                    tvTitle.setText("");
                    rlAttendanceLeavesHeader.setVisibility(View.VISIBLE);

                    if (flagLeave) {

                        viewAttendance.setVisibility(View.GONE);
                        viewLeaves.setVisibility(View.VISIBLE);
                        tvAttendance.setTextColor(getResources().getColor(R.color.light_gray));
                        tvLeaves.setTextColor(getResources().getColor(R.color.white));

                        if (flagEveryone) {
                            //fragment = new LeaveHistoryUserFragment();
                            fragment =new LeaveHistoryUserFragment().newInstance("",ManagerDashboardNewActivity.this, Constant.FROM_MANAGER_DASHBOARD);

                        } else {
                            fragment = new LeaveHistoryAdminFragment();
                        }
                    } else {

                        viewAttendance.setVisibility(View.VISIBLE);
                        viewLeaves.setVisibility(View.GONE);
                        tvAttendance.setTextColor(getResources().getColor(R.color.white));
                        tvLeaves.setTextColor(getResources().getColor(R.color.light_gray));

                        if (flagEveryone) {
                            fragment = new AttendanceHistoryUserFragment();
                        } else {
                            fragment = new AttendanceHistoryAdminFragment();
                        }
                    }

                    break;
                case 3:
                    fragment = new ApprovalsFragment();
                    tvTitle.setText(getResources().getString(R.string.approvals));
                    break;
                case 4:

                    fragment = new TrackingFragment().newInstance(fragmentManager);
                    tvTitle.setText(getResources().getString(R.string.tracking));

                    rlTrackingDate.setVisibility(View.VISIBLE);
                    try {
                        calendar = Calendar.getInstance();
                        curMonth = calendar.get(calendar.MONTH);
                        curYear = calendar.get(calendar.YEAR);
                        curDate = calendar.get(calendar.DAY_OF_MONTH);

                        String date = "" + curDate + "/" + (curMonth + 1) + "/" + curYear;
                        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
                        SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM, ");
                        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy");
                        tvTrackingDate.setText("" + sdf2.format(sdf1.parse(date)).toUpperCase());
                        tvTrackingYear.setText("" + sdf3.format(sdf1.parse(date)));

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    break;
                case 5:
                    fragment = new NotificationFragment();
                    tvTitle.setText(getResources().getString(R.string.notifications));
                    break;
                case 6:
                    fragment = new SettingsFragment();
                    tvTitle.setText(getResources().getString(R.string.settings));
                    break;
                case 7:
                    fragment = new ExportFragment();
                    tvTitle.setText(getResources().getString(R.string.export));
                    break;
                case 8: //Expenses
                    fragment = new ExpensesUserFragment().newInstance(fragmentManager);
                    tvTitle.setText(getResources().getString(R.string.expenses));
                    break;
                case 9: //Payroll
                    fragment = new PayrollFragment().newInstance(fragmentManager);
                    tvTitle.setText(getResources().getString(R.string.payslip));
                    break;
                default:
                    fragment = new ApprovalsFragment();
                    tvTitle.setText(getResources().getString(R.string.tasks));
                    break;
            }

            FragmentManager fragmentManager = getFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, fragment).commit();
            frameLayout.setAlpha(1);
        }

    }

    private DatePickerDialog.OnDateSetListener mytoDateListener
            = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {

            try {
                String date = "" + year + "/" + (month + 1) + "/" + day;
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/dd");
                SimpleDateFormat sdf2 = new SimpleDateFormat("dd MMM, ");
                SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy");
                tvTrackingDate.setText("" + sdf2.format(sdf1.parse(date)).toUpperCase());
                tvTrackingYear.setText("" + sdf3.format(sdf1.parse(date)));

                Intent intent1 = new Intent();
                // intent.setAction(MY_ACTION);
                intent1.setAction(ManagerDashboardNewActivity.ACTIVITY_SERVICE);
                intent1.putExtra("trackingDate", ""
                        + date);
                sendBroadcast(intent1);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    protected void toast(CharSequence text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}

