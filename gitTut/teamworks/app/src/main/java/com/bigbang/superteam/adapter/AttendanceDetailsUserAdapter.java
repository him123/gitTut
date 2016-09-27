package com.bigbang.superteam.adapter;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.dataObjs.AttendanceHistoryModel;
import com.bigbang.superteam.leave_attendance.UpdateTimeActivity;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 7 on 7/20/2015.
 */
public class AttendanceDetailsUserAdapter extends BaseAdapter {
    private ArrayList<AttendanceHistoryModel> data;
    private Context mContext;
    private static LayoutInflater inflater = null;

    static SQLiteHelper helper;
    public static SQLiteDatabase db = null;
    private boolean updateFlag = false;

    DisplayImageOptions options;
    String TAG = "AttendanceDetailsAdapter";
    ImageLoader
            imageLoader = ImageLoader.getInstance();

    Calendar startCalendar, endCalendar, calendar;

    public static final int START_TIME = 107;
    public static final int END_TIME = 108;
    int indexNew = -1;
    String tempTimeIn, tempTimeOut,oldTimeIn,oldTimeOut;
    TimePickerDialog.OnTimeSetListener myToTimeListener, myfromTimeListener;

    private TransparentProgressDialog pd;

    public AttendanceDetailsUserAdapter(Context ctx, ArrayList<AttendanceHistoryModel> d) {
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

        startCalendar = Calendar.getInstance(TimeZone.getDefault());
        endCalendar = Calendar.getInstance(TimeZone.getDefault());
        calendar = Calendar.getInstance(TimeZone.getDefault());

        pd = new TransparentProgressDialog(ctx, R.drawable.progressdialog, false);
    }

    protected void updateStatusTimeIn(int pos, String status) {

        String oldTime = Util.gmtToLocal(data.get(pos).getNewTimeIn());
        String newDate = oldTime + " " + status + ":00";
        String newTime = Util.locatToGMTTemp(newDate);
        tempTimeIn = data.get(pos).getNewTimeIn();
        //tempTimeOut = data.get(pos).getNewTimeOut();
        data.get(pos).setNewTimeIn(newTime);
        notifyDataSetChanged();
    }

    protected void updateStatusTimeOut(int pos, String status) {

        String oldTime = Util.gmtToLocal(data.get(pos).getNewTimeOut());
        String newDate = oldTime + " " + status + ":00";
        String newTime = Util.locatToGMTTemp(newDate);
        //tempTimeIn = data.get(pos).getNewTimeIn();
        tempTimeOut = data.get(pos).getNewTimeOut();
        data.get(pos).setNewTimeOut(newTime);
        notifyDataSetChanged();
    }

   /* protected void updateStatus(int pos, String timeIN,String tiemOut) {
        data.get(pos).setNewTimeIn(timeIN);
        data.get(pos).setNewTimeOut(tiemOut);
        notifyDataSetChanged();
    }*/
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
            view = inflater.inflate(R.layout.listrow_user_attendance, null);
            holder = new ViewHolder(view);

            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();
        holder.tvUserName.setText(data.get(index).getUserName());
        String timeOut = Util.gmtToLocalTime("" + data.get(index).getNewTimeOut());
        holder.tvCheckOut.setText(timeOut);
        String date = Util.gmtToLocal("" + data.get(index).getRequestDate());
        holder.tvisManual.setText(date);
        updateFlag = false;
        boolean isUpadated = false;

