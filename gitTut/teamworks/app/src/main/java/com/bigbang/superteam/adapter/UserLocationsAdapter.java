package com.bigbang.superteam.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class UserLocationsAdapter extends BaseAdapter {

    ArrayList<HashMap<String, String>> locallist;
    Activity activity;
    DisplayImageOptions options;

    ImageLoader
            imageLoader = ImageLoader.getInstance();


    public UserLocationsAdapter(Activity activity, ArrayList<HashMap<String, String>> locallist) {
        this.activity = activity;
        this.locallist = locallist;

        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(150))
                .showImageOnLoading(0).resetViewBeforeLoading(true)
                .showImageForEmptyUri(0).showImageOnFail(0).cacheInMemory(true)
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

        final ViewHolder holder;
        View view = convertView;

        if (view == null) {
            view = activity.getLayoutInflater().inflate(R.layout.tracking_team_member_row, parent,
                    false);
            holder = new ViewHolder();
            assert view != null;
            holder.imageView = (ImageView) view.findViewById(R.id.img);
            holder.title = (TextView) view.findViewById(R.id.title);
            holder.desc = (TextView) view.findViewById(R.id.desc);
            holder.date = (TextView) view.findViewById(R.id.date);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.title.setText("" + locallist.get(position).get("Name"));

        holder.date.setText("");
        String strTime = "" + locallist.get(position).get("Time");
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat dateFormatOutput = new SimpleDateFormat("hh:mm a");
            Date date = dateFormat.parse(strTime);
            strTime = dateFormatOutput.format(date);
            holder.date.setText("" + strTime);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.date.setText("" + strTime);
        holder.desc.setText("" + locallist.get(position).get("Location"));

        String strImage = "" + locallist.get(position).get("ImageURL");
        imageLoader.displayImage(strImage, holder.imageView, options);

        return view;
    }

    static class ViewHolder {
        ImageView imageView;
        TextView title, date, desc;
    }
}