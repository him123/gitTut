package com.bigbang.superteam.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bigbang.superteam.DisplayFullscreenImage;
import com.bigbang.superteam.R;
import com.bigbang.superteam.dataObjs.Project;
import com.bigbang.superteam.dataObjs.WorkItem;
import com.bigbang.superteam.dataObjs.WorkTransaction;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UpdateWorkListAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    Activity mContext;
    ArrayList<WorkTransaction> dataArray;
    DisplayImageOptions options;
    ImageLoader imageLoader = ImageLoader.getInstance();
    WorkItem workItem;
    String TAG = "UpdateWorkListAdapter";

    SQLiteDatabase db;

    public UpdateWorkListAdapter(Activity mainActivity, ArrayList<WorkTransaction> dataList, WorkItem workItem) {
        this.mContext = mainActivity;
        this.dataArray = dataList;
        this.workItem = workItem;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        options = new DisplayImageOptions.Builder()
//                .displayer(new RoundedBitmapDisplayer(150))
                .showImageOnLoading(R.drawable.default_image).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image)
                .cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        imageLoader.init(ImageLoaderConfiguration.createDefault(mainActivity));

        SQLiteHelper helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();
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
            convertView = inflater.inflate(R.layout.listraw_workupdate, null);
            holder = new Holder(convertView);
            holder.taskImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (workItem.getTaskImage() != null && workItem.getTaskImage().length() > 4) {
                        Intent myIntent = new Intent(mContext, DisplayFullscreenImage.class);
                        myIntent.putExtra("URLURI", workItem.getTaskImage());
                        mContext.startActivity(myIntent);
                    }
                }
            });
        } else {
            holder = (Holder) convertView.getTag();
        }

        if (position == 0) {
            holder.titleView.setVisibility(View.VISIBLE);
            holder.parentContainer.setVisibility(View.GONE);
            setViewWorkItem(holder);
        } else {
            holder.titleView.setVisibility(View.GONE);
            holder.parentContainer.setVisibility(View.VISIBLE);

            try {
                Calendar createCalendar = Calendar.getInstance();
                createCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(dataArray.get(position).getCreated_on())));
                String sdate = Util.SDF.format(createCalendar.getTime());
                holder.tvCreatedOn.setText(sdate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.tvUserName.setText("" + Util.getUsernames("" + dataArray.get(position).getUser_code(), mContext));
            Log.e("Update", "Des:" + dataArray.get(position).getDiscription() + " type:" + dataArray.get(position).getUpdate_type() + " Messagetype:" + dataArray.get(position).getMessage_type());
            if (dataArray.get(position).getMessage_type().equals(WorkTransaction.TEXT)) {
//            {"Done", "Postponed", "Query", "Delegate", "Information", "Expense"};
                if (dataArray.get(position).getUpdate_type().equals("Postponed")) {
                    String msg = mContext.getString(R.string.postponed_new_dates_are) + " : " + Util.utcToLocalTime1(dataArray.get(position).getStart_date()) + " to " + Util.utcToLocalTime1(dataArray.get(position).getEnd_date());
                    try {
                        String qry = "select " + WorkItem.TASK_TYPE + " from " + Constant.WorkItemTable + " where " + WorkItem.TASK_ID + " = " + dataArray.get(position).getTask_code();
                        Cursor crsr = db.rawQuery(qry, null);
                        if (crsr != null) {
                            crsr.moveToFirst();
                            String taskType = "" + "" + crsr.getString(crsr.getColumnIndex(WorkItem.TASK_TYPE));
                            if (taskType.equals("Regular")) {
                                msg = mContext.getString(R.string.postponed_new_times_are) + " : " + Util.utcToLocalTime2(dataArray.get(position).getStart_date()) + " to " + Util.utcToLocalTime2(dataArray.get(position).getEnd_date());
                            }
                            crsr.close();
                        }
                        if (crsr != null) crsr.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Log.e("Postponed", ">>" + msg + mContext.getString(R.string.for_) + dataArray.get(position).getDiscription());
                    holder.tv.setText(msg + mContext.getString(R.string.for_) + dataArray.get(position).getDiscription());
                } else if (dataArray.get(position).getUpdate_type().equals(mContext.getString(R.string.delegate))) {
                    String userNames = Util.getUsernames(dataArray.get(position).getDelegate_to(), mContext);
                    String msg = mContext.getString(R.string.delegate) + ":" + userNames + mContext.getString(R.string.for_) + dataArray.get(position).getDiscription();
                    holder.tv.setText(msg);
                } else if (dataArray.get(position).getUpdate_type().equals("Expense")) {
                    String msg = mContext.getString(R.string.expense_details) + ": " + dataArray.get(position).getAmount() + mContext.getString(R.string.occured_for) + dataArray.get(position).getDiscription() + mContext.getString(R.string.On) + " " + Util.utcToLocalTime(dataArray.get(position).getInvoice_date()).substring(0, 11);
                    holder.tv.setText(msg);
                } else if (dataArray.get(position).getUpdate_type().equals("Query")) {
                    String txt1 = mContext.getString(R.string.query) + ": " + dataArray.get(position).getDiscription() + " to ";
                    String userNames = Util.getUsernames(dataArray.get(position).getDelegate_to(), mContext);
                    String msg = mContext.getString(R.string.query) + ": " + dataArray.get(position).getDiscription() + " to " + userNames;
//                    holder.tv.setText(msg);
                    Spannable spannable = new SpannableString(msg);
                    spannable.setSpan(new ForegroundColorSpan(Color.BLUE), txt1.length(), (txt1 + userNames).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    holder.tv.setText(spannable, TextView.BufferType.SPANNABLE);

                } else if (dataArray.get(position).getUpdate_type().equals("Done")) {
                    holder.tv.setText(mContext.getString(R.string.done) + ": " + dataArray.get(position).getMessage());
                } else
                    holder.tv.setText(dataArray.get(position).getMessage());

                String updateType = dataArray.get(position).getUpdate_type();
                String status = dataArray.get(position).getUpdate_type();
                if (status != null && status.equals(Constant.offline))
                    holder.tv.setBackgroundColor(mContext.getResources().getColor(R.color.gray));
                else if (updateType != null || !updateType.equals("null")) {
                    if (updateType.equals("Postponed") || updateType.equals("Delegate") || updateType.equals("Expense") || updateType.equals("Done") || updateType.equals("Information")) {
                        if (dataArray.get(position).getStatus().length() > 0) {
                            if (dataArray.get(position).getStatus().equals("Approved")) {
                                holder.tv.setBackgroundColor(mContext.getResources().getColor(R.color.appoved_color));
                            } else if (dataArray.get(position).getStatus().equals("Rejected")) {
                                holder.tv.setBackgroundColor(mContext.getResources().getColor(R.color.rejected));
                            } else if (dataArray.get(position).getStatus().equals("Pending")) {
                                holder.tv.setBackgroundColor(mContext.getResources().getColor(R.color.pending));
                            } else {
                                holder.tv.setBackgroundColor(mContext.getResources().getColor(R.color.gray));
                            }
                        }
                    } else {
                        holder.tv.setBackgroundColor(mContext.getResources().getColor(R.color.appoved_color));
                    }
                } else {
                    if (dataArray.get(position).getStatus().length() > 0) {
                        if (dataArray.get(position).getStatus().equals("Approved")) {
                            holder.tv.setBackgroundColor(mContext.getResources().getColor(R.color.appoved_color));
                        } else if (dataArray.get(position).getStatus().equals("Rejected")) {
                            holder.tv.setBackgroundColor(mContext.getResources().getColor(R.color.rejected));
                        } else if (dataArray.get(position).getStatus().equals("Pending")) {
                            holder.tv.setBackgroundColor(mContext.getResources().getColor(R.color.pending));
                        }
                    }
                }

                holder.img.setVisibility(View.GONE);
                holder.tv.setVisibility(View.VISIBLE);
                holder.soundBar.setVisibility(View.GONE);
            } else if (dataArray.get(position).getMessage_type().equals(WorkTransaction.IMAGE)) {
                System.out.println("going to display image:" + dataArray.get(position).getLink());
                if (dataArray.get(position).getLink() != null && dataArray.get(position).getLink().length() > 4) {
                    imageLoader.displayImage(dataArray.get(position).getLink(), holder.img, options);
                }
                holder.img.setVisibility(View.VISIBLE);
                holder.tv.setVisibility(View.GONE);
                holder.soundBar.setVisibility(View.GONE);
                holder.img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent myIntent = new Intent(mContext, DisplayFullscreenImage.class);
                        myIntent.putExtra("URLURI", dataArray.get(position).getLink());
                        mContext.startActivity(myIntent);
                        mContext.overridePendingTransition(R.anim.enter_from_bottom,
                                R.anim.hold_bottom);
                    }
                });
            } else // Sound
            {
                if (dataArray.get(position).mp == null) {
                    holder.img.setVisibility(View.GONE);
                    holder.tv.setVisibility(View.VISIBLE);
                    holder.tv.setText(mContext.getString(R.string.downloading_attachment));
                    holder.soundBar.setVisibility(View.GONE);
                } else {
                    holder.img.setVisibility(View.GONE);
                    holder.tv.setVisibility(View.GONE);
                    holder.soundBar.setVisibility(View.VISIBLE);
                    holder.seekBar.setMax(dataArray.get(position).mp.getDuration());
                    holder.soundView.setText("" + dataArray.get(position).mp.getDuration());
                    dataArray.get(position).mp
                            .setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                                @Override
                                public void onCompletion(MediaPlayer mp) {
                                    holder.soundBtn.setBackgroundResource(R.drawable.play);
                                    holder.seekBar.setProgress(0);
                                }
                            });
                    holder.soundBtn.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            if (dataArray.get(position).mp.isPlaying()) {
                                dataArray.get(position).mp.pause();
                                holder.soundBtn.setBackgroundResource(R.drawable.play);

                            } else {
                                dataArray.get(position).mp.start();
                                setSeek(dataArray.get(position).mp, holder.seekBar,
                                        holder.soundView);
                                holder.soundBtn.setBackgroundResource(R.drawable.pause);
                            }
                        }
                    });

                    double startTime = dataArray.get(position).mp.getCurrentPosition();
                    holder.seekBar.setProgress((int) startTime);
                    holder.soundView.setText(String.format(
                            "%d:%d",
                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) startTime)
                                    - TimeUnit.MINUTES
                                    .toSeconds(TimeUnit.MILLISECONDS
                                            .toMinutes((long) startTime))));
                }
            }
            if (dataArray.get(position).getUser_code() == Integer.parseInt(Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID))) {
                holder.parentContainer.setGravity(Gravity.RIGHT);
                holder.tvUserName.setVisibility(View.GONE);
                holder.tvUserName1.setVisibility(View.VISIBLE);
                holder.tvUserName1.setText(mContext.getString(R.string.me));
            } else {
                holder.parentContainer.setGravity(Gravity.LEFT);
                holder.tvUserName.setVisibility(View.VISIBLE);
                holder.tvUserName1.setVisibility(View.GONE);
            }
        }
        convertView.setTag(holder);
        return convertView;
    }

    private void setViewWorkItem(Holder holder) {
        holder.tvTitle.setText("" + workItem.getTitle());
        holder.tvDescription.setText("" + workItem.getDescription());
        holder.tvBudget.setText("" + workItem.getBudget());
        holder.tvPriority.setText("" + workItem.getPriority());
        holder.tvLocation.setText("" + workItem.getWorkLocation());

        try {
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(workItem.getStartDate())));
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(workItem.getEndDate())));
            String sdate = Util.SDF.format(startCalendar.getTime());
            holder.tvStartTime.setText("" + sdate);
            sdate = Util.SDF.format(endCalendar.getTime());
            holder.tvEndTime.setText("" + sdate);


            String s = GetEstimatedWorkTime(workItem.getEstimatedWorkTime());
            holder.tvActualTime.setText("" + s);

            String userNames = Util.getUsernames(workItem.getTaskAssignedTo(), mContext);
            holder.tvAssignTo.setText("" + userNames);

            userNames = Util.getUsernames(workItem.getTaskCCTo(), mContext);
            holder.tvCCTo.setText("" + userNames);

            holder.tvworkType.setText("" + workItem.getTaskType());
            String workType = workItem.getTaskType();

            if (workType.equals("Project")) //For Project
            {
                setViewProject(holder);
            } else if (workType.equals("Regular")) {
                setViewRegular(holder);
            } else if (workType.equals("One Time")) {
                setViewOneTime(holder);
            } else if (workType.equals("Service Call")) {
                setViewServiceAndSalesCall(holder);
            } else if (workType.equals("Sales Call")) {
                setViewServiceAndSalesCall(holder);
            } else if (workType.equals("Shopping/Purchase")) {
                Log.e("View Work Item", "Setting View Shipping purchase");
                setViewShippingAndPurchase(holder);
            } else if (workType.equals("Collection")) {
                setViewCollection(holder);
            }
            holder.tvCreatedBy.setText(Util.getUsernames(workItem.getUserCode() + "", mContext));
            if (workItem.getTaskImage() != null && workItem.getTaskImage().length() > 4) {
                imageLoader.displayImage(workItem.getTaskImage(), holder.taskImageView);
            } else {
                int counter = 0;
                char c = workItem.getTitle().toUpperCase().charAt(counter);
                while (c == ' ') {
                    counter++;
                    c = workItem.getTitle().charAt(counter);
                }
                holder.taskImageView.setImageResource(Util.getimgSrc(c));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setViewOneTime(Holder holder) {
        holder.optionalLayout.setVisibility(View.GONE);
    }

    private void setViewRegular(Holder holder) {
        holder.optionalLayout.setVisibility(View.VISIBLE);
        holder.op1.setText(mContext.getString(R.string.frequency));
        holder.op11.setText("" + workItem.getFrequency());
        holder.op2.setText(mContext.getString(R.string.days));
        if (workItem.getFrequency().equals("Weekly")) {
            holder.op22.setText("" + getDaysString(workItem.getDayCodesSelected()));
        } else if (workItem.getFrequency().equals("Monthly")) {
            holder.op22.setText("" + workItem.getDayCodesSelected());
        } else {
            holder.raw2.setVisibility(View.GONE);
        }

        holder.raw3.setVisibility(View.GONE);
        holder.raw4.setVisibility(View.GONE);
        holder.raw5.setVisibility(View.GONE);
        holder.raw6.setVisibility(View.GONE);
        holder.raw7.setVisibility(View.GONE);
        try {
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(workItem.getStartDate())));
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(workItem.getEndDate())));
            String sdate = Util.SDF.format(startCalendar.getTime());
            holder.tvStartTime.setText("" + sdate.substring(11));
            sdate = Util.SDF.format(endCalendar.getTime());
            holder.tvEndTime.setText("" + sdate.substring(11));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String getDaysString(String dayCodesSelected) {
        try {
            if (dayCodesSelected != null && dayCodesSelected.length() > 0) {
                String[] days = dayCodesSelected.split(",");
                if (days.length > 0) {
                    String str = "";
                    for (int i = 0; i < days.length; i++) {
                        int code = Integer.parseInt(days[i]);
                        if (i < days.length - 1)
                            str = str + mContext.getResources().getStringArray(R.array.week_days)[code] + ", ";
                        else
                            str = str + mContext.getResources().getStringArray(R.array.week_days)[code] + " ";
                    }
                    if (str.length() > 0)
                        return str;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private boolean isNum(String customerName) {
        try {
            int x = Integer.parseInt(customerName);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String getCustomername(String customerName) {
        Cursor cursor = db.rawQuery("select * from " + Constant.tableCustomers + " where Id like " + customerName, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                Log.e("Name Founded Customer", " ::" + cursor.getString(cursor.getColumnIndex("Name")));
                return cursor.getString(cursor.getColumnIndex("Name"));
            }
        }
        if (cursor != null) cursor.close();
        return customerName;
    }

    private void setViewServiceAndSalesCall(Holder holder) {
        holder.optionalLayout.setVisibility(View.VISIBLE);
        holder.op1.setText(mContext.getString(R.string.customer_name));
        if (isNum(workItem.getCustomerName())) {
            holder.op11.setText("" + getCustomername(workItem.getCustomerName()));
        } else {
            holder.op11.setText("" + workItem.getCustomerName());
        }
        holder.op2.setText(mContext.getString(R.string.customer_contact));
        holder.op22.setText("" + workItem.getCustomerContact());
        holder.op3.setText(mContext.getString(R.string.customer_type));
        holder.op33.setText("" + workItem.getCustomerType());
        if (workItem.getCustomerType().equals(Constant.REPEAT)) {
            holder.op33.setText("" + mContext.getString(R.string.repeat_call));
        } else {
            holder.op33.setText("" + mContext.getString(R.string.new_call));
        }

        holder.op4.setText(mContext.getString(R.string.past_history));
        holder.op44.setText("" + workItem.getPastHistory());
        holder.raw5.setVisibility(View.GONE);
        holder.raw6.setVisibility(View.GONE);
        holder.raw7.setVisibility(View.GONE);
    }

    private void setViewShippingAndPurchase(Holder holder) {
        holder.optionalLayout.setVisibility(View.VISIBLE);
        holder.op1.setText(mContext.getString(R.string.vendor_name));
        if (workItem.getSpVendorName().contains(",") || isNum(workItem.getSpVendorName())) {
            holder.op11.setText("" + getVendorNames(workItem.getSpVendorName()));
        } else {
            holder.op11.setText(workItem.getSpVendorName());
        }
        holder.op2.setText(mContext.getString(R.string.vensor_selection));
        holder.op22.setText("" + workItem.getSpVendorPreference());
        holder.op3.setText(mContext.getString(R.string.advance_paid));
        holder.op33.setText("" + workItem.getSpAdvancePaid());
        holder.raw4.setVisibility(View.GONE);
        holder.raw5.setVisibility(View.GONE);
        holder.raw6.setVisibility(View.GONE);
        holder.raw7.setVisibility(View.GONE);
    }

    private String getVendorNames(String spVendorName) {

        String[] ids = spVendorName.split(",");
        String str = "";
        for (int i = 0; i < ids.length; i++) {
            Cursor cursor = db.rawQuery("select * from " + Constant.tableVendors + " where ID like " + ids[i], null);
            if (cursor != null) {
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    Log.e("Name Founded Customer", " ::" + cursor.getString(cursor.getColumnIndex("Name")));
                    if (i == ids.length - 1)
                        str = str + cursor.getString(cursor.getColumnIndex("Name"));
                    else
                        str = str + cursor.getString(cursor.getColumnIndex("Name")) + ", ";
                }
            }
            if (cursor != null) cursor.close();
        }
        if (str.length() > 0)
            if (str.charAt(str.length() - 1) == ',')
                return str.substring(0, str.length());
            else
                return str;
        else
            return spVendorName;
    }

    private void setViewCollection(Holder holder) {
        holder.optionalLayout.setVisibility(View.VISIBLE);
        holder.op1.setText(mContext.getString(R.string.customer_name));
        holder.op2.setText(mContext.getString(R.string.customer_contact));
        holder.op3.setText(mContext.getString(R.string.invoice_amount));
        holder.op4.setText(mContext.getString(R.string.invoice_date));
        holder.op5.setText(mContext.getString(R.string.due_date));
        holder.op6.setText(mContext.getString(R.string.outstanding_amount));

        if (isNum(workItem.getCustomerName())) {
            holder.op11.setText("" + getCustomername(workItem.getCustomerName()));
        } else {
            holder.op11.setText("" + workItem.getCustomerName());
        }
        holder.op22.setText("" + workItem.getCustomerContact());
        holder.op33.setText("" + workItem.getInvoiceAmount());
        try {
            Calendar invCalendar = Calendar.getInstance();
            invCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(workItem.getInvoiceDate())));
            String sdate = Util.SDF.format(invCalendar.getTime());
            holder.op44.setText("" + sdate.substring(0, 11));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            Calendar dueCalendar = Calendar.getInstance();
            dueCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(workItem.getDueDate())));
            String sdate = Util.SDF.format(dueCalendar.getTime());
            holder.op55.setText("" + sdate.substring(0, 11));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.op66.setText("" + workItem.getOutStandingAmt());
        holder.raw7.setVisibility(View.GONE);
    }

    private void setViewProject(Holder holder) {
        holder.op1.setText("" + mContext.getString(R.string.project));
        holder.op2.setText("" + mContext.getString(R.string.depent_workItems));

        holder.op11.setText("" + workItem.getProjectCode());
        String name = getProjectName(workItem.getProjectCode());
        if (name.length() > 0) holder.op11.setText(name);
        if (workItem.getTaskCodeAfter() != null && !workItem.getTaskCodeAfter().equals("null"))
            holder.op22.setText("" + workItem.getTaskCodeAfter());
        name = getTaskNames(workItem.getTaskCodeAfter());
        if (name.length() > 0) holder.op22.setText(name);

        holder.raw3.setVisibility(View.GONE);
        holder.raw4.setVisibility(View.GONE);
        holder.raw5.setVisibility(View.GONE);
        holder.raw6.setVisibility(View.GONE);
        holder.raw7.setVisibility(View.GONE);
    }

    private String getTaskNames(String taskCodeAfter) {
        if (taskCodeAfter != null && taskCodeAfter.length() > 0) {
            String tasks[] = taskCodeAfter.split(",");
            String tasknames = "";

            for (int i = 0; i < tasks.length; i++) {
                if (tasks[i] != null && isNum(tasks[i])) {
                    Cursor cursor = db.rawQuery("select * from " + Constant.WorkItemTable + " where " + WorkItem.TASK_ID + " =" + tasks[i], null);
                    if (cursor != null && cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        if (i == tasks.length - 1) {
                            tasknames = tasknames + " " + cursor.getString(cursor.getColumnIndex(WorkItem.TASK_NAME));
                        } else {
                            tasknames = tasknames + " " + cursor.getString(cursor.getColumnIndex(WorkItem.TASK_NAME)) + " " + ",";
                        }
                    }
                    if (cursor != null) cursor.close();
                }
            }
            if (tasknames.length() > 3)
                tasknames = tasknames.substring(0, tasknames.length());
            return tasknames;
        }
        return "";
    }

    private String getProjectName(int projectCode) {
        String query = "select * from " + Constant.ProjectTable + " where " + Project.PROJECT_ID + " = " + projectCode;
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex(Project.PROJECT_NAME));
        }
        if (cursor != null) cursor.close();
        return "";
    }


    protected void setSeek(final MediaPlayer mp, final SeekBar seekBar,
                           final TextView soundText) {
        final Handler myHandler = new Handler();
        Runnable UpdateSongTime = new Runnable() {
            public void run() {
                double startTime = mp.getCurrentPosition();

                soundText.setText(String.format(
                        "%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime)
                                - TimeUnit.MINUTES
                                .toSeconds(TimeUnit.MILLISECONDS
                                        .toMinutes((long) startTime))));
                seekBar.setProgress((int) startTime);
                myHandler.postDelayed(this, 100);
            }
        };
        myHandler.postDelayed(UpdateSongTime, 100);
    }

    private String GetEstimatedWorkTime(String estimatedWorkTime) {
        String str[] = estimatedWorkTime.split(",");
        if (str.length > 0) {
            return str[0] + ":Hours " + str[1] + ":Minutes";
        }
        return "";
    }

    public void PauseCalled() {
        for (int i = 0; i < dataArray.size(); i++) {
            if (dataArray != null && dataArray.size() > 1) {
                Log.e("Update Activity", "Pause called" + dataArray.get(i).getMessage_type());
                if (dataArray.get(i).getMessage_type() != null && dataArray.get(i).getMessage_type().equals(WorkTransaction.SOUND)) {
                    Log.e("Update Activity", "Pause called s1");
                    if (dataArray.get(i).mp != null) {
                        Log.e("Update Activity", "Pause called s2");
                        dataArray.get(i).mp.stop();
                    }
                }
            }
        }
    }

    public class Holder {
        @InjectView(R.id.tv_message)
        TextView tv;
        @InjectView(R.id.tv_soundText)
        TextView soundView;
        @InjectView(R.id.tv_userName)
        TextView tvUserName;
        @InjectView(R.id.tv_userName1)
        TextView tvUserName1;
        @InjectView(R.id.iv_workUpdateImage)
        ImageView img;
        @InjectView(R.id.btn_soundBtn)
        Button soundBtn;
        @InjectView(R.id.seek_Sound)
        SeekBar seekBar;
        @InjectView(R.id.ll_soundBar)
        LinearLayout soundBar;
        @InjectView(R.id.ll_rawParent)
        LinearLayout parentContainer;
        @InjectView(R.id.ll_titleView)
        LinearLayout titleView;
        @InjectView(R.id.tv_itemTitle)
        TextView tvTitle;
        /////////////////////
        @InjectView(R.id.tv_itemDescription)
        TextView tvDescription;
        @InjectView(R.id.tv_itemBudget)
        TextView tvBudget;
        @InjectView(R.id.tv_itemPriority)
        TextView tvPriority;
        @InjectView(R.id.tv_itemLocation)
        TextView tvLocation;
        @InjectView(R.id.tv_itemStarttime)
        TextView tvStartTime;
        @InjectView(R.id.tv_itemEndTime)
        TextView tvEndTime;
        @InjectView(R.id.tv_itemActualTime)
        TextView tvActualTime;
        @InjectView(R.id.tv_itemAssignedto)
        TextView tvAssignTo;
        @InjectView(R.id.tv_ccto)
        TextView tvCCTo;
        @InjectView(R.id.tv_itemTypeofWork)
        TextView tvworkType;
        @InjectView(R.id.tv_createdby)
        TextView tvCreatedBy;
        @InjectView(R.id.tv_createdOn)
        TextView tvCreatedOn;
        @InjectView(R.id.ll_optional)
        LinearLayout optionalLayout;
        @InjectView(R.id.ll_raw12)
        LinearLayout raw2;
        @InjectView(R.id.ll_raw13)
        LinearLayout raw3;
        @InjectView(R.id.ll_raw14)
        LinearLayout raw4;
        @InjectView(R.id.ll_raw15)
        LinearLayout raw5;
        @InjectView(R.id.ll_raw16)
        LinearLayout raw6;
        @InjectView(R.id.ll_raw17)
        LinearLayout raw7;
        @InjectView(R.id.tv_option1)
        TextView op1;
        @InjectView(R.id.tv_option11)
        TextView op11;
        @InjectView(R.id.tv_option2)
        TextView op2;
        @InjectView(R.id.tv_option22)
        TextView op22;
        @InjectView(R.id.tv_option3)
        TextView op3;
        @InjectView(R.id.tv_option33)
        TextView op33;
        @InjectView(R.id.tv_option4)
        TextView op4;
        @InjectView(R.id.tv_option44)
        TextView op44;
        @InjectView(R.id.tv_option5)
        TextView op5;
        @InjectView(R.id.tv_option55)
        TextView op55;
        @InjectView(R.id.tv_option6)
        TextView op6;
        @InjectView(R.id.tv_option66)
        TextView op66;
        @InjectView(R.id.iv_taskimage)
        ImageView taskImageView;
        DisplayImageOptions options;

        public Holder(View view) {
            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();
            Util.setAppFont(mContainer, mFont);
        }
    }

}
