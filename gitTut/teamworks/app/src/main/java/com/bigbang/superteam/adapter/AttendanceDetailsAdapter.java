package com.bigbang.superteam.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
 * Created by USER 7 on 5/30/2015.
 */
public class AttendanceDetailsAdapter extends BaseAdapter {
    private ArrayList<AttendanceHistoryModel> data;
    private Context mContext;
    private static LayoutInflater inflater = null;

    static SQLiteHelper helper;
    public static SQLiteDatabase db = null;

    DisplayImageOptions options;
    String TAG = "AttendanceDetailsAdapter";
    ImageLoader
            imageLoader = ImageLoader.getInstance();

    private TransparentProgressDialog pd;

    public AttendanceDetailsAdapter(Context ctx, ArrayList<AttendanceHistoryModel> d) {
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

        pd = new TransparentProgressDialog(ctx, R.drawable.progressdialog,false);

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
        String timeOut = Util.gmtToLocalTime(""+data.get(index).getTimeOut());
        String updatedTimeCheckOut = Util.gmtToLocalTime(""+data.get(index).getNewTimeOut());
        holder.tvCheckOut.setText(timeOut);
        holder.tvUpdateCheckOut.setText(updatedTimeCheckOut);
        String date = Util.gmtToLocal(""+data.get(index).getRequestDate());
        holder.tvisManual.setText(date);
Log.e(TAG,"Index above all :- "+index);

        if(data.get(index).getIsManualAttendance().equals("1") ||data.get(index).getIsManualAttendance().equals("2"))
        {
            holder.imgVwManual.setVisibility(View.GONE);
            holder.tvCheckOut.setVisibility(View.GONE);

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
        }else{

          //  Log.e(TAG,"Inside getview else part 1111111111111");
            holder.tvUpdate.setVisibility(View.GONE);
            if((data.get(index).getTimeIN().equals(data.get(index).getNewTimeIn()) && (data.get(index).getTimeOut().equals(data.get(index).getNewTimeOut())))){
                holder.relativeUpdate.setVisibility(View.GONE);
                if(data.get(index).getCheckInApprovalState().equals("2") || data.get(index).getCheckOutApprovalState().equals("2")){
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
                        Log.e(TAG,"Index is:- 11111111  "+index);
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
            }else{
                holder.relativeUpdate.setVisibility(View.VISIBLE);
                holder.tvUpdateNew.setVisibility(View.GONE);
                holder.tvUpdate.setVisibility(View.VISIBLE);
                if(data.get(index).getCheckInApprovalState().equals("2") || data.get(index).getCheckOutApprovalState().equals("2")){
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
                    Log.e(TAG,"Index is:- 22222222222  "+index);
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
/*
            holder.tvCheckInDisp.setText("In: ");
            holder.tvUpdateCheckInDisp.setText("In: ");

            if(data.get(index).getIsPresent().equals("1"))
            {
                holder.tvCheckOut.setVisibility(View.GONE);
                holder.tvCheckOutDisp.setVisibility(View.GONE);
                holder.tvUpdateCheckOut.setVisibility(View.GONE);
                holder.tvUpdateCheckOutDisp.setVisibility(View.GONE);
            }
            else
            {
                holder.tvCheckOut.setVisibility(View.VISIBLE);
                holder.tvCheckOutDisp.setVisibility(View.VISIBLE);
                holder.tvUpdateCheckOut.setVisibility(View.VISIBLE);
                holder.tvUpdateCheckOutDisp.setVisibility(View.VISIBLE);
            }
            holder.imgVwManual.setVisibility(View.GONE);
            String timeIN = Util.gmtToLocalTime(""+data.get(index).getTimeIN());
            String updateTimeIN = Util.gmtToLocalTime(""+data.get(index).getNewTimeIn());
            holder.tvCheckIn.setText(timeIN);
            holder.tvUpdateCheckIn.setText(updateTimeIN);
*/

            holder.imgVwManual.setVisibility(View.GONE);
            String timeIn = Util.gmtToLocalTime(""+data.get(index).getTimeIN());
            holder.tvCheckIn.setText(""+timeIn);
            holder.tvUpdateCheckInDisp.setText("In: ");
            String updateTimeIN = Util.gmtToLocalTime(""+data.get(index).getNewTimeIn());
            holder.tvUpdateCheckIn.setText(updateTimeIN);
            holder.tvCheckIn.setVisibility(View.VISIBLE);
            holder.tvCheckInDisp.setVisibility(View.VISIBLE);

            if (data.get(index).getIsPresent().equals("1")) {
                holder.tvCheckOut.setVisibility(View.GONE);
                holder.tvCheckOutDisp.setVisibility(View.GONE);
                holder.tvUpdateCheckOut.setVisibility(View.GONE);
                holder.tvUpdateCheckOutDisp.setVisibility(View.GONE);

               // Log.e(TAG,"Inside getview else part 222222222222222");
            } else {
                holder.tvCheckOut.setVisibility(View.VISIBLE);
                holder.tvCheckOutDisp.setVisibility(View.VISIBLE);
                holder.tvUpdateCheckOut.setVisibility(View.VISIBLE);
                holder.tvUpdateCheckOutDisp.setVisibility(View.VISIBLE);

                //Log.e(TAG,"Inside getview else part 3333333333333333");
            }
        }
        String strImage = "" + data.get(index).getImageUrl();
        imageLoader.displayImage(strImage, holder.imgUser, options);
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
        TextView tvUpdateCheckInDisp;/*
        @InjectView(R.id.linearBtn)
        LinearLayout linearBtn;*/
        @InjectView(R.id.tvUpdate)
        TextView tvUpdate;
        @InjectView(R.id.tvUpdateNewTime)
        TextView tvUpdateNew;
        //tvUpdateCheckIn,tvUpdateCheckInTime,tvUpdateCheckOut,tvUpdateCheckOutTime


        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}
