package com.bigbang.superteam.task.adapter;


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
import com.bigbang.superteam.task.database.TaskMemberDAO;
import com.bigbang.superteam.task.model.TaskChat;
import com.bigbang.superteam.task.model.TaskMember;
import com.bigbang.superteam.task.model.TaskModel;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class UpdateTaskChatAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    Activity mContext;
    ArrayList<TaskChat> dataArray;
    DisplayImageOptions options;
    ImageLoader imageLoader = ImageLoader.getInstance();
    //    WorkItem workItem;
    TaskModel taskModel;
    String TAG = "UpdateWorkListAdapter";

    SQLiteDatabase db;
    TaskMemberDAO taskMemberDAO;

    public UpdateTaskChatAdapter(Activity mainActivity, ArrayList<TaskChat> dataList, TaskModel taskModel) {
        this.mContext = mainActivity;
        this.dataArray = dataList;
        this.taskModel = taskModel;
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
        taskMemberDAO = new TaskMemberDAO(mContext);
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
                    if (taskModel.attachments.get(position) != null && taskModel.attachments.get(position).length() > 4) {
                        Intent myIntent = new Intent(mContext, DisplayFullscreenImage.class);
                        myIntent.putExtra("URLURI", taskModel.attachments.get(position));
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
                createCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(dataArray.get(position).createdOn)));
                String sdate = Util.SDF.format(createCalendar.getTime());
                holder.tvCreatedOn.setText(sdate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.tvUserName.setText("" + dataArray.get(position).createdByName);

            //****************************  TEXT MESSAGES
            if (dataArray.get(position).dataType.equals(WorkTransaction.TEXT)) {
                holder.tv.setText(dataArray.get(position).message);
//                holder.tvCreatedOn.setText(dataArray.get(position).createdOn);

                String status = dataArray.get(position).chatType;
                if (status != null && status.equals(Constant.offline))
                    holder.tv.setBackgroundColor(mContext.getResources().getColor(R.color.gray));
                else if (dataArray.get(position).chatStatus.length() > 0) {
                    if (dataArray.get(position).chatStatus.equals("Approved")) {
                        holder.tv.setBackgroundColor(mContext.getResources().getColor(R.color.appoved_color));
                    } else if (dataArray.get(position).chatStatus.equals("Pending")) {
                        holder.tv.setBackgroundColor(mContext.getResources().getColor(R.color.pending));
                    }
                }
                holder.img.setVisibility(View.GONE);
                holder.tv.setVisibility(View.VISIBLE);
                holder.soundBar.setVisibility(View.GONE);
            }
            //****************************  IMAGE MESSAGES
            else if (dataArray.get(position).dataType.equals(WorkTransaction.IMAGE)) {
                String imagePath = dataArray.get(position).attachmentPath;
                System.out.println("going to display image:" + dataArray.get(position).attachmentPath);
                if (dataArray.get(position).attachmentPath != null && dataArray.get(position).attachmentPath.length() > 4) {
                    imageLoader.displayImage(dataArray.get(position).attachmentPath, holder.img, options);
                }
                holder.img.setVisibility(View.VISIBLE);
                holder.tv.setVisibility(View.GONE);
                holder.soundBar.setVisibility(View.GONE);
                holder.img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent myIntent = new Intent(mContext, DisplayFullscreenImage.class);
                        myIntent.putExtra("URLURI", dataArray.get(position).attachmentPath);
                        mContext.startActivity(myIntent);
                        mContext.overridePendingTransition(R.anim.enter_from_bottom,
                                R.anim.hold_bottom);
                    }
                });
            }
            //****************************  SOUND MESSAGES
            else {
                if (dataArray.get(position).mp == null) {
                    Log.e("","============================= mp null");
                    holder.img.setVisibility(View.GONE);
                    holder.tv.setVisibility(View.VISIBLE);
                    holder.tv.setText(mContext.getString(R.string.downloading_attachment));
                    holder.soundBar.setVisibility(View.GONE);
                } else {
                    Log.e("","============================= mp NOT null");
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


            int creatidByid = dataArray.get(position).createdById;
            if (creatidByid == Integer.parseInt(Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID))) {
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
        holder.tvTitle.setText("" + taskModel.name);
        holder.tvDescription.setText("" + taskModel.description);
        holder.tvBudget.setText("" + taskModel.budget);
        holder.tvPriority.setText("" + taskModel.priority);
        holder.tvLocation.setText("" + taskModel.addressStr);
        holder.tvworkType.setText("" + taskModel.taskType);

        try {
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(taskModel.startTime)));
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(taskModel.endTime)));
            String sdate = Util.SDF.format(startCalendar.getTime());
            holder.tvStartTime.setText("" + sdate);
            sdate = Util.SDF.format(endCalendar.getTime());
            holder.tvEndTime.setText("" + sdate);
            holder.tvActualTime.setText("" + taskModel.estimatedTime);

            String listStringAssgin = "";
            //Getting Assigned list
            List<TaskMember> taskMembersAssign = taskMemberDAO.getTaskMemberDataByTaskIdAndMemberType(taskModel.taskID, Constant.MemberTypeAssigned);
            for (TaskMember w : taskMembersAssign) {
                listStringAssgin += w.userName + ",\t";

            }

            if (listStringAssgin.length() > 0)
                holder.tvAssignTo.setText("" + listStringAssgin.replaceAll("null", ""));
            String listStringCC = "";
            //Getting CC list
            List<TaskMember> taskMembersCC = taskMemberDAO.getTaskMemberDataByTaskIdAndMemberType(taskModel.taskID, Constant.MemberTypeCC);
            for (TaskMember w : taskMembersCC) {
                listStringCC += w.userName + ",\t";
            }

            if (listStringCC.length() > 0)
                holder.tvCCTo.setText("" + listStringCC.replaceAll("null", ""));

            String workType = taskModel.taskType;

            if (workType.equals("Project")) //For Project Task Type
            {
//                setViewProject(holder);
            } else if (workType.equals("Regular")) {//For Regular Task Type
                setViewRegular(holder);
            } else if (workType.equals("One Time")) {//For One Time Task Type
                setViewOneTime(holder);
            } else if (workType.equals("Service Call")) {//For Service Call Task Type
                setViewServiceAndSalesCall(holder);
            } else if (workType.equals("Sales Call")) {//For Sales Call Task Type
                setViewServiceAndSalesCall(holder);
            } else if (workType.equals("Shopping/Purchase")) {//For Shopping/Purchase Task Type
                Log.e("View Work Item", "Setting View Shipping purchase");
                setViewShippingAndPurchase(holder);
            } else if (workType.equals("Collection")) {//For Collection Task Type
                setViewCollection(holder);
            }
            holder.tvCreatedBy.setText(taskModel.createdByName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // TASK TYPE ONE TIME SET CONTENT
    private void setViewOneTime(Holder holder) {
        holder.optionalLayout.setVisibility(View.GONE);
    }

    // TASK TYPE SERVICE CALL AND SALES CALL SET CONTENT
    private void setViewServiceAndSalesCall(Holder holder) {
        holder.optionalLayout.setVisibility(View.VISIBLE);
        holder.op1.setText(mContext.getString(R.string.customer_name));

        List<TaskMember> taskMembersAssign = taskMemberDAO.getTaskMemberDataByTaskIdAndMemberType(taskModel.taskID, "U");
        String listStringCust = "", listStringNum = "";
        if (taskMembersAssign.size() > 0)
            for (TaskMember w : taskMembersAssign) {
                listStringCust += w.userName + ",\t";
                listStringNum += w.contact_num + ",\t";
            }

        holder.op11.setText("" + listStringCust);

        holder.op2.setText(mContext.getString(R.string.customer_contact));
        holder.op22.setText("" + listStringNum);
        holder.op3.setText(mContext.getString(R.string.customer_type));
        holder.op33.setText("" + taskModel.customerType);
        if (taskModel.customerType.equals(Constant.REPEAT)) {
            holder.op33.setText("" + mContext.getString(R.string.repeat_call));
        } else {
            holder.op33.setText("" + mContext.getString(R.string.new_call));
        }

        holder.op4.setText(mContext.getString(R.string.past_history));
        holder.op44.setText("" + taskModel.pastHistory);
        holder.raw5.setVisibility(View.GONE);
        holder.raw6.setVisibility(View.GONE);
        holder.raw7.setVisibility(View.GONE);
    }

    // TASK TYPE REGULAR SET CONTENT
    private void setViewRegular(Holder holder) {
        holder.optionalLayout.setVisibility(View.VISIBLE);
        holder.op1.setText(mContext.getString(R.string.frequency));
        holder.op11.setText("" + taskModel.frequency);
        holder.op2.setText(mContext.getString(R.string.days));
        if (taskModel.frequency.equals("Weekly")) {
            holder.op22.setText("" + taskModel.daycodes);
        } else if (taskModel.frequency.equals("Monthly")) {
            holder.op22.setText("" + taskModel.daycodes);
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
            startCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(taskModel.startTime)));
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(taskModel.endTime)));
            String sdate = Util.SDF.format(startCalendar.getTime());
            holder.tvStartTime.setText("" + sdate.substring(11));
            sdate = Util.SDF.format(endCalendar.getTime());
            holder.tvEndTime.setText("" + sdate.substring(11));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //TASK TYPE COLLECTION SET CONTENT
    private void setViewCollection(Holder holder) {
        holder.optionalLayout.setVisibility(View.VISIBLE);
        holder.op1.setText(mContext.getString(R.string.customer_name));
        holder.op2.setText(mContext.getString(R.string.customer_contact));
        holder.op3.setText(mContext.getString(R.string.invoice_amount));
        holder.op4.setText(mContext.getString(R.string.invoice_date));
        holder.op5.setText(mContext.getString(R.string.due_date));
        holder.op6.setText(mContext.getString(R.string.outstanding_amount));

        //Contact name
        List<TaskMember> taskMembersAssign = taskMemberDAO.getTaskMemberDataByTaskIdAndMemberType(taskModel.taskID, "U");
        String listStringCust = "";
        String listStringCustNum = "";
        for (TaskMember w : taskMembersAssign) {
            listStringCust += w.userName + ",\t";
            listStringCustNum += w.contact_num + ",\t";
        }

        holder.op11.setText(listStringCust);
        holder.op22.setText("" + listStringCustNum);
        holder.op33.setText("" + taskModel.invoiceAmount);
        try {
            Calendar invCalendar = Calendar.getInstance();
            invCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(taskModel.invoiceDate)));
            String sdate = Util.SDF.format(invCalendar.getTime());
            holder.op44.setText("" + sdate.substring(0, 11));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            Calendar dueCalendar = Calendar.getInstance();
            dueCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(taskModel.invoiceDate)));
            String sdate = Util.SDF.format(dueCalendar.getTime());
            holder.op55.setText("" + sdate.substring(0, 11));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.op66.setText("" + taskModel.outstandingAmount);
        holder.raw7.setVisibility(View.GONE);
    }

    //TASK TYPE SHOPPING / PURCHASE SET CONTENT
    private void setViewShippingAndPurchase(Holder holder) {
        holder.optionalLayout.setVisibility(View.VISIBLE);
        holder.op1.setText(mContext.getString(R.string.vendor_name));

        //Getting vendor list
        List<TaskMember> taskMembersAssign = taskMemberDAO.getTaskMemberDataByTaskIdAndMemberType(taskModel.taskID, "V");
        String listStringVend = "";
        for (TaskMember w : taskMembersAssign) {
            listStringVend += w.userName + ",\t";
        }

        holder.op11.setText("" + listStringVend);


        holder.op2.setText(mContext.getString(R.string.vensor_selection));
        holder.op22.setText("" + taskModel.vendorPreference);
        holder.op3.setText(mContext.getString(R.string.advance_paid));
        holder.op33.setText("" + taskModel.advancePaid);
        holder.raw4.setVisibility(View.GONE);
        holder.raw5.setVisibility(View.GONE);
        holder.raw6.setVisibility(View.GONE);
        holder.raw7.setVisibility(View.GONE);

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

//    public void PauseCalled() {
//        for (int i = 0; i < dataArray.size(); i++) {
//            if (dataArray != null && dataArray.size() > 1) {
//                Log.e("Update Activity", "Pause called" + dataArray.get(i).dataType);
//                if (dataArray.get(i).message != null && dataArray.get(i).chatType.equals(WorkTransaction.SOUND)) {
//                    Log.e("Update Activity", "Pause called s1");
//                    if (dataArray.get(i).mp != null) {
//                        Log.e("Update Activity", "Pause called s2");
//                        dataArray.get(i).mp.stop();
//                    }
//                }
//            }
//        }
//    }

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
