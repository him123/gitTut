package com.bigbang.superteam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by USER 8 on 8/25/2015.
 */
public class HeartbeatReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        context.sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
        context.sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));

//        String roleId = Util.ReadSharePrefrence(context, Constant.SHRED_PR.KEY_ROLE_ID);
//        if (Arrays.asList("3", "4").contains(roleId)) {
////            Util.registerUserTrackingReceiver(context);
//        }
    }
}
