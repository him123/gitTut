package com.bigbang.superteam.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bigbang.superteam.R;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 8 on 7/21/2015.
 */
public class SpinnerAdapter extends ArrayAdapter<HashMap<String, String>> {

    private Activity context;
    private ArrayList<HashMap<String, String>> locallist;
    private static LayoutInflater inflater = null;

    public int getCount() {
        return locallist.size();
    }

    public SpinnerAdapter(Activity context, int textViewResourceId, ArrayList<HashMap<String, String>> locallist) {
        super(context, textViewResourceId);

        this.context = context;
        this.locallist = locallist;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.listrow_spinner, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final HashMap<String, String> user = locallist.get(position);
        holder.tvTitle.setText(user.get("role_desc") + ": " + user.get("firstName") + " " + user.get("lastName"));
        Log.e("position", ">>" + position);

        return view;
    }


    public View getDropDownView(int position, View view, ViewGroup parent) {

        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.listrow_spinner, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final HashMap<String, String> user = locallist.get(position);
        holder.tvTitle.setText("    " + user.get("role_desc") + ": " + user.get("firstName") + " " + user.get("lastName"));
        Log.e("position", ">>" + position);

        return view;
    }

    public class ViewHolder {
        @InjectView(R.id.tvTitle)
        TextView tvTitle;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}