        if (data.get(index).getIsManualAttendance().equals("1") || data.get(index).getIsManualAttendance().equals("2")) {
            holder.tvCheckOut.setVisibility(View.GONE);

            holder.tvCheckOutDisp.setVisibility(View.GONE);
            holder.btnOk.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
            holder.tvUpdateTime.setVisibility(View.GONE);
            holder.tvCheckInDisp.setText("Manual Attendance : ");

            if (data.get(index).getIsManualAttendance().equals("1")) {
                holder.tvCheckIn.setText("Full Day");
            } else {
                holder.tvCheckIn.setText("Half Day");
            }
        } else {
            holder.tvCheckInDisp.setText("In: ");
            holder.btnOk.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
            holder.tvUpdateTime.setVisibility(View.VISIBLE);
           /* if (indexNew == index) {
                holder.tvUpdateTime.setVisibility(View.GONE);
                holder.btnOk.setVisibility(View.VISIBLE);
                holder.btnCancel.setVisibility(View.VISIBLE);
            } else {
                holder.tvUpdateTime.setVisibility(View.VISIBLE);
                holder.btnOk.setVisibility(View.GONE);
                holder.btnCancel.setVisibility(View.GONE);
            }*/

            if (data.get(index).getIsPresent().equals("1")) {
                holder.tvCheckOut.setVisibility(View.GONE);
                holder.tvCheckOutDisp.setVisibility(View.GONE);
            } else {
                holder.tvCheckOut.setVisibility(View.VISIBLE);
                holder.tvCheckOutDisp.setVisibility(View.VISIBLE);
            }
            String timeIN = Util.gmtToLocalTime("" + data.get(index).getNewTimeIn());
            holder.tvCheckIn.setText(timeIN);

        }

