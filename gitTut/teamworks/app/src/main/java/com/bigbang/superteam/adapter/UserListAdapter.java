package com.bigbang.superteam.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.model.User;
import com.bigbang.superteam.user.UserDetailsActivity;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 3 on 4/15/2015.
 */
public class UserListAdapter extends BaseAdapter {
    private static final String TAG = UserListAdapter.class.getSimpleName();
    private static LayoutInflater inflater = null;
    private List<User> users;
    private List<User> users_tmp;
    private DisplayImageOptions options;
    private Activity mContext;
    private ImageLoader imageLoader;

    public UserListAdapter(Activity ctx, List<User> users) {
        mContext = ctx;
        this.users = users;
        users_tmp = new ArrayList<>();
        users_tmp.addAll(users);
        inflater = (LayoutInflater) mContext.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration
                .createDefault(mContext));
        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(100))
                .showImageOnLoading(R.drawable.default_image).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();
    }

    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int i) {
        return users.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.listraw_user, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final User user = users.get(i);
        holder.tvName.setText(user.getFirstName().toUpperCase());
        holder.tvUserName.setText(user.getFirstName() + " " + user.getLastName());
        if (user.getUserID().toString().equals(Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID)))
            holder.tvUserName.setText("You");
        holder.tvUserType.setText(user.getRole().getDesc());

        String logo = user.getPicture();
        imageLoader.displayImage("" + logo, holder.ivUserImage, options);
        if (logo.length() > 0) holder.tvName.setVisibility(View.GONE);
        else holder.tvName.setVisibility(View.VISIBLE);

        holder.rlCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String uri = "tel:" + user.getMobileNo1();
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(uri));
                    mContext.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String roleId = user.getRole().getId().toString();
                if (!Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_USERID).equals("" + user.getUserID()) && !roleId.equals("1")) {
                    Intent intent = new Intent(mContext, UserDetailsActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("user", user);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                    mContext.overridePendingTransition(R.anim.enter_from_left,
                            R.anim.hold_bottom);
                }
            }
        });

        return view;
    }

    public class ViewHolder {
        @InjectView(R.id.title)
        TextView tvUserName;
        @InjectView(R.id.desc)
        TextView tvUserType;
        @InjectView(R.id.img)
        ImageView ivUserImage;
        @InjectView(R.id.tvName)
        TextView tvName;
        @InjectView(R.id.rlCall)
        RelativeLayout rlCall;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();
            Util.setAppFont(mContainer, mFont);
        }
    }

    // Filter method
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        users.clear();
        if (charText.length() == 0) {
            users.addAll(users_tmp);
        } else {
            for (User wp : users_tmp) {
                if (wp.getFirstName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    users.add(wp);
                }
                if (wp.getMobileNo1().contains(charText)) {
                    users.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
