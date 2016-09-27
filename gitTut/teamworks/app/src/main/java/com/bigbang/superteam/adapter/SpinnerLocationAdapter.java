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
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 7 on 7/29/2015.
 */
public class SpinnerLocationAdapter extends ArrayAdapter<HashMap<String, String>> {

    private Activity context;
    private ArrayList<HashMap<String, String>> locallist;
    private ArrayList<HashMap<String, String>> locallist_temp;
    private static LayoutInflater inflater = null;

    public int getCount() {
        return locallist.size();
    }

    public SpinnerLocationAdapter(Activity context, int textViewResourceId, ArrayList<HashMap<String, String>> locallist) {
        super(context, textViewResourceId);

        this.context = context;
        this.locallist = locallist;
        locallist_temp = new ArrayList<>();
        locallist_temp.addAll(locallist);
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public HashMap<String, String> getItem(int position) {
        HashMap<String, String> map = new HashMap<>();
        map = locallist.get(position);
        return map;
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
        holder.tvTitle.setText(""+user.get("name"));
        Log.e("position", ">>" + position+">>>>>>>>>>>> "+user.get("name"));

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
        holder.tvTitle.setText(""+user.get("name"));
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

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        locallist.clear();
        if (charText.length() == 0) {
            locallist.addAll(locallist_temp);
        } else {
            int i=0;
            for (HashMap<String, String> wp : locallist_temp) {
                if (locallist_temp.get(i).get("name").toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    locallist.add(wp);
                }
//                if (wp.getMobileNo().contains(charText)) {
//                    locallist.add(wp);
//                }
                i++;
            }
        }
        notifyDataSetChanged();
    }

}
