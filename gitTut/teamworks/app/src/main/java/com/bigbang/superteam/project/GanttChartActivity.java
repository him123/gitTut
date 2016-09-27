package com.bigbang.superteam.project;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.FrameLayout;

import com.bigbang.superteam.R;
import com.bigbang.superteam.dataObjs.WorkItem;
import com.bigbang.superteam.util.GanttView;

import java.util.ArrayList;

/**
 * Created by USER 3 on 4/16/2015.
 */
public class GanttChartActivity extends Activity {

    public static ArrayList<WorkItem> WorkList;
    GanttView ganttChartView;
    FrameLayout ganttFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_gantt);
        WorkList = (ArrayList<WorkItem>) getIntent().getSerializableExtra("List");
        ganttFrame = (FrameLayout) findViewById(R.id.chartFrameLayout);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        Bundle bundle = new Bundle();
        bundle.putInt("screenHeight", displaymetrics.heightPixels);
        bundle.putInt("screenWidth", displaymetrics.widthPixels);
        ganttChartView = new GanttView(this);
        ganttChartView.setData(bundle);
        ganttFrame.addView(ganttChartView);

    }
}
