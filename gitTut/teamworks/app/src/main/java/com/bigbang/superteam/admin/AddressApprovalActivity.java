package com.bigbang.superteam.admin;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.util.Util;

import butterknife.InjectView;
import butterknife.OnClick;

public class AddressApprovalActivity extends BaseActivity {

    @InjectView(R.id.tvOldTitle)
    TextView tvOldTitle;
    @InjectView(R.id.tvNewTitle)
    TextView tvNewTitle;
    @InjectView(R.id.tvOldType)
    TextView tvOldType;
    @InjectView(R.id.tvNewType)
    TextView tvNewType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_approval);

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tvOldTitle.setText("" + extras.getString("OldAddress"));
            tvNewTitle.setText("" + extras.getString("NewAddress"));
            tvOldType.setText("" + extras.getString("OldType"));
            tvNewType.setText("" + extras.getString("NewType"));
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }

    @OnClick(R.id.rlBack)
    @SuppressWarnings("unused")
    public void Back(View view) {
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
    }

}
