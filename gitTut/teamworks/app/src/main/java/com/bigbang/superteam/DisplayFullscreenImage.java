package com.bigbang.superteam;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.util.TouchImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by USER 3 on 15/05/2015.
 */
public class DisplayFullscreenImage extends BaseActivity {

    @InjectView(R.id.img)
    TouchImageView img;
    ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;

    String URLURItoImage;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_fullscreen_image);

        init();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            URLURItoImage = extras.getString("URLURI");
            imageLoader.displayImage(URLURItoImage, img, options);
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
    }

    @OnClick(R.id.rlBack)
    @SuppressWarnings("unused")
    public void Back(View view) {
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
    }

    private void init() {

        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(0).resetViewBeforeLoading(true)
                .showImageForEmptyUri(0).showImageOnFail(0).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565).build();

        imageLoader.init(ImageLoaderConfiguration
                .createDefault(getApplicationContext()));

    }
}
