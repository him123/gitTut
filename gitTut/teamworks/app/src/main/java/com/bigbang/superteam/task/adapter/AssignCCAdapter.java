package com.bigbang.superteam.task.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.model.User;
import com.bigbang.superteam.util.Util;

import java.util.ArrayList;

import butterknife.ButterKnife;

public class AssignCCAdapter extends BaseAdapter {

    ArrayList<User> locallist;
    Context mContext;

    public AssignCCAdapter(ArrayList<User> locallist, Context activity) {
        this.locallist = locallist;
        this.mContext = activity;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        View view = convertView;

        if (view == null) {
            view = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate
                    (R.layout.listrow_assignee_cc, parent, false);
            holder = new ViewHolder(view);
            assert view != null;
            holder.name = (TextView) view.findViewById(R.id.name);
            holder.relativeCheck = (RelativeLayout) view
                    .findViewById(R.id.relativeCheck);
            holder.btnCheck = (ImageButton) view.findViewById(R.id.check);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final User user = locallist.get(position);
        holder.name.setText("" + user.getFirstName() + " " + user.getLastName());

        if (user.isChecked()) {
            holder.btnCheck.setBackgroundResource(R.drawable.chacked);
        } else {
            holder.btnCheck.setBackgroundResource(R.drawable.unchacked);
        }

        if (user.isDisabled()) {
            holder.relativeCheck.setEnabled(false);
            holder.name.setTextColor(mContext.getResources().getColor(R.color.gray));
        } else {
            holder.relativeCheck.setEnabled(true);
            holder.name.setTextColor(mContext.getResources().getColor(R.color.black));
        }

        final int i = position;

        holder.relativeCheck.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (user.isChecked()) {
                    holder.btnCheck
                            .setBackgroundResource(R.drawable.unchacked);
                    user.setChecked(false);
                    locallist.get(i).setChecked(false);
                } else {
                    holder.btnCheck
                            .setBackgroundResource(R.drawable.chacked);
                    locallist.get(i).setChecked(true);
                }
            }
        });
        return view;
    }

    class ViewHolder {
        TextView name;
        RelativeLayout relativeCheck;
        ImageButton btnCheck;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();
            Util.setAppFont(mContainer, mFont);
        }
    }


}