package com.bigbang.superteam.leave_attendance;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.FragmentAdapter;
import com.bigbang.superteam.fragment.AttendanceFragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 7 on 7/24/2015.
 */
public class LeaveAttendanceManager extends FragmentActivity {
    FragmentAdapter pageAdapter;

    @InjectView(R.id.viewpagerUserHistory)
    ViewPager pager;

    Context context = this;
    String TAG = "LeaveAttendanceManager";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_leaveattendance);
        ButterKnife.inject(this);
        List<Fragment> fragments = getFragments();

        pageAdapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
        pager.setAdapter(pageAdapter);

        int pageno = getIntent().getIntExtra("pageno", 0);
        pager.setCurrentItem(pageno);
    }


    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<Fragment>();
        fList.add(AttendanceFragment.newInstance("1", this));

        return fList;

    }

}