        if(data.get(index).getCheckInApprovalState().equals("2")||data.get(index).getCheckOutApprovalState().equals("2")){
            if (data.get(index).getIsManualAttendance().equals("1") || data.get(index).getIsManualAttendance().equals("2")){
                holder.tvCheckInDisp.setTextColor(Color.parseColor("#FFD700"));
                holder.tvCheckIn.setTextColor(Color.parseColor("#FFD700"));
            }else{
                isUpadated = false;
                /*if(data.get(index).getUpdateTimeIn().equals(data.get(index).getNewTimeIn())){
                    holder.tvCheckInDisp.setTextColor(Color.parseColor("#000000"));
                    holder.tvCheckIn.setTextColor(Color.parseColor("#000000"));
                }else{
                    holder.tvCheckInDisp.setTextColor(Color.parseColor("#FFD700"));
                    holder.tvCheckIn.setTextColor(Color.parseColor("#FFD700"));
                    isUpadated= true;
                }*/

                if(data.get(index).getCheckInApprovalState().equals("2")){
                    holder.tvCheckInDisp.setTextColor(Color.parseColor("#FFD700"));
                    holder.tvCheckIn.setTextColor(Color.parseColor("#FFD700"));
                    isUpadated= true;
                }else{
                    holder.tvCheckInDisp.setTextColor(Color.parseColor("#000000"));
                    holder.tvCheckIn.setTextColor(Color.parseColor("#000000"));
                }

                if(data.get(index).getCheckOutApprovalState().equals("2")){
                    holder.tvCheckOut.setTextColor(Color.parseColor("#FFD700"));
                    holder.tvCheckOutDisp.setTextColor(Color.parseColor("#FFD700"));
                    isUpadated = true;
                }else{
                    holder.tvCheckOut.setTextColor(Color.parseColor("#000000"));
                    holder.tvCheckOutDisp.setTextColor(Color.parseColor("#000000"));
                }
               /* if(data.get(index).getUpdateTimeOut().equals(data.get(index).getNewTimeOut())){
                    holder.tvCheckOut.setTextColor(Color.parseColor("#000000"));
                    holder.tvCheckOutDisp.setTextColor(Color.parseColor("#000000"));
                }else{
                    holder.tvCheckOut.setTextColor(Color.parseColor("#FFD700"));
                    holder.tvCheckOutDisp.setTextColor(Color.parseColor("#FFD700"));
                    isUpadated = true;
                }*/

                if(!isUpadated){
                    holder.tvCheckInDisp.setTextColor(Color.parseColor("#FFD700"));
                    holder.tvCheckIn.setTextColor(Color.parseColor("#FFD700"));
                    holder.tvCheckOut.setTextColor(Color.parseColor("#FFD700"));
                    holder.tvCheckOutDisp.setTextColor(Color.parseColor("#FFD700"));
                }
            }

            holder.tvUpdateTime.setVisibility(View.GONE);
        }else{

            Log.e(TAG,"Inside getview @@@@@@@@@@@ approval#############################");
            holder.tvCheckInDisp.setTextColor(Color.parseColor("#000000"));
            holder.tvCheckOut.setTextColor(Color.parseColor("#000000"));
            holder.tvCheckOutDisp.setTextColor(Color.parseColor("#000000"));
            holder.tvCheckIn.setTextColor(Color.parseColor("#000000"));
        }
        holder.tvUpdateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // updateFlag = true;
                indexNew = index;
                Intent i = new Intent(mContext, UpdateTimeActivity.class);
                i.putExtra("data",data.get(index).getJsonObj());
                mContext.startActivity(i);
               /* holder.btnOk.setVisibility(View.VISIBLE);
                holder.btnCancel.setVisibility(View.VISIBLE);
                holder.tvUpdateTime.setVisibility(View.GONE);
                tempTimeIn = data.get(index).getNewTimeIn();
                tempTimeOut = data.get(index).getNewTimeOut();
                oldTimeOut = data.get(index).getNewTimeOut();
                oldTimeIn = data.get(index).getNewTimeIn();
                holder.tvCheckOut.setClickable(true);
                holder.tvCheckIn.setClickable(true);
                notifyDataSetChanged();
                String am_pm;

                try {
                    String[] startTime = holder.tvCheckIn.getText().toString().split(":");

                    String[] tempTime = startTime[1].toString().split(" ");
                    am_pm = tempTime[1];
                    if (am_pm.toLowerCase().equals("pm")) {
                        startCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTime[0]));
                        if (!startTime[0].equals("12"))
                            startCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTime[0]) + 12);

                    } else {
                        startCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTime[0]));
                        if (startTime[0].equals("12"))
                            startCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(startTime[0]) - 12);

                    }

                    startCalendar.set(Calendar.MINUTE, Integer.parseInt(tempTime[0]));


                    String[] endTime = holder.tvCheckOut.getText().toString().split(":");
                    //endCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTime[0]));
                    String[] tempTime1 = endTime[1].toString().split(" ");
                    am_pm = tempTime1[1];

                    if (am_pm.toLowerCase().equals("pm")) {
                        endCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTime[0]));
                        if (!endTime[0].equals("12"))
                            endCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTime[0]) + 12);

                    } else {
                        endCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTime[0]));
                        if (endTime[0].equals("12"))
                            endCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(endTime[0]) - 12);

                    }
                    endCalendar.set(Calendar.MINUTE, Integer.parseInt(tempTime1[0]));

                } catch (Exception e) {
                    e.printStackTrace();
                }
*/
            }
        });
        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // updateFlag = true;
                indexNew = -1;
                holder.btnOk.setVisibility(View.GONE);
                holder.btnCancel.setVisibility(View.GONE);
                holder.tvUpdateTime.setVisibility(View.VISIBLE);
                holder.tvCheckOut.setClickable(false);
                holder.tvCheckIn.setClickable(false);
                data.get(index).setNewTimeIn(tempTimeIn);
                data.get(index).setNewTimeOut(tempTimeOut);
               /* startCalendar = Calendar.getInstance(TimeZone.getDefault());
                endCalendar = Calendar.getInstance(TimeZone.getDefault());*/
                notifyDataSetChanged();
            }
        });
        holder.btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // updateFlag = true;
                indexNew = -1;
                holder.btnOk.setVisibility(View.GONE);
                holder.btnCancel.setVisibility(View.GONE);
                holder.tvUpdateTime.setVisibility(View.VISIBLE);
                holder.tvCheckOut.setClickable(false);
                holder.tvCheckIn.setClickable(false);

          /*      holder.tvCheckOut.setVisibility(View.GONE);
                holder.tvCheckOutDisp.setVisibility(View.GONE);*/
                if(holder.tvCheckOut.getVisibility()==View.VISIBLE && holder.tvCheckOutDisp.getVisibility()==View.VISIBLE){
                    if (startCalendar.after(endCalendar)) {
                        Log.e(TAG, "TimeIn value is:- &&^^&" + tempTimeIn + "******* Timeout value is:- " + tempTimeOut + "Index is:- " + index);
                        data.get(index).setNewTimeIn(oldTimeIn);
                        data.get(index).setNewTimeOut(oldTimeOut);
                        Toast.makeText(mContext, "CheckIn time should be lesser than or equal to checkOut time", Toast.LENGTH_SHORT).show();
                    } else {
                        if (Util.isOnline(mContext)) {
                           // new updateAttendanceTime(index, mContext).execute();
                        } else {
                            //Toast.makeText(mContext, R.string.network_error,Toast.LENGTH_SHORT);
                            try {
                                JSONObject jObj = new JSONObject();
                                jObj.put("UserID", "" + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID));
                                jObj.put("OldCheckInTime", "" + data.get(index).getTimeIN());
                                jObj.put("OldCheckOutTime", "" + data.get(index).getTimeOut());
                                jObj.put("NewCheckInTime", "" + data.get(index).getNewTimeIn());
                                jObj.put("NewCheckOutTime", "" + data.get(index).getNewTimeOut());

                                String strJson = jObj.toString();
                                String actionName = "updateCheckInOutTime";
                                String methodType = "2";


                                Util.insertIntoOffline(strJson, methodType, actionName, mContext);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }else{
                    Log.e(TAG, "1111111111111111111111111111111111111111111111111111");
                    if (Util.isOnline(mContext)) {
                        //new updateAttendanceTime(index, mContext).execute();
                    } else {
                        //Toast.makeText(mContext, R.string.network_error,Toast.LENGTH_SHORT);
                        try {
                            JSONObject jObj = new JSONObject();
                            jObj.put("UserID", "" + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID));
                            jObj.put("OldCheckInTime", "" + data.get(index).getTimeIN());
                            jObj.put("OldCheckOutTime", "" + data.get(index).getTimeOut());
                            jObj.put("NewCheckInTime", "" + data.get(index).getNewTimeIn());
                            jObj.put("NewCheckOutTime", "" + data.get(index).getNewTimeOut());

                            String strJson = jObj.toString();
                            String actionName = "updateCheckInOutTime";
                            String methodType = "2";

                            Util.insertIntoOffline(strJson, methodType, actionName, mContext);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                }

               /* startCalendar = Calendar.getInstance(TimeZone.getDefault());
                endCalendar = Calendar.getInstance(TimeZone.getDefault());*/
                notifyDataSetChanged();
            }
        });

        holder.tvCheckIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (holder.tvUpdateTime.getVisibility() == View.GONE && holder.btnOk.getVisibility()== View.VISIBLE) {
                    if (!(data.get(index).getIsManualAttendance().equals("1") || data.get(index).getIsManualAttendance().equals("2"))) {
                        indexNew = index;
                        Dialog dialog = onCreateDialog(START_TIME, "" + holder.tvCheckIn.getText());
                        dialog.show();
                    }
                }

            }
        });
        holder.tvCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (holder.tvUpdateTime.getVisibility() == View.GONE && holder.btnOk.getVisibility()== View.VISIBLE) {
                    if(!(data.get(index).getIsManualAttendance().equals("1") || data.get(index).getIsManualAttendance().equals("2"))){
                        indexNew = index;
                        Dialog dialog = onCreateDialog(END_TIME, "" + holder.tvCheckOut.getText());
                        dialog.show();
                    }

                }


            }
        });

        myToTimeListener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker timePicker, int i, int i2) {


                endCalendar.set(calendar.HOUR_OF_DAY, i);
                endCalendar.set(calendar.MINUTE, i2);
                SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
                String sdate = sdf1.format(endCalendar.getTime());
                //holder.tvCheckOut.setText("***"+sdate);
                updateStatusTimeOut(indexNew, sdate);
                // Log.v(TAG,"Inside time listener myToTimeListener *************   "+sdate);
               /* if (startCalendar.before(endCalendar)) {


                } *//*else {
                *//*tvCheckIn.setText(getResources().getText(R.string.datetimeformat));
                tvCheckOut.setText(getResources().getText(R.string.datetimeformat));*//*
                Toast.makeText(mContext, "Reselect Date and Time", Toast.LENGTH_SHORT).show();
            }*/
            }
        };
        myfromTimeListener = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker timePicker, int i, int i2) {

                startCalendar.set(calendar.HOUR_OF_DAY, i);
                startCalendar.set(calendar.MINUTE, i2);
                SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
                String sdate = sdf1.format(startCalendar.getTime());
                // Log.v(TAG,"Inside timelistner myfromTimeListener *************   "+sdate);
                // holder.tvCheckIn.setText(sdate);

                updateStatusTimeIn(indexNew, sdate);

            }
        };

        String strImage = "" + data.get(index).getImageUrl();
        imageLoader.displayImage(strImage, holder.imgUser, options);
        return view;
    }


    static class ViewHolder {

        @InjectView(R.id.title)
        TextView tvUserName;
        @InjectView(R.id.date)
        TextView tvisManual;
        @InjectView(R.id.tvCheckInTime)
        TextView tvCheckIn;
        @InjectView(R.id.tvCheckOutTime)
        TextView tvCheckOut;
        @InjectView(R.id.tvCheckOut)
        TextView tvCheckOutDisp;
        @InjectView(R.id.tvCheckIn)
        TextView tvCheckInDisp;
        @InjectView(R.id.img)
        ImageView imgUser;
        @InjectView(R.id.tvUpdateTime)
        TextView tvUpdateTime;
        @InjectView(R.id.btn_ok)
        Button btnOk;
        @InjectView(R.id.btn_cancel)
        Button btnCancel;
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

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }


    protected Dialog onCreateDialog(int id, String time) {

        if (id == END_TIME) {

            return new TimePickerDialog(mContext, myToTimeListener, endCalendar.get(endCalendar.HOUR_OF_DAY), endCalendar.get(endCalendar.MINUTE), true);

        }
        if (id == START_TIME) {
            return new TimePickerDialog(mContext, myfromTimeListener, startCalendar.get(startCalendar.HOUR_OF_DAY), startCalendar.get(startCalendar.MINUTE), true);
        }
        return null;
    }
