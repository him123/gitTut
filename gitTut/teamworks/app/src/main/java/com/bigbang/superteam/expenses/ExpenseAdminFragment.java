package com.bigbang.superteam.expenses;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.FragmentAdapter;
import com.bigbang.superteam.util.Util;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by User on 8/2/2016.
 */
public class ExpenseAdminFragment extends Fragment{

    @InjectView(R.id.imageViewOwn)
    ImageView imageViewOwn;
    @InjectView(R.id.imageViewEveryone)
    ImageView imageViewEveryone;
    @InjectView(R.id.viewOwn)
    View viewOwn;
    @InjectView(R.id.viewEveryone)
    View viewEveryone;

    Activity activity;
    static android.support.v4.app.FragmentManager fragmentManager;
    FragmentAdapter pageAdapter;
    View v;
    public static ViewPager pager;
    public static int currentPosition = 0;

    public static Fragment newInstance(android.support.v4.app.FragmentManager fragmentManager1) {
        ExpenseAdminFragment fragment = new ExpenseAdminFragment();
        fragmentManager = fragmentManager1;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        activity = getActivity();
        View v = inflater.inflate(R.layout.fragment_expense_admin, container, false);
        final Typeface mFont = Typeface.createFromAsset(activity.getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) v.getRootView();
        Util.setAppFont(mContainer, mFont);
        ButterKnife.inject(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(fragmentManager!=null)
            init();
    }

    @OnClick(R.id.rlOwn)
    @SuppressWarnings("unused")
    public void Own(View view) {
        pager.setCurrentItem(0);
    }

    @OnClick(R.id.rlEveryone)
    @SuppressWarnings("unused")
    public void Everyone(View view) {
        pager.setCurrentItem(1);
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
                        viewOwn.setVisibility(View.VISIBLE);
                        viewEveryone.setVisibility(View.GONE);
                        break;
                    case 1:
                        viewOwn.setVisibility(View.GONE);
                        viewEveryone.setVisibility(View.VISIBLE);
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
        fragmentList.add(ExpenseAdminOwnFragment.newInstance());
        fragmentList.add(ExpenseAdminEveryoneFragment.newInstance());

        return fragmentList;
    }

}