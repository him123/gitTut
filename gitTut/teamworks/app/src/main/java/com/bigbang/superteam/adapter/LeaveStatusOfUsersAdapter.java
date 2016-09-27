package com.bigbang.superteam.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
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

import com.bigbang.superteam.Privileges;
import com.bigbang.superteam.R;
import com.bigbang.superteam.dataObjs.LeaveHistoryModel;
import com.bigbang.superteam.leave_attendance.LeaveDetailsActivity;
import com.bigbang.superteam.leave_attendance.LeaveStatusAdminActivity;
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
 * Created by USER 7 on 7/9/2015.
 */
public class LeaveStatusOfUsersAdapter extends BaseAdapter {
    private ArrayList<LeaveHistoryModel> data;
    private Activity mContext;
    private static LayoutInflater inflater = null;

    static SQLiteHelper helper;
    public static SQLiteDatabase db = null;

    String TAG = "LeaveHistoryAdapter";

    private TransparentProgressDialog pd;

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options,options1;
    boolean isRefresh;

    public LeaveStatusOfUsersAdapter(Activity ctx, ArrayList<LeaveHistoryModel> d,boolean isRefresh) {
        mContext = ctx;
        data = d;
        this.isRefresh=isRefresh;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();
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
            view = inflater.inflate(R.layout.listrow_leave_admin_history, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

      /*  final String date = Util.gmtToLocal("" + data.get(index).getStartDate());
        holder.tvDateTime.setText("" +date);*/

        String startDate = dateFormatEndDate("" + data.get(index).getStartDate());
        //String endDate = dateFormatEndDate("" + data.get(index).getEndDate());

        //holder.tvDateTime.setText("" + startDate + " - " + endDate);
        holder.tvDateTime.setText("" + startDate);

     /*   String type = ""+data.get(index).getLeaveType();
        String subStr = type.substring(0, type.indexOf(' '));
        holder.tvLeaveType.setText(subStr);*/
        holder.tvLeaveType.setText("" + data.get(index).getLeaveType());
        holder.tvUserName.setText("" + data.get(index).getUserName());

        holder.tvContactImg.setText("" + data.get(index).getUserName().toUpperCase());
        imageLoader.displayImage(""+data.get(index).getImageUrl(),holder.imgLogo , options);

        if ((data.get(index).getImageUrl().length())>5){
            holder.tvContactImg.setVisibility(View.GONE);
        }else{
            holder.tvContactImg.setVisibility(View.VISIBLE);
        }

        if(data.get(index).getLeaveStatus().equals("Pending")){
            holder.imgColor.setVisibility(View.GONE);
        }else if(data.get(index).getLeaveStatus().equals("Approved")){
            holder.imgColor.setVisibility(View.VISIBLE);
            holder.imgColor.setBackgroundResource(R.drawable.approved_color);
        }else if(data.get(index).getLeaveStatus().equals("Cancelled")){
            holder.imgColor.setVisibility(View.VISIBLE);
            holder.imgColor.setBackgroundResource(R.drawable.rejected_color);
        }else if(data.get(index).getLeaveStatus().equals("Withdrawn")){
            holder.imgColor.setVisibility(View.VISIBLE);
            holder.imgColor.setBackgroundResource(R.drawable.offline_color);
        }
        //holder.tvReason.setText("" + data.get(index).getReason());
        if (Privileges.APPROVE_LEAVE_REQUEST) {
            //  holder.tvLeaveStatus.setVisibility(View.GONE);
            if (data.get(index).getLeaveStatus().equals("Pending")) {
                holder.relativeApprove.setVisibility(View.VISIBLE);
                holder.relativeReject.setVisibility(View.VISIBLE);
                //holder.imgColor.setVisibility(View.GONE);
               // holder.tvLeaveStatus.setVisibility(View.GONE);
            } else if (data.get(index).getLeaveStatus().equals("Approved") || data.get(index).getLeaveStatus().equals("Cancelled") || (data.get(index).getLeaveStatus().equals("Withdrawn"))) {
                holder.relativeApprove.setVisibility(View.GONE);
                holder.relativeReject.setVisibility(View.GONE);
               /* holder.tvLeaveStatus.setVisibility(View.VISIBLE);
                holder.tvLeaveStatus.setText("" + data.get(index).getLeaveStatus());*/

            }
        } else {
            holder.relativeApprove.setVisibility(View.GONE);
            holder.relativeReject.setVisibility(View.GONE);
           /* holder.tvLeaveStatus.setVisibility(View.VISIBLE);
            holder.tvLeaveStatus.setText("" + data.get(index).getLeaveStatus());*/
        }

        holder.relativeApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Util.isOnline(mContext)) {
                    new LeaveApproveOrReject(index, "true").execute();
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }


            }
        });
        holder.relativeReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Util.isOnline(mContext)) {
                    new LeaveApproveOrReject(index, "false").execute();
                } else {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
                }

            }

        });


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (data.get(index).getFromActivity()==Constant.FROM_ADMIN_DASHBOARD) {

                    Intent iLeave = new Intent(mContext, LeaveStatusAdminActivity.class);
                    iLeave.putExtra("userID", "" + data.get(index).getUserId());

                    mContext.startActivity(iLeave);
                    ((Activity) mContext).overridePendingTransition(R.anim.enter_from_left,
                            R.anim.hold_bottom);
                    //((Activity)mContext).finish();
                } else  {
                    Intent iLeave = new Intent(mContext, LeaveDetailsActivity.class);
                    iLeave.putExtra("adminId", "" + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID));
                    iLeave.putExtra("startDate", "" + data.get(index).getStartDate());
                    iLeave.putExtra("leaveDay", "" + data.get(index).getLeaveDay());
                    iLeave.putExtra("reason", "" + data.get(index).getReason());
                    iLeave.putExtra("leaveType", "" + data.get(index).getLeaveType());
                    iLeave.putExtra("leaveStatus", "" + data.get(index).getLeaveStatus());
                    iLeave.putExtra("leaveId", "" + data.get(index).getApprovalId());
                    iLeave.putExtra("userID", "" + data.get(index).getUserId());
                    iLeave.putExtra("imgUrl",""+data.get(index).getImageUrl());
                    iLeave.putExtra("userName",""+data.get(index).getUserName());
                    //iLeave.putExtra("activityName", "LeaveStatusOfUsersAdapter");
                    iLeave.putExtra("fromActivity",Constant.FROM_LEAVE_ADAPTER);
                    mContext.startActivity(iLeave);
                    ((Activity) mContext).overridePendingTransition(R.anim.enter_from_left,
                            R.anim.hold_bottom);
                    //((Activity) mContext).finish();

                }


            }
        });
        return view;
    }

    public class ViewHolder {
        @InjectView(R.id.tvUserName)
        TextView tvUserName;
        @InjectView(R.id.tvLeaveType)
        TextView tvLeaveType;
        @InjectView(R.id.tvDate)
        TextView tvDateTime;
        @InjectView(R.id.rlApprove)
        RelativeLayout relativeApprove;
        @InjectView(R.id.rlReject)
        RelativeLayout relativeReject;
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

    class LeaveApproveOrReject extends AsyncTask<Void, String, String> {

        int index;
        String actionName;
        String response;
        //ProgressDialog pd;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pd != null) {
                pd.show();
            }
        }

        public LeaveApproveOrReject(int pos, String action) {
            this.index = pos;
            this.actionName = action;
        }

        @Override
        protected String doInBackground(Void... params) {
            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            ArrayList<String> list = new ArrayList<String>();

            list.add(""+data.get(index).getStartDate());
            //list.add(""+data.get(index).getStartDate());

            JSONArray jsArray = new JSONArray(list);
            Log.e(TAG,"Json array value is:- "+jsArray.toString().replace("\\",""));

            params1.add(new BasicNameValuePair("MemberID", "" + data.get(index).getUserId()));
            params1.add(new BasicNameValuePair("TransactionID", "" + data.get(index).getTransactionId()));
            params1.add(new BasicNameValuePair("LeaveDates", ""+jsArray.toString().replace("\\","")));
            params1.add(new BasicNameValuePair("Approve", "" +actionName));

            Log.e("params1", ">>" + params1);
            response = Util.makeServiceCall(Constant.URL + "approveLeaves", 2, params1, mContext);
            Log.e(TAG, "** response is:- " + response);
            return response;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd != null) {
                pd.dismiss();
            }
            try {
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");
                Toast.makeText(mContext, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
                if (status.equals("Success")) {
                /*    if (actionName.equalsIgnoreCase("true")) {
                        updateStatus(index, "Approved");
                    } else {
                        updateStatus(index, "Cancelled");
                    }*/

                    int approvalStatus=1;
                    if(actionName.equals("true")) approvalStatus=2;
                    else if (actionName.equals("false")) approvalStatus=3;
                    updateApprovalsDB(index, approvalStatus);

                    if(isRefresh){
                        Intent intent1 = new Intent();
                        // intent.setAction(MY_ACTION);
                        intent1.setAction(mContext.ACTIVITY_SERVICE);
                        mContext.sendBroadcast(intent1);
                    }
                } /*else {
                    JSONObject jData = jObj.getJSONObject("data");
                    String state = jData.optString("RemoveId");
                    if (state.equals("1")) {
                        updateStatus(index, "Withdrawn");
                        //Toast.makeText(mContext, "This leave is already withdrawn by user or cancelled", Toast.LENGTH_LONG).show();
                        removeFromDataBase(data.get(index).getTransactionId(), index);
                    }

                }*/
            } catch (Exception e) {
                e.printStackTrace();

            }


        }
    }


    private void updateApprovalsDB(int index,int approvalStatus) {
        ContentValues values = new ContentValues();
        values.put("Status", "" + approvalStatus);

        db.update(Constant.ApprovalsTable, values, "TransactionID like \"" + data.get(index).getTransactionId() + "\"", null);

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
