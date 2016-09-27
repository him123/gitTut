package com.bigbang.superteam.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.tracking.UserDailyReportActivity;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 8 on 4/16/2015.
 */
public class TeamMemberAdapter extends BaseAdapter {

    ArrayList<HashMap<String, String>> locallist;
    Activity activity;
    DisplayImageOptions options;
    boolean FROM_ADMIN;

    ImageLoader
            imageLoader = ImageLoader.getInstance();


    public TeamMemberAdapter(Activity activity, ArrayList<HashMap<String, String>> locallist, boolean FROM_ADMIN) {
        this.activity = activity;
        this.locallist = locallist;
        this.FROM_ADMIN = FROM_ADMIN;

        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(100))
                .showImageOnLoading(R.drawable.default_image).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        imageLoader.init(ImageLoaderConfiguration
                .createDefault(activity));

    }

    @Override
    public int getCount() {
        return locallist.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        ViewHolder holder;
        if (view == null) {
            view = activity.getLayoutInflater().inflate(R.layout.tracking_team_member_row, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.tvName.setText("" + locallist.get(position).get("FirstName").toUpperCase());
        holder.tvTitle.setText("" + locallist.get(position).get("FirstName") + " " + locallist.get(position).get("LastName"));

        holder.tvDate.setText("");
        Log.e("date", ">>" + locallist.get(position).get("date"));
        String strTime = "" + locallist.get(position).get("date");
        try {
            //SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat dateFormatOutput = new SimpleDateFormat("hh:mm a");
            Date date = Util.sdf.parse(strTime);
            strTime = dateFormatOutput.format(date);
            holder.tvDate.setText("" + strTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String Location = "" + locallist.get(position).get("Location");
        holder.tvDesc.setText(Location);

        String strImage = "" + locallist.get(position).get("ImageURL");
        imageLoader.displayImage(strImage, holder.imageView, options);

        if (strImage.length() > 0) holder.tvName.setVisibility(View.GONE);
        else holder.tvName.setVisibility(View.VISIBLE);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (FROM_ADMIN) {
                    Intent intent = new Intent(activity, UserDailyReportActivity.class);
                    intent.putExtra("UserName", "" + locallist.get(position).get("FirstName") + " " + locallist.get(position).get("LastName"));
                    intent.putExtra("UserID", "" + locallist.get(position).get("UserID"));
                    intent.putExtra("day", "" + locallist.get(position).get("day"));
                    intent.putExtra("month", "" + locallist.get(position).get("month"));
                    intent.putExtra("year", "" + locallist.get(position).get("year"));
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.enter_from_left,
                            R.anim.hold_bottom);
                }
            }
        });

        return view;
    }


    public class ViewHolder {
        @InjectView(R.id.img)
        ImageView imageView;
        @InjectView(R.id.title)
        TextView tvTitle;
        @InjectView(R.id.desc)
        TextView tvDesc;
        @InjectView(R.id.date)
        TextView tvDate;
        @InjectView(R.id.tvName)
        TextView tvName;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(activity.getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();
            Util.setAppFont(mContainer, mFont);
        }
    }

}
