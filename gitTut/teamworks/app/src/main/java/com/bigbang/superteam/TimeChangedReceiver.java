package com.bigbang.superteam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bigbang.superteam.util.Util;

import java.util.Calendar;

/**
 * Created by USER 8 on 13-Jul-16.
 */
public class TimeChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
//        Toast.makeText(context, "OnTimeChange", Toast.LENGTH_SHORT).show();
        Calendar calendar = Calendar.getInstance();
        String action = intent.getAction();

        if (action.equals(Intent.ACTION_TIME_CHANGED)) {
//            Toast.makeText(context, "TimeChange", Toast.LENGTH_SHORT).show();
            Util.appendLog("@ Update Time @ " + calendar.get(Calendar.DAY_OF_MONTH) +
                    " / " + (calendar.get(Calendar.MONTH) + 1) +
                    " == " + calendar.get(Calendar.HOUR_OF_DAY) +
                    " : " + calendar.get(Calendar.MINUTE) +
                    " : " + calendar.get(Calendar.SECOND));
            Util.registerUserTrackingReceiver(context);
        } else if (action.equals(Intent.ACTION_TIMEZONE_CHANGED)) {
//            Toast.makeText(context, "TimeZoneChanged", Toast.LENGTH_SHORT).show();
            Util.appendLog("@ Update TimeZone @ " + calendar.get(Calendar.DAY_OF_MONTH) +
                    " / " + (calendar.get(Calendar.MONTH) + 1) +
                    " == " + calendar.get(Calendar.HOUR_OF_DAY) +
                    " : " + calendar.get(Calendar.MINUTE) +
                    " : " + calendar.get(Calendar.SECOND));
            Util.registerUserTrackingReceiver(context);
        }
//        Toast.makeText(context, "TimeChange", Toast.LENGTH_SHORT).show();
//        BroadcastsManager.updateBroadcastsFromAlarms(context,
//                AlarmsDbAdapter.getInstance(context));
    }
}
