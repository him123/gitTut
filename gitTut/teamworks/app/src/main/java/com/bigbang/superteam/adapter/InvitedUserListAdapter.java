package com.bigbang.superteam.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.Application.TeamWorkApplication;
import com.bigbang.superteam.R;
import com.bigbang.superteam.model.InvitedUser;
import com.bigbang.superteam.rest.RestClient;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by USER 8 on 8/6/2015.
 */
public class InvitedUserListAdapter extends BaseAdapter {
    private static final String TAG = UserListAdapter.class.getSimpleName();
    private static LayoutInflater inflater = null;
    private List<InvitedUser> users;
    private List<InvitedUser> users_temp;
    private DisplayImageOptions options;
    private Activity mContext;
    private ImageLoader imageLoader;
    private TransparentProgressDialog progressDialog;

    public InvitedUserListAdapter(Activity ctx, List<InvitedUser> users, TransparentProgressDialog progressDialog) {
        mContext = ctx;
        this.users = users;
        users_temp = new ArrayList<>();
        users_temp.addAll(users);
        this.progressDialog = progressDialog;
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

    public void reload() {
        notifyDataSetChanged();
    }

    public void delete(int pos) {
        users.remove(pos);
        notifyDataSetChanged();
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
            view = inflater.inflate(R.layout.listraw_inviteduser, null);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final InvitedUser user = users.get(i);
        holder.tvName.setText(user.getName().toUpperCase());
        holder.tvUserName.setText(user.getName());
        holder.tvUserType.setText("" + user.getRole().getDesc() + "\n" + user.getMobileNo());

        String logo = "";
        imageLoader.displayImage("" + logo, holder.ivUserImage, options);
        if (logo.length() > 0) holder.tvName.setVisibility(View.GONE);
        else holder.tvName.setVisibility(View.VISIBLE);

        if (user.getStatus().equals("New") || user.getStatus().equals("Pending") || user.getStatus().equals("Reject")) {
            holder.rlCancel.setVisibility(View.VISIBLE);
            holder.rlResend.setVisibility(View.GONE);
        } else {
            holder.rlCancel.setVisibility(View.GONE);
            holder.rlResend.setVisibility(View.VISIBLE);
        }


        holder.rlResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.isOnline(mContext)) {
                    addUser(i);
                } else toast(mContext.getResources().getString(R.string.network_error));
            }
        });

        holder.rlCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.isOnline(mContext)) {
                    cancelInvite(i);
                } else toast(mContext.getResources().getString(R.string.network_error));
            }
        });

        return view;
    }

    public class ViewHolder {
        @InjectView(R.id.title)
        TextView tvUserName;
        @InjectView(R.id.desc)
        TextView tvUserType;
        @InjectView(R.id.rlCancel)
        RelativeLayout rlCancel;
        @InjectView(R.id.rlResend)
        RelativeLayout rlResend;
        @InjectView(R.id.img)
        ImageView ivUserImage;
        @InjectView(R.id.tvName)
        TextView tvName;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(mContext.getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();
            Util.setAppFont(mContainer, mFont);
        }
    }

    private void addUser(final int pos) {
        if (progressDialog != null) progressDialog.show();
        InvitedUser user = users.get(pos);
        RestClient.getCommonService().addUser("TeamWorks",
                user.getEmailID(),
                user.getMobileNo(),
                user.getName(),
                user.getRole().getId(), read(Constant.SHRED_PR.KEY_COMPANY_ID),
                read(Constant.SHRED_PR.KEY_USERID), "" + user.getManager().getUserID(), "" + user.getID(), read(Constant.SHRED_PR.KEY_USERID), read(Constant.SHRED_PR.KEY_TOKEN),
                new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response1) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();

                        try {
                            JSONObject jObj = new JSONObject(Util.getString(response.getBody().in()));
                            String status = jObj.optString("status");
                            Log.e(TAG, "response is:- " + response);
                            if (status.equals(Constant.InvalidToken)) {
                                TeamWorkApplication.LogOutClear(mContext);
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (response != null) {
                            try {
                                Map<String, String> map = Util.readStatus(response);
                                if (map.containsKey("message")) {
                                    toast(map.get("message"));
                                }
                                if (map.containsKey("status") && map.get("status").equals("Success")) {
                                    users.get(pos).setStatus("New");
                                    reload();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();
                        Log.e(TAG, "" + error);
                        toast(mContext.getResources().getString(R.string.some_error));
                    }
                });
    }

    private void cancelInvite(final int pos) {
        if (progressDialog != null) progressDialog.show();
        InvitedUser user = users.get(pos);
        RestClient.getCommonService().cancelInvite("" + user.getID(), read(Constant.SHRED_PR.KEY_USERID), read(Constant.SHRED_PR.KEY_TOKEN),
                new Callback<Response>() {
                    @Override
                    public void success(Response response, Response response1) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();

                        try {
                            JSONObject jObj = new JSONObject(Util.getString(response.getBody().in()));
                            String status = jObj.optString("status");
                            Log.e(TAG, "response is:- " + response);
                            if (status.equals(Constant.InvalidToken)) {
                                TeamWorkApplication.LogOutClear(mContext);
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (response != null) {
                            try {
                                Map<String, String> map = Util.readStatus(response);
                                if (map.containsKey("message")) {
                                    toast(map.get("message"));
                                }
                                if (map.containsKey("status") && map.get("status").equals("Success")) {
                                    users.get(pos).setStatus("Decline");
                                    reload();
                                }
                            } catch (Exception e) {
                                Log.e(TAG, e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (progressDialog != null)
                            if (progressDialog.isShowing()) progressDialog.dismiss();
                        Log.e(TAG, "" + error);
                        toast(mContext.getResources().getString(R.string.some_error));
                    }
                });
    }


    protected void toast(CharSequence text) {
        Toast.makeText(mContext, text, Toast.LENGTH_SHORT).show();
    }

    protected void toast(int resId) {
        toast(mContext.getResources().getText(resId));
    }

    protected String read(String key) {
        return Util.ReadSharePrefrence(mContext, key);
    }

    // Filter method
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        users.clear();
        if (charText.length() == 0) {
            users.addAll(users_temp);
        } else {
            for (InvitedUser wp : users_temp) {
                if (wp.getName().toLowerCase(Locale.getDefault())
                        .contains(charText)) {
                    users.add(wp);
                }
                if (wp.getMobileNo().contains(charText)) {
                    users.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}
