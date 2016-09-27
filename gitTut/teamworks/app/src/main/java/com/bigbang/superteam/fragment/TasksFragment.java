package com.bigbang.superteam.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.FragmentAdapter;
import com.bigbang.superteam.util.Util;
import com.bigbang.superteam.workitem.CreateWorkActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by USER 8 on 02-Nov-15.
 */
public class TasksFragment extends Fragment {

    @InjectView(R.id.viewActive)
    View viewActive;
    @InjectView(R.id.viewArchived)
    View viewArchived;
    @InjectView(R.id.viewPending)
    View viewPending;
    @InjectView(R.id.tvActive)
    TextView tvActive;
    @InjectView(R.id.tvArchived)
    TextView tvArchived;
    @InjectView(R.id.tvPending)
    TextView tvPending;

    static android.support.v4.app.FragmentManager fragmentManager;
    MyReceiver myReceiver;
    FragmentAdapter pageAdapter;
    View v;
    public static ViewPager pager;
    public static int currentPosition = 0;
    Activity activity;

    public static Fragment newInstance(android.support.v4.app.FragmentManager fragmentManager1) {
        TasksFragment fragment = new TasksFragment();
        fragmentManager = fragmentManager1;
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity=getActivity();
        View v = inflater.inflate(R.layout.fragment_tasks, container, false);
        ButterKnife.inject(this, v);
        final Typeface mFont = Typeface.createFromAsset(activity.getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) v.getRootView();
        Util.setAppFont(mContainer, mFont);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(fragmentManager!=null)
        init();

    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            myReceiver = new MyReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(activity.ACTIVITY_SERVICE);
            activity.registerReceiver(myReceiver, intentFilter);
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        try {
            activity.unregisterReceiver(myReceiver);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @OnClick(R.id.rlCreate)
    @SuppressWarnings("unused")
    public void Create(View view) {
        startActivity(new Intent(activity, CreateWorkActivity.class));
        activity.overridePendingTransition(R.anim.enter_from_left,
                R.anim.hold_bottom);
    }

    @OnClick(R.id.rlPending)
    @SuppressWarnings("unused")
    public void Pending(View view) {
        pager.setCurrentItem(2);
    }

    @OnClick(R.id.rlArchived)
    @SuppressWarnings("unused")
    public void Archived(View view) {
        pager.setCurrentItem(1);
    }

    @OnClick(R.id.rlActive)
    @SuppressWarnings("unused")
    public void Active(View view) {
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
                    if (type.equals("active")) {
                        tvActive.setText(getResources().getString(R.string.active) + " (" + count + ")");
                    } else if (type.equals("archived")) {
                        tvArchived.setText(getResources().getString(R.string.archived) + " (" + count + ")");
                    }else if (type.equals("pending")) {
                        tvPending.setText(getResources().getString(R.string.pending_) + " (" + count + ")");
                    }
                }
            }
        }
    }

    private void init() {

        pager = (ViewPager) getView().findViewById(R.id.viewpagerDemo);
        final List<android.support.v4.app.Fragment> fragmentList = getFragments();
        pageAdapter = new FragmentAdapter(fragmentManager, fragmentList);
        pager.setAdapter(pageAdapter);
        pager.setCurrentItem(0);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int pageNo) {
                currentPosition = pageNo;
                switch (currentPosition) {
                    case 0:
                        viewActive.setVisibility(View.VISIBLE);
                        viewArchived.setVisibility(View.GONE);
                        viewPending.setVisibility(View.GONE);
                        tvActive.setTextColor(getResources().getColor(R.color.white));
                        tvArchived.setTextColor(getResources().getColor(R.color.light_gray));
                        tvPending.setTextColor(getResources().getColor(R.color.light_gray));
                        break;
                    case 1:
                        viewActive.setVisibility(View.GONE);
                        viewArchived.setVisibility(View.VISIBLE);
                        viewPending.setVisibility(View.GONE);
                        tvActive.setTextColor(getResources().getColor(R.color.light_gray));
                        tvArchived.setTextColor(getResources().getColor(R.color.white));
                        tvPending.setTextColor(getResources().getColor(R.color.light_gray));
                        break;
                    case 2:
                        viewActive.setVisibility(View.GONE);
                        viewArchived.setVisibility(View.GONE);
                        viewPending.setVisibility(View.VISIBLE);
                        tvActive.setTextColor(getResources().getColor(R.color.light_gray));
                        tvArchived.setTextColor(getResources().getColor(R.color.light_gray));
                        tvPending.setTextColor(getResources().getColor(R.color.white));
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

    private List<android.support.v4.app.Fragment> getFragments() {
        List<android.support.v4.app.Fragment> fragmentList = new ArrayList<android.support.v4.app.Fragment>();
        fragmentList.add(ActiveTasksFragmentBuild.newInstance());
        fragmentList.add(ArchivedTasksFragment.newInstance());
        fragmentList.add(PendingTasksFragment.newInstance());

        return fragmentList;
    }

}