package com.bigbang.superteam.fragment;

import android.app.Activity;
import android.app.NotificationManager;
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
import com.bigbang.superteam.task.model.TaskModel;
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
public class ActiveTasksFragment extends Fragment {
    //    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";

    public static Activity mContext;
    public static Boolean Active = false;
    @InjectView(R.id.listItems)
    ListView workList;
    @InjectView(R.id.tvFail)
    TextView tvFail;
    @InjectView(R.id.imgEmpty)
    ImageButton imgEmpty;
    TaskAdapter dataAdapter;
    ArrayList<WorkItem> AllDataList = new ArrayList<>();
    SQLiteDatabase db;

    public static ActiveTasksFragment newInstance() {
        ActiveTasksFragment f = new ActiveTasksFragment();
        return f;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        View v;
        v = inflater.inflate(R.layout.fragment_activetasks, container, false);
        ButterKnife.inject(this, v);
        final Typeface mFont = Typeface.createFromAsset(mContext.getAssets(),
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
        try {
            UpdateList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiver,
                new IntentFilter("workitem_created"));
        Active = true;

        try {
            UpdateList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                UpdateList();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    SQLiteHelper helper;//= new SQLiteHelper(mContext, Constant.DatabaseName);

    private void Init() {

        helper = new SQLiteHelper(mContext, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        workList.setDividerHeight(0);
        workList.setDivider(null);

        workList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (AllDataList.get(i).getStatus().equals(Constant.offline)) {
                    Toast.makeText(mContext, R.string.work_item_is_offline, Toast.LENGTH_SHORT).show();
                } else {
                    Intent updateWorkItemIntent = new Intent(mContext
                            , UpdateWorkActivity.class);
                    updateWorkItemIntent.putExtra("Status", AllDataList.get(i).getStatus());
                    updateWorkItemIntent.putExtra(WorkItem.TASK_ID, AllDataList.get(i).getTaskCode());
                    removeCountsforWorkItem(AllDataList.get(i).getTaskCode());
                    startActivity(updateWorkItemIntent);
                    mContext.overridePendingTransition(R.anim.enter_from_left,
                            R.anim.hold_bottom);
                }
            }
        });
    }

    public void UpdateList() {
        AllDataList.clear();
        db = helper.openDatabase();
        Cursor cursor = db.rawQuery("select * from " + "task_master", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                    WorkItem workItem = new WorkItem();
                    TaskModel taskModel = new TaskModel();
                    workItem.setTitle(cursor.getString(cursor.getColumnIndex(WorkItem.TASK_NAME)));
                    workItem.setDescription(cursor.getString(cursor.getColumnIndex(WorkItem.DESCRIPTION)));
                    workItem.setTaskImage(cursor.getString(cursor.getColumnIndex(WorkItem.TASK_IMAGE)));
                    workItem.setTaskCode(cursor.getInt(cursor.getColumnIndex(WorkItem.TASK_ID)));
                    workItem.setStatus(cursor.getString(cursor.getColumnIndex(WorkItem.STATUS)));
                    workItem.setTaskAssignedTo(cursor.getString(cursor.getColumnIndex(WorkItem.ASSIGNED_TO)));
                    workItem.setTaskType(cursor.getString(cursor.getColumnIndex(WorkItem.TASK_TYPE)));
                    workItem.setAlertCounter(getCountsforWorkItem(cursor.getInt(cursor.getColumnIndex(WorkItem.TASK_ID))));
                    workItem.setEndDate(Util.utcToLocalTime(cursor.getString(cursor.getColumnIndex(WorkItem.END_DATE))));


                    int x = getCountsforWorkItem(cursor.getInt(cursor.getColumnIndex(WorkItem.TASK_ID)));
                    workItem.setAlertCounter(x);

                    boolean flag = true;
                    int flagPriority = Integer.parseInt(Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_FILTER_PRIORITY));
                    switch (flagPriority) {
                        case 0:
                            flag = true;
                            break;
                        case 1:
                            if (cursor.getString(cursor.getColumnIndex(WorkItem.PRIORITY)).equals("High"))
                                flag = true;
                            else flag = false;
                            break;
                        case 2:
                            if (cursor.getString(cursor.getColumnIndex(WorkItem.PRIORITY)).equals("Medium"))
                                flag = true;
                            else flag = false;
                            break;
                        case 3:
                            if (cursor.getString(cursor.getColumnIndex(WorkItem.PRIORITY)).equals("Low"))
                                flag = true;
                            else flag = false;
                            break;
                        default:
                            flag = true;
                            break;
                    }

                    if (flag) {
                        String taskType = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_FILTER_TASK_TYPE);
                        if (taskType.length() > 0) {
                            flag = false;
                            if (taskType.toLowerCase().contains(workItem.getTaskType().toLowerCase()))
                                flag = true;
                        }
                    }

                    if (flag) {
                        String Assignees = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_FILTER_ASSIGNED);
                        if (Assignees != null && Assignees.length() > 0) {
                            String asgns[] = Assignees.split(",");  // 35,45
                            if (asgns != null && asgns.length > 0) {
                                String frq[] = workItem.getTaskAssignedTo().split(",");
                                if (frq != null && frq.length > 0) {
                                    flag = false;
                                    for (int i = 0; i < asgns.length; i++) {
                                        for (int j = 0; j < frq.length; j++) {
                                            if (asgns[i].equals("" + frq[j])) {
                                                flag = true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if (flag)
                        AllDataList.add(workItem);
                } while (cursor.moveToNext());
            }
        }

        if (cursor != null) cursor.close();

        Collections.sort(AllDataList);
        dataAdapter = new TaskAdapter(mContext, AllDataList);
        workList.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();

        if (AllDataList.size() == 0) {
            tvFail.setVisibility(View.VISIBLE);
            imgEmpty.setVisibility(View.VISIBLE);
        } else {
            tvFail.setVisibility(View.GONE);
            imgEmpty.setVisibility(View.GONE);
        }

        Intent intent1 = new Intent();
        // intent.setAction(MY_ACTION);
        intent1.setAction(mContext.ACTIVITY_SERVICE);
        intent1.putExtra("type", "active");
        intent1.putExtra("count", ""
                + AllDataList.size());
        mContext.sendBroadcast(intent1);

    }


    public int getCountsforWorkItem(int id) {
        Cursor cursor = null;
        try {
            String query = "select * from " + Constant.AlertsTable + " where Id=" + id + " AND Type like " + "\'" + "Work Update" + "\'";
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                int i = cursor.getCount();
                cursor.close();
                return i;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
        }

        return 0;
    }

    public void removeCountsforWorkItem(int id) {

        NotificationManager notificationManager = (NotificationManager) mContext
                .getSystemService(Context.NOTIFICATION_SERVICE);
        int pk = 0;
        String query = "select * from " + Constant.AlertsTable;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                try {
                    pk = Integer.parseInt(cursor.getString(cursor.getColumnIndex("pk")));
                    Log.e("Id for cancellation:", " pk :=" + pk);
                    try {
                        notificationManager.cancel(pk);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        if (cursor != null) cursor.close();
        db.delete(Constant.AlertsTable, "Id=" + id, null);
    }

}
