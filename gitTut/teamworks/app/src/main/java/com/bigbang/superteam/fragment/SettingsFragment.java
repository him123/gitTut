package com.bigbang.superteam.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.AdminSettingsAdapter;
import com.bigbang.superteam.adapter.ManagerSettingsAdapter;
import com.bigbang.superteam.adapter.OtherMenusAdapter;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.TransparentProgressDialog;
import com.bigbang.superteam.util.Util;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 8 on 02-Nov-15.
 */
public class SettingsFragment extends Fragment {

    @InjectView(R.id.img_profile_small)
    ImageView imgProfileSmall;
    @InjectView(R.id.imgWhite)
    ImageView imgWhite;
    @InjectView(R.id.tvName)
    TextView tvName;
    @InjectView(R.id.tvUserName)
    TextView tvUserName;
    @InjectView(R.id.tvRole)
    TextView tvRole;
    @InjectView(R.id.listViewSettings)
    ListView listViewSettings;

    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options, options1;
    TransparentProgressDialog progressDialog;
    Activity activity;

    public static Fragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity = getActivity();
        View v = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.inject(this, v);
        final Typeface mFont = Typeface.createFromAsset(activity.getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) v.getRootView();
        Util.setAppFont(mContainer, mFont);

        if (Util.checkNetworkServiceProvider(getActivity())) {
            Log.d("", "************************* Service Provider found");
        } else {
            Log.d("", "************************* Service Provider NOT found");
        }

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        init();

        int roleID = Integer.parseInt(read(Constant.SHRED_PR.KEY_ROLE_ID));
        switch (roleID) {
            case 1:
                listViewSettings.setAdapter(new AdminSettingsAdapter(activity, progressDialog));
                break;
            case 2:
                listViewSettings.setAdapter(new AdminSettingsAdapter(activity, progressDialog));
                break;
            case 3:
                listViewSettings.setAdapter(new ManagerSettingsAdapter(activity, progressDialog));
                break;
            case 4:
                listViewSettings.setAdapter(new OtherMenusAdapter(activity, progressDialog));
                break;
            default:
                break;
        }

        Util.setListViewHeightBasedOnChildren(listViewSettings);
    }

    @Override
    public void onResume() {
        super.onResume();

        String picture = read(Constant.SHRED_PR.KEY_Picture);
        String firstName = read(Constant.SHRED_PR.KEY_FIRSTNAME);
        String lastName = read(Constant.SHRED_PR.KEY_LASTNAME);
        String role = read(Constant.SHRED_PR.KEY_ROLE);
        tvRole.setText("" + role);
        tvName.setText("" + firstName.toUpperCase());
        tvUserName.setText("" + firstName + " " + lastName);
        Log.i("picture", "::" + picture);
        imageLoader.displayImage("" + picture, imgProfileSmall, options);
        if (picture.length() > 0) tvName.setVisibility(View.GONE);
        else tvName.setVisibility(View.VISIBLE);
    }

    private void init() {

        progressDialog = new TransparentProgressDialog(activity, R.drawable.progressdialog, false);

        options = new DisplayImageOptions.Builder()
                .displayer(new RoundedBitmapDisplayer(150))
                .showImageOnLoading(R.drawable.default_image).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.default_image).showImageOnFail(R.drawable.default_image).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        options1 = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.empty_profilepic).resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.drawable.empty_profilepic).showImageOnFail(R.drawable.empty_profilepic).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        imageLoader.init(ImageLoaderConfiguration
                .createDefault(activity));

        imageLoader.displayImage("", imgWhite, options1);
        imageLoader.displayImage("", imgProfileSmall, options);

        listViewSettings.setDivider(null);
        listViewSettings.setDividerHeight(0);

    }

    protected String read(String key) {
        return Util.ReadSharePrefrence(activity, key);
    }

}