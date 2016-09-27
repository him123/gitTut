package com.bigbang.superteam.slidingmap;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HeaderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_FOOTER = 2;
    ArrayList<HashMap<String, String>> locallist;
    Activity activity;
    DisplayImageOptions options;
    boolean FROM_ADMIN;

    ImageLoader
            imageLoader = ImageLoader.getInstance();

    private LayoutInflater mLayoutInflater;

    private boolean mIsSpaceVisible = true;

    public interface ItemClickListener {
        void onItemClicked(int position);
    }

    private WeakReference<ItemClickListener> mCallbackRef;

    public HeaderAdapter(Activity ctx, ArrayList<HashMap<String, String>> locallist, ItemClickListener listener, boolean FROM_ADMIN) {
        this.activity = ctx;
        this.FROM_ADMIN = FROM_ADMIN;
        mLayoutInflater = LayoutInflater.from(ctx);
        this.locallist = locallist;
        mCallbackRef = new WeakReference<>(listener);

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
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            //inflate your layout and pass it to view holder
            View v = mLayoutInflater.inflate(R.layout.tracking_team_member_row, parent, false);
            return new MyItem(v);
        } else if (viewType == TYPE_HEADER) {
            View v = mLayoutInflater.inflate(R.layout.transparent_header_view, parent, false);
            return new HeaderItem(v);
        } else if (viewType == TYPE_FOOTER) {
            View v = mLayoutInflater.inflate(R.layout.transparent_footer_view, parent, false);
            return new HeaderItem(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyItem) {

            int pos = getItem(position);
            ((MyItem) holder).tvName.setText("" + locallist.get(pos).get("FirstName").toUpperCase());
            ((MyItem) holder).tvTitle.setText("" + locallist.get(pos).get("FirstName") + " " + locallist.get(pos).get("LastName"));

            ((MyItem) holder).tvDate.setText("");
            Log.e("date", ">>" + locallist.get(pos).get("date") + "  Position is:- ****" + (pos));
            String strTime = "" + locallist.get(pos).get("date");
            try {
                //SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
                SimpleDateFormat dateFormatOutput = new SimpleDateFormat("hh:mm a");
                Date date = Util.sdf.parse(strTime);
                strTime = dateFormatOutput.format(date);
                ((MyItem) holder).tvDate.setText("" + strTime);
            } catch (Exception e) {
                e.printStackTrace();
            }

            String Location = "" + locallist.get(pos).get("Location");
            if (!Boolean.parseBoolean(locallist.get(pos).get("isGPSOn").toString())) {
                ((MyItem) holder).tvDesc.setTextColor(activity.getResources().getColor(R.color.red));
            }else{
                ((MyItem) holder).tvDesc.setTextColor(activity.getResources().getColor(R.color.gray));
            }
            ((MyItem) holder).tvDesc.setText(Location);

            String strImage = "" + locallist.get(pos).get("ImageURL");
            imageLoader.displayImage(strImage, ((MyItem) holder).imageView, options);

            if (strImage.length() > 0) ((MyItem) holder).tvName.setVisibility(View.GONE);
            else ((MyItem) holder).tvName.setVisibility(View.VISIBLE);

            ((MyItem) holder).mPosition = position;


        } else if (holder instanceof HeaderItem) {
            ((HeaderItem) holder).mSpaceView.setVisibility(mIsSpaceVisible ? View.VISIBLE : View.GONE);
            ((HeaderItem) holder).mPosition = position;
        }
    }

    @Override
    public int getItemCount() {
        if (locallist.size() > 2)
            return locallist.size() + 1;
        else return locallist.size() + 3;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0) {
            return TYPE_HEADER;
        }
        if (locallist.size() <= 2) {
            if (position == (getItemCount() - 1) || position == (getItemCount() - 2)) {
                return TYPE_FOOTER;
            }
        }
        return TYPE_ITEM;
    }

    private int getItem(int position) {
        return position - 1;
    }

    class MyItem extends HeaderItem implements View.OnClickListener {

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

        public MyItem(View view) {
            super(view);

            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(activity.getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();
            Util.setAppFont(mContainer, mFont);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ItemClickListener callback = mCallbackRef != null ? mCallbackRef.get() : null;
            if (callback != null) {
                callback.onItemClicked(mPosition);
            }

        }

    }

    class HeaderItem extends RecyclerView.ViewHolder implements View.OnClickListener {

        View mSpaceView;
        int mPosition;

        public HeaderItem(View itemView) {
            super(itemView);
            mSpaceView = itemView.findViewById(R.id.space);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            ItemClickListener callback = mCallbackRef != null ? mCallbackRef.get() : null;
            if (callback != null) {
                callback.onItemClicked(mPosition);
            }
        }
    }

    public void hideSpace() {
        mIsSpaceVisible = false;
        notifyItemChanged(0);
    }

    public void showSpace() {
        mIsSpaceVisible = true;
        notifyItemChanged(0);
    }
}