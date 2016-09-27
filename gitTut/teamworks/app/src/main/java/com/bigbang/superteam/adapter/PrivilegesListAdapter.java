package com.bigbang.superteam.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bigbang.superteam.R;
import com.bigbang.superteam.util.Util;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 8 on 7/8/2015.
 */
public class PrivilegesListAdapter extends BaseAdapter {

    private static final String TAG = UserListAdapter.class.getSimpleName();
    private static LayoutInflater inflater = null;
    ArrayList<HashMap<String, String>> locallist;
    Activity context;

    public PrivilegesListAdapter(Activity activity, ArrayList<HashMap<String, String>> locallist) {
        this.locallist = locallist;
        this.context = activity;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return locallist.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        final ViewHolder holder;

        if (view == null) {
            view = inflater.inflate(R.layout.listprivileges_row, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.tvTitle.setText(locallist.get(position).get("privilege"));

        if (locallist.get(position).get("checked").equals("0")) {
            holder.tbCheck.setChecked(false);
        } else {
            holder.tbCheck.setChecked(true);
        }

        holder.tbCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.tbCheck.isChecked()) {
                    locallist.get(position).put("checked", "1");
                } else {
                    locallist.get(position).put("checked", "0");
                }
            }
        });

        return view;
    }

    public class ViewHolder {
        @InjectView(R.id.tv_title)
        TextView tvTitle;
        @InjectView(R.id.tbCheck)
        ToggleButton tbCheck;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(context.getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();
            Util.setAppFont(mContainer, mFont);
        }
    }

}
