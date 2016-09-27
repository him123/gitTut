package com.bigbang.superteam.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.dataObjs.AttendanceHistoryModel;
import com.bigbang.superteam.leave_attendance.UpdateTimeActivity;
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
 * Created by USER 7 on 11/4/2015.
 */
public class AttendanceDeatilsUserAdapterNew extends BaseAdapter {
    private ArrayList<AttendanceHistoryModel> data;
    private Activity mContext;
    private static LayoutInflater inflater = null;

    static SQLiteHelper helper;
    public static SQLiteDatabase db = null;


    String TAG = "AttendanceDeatilsUserAdapterNew";
    int indexNew = -1;

    private TransparentProgressDialog pd;
    ImageLoader
            imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options,options1;

    public AttendanceDeatilsUserAdapterNew(Activity ctx, ArrayList<AttendanceHistoryModel> d) {
        mContext = ctx;
        data = d;
        /***********  Layout inflator to call external xml layout () ***********/
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(150))
                .showImageOnLoading(R.drawable.default_image).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        options1 = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.empty_profilepic).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.empty_profilepic).showImageOnFail(R.drawable.empty_profilepic).cacheInMemory(true)
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
        final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.listrow_user_attendance_new, null);
            holder = new ViewHolder(view);

            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();
        holder.tvUserName.setText(data.get(index).getUserName());

       /* String newTimeOut = Util.gmtToLocalTime("" + data.get(index).getNewTimeOut());
        String newTimeIN = Util.gmtToLocalTime("" + data.get(index).getNewTimeIn());
        String oldTimeIN = Util.gmtToLocalTime("" + data.get(index).getTimeIN());
        String oldTimeOut = Util.gmtToLocalTime("" + data.get(index).getTimeOut());
        String updatedTimeIN = Util.gmtToLocalTime("" + data.get(index).getUpdateTimeIn());
        String updatedTimeOut = Util.gmtToLocalTime("" + data.get(index).getUpdateTimeOut());*/
        //holder.tvNewTimeIn.setText(timeIN);

        holder.tvNewTimeIn.setText("");
        holder.tvNewTimeOut.setText("");
        String date = Util.dateFormatWithGMTWithoutYear("" + data.get(index).getRequestDate());
        holder.tvDate.setText(date);

        holder.tvContactImg.setText("" + data.get(index).getUserName().toUpperCase());
        imageLoader.displayImage(""+data.get(index).getImageUrl(),holder.imgLogo , options);

        if ((data.get(index).getImageUrl().length())>5){
            holder.tvContactImg.setVisibility(View.GONE);
        }else{
            holder.tvContactImg.setVisibility(View.VISIBLE);
        }

        if (data.get(index).getIsManualAttendance().equals("full") || data.get(index).getIsManualAttendance().equals("half")) {

            holder.linearNewTimes.setVisibility(View.GONE);
            holder.tvAttendanceType.setVisibility(View.VISIBLE);
            holder.relativeUpdate.setVisibility(View.GONE);
            if (data.get(index).getIsManualAttendance().equals("full")) {
                holder.tvAttendanceType.setText("Manual: Full day");
            } else {
                holder.tvAttendanceType.setText("Manual: Half day");
            }
            if (!(data.get(index).getManualApprovalState())) {
                holder.tvAttendanceType.setTextColor(mContext.getResources().getColor(R.color.yellow));
            } else {
                holder.tvAttendanceType.setTextColor(mContext.getResources().getColor(R.color.gray));
            }
        } else {
            holder.tvAttendanceType.setVisibility(View.GONE);
            holder.relativeUpdate.setVisibility(View.VISIBLE);
            holder.linearNewTimes.setVisibility(View.VISIBLE);
            if (data.get(index).getIsPresent()) {
                    holder.tvNewTimeOut.setVisibility(View.INVISIBLE);
            } else {
                holder.tvNewTimeOut.setVisibility(View.VISIBLE);
            }
        }

       /* if (!(data.get(index).getCheckInApprovalState()) || !(data.get(index).getCheckOutApprovalState())) {
            if (data.get(index).getIsManualAttendance().equals("full") || data.get(index).getIsManualAttendance().equals("half")) {
                holder.tvNewTimeIn.setTextColor(mContext.getResources().getColor(R.color.yellow));
            } else {
                if (!data.get(index).getCheckInApprovalState()) {
                    holder.tvNewTimeIn.setTextColor(mContext.getResources().getColor(R.color.yellow));
                } else {
                    holder.tvNewTimeIn.setTextColor(mContext.getResources().getColor(R.color.black));
                }
                if (!data.get(index).getCheckOutApprovalState()) {
                    holder.tvNewTimeOut.setTextColor(mContext.getResources().getColor(R.color.yellow));
                } else {
                    holder.tvNewTimeOut.setTextColor(mContext.getResources().getColor(R.color.black));
                }
            }
            holder.relativeUpdate.setVisibility(View.GONE);
        } else {
            holder.tvNewTimeOut.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.tvNewTimeIn.setTextColor(mContext.getResources().getColor(R.color.black));
        }*/


        //check in data display
        if(data.get(index).getCheckInApprovalState()){
            if(Util.gmtToLocalTime(""+data.get(index).getNewTimeIn()).length()>0){
                Log.e(TAG,"Inside else 3333333 :- "+index);
                holder.tvNewTimeIn.setText(Util.gmtToLocalTime(data.get(index).getNewTimeIn()));
                holder.tvNewTimeIn.setTextColor(mContext.getResources().getColor(R.color.black));
            }else{
                Log.e(TAG,"Inside else 444444 :- "+index);
                holder.tvNewTimeIn.setText(Util.gmtToLocalTime(data.get(index).getTimeIN()));
                holder.tvNewTimeIn.setTextColor(mContext.getResources().getColor(R.color.black));
            }
        }else{
            Log.e(TAG,"Inside else 22222 :- "+index);
            holder.tvNewTimeIn.setText(Util.gmtToLocalTime(data.get(index).getUpdateTimeIn()));
            holder.tvNewTimeIn.setTextColor(mContext.getResources().getColor(R.color.yellow));
        }

