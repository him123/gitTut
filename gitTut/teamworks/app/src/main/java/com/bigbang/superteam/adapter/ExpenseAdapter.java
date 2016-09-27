package com.bigbang.superteam.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.dataObjs.WorkTransaction;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 3 on 03/07/2015.
 */
public class ExpenseAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    Activity mContext;
    ArrayList<WorkTransaction> dataArray;
    DisplayImageOptions options;
    String TAG = "Expense Adapter";

    public ExpenseAdapter(Activity mainActivity, ArrayList<WorkTransaction> dataList) {
        this.mContext = mainActivity;
        this.dataArray = dataList;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return dataArray.size();
    }

    @Override
    public Object getItem(int position) {
        return dataArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listraw_expense, null);
            holder = new Holder(convertView);
        } else {
            holder = (Holder) convertView.getTag();
        }
        holder.tvIndexNo.setText("" + (position + 1));
//        holder.tvDate.setText(Util.utcToLocalTime(dataArray.get(position).getInvoice_date()).substring(0,10));
        try {
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(dataArray.get(position).getInvoice_date())));
            String sdate = Util.ddMMMyyyy1.format(startCalendar.getTime());
            holder.tvDate.setText("" + sdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.tvDescription.setText(dataArray.get(position).getDiscription());
        holder.tvAmount.setText(dataArray.get(position).getAmount());
        convertView.setTag(holder);
        return convertView;
    }

    public class Holder {
        @InjectView(R.id.tvIndexNo)
        TextView tvIndexNo;
        @InjectView(R.id.tvDate)
        TextView tvDate;
        @InjectView(R.id.tvDescription)
        TextView tvDescription;
        @InjectView(R.id.tvAmount)
        TextView tvAmount;

        public Holder(View view) {
            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();
            Util.setAppFont(mContainer, mFont);
        }
    }

}
