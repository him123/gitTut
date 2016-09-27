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
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 8 on 9/22/2015.
 */
public class YourManagersAdapter extends BaseAdapter {

    private static final String TAG = InvitationAdapter.class.getSimpleName();
    private static LayoutInflater inflater = null;
    ArrayList<HashMap<String, String>> locallist;
    static Activity activity;
    TransparentProgressDialog progressDialog;
    DisplayImageOptions options;
    ImageLoader imageLoader = ImageLoader.getInstance();

    public YourManagersAdapter(Activity activity, ArrayList<HashMap<String, String>> locallist) {
        this.activity = activity;
        this.locallist = locallist;
        inflater = (LayoutInflater) activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
    public View getView(int position, View view, ViewGroup parent) {

        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.yourmanagers_listraw, null);
            holder = new ViewHolder(view);

            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

        if (position == locallist.size() - 1) {
            holder.img1.setVisibility(View.GONE);
        } else {
            holder.img1.setVisibility(View.VISIBLE);
        }

        if (locallist.get(position).get("UserID").equals(Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_USERID))) {
            holder.tvTitle.setText("You");
        } else
            holder.tvTitle.setText("" + locallist.get(position).get("FirstName") + " " + locallist.get(position).get("LastName"));

        holder.tvName.setText("" + locallist.get(position).get("FirstName").toUpperCase());
        holder.tvRole.setText("" + locallist.get(position).get("role_desc").toUpperCase());
        String desc = "" + locallist.get(position).get("MobileNo1");
        String email = "" + locallist.get(position).get("EmailID");
        if (email.length() > 0) desc = desc + "\n" + email;
        holder.tvDesc.setText("" + desc);

        String strImage = "" + locallist.get(position).get("picture");
        imageLoader.displayImage(strImage, holder.img, options);
        if (strImage.length() > 0) holder.tvName.setVisibility(View.GONE);
        else holder.tvName.setVisibility(View.VISIBLE);

        return view;
    }

    static class ViewHolder {
        @InjectView(R.id.tvName)
        TextView tvName;
        @InjectView(R.id.title)
        TextView tvTitle;
        @InjectView(R.id.desc)
        TextView tvDesc;
        @InjectView(R.id.tvRole)
        TextView tvRole;
        @InjectView(R.id.img)
        ImageView img;
        @InjectView(R.id.img1)
        ImageView img1;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(activity.getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();
            Util.setAppFont(mContainer, mFont);
        }
    }

}
