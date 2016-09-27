package com.bigbang.superteam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.bigbang.superteam.util.Constant;
import com.bigbang.superteam.util.Util;

import java.util.Calendar;

/**
 * Created by USER 8 on 5/12/2015.
 */
public class DeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {

            Util.registerHeartbeatReceiver(context);

            try {
                if (Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_IS_LOGGEDIN).equals("true")) {
                    if (Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("3") ||
                            Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID).equals("4")) {
                        try {
                            Calendar calendar = Calendar.getInstance();
                            Util.appendLog("Mobile Restarted @ : " + calendar.get(Calendar.DAY_OF_MONTH) + " / " + (calendar.get(Calendar.MONTH) + 1) + " == " + calendar.get(Calendar.HOUR_OF_DAY) + " : " + calendar.get(Calendar.MINUTE) + " : " + calendar.get(Calendar.SECOND));
                            Util.registerUserTrackingReceiver(context);
                        } catch (Exception e) {
                            Util.appendLog("Exception on mobile restarted calling registerUserTrackingReceiver() : " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
