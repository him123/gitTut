package com.bigbang.superteam.workitem;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.dataObjs.User;
import com.bigbang.superteam.dataObjs.WorkItem;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;
import com.bigbang.superteam.util.Util;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by USER 3 on 08/12/2015.
 */
public class FilterTasksActivity extends BaseActivity {

    //Priority:
    @InjectView(R.id.viewAllPriority)
    View viewAllPriority;
    @InjectView(R.id.viewHighPriority)
    View viewHighPriority;
    @InjectView(R.id.viewMediumPriority)
    View viewMediumPriority;
    @InjectView(R.id.viewLowPriority)
    View viewLowPriority;
    @InjectView(R.id.tvAllPriority)
    TextView tvAllPriority;
    @InjectView(R.id.tvHighPriority)
    TextView tvHighPriority;
    @InjectView(R.id.tvMediumPriority)
    TextView tvMediumPriority;
    @InjectView(R.id.tvLowPriority)
    TextView tvLowPriority;
    //Task Type:
    @InjectView(R.id.llTaskType)
    LinearLayout llTaskType;
    @InjectView(R.id.rlProjectType)
    RelativeLayout rlProjectType;
    @InjectView(R.id.rlRegularType)
    RelativeLayout rlRegularType;
    @InjectView(R.id.rlOneType)
    RelativeLayout rlOneType;
    @InjectView(R.id.rlServiceType)
    RelativeLayout rlServiceType;
    @InjectView(R.id.rlSalesType)
    RelativeLayout rlSalesType;
    @InjectView(R.id.rlSPType)
    RelativeLayout rlSPType;
    @InjectView(R.id.rlCollectionType)
    RelativeLayout rlCollectionType;
    @InjectView(R.id.tvProjectType)
    TextView tvProjectType;
    @InjectView(R.id.tvRegularType)
    TextView tvRegularType;
    @InjectView(R.id.tvOneType)
    TextView tvOneType;
    @InjectView(R.id.tvServiceType)
    TextView tvServiceType;
    @InjectView(R.id.tvSalesType)
    TextView tvSalesType;
    @InjectView(R.id.tvSPType)
    TextView tvSPType;
    @InjectView(R.id.tvCollectionType)
    TextView tvCollectionType;
    SQLiteDatabase db;
    //Users:
    @InjectView(R.id.lvUsers)
    ListView lvUsers;
    @InjectView(R.id.scrollMain)
    ScrollView scrollMain;
    int flagPriority = Constant.PRIORITY_ALL;
    ArrayList<WorkItem> listTasks = new ArrayList<>();
    ArrayList<User> listUsers = new ArrayList<>();
    ArrayList<HashMap<String, String>> listType = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        init();
        GetUserList();
        setDefault();
        setTaskCount();

        scrollMain.fullScroll(ScrollView.FOCUS_UP);
        scrollMain.smoothScrollBy(0, 0);
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

