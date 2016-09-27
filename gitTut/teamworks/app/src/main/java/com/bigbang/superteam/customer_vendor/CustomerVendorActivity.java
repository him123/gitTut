package com.bigbang.superteam.customer_vendor;

import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.FragmentAdapter;
import com.bigbang.superteam.fragment.CustomerListFragment;
import com.bigbang.superteam.fragment.VendorListFragment;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class CustomerVendorActivity extends FragmentActivity {

    @InjectView(R.id.viewCustomers)
    View viewCustomers;
    @InjectView(R.id.viewVendors)
    View viewVendors;
    @InjectView(R.id.tvCustomers)
    TextView tvCustomers;
    @InjectView(R.id.tvVendors)
    TextView tvVendors;
    @InjectView(R.id.rl_add)
    RelativeLayout rlAdd;

    FragmentAdapter pageAdapter;
    View v;
    public static ViewPager pager;
    public int currentPosition = 0;
    public static FragmentManager fragmentManager;

    String CustomerVendorType = "U";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_vendor);
        ButterKnife.inject(this);

        init();
        reload();

        String roleId = Util.ReadSharePrefrence(getApplicationContext(), Constant.SHRED_PR.KEY_ROLE_ID);
//        if (Arrays.asList("1", "2").contains(roleId)) {
//            rlAdd.setVisibility(View.VISIBLE);
//        } else rlAdd.setVisibility(View.GONE);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (read(Constant.SHRED_PR.KEY_RELOAD_CUST_VEND).equals("1")) {
            write(Constant.SHRED_PR.KEY_RELOAD_CUST_VEND, "0");
            reload();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }

    @OnClick(R.id.rlCustomers)
    @SuppressWarnings("unused")
    public void Customers(View view) {
        pager.setCurrentItem(0);
    }

    @OnClick(R.id.rlVendors)
    @SuppressWarnings("unused")
    public void Vendors(View view) {
        pager.setCurrentItem(1);
    }

    @OnClick(R.id.rl_add)
    @SuppressWarnings("unused")
    public void addUser(View view) {
        Intent intent1 = new Intent(getApplicationContext(), CreateCustomerActivity.class);
        intent1.putExtra("Type", CustomerVendorType);
        intent1.putExtra("Create", "1");
        startActivity(intent1);
        overridePendingTransition(R.anim.enter_from_left,
                R.anim.hold_bottom);
    }

    @OnClick(R.id.rlBack)
    @SuppressWarnings("unused")
    public void Back(View view) {
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }


    private void init() {

        fragmentManager = getFragmentManager();
        pager = (ViewPager) findViewById(R.id.viewpager);

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int pageNo) {
                currentPosition = pageNo;
                switch (currentPosition) {
                    case 0:
                        currentPosition = 0;
                        CustomerVendorType = "U";
                        viewCustomers.setVisibility(View.VISIBLE);
                        viewVendors.setVisibility(View.GONE);
                        tvCustomers.setTextColor(getResources().getColor(R.color.white));
                        tvVendors.setTextColor(getResources().getColor(R.color.light_gray));
                        break;
                    case 1:
                        currentPosition = 1;
                        CustomerVendorType = "V";
                        viewCustomers.setVisibility(View.GONE);
                        viewVendors.setVisibility(View.VISIBLE);
                        tvCustomers.setTextColor(getResources().getColor(R.color.light_gray));
                        tvVendors.setTextColor(getResources().getColor(R.color.white));
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

    private void reload() {
        final List<Fragment> fragmentList = getFragments();
        pageAdapter = new FragmentAdapter(getSupportFragmentManager(), fragmentList);
        pager.setAdapter(pageAdapter);
        pager.setCurrentItem(currentPosition);
    }


    private List<Fragment> getFragments() {
        List<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(CustomerListFragment.newInstance());
        fragmentList.add(VendorListFragment.newInstance());

        return fragmentList;
    }

    protected void startActivity(Class klass) {
        startActivity(new Intent(this, klass));
    }

    protected void write(String key, String val) {
        Util.WriteSharePrefrence(getApplicationContext(), key, val);
    }

    protected String read(String key) {
        return Util.ReadSharePrefrence(getApplicationContext(), key);
    }
}
