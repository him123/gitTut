package com.bigbang.superteam.fragment;

import android.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bigbang.superteam.R;
import com.bigbang.superteam.util.Util;

import butterknife.ButterKnife;

/**
 * Created by USER 8 on 02-Nov-15.
 */
public class AttendanceLeaveFragment extends Fragment {


    public static Fragment newInstance() {
        AttendanceLeaveFragment fragment = new AttendanceLeaveFragment();
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_attendance_leave, container, false);
        ButterKnife.inject(this, v);
        final Typeface mFont = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) v.getRootView();
        Util.setAppFont(mContainer, mFont);
        return v;
    }


}