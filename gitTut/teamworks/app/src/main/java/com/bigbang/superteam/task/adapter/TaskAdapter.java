package com.bigbang.superteam.task.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.task.model.TaskModel;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 3 on 04/12/2015.
 */
public class TaskAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    DisplayImageOptions options;
    private ArrayList<TaskModel> data;
    private Activity mContext;
    private ImageLoader imageLoader;

    public TaskAdapter(Activity ctx, ArrayList<TaskModel> d) {
        mContext = ctx;
        data = d;
        /***********  Layout inflator to call external xml layout () ***********/
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(mContext));
        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(100))
                .showImageOnLoading(0).resetViewBeforeLoading(true)
                .showImageForEmptyUri(0).showImageOnFail(0).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.listraw_workitem, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

        holder.itemTitleTv.setText(data.get(i).name);
        holder.itemDescriptionTv.setText(data.get(i).description);

        if (data.get(i).id > 0) {
            holder.alertCounter.setText("" + data.get(i).taskID);
            holder.rlCounter.setVisibility(View.VISIBLE);
        } else {
            holder.rlCounter.setVisibility(View.GONE);
        }

        /// set Raw background color
        holder.imgColor.setVisibility(View.GONE);
        if (data.get(i).status != null) {
            holder.imgColor.setVisibility(View.VISIBLE);
            if (data.get(i).status.equals("Approved")) {
                holder.imgColor.setBackgroundResource(R.drawable.approved_color);
            } else if (data.get(i).status.equals("Pending")) {
                holder.imgColor.setBackgroundResource(R.drawable.pending_color);
            } else if (data.get(i).status.equals("Rejected")) {
                holder.imgColor.setBackgroundResource(R.drawable.rejected_color);
            } else if (data.get(i).status.equals(Constant.offline)) {
                holder.imgColor.setBackgroundResource(R.drawable.offline_color);
            } else if (data.get(i).status.equals(mContext.getString(R.string.delegated))) {
                holder.imgColor.setBackgroundResource(R.drawable.delegated_color);
            } else
                holder.imgColor.setVisibility(View.GONE);
        }

        String localEndTime = Util.utcToLocalTime(data.get(i).endTime);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        try {
            Date date1 = simpleDateFormat.parse(localEndTime);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String currentDateandTime = sdf.format(new Date());
            Date date2 = simpleDateFormat.parse(currentDateandTime);

            if(!date1.before(date2)){
                holder.tv_itemTime.setText(Util.diffBetweenTwoDates(date2,date1));
            }else{
                holder.tv_itemTime.setText("Task timeout");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    public class ViewHolder {
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
        @InjectView(R.id.tv_itemTime)
        TextView tv_itemTime;


        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();
            Util.setAppFont(mContainer, mFont);
        }
    }
}
