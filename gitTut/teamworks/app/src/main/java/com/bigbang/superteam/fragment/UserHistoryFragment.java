package com.bigbang.superteam.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;

import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.FragmentAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 7 on 7/8/2015.
 */
public class UserHistoryFragment extends FragmentActivity implements View.OnClickListener {

    FragmentAdapter pageAdapter;
    View views[] = new View[2];
    RelativeLayout relativeLayouts[] = new RelativeLayout[2];

    @InjectView(R.id.viewpagerUserHistory)
    ViewPager pager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_history);
        ButterKnife.inject(this);
        Init();
        List<Fragment> fragments = getFragments();

        pageAdapter = new FragmentAdapter(getSupportFragmentManager(), fragments);
        pager.setAdapter(pageAdapter);
        pager.setOnPageChangeListener(myListener);

        int pageno = getIntent().getIntExtra("pageno", 0);
        pager.setCurrentItem(pageno);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_attendanceHistory:
                pager.setCurrentItem(0);
                break;
            case R.id.rl_leaveHistory:
                pager.setCurrentItem(1);
                break;

        }
    }


    private List<Fragment> getFragments() {
        List<Fragment> fList = new ArrayList<Fragment>();
        fList.add(AttendanceHistoryFragment.newInstance("1", this));
        fList.add(LeaveHistoryFragment.newInstance("2", this));

        return fList;

    }


    private void Init() {
        // pager = (ViewPager) findViewById(R.id.viewpagerUserDashboard);

        views[0] = (View) findViewById(R.id.view1);
        views[1] = (View) findViewById(R.id.view2);


        relativeLayouts[0] = (RelativeLayout) findViewById(R.id.rl_attendanceHistory);
        relativeLayouts[1] = (RelativeLayout) findViewById(R.id.rl_leaveHistory);

        for (int i = 0; i < 2; i++) {
            relativeLayouts[i].setOnClickListener(this);
        }
    }

    ViewPager.OnPageChangeListener myListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int pageNo) {
            for (int i = 0; i < 2; i++) {
                if (pageNo == i) {
                    views[i].setVisibility(View.VISIBLE);
                } else {
                    views[i].setVisibility(View.GONE);
                }
            }
        }

        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        public void onPageScrollStateChanged(int arg0) {
        }
    };
}