package com.bigbang.superteam.util;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.bigbang.superteam.R;
import com.bigbang.superteam.dataObjs.WorkItem;

/**
 * Created by USER 3 on 20/07/2015.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        SQLiteHelper helper = new SQLiteHelper(context, Constant.DatabaseName);
        helper.createDatabase();
        SQLiteDatabase db = helper.openDatabase();

        //Define Notification Manager
        String itemID = intent.getStringExtra(WorkItem.TASK_ID);
        WorkItem workItem = null;
        Cursor crsr = db.rawQuery("select * from " + Constant.WorkItemTable + " where " + WorkItem.TASK_ID + " = " + itemID, null);
        if (crsr != null && crsr.getCount() > 0) {
            crsr.moveToFirst();
            workItem = WorkItem.getWorkItemfromCursor(crsr, crsr.getPosition());
            Log.e("Alarm", "Work item Received"+workItem.getStatus());
        }
        if(crsr!=null) crsr.close();

        if (workItem != null && workItem.getStatus().equals("Approved")) {

            String message = intent.getStringExtra("message");
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            Log.e("Alarm Receiveed", message);

            Log.e("Alarm", "Notification setted");
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//Define sound URI
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.notification1_icon)
                    .setContentTitle("Task Completion Notification")
                    .setContentText(message)
                    .setSound(soundUri); //This sets the sound to play
//Display notification
            notificationManager.notify(0, mBuilder.build());
        }
    }
}
