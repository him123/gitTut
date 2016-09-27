package com.bigbang.superteam.leave_attendance;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bigbang.superteam.R;

/**
 * Created by USER 3 on 4/10/2015.
 */
public class AttendanceActivity extends Activity {

    Button attendenceBtn, leaveBtn;
    LinearLayout attendanceLayout, leaveLayout;
    Button chekInBtn, checkOutBtn, manualRequestBtn, historyBtn;
    Button applyForLeaveBtn, leaveStatusBtn, leaveBalanceBtn;


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_fragment);
        InitView();
        setViewAttendance();


    }


    private void InitView() {
        attendanceLayout = (LinearLayout) findViewById(R.id.linearLayoutAttendance);
        leaveLayout = (LinearLayout) findViewById(R.id.linearLayoutLeave);
        attendenceBtn = (Button) findViewById(R.id.btnAttendance);
        leaveBtn = (Button) findViewById(R.id.btnLeave);
        chekInBtn = (Button) findViewById(R.id.btnCheckIn);
        checkOutBtn = (Button) findViewById(R.id.btnCheckOut);
        manualRequestBtn = (Button) findViewById(R.id.btnManualRequest);
        historyBtn = (Button) findViewById(R.id.btnAttendanceHistory);
        applyForLeaveBtn = (Button) findViewById(R.id.btnApplyforLeave);
        leaveStatusBtn = (Button) findViewById(R.id.btnLeaveStatus);
        leaveBalanceBtn = (Button) findViewById(R.id.btnLeaveBalance);
        attendenceBtn.setOnClickListener(btnClickListener);
        leaveBtn.setOnClickListener(btnClickListener);
        chekInBtn.setOnClickListener(btnClickListener);
        checkOutBtn.setOnClickListener(btnClickListener);
        manualRequestBtn.setOnClickListener(btnClickListener);
        historyBtn.setOnClickListener(btnClickListener);
        applyForLeaveBtn.setOnClickListener(btnClickListener);
        leaveStatusBtn.setOnClickListener(btnClickListener);
        leaveBalanceBtn.setOnClickListener(btnClickListener);
    }

    View.OnClickListener btnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btnAttendance:
                    setViewAttendance();
                    break;
                case R.id.btnLeave:
                    setViewLeave();
                    break;
                case R.id.btnCheckIn:
                    //Check In Function
                    break;
                case R.id.btnCheckOut:
                    //Check Out Function
                    break;
                case R.id.btnManualRequest:
                    startManualRequest();
                    break;
                case R.id.btnAttendanceHistory:
                    startAttendanceHistory();
                    break;
                case R.id.btnApplyforLeave:
                    startApplyforLeave();
                    break;
                case R.id.btnLeaveStatus:
                    startLeaveStatus();
                    break;
                case R.id.btnLeaveBalance:
                    break;
            }
        }
    };

    private void startAttendanceHistory() {
        Intent attendanceHistory = new Intent(this, AttendanceHistoryActivity.class);
        startActivity(attendanceHistory);
    }

    private void startLeaveStatus() {
        Intent leaveStatus = new Intent(this, LeaveStatusActivity.class);
        startActivity(leaveStatus);
    }

    private void startApplyforLeave() {
        Intent applyforLeaveIntent = new Intent(this, ApplyLeaveActivity.class);
        startActivity(applyforLeaveIntent);
    }

    private void startManualRequest() {
        Intent manualAttendanceIntent = new Intent(this, ManualAttendanceActivity.class);
        startActivity(manualAttendanceIntent);
    }

    /// Sets Leave View
    private void setViewLeave() {
        leaveLayout.setVisibility(View.VISIBLE);
        attendanceLayout.setVisibility(View.GONE);
        attendenceBtn.setBackgroundColor(getResources().getColor(R.color.btnColor));
        leaveBtn.setBackgroundColor(getResources().getColor(R.color.btnPressedColor));
    }

    /// Sets Attendance View
    private void setViewAttendance() {
        leaveLayout.setVisibility(View.GONE);
        attendanceLayout.setVisibility(View.VISIBLE);
        attendenceBtn.setBackgroundColor(getResources().getColor(R.color.btnPressedColor));
        leaveBtn.setBackgroundColor(getResources().getColor(R.color.btnColor));
    }


}
