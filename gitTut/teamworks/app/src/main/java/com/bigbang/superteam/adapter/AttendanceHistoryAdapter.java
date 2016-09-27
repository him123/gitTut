package com.bigbang.superteam.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.dataObjs.AttendanceHistoryModel;
import com.bigbang.superteam.leave_attendance.AttendanceDetailsActivity;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 7 on 5/20/2015.
 */
public class AttendanceHistoryAdapter extends BaseAdapter {
    private ArrayList<AttendanceHistoryModel> data;
    private Context mContext;
    private static LayoutInflater inflater = null;

    static SQLiteHelper helper;
    public static SQLiteDatabase db = null;

    DisplayImageOptions options;

    ImageLoader
            imageLoader = ImageLoader.getInstance();

    public AttendanceHistoryAdapter(Context ctx, ArrayList<AttendanceHistoryModel> d) {
        mContext = ctx;
        data = d;
        /***********  Layout inflator to call external xml layout () ***********/
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(150))
                .showImageOnLoading(0).resetViewBeforeLoading(true)
                .showImageForEmptyUri(0).showImageOnFail(0).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(ctx));
    }

    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int index, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.listrow_attendance_history, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

        final String role = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_ROLE);
        if (role.equals("Admin")) {
            holder.tvAttendanceType.setText("" + data.get(index).getUserName());
            holder.tvStatus.setVisibility(View.GONE);
            if (data.get(index).getIsManualAttendance().equalsIgnoreCase("1")) {
                holder.tvReason.setText("Manual Attendance");
            } else {
                holder.tvReason.setText(data.get(index).getAttendanceType() + " Attendance");
            }
        } else {
            if (data.get(index).getIsManualAttendance().equalsIgnoreCase("1")) {
                holder.tvAttendanceType.setText("Manual Attendance");
                holder.tvReason.setText("" + data.get(index).getReason());
                holder.tvStatus.setText(data.get(index).getRequestStatus());
            } else {
                holder.tvAttendanceType.setText(data.get(index).getAttendanceType() + " Attendance");
                holder.tvReason.setText("" + data.get(index).getLocationType());
                holder.tvStatus.setVisibility(View.GONE);
            }
        }

        holder.tvDate.setText(data.get(index).getRequestDate());
        holder.tvTime.setText(data.get(index).getRequestTime());

        String strImage = "" + data.get(index).getImageUrl();
        imageLoader.displayImage(strImage, holder.imgUser, options);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (role.equals("Admin")) {
                    Intent approvalDetailIntent = new Intent(mContext, AttendanceDetailsActivity.class);
                    approvalDetailIntent.putExtra("userId", "" + data.get(index).getUserId());
                    mContext.startActivity(approvalDetailIntent);
                }
            }
        });

        return view;
    }
    static class ViewHolder {
        @InjectView(R.id.tvAttendanceType)
        TextView tvAttendanceType;
        @InjectView(R.id.tvReason)
        TextView tvReason;
        @InjectView(R.id.tvDate)
        TextView tvDate;
        @InjectView(R.id.tvTime)
        TextView tvTime;
       @InjectView(R.id.tvStatus)
       TextView tvStatus;
       @InjectView(R.id.viewUserImg)
        ImageView imgUser;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}