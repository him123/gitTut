package com.bigbang.superteam.alarm;

/**
 * Created by USER 8 on 21-Jun-16.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.bigbang.superteam.util.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SimpleWakefulReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // This is the Intent to deliver to our service.
        Calendar calendar = Calendar.getInstance();
        Util.appendLog("===============================");
        Util.appendLog("Starting service @ " + calendar.get(Calendar.DAY_OF_MONTH) + " / " + (calendar.get(Calendar.MONTH) + 1) + " == " + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE) + " : " + calendar.get(Calendar.SECOND));

        Intent service = new Intent(context, UserTrackingReceiverIntentService.class);
        Date date = new Date();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String DateTime = sdf.format(date);
        DateTime = Util.locatToUTC(DateTime);
        service.putExtra("date", String.valueOf(DateTime));

        String latitude = Util.ReadSharePrefrence(context, "track_lat");
        String longitude = Util.ReadSharePrefrence(context, "track_lng");

        service.putExtra("lat", latitude);
        service.putExtra("lon", longitude);

        Log.i("SimpleWakefulReceiver", "Starting service @ " + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE) + " : " + calendar.get(Calendar.SECOND));

        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, service);
    }
}