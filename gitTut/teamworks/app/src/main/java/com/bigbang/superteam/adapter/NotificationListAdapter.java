package com.bigbang.superteam.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.dataObjs.NotificationInfo;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 3 on 4/15/2015.
 */
public class NotificationListAdapter extends BaseAdapter {

    private ArrayList<NotificationInfo> data;
    private Activity mContext;
    private static LayoutInflater inflater = null;
    DisplayImageOptions options;
    ImageLoader imageLoader = ImageLoader.getInstance();

    public NotificationListAdapter(Activity ctx, ArrayList<NotificationInfo> d) {
        mContext = ctx;
        data = d;
        /***********  Layout inflator to call external xml layout () ***********/
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(100))
                .showImageOnLoading(R.drawable.default_image).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        imageLoader.init(ImageLoaderConfiguration
                .createDefault(ctx));
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int i) {
        return data.get(i);
    }

    public long getItemId(int i) {
        return i;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.notification_list_raw, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.tvName.setText(data.get(i).getTitle().toUpperCase());
        holder.tvTitle.setText(data.get(i).getTitle());
        holder.tvDescription.setText(data.get(i).getDescription());
        holder.tvDate.setText("");

        try {
            String currentstrDate = Util.sdf.format(new Date());
            currentstrDate = Util.locatToUTC(currentstrDate);
            Date currentDate = Util.sdf.parse(currentstrDate);
//            currentstrDate = Util.SDF.format(currentDate);
//            currentDate = Util.SDF.parse(currentstrDate);
            Date date = Util.sdf.parse("" + data.get(i).getTime());

            //milliseconds
            long different = currentDate.getTime() - date.getTime();

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = different / daysInMilli;
            different = different % daysInMilli;

            long elapsedHours = different / hoursInMilli;
            different = different % hoursInMilli;

            long elapsedMinutes = different / minutesInMilli;
            different = different % minutesInMilli;

            long elapsedSeconds = different / secondsInMilli;

            if (elapsedDays > 0) holder.tvDate.setText(elapsedDays + "d");
            else if (elapsedHours > 0) holder.tvDate.setText(elapsedHours + "h");
            else if (elapsedMinutes > 0) holder.tvDate.setText(elapsedMinutes + "m");
            else if (elapsedSeconds > 0) holder.tvDate.setText(elapsedSeconds + "s");
            else if (elapsedSeconds == 0) holder.tvDate.setText("1s");
            else holder.tvDate.setText("");

        } catch (Exception e) {
            e.printStackTrace();
        }

        String logo = data.get(i).getImage_Url();
        imageLoader.displayImage("", holder.ivProfile, options);
        if (data.get(i).getImage_Url() != null && data.get(i).getImage_Url().length() > 4)
            imageLoader.displayImage(logo, holder.ivProfile, options);

        if (logo.length() > 4) {
            holder.tvName.setVisibility(View.GONE);
        } else {
            holder.tvName.setVisibility(View.VISIBLE);
        }

        return view;
    }

    public class ViewHolder {
        @InjectView(R.id.tv_title)
        TextView tvTitle;
        @InjectView(R.id.tv_desc)
        TextView tvDescription;
        @InjectView(R.id.tvDate)
        TextView tvDate;
        @InjectView(R.id.image)
        ImageView ivProfile;
        @InjectView(R.id.tvName)
        TextView tvName;

        public ViewHolder(View v) {
            ButterKnife.inject(this, v);
            final Typeface mFont = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) v.getRootView();
            Util.setAppFont(mContainer, mFont);
            ButterKnife.inject(this, v);
        }
    }
}
