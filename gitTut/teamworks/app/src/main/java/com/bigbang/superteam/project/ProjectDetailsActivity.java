package com.bigbang.superteam.project;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigbang.superteam.Privileges;
import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.ProjectDetailsAdapter;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.dataObjs.GeneralObj;
import com.bigbang.superteam.dataObjs.Project;
import com.bigbang.superteam.dataObjs.WorkItem;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.Util;
import com.bigbang.superteam.workitem.UpdateWorkActivity;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by USER 3 on 4/16/2015.
 */
public class ProjectDetailsActivity extends BaseActivity implements View.OnClickListener {

    SQLiteDatabase db = null;
    public static Boolean Active = false;

    @InjectView(R.id.rlOptionMenu)
    RelativeLayout rlOptionsMenu;
    @InjectView(R.id.btn_ganttChart)
    Button chartBtn;
    @InjectView(R.id.lv_workList)
    ListView lvWorkList;
    @InjectView(R.id.ll_menuLayout)
    LinearLayout optionsMenu;
    @InjectView(R.id.btn_editProject)
    Button editBtn;
    @InjectView(R.id.rl_parent)
    RelativeLayout rlParent;
    @InjectView(R.id.tv_headerTitle)
    TextView tvHeaderTitle;
    int projectCode;
    Project project;
    SQLiteHelper helper;
    ArrayList<WorkItem> WorkList;
    ArrayList<GeneralObj> objList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projectdetail);
        ButterKnife.inject(this);

        projectCode = getIntent().getIntExtra(Project.PROJECT_ID, 0);
        Init();
        helper = new SQLiteHelper(this, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        InitializeProjectItem();
        generateLists();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Active = true;

        if(read(Constant.SHRED_PR.KEY_RELOAD).equals("1")){
            write(Constant.SHRED_PR.KEY_RELOAD,"0");
            try {
                InitializeProjectItem();
                generateLists();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Active = true;
    }

    @Override
    public void onBackPressed() {
        if (optionsMenu.getVisibility() == View.VISIBLE) {
            optionsMenu.setVisibility(View.GONE);
        } else {
            finish();
            overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
        }
    }

    @OnClick(R.id.rlBack)
    @SuppressWarnings("unused")
    public void Back(View view) {
        if (optionsMenu.getVisibility() == View.VISIBLE) {
            optionsMenu.setVisibility(View.GONE);
        } else {
            finish();
            overridePendingTransition(R.anim.hold_top, R.anim.exit_in_left);
        }

    }

    @OnClick(R.id.rlOptionMenu)
    @SuppressWarnings("unused")
    public void OptionsMenu1(View view) {

        if (optionsMenu.getVisibility() == View.VISIBLE) {
            optionsMenu.setVisibility(View.GONE);
        } else {
            optionsMenu.setVisibility(View.VISIBLE);
        }
    }

    private void InitializeProjectItem() {
        project = new Project();

        Cursor crsr = db.rawQuery("select * from " + Constant.ProjectTable + " where " + Project.PROJECT_ID + " = " + projectCode, null);
        if (crsr != null && crsr.getCount() > 0) {
            crsr.moveToFirst();

            project.setProject_name(crsr.getString(crsr.getColumnIndex("" + Project.PROJECT_NAME)));
            project.setDescription(crsr.getString(crsr.getColumnIndex("" + Project.DESCRIPTION)));
            project.setPriority(crsr.getString(crsr.getColumnIndex("" + Project.PRIORITY)));
            project.setStartDate(crsr.getString(crsr.getColumnIndex("" + Project.START_DATE)));
            project.setEndDate(crsr.getString(crsr.getColumnIndex("" + Project.END_DATE)));
            project.setEstimatedWorkTime(crsr.getString(crsr.getColumnIndex("" + Project.ESTIMATED_TIME)));
            project.setProjectAssignedTo(crsr.getString(crsr.getColumnIndex("" + Project.ASSIGNED_TO)));
            project.setProjectCCTo(crsr.getString(crsr.getColumnIndex("" + Project.CC_TO)));
            project.setUserCode(crsr.getInt(crsr.getColumnIndex(Project.USER_CODE)));
            project.setProject_Id(crsr.getInt(crsr.getColumnIndex(Project.PROJECT_ID)));
            project.setProjectTasks(crsr.getString(crsr.getColumnIndex("" + Project.PROJECT_TASKS)));
            project.setProjectImage(crsr.getString(crsr.getColumnIndex(Project.PROJECT_IMAGE)));
            project.setOwner(crsr.getString(crsr.getColumnIndex(Project.OWNER)));
            project.setStatus(crsr.getString(crsr.getColumnIndex(Project.STATUS)));
        }
        if(crsr!=null) crsr.close();
        String roleId = Util.ReadSharePrefrence(this, Constant.SHRED_PR.KEY_ROLE_ID);
        if (project.getStatus() != null && project.getStatus().equals("Closed")) {
//            13 , Reopen project
            if (Privileges.REOPEN_PROJECT) {
                editBtn.setVisibility(View.VISIBLE);
                editBtn.setText(getString(R.string.reopen_project));
            } else {
                editBtn.setVisibility(View.GONE);
            }
        } else {
            if (Privileges.MODIFY_PROJECT)
//                if (Util.ReadSharePrefrence(ProjectDetailsActivity.this, Constant.SHRED_PR.KEY_USERID).equals(project.getUserCode()) ||
//                        Util.ReadSharePrefrence(ProjectDetailsActivity.this, Constant.SHRED_PR.KEY_ROLE_ID).equals("2") ||
//                        Util.ReadSharePrefrence(ProjectDetailsActivity.this, Constant.SHRED_PR.KEY_ROLE_ID).equals("1") ||
//                        Util.ReadSharePrefrence(ProjectDetailsActivity.this, Constant.SHRED_PR.KEY_ROLE_ID).equals("3"))
                editBtn.setVisibility(View.VISIBLE);
            else
                editBtn.setVisibility(View.GONE);
        }
        tvHeaderTitle.setText(project.getProject_name());

        if (project.getStatus() != null && (project.getStatus().equals(getString(R.string.pending)) || project.getStatus().equals(getString(R.string.rejected))) || project.getStatus().equals(getString(R.string.delegated)) ) {
            rlOptionsMenu.setEnabled(false);
        }

    }

    private void generateLists() {
        WorkList = new ArrayList<>();
        objList.clear();
        if (project.getProject_Id() > 0)
            WorkList = generateListfromCodes(project.getProject_Id());
        GeneralObj tempObj = new GeneralObj();
        objList.add(tempObj);
        for (int i = 0; i < WorkList.size(); i++) {
            tempObj = new GeneralObj();
            tempObj.setName(WorkList.get(i).getTitle());
            tempObj.setDescription(WorkList.get(i).getDescription());
            tempObj.setUrl(WorkList.get(i).getTaskImage());
            tempObj.setTaskCode(WorkList.get(i).getTaskCode());
            tempObj.setStatus(WorkList.get(i).getStatus());
            tempObj.setAlertCounter(getCountsforWorkItem(WorkList.get(i).getTaskCode()));
            objList.add(tempObj);
        }

        ProjectDetailsAdapter adapter = new ProjectDetailsAdapter(this, objList, project);
        lvWorkList.setAdapter(adapter);
        lvWorkList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    Intent updateWorkItemIntent = new Intent(ProjectDetailsActivity.this, UpdateWorkActivity.class);
                    updateWorkItemIntent.putExtra(WorkItem.TASK_ID, objList.get(i).getTaskCode());
                    updateWorkItemIntent.putExtra("Status", objList.get(i).getStatus());
                    //removeCountsforWorkItem(list.get(i).getTaskCode());
                    startActivity(updateWorkItemIntent);
                    overridePendingTransition(R.anim.enter_from_left,
                            R.anim.hold_bottom);
                }
            }
        });
    }

    private ArrayList<WorkItem> generateListfromCodes(int id) {
        ArrayList<WorkItem> localList = new ArrayList<>();
//        Cursor crsr = db.rawQuery("select * from " + Constant.WorkItemTable + " where " + WorkItem.PROJECT_CODE + " = " + id + " AND " + WorkItem.STATUS + "= 'Approved'", null);
        Cursor crsr = db.rawQuery("select * from " + Constant.WorkItemTable + " where " + WorkItem.PROJECT_CODE + " = " + id, null);
        if (crsr != null) {
            crsr.moveToFirst();
            if (crsr.getCount() > 0)
                do {
                    WorkItem workItem = new WorkItem();
                    workItem = WorkItem.getWorkItemfromCursor(crsr, crsr.getPosition());
                    localList.add(workItem);
                } while (crsr.moveToNext());
        }
        if(crsr!=null) crsr.close();
        Log.e("Project Details", "List Size :" + localList.size());
        return localList;
    }

    private void Init() {

        lvWorkList.setDivider(null);
        lvWorkList.setDividerHeight(0);
        chartBtn.setOnClickListener(this);
        editBtn.setOnClickListener(this);
        optionsMenu.setVisibility(View.GONE);
        rlParent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                optionsMenu.setVisibility(View.GONE);
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_ganttChart:
                optionsMenu.setVisibility(View.GONE);
                if (WorkList != null && WorkList.size() > 0)
                    startGanttChart();
                else
                    Toast.makeText(getApplicationContext(), R.string.no_item_presents, Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_editProject:
                optionsMenu.setVisibility(View.GONE);
                if (project.getStatus() != null && project.getStatus().equals("Closed")) {
                    /// Call apis for reopen project
                } else {
//                    if (Util.ReadSharePrefrence(ProjectDetailsActivity.this, Constant.SHRED_PR.KEY_USERID).equals(project.getUserCode()) ||
//                            Util.ReadSharePrefrence(ProjectDetailsActivity.this, Constant.SHRED_PR.KEY_ROLE_ID).equals("2") ||
//                            Util.ReadSharePrefrence(ProjectDetailsActivity.this, Constant.SHRED_PR.KEY_ROLE_ID).equals("1") ||
//                            Util.ReadSharePrefrence(ProjectDetailsActivity.this, Constant.SHRED_PR.KEY_ROLE_ID).equals("3")) {
                    Intent editIntent = new Intent(ProjectDetailsActivity.this, CreateProjectActivity.class);
                    editIntent.putExtra("ProjectItem", project);
                    startActivity(editIntent);
                    overridePendingTransition(R.anim.enter_from_left,
                            R.anim.hold_bottom);
//                    } else
//                        Toast.makeText(ProjectDetailsActivity.this, getString(R.string.contact_adming_to_edit_this_work), Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void startGanttChart() {
        Intent MyIntent = new Intent(getApplicationContext(), GanttChartActivity.class);
        MyIntent.putExtra("List", WorkList);
        startActivity(MyIntent);
        overridePendingTransition(R.anim.enter_from_left,
                R.anim.hold_bottom);
    }

    public int getCountsforWorkItem(int id) {
        Cursor cursor=null;
        try {
            String query = "select * from " + Constant.AlertsTable + " where Id=" + id + " AND Type like " + "\'" + "Work Update" + "\'";
            cursor = db.rawQuery(query, null);
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                return cursor.getCount();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(cursor!=null) cursor.close();
        }
        return 0;
    }
}
