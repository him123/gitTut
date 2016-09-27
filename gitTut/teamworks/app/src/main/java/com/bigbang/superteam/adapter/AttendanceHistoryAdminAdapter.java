package com.bigbang.superteam.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.dataObjs.AttendanceHistoryModel;
import com.bigbang.superteam.leave_attendance.AttendanceDetailsActivity;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 7 on 6/2/2015.
 */
public class AttendanceHistoryAdminAdapter extends BaseAdapter {
    private ArrayList<AttendanceHistoryModel> data;
    private Context mContext;
    private static LayoutInflater inflater = null;

    static SQLiteHelper helper;
    public static SQLiteDatabase db = null;

    DisplayImageOptions options;
    String TAG="AttendanceHistoryAdminAdapter";

    ImageLoader
            imageLoader = ImageLoader.getInstance();

    private TransparentProgressDialog pd;

    public AttendanceHistoryAdminAdapter(Context ctx, ArrayList<AttendanceHistoryModel> d) {
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

        pd = new TransparentProgressDialog(ctx, R.drawable.progressdialog, false);
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
            view = inflater.inflate(R.layout.listrow_admin_attendance, null);
            holder = new ViewHolder(view);

            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();


        holder.tvUserName.setText(data.get(index).getUserName());

        String timeCheckout = Util.gmtToLocalTime(""+data.get(index).getTimeOut());
        holder.tvCheckOut.setText(""+timeCheckout);

       // String timeOut = Util.gmtToLocalTime(""+data.get(index).getTimeOut());
        String updatedTimeCheckOut = Util.gmtToLocalTime(""+data.get(index).getNewTimeOut());
       // holder.tvCheckOut.setText(timeOut);
        holder.tvUpdateCheckOut.setText(updatedTimeCheckOut);

        if (data.get(index).getIsManualAttendance().equals("1") || data.get(index).getIsManualAttendance().equals("2")) {
          /*  holder.imgVwManual.setVisibility(View.GONE);
            holder.tvisManual.setVisibility(View.GONE);
            holder.tvCheckOut.setVisibility(View.GONE);
            holder.tvCheckOutDisp.setVisibility(View.GONE);
            holder.tvCheckInDisp.setText("Manual Attendance : ");

            if(data.get(index).getIsManualAttendance().equals("1"))
            {
                holder.tvCheckIn.setText("Full Day");
            }
            else
            {
                holder.tvCheckIn.setText("Half Day");
            }
            holder.relativeUpdate.setVisibility(View.GONE);*/
            holder.imgVwManual.setVisibility(View.GONE);
            holder.tvCheckOut.setVisibility(View.GONE);
            holder.tvisManual.setVisibility(View.GONE);
            holder.tvCheckOutDisp.setVisibility(View.GONE);
            holder.tvCheckInDisp.setVisibility(View.GONE);
            holder.tvUpdateCheckInDisp.setText("Manual Attendance : ");

            if(data.get(index).getIsManualAttendance().equals("1"))
            {
                holder.tvCheckIn.setVisibility(View.GONE);
                holder.tvUpdateCheckIn.setText("Full Day");
            }
            else
            {
                holder.tvCheckIn.setVisibility(View.GONE);
                holder.tvUpdateCheckIn.setText("Half Day");
            }
            holder.relativeUpdate.setVisibility(View.VISIBLE);

            //holder.tvUpdateCheckIn.setVisibility(View.GONE);
            // holder.tvUpdateCheckInDisp.setVisibility(View.GONE);
            holder.tvUpdateCheckOut.setVisibility(View.GONE);
            holder.tvUpdateCheckOutDisp.setVisibility(View.GONE);
            if(data.get(index).getCheckInApprovalState().equals("2")||data.get(index).getCheckOutApprovalState().equals("2")){
                holder.tvUpdate.setVisibility(View.VISIBLE);
                holder.tvUpdate.setText("Pending");

                holder.tvUpdateCheckInDisp.setTextColor(Color.parseColor("#FFD700"));
                holder.tvUpdateCheckIn.setTextColor(Color.parseColor("#FFD700"));
                holder.tvCheckIn.setTextColor(Color.parseColor("#000000"));
                holder.tvCheckInDisp.setTextColor(Color.parseColor("#000000"));
            }else{
                holder.tvUpdate.setVisibility(View.GONE);
                holder.tvUpdateCheckInDisp.setTextColor(Color.parseColor("#000000"));
                holder.tvUpdateCheckIn.setTextColor(Color.parseColor("#000000"));
                holder.tvCheckIn.setTextColor(Color.parseColor("#000000"));
                holder.tvCheckInDisp.setTextColor(Color.parseColor("#000000"));
            }

        } else {

            holder.tvUpdate.setVisibility(View.GONE);
            if ((data.get(index).getTimeIN().equals(data.get(index).getNewTimeIn()) && (data.get(index).getTimeOut().equals(data.get(index).getNewTimeOut())))) {
                holder.relativeUpdate.setVisibility(View.GONE);
                if(data.get(index).getCheckInApprovalState().equals("2")||data.get(index).getCheckOutApprovalState().equals("2")){
                    holder.tvUpdateNew.setVisibility(View.VISIBLE);
                    holder.tvUpdateNew.setText("Pending");
                    holder.tvUpdate.setVisibility(View.GONE);

                    if(data.get(index).getCheckInApprovalState().equals("2")){
                        holder.tvCheckInDisp.setTextColor(Color.parseColor("#FFD700"));
                        holder.tvCheckIn.setTextColor(Color.parseColor("#FFD700"));
                        holder.tvUpdateCheckInDisp.setTextColor(Color.parseColor("#000000"));
                        holder.tvUpdateCheckIn.setTextColor(Color.parseColor("#000000"));
                    }else{
                        holder.tvCheckInDisp.setTextColor(Color.parseColor("#000000"));
                        holder.tvCheckIn.setTextColor(Color.parseColor("#000000"));
                        holder.tvUpdateCheckInDisp.setTextColor(Color.parseColor("#000000"));
                        holder.tvUpdateCheckIn.setTextColor(Color.parseColor("#000000"));
                    }

                    if(data.get(index).getCheckOutApprovalState().equals("2")){
                        holder.tvCheckOutDisp.setTextColor(Color.parseColor("#FFD700"));
                        holder.tvCheckOut.setTextColor(Color.parseColor("#FFD700"));
                        holder.tvUpdateCheckOutDisp.setTextColor(Color.parseColor("#000000"));
                        holder.tvUpdateCheckOut.setTextColor(Color.parseColor("#000000"));
                    }else{
                        holder.tvCheckOutDisp.setTextColor(Color.parseColor("#000000"));
                        holder.tvCheckOut.setTextColor(Color.parseColor("#000000"));
                        holder.tvUpdateCheckOutDisp.setTextColor(Color.parseColor("#000000"));
                        holder.tvUpdateCheckOut.setTextColor(Color.parseColor("#000000"));
                    }
                }else{
                    holder.tvUpdateNew.setVisibility(View.GONE);

                }
            } else {
               holder.relativeUpdate.setVisibility(View.VISIBLE);
                holder.tvUpdateNew.setVisibility(View.GONE);
                holder.tvUpdate.setVisibility(View.VISIBLE);
                if(data.get(index).getCheckInApprovalState().equals("2")||data.get(index).getCheckOutApprovalState().equals("2")){
                    holder.tvUpdate.setText("Pending");

                }else{
                    holder.tvUpdate.setText("Updated");
                }
                if(data.get(index).getCheckInApprovalState().equals("2")){
                    holder.tvUpdateCheckInDisp.setTextColor(Color.parseColor("#FFD700"));
                    holder.tvUpdateCheckIn.setTextColor(Color.parseColor("#FFD700"));
                    holder.tvCheckInDisp.setTextColor(Color.parseColor("#000000"));
                    holder.tvCheckIn.setTextColor(Color.parseColor("#000000"));
                }else{
                    holder.tvUpdateCheckInDisp.setTextColor(Color.parseColor("#000000"));
                    holder.tvUpdateCheckIn.setTextColor(Color.parseColor("#000000"));
                    holder.tvCheckInDisp.setTextColor(Color.parseColor("#000000"));
                    holder.tvCheckIn.setTextColor(Color.parseColor("#000000"));
                }

                if(data.get(index).getCheckOutApprovalState().equals("2")){
                    holder.tvUpdateCheckOutDisp.setTextColor(Color.parseColor("#FFD700"));
                    holder.tvUpdateCheckOut.setTextColor(Color.parseColor("#FFD700"));
                    holder.tvCheckOutDisp.setTextColor(Color.parseColor("#000000"));
                    holder.tvCheckOut.setTextColor(Color.parseColor("#000000"));
                }else{
                    holder.tvUpdateCheckOutDisp.setTextColor(Color.parseColor("#000000"));
                    holder.tvUpdateCheckOut.setTextColor(Color.parseColor("#000000"));
                    holder.tvCheckOutDisp.setTextColor(Color.parseColor("#000000"));
                    holder.tvCheckOut.setTextColor(Color.parseColor("#000000"));
                }
            }

            holder.tvisManual.setVisibility(View.GONE);
            holder.imgVwManual.setVisibility(View.GONE);
            String timeIn = Util.gmtToLocalTime(""+data.get(index).getTimeIN());
            holder.tvCheckIn.setText(""+timeIn);
            holder.tvUpdateCheckInDisp.setText("In: ");
            String updateTimeIN = Util.gmtToLocalTime(""+data.get(index).getNewTimeIn());
            holder.tvUpdateCheckIn.setText(updateTimeIN);

            if (data.get(index).getIsPresent().equals("1")) {
                holder.tvCheckOut.setVisibility(View.GONE);
                holder.tvCheckOutDisp.setVisibility(View.GONE);
                holder.tvUpdateCheckOut.setVisibility(View.GONE);
                holder.tvUpdateCheckOutDisp.setVisibility(View.GONE);
            } else {
                holder.tvCheckOut.setVisibility(View.VISIBLE);
                holder.tvCheckOutDisp.setVisibility(View.VISIBLE);
                holder.tvUpdateCheckOut.setVisibility(View.VISIBLE);
                holder.tvUpdateCheckOutDisp.setVisibility(View.VISIBLE);
            }
        }
        String strImage = "" + data.get(index).getImageUrl();
        imageLoader.displayImage(strImage, holder.imgUser, options);


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent approvalDetailIntent = new Intent(mContext, AttendanceDetailsActivity.class);
                approvalDetailIntent.putExtra("userId", "" + data.get(index).getUserId());
                approvalDetailIntent.putExtra("userName", "" + data.get(index).getUserName());
                approvalDetailIntent.putExtra("activityName","AttendanceHistoryAdminAdapter");
                mContext.startActivity(approvalDetailIntent);

            }
        });

        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.tvUserName)
        TextView tvUserName;
        @InjectView(R.id.tv_ismanual)
        TextView tvisManual;
        @InjectView(R.id.tvCheckInTime)
        TextView tvCheckIn;
        @InjectView(R.id.tvCheckOutTime)
        TextView tvCheckOut;
        @InjectView(R.id.tvCheckOut)
        TextView tvCheckOutDisp;
        @InjectView(R.id.tvCheckIn)
        TextView tvCheckInDisp;
        @InjectView(R.id.viewUserImg)
        ImageView imgUser;
        @InjectView(R.id.imgVwManual)
        ImageView imgVwManual;
        @InjectView(R.id.relativeUpdateTime)
        RelativeLayout relativeUpdate;
        @InjectView(R.id.tvUpdateCheckInTime)
        TextView tvUpdateCheckIn;
        @InjectView(R.id.tvUpdateCheckOutTime)
        TextView tvUpdateCheckOut;
        @InjectView(R.id.tvUpdateCheckOut)
        TextView tvUpdateCheckOutDisp;
        @InjectView(R.id.tvUpdateCheckIn)
        TextView tvUpdateCheckInDisp;
        /*@InjectView(R.id.linearBtn)
        LinearLayout linearBtn;*/
        @InjectView(R.id.tvUpdate)
        TextView tvUpdate;
        @InjectView(R.id.tvUpdateNewTime)
        TextView tvUpdateNew;


        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}