    @OnClick(R.id.rlSave)
    @SuppressWarnings("unused")
    public void Save(View view) {

        //Priority
        write(Constant.SHRED_PR.KEY_FILTER_PRIORITY, "" + flagPriority);

        //Task Type
        String taskType="";
        for (int i=0;i<listType.size();i++){
            if(listType.get(i).get("selected").equals("1")){
                taskType=taskType+","+listType.get(i).get("type");
            }
        }
        write(Constant.SHRED_PR.KEY_FILTER_TASK_TYPE, "" + taskType);
        Log.e("taskType", ">>" + taskType);

        //users:
        String assigned = "";
        for (int i = 0; i < listUsers.size(); i++) {
            User user = listUsers.get(i);
            if (user.isSelected()) {
                if (assigned.length() == 0) assigned = "" + user.getUser_Id();
                else assigned += "," + user.getUser_Id();
            }
        }
        write(Constant.SHRED_PR.KEY_FILTER_ASSIGNED, "" + assigned);

        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);
    }

    @OnClick(R.id.rlAllPriority)
    @SuppressWarnings("unused")
    public void AllPriority(View view) {

        viewAllPriority.setBackgroundColor(getResources().getColor(R.color.green_header));
        viewHighPriority.setBackgroundColor(getResources().getColor(R.color.white));
        viewMediumPriority.setBackgroundColor(getResources().getColor(R.color.white));
        viewLowPriority.setBackgroundColor(getResources().getColor(R.color.white));

        tvAllPriority.setTextColor(getResources().getColor(R.color.black));
        tvHighPriority.setTextColor(getResources().getColor(R.color.gray));
        tvMediumPriority.setTextColor(getResources().getColor(R.color.gray));
        tvLowPriority.setTextColor(getResources().getColor(R.color.gray));

        flagPriority = Constant.PRIORITY_ALL;

    }

    @OnClick(R.id.rlHighPriority)
    @SuppressWarnings("unused")
    public void HighPriority(View view) {

        viewAllPriority.setBackgroundColor(getResources().getColor(R.color.white));
        viewHighPriority.setBackgroundColor(getResources().getColor(R.color.green_header));
        viewMediumPriority.setBackgroundColor(getResources().getColor(R.color.white));
        viewLowPriority.setBackgroundColor(getResources().getColor(R.color.white));

        tvAllPriority.setTextColor(getResources().getColor(R.color.gray));
        tvHighPriority.setTextColor(getResources().getColor(R.color.black));
        tvMediumPriority.setTextColor(getResources().getColor(R.color.gray));
        tvLowPriority.setTextColor(getResources().getColor(R.color.gray));

        flagPriority = Constant.PRIORITY_HIGH;

    }

    @OnClick(R.id.rlMediumPriority)
    @SuppressWarnings("unused")
    public void MediumPriority(View view) {

        viewAllPriority.setBackgroundColor(getResources().getColor(R.color.white));
        viewHighPriority.setBackgroundColor(getResources().getColor(R.color.white));
        viewMediumPriority.setBackgroundColor(getResources().getColor(R.color.green_header));
        viewLowPriority.setBackgroundColor(getResources().getColor(R.color.white));

        tvAllPriority.setTextColor(getResources().getColor(R.color.gray));
        tvHighPriority.setTextColor(getResources().getColor(R.color.gray));
        tvMediumPriority.setTextColor(getResources().getColor(R.color.black));
        tvLowPriority.setTextColor(getResources().getColor(R.color.gray));

        flagPriority = Constant.PRIORITY_MEDIUM;

    }

    @OnClick(R.id.rlLowPriority)
    @SuppressWarnings("unused")
    public void LowPriority(View view) {

        viewAllPriority.setBackgroundColor(getResources().getColor(R.color.white));
        viewHighPriority.setBackgroundColor(getResources().getColor(R.color.white));
        viewMediumPriority.setBackgroundColor(getResources().getColor(R.color.white));
        viewLowPriority.setBackgroundColor(getResources().getColor(R.color.green_header));

        tvAllPriority.setTextColor(getResources().getColor(R.color.gray));
        tvHighPriority.setTextColor(getResources().getColor(R.color.gray));
        tvMediumPriority.setTextColor(getResources().getColor(R.color.gray));
        tvLowPriority.setTextColor(getResources().getColor(R.color.black));

        flagPriority = Constant.PRIORITY_LOW;

    }

    @OnClick(R.id.rlProjectType)
    @SuppressWarnings("unused")
    public void rlProjectType(View view) {

        if (listType.get(0).get("selected").equals("0")) {
            listType.get(0).put("selected", "1");
            rlProjectType.setBackgroundResource(R.drawable.rectangle_blue_selected);
            tvProjectType.setTextColor(getResources().getColor(R.color.white));
        } else {
            listType.get(0).put("selected", "0");
            rlProjectType.setBackgroundResource(R.drawable.rectangle_unselected);
            tvProjectType.setTextColor(getResources().getColor(R.color.gray));
        }

    }

    @OnClick(R.id.rlRegularType)
    @SuppressWarnings("unused")
    public void rlRegularType(View view) {

        if (listType.get(1).get("selected").equals("0")) {
            listType.get(1).put("selected", "1");
            rlRegularType.setBackgroundResource(R.drawable.rectangle_blue_selected);
            tvRegularType.setTextColor(getResources().getColor(R.color.white));
        } else {
            listType.get(1).put("selected", "0");
            rlRegularType.setBackgroundResource(R.drawable.rectangle_unselected);
            tvRegularType.setTextColor(getResources().getColor(R.color.gray));
        }

    }

    @OnClick(R.id.rlOneType)
    @SuppressWarnings("unused")
    public void rlOneType(View view) {

        if (listType.get(2).get("selected").equals("0")) {
            listType.get(2).put("selected", "1");
            rlOneType.setBackgroundResource(R.drawable.rectangle_blue_selected);
            tvOneType.setTextColor(getResources().getColor(R.color.white));
        } else {
            listType.get(2).put("selected", "0");
            rlOneType.setBackgroundResource(R.drawable.rectangle_unselected);
            tvOneType.setTextColor(getResources().getColor(R.color.gray));
        }

    }

    @OnClick(R.id.rlServiceType)
    @SuppressWarnings("unused")
    public void rlServiceType(View view) {

        if (listType.get(3).get("selected").equals("0")) {
            listType.get(3).put("selected", "1");
            rlServiceType.setBackgroundResource(R.drawable.rectangle_blue_selected);
            tvServiceType.setTextColor(getResources().getColor(R.color.white));
        } else {
            listType.get(3).put("selected", "0");
            rlServiceType.setBackgroundResource(R.drawable.rectangle_unselected);
            tvServiceType.setTextColor(getResources().getColor(R.color.gray));
        }

    }

    @OnClick(R.id.rlSalesType)
    @SuppressWarnings("unused")
    public void rlSalesType(View view) {

        if (listType.get(4).get("selected").equals("0")) {
            listType.get(4).put("selected", "1");
            rlSalesType.setBackgroundResource(R.drawable.rectangle_blue_selected);
            tvSalesType.setTextColor(getResources().getColor(R.color.white));
        } else {
            listType.get(4).put("selected", "0");
            rlSalesType.setBackgroundResource(R.drawable.rectangle_unselected);
            tvSalesType.setTextColor(getResources().getColor(R.color.gray));
        }

    }

    @OnClick(R.id.rlSPType)
    @SuppressWarnings("unused")
    public void rlSPType(View view) {

        if (listType.get(5).get("selected").equals("0")) {
            listType.get(5).put("selected", "1");
            rlSPType.setBackgroundResource(R.drawable.rectangle_blue_selected);
            tvSPType.setTextColor(getResources().getColor(R.color.white));
        } else {
            listType.get(5).put("selected", "0");
            rlSPType.setBackgroundResource(R.drawable.rectangle_unselected);
            tvSPType.setTextColor(getResources().getColor(R.color.gray));
        }

    }

    @OnClick(R.id.rlCollectionType)
    @SuppressWarnings("unused")
    public void rlCollectionType(View view) {

        if (listType.get(6).get("selected").equals("0")) {
            listType.get(6).put("selected", "1");
            rlCollectionType.setBackgroundResource(R.drawable.rectangle_blue_selected);
            tvCollectionType.setTextColor(getResources().getColor(R.color.white));
        } else {
            listType.get(6).put("selected", "0");
            rlCollectionType.setBackgroundResource(R.drawable.rectangle_unselected);
            tvCollectionType.setTextColor(getResources().getColor(R.color.gray));
        }

    }

    private void init() {

        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);

        SQLiteHelper helper = new SQLiteHelper(getApplicationContext(), Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();

        scrollMain.fullScroll(ScrollView.FOCUS_UP);

        for (int i = 0; i < Constant.WORKTYPES.length; i++) {
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("selected", "0");
            hashMap.put("type", ""+Constant.WORKTYPES[i]);
            listType.add(hashMap);
        }

        View[] views = new View[10];

        for (int i = 0; i < 10; i++) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            TextView textView = new TextView(getApplicationContext());
            textView.setLayoutParams(lp);
            textView.setText("Hi Hello....");
            views[i] = textView;
        }

        LinearLayout LL = new LinearLayout(getApplicationContext());
        populateText(LL, views, getApplicationContext());
    }

    private void populateText(LinearLayout ll, View[] views, Context mContext) {
        Display display = getWindowManager().getDefaultDisplay();
        ll.removeAllViews();
        int maxWidth = display.getWidth() - 20;

        LinearLayout.LayoutParams params;
        LinearLayout newLL = new LinearLayout(mContext);
        newLL.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        newLL.setGravity(Gravity.LEFT);
        newLL.setOrientation(LinearLayout.HORIZONTAL);

        int widthSoFar = 0;

        for (int i = 0; i < views.length; i++) {
            LinearLayout LL = new LinearLayout(mContext);
            LL.setOrientation(LinearLayout.HORIZONTAL);
            LL.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
            LL.setLayoutParams(new ListView.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            //my old code
            //TV = new TextView(mContext);
            //TV.setText(textArray[i]);
            //TV.setTextSize(size);  <<<< SET TEXT SIZE
            //TV.measure(0, 0);
            views[i].measure(0, 0);
            params = new LinearLayout.LayoutParams(views[i].getMeasuredWidth(),
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            //params.setMargins(5, 0, 5, 0);  // YOU CAN USE THIS
            //LL.addView(TV, params);
            LL.addView(views[i], params);
            LL.measure(0, 0);
            widthSoFar += views[i].getMeasuredWidth();// YOU MAY NEED TO ADD THE MARGINS
            Log.e("views width:", "" + views[i].getMeasuredWidth());
            if (widthSoFar >= maxWidth) {
                ll.addView(newLL);

                newLL = new LinearLayout(mContext);
                newLL.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                newLL.setOrientation(LinearLayout.HORIZONTAL);
                newLL.setGravity(Gravity.LEFT);
                params = new LinearLayout.LayoutParams(LL
                        .getMeasuredWidth(), LL.getMeasuredHeight());
                newLL.addView(LL, params);
                widthSoFar = LL.getMeasuredWidth();
            } else {
                newLL.addView(LL);
            }
        }
        ll.addView(newLL);
    }

    private void setDefault() {

        //Priority:
        flagPriority = Integer.parseInt(read(Constant.SHRED_PR.KEY_FILTER_PRIORITY));
        switch (flagPriority) {
            case 0:

                viewAllPriority.setBackgroundColor(getResources().getColor(R.color.green_header));
                viewHighPriority.setBackgroundColor(getResources().getColor(R.color.white));
                viewMediumPriority.setBackgroundColor(getResources().getColor(R.color.white));
                viewLowPriority.setBackgroundColor(getResources().getColor(R.color.white));

                tvAllPriority.setTextColor(getResources().getColor(R.color.black));
                tvHighPriority.setTextColor(getResources().getColor(R.color.gray));
                tvMediumPriority.setTextColor(getResources().getColor(R.color.gray));
                tvLowPriority.setTextColor(getResources().getColor(R.color.gray));

                break;
            case 1:

                viewAllPriority.setBackgroundColor(getResources().getColor(R.color.white));
                viewHighPriority.setBackgroundColor(getResources().getColor(R.color.green_header));
                viewMediumPriority.setBackgroundColor(getResources().getColor(R.color.white));
                viewLowPriority.setBackgroundColor(getResources().getColor(R.color.white));

                tvAllPriority.setTextColor(getResources().getColor(R.color.gray));
                tvHighPriority.setTextColor(getResources().getColor(R.color.black));
                tvMediumPriority.setTextColor(getResources().getColor(R.color.gray));
                tvLowPriority.setTextColor(getResources().getColor(R.color.gray));

                break;
            case 2:

                viewAllPriority.setBackgroundColor(getResources().getColor(R.color.white));
                viewHighPriority.setBackgroundColor(getResources().getColor(R.color.white));
                viewMediumPriority.setBackgroundColor(getResources().getColor(R.color.green_header));
                viewLowPriority.setBackgroundColor(getResources().getColor(R.color.white));

                tvAllPriority.setTextColor(getResources().getColor(R.color.gray));
                tvHighPriority.setTextColor(getResources().getColor(R.color.gray));
                tvMediumPriority.setTextColor(getResources().getColor(R.color.black));
                tvLowPriority.setTextColor(getResources().getColor(R.color.gray));

                break;
            case 3:

                viewAllPriority.setBackgroundColor(getResources().getColor(R.color.white));
                viewHighPriority.setBackgroundColor(getResources().getColor(R.color.white));
                viewMediumPriority.setBackgroundColor(getResources().getColor(R.color.white));
                viewLowPriority.setBackgroundColor(getResources().getColor(R.color.green_header));

                tvAllPriority.setTextColor(getResources().getColor(R.color.gray));
                tvHighPriority.setTextColor(getResources().getColor(R.color.gray));
                tvMediumPriority.setTextColor(getResources().getColor(R.color.gray));
                tvLowPriority.setTextColor(getResources().getColor(R.color.black));

                break;
            default:

                viewAllPriority.setBackgroundColor(getResources().getColor(R.color.green_header));
                viewHighPriority.setBackgroundColor(getResources().getColor(R.color.white));
                viewMediumPriority.setBackgroundColor(getResources().getColor(R.color.white));
                viewLowPriority.setBackgroundColor(getResources().getColor(R.color.white));

                tvAllPriority.setTextColor(getResources().getColor(R.color.black));
                tvHighPriority.setTextColor(getResources().getColor(R.color.gray));
                tvMediumPriority.setTextColor(getResources().getColor(R.color.gray));
                tvLowPriority.setTextColor(getResources().getColor(R.color.gray));

                flagPriority = Constant.PRIORITY_ALL;

                break;
        }

        //Task Type:
        String taskType = read(Constant.SHRED_PR.KEY_FILTER_TASK_TYPE);

        if (taskType.contains(Constant.WORKTYPES[Constant.PROJECT])) {
            listType.get(0).put("selected", "1");
            rlProjectType.setBackgroundResource(R.drawable.rectangle_blue_selected);
            tvProjectType.setTextColor(getResources().getColor(R.color.white));
        } else {
            listType.get(0).put("selected", "0");
            rlProjectType.setBackgroundResource(R.drawable.rectangle_unselected);
            tvProjectType.setTextColor(getResources().getColor(R.color.gray));
        }

        if (taskType.contains(Constant.WORKTYPES[Constant.REGULAR])) {
            listType.get(1).put("selected", "1");
            rlRegularType.setBackgroundResource(R.drawable.rectangle_blue_selected);
            tvRegularType.setTextColor(getResources().getColor(R.color.white));
        } else {
            listType.get(1).put("selected", "0");
            rlRegularType.setBackgroundResource(R.drawable.rectangle_unselected);
            tvRegularType.setTextColor(getResources().getColor(R.color.gray));
        }

        if (taskType.contains(Constant.WORKTYPES[Constant.ONE_TIME])) {
            listType.get(2).put("selected", "1");
            rlOneType.setBackgroundResource(R.drawable.rectangle_blue_selected);
            tvOneType.setTextColor(getResources().getColor(R.color.white));
        } else {
            listType.get(2).put("selected", "0");
            rlOneType.setBackgroundResource(R.drawable.rectangle_unselected);
            tvOneType.setTextColor(getResources().getColor(R.color.gray));
        }

        if (taskType.contains(Constant.WORKTYPES[Constant.SERVICE_CALL])) {
            listType.get(3).put("selected", "1");
            rlServiceType.setBackgroundResource(R.drawable.rectangle_blue_selected);
            tvServiceType.setTextColor(getResources().getColor(R.color.white));
        } else {
            listType.get(3).put("selected", "0");
            rlServiceType.setBackgroundResource(R.drawable.rectangle_unselected);
            tvServiceType.setTextColor(getResources().getColor(R.color.gray));
        }

        if (taskType.contains(Constant.WORKTYPES[Constant.SALES_CALL])) {
            listType.get(4).put("selected", "1");
            rlSalesType.setBackgroundResource(R.drawable.rectangle_blue_selected);
            tvSalesType.setTextColor(getResources().getColor(R.color.white));
        } else {
            listType.get(4).put("selected", "0");
            rlSalesType.setBackgroundResource(R.drawable.rectangle_unselected);
            tvSalesType.setTextColor(getResources().getColor(R.color.gray));
        }

        if (taskType.contains(Constant.WORKTYPES[Constant.SHOPPING_PURCHASE])) {
            listType.get(5).put("selected", "1");
            rlSPType.setBackgroundResource(R.drawable.rectangle_blue_selected);
            tvSPType.setTextColor(getResources().getColor(R.color.white));
        } else {
            listType.get(5).put("selected", "0");
            rlSPType.setBackgroundResource(R.drawable.rectangle_unselected);
            tvSPType.setTextColor(getResources().getColor(R.color.gray));
        }

        if (taskType.contains(Constant.WORKTYPES[Constant.COLLECTION])) {
            listType.get(6).put("selected", "1");
            rlCollectionType.setBackgroundResource(R.drawable.rectangle_blue_selected);
            tvCollectionType.setTextColor(getResources().getColor(R.color.white));
        } else {
            listType.get(6).put("selected", "0");
            rlCollectionType.setBackgroundResource(R.drawable.rectangle_unselected);
            tvCollectionType.setTextColor(getResources().getColor(R.color.gray));
        }

        //Set users:
        String Assignees = read(Constant.SHRED_PR.KEY_FILTER_ASSIGNED);
        if (Assignees != null && Assignees.length() > 0) {
            String asgns[] = Assignees.split(",");  // 35,45
            if (asgns != null && asgns.length > 0) {
                for (int i = 0; i < asgns.length; i++) {
                    for (int j = 0; j < listUsers.size(); j++) {
                        if (asgns[i].equals("" + listUsers.get(j).getUser_Id())) {
                            listUsers.get(j).setSelected(true);
                        }
                    }
                }
            }
        }

    }

    public void setTaskCount() {
        listTasks.clear();
        Cursor cursor = db.rawQuery("select * from " + Constant.WorkItemTable + " where " + WorkItem.STATUS + " IN (\"Approved\",\"Done\",\"Delegated\",\"Pending\",\"Offline\")", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {
                        WorkItem workItem = new WorkItem();
                        workItem.setPriority(cursor.getString(cursor.getColumnIndex(WorkItem.PRIORITY)));
                        workItem.setTaskType(cursor.getString(cursor.getColumnIndex(WorkItem.TASK_TYPE)));
                        workItem.setTaskCode(cursor.getInt(cursor.getColumnIndex(WorkItem.TASK_ID)));

                        listTasks.add(workItem);
                } while (cursor.moveToNext());
            }
        }
        if(cursor!=null) cursor.close();

        //Set Priority:
        tvAllPriority.setText("All\n(" + listTasks.size() + ")");

        ArrayList<WorkItem> listTasksHighP = new ArrayList<>();
        for (int i = 0; i < listTasks.size(); i++) {
            if (listTasks.get(i).getPriority().equals("High")) listTasksHighP.add(listTasks.get(i));
        }
        tvHighPriority.setText("High\n(" + listTasksHighP.size() + ")");

        ArrayList<WorkItem> listTasksMediumP = new ArrayList<>();
        for (int i = 0; i < listTasks.size(); i++) {
            if (listTasks.get(i).getPriority().equals("Medium"))
                listTasksMediumP.add(listTasks.get(i));
        }
        tvMediumPriority.setText("Medium\n(" + listTasksMediumP.size() + ")");

        ArrayList<WorkItem> listTasksLowP = new ArrayList<>();
        for (int i = 0; i < listTasks.size(); i++) {
            if (listTasks.get(i).getPriority().equals("Low")) listTasksLowP.add(listTasks.get(i));
        }
        tvLowPriority.setText("Low\n(" + listTasksLowP.size() + ")");

        //Task Type:

        ArrayList<WorkItem> listTasks1 = new ArrayList<>();
        for (int i = 0; i < listTasks.size(); i++) {
            if (listTasks.get(i).getTaskType().equals(Constant.WORKTYPES[Constant.PROJECT])) listTasks1.add(listTasks.get(i));
        }
        tvProjectType.setText(Constant.WORKTYPES[0]+" (" + listTasks1.size() + ")");

        ArrayList<WorkItem> listTasks2 = new ArrayList<>();
        for (int i = 0; i < listTasks.size(); i++) {
            if (listTasks.get(i).getTaskType().equals(Constant.WORKTYPES[Constant.REGULAR])) listTasks2.add(listTasks.get(i));
        }
        tvRegularType.setText(Constant.WORKTYPES[1]+" (" + listTasks2.size() + ")");

        ArrayList<WorkItem> listTasks3 = new ArrayList<>();
        for (int i = 0; i < listTasks.size(); i++) {
            if (listTasks.get(i).getTaskType().equals(Constant.WORKTYPES[Constant.ONE_TIME])) listTasks3.add(listTasks.get(i));
        }
        tvOneType.setText(Constant.WORKTYPES[2]+" (" + listTasks3.size() + ")");

        ArrayList<WorkItem> listTasks4 = new ArrayList<>();
        for (int i = 0; i < listTasks.size(); i++) {
            if (listTasks.get(i).getTaskType().equals(Constant.WORKTYPES[Constant.SERVICE_CALL])) listTasks4.add(listTasks.get(i));
        }
        tvServiceType.setText(Constant.WORKTYPES[3]+" (" + listTasks4.size() + ")");

        ArrayList<WorkItem> listTasks5 = new ArrayList<>();
        for (int i = 0; i < listTasks.size(); i++) {
            if (listTasks.get(i).getTaskType().equals(Constant.WORKTYPES[Constant.SALES_CALL])) listTasks5.add(listTasks.get(i));
        }
        tvSalesType.setText(Constant.WORKTYPES[4]+" (" + listTasks5.size() + ")");

        ArrayList<WorkItem> listTasks6 = new ArrayList<>();
        for (int i = 0; i < listTasks.size(); i++) {
            if (listTasks.get(i).getTaskType().equals(Constant.WORKTYPES[Constant.SHOPPING_PURCHASE])) listTasks6.add(listTasks.get(i));
        }
        tvSPType.setText(Constant.WORKTYPES[5]+" (" + listTasks6.size() + ")");

        ArrayList<WorkItem> listTasks7 = new ArrayList<>();
        for (int i = 0; i < listTasks.size(); i++) {
            if (listTasks.get(i).getTaskType().equals(Constant.WORKTYPES[Constant.COLLECTION])) listTasks7.add(listTasks.get(i));
        }
        tvCollectionType.setText(Constant.WORKTYPES[6] + " (" + listTasks7.size() + ")");

    }

    private void GetUserList() {

        listUsers.clear();

        Cursor cursor = db.rawQuery("select * from " + Constant.UserTable, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                do {

                    User user = new User();
                    user.setUser_Id(cursor.getInt(cursor.getColumnIndex(User.USER_ID)));
                    user.setFirstName(cursor.getString(cursor.getColumnIndex(User.FIRST_NAME)));
                    user.setLastName(cursor.getString(cursor.getColumnIndex(User.LAST_NAME)));
                    user.setUser_Image(cursor.getString(cursor.getColumnIndex(User.USER_IMAGE)));
                    user.setSelected(false);
                    listUsers.add(user);

                } while (cursor.moveToNext());
            }
        }
        if(cursor!=null) cursor.close();

        lvUsers.setAdapter(new CustomAdapter(listUsers));
        Util.setListViewHeightBasedOnChildren(lvUsers);

    }

    class CustomAdapter extends BaseAdapter {

        ArrayList<User> locallist;

        public CustomAdapter(ArrayList<User> locallist) {
            // TODO Auto-generated constructor stub
            this.locallist = locallist;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return locallist.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            final ViewHolder holder;
            View view = convertView;

            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.listrow_assignee_cc,
                        parent, false);
                holder = new ViewHolder(view);
                assert view != null;
                holder.name = (TextView) view.findViewById(R.id.name);
                holder.relativeCheck = (RelativeLayout) view
                        .findViewById(R.id.relativeCheck);
                holder.btnCheck = (ImageButton) view.findViewById(R.id.check);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            final User user = locallist.get(position);
            holder.name.setText("" + user.getFirstName() + " " + user.getLastName());

            if (user.isSelected()) {
                holder.btnCheck.setBackgroundResource(R.drawable.chacked);
            } else {
                holder.btnCheck.setBackgroundResource(R.drawable.unchacked);
            }

            holder.relativeCheck.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (user.isSelected()) {
                        holder.btnCheck
                                .setBackgroundResource(R.drawable.unchacked);
                        user.setSelected(false);
                        locallist.get(position).setSelected(false);
                    } else {
                        holder.btnCheck
                                .setBackgroundResource(R.drawable.chacked);
                        locallist.get(position).setSelected(true);
                    }

                }
            });


            return view;
        }
    }

    class ViewHolder {
        TextView name;
        RelativeLayout relativeCheck;
        ImageButton btnCheck;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
            final Typeface mFont = Typeface.createFromAsset(getAssets(),
                    "fonts/montserrat_regular.otf");
            final ViewGroup mContainer = (ViewGroup) view.getRootView();

            Util.setAppFont(mContainer, mFont);
        }
    }

}
