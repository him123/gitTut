package com.bigbang.superteam.adapter;

import android.app.Activity;
import android.content.Context;
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
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.dataObjs.AttendanceHistoryModel;
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
 * Created by USER 7 on 11/3/2015.
 */
public class AttendanceDetailsAdapterNew extends BaseAdapter {
    private ArrayList<AttendanceHistoryModel> data;
    private Activity mContext;
    private static LayoutInflater inflater = null;

    static SQLiteHelper helper;
    public static SQLiteDatabase db = null;


    String TAG = "AttendanceDetailsAdapterNew";
    ImageLoader
            imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options, options1;
    private TransparentProgressDialog pd;

    String oldTimeIn, oldTimeOut, newTimeIn, newTimeOut, updatedTimeIn, updatedTimeOut;


    public AttendanceDetailsAdapterNew(Activity ctx, ArrayList<AttendanceHistoryModel> d) {
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

//    @Override
//    public int getViewTypeCount() {
//
//        return getCount();
//    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }

    @Override
    public View getView(final int index, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.listrow_admin_user_attendance, null);
            holder = new ViewHolder(view);

            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();
        holder.tvUserName.setText(data.get(index).getUserName());
        oldTimeOut = Util.gmtToLocalTime("" + data.get(index).getTimeOut());
        //holder.tvOldTimeOut.setText(""+timeOut);
        oldTimeIn = Util.gmtToLocalTime("" + data.get(index).getTimeIN());
        // holder.tvOldTimeIn.setText(timeIn);
        newTimeOut = Util.gmtToLocalTime("" + data.get(index).getNewTimeOut());
        //holder.tvNewTimeOut.setText(""+newTimeOut);
        newTimeIn = Util.gmtToLocalTime("" + data.get(index).getNewTimeIn());
        updatedTimeIn = Util.gmtToLocalTime("" + data.get(index).getUpdateTimeIn());
        updatedTimeOut = Util.gmtToLocalTime("" + data.get(index).getUpdateTimeOut());
        //holder.tvNewTimeIn.setText(newTimeIn);

        holder.tvContactImg.setText("" + data.get(index).getUserName().toUpperCase());
        imageLoader.displayImage("" + data.get(index).getImageUrl(), holder.imgLogo, options);

        if ((data.get(index).getImageUrl().length()) > 5) {
            holder.tvContactImg.setVisibility(View.GONE);
        } else {
            holder.tvContactImg.setVisibility(View.VISIBLE);
        }


        // String date = Util.gmtToLocal("" + data.get(index).getRequestDate());
        holder.tvDate.setText(Util.dateFormatWithGMTWithoutYear("" + data.get(index).getRequestDate()));
        Log.e(TAG, "Index above all :- " + index);
        holder.tvNewTimeIn.setTextColor(mContext.getResources().getColor(R.color.black));
        holder.tvOldTimeIn.setTextColor(mContext.getResources().getColor(R.color.gray));
        holder.tvNewTimeOut.setTextColor(mContext.getResources().getColor(R.color.black));
        holder.tvOldTimeOut.setTextColor(mContext.getResources().getColor(R.color.gray));
        if (data.get(index).getIsManualAttendance().equals("full") || data.get(index).getIsManualAttendance().equals("half")) {
            holder.linearNewTimes.setVisibility(View.GONE);
            holder.linearOldTimes.setVisibility(View.GONE);
            holder.tvAttendanceType.setVisibility(View.VISIBLE);
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
            holder.linearNewTimes.setVisibility(View.VISIBLE);
            holder.linearOldTimes.setVisibility(View.VISIBLE);
            holder.tvAttendanceType.setVisibility(View.GONE);

         /*   if ((data.get(index).getTimeIN().equals(data.get(index).getNewTimeIn()) && (data.get(index).getTimeOut().equals(data.get(index).getNewTimeOut())))) {
                if (data.get(index).getCheckInApprovalState().equals("2") || data.get(index).getCheckOutApprovalState().equals("2")) {
                    holder.linearOldTimes.setVisibility(View.GONE);
                    holder.tvNewTimeIn.setTextColor(mContext.getResources().getColor(R.color.black));
                    holder.tvOldTimeIn.setTextColor(mContext.getResources().getColor(R.color.gray));
                    holder.tvNewTimeOut.setTextColor(mContext.getResources().getColor(R.color.black));
                    holder.tvOldTimeOut.setTextColor(mContext.getResources().getColor(R.color.gray));
                    if (data.get(index).getCheckInApprovalState().equals("2")) {
                        holder.tvNewTimeIn.setTextColor(mContext.getResources().getColor(R.color.yellow));
                        holder.tvOldTimeIn.setTextColor(mContext.getResources().getColor(R.color.gray));
                    } else {
                        holder.tvNewTimeIn.setTextColor(mContext.getResources().getColor(R.color.black));
                        holder.tvOldTimeIn.setTextColor(mContext.getResources().getColor(R.color.gray));
                    }
                    if (data.get(index).getCheckOutApprovalState().equals("2")) {
                        holder.tvNewTimeOut.setTextColor(mContext.getResources().getColor(R.color.yellow));
                        holder.tvOldTimeOut.setTextColor(mContext.getResources().getColor(R.color.gray));
                    } else {
                        holder.tvNewTimeOut.setTextColor(mContext.getResources().getColor(R.color.black));
                        holder.tvOldTimeOut.setTextColor(mContext.getResources().getColor(R.color.gray));
                    }
                } else {
                    holder.linearOldTimes.setVisibility(View.GONE);
                }
            } else {
                    holder.linearOldTimes.setVisibility(View.VISIBLE);

               if (data.get(index).getCheckInApprovalState().equals("2") || data.get(index).getCheckOutApprovalState().equals("2")) {
                   holder.tvNewTimeIn.setTextColor(mContext.getResources().getColor(R.color.yellow));
                   holder.tvOldTimeIn.setTextColor(mContext.getResources().getColor(R.color.gray));
                   holder.tvNewTimeOut.setTextColor(mContext.getResources().getColor(R.color.yellow));
                   holder.tvOldTimeOut.setTextColor(mContext.getResources().getColor(R.color.gray));
                } else {
                    holder.tvNewTimeIn.setTextColor(mContext.getResources().getColor(R.color.black));
                    holder.tvOldTimeIn.setTextColor(mContext.getResources().getColor(R.color.gray));
                    holder.tvNewTimeOut.setTextColor(mContext.getResources().getColor(R.color.black));
                    holder.tvOldTimeOut.setTextColor(mContext.getResources().getColor(R.color.gray));
                }
                if (data.get(index).getCheckInApprovalState().equals("2")) {
                    holder.tvNewTimeIn.setTextColor(mContext.getResources().getColor(R.color.yellow));
                    holder.tvOldTimeIn.setTextColor(mContext.getResources().getColor(R.color.gray));
                } else {
                    holder.tvNewTimeIn.setTextColor(mContext.getResources().getColor(R.color.black));
                    holder.tvOldTimeIn.setTextColor(mContext.getResources().getColor(R.color.gray));
                }

                if (data.get(index).getCheckOutApprovalState().equals("2")) {
                    holder.tvNewTimeOut.setTextColor(mContext.getResources().getColor(R.color.yellow));
                    holder.tvOldTimeOut.setTextColor(mContext.getResources().getColor(R.color.gray));
                } else {
                    holder.tvNewTimeOut.setTextColor(mContext.getResources().getColor(R.color.black));
                    holder.tvOldTimeOut.setTextColor(mContext.getResources().getColor(R.color.gray));
                }
            }*/

            if (data.get(index).getIsPresent()) {
                holder.tvNewTimeOut.setVisibility(View.INVISIBLE);
                holder.tvOldTimeOut.setVisibility(View.INVISIBLE);
            } else {
                holder.tvNewTimeOut.setVisibility(View.VISIBLE);
                holder.tvOldTimeOut.setVisibility(View.VISIBLE);
            }
        }


        if (data.get(index).getCheckInApprovalState()) {
            holder.tvNewTimeIn.setTextColor(mContext.getResources().getColor(R.color.black));
        } else {
            holder.tvNewTimeIn.setTextColor(mContext.getResources().getColor(R.color.yellow));
        }

        if (data.get(index).getCheckOutApprovalState()) {
            holder.tvNewTimeOut.setTextColor(mContext.getResources().getColor(R.color.black));
        } else {
            holder.tvNewTimeOut.setTextColor(mContext.getResources().getColor(R.color.yellow));
        }


        if ((data.get(index).getNewTimeIn().length() > 0 || (data.get(index).getTimeIN().length() > 0 && data.get(index).getUpdateTimeIn().length() > 0)) || (data.get(index).getNewTimeOut().length() > 0 || (data.get(index).getTimeOut().length() > 0 && data.get(index).getUpdateTimeOut().length() > 0))) {
            holder.tvOldTimeOut.setVisibility(View.VISIBLE);
            holder.tvOldTimeIn.setVisibility(View.VISIBLE);
            Log.e(TAG, "Inside old time visible ---->=== " + index);
        } else {
            holder.tvOldTimeOut.setVisibility(View.GONE);
            holder.tvOldTimeIn.setVisibility(View.GONE);
            Log.e(TAG, "Inside old time GONE ----> " + index);
        }


        if (data.get(index).getTimeIN().length() > 0) {
            // Log.e(TAG,"11111111111111" +"----> "+timeInOld);
            holder.tvOldTimeIn.setText(oldTimeIn);
            holder.tvNewTimeIn.setText(oldTimeIn);
            if (data.get(index).getCheckInApprovalState()) {
                //Log.e(TAG,"222222222222222222" +"----> "+timeInNew);
                if (newTimeIn.length() > 0) {
                    holder.tvNewTimeIn.setText(newTimeIn);
                }
            } else {
                // Log.e(TAG,"333333333333" +"----> "+timeInUpdated);
                if (updatedTimeIn.length() > 0) {
                    holder.tvNewTimeIn.setText(updatedTimeIn);
                }
            }
        } else {
            //Log.e(TAG,"44444444444444" +"----> "+timeInUpdated);
            holder.tvNewTimeIn.setText("" + updatedTimeIn);
        }

        if (data.get(index).getTimeOut().length() > 0) {
            //Log.e(TAG,"555555555555555" +"----> "+timeOutOld);
            holder.tvOldTimeOut.setText(oldTimeOut);
            holder.tvNewTimeOut.setText(oldTimeOut);
            if (data.get(index).getCheckOutApprovalState()) {
                // holder.tvNewTimeOut.setText(timeOutNew);
                if (newTimeOut.length() > 0) {
                    holder.tvNewTimeOut.setText(newTimeOut);
                }
                Log.e(TAG, "666666666666666666666" + "----> " + newTimeOut);
            } else {
                // holder.tvNewTimeOut.setText(timeOutUpdated);
                if (updatedTimeOut.length() > 0) {
                    holder.tvNewTimeOut.setText(updatedTimeOut);
                }
                Log.e(TAG, "77777777777" + "----> " + updatedTimeOut);
            }
        } else {
            holder.tvNewTimeOut.setText(updatedTimeOut);
            Log.e(TAG, "888888888888888" + "----> " + updatedTimeOut);
        }


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
        @InjectView(R.id.tvOldTimeIn)
        TextView tvOldTimeIn;
        @InjectView(R.id.tvOldTimeOut)
        TextView tvOldTimeOut;
        @InjectView(R.id.tvAttendanceType)
        TextView tvAttendanceType;
        @InjectView(R.id.linearNewTimes)
        LinearLayout linearNewTimes;
        @InjectView(R.id.linearOldTimes)
        LinearLayout linearOldTimes;
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
