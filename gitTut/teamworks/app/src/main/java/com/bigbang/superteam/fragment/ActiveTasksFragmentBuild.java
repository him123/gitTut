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
import android.os.Parcelable;
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
import com.bigbang.superteam.dataObjs.WorkItem;
import com.bigbang.superteam.model.User;
import com.bigbang.superteam.task.adapter.TaskAdapter;
import com.bigbang.superteam.task.database.TaskDAO;
import com.bigbang.superteam.task.database.TaskMemberDAO;
import com.bigbang.superteam.task.database.UserDAO;
import com.bigbang.superteam.task.model.TaskMember;
import com.bigbang.superteam.task.model.TaskModel;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.Util;
import com.bigbang.superteam.workitem.UpdateWorkActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 8 on 06-Nov-15.
 */
public class ActiveTasksFragmentBuild extends Fragment {
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
    ArrayList<TaskModel> AllDataList = new ArrayList<>();
    SQLiteDatabase db;
    TaskDAO taskDAO;
    TaskMemberDAO taskMemberDAO;

    public static ActiveTasksFragmentBuild newInstance() {
        ActiveTasksFragmentBuild f = new ActiveTasksFragmentBuild();
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

        taskDAO = new TaskDAO(getActivity());
        taskMemberDAO = new TaskMemberDAO(getActivity());

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

    public static String convert(String[] name) {
        StringBuilder sb = new StringBuilder();
        for (String st : name) {
            sb.append(st).append(',');
        }
        if (name.length != 0) sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private void Init() {

//        helper = new SQLiteHelper(mContext, Constant.DatabaseName);
//        helper.createDatabase();
//        db = helper.openDatabase();

        workList.setDividerHeight(0);
        workList.setDivider(null);

        workList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (AllDataList.get(i).status.equals(Constant.offline)) {
                    Toast.makeText(mContext, R.string.work_item_is_offline, Toast.LENGTH_SHORT).show();
                } else {
                    Intent updateWorkItemIntent = new Intent(mContext
                            , UpdateWorkActivity.class);
                    updateWorkItemIntent.putExtra("Status", AllDataList.get(i).status);
                    int tId = AllDataList.get(i).taskID;
                    updateWorkItemIntent.putExtra("task_id", tId);
//                    removeCountsforWorkItem((int) AllDataList.get(i).taskID);
                    startActivity(updateWorkItemIntent);
                    mContext.overridePendingTransition(R.anim.enter_from_left,
                            R.anim.hold_bottom);
                }
            }
        });
    }

    public void UpdateList() {
        AllDataList.clear();
        ArrayList<TaskModel> taskModelList = taskDAO.getTaskData3();
        for (int i = 0; i <= taskModelList.size() - 1; i++) {

            TaskModel taskModel = new TaskModel();

            taskModel.name = taskModelList.get(i).name.toString();
            taskModel.description = taskModelList.get(i).description.toString();
            taskModel.taskID = taskModelList.get(i).taskID;
            taskModel.status = taskModelList.get(i).status.toString();
            taskModel.taskType = taskModelList.get(i).taskType.toString();
            taskModel.endTime = taskModelList.get(i).endTime.toString();
            taskModel.invoiceAmount = taskModelList.get(i).invoiceAmount;
            taskModel.invoiceDate = taskModelList.get(i).invoiceDate;
            taskModel.outstandingAmount = taskModelList.get(i).outstandingAmount;

            ArrayList<TaskMember> arrUser = taskMemberDAO.getTaskMemberDataByTaskId(taskModelList.get(i).taskID);
            String[] assinged = new String[arrUser.size()];
            for (int j = 0; j < arrUser.size(); j++) {
                assinged[j] = String.valueOf(arrUser.get(j).userID);
            }

            String strAssignedString = convert(assinged);

            int x = 1;
            taskModel.alart_counter = x;

            //Filter Priority
            boolean flag = true;
            int flagPriority = Integer.parseInt(Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_FILTER_PRIORITY));
            switch (flagPriority) {
                case 0:
                    flag = true;
                    break;
                case 1:
                    if (taskModelList.get(i).priority.equals("High"))
                        flag = true;
                    else flag = false;
                    break;
                case 2:
                    if (taskModelList.get(i).priority.equals("Medium"))
                        flag = true;
                    else flag = false;
                    break;
                case 3:
                    if (taskModelList.get(i).priority.equals("Low"))
                        flag = true;
                    else flag = false;
                    break;
                default:
                    flag = true;
                    break;
            }

            //Filter TaskType
            if (flag) {
                String taskType = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_FILTER_TASK_TYPE);
                if (taskType.length() > 0) {
                    flag = false;
                    if (taskType.toLowerCase().contains(taskModelList.get(i).taskType.toLowerCase()))
                        flag = true;
                }
            }

            //Filter by Assigned
            if (flag) {
                String Assignees = Util.ReadSharePrefrence(mContext, Constant.SHRED_PR.KEY_FILTER_ASSIGNED);
                if (Assignees != null && Assignees.length() > 0) {
                    String asgns[] = Assignees.split(",");  // 35,45
                    if (asgns != null && asgns.length > 0) {
                        String frq[] = strAssignedString.split(",");
                        if (frq != null && frq.length > 0) {
                            flag = false;
                            for (int k = 0; k < asgns.length; k++) {
                                for (int l = 0; l < frq.length; l++) {
                                    if (asgns[k].equals("" + frq[l])) {
                                        flag = true;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (flag)
                AllDataList.add(taskModel);
        }

        Log.e("", "============ Array: " + AllDataList.toString());
//        Collections.sort(AllDataList);
        dataAdapter = new TaskAdapter(mContext, AllDataList);

        workList.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();

        // Restore previous state (including selected item index and scroll position)
        if(state != null) {
            Log.d("", "trying to restore listview state..");
            workList.onRestoreInstanceState(state);
        }

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

    Parcelable state;

    @Override
    public void onPause() {
        // Save ListView state @ onPause
        Log.d("", "saving listview state @ onPause");
        state = workList.onSaveInstanceState();
        super.onPause();
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

        try {
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
        } catch (Exception e) {
            Log.e("", "Exception: " + e);
        }
    }

}
