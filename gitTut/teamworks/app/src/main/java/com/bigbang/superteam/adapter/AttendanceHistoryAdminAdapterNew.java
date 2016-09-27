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
 * Created by USER 7 on 11/3/2015.
 */
public class AttendanceHistoryAdminAdapterNew extends BaseAdapter {
    private ArrayList<AttendanceHistoryModel> data;
    private Activity mContext;
    private static LayoutInflater inflater = null;

    static SQLiteHelper helper;
    public static SQLiteDatabase db = null;

    DisplayImageOptions options, options1;
    String TAG = "AttendanceHistoryAdminAdapterNew";

    ImageLoader
            imageLoader = ImageLoader.getInstance();

    private TransparentProgressDialog pd;

    String timeOutOld, timeInOld, timeInNew, timeOutNew, timeInUpdated, timeOutUpdated;

    public AttendanceHistoryAdminAdapterNew(Activity ctx, ArrayList<AttendanceHistoryModel> d) {
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
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.listrow_admin_attendance_new, null);
            holder = new ViewHolder(view);

            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();


        holder.tvUserName.setText(data.get(index).getUserName());

        // imageLoader.displayImage("", holder.imgWhite, options1);
        imageLoader.displayImage("", holder.imgLogo, options);

        timeOutOld = Util.gmtToLocalTime("" + data.get(index).getTimeOut());
        timeInOld = Util.gmtToLocalTime("" + data.get(index).getTimeIN());
        timeOutNew = Util.gmtToLocalTime("" + data.get(index).getNewTimeOut());
        timeInNew = Util.gmtToLocalTime("" + data.get(index).getNewTimeIn());
        timeInUpdated = Util.gmtToLocalTime("" + data.get(index).getUpdateTimeIn());
        timeOutUpdated = Util.gmtToLocalTime("" + data.get(index).getUpdateTimeOut());

        holder.tvContactImg.setText("" + data.get(index).getUserName().toUpperCase());
        imageLoader.displayImage("" + data.get(index).getImageUrl(), holder.imgLogo, options);

        if ((data.get(index).getImageUrl().length()) > 5) {
            holder.tvContactImg.setVisibility(View.GONE);
        } else {
            holder.tvContactImg.setVisibility(View.VISIBLE);
        }

        if (data.get(index).getIsManualAttendance().equals("full") || data.get(index).getIsManualAttendance().equals("half")) {
            holder.tvAttendanceStatus.setVisibility(View.VISIBLE);
            if (data.get(index).getIsManualAttendance().equals("full")) {
                holder.tvAttendanceStatus.setText("Manual: Full day");
            } else {
                holder.tvAttendanceStatus.setText("Manual: Half day");
            }
            if (!(data.get(index).getManualApprovalState())) {
                holder.tvAttendanceStatus.setTextColor(mContext.getResources().getColor(R.color.yellow));
            } else {
                holder.tvAttendanceStatus.setTextColor(mContext.getResources().getColor(R.color.gray));
            }
            holder.relativeTime.setVisibility(View.GONE);
        } else {
            holder.tvAttendanceStatus.setVisibility(View.GONE);
            holder.tvAttendanceStatus.setTextColor(mContext.getResources().getColor(R.color.red));

            if (data.get(index).getIsPresent()) {
                holder.tvOldTimeOut.setVisibility(View.INVISIBLE);
                holder.tvNewTimeOut.setVisibility(View.INVISIBLE);
            } else {
                holder.tvOldTimeOut.setVisibility(View.VISIBLE);
                holder.tvNewTimeOut.setVisibility(View.VISIBLE);
            }

            /*if ((data.get(index).getTimeIN().equals(data.get(index).getNewTimeIn()) && (data.get(index).getTimeOut().equals(data.get(index).getNewTimeOut())))) {
                holder.linearOldTime.setVisibility(View.GONE);
                if (data.get(index).getCheckInApprovalState().equals("2") || data.get(index).getCheckOutApprovalState().equals("2")) {
                    if (data.get(index).getCheckInApprovalState().equals("2")) {
                        holder.tvNewTimeIn.setTextColor(mContext.getResources().getColor(R.color.yellow));

                    } else {
                        holder.tvNewTimeIn.setTextColor(mContext.getResources().getColor(R.color.black));

                    }
                    if (data.get(index).getCheckOutApprovalState().equals("2")) {
                        holder.tvNewTimeOut.setTextColor(mContext.getResources().getColor(R.color.yellow));

                    } else {
                        holder.tvNewTimeOut.setTextColor(mContext.getResources().getColor(R.color.black));

                    }
                } else {

                    holder.linearOldTime.setVisibility(View.GONE);
                }
            } else {
                holder.linearOldTime.setVisibility(View.VISIBLE);
                if (data.get(index).getCheckInApprovalState().equals("2")) {
                    holder.tvNewTimeIn.setTextColor(mContext.getResources().getColor(R.color.yellow));

                } else {
                    holder.tvNewTimeIn.setTextColor(mContext.getResources().getColor(R.color.black));

                }
                if (data.get(index).getCheckOutApprovalState().equals("2")) {
                    holder.tvNewTimeOut.setTextColor(mContext.getResources().getColor(R.color.yellow));

                } else {
                    holder.tvNewTimeOut.setTextColor(mContext.getResources().getColor(R.color.black));

                }
            }

            if (data.get(index).getIsPresent().equals("1")) {
                holder.tvOldTimeOut.setVisibility(View.INVISIBLE);
                holder.tvNewTimeOut.setVisibility(View.INVISIBLE);
            } else {
                holder.tvOldTimeOut.setVisibility(View.VISIBLE);
                holder.tvNewTimeOut.setVisibility(View.VISIBLE);
            }*/
        }

