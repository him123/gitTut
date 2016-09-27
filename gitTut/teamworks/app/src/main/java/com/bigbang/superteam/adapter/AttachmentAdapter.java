package com.bigbang.superteam.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.util.Util;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 8 on 06-Nov-15.
 */
public class AttachmentAdapter extends BaseAdapter {

    private static final String TAG = UserListAdapter.class.getSimpleName();
    private static LayoutInflater inflater = null;
    String CreateType;
    private ArrayList<HashMap<String, String>> locallist;
    private Activity mContext;

    public AttachmentAdapter(Activity ctx, ArrayList<HashMap<String, String>> locallist) {
        mContext = ctx;
        this.locallist = locallist;
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void delete(int position) {
        locallist.remove(position);
        notifyDataSetChanged();
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
    public View getView(final int position, View view, ViewGroup parent) {

        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.attachment_raw, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.tvTitle.setText(Util.getFilename(locallist.get(position).get("file_path")));
        if (locallist.get(position).get("uploaded").equals("1")) {
            holder.progressBar.setVisibility(View.GONE);
        } else {
            holder.progressBar.setVisibility(View.VISIBLE);
        }

        holder.rlDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                delete(position);
            }
        });


        return view;

    }

    public class ViewHolder {
        @InjectView(R.id.title)
        TextView tvTitle;
        @InjectView(R.id.rlDelete)
        RelativeLayout rlDelete;
        @InjectView(R.id.progressBar)
        ProgressBar progressBar;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();
            Util.setAppFont(mContainer, mFont);
        }
    }
}

