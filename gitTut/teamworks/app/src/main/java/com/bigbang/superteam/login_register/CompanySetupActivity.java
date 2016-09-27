package com.bigbang.superteam.login_register;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.FragmentAdapter;
import com.bigbang.superteam.fragment.AttandanceSetupFragment;
import com.bigbang.superteam.fragment.CompanySetupFragment;
import com.bigbang.superteam.fragment.HolidaysSetupFragment;
import com.bigbang.superteam.fragment.LeavesSetupFragment;
import com.bigbang.superteam.fragment.RolesRightsSetupFragment;
import com.bigbang.superteam.payroll.PayrollSetupFragment;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class CompanySetupActivity extends FragmentActivity {

    FragmentAdapter pageAdapter;
    View v;
    RelativeLayout rlLogout;
    public static ViewPager pager;
    public static int currentPosition = 0;
    public static FragmentManager fragmentManager;
    TextView tvTitle;
    int FROM = Constant.FROM_LOGIN;
    TransparentProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_company_setup);
        ButterKnife.inject(this);

        try {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                currentPosition = Integer.parseInt(extras.getString("position"));
                FROM = Integer.parseInt(extras.getString("from"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        init();

        if (FROM == Constant.FROM_LOGIN) {
            rlLogout.setVisibility(View.VISIBLE);
        } else {
            rlLogout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        if (FROM == Constant.FROM_LOGIN) {
            if (CompanySetupActivity.currentPosition >= 2) {
                if (CompanySetupActivity.currentPosition == 6) {
                    CompanySetupActivity.currentPosition--;
                    CompanySetupActivity.pager.setCurrentItem(CompanySetupActivity.currentPosition);

                } else {
                    CompanySetupActivity.currentPosition--;
                    CompanySetupActivity.pager.setCurrentItem(CompanySetupActivity.currentPosition);
                }
            } else {
                super.onBackPressed();
                finish();
                overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
            }
        } else {
            super.onBackPressed();
            finish();
            overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
        }
    }

    @OnClick(R.id.rlBack)
    @SuppressWarnings("unused")
    public void Back(View view) {
        if (FROM == Constant.FROM_LOGIN) {
            if (CompanySetupActivity.currentPosition >= 2) {
                if (CompanySetupActivity.currentPosition == 6) {
                    CompanySetupActivity.currentPosition--;
                    CompanySetupActivity.pager.setCurrentItem(CompanySetupActivity.currentPosition);

                } else {
                    CompanySetupActivity.currentPosition--;
                    CompanySetupActivity.pager.setCurrentItem(CompanySetupActivity.currentPosition);
                }
            } else {
                finish();
                overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
            }
        } else {
            finish();
            overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
        }
    }

    private void init() {

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        progressDialog = new TransparentProgressDialog(CompanySetupActivity.this, R.drawable.progressdialog, false);
        fragmentManager = getFragmentManager();
        pager = (ViewPager) findViewById(R.id.viewpagerDemo);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        rlLogout = (RelativeLayout) findViewById(R.id.rlLogout);
        final List<Fragment> fragmentList = getFragments();
        pageAdapter = new FragmentAdapter(getSupportFragmentManager(), fragmentList);
        pager.setAdapter(pageAdapter); //079 26430530

        setInit(currentPosition);

        rlLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert1 = new AlertDialog.Builder(CompanySetupActivity.this);
                alert1.setTitle("" + Constant.AppNameSuper);
                alert1.setMessage("Are you sure you want to exit?");
                alert1.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @SuppressLint("InlinedApi")
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                if (Util.isOnline(getApplicationContext())) {
                                    TeamWorkApplication.logout(CompanySetupActivity.this, progressDialog);
                                } else {
                                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                alert1.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub
                                dialog.cancel();
                            }
                        });
                alert1.create();
                alert1.show();
            }
        });

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int pageNo) {
                currentPosition = pageNo;
                switch (currentPosition) {
                    case 0:
                        tvTitle.setText(getResources().getString(R.string.company_setup));
                        break;
                    case 1:
                        tvTitle.setText(getResources().getString(R.string.attendance_setup));
                        break;
                    case 2:
                        tvTitle.setText(getResources().getString(R.string.holidays));
                        break;
                    case 3:
                        tvTitle.setText(getResources().getString(R.string.leave));
                        break;
                    case 4:
                        tvTitle.setText(getResources().getString(R.string.payroll_setup));
                        break;
                    case 5:
                        tvTitle.setText(getResources().getString(R.string.roles_rights_setup));
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

    private void setInit(int currentPosition) {
        pager.setCurrentItem(currentPosition);
        switch (currentPosition) {
            case 0:
                tvTitle.setText(getResources().getString(R.string.company_setup));
                break;
            case 1:
                tvTitle.setText(getResources().getString(R.string.attendance_setup));
                break;
            case 2:
                tvTitle.setText(getResources().getString(R.string.holidays));
                break;
            case 3:
                tvTitle.setText(getResources().getString(R.string.leave));
                break;
            case 4:
                tvTitle.setText(getResources().getString(R.string.payroll_setup));
                break;
            case 5:
                tvTitle.setText(getResources().getString(R.string.roles_rights_setup));
                break;
        }
    }

    private List<Fragment> getFragments() {
        List<Fragment> fragmentList = new ArrayList<Fragment>();
        fragmentList.add(CompanySetupFragment.newInstance());
        fragmentList.add(AttandanceSetupFragment.newInstance(FROM));
        fragmentList.add(HolidaysSetupFragment.newInstance(FROM));
        fragmentList.add(LeavesSetupFragment.newInstance(FROM));
        fragmentList.add(PayrollSetupFragment.newInstance(FROM));
        fragmentList.add(RolesRightsSetupFragment.newInstance(FROM));
        return fragmentList;
    }

    protected String read(String key) {
        return Util.ReadSharePrefrence(getApplicationContext(), key);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