        // holder.linearOldTime.setVisibility(View.GONE);

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
            holder.linearOldTime.setVisibility(View.VISIBLE);
            Log.e(TAG, "Inside old time visible ---->=== " + index);
        } else {
            holder.linearOldTime.setVisibility(View.GONE);
            Log.e(TAG, "Inside old time GONE ----> " + index);
        }

        if (data.get(index).getTimeIN().length() > 0) {
            Log.e(TAG, "11111111111111" + "----> " + timeInOld);
            holder.tvOldTimeIn.setText(timeInOld);
            holder.tvNewTimeIn.setText(timeInOld);
            if (data.get(index).getCheckInApprovalState()) {
                Log.e(TAG, "222222222222222222" + "----> " + timeInNew);
                if (timeInNew.length() > 0) {
                    holder.tvNewTimeIn.setText(timeInNew);
                }
            } else {
                Log.e(TAG, "333333333333" + "----> " + timeInUpdated);
                if (timeInUpdated.length() > 0) {
                    holder.tvNewTimeIn.setText(timeInUpdated);
                }
            }
        } else {
            Log.e(TAG, "44444444444444" + "----> " + timeInUpdated);
            holder.tvNewTimeIn.setText("" + timeInUpdated);
        }

        if (data.get(index).getTimeOut().length() > 0) {
            Log.e(TAG, "555555555555555" + "----> " + timeOutOld);
            holder.tvOldTimeOut.setText(timeOutOld);
            holder.tvNewTimeOut.setText(timeOutOld);
            if (data.get(index).getCheckOutApprovalState()) {
                // holder.tvNewTimeOut.setText(timeOutNew);
                if (timeOutNew.length() > 0) {
                    holder.tvNewTimeOut.setText(timeOutNew);
                }
                Log.e(TAG, "666666666666666666666" + "----> " + timeOutNew);
            } else {
                // holder.tvNewTimeOut.setText(timeOutUpdated);
                if (timeOutUpdated.length() > 0) {
                    holder.tvNewTimeOut.setText(timeOutUpdated);
                }
                Log.e(TAG, "77777777777" + "----> " + timeOutUpdated);
            }
        } else {
            holder.tvNewTimeOut.setText(timeOutUpdated);
            Log.e(TAG, "888888888888888" + "----> " + timeOutUpdated);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent approvalDetailIntent = new Intent(mContext, AttendanceDetailsActivity.class);
                approvalDetailIntent.putExtra("userId", "" + data.get(index).getUserId());
                approvalDetailIntent.putExtra("userName", "" + data.get(index).getUserName());
                approvalDetailIntent.putExtra("activityName", "AttendanceHistoryAdminAdapter");
                mContext.startActivity(approvalDetailIntent);
                ((Activity) mContext).overridePendingTransition(R.anim.enter_from_left,
                        R.anim.hold_bottom);

            }
        });

        return view;
    }

    public class ViewHolder {
        @InjectView(R.id.tvUserName)
        TextView tvUserName;
        @InjectView(R.id.tvNewTimeIn)
        TextView tvNewTimeIn;
        @InjectView(R.id.tvNewTimeOut)
        TextView tvNewTimeOut;
        @InjectView(R.id.tvOldTimeIn)
        TextView tvOldTimeIn;
        @InjectView(R.id.tvOldTimeOut)
        TextView tvOldTimeOut;
        @InjectView(R.id.tvAttendanceStatus)
        TextView tvAttendanceStatus;
        @InjectView(R.id.linearNewTimes)
        LinearLayout linearNewTime;
        @InjectView(R.id.linearOldTimes)
        LinearLayout linearOldTime;
        @InjectView(R.id.relativeTimes)
        RelativeLayout relativeTime;
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
