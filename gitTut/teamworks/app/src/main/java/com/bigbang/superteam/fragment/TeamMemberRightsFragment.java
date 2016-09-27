package com.bigbang.superteam.fragment;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.PrivilegesListAdapter;
import com.bigbang.superteam.util.Util;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 8 on 03-Nov-15.
 */
public class TeamMemberRightsFragment extends Fragment {

    @InjectView(R.id.listviewRights)
    ListView lvPrivileges;

    Activity activity;

    public static Fragment newInstance() {
        TeamMemberRightsFragment fragment = new TeamMemberRightsFragment();
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity=getActivity();
        View v = inflater.inflate(R.layout.fragment_rights, container, false);
        ButterKnife.inject(this, v);
        final Typeface mFont = Typeface.createFromAsset(activity.getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) v.getRootView();
        Util.setAppFont(mContainer, mFont);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        lvPrivileges.setDivider(null);
        lvPrivileges.setDividerHeight(0);
        lvPrivileges.setAdapter(new PrivilegesListAdapter(activity, RolesRightsSetupFragment.listPrivilegesTM));
    }


}