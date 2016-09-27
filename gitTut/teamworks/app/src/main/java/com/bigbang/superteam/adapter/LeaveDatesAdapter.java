package com.bigbang.superteam.adapter;

import android.app.Activity;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.util.SQLiteHelper;
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
 * Created by USER 7 on 1/26/2016.
 */
public class LeaveDatesAdapter extends BaseAdapter {
    ArrayList<HashMap<String, String>> dateSelected;
    private Activity mContext;
    private static LayoutInflater inflater = null;

    static SQLiteHelper helper;
    public static SQLiteDatabase db = null;

    DisplayImageOptions options;
    String TAG = "LeaveDatesAdapter";

    ImageLoader
            imageLoader = ImageLoader.getInstance();


    public LeaveDatesAdapter(Activity ctx,   ArrayList<HashMap<String, String>>  d) {
        this.mContext = ctx;
        this.dateSelected = d;
        /***********  Layout inflator to call external xml layout () ***********/
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(150))
                .showImageOnLoading(0).resetViewBeforeLoading(true)
                .showImageForEmptyUri(0).showImageOnFail(0).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        imageLoader.init(ImageLoaderConfiguration.createDefault(ctx));
    }

    public int getCount() {
        Log.e(TAG,">>>>>>===="+dateSelected.size());
        return dateSelected.size();
    }

    @Override
    public Object getItem(int i) {
        return dateSelected.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int index, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.listrow_leave_dates, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

        Log.e(TAG,">>>>>>   "+dateSelected.get(index)+"  ***********  "+index);
        holder.tvDate.setText("" + Util.dateFormatWithGMT(dateSelected.get(index).get("date")));
        if(dateSelected.get(index).get("approved").equals("1")){
            holder.checkbox.setChecked(true);
        }else{
            holder.checkbox.setChecked(false);
        }

        //holder.tvTitle.setText("" + dateSelected.get(position).get("date"));

        holder.checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.checkbox.isChecked()){
                    dateSelected.get(index).put("approved","1");
                    Log.e(TAG,"Hashmap values is:- >>> "+dateSelected.get(index).get("approved"));
                }else{
                    dateSelected.get(index).put("approved","0");
                    Log.e(TAG,"Hashmap values is:- >>>++++++ "+dateSelected.get(index).get("approved"));
                }
            }
        });
        return view;
    }


     class ViewHolder {
        @InjectView(R.id.tvDate)
        TextView tvDate;
        @InjectView(R.id.checkbox)
        CheckBox checkbox;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();
            Util.setAppFont(mContainer, mFont);
        }
    }

}
