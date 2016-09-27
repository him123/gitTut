package com.bigbang.superteam.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bigbang.superteam.R;

/**
 * Created by USER 7 on 8/11/2015.
 */
public class TransparentProgressDialog extends Dialog {

    private ImageView iv;

    public TransparentProgressDialog(Context context, int resourceIdOfImage, boolean cancelable) {
        super(context, R.style.TransparentProgressDialog);
        WindowManager.LayoutParams wlmp = getWindow().getAttributes();
        wlmp.gravity = Gravity.CENTER;
        getWindow().setAttributes(wlmp);
        setTitle(null);
        setCancelable(cancelable);
        setCanceledOnTouchOutside(cancelable);
        setOnCancelListener(null);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        iv = new ImageView(context);
        iv.setImageResource(R.drawable.progress);
        layout.addView(iv, params);
        addContentView(layout, params);
    }

    @Override
    public void show() {
        super.show();
        RotateAnimation anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
        anim.setInterpolator(new LinearInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
        anim.setDuration(1000);
        iv.setAnimation(anim);
        iv.startAnimation(anim);
    }
}
