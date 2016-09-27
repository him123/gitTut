package com.bigbang.superteam.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.dataObjs.LeaveBalanceModel;
import com.bigbang.superteam.util.SQLiteHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 7 on 6/10/2015.
 */
public class LeaveBalanceAdapter extends BaseAdapter {
    private ArrayList<LeaveBalanceModel> data;
    private Context mContext;
    private static LayoutInflater inflater = null;

    static SQLiteHelper helper;
    public static SQLiteDatabase db = null;

    DisplayImageOptions options;
    String TAG = "LeaveBalanceAdapter";

    ImageLoader
            imageLoader = ImageLoader.getInstance();


    public LeaveBalanceAdapter(Context ctx, ArrayList<LeaveBalanceModel> d,ProgressBar pBar) {
        mContext = ctx;
        data = d;
        /***********  Layout inflator to call external xml layout () ***********/
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(150))
                .showImageOnLoading(0).resetViewBeforeLoading(true)
                .showImageForEmptyUri(0).showImageOnFail(0).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(ctx));
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
    public View getView(final int index, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.listrow_leave_balance, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else
            holder = (ViewHolder) view.getTag();

            holder.tvLeaveType.setText(""+data.get(index).getLeaveType());
            holder.tvNoOfLeaves.setText(""+data.get(index).getNumberOfLeaves());
            holder.tvAvailableLeaves.setText(""+data.get(index).getAvailableLeaves());
            holder.tvUsedLeaves.setText(""+data.get(index).getUsedLeaves());

        return view;
    }
    static class ViewHolder {
        @InjectView(R.id.tvLeaveType)
        TextView tvLeaveType;
        @InjectView(R.id.tvNumberOfLeaves)
        TextView tvNoOfLeaves;
        @InjectView(R.id.tvAvailableOfLeaves)
        TextView tvAvailableLeaves;
        @InjectView(R.id.tvUsedLeavesLeft)
        TextView tvUsedLeaves;


        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}
