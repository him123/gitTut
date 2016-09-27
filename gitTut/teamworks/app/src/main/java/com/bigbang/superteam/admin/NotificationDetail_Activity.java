package com.bigbang.superteam.admin;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bigbang.superteam.R;

/**
 * Created by USER 3 on 4/15/2015.
 */
public class NotificationDetail_Activity extends Activity {
    Button okBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificationdetails);
        Init();
    }

    private void Init() {
        okBtn = (Button) findViewById(R.id.btn_ok);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
