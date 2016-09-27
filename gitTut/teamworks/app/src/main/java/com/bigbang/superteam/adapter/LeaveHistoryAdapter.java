package com.bigbang.superteam.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.dataObjs.LeaveHistoryModel;
import com.bigbang.superteam.leave_attendance.LeaveDetailsActivity;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 7 on 6/6/2015.
 */
public class LeaveHistoryAdapter extends BaseAdapter {
    private ArrayList<LeaveHistoryModel> data;
    private Activity mContext;
    private static LayoutInflater inflater = null;

    static SQLiteHelper helper;
    public static SQLiteDatabase db = null;

    String TAG = "LeaveHistoryAdapter";


    private TransparentProgressDialog pd;

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options,options1;

    public LeaveHistoryAdapter(Activity ctx, ArrayList<LeaveHistoryModel> d) {
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

        pd = new TransparentProgressDialog(ctx, R.drawable.progressdialog,false);

    }

    protected void updateStatus(int pos, String status) {
        data.get(pos).setLeaveStatus(status);
        notifyDataSetChanged();
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
            view = inflater.inflate(R.layout.listrow_leave_status, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();
/*
        String date = Util.gmtToLocal(""+data.get(index).getStartDate());
        holder.tvTime.setText("" +date);*/
        String startDate = dateFormatEndDate("" + data.get(index).getStartDate());
       // String endDate = dateFormatEndDate(""+data.get(index).getEndDate());

      //  holder.tvDate.setText(""+startDate+" - "+endDate);
        holder.tvDate.setText(""+startDate);
        holder.tvUserName.setText(""+data.get(index).getUserName());
       /* String type = data.get(index).getLeaveType();
        String subStr = type.substring(0, type.indexOf(' '));*/
        holder.tvLeaveType.setText(data.get(index).getLeaveType());
        //holder.tvReason.setText("" + data.get(index).getReason());


        holder.tvContactImg.setText("" + data.get(index).getUserName().toUpperCase());
        imageLoader.displayImage(""+data.get(index).getImageUrl(),holder.imgLogo , options);

        if ((data.get(index).getImageUrl().length())>5){
            holder.tvContactImg.setVisibility(View.GONE);
        }else{
            holder.tvContactImg.setVisibility(View.VISIBLE);
        }

        if (data.get(index).getLeaveStatus().equals("Pending")) {

            holder.imgColor.setBackgroundResource(R.drawable.pending_color);
            holder.relativeCancel.setVisibility(View.VISIBLE);
           /* int isDateGreater = Util.calculateDateDifference(""+data.get(index).getStartDate());
            if(isDateGreater==0){
                holder.relativeCancel.setVisibility(View.GONE);
            }else{
                holder.relativeCancel.setVisibility(View.VISIBLE);
            }*/
           // holder.tvWithDraw.setVisibility(View.VISIBLE);
        } else if (data.get(index).getLeaveStatus().equals("Approved")) {

            holder.relativeCancel.setVisibility(View.VISIBLE);
            holder.imgColor.setBackgroundResource(R.drawable.approved_color);
         /*  int isDateGreater = Util.calculateDateDifference(""+data.get(index).getStartDate());
           if(isDateGreater==0){
               holder.relativeCancel.setVisibility(View.GONE);
           }else{
               holder.relativeCancel.setVisibility(View.VISIBLE);
           }*/

        } else if (data.get(index).getLeaveStatus().equals("Cancelled")) {
            holder.imgColor.setBackgroundResource(R.drawable.rejected_color);
            holder.relativeCancel.setVisibility(View.GONE);

        } else if (data.get(index).getLeaveStatus().equals("Withdrawn")) {
            holder.imgColor.setBackgroundResource(R.drawable.offline_color);
            holder.relativeCancel.setVisibility(View.INVISIBLE);
        }



        holder.relativeCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (data.get(index).getLeaveStatus().equals("Pending") || data.get(index).getLeaveStatus().equals("Approved")) {
                    if(Util.isOnline(mContext)){
                        //put dialog
                        AlertDialog.Builder alert1 = new AlertDialog.Builder(mContext);
                        alert1.setTitle("" + Constant.AppNameSuper);
                        alert1.setMessage(mContext.getResources().getString(R.string.confirm_withdraw));
                        alert1.setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @SuppressLint("InlinedApi")
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        new leaveWithdraw(index).execute();
                                    }
                                });
                        alert1.setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.cancel();
                                    }
                                });
                        alert1.create();
                        alert1.show();

                    }else{
                        try{
                            JSONObject jObj = new JSONObject();
                            ArrayList<String> list = new ArrayList<String>();
                            list.add(data.get(index).getStartDate());

                            JSONArray jsArray = new JSONArray(list);
                            Log.e(TAG,"Json array value is:- "+jsArray.toString().replace("\\",""));

                            jObj.put("UserID", "" + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID));
                            jObj.put("LeaveDates", ""+jsArray.toString().replace("\\",""));
                            jObj.put("Status", ""+data.get(index).getLeaveStatus());

                            String strJson = jObj.toString();
                            String actionName = "withdrawLeaves";
                            String methodType = "2";

                            Util.insertIntoOffline(strJson, methodType, actionName,mContext);
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                }
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent iLeave = new Intent(mContext, LeaveDetailsActivity.class);
                    iLeave.putExtra("startDate",""+data.get(index).getStartDate());
                    iLeave.putExtra("leaveDay",""+data.get(index).getLeaveDay());
                    iLeave.putExtra("reason",""+data.get(index).getReason());
                    iLeave.putExtra("leaveType",""+data.get(index).getLeaveType());
                    iLeave.putExtra("leaveStatus",""+data.get(index).getLeaveStatus());
                    iLeave.putExtra("leaveId",""+data.get(index).getApprovalId());
                    iLeave.putExtra("userID",""+data.get(index).getUserId());
                    iLeave.putExtra("imgUrl",""+data.get(index).getImageUrl());
                    iLeave.putExtra("userName",""+data.get(index).getUserName());
                    iLeave.putExtra("fromActivity",Constant.FROM_LEAVE_HISTORY_ADAPTER);
                    //iLeave.putExtra("activityName","LeaveHistoryAdapter");
                    iLeave.putExtra("adminId", "" + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID));
                    mContext.startActivity(iLeave);
                    ((Activity) mContext).overridePendingTransition(R.anim.enter_from_left,
                        R.anim.hold_bottom);
                    //((Activity)mContext).finish();

                }
        });
        return view;
    }

    public class ViewHolder {

        @InjectView(R.id.tvUserName)
        TextView tvUserName;
        @InjectView(R.id.tvDate)
        TextView tvDate;
        @InjectView(R.id.tvLeaveType)
        TextView tvLeaveType;
        @InjectView(R.id.rlCancelLeave)
        RelativeLayout relativeCancel;
        @InjectView(R.id.imgLogo)
        ImageView imgLogo;
        @InjectView(R.id.tvContactImg)
        TextView tvContactImg;
        @InjectView(R.id.imgColor)
        ImageView imgColor;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();
            Util.setAppFont(mContainer, mFont);
        }
    }


    class leaveWithdraw extends AsyncTask<Void, String, String> {
        String response;
        int index;

        //TextView tvLeaveStatus;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(pd!=null){
                pd.show();
            }

        }

        public leaveWithdraw(int pos) {
            this.index = pos;
           // tvLeaveStatus = tvStatus;

        }

        @Override
        protected String doInBackground(Void... params) {

            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            ArrayList<String> list = new ArrayList<String>();
            list.add(data.get(index).getStartDate());

            JSONArray jsArray = new JSONArray(list);
            Log.e(TAG,"Json array value is:- "+jsArray.toString().replace("\\",""));


            params1.add(new BasicNameValuePair("UserID", "" + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID)));
            params1.add(new BasicNameValuePair("Status", ""+data.get(index).getLeaveStatus()));
            params1.add(new BasicNameValuePair("LeaveDates", ""+jsArray.toString().replace("\\","")));
            Log.e("params1", ">>" + params1);
          /*  response = Util.postData(params1, Constant.URL
                    + "leaveWithdrawn");*/
            response = Util.makeServiceCall(Constant.URL+"withdrawLeaves",2,params1,mContext);

            Log.v(TAG, "" + response);
            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if(pd!=null){
                pd.dismiss();
            }

            //pBar.setVisibility(View.GONE);
            try {
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");
                Toast.makeText(mContext, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
                if (status.equals("Success")) {
                    //removeFromDataBase(""+data.get(index).getApprovalId(),index);
                   // tvLeaveStatus.setText("Withdrawn");
                        updateStatus(index,"Withdrawn");
                }
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

    }


    public static String dateFormatStartDate(String dtStart) {

        String newDate = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            Date localTime = format.parse(dtStart);
            Date fromGmt = new Date(localTime.getTime() + TimeZone.getDefault().getOffset(localTime.getTime()));
            SimpleDateFormat sdf = new SimpleDateFormat("d");
            newDate = sdf.format(fromGmt);
            return newDate;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDate;
    }

    public static String dateFormatEndDate(String dtStart) {

        String newDate = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        try {
            Date localTime = format.parse(dtStart);
            Date fromGmt = new Date(localTime.getTime() + TimeZone.getDefault().getOffset(localTime.getTime()));
            SimpleDateFormat sdf = new SimpleDateFormat("d"+" MMM");
            newDate = sdf.format(fromGmt);
            return newDate;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return newDate;
    }


}
