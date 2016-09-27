package com.bigbang.superteam.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.ProjectListAdapter;
import com.bigbang.superteam.dataObjs.GeneralObj;
import com.bigbang.superteam.dataObjs.Project;
import com.bigbang.superteam.project.CreateProjectActivity;
import com.bigbang.superteam.project.ProjectDetailsActivity;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.Util;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by USER 8 on 02-Nov-15.
 */
public class ProjectFragment extends Fragment {

    public static ArrayList<GeneralObj> dataList = new ArrayList<>();
    public static Boolean Active = false;
    ProjectListAdapter dataAdapter;
    @InjectView(R.id.listProjects)
    ListView listProjects;
    @InjectView(R.id.tvFail)
    TextView tvFail;
    @InjectView(R.id.imgEmpty)
    ImageButton imgEmpty;
    Activity activity;
    SQLiteDatabase db;

    public static Fragment newInstance() {
        ProjectFragment fragment = new ProjectFragment();
        return fragment;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity=getActivity();
        View v = inflater.inflate(R.layout.fragment_project, container, false);
        ButterKnife.inject(this, v);
        final Typeface mFont = Typeface.createFromAsset(activity.getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) v.getRootView();
        Util.setAppFont(mContainer, mFont);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Init();
        try{UpdateList();}catch (Exception e){e.printStackTrace();}

    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(activity).registerReceiver(mMessageReceiver,
                new IntentFilter("project_created"));
        Active = true;
        try{UpdateList();}catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void onPause() {
        super.onPause();
        Active = false;
    }

    @OnClick(R.id.rlCreate)
    @SuppressWarnings("unused")
    public void Create(View view) {
        startActivity(new Intent(activity, CreateProjectActivity.class));
        activity.overridePendingTransition(R.anim.enter_from_left,
                R.anim.hold_bottom);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try{UpdateList();}catch (Exception e){e.printStackTrace();}
        }
    };

    private void UpdateList() {
        dataList.clear();
        Cursor cursor = db.rawQuery("select * from " + Constant.ProjectTable + " order by End_Date", null); //Start_Date

        Log.e("Project Items", "No of Project Items:" + cursor.getCount());
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    GeneralObj tempObj = new GeneralObj();
                    tempObj.setName(cursor.getString(cursor.getColumnIndex(Project.PROJECT_NAME)));
                    tempObj.setDescription(cursor.getString(cursor.getColumnIndex(Project.DESCRIPTION)));
                    tempObj.setUrl(cursor.getString(cursor.getColumnIndex(Project.PROJECT_IMAGE)));
                    tempObj.setTaskCode(cursor.getInt(cursor.getColumnIndex(Project.PROJECT_ID)));
                    tempObj.setStatus(cursor.getString(cursor.getColumnIndex(Project.STATUS)));
                    dataList.add(tempObj);
                } while (cursor.moveToNext());
            }
        }

        if(cursor!=null) cursor.close();

        dataAdapter = new ProjectListAdapter(activity, dataList);
        listProjects.setAdapter(dataAdapter);

        if (dataList.size() == 0) {
            tvFail.setVisibility(View.VISIBLE);
            imgEmpty.setVisibility(View.VISIBLE);
        } else {
            tvFail.setVisibility(View.GONE);
            imgEmpty.setVisibility(View.GONE);
        }

    }

    private void Init() {

        SQLiteHelper helper = new SQLiteHelper(activity, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        listProjects.setDivider(null);
        listProjects.setDividerHeight(0);

        dataAdapter = new ProjectListAdapter(activity, dataList);
        listProjects.setAdapter(dataAdapter);

        listProjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (dataList.get(i).getStatus() != Constant.offline) {
                    Intent startProjectDetail = new Intent(activity, ProjectDetailsActivity.class);
                    Log.d("ProjectActivity", "Project Code :" + dataList.get(i).getTaskCode());
                    startProjectDetail.putExtra(Project.PROJECT_ID, dataList.get(i).getTaskCode());
                    startActivity(startProjectDetail);
                    activity.overridePendingTransition(R.anim.enter_from_left,
                            R.anim.hold_bottom);
                } else
                    Toast.makeText(activity, R.string.work_item_is_offline, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
