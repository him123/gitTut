package com.bigbang.superteam.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.bigbang.superteam.adapter.TaskAdapter;
import com.bigbang.superteam.dataObjs.WorkItem;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.Util;
import com.bigbang.superteam.workitem.UpdateWorkActivity;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 8 on 06-Nov-15.
 */
public class ArchivedTasksFragment extends Fragment {

    public static ArrayList<String> data;
    public static Boolean Active = false;

    @InjectView(R.id.listItems)
    ListView workList;
    @InjectView(R.id.tvFail)
    TextView tvFail;
    @InjectView(R.id.imgEmpty)
    ImageButton imgEmpty;

    TaskAdapter taskAdapter;
    ArrayList<WorkItem> list = new ArrayList<>();
    Activity activity;
    SQLiteDatabase db;

    public static ArchivedTasksFragment newInstance() {
        ArchivedTasksFragment f = new ArchivedTasksFragment();
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activity=getActivity();
        View v;
        v = inflater.inflate(R.layout.fragment_archived, container, false);
        ButterKnife.inject(this, v);
        final Typeface mFont = Typeface.createFromAsset(activity.getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) v.getRootView();
        Util.setAppFont(mContainer, mFont);
        ButterKnife.inject(this, v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Init();
        try{UpdateList();}catch (Exception e){e.printStackTrace();}

    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(activity).registerReceiver(mMessageReceiver,
                new IntentFilter("workitem_created"));
        Active = true;
        try{UpdateList();}catch (Exception e){e.printStackTrace();}
    }

    @Override
    public void onPause() {
        super.onPause();
        Active = false;
    }

    private void UpdateList() {
        list.clear();

        SQLiteHelper helper = new SQLiteHelper(activity, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        Cursor cursor = db.rawQuery("select * from " + Constant.WorkItemTable + " where " + WorkItem.STATUS + " IN (\"Done\",\"Delegated\") order by Start_Date", null);
        Log.e("Work Items", "No of Work Items:" + cursor.getCount());
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                        WorkItem workItem=new WorkItem();
                        workItem.setTitle(cursor.getString(cursor.getColumnIndex(WorkItem.TASK_NAME)));
                        workItem.setDescription(cursor.getString(cursor.getColumnIndex(WorkItem.DESCRIPTION)));
                        workItem.setTaskImage(cursor.getString(cursor.getColumnIndex(WorkItem.TASK_IMAGE)));
                        workItem.setTaskCode(cursor.getInt(cursor.getColumnIndex(WorkItem.TASK_ID)));
                        workItem.setStatus(cursor.getString(cursor.getColumnIndex(WorkItem.STATUS)));
                        workItem.setTaskAssignedTo(cursor.getString(cursor.getColumnIndex(WorkItem.ASSIGNED_TO)));
                        workItem.setTaskType(cursor.getString(cursor.getColumnIndex(WorkItem.TASK_TYPE)));
                        workItem.setEndDate(Util.utcToLocalTime(cursor.getString(cursor.getColumnIndex(WorkItem.END_DATE))));

                    boolean flag=true;
                    int flagPriority = Integer.parseInt(Util.ReadSharePrefrence(activity,Constant.SHRED_PR.KEY_FILTER_PRIORITY));
                    switch (flagPriority) {
                        case 0:
                            flag=true;
                            break;
                        case 1:
                            if(cursor.getString(cursor.getColumnIndex(WorkItem.PRIORITY)).equals("High")) flag=true;
                            else flag=false;
                            break;
                        case 2:
                            if(cursor.getString(cursor.getColumnIndex(WorkItem.PRIORITY)).equals("Medium")) flag=true;
                            else flag=false;
                            break;
                        case 3:
                            if(cursor.getString(cursor.getColumnIndex(WorkItem.PRIORITY)).equals("Low")) flag=true;
                            else flag=false;
                            break;
                        default:
                            flag=true;
                            break;
                    }

                    if(flag){
                        String taskType = Util.ReadSharePrefrence(activity,Constant.SHRED_PR.KEY_FILTER_TASK_TYPE);
                        if(taskType.length()>0){
                            flag=false;
                            if(taskType.toLowerCase().contains(workItem.getTaskType().toLowerCase())) flag=true;
                        }
                    }

                    if(flag){
                        String Assignees = Util.ReadSharePrefrence(activity, Constant.SHRED_PR.KEY_FILTER_ASSIGNED);
                        if (Assignees != null && Assignees.length() > 0) {
                            String asgns[] = Assignees.split(",");  // 35,45
                            if (asgns != null && asgns.length > 0) {
                                String frq[] = workItem.getTaskAssignedTo().split(",");
                                if (frq != null && frq.length > 0) {
                                    flag=false;
                                    for (int i = 0; i < asgns.length; i++) {
                                        for (int j = 0; j < frq.length; j++) {
                                            if (asgns[i].equals("" + frq[j])) {
                                                flag=true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if(flag)
                        list.add(workItem);
                } while (cursor.moveToNext());
            }
        }

        if(cursor!=null) cursor.close();

        if (list.size() == 0) {
            tvFail.setVisibility(View.VISIBLE);
            imgEmpty.setVisibility(View.VISIBLE);
        } else {
            tvFail.setVisibility(View.GONE);
            imgEmpty.setVisibility(View.GONE);
        }

        Collections.sort(list);
        taskAdapter = new TaskAdapter(activity, list);
        workList.setAdapter(taskAdapter);

        Intent intent1 = new Intent();
        // intent.setAction(MY_ACTION);
        intent1.setAction(activity.ACTIVITY_SERVICE);
        intent1.putExtra("type", "archived");
        intent1.putExtra("count", ""
                + list.size());
        activity.sendBroadcast(intent1);

        cursor.close();
        db.close();
    }

    private void Init() {

//        SQLiteHelper helper = new SQLiteHelper(activity, Constant.DatabaseName);
//        helper.createDatabase();
//        db = helper.openDatabase();

        workList.setDividerHeight(0);
        workList.setDivider(null);

        taskAdapter = new TaskAdapter(activity, list);
        workList.setAdapter(taskAdapter);
        workList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (list.get(i).getStatus().equals(Constant.offline)) {
                    Toast.makeText(activity, R.string.work_item_is_offline, Toast.LENGTH_SHORT).show();
                } else {
                    Intent updateWorkItemIntent = new Intent(activity
                            , UpdateWorkActivity.class);
                    updateWorkItemIntent.putExtra("Status", list.get(i).getStatus());
                    updateWorkItemIntent.putExtra(WorkItem.TASK_ID, list.get(i).getTaskCode());
                    startActivity(updateWorkItemIntent);
                    activity.overridePendingTransition(R.anim.enter_from_left,
                            R.anim.hold_bottom);
                }
            }
        });
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            UpdateList();
        }
    };

}


