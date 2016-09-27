package com.bigbang.superteam.payroll;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.FragmentAdapter;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class PaySlipAdminActivity extends FragmentActivity {

    @InjectView(R.id.tvGenerate)
    TextView tvGenerate;
    @InjectView(R.id.tvPublish)
    TextView tvPublish;
    @InjectView(R.id.viewGenerate)
    View viewGenerate;
    @InjectView(R.id.viewPublish)
    View viewPublish;

    static android.support.v4.app.FragmentManager fragmentManager;
    FragmentAdapter pageAdapter;
    View v;
    public static ViewPager pager;
    public static int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_slip_admin);

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        ButterKnife.inject(this);

        //clear payslip month & year preferences
        Util.WriteSharePrefrenceForInteger(PaySlipAdminActivity.this, Constant.SHRED_PR.KEY_PAYSLIP_MONTH, 0);
        Util.WriteSharePrefrenceForInteger(PaySlipAdminActivity.this, Constant.SHRED_PR.KEY_PAYSLIP_YEAR, 0);

        fragmentManager = this.getSupportFragmentManager();

        if(fragmentManager!=null)
            init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Util.WriteSharePrefrenceForInteger(this, Constant.SHRED_PR.KEY_PAYSLIP_MONTH, 0);
        //Util.WriteSharePrefrenceForInteger(this, Constant.SHRED_PR.KEY_PAYSLIP_YEAR, 0);
    }

    @OnClick(R.id.rlGenerate)
    @SuppressWarnings("unused")
    public void Generate(View view) {
        pager.setCurrentItem(0);
    }

    @OnClick(R.id.rlPublish)
    @SuppressWarnings("unused")
    public void Publish(View view) {
        pager.setCurrentItem(1);
    }


    private void init() {

        pager = (ViewPager)findViewById(R.id.viewpagerDemo);
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
                        viewGenerate.setVisibility(View.VISIBLE);
                        viewPublish.setVisibility(View.GONE);
                        tvGenerate.setTextColor(getResources().getColor(R.color.white));
                        tvPublish.setTextColor(getResources().getColor(R.color.light_gray));
                        break;
                    case 1:
                        viewGenerate.setVisibility(View.GONE);
                        viewPublish.setVisibility(View.VISIBLE);
                        tvGenerate.setTextColor(getResources().getColor(R.color.light_gray));
                        tvPublish.setTextColor(getResources().getColor(R.color.white));
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
        fragmentList.add(GeneratePayslipFragment.newInstance());
        fragmentList.add(PublishPayslipFragment.newInstance());
        return fragmentList;
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
}