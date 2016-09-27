package com.bigbang.superteam.task;

import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.model.User;
import com.bigbang.superteam.task.adapter.AssignCCAdapter;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.InjectView;
import butterknife.OnClick;

public class AssignCCActivity extends BaseActivity {

    @InjectView(R.id.tvTitle)
    TextView tvTitle;
    @InjectView(R.id.lvAssigneeCC)
    ListView lvAssigneeCC;

    ArrayList<User> usersList = new ArrayList<>();
    String title,key_assignee_cc;
    AssignCCAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_assignee_cc);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            title=extras.getString("title");
            key_assignee_cc=extras.getString("key_assignee_cc");
            usersList = (ArrayList<User>)extras.getSerializable("list");
        }

        init();

        tvTitle.setText(""+title);
    }

    private void init() {
        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();

        Util.setAppFont(mContainer, mFont);

        if(usersList != null && usersList.size() > 0){
            adapter = new AssignCCAdapter(usersList , getApplicationContext());
            lvAssigneeCC.setAdapter(adapter);
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
        hideKeyboard();
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
    }

    @OnClick(R.id.rlSave)
    @SuppressWarnings("unused")
    public void Save(View view) {   
        write(Constant.SHRED_PR.KEY_LIST, "" + usersList);
        write(Constant.SHRED_PR.KEY_ASSIGNEE_CC, ""+key_assignee_cc);
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
    }

}
