package com.bigbang.superteam.user;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.FragmentAdapter;
import com.bigbang.superteam.fragment.ExisitingUsersFragment;
import com.bigbang.superteam.fragment.InvitedUsersFragment;
import com.bigbang.superteam.util.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class UsersActivity extends FragmentActivity {

    @InjectView(R.id.viewExisting)
    View viewExisting;
    @InjectView(R.id.viewInvited)
    View viewInvited;
    @InjectView(R.id.tvExisting)
    TextView tvExisting;
    @InjectView(R.id.tvInvited)
    TextView tvInvited;

    MyReceiver myReceiver;
    FragmentAdapter pageAdapter;
    View v;
    public static ViewPager pager;
    public static int currentPosition = 0;
    public static FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_users);
        ButterKnife.inject(this);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            myReceiver = new MyReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTIVITY_SERVICE);
            registerReceiver(myReceiver, intentFilter);
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        try {
            unregisterReceiver(myReceiver);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }


    @OnClick(R.id.rlBack)
    @SuppressWarnings("unused")
    public void Back(View view) {
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }


    @OnClick(R.id.rlInvite)
    @SuppressWarnings("unused")
    public void addUser(View view) {
        startActivity(InviteUserActivity.class);
        overridePendingTransition(R.anim.enter_from_bottom,
                R.anim.hold_bottom);
    }

    @OnClick(R.id.rlInvited)
    @SuppressWarnings("unused")
    public void Invited(View view) {
        pager.setCurrentItem(1);
    }

    @OnClick(R.id.rlExisting)
    @SuppressWarnings("unused")
    public void Existing(View view) {
        pager.setCurrentItem(0);
    }

    private class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            Bundle extras = arg1.getExtras();
            if (extras != null) {
                String type = extras.getString("type");
                String count = extras.getString("count");
                if (type != null && count != null) {
                    if (type.equals("existing")) {
                        tvExisting.setText(getResources().getString(R.string.exising) + " (" + count + ")");
                    } else if (type.equals("invited")) {
                        tvInvited.setText(getResources().getString(R.string.invited) + " (" + count + ")");
                    }
                }
            }
        }
    }

    private void init() {

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        fragmentManager = getFragmentManager();
        pager = (ViewPager) findViewById(R.id.viewpagerDemo);
        final List<Fragment> fragmentList = getFragments();
        pageAdapter = new FragmentAdapter(getSupportFragmentManager(), fragmentList);
        pager.setAdapter(pageAdapter);
        pager.setCurrentItem(0);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int pageNo) {
                currentPosition = pageNo;
                switch (currentPosition) {
                    case 0:
                        viewExisting.setVisibility(View.VISIBLE);
                        viewInvited.setVisibility(View.GONE);
                        tvExisting.setTextColor(getResources().getColor(R.color.white));
                        tvInvited.setTextColor(getResources().getColor(R.color.light_gray));
                        break;
                    case 1:
                        viewExisting.setVisibility(View.GONE);
                        viewInvited.setVisibility(View.VISIBLE);
                        tvExisting.setTextColor(getResources().getColor(R.color.light_gray));
                        tvInvited.setTextColor(getResources().getColor(R.color.white));
                        break;
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
    }

    private List<Fragment> getFragments() {
        List<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(ExisitingUsersFragment.newInstance());
        fragmentList.add(InvitedUsersFragment.newInstance());

        return fragmentList;
    }

    protected void startActivity(Class klass) {
        startActivity(new Intent(this, klass));
    }

}
