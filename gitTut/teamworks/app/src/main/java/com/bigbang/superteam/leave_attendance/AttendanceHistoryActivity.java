package com.bigbang.superteam.leave_attendance;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.bigbang.superteam.R;
import com.bigbang.superteam.adapter.AttendanceHistoryAdapter;
import com.bigbang.superteam.dataObjs.AttendanceHistoryModel;
import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.SQLiteHelper;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by USER 3 on 4/9/2015.
 */
public class AttendanceHistoryActivity extends Activity {

    @InjectView(R.id.lv_attendance_history)
    ListView lwAttendanceHistory;

    AttendanceHistoryAdapter atndHistoryAdapter;
    public static ArrayList<AttendanceHistoryModel> listAttendHistory;
    AttendanceHistoryModel attendanceHistoryModel;

    SQLiteHelper helper;
    String TAG = "AttendanceHistory Activity";
    public static SQLiteDatabase db = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendancehistory);
        ButterKnife.inject(this);
        helper = new SQLiteHelper(this, Constant.DatabaseName);
        helper.createDatabase();
        db = helper.openDatabase();
        listAttendHistory = new ArrayList<AttendanceHistoryModel>();
        fetchDataFromDB();

    }
    public void fetchDataFromDB()
    {
        Cursor cursor = db.rawQuery("select * from attendanceHistory", null);

        if(cursor != null)
        {

            listAttendHistory.clear();
            if (cursor.getCount() > 0) {
                cursor.moveToLast();
                do {
                    attendanceHistoryModel = new AttendanceHistoryModel();

                    attendanceHistoryModel.setUserId(cursor.getString(cursor.getColumnIndex("user_id")));
                    attendanceHistoryModel.setRequestTime(cursor.getString(cursor.getColumnIndex("time")));
                    attendanceHistoryModel.setRequestDate(cursor.getString(cursor.getColumnIndex("date")));
                    attendanceHistoryModel.setRequestStatus(cursor.getString(cursor.getColumnIndex("status")));
                    attendanceHistoryModel.setAttendanceType(cursor.getString(cursor.getColumnIndex("attendanceType")));
                    attendanceHistoryModel.setReason(cursor.getString(cursor.getColumnIndex("reason")));
                    Log.e(TAG,"Reason is:- "+cursor.getString(cursor.getColumnIndex("reason")));
                    attendanceHistoryModel.setLocationType(cursor.getString(cursor.getColumnIndex("location")));
                    attendanceHistoryModel.setImageUrl(cursor.getString(cursor.getColumnIndex("imgUrl")));
                    attendanceHistoryModel.setAttendanceID(cursor.getString(cursor.getColumnIndex("attendanceID")));
                    attendanceHistoryModel.setIsManualAttendance(cursor.getString(cursor.getColumnIndex("isManualAttendance")));

                    listAttendHistory.add(attendanceHistoryModel);
                    // map.put("pk", "" + crsr.getString(crsr.getColumnIndex("pk")));

                }while (cursor.moveToPrevious());
            }

            Log.e(TAG,"List size is:- "+listAttendHistory.size());
        }

       atndHistoryAdapter = new AttendanceHistoryAdapter(this, listAttendHistory);
       lwAttendanceHistory.setAdapter(atndHistoryAdapter);
    }
}