//checkout data display
        if(data.get(index).getCheckOutApprovalState()){
            if(Util.gmtToLocalTime(data.get(index).getNewTimeOut()).length()>0){
                Log.e(TAG,"Inside else 6666666666 :- "+index);
                holder.tvNewTimeOut.setText(Util.gmtToLocalTime(data.get(index).getNewTimeOut()));
                holder.tvNewTimeOut.setTextColor(mContext.getResources().getColor(R.color.black));
            }else{
                Log.e(TAG,"Inside else 7777777 :- "+index);
                holder.tvNewTimeOut.setText(Util.gmtToLocalTime(data.get(index).getTimeOut()));
                holder.tvNewTimeOut.setTextColor(mContext.getResources().getColor(R.color.black));
            }
        }else{
            Log.e(TAG,"Inside else 5555555 :- "+index);
            holder.tvNewTimeOut.setText(Util.gmtToLocalTime(data.get(index).getUpdateTimeOut()));
            holder.tvNewTimeOut.setTextColor(mContext.getResources().getColor(R.color.yellow));
        }

        //manual data display

        if (data.get(index).getIsManualAttendance().equals("full") || data.get(index).getIsManualAttendance().equals("half")) {
            holder.relativeUpdate.setVisibility(View.GONE);
            if(data.get(index).getManualApprovalState()){
                holder.tvNewTimeIn.setTextColor(mContext.getResources().getColor(R.color.black));
            }else{
                holder.tvNewTimeIn.setTextColor(mContext.getResources().getColor(R.color.yellow));
            }
            Log.e(TAG,"Inside else 00000 :- "+index);
        }else{
            if(data.get(index).getCheckInApprovalState() && data.get(index).getCheckOutApprovalState()){
                holder.relativeUpdate.setVisibility(View.VISIBLE);
            }else{
                holder.relativeUpdate.setVisibility(View.GONE);
            }

        }


      /*  if (!(data.get(index).getCheckInApprovalState()) || !(data.get(index).getCheckOutApprovalState() || !data.get(index).getManualApprovalState())) {
            if (data.get(index).getIsManualAttendance().equals("full") || data.get(index).getIsManualAttendance().equals("half")) {
                holder.tvNewTimeIn.setTextColor(mContext.getResources().getColor(R.color.yellow));
                Log.e(TAG,"Inside else 00000 :- "+index);
            } else {
                Log.e(TAG,"Inside else 111111 :- "+index);
                if(data.get(index).getCheckInApprovalState()){
                    if(Util.gmtToLocalTime(""+data.get(index).getNewTimeIn()).length()>0){
                        Log.e(TAG,"Inside else 3333333 :- "+index);
                        holder.tvNewTimeIn.setText(Util.gmtToLocalTime("" + data.get(index).getNewTimeIn()));
                        holder.tvNewTimeIn.setTextColor(mContext.getResources().getColor(R.color.black));
                    }else{
                        Log.e(TAG,"Inside else 444444 :- "+index);
                        holder.tvNewTimeIn.setText(Util.gmtToLocalTime("" + data.get(index).getTimeIN()));
                        holder.tvNewTimeIn.setTextColor(mContext.getResources().getColor(R.color.black));
                    }
                }else{
                    Log.e(TAG,"Inside else 22222 :- "+index);
                    holder.tvNewTimeIn.setText(Util.gmtToLocalTime("" + data.get(index).getUpdateTimeIn()));
                    holder.tvNewTimeIn.setTextColor(mContext.getResources().getColor(R.color.yellow));
                }

                if(data.get(index).getCheckOutApprovalState()){
                    if(Util.gmtToLocalTime("" + data.get(index).getNewTimeOut()).length()>0){
                        Log.e(TAG,"Inside else 6666666666 :- "+index);
                        holder.tvNewTimeOut.setText(Util.gmtToLocalTime("" + data.get(index).getNewTimeOut()));
                        holder.tvNewTimeOut.setTextColor(mContext.getResources().getColor(R.color.black));
                    }else{
                        Log.e(TAG,"Inside else 7777777 :- "+index);
                        holder.tvNewTimeOut.setText(Util.gmtToLocalTime("" + data.get(index).getTimeOut()));
                        holder.tvNewTimeOut.setTextColor(mContext.getResources().getColor(R.color.black));
                    }
                }else{
                    Log.e(TAG,"Inside else 5555555 :- "+index);
                    holder.tvNewTimeOut.setText(Util.gmtToLocalTime("" + data.get(index).getUpdateTimeOut()));
                    holder.tvNewTimeOut.setTextColor(mContext.getResources().getColor(R.color.yellow));
                }
            }
            holder.relativeUpdate.setVisibility(View.GONE);
        }else {
            Log.e(TAG,"Inside else @@@@@@@@@@@@@ :- "+index);
            holder.tvNewTimeOut.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.tvNewTimeIn.setTextColor(mContext.getResources().getColor(R.color.black));
            holder.relativeUpdate.setVisibility(View.VISIBLE);
        }*/


        holder.relativeUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // updateFlag = true;
                indexNew = index;
                Intent i = new Intent(mContext, UpdateTimeActivity.class);
                i.putExtra("data", data.get(index).getJsonObj());
                mContext.startActivity(i);
                ((Activity) mContext).overridePendingTransition(R.anim.enter_from_left,
                        R.anim.hold_bottom);
            }
        });

        return view;
    }

    public class ViewHolder {

        @InjectView(R.id.tvUserName)
        TextView tvUserName;
        @InjectView(R.id.tvDate)
        TextView tvDate;
        @InjectView(R.id.tvNewTimeIn)
        TextView tvNewTimeIn;
        @InjectView(R.id.tvNewTimeOut)
        TextView tvNewTimeOut;
        @InjectView(R.id.tvAttendanceType)
        TextView tvAttendanceType;
        @InjectView(R.id.linearNewTimes)
        LinearLayout linearNewTimes;
        @InjectView(R.id.relativeUpdate)
        RelativeLayout relativeUpdate;
        @InjectView(R.id.imgLogo)
        ImageView imgLogo;
        @InjectView(R.id.tvContactImg)
        TextView tvContactImg;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();
            Util.setAppFont(mContainer, mFont);
        }
    }
}
