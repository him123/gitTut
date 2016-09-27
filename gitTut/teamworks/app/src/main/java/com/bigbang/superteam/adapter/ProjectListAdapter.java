package com.bigbang.superteam.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.dataObjs.GeneralObj;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 3 on 4/8/2015.
 */
public class ProjectListAdapter extends BaseAdapter {
    //private static LayoutInflater inflater = null;
    DisplayImageOptions options;
    private ArrayList<GeneralObj> data;
    private Activity mContext;
    private ImageLoader imageLoader;

    public ProjectListAdapter(Activity ctx, ArrayList d) {
        mContext = ctx;
        data = d;
//        inflater = (LayoutInflater) mContext.
//                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            view = mContext.getLayoutInflater().inflate(R.layout.listraw_project, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

        holder.projectTitleTv.setText(data.get(i).getName());
        holder.projectDescriptionTv.setText(data.get(i).getDescription());


        /// set Raw background color
        holder.imgColor.setVisibility(View.GONE);
        if (data.get(i).getStatus() != null) {
            holder.imgColor.setVisibility(View.VISIBLE);
            if (data.get(i).getStatus().equals("Approved")) {
                holder.imgColor.setBackgroundResource(R.drawable.approved_color);
            } else if (data.get(i).getStatus().equals("Pending")) {
                holder.imgColor.setBackgroundResource(R.drawable.pending_color);
            } else if (data.get(i).getStatus().equals("Rejected")) {
                holder.imgColor.setBackgroundResource(R.drawable.rejected_color);
            } else if (data.get(i).getStatus().equals(Constant.offline)) {
                holder.imgColor.setBackgroundResource(R.drawable.offline_color);
            }else if (data.get(i).getStatus().equals(mContext.getString(R.string.delegated))) {
                holder.imgColor.setBackgroundResource(R.drawable.delegated_color);
            }
            else
                holder.imgColor.setVisibility(View.GONE);
        }


        return view;
    }

    public class ViewHolder {
        @InjectView(R.id.tv_itemTitle)
        TextView projectTitleTv;
        @InjectView(R.id.tv_itemDescription)
        TextView projectDescriptionTv;
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
}
