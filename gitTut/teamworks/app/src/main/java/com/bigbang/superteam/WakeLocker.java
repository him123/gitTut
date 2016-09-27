package com.bigbang.superteam;

/**
 * Created by USER 3 on 02/12/2015.
 */

import android.content.Context;
import android.os.PowerManager;

import com.bigbang.superteam.alarm.UserTrackingReceiverIntentService;

public abstract class WakeLocker {
    private static PowerManager.WakeLock wakeLock;

    public static void acquire(Context ctx) {
        if (wakeLock != null) wakeLock.release();

        PowerManager pm = (PowerManager) ctx.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK |
                PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.ON_AFTER_RELEASE, UserTrackingReceiverIntentService.TAG);
        wakeLock.acquire();
    }

    public static void release() {
        if (wakeLock != null) wakeLock.release();
        wakeLock = null;
    }
}
