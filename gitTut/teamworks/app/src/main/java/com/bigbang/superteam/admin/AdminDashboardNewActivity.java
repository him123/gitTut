package com.bigbang.superteam.admin;

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
import com.bigbang.superteam.expenses.ExpenseAdminFragment;
import com.bigbang.superteam.expenses.ExpenseSuperAdminFragment;
import com.bigbang.superteam.fragment.ApprovalsFragment;
import com.bigbang.superteam.fragment.AttendanceHistoryAdminFragment;
import com.bigbang.superteam.fragment.ExportFragment;
import com.bigbang.superteam.fragment.LeaveHistoryAdminFragment;
import com.bigbang.superteam.fragment.NotificationFragment;
import com.bigbang.superteam.fragment.ProjectFragment;
import com.bigbang.superteam.fragment.SettingsFragment;
import com.bigbang.superteam.fragment.TasksFragment;
import com.bigbang.superteam.fragment.TrackingFragment;
import com.bigbang.superteam.payroll.PaySlipAdminActivity;
import com.bigbang.superteam.payroll.PaySlipAdminFragment;
import com.bigbang.superteam.payroll.PayrollFragment;
import com.bigbang.superteam.util.Constant;
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

public class AdminDashboardNewActivity extends FragmentActivity {

    public static android.support.v4.app.FragmentManager fragmentManager;
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

    @InjectView(R.id.frameLayout)
    FrameLayout frameLayout;
    @InjectView(R.id.tvTitle)
    TextView tvTitle;
    @InjectView(R.id.rlMenuBox)
    RelativeLayout rlMenuBox;
    @InjectView(R.id.rlPayroll)
    RelativeLayout rlPayroll;

    //Task
    @InjectView(R.id.rlFilter)
    RelativeLayout rlFilter;

    //Attendance & Leaves
    @InjectView(R.id.rlAttendanceLeavesHeader)
    RelativeLayout rlAttendanceLeavesHeader;
    @InjectView(R.id.viewAttendance)
    View viewAttendance;
    @InjectView(R.id.viewLeaves)
    View viewLeaves;
    @InjectView(R.id.tvAttendance)
    TextView tvAttendance;
    @InjectView(R.id.tvLeaves)
    TextView tvLeaves;

    //Payroll
    @InjectView(R.id.rlEveryone)
    RelativeLayout rlEveryone;
    @InjectView(R.id.imgEveryone)
    ImageButton imgEveryone;
    boolean flagEveryone = false;

    //Tracking:
    @InjectView(R.id.rlTrackingDate)
    RelativeLayout rlTrackingDate;
    @InjectView(R.id.tvTrackingDate)
    TextView tvTrackingDate;
    @InjectView(R.id.tvTrackingYear)
    TextView tvTrackingYear;
    Calendar calendar;
    int curMonth, curYear, curDate;
    FragmentTransaction fragmentTransaction;
    Fragment fragment;
    int selectedTab = -1;
    Animation animation, animation1;
    boolean flag = false;

    String roleId = "0";

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
                intent1.setAction(AdminDashboardNewActivity.ACTIVITY_SERVICE);
                intent1.putExtra("trackingDate", ""
                        + date);
                sendBroadcast(intent1);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard_new);
        ButterKnife.inject(this);

        roleId = Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_ROLE_ID);

        int tab = getIntent().getIntExtra("pageno", 0);

        init();
        openMenu(tab);

        Util.registerHeartbeatReceiver(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        flag = false;
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


    @OnClick(R.id.rlAttendance)
    @SuppressWarnings("unused")
    public void Attendance(View view) {

        viewAttendance.setVisibility(View.VISIBLE);
        viewLeaves.setVisibility(View.GONE);
        tvAttendance.setTextColor(getResources().getColor(R.color.white));
        tvLeaves.setTextColor(getResources().getColor(R.color.light_gray));

        fragment = new AttendanceHistoryAdminFragment();
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

        viewAttendance.setVisibility(View.GONE);
        viewLeaves.setVisibility(View.VISIBLE);
        tvAttendance.setTextColor(getResources().getColor(R.color.light_gray));
        tvLeaves.setTextColor(getResources().getColor(R.color.white));

        fragment = new LeaveHistoryAdminFragment();
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

        DatePickerDialog dialog = new DatePickerDialog(AdminDashboardNewActivity.this, DatePickerDialog.THEME_HOLO_LIGHT, mytoDateListener, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DATE));
        dialog.getDatePicker().setMaxDate(new Date().getTime());
        dialog.show();
    }

    @OnClick(R.id.rlFilter)
    void Filter() {
        startActivity(new Intent(AdminDashboardNewActivity.this, FilterTasksActivity.class));
        overridePendingTransition(R.anim.enter_from_bottom,
                R.anim.hold_bottom);
    }

    @OnClick(R.id.rlEveryone)
    public void Everyone(View view) {

        if (flagEveryone) {
            flagEveryone = false;
            imgEveryone.setBackgroundResource(R.drawable.task_delegate);
        } else {
            flagEveryone = true;
            imgEveryone.setBackgroundResource(R.drawable.nav_everyone);
        }
        selectedTab=-1;
        openMenu(9);
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
            rlEveryone.setVisibility(View.GONE);

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
                    fragment = new AttendanceHistoryAdminFragment();
                    tvTitle.setText("");
                    rlAttendanceLeavesHeader.setVisibility(View.VISIBLE);

                    viewAttendance.setVisibility(View.VISIBLE);
                    viewLeaves.setVisibility(View.GONE);
                    tvAttendance.setTextColor(getResources().getColor(R.color.white));
                    tvLeaves.setTextColor(getResources().getColor(R.color.light_gray));

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
                case 8:
                    if (roleId.equalsIgnoreCase("1"))
                        fragment = new ExpenseSuperAdminFragment().newInstance(fragmentManager);
                    else
                        fragment = new ExpenseAdminFragment().newInstance(fragmentManager);
                    tvTitle.setText(getResources().getString(R.string.expenses));
                    break;
                case 9:
                    if(roleId.equalsIgnoreCase("1")){
                        fragment = new PaySlipAdminFragment().newInstance(fragmentManager);
                    }else {
                        rlEveryone.setVisibility(View.VISIBLE);
                        if (flagEveryone) {
                            fragment = new PayrollFragment().newInstance(fragmentManager);
                        } else {
                            fragment = new PaySlipAdminFragment().newInstance(fragmentManager);
                        }
                    }
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
        }
    }

    protected void toast(CharSequence text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

}
