package com.bigbang.superteam.user;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.expenses.ExpensesUserFragment;
import com.bigbang.superteam.fragment.ApprovalsFragment;
import com.bigbang.superteam.fragment.AttendanceHistoryUserFragment;
import com.bigbang.superteam.fragment.LeaveHistoryUserFragment;
import com.bigbang.superteam.fragment.NotificationFragment;
import com.bigbang.superteam.fragment.ProjectFragment;
import com.bigbang.superteam.fragment.SettingsFragment;
import com.bigbang.superteam.fragment.TasksFragment;
import com.bigbang.superteam.payroll.PayrollFragment;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.GPSTracker;
import com.bigbang.superteam.util.Util;
import com.bigbang.superteam.workitem.FilterTasksActivity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by USER 8 on 02-Nov-15.
 */
public class UserDashboardNewActivity extends FragmentActivity {

    public static android.support.v4.app.FragmentManager fragmentManager;
    private static Map<Integer, Integer> ID_CLASS = new HashMap<>();

    static {
        ID_CLASS.put(R.id.rlTasks, 0);
        ID_CLASS.put(R.id.rlProjects, 1);
        ID_CLASS.put(R.id.rlAttendanceLeaves, 2);
        ID_CLASS.put(R.id.rlApprovals, 3);
        ID_CLASS.put(R.id.rlNotifications, 4);
        ID_CLASS.put(R.id.rlSettings, 5);
        ID_CLASS.put(R.id.rlExpenses, 6);
        ID_CLASS.put(R.id.rlPayroll, 7);
    }

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
    @InjectView(R.id.viewAttendance)
    View viewAttendance;
    @InjectView(R.id.viewLeaves)
    View viewLeaves;
    @InjectView(R.id.tvAttendance)
    TextView tvAttendance;
    @InjectView(R.id.tvLeaves)
    TextView tvLeaves;
    FragmentTransaction fragmentTransaction;
    Fragment fragment;

    int selectedTab = -1;
    Animation animation, animation1;
    boolean flag = false;
    static IntentFilter s_intentFilter;

    static {
        s_intentFilter = new IntentFilter();
        s_intentFilter.addAction(Intent.ACTION_TIME_TICK);
        s_intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        s_intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        unregisterReceiver(m_timeChangedReceiver);
//    }

//    private final BroadcastReceiver m_timeChangedReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            final String action = intent.getAction();
//
//            if (action.equals(Intent.ACTION_TIME_CHANGED) ||
//                    action.equals(Intent.ACTION_TIMEZONE_CHANGED))
//            {
//                toast("Time changed manuamlly");
//            }
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_dashboardnew);
        ButterKnife.inject(this);

        int tab = getIntent().getIntExtra("pageno", 0);

//        GPSTracker gps = new GPSTracker(UserDashboardNewActivity.this);
//        Double curLat = gps.getLatitude();
//        Double curLng = gps.getLongitude();


//        registerReceiver(m_timeChangedReceiver, s_intentFilter);

        init();
        openMenu(tab);
        Calendar calendar = Calendar.getInstance();
//        String s1 = ""+calendar.get(Calendar.MINUTE);
//        String s2 = ""+calendar.get(Calendar.DAY_OF_MONTH);
//        String s3 = ""+calendar.get(Calendar.DAY_OF_WEEK);
//        calendar.getActualMaximum(Calendar.DAY_OF_MONTH);


//        Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (!Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_IS_SERVICE_ON).equals("true")) {
//                    Util.registerUserTrackingReceiver_HalfMinute(UserDashboardNewActivity.this);
//                    Util.WriteSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_IS_SERVICE_ON, "true");
//                }
//            }
//        }, 1000);

//        MyLocation.LocationResult locationResult = new MyLocation.LocationResult() {
//            @Override
//            public void gotLocation(Location location) {
//                //Got the location!
//
//                toast("Check location : Latitude is: "+location.getLatitude()+" Longitude is: "+location.getLongitude());
//
//            }
//        };
//
//        MyLocation myLocation = new MyLocation();
//        myLocation.getLocation(this, locationResult);
//        myLocation.stopTimer();

        Util.registerUserTrackingReceiver(UserDashboardNewActivity.this);
        Util.registerHeartbeatReceiver(getApplicationContext());

        // returns true if mock location enabled, false if not enabled.
//        if (Settings.Secure.getString(getContentResolver(),
//                Settings.Secure.ALLOW_MOCK_LOCATION).equals("0")) {
////            return false;
//            toast("Mock found disbled");
//        }else {
////            return true;
//            toast("Mock found enabled");
//        }
    }



    @Override
    protected void onResume() {
        super.onResume();

        flag = false;
        GPSTracker gps = new GPSTracker(UserDashboardNewActivity.this);
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

    @OnClick({R.id.rlTasks, R.id.rlProjects,
            R.id.rlAttendanceLeaves, R.id.rlApprovals,
            R.id.rlNotifications,
            R.id.rlSettings,
            R.id.rlPayroll,
            R.id.rlExpenses})
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

        fragment = new AttendanceHistoryUserFragment();
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

        // fragment = new LeaveHistoryUserFragment();
        fragment = new LeaveHistoryUserFragment().newInstance("", UserDashboardNewActivity.this, Constant.FROM_USER_DASHBOARD);


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

    @OnClick(R.id.rlFilter)
    void Filter() {
        startActivity(new Intent(UserDashboardNewActivity.this, FilterTasksActivity.class));
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
                    fragment = new AttendanceHistoryUserFragment();
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
                    fragment = new NotificationFragment();
                    tvTitle.setText(getResources().getString(R.string.notifications));
                    break;
                case 5:
                    fragment = new SettingsFragment();
                    tvTitle.setText(getResources().getString(R.string.settings));
                    break;
                case 6: //Expenses
                    fragment = new ExpensesUserFragment().newInstance(fragmentManager);
                    tvTitle.setText(getResources().getString(R.string.expenses));
                    break;
                case 7: //Payroll
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

    protected void toast(CharSequence text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