/*

    class updateAttendanceTime extends AsyncTask<Void, String, String> {
        String response;
        int pos;

        Context context;

        public updateAttendanceTime(int index, Context context) {
            this.pos = index;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (pd != null) {
                pd.show();
            }

        }

        @Override
        protected String doInBackground(Void... params) {

            List<NameValuePair> params1 = new ArrayList<NameValuePair>(2);
            params1.add(new BasicNameValuePair("UserID", "" + "" + Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID)));
            params1.add(new BasicNameValuePair("OldCheckInTime", "" + data.get(pos).getTimeIN()));
            params1.add(new BasicNameValuePair("OldCheckOutTime", "" + data.get(pos).getTimeOut()));
            params1.add(new BasicNameValuePair("NewCheckInTime", "" + data.get(pos).getNewTimeIn()));
            params1.add(new BasicNameValuePair("NewCheckOutTime", "" + data.get(pos).getNewTimeOut()));

            Log.e("params1", ">>" + params1);
            response = Util.makeServiceCall(Constant.URL + "updateCheckInOutTime", 2, params1, context);

            Log.e(TAG, "response is:- >>>" + response);
            return response;
        }

        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (pd != null) {
                pd.dismiss();
            }
            try {
                JSONObject jObj = new JSONObject(result);
                String status = jObj.optString("status");
                Toast.makeText(mContext, "" + jObj.getString("message"), Toast.LENGTH_LONG).show();
                if (status.equals("Success")) {
                   *//* data.get(pos).setNewTimeIn(tempTimeIn);
                    data.get(pos).setNewTimeOut(tempTimeOut);*//*
                    data.get(pos).setApprovalState("2");
                    Log.e(TAG,"Inside post execute !!!!!!!!!!!!!!!!!!");
                }else{
                    data.get(pos).setNewTimeIn(tempTimeIn);
                    data.get(pos).setNewTimeOut(tempTimeOut);
                }
                notifyDataSetChanged();
                Log.e(TAG,"Inside post execute @@@@@@@@@@!!!!!!!!!!!!!!!!!!");
            } catch (Exception e) {
                e.printStackTrace();

            }
        }

    }*/

}
