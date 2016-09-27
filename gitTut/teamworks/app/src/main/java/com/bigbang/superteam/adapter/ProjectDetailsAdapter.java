package com.bigbang.superteam.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigbang.superteam.DisplayFullscreenImage;
import com.bigbang.superteam.R;
import com.bigbang.superteam.dataObjs.GeneralObj;
import com.bigbang.superteam.dataObjs.Project;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 3 on 27/06/2015.
 */
public class ProjectDetailsAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    Activity mContext;
    ArrayList<GeneralObj> dataArray;
    DisplayImageOptions options;
    ImageLoader imageLoader = ImageLoader.getInstance();
    Project project;

    public ProjectDetailsAdapter(Activity ctx, ArrayList d, Project pr) {
        mContext = ctx;
        dataArray = d;
        project = pr;
        /***********  Layout inflator to call external xml layout () ***********/
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(mContext));
        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(150))
                .showImageOnLoading(0).resetViewBeforeLoading(true)
                .showImageForEmptyUri(0).showImageOnFail(0).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    public int getCount() {
        return dataArray.size();
    }

    @Override
    public Object getItem(int i) {
        return dataArray.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.project_view, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();
        if (i == 0) {
            setProjectItem(holder);
        } else {
            holder.parentView.setVisibility(View.GONE);
            holder.childView.setVisibility(View.VISIBLE);
            holder.itemTitleTv.setText(dataArray.get(i).getName());
            holder.itemDescriptionTv.setText(dataArray.get(i).getDescription());

            if (dataArray.get(i).getAlertCounter() > 0) {
                holder.alertCounter.setText("" + dataArray.get(i).getAlertCounter());
                holder.rlCounter.setVisibility(View.VISIBLE);
            } else {
                holder.rlCounter.setVisibility(View.GONE);
            }

            /// set Raw background color
            holder.imgColor.setVisibility(View.GONE);
            if (dataArray.get(i).getStatus() != null) {
                holder.imgColor.setVisibility(View.VISIBLE);
                if (dataArray.get(i).getStatus().equals("Approved")) {
                    holder.imgColor.setBackgroundResource(R.drawable.approved_color);
                } else if (dataArray.get(i).getStatus().equals("Pending")) {
                    holder.imgColor.setBackgroundResource(R.drawable.pending_color);
                } else if (dataArray.get(i).getStatus().equals("Rejected")) {
                    holder.imgColor.setBackgroundResource(R.drawable.rejected_color);
                } else if (dataArray.get(i).getStatus().equals(Constant.offline)) {
                    holder.imgColor.setBackgroundResource(R.drawable.offline_color);
                } else if (dataArray.get(i).getStatus().equals(mContext.getString(R.string.delegated))) {
                    holder.imgColor.setBackgroundResource(R.drawable.delegated_color);
                } else
                    holder.imgColor.setVisibility(View.GONE);
            }


        }
        return view;
    }

    private void setProjectItem(ViewHolder holder) {
        holder.parentView.setVisibility(View.VISIBLE);
        holder.childView.setVisibility(View.GONE);
        holder.tvName.setText(project.getProject_name());
        holder.tvDescription.setText(project.getDescription());
        holder.tvPriority.setText(project.getPriority());
        try {
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(project.getStartDate())));
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(Util.sdf.parse(Util.utcToLocalTime(project.getEndDate())));
            String sdate = Util.SDF.format(startCalendar.getTime());
            holder.tvStartTime.setText("" + sdate);
            sdate = Util.SDF.format(endCalendar.getTime());
            holder.tvEndtime.setText("" + sdate);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        holder.tvAssignees.setText(Util.getUsernames(project.getProjectAssignedTo(),mContext));
        holder.tvCCList.setText(Util.getUsernames(project.getProjectCCTo(),mContext));
        holder.tvCreatedBy.setText(Util.getUsernames("" + project.getUserCode(),mContext));
        holder.tvOwner.setText(Util.getUsernames("" + project.getOwner(),mContext));
        if (project.getProjectImage() != null && project.getProjectImage().length() > 4) {
            holder.ivProjectImage.setImageBitmap(null);
            imageLoader.displayImage(project.getProjectImage(), holder.ivProjectImage);
            holder.ivProjectImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent myIntent = new Intent(mContext, DisplayFullscreenImage.class);
                    myIntent.putExtra("URLURI", project.getProjectImage());
                    mContext.startActivity(myIntent);
                }
            });
        } else {
            int counter = 0;
            char c = project.getProject_name().toUpperCase().charAt(counter);
            while (c == ' ') {
                counter++;
                c = project.getProject_name().toUpperCase().charAt(counter);
            }
            holder.ivProjectImage.setImageBitmap(null);
            holder.ivProjectImage.setImageResource(Util.getimgSrc(c));
        }
    }

    public class ViewHolder {
        @InjectView(R.id.tv_name)
        TextView tvName;
        @InjectView(R.id.tv_description)
        TextView tvDescription;
        @InjectView(R.id.tv_priority)
        TextView tvPriority;
        @InjectView(R.id.tv_start_time)
        TextView tvStartTime;
        @InjectView(R.id.tv_end_time)
        TextView tvEndtime;
        @InjectView(R.id.tv_assignees)
        TextView tvAssignees;
        @InjectView(R.id.tv_cc)
        TextView tvCCList;
        @InjectView(R.id.tv_createdby)
        TextView tvCreatedBy;
        @InjectView(R.id.tv_owner)
        TextView tvOwner;
        @InjectView(R.id.ll_parent)
        LinearLayout parentView;
        @InjectView(R.id.ll_child)
        LinearLayout childView;
        @InjectView(R.id.iv_projectimage)
        ImageView ivProjectImage;

        /////////    For Project Raw     /////////////////

        @InjectView(R.id.tv_itemTitle)
        TextView itemTitleTv;
        @InjectView(R.id.tv_itemDescription)
        TextView itemDescriptionTv;
        @InjectView(R.id.tv_alertCounter)
        TextView alertCounter;
        @InjectView(R.id.imgColor)
        ImageView imgColor;
        @InjectView(R.id.rlCounter)
        RelativeLayout rlCounter;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();
            Util.setAppFont(mContainer, mFont);
        }
    }
}
