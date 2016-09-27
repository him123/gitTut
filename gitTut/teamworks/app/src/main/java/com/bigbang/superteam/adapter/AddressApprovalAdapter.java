package com.bigbang.superteam.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.util.Util;
import com.bigbang.superteam.admin.AddressApprovalActivity;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 8 on 9/29/2015.
 */
public class AddressApprovalAdapter extends BaseAdapter {

    ArrayList<HashMap<String, String>> locallist;
    ArrayList<HashMap<String, String>> locallistNew;
    Activity activity;
    private static LayoutInflater inflater = null;

    public AddressApprovalAdapter(Activity activity, ArrayList<HashMap<String, String>> locallist, ArrayList<HashMap<String, String>> locallistNew) {
        this.locallist = locallist;
        this.activity = activity;
        this.locallistNew = locallistNew;
        inflater = (LayoutInflater) activity.
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        View view = convertView;

        if (view == null) {
            view = inflater.inflate(R.layout.listrow_address_approval, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.tvTitle.setText("" + locallist.get(position).get("Address"));
        holder.tvType.setText("" + locallist.get(position).get("Type"));

        if (locallistNew != null) {
            holder.rlArrow.setVisibility(View.VISIBLE);
        } else holder.rlArrow.setVisibility(View.GONE);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locallistNew != null) {
                    Intent intent = new Intent(activity, AddressApprovalActivity.class);
                    intent.putExtra("OldAddress", "" + locallist.get(position).get("Address"));
                    intent.putExtra("NewAddress", "" + locallistNew.get(position).get("Address"));
                    intent.putExtra("OldType", "" + locallist.get(position).get("Type"));
                    intent.putExtra("NewType", "" + locallistNew.get(position).get("Type"));
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.enter_from_left,
                            R.anim.hold_bottom);
                }
            }
        });

        return view;
    }


    public class ViewHolder {
        @InjectView(R.id.tvTitle)
        TextView tvTitle;
        @InjectView(R.id.tvType)
        TextView tvType;
        @InjectView(R.id.rlArrow)
        RelativeLayout rlArrow;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(activity.getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();
            Util.setAppFont(mContainer, mFont);
        }
    }
}
