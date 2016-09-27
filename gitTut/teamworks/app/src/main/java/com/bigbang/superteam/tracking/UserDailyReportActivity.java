package com.bigbang.superteam.tracking;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.bigbang.superteam.R;
import com.bigbang.superteam.slidingmap.MainFragment;
import com.bigbang.superteam.util.GPSTracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import butterknife.ButterKnife;

public class UserDailyReportActivity extends FragmentActivity {

    GPSTracker gps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_daily_report);
        ButterKnife.inject(this);

        gps = new GPSTracker(UserDailyReportActivity.this);
        // Getting status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        // Showing status
        if (status == ConnectionResult.SUCCESS) {
            if (savedInstanceState == null) {
                Bundle extras = getIntent().getExtras();
                FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
                trans.add(R.id.fragment, MainFragment.newInstance(extras, getSupportFragmentManager()));
                trans.commit();
            }
        } else {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        gps = new GPSTracker(UserDailyReportActivity.this);
//        if (!gps.canGetLocation()) gps.showSettingsAlert();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }
}
