package com.bigbang.superteam.workitem;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.common.BaseActivity;
import com.bigbang.superteam.dataObjs.WorkItem;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by USER 3 on 27/11/2015.
 */
public class SelectTaskActivity extends BaseActivity {

    @InjectView(R.id.tvTitle)
    TextView tvTitle;
    @InjectView(R.id.lvTasks)
    ListView lvTasks;

    ArrayList<WorkItem> listTasks = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_task);

        init();

        tvTitle.setText(getResources().getString(R.string.depent_workItems_));

        try {
            JSONArray jsonArray = new JSONArray(read(Constant.SHRED_PR.KEY_LIST));
            listTasks.clear();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.optJSONObject(i);

                WorkItem workItem = new WorkItem();
                workItem.setTaskCode(jsonObject1.optInt(WorkItem.TASK_ID));
                workItem.setTitle("" + jsonObject1.optString(WorkItem.TASK_NAME));
                workItem.setStartDate("" + jsonObject1.optString(WorkItem.START_DATE));
                workItem.setEndDate("" + jsonObject1.optString(WorkItem.END_DATE));
                workItem.setSelected(jsonObject1.optBoolean(WorkItem.SELECTED));

                listTasks.add(workItem);

            }
            lvTasks.setAdapter(new CustomAdapter(listTasks));
        } catch (Exception e) {
            e.printStackTrace();
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

        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < listTasks.size(); i++) {
                JSONObject jsonObject=new JSONObject();
                jsonObject.put(WorkItem.TASK_ID,""+listTasks.get(i).getTaskCode());
                jsonObject.put(WorkItem.TASK_NAME,""+listTasks.get(i).getTitle());
                jsonObject.put(WorkItem.START_DATE,""+listTasks.get(i).getStartDate());
                jsonObject.put(WorkItem.END_DATE, "" + listTasks.get(i).getEndDate());
                jsonObject.put(WorkItem.SELECTED,""+listTasks.get(i).isSelected());
                jsonArray.put(jsonObject);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        write(Constant.SHRED_PR.KEY_LIST, "" + jsonArray);
        write(Constant.SHRED_PR.KEY_TASKLIST, "1");
        finish();
        overridePendingTransition(R.anim.hold_top, R.anim.exit_in_bottom);

    }

    private void init() {
        final Typeface mFont = Typeface.createFromAsset(getAssets(),
                "fonts/montserrat_regular.otf");
        final ViewGroup mContainer = (ViewGroup) findViewById(
                android.R.id.content).getRootView();
        Util.setAppFont(mContainer, mFont);
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

    class CustomAdapter extends BaseAdapter {

        ArrayList<WorkItem> locallist;

        public CustomAdapter(ArrayList<WorkItem> locallist) {
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

            final WorkItem workItem = locallist.get(position);
            holder.name.setText("" + workItem.getTitle());

            if (workItem.isSelected()) {
                holder.btnCheck.setBackgroundResource(R.drawable.chacked);
            } else {
                holder.btnCheck.setBackgroundResource(R.drawable.unchacked);
            }

            holder.relativeCheck.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (workItem.isSelected()) {
                        holder.btnCheck
                                .setBackgroundResource(R.drawable.unchacked);
                        workItem.setSelected(false);
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

}

