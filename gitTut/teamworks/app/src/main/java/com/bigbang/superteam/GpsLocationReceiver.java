package com.bigbang.superteam;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.LocationService;
import com.bigbang.superteam.util.Util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by USER 8 on 11-Jun-16.
 */
public class GpsLocationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().matches("android.location.PROVIDERS_CHANGED")) {
            Calendar calendar = Calendar.getInstance();
            ContentResolver contentResolver = context.getContentResolver();
            // Find out what the settings say about which providers are enabled
            int mode = Settings.Secure.getInt(
                    contentResolver, Settings.Secure.LOCATION_MODE, Settings.Secure.LOCATION_MODE_OFF);
            String roleId = read(Constant.SHRED_PR.KEY_ROLE_ID, context);

            if (mode == Settings.Secure.LOCATION_MODE_OFF) {
                // Location is turned OFF!
                if (!roleId.equals("2") && !roleId.equals("1")) {
                    boolean isLogged = Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_IS_LOGGEDIN).equals("true");
                    if (isLogged) {
                        try {
                            SimpleDateFormat sdfTime = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                            SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy/MM/dd");
                            Date timeNow = sdfTime.parse(sdfTime.format(new Date()) + ":00");
                            Date officeStartTime = sdfTime.parse(sdfDate.format(new Date()) + " " + Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_COMPANY_STARTTIME));
                            Date officeEndTime = sdfTime.parse(sdfDate.format(new Date()) + " " + Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_COMPANY_ENDTIME));
                            if (((timeNow.compareTo(officeStartTime) > 0) || (timeNow.compareTo(officeStartTime) == 0)) &&
                                    ((timeNow.compareTo(officeEndTime) < 0) || (timeNow.compareTo(officeEndTime) == 0))) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    generateNotification(context, "You've turned off location services, which has disabled SuperTeam tracking. " +
                                            "Please turn your location services on.");
                                }

//                                Util.WriteSharePrefrence(context, "track_lat", "" + "0.0");
//                                Util.WriteSharePrefrence(context, "track_lng", "" + "0.0");
                                Util.appendLog("GPS OFF Manually @ : "
                                        + calendar.get(Calendar.DAY_OF_MONTH) +
                                        " / " + (calendar.get(Calendar.MONTH) + 1) +
                                        " == " + calendar.get(Calendar.HOUR_OF_DAY) +
                                        " : " + calendar.get(Calendar.MINUTE) +
                                        " : " + calendar.get(Calendar.SECOND));
                            }
                        } catch (Exception e) {
                            Log.d("", "Exception: " + e);
                        }
                    }
                }
            } else {
                context.startService(new Intent(context, LocationService.class));
                Util.appendLog("GPS On Manually @ : " + calendar.get(Calendar.DAY_OF_MONTH) + " / " + (calendar.get(Calendar.MONTH) + 1) + " == " + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE) + " : " + calendar.get(Calendar.SECOND));
            }
        }
    }

    protected String read(String key, Context context) {
        return Util.ReadSharePrefrence(context, key);
    }

    public void generateNotification(Context context, String message) {

        int icon = R.drawable.icon;
        int id = (int) System.currentTimeMillis();
        long[] pattern = {0, 200, 0};

        Intent notificationIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        long when = System.currentTimeMillis();

        PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        Notification notification = mBuilder.setSmallIcon(R.drawable.icon_bell).setTicker(message).setWhen(id)
                .setAutoCancel(true)
                .setWhen(when)
                .setContentTitle(Constant.AppNameSuper)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setContentIntent(intent)
                .setVibrate(pattern)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), icon))
                .setContentText(message).build();

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);

    }
}
