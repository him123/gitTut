package com.bigbang.superteam.leave_attendance;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;

import com.bigbang.superteam.R;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.fragment.LeaveHistoryUserFragment;
import com.bigbang.superteam.util.Constant;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by USER 7 on 11/6/2015.
 */

public class LeaveStatusAdminActivity extends BaseActivity {
    String userId;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leavehistory_for_one_user);
        ButterKnife.inject(this);

        Intent i = getIntent();
        userId = i.getStringExtra("userID");


        LeaveHistoryUserFragment leaveHistoryUserFragment = new LeaveHistoryUserFragment().newInstance(""+userId,LeaveStatusAdminActivity.this, Constant.FROM_ADMIN_DASHBOARD);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, leaveHistoryUserFragment).commit();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }


    @OnClick(R.id.rlBack)
    void backPressed() {
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }
}
