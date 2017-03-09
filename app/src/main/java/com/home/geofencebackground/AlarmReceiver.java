package com.home.geofencebackground;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by USUARIO on 07/03/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = AlarmReceiver.class.getSimpleName();
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;
        int intentType = intent.getExtras().getInt("IntentType");

        if (intentType == 1) {
            if (isMyServiceRunning(LocationClientService.class)) {
                context.stopService(new Intent(context, LocationClientService.class));
                Log.d(TAG, "service stopped");
            }else{
                Log.d(TAG, "Service already stopped");
            }
        } else if (intentType == 2) {

            if(!isMyServiceRunning(LocationClientService.class)) {
                context.startService(new Intent(context, LocationClientService.class));
                Log.d(TAG, "service started from broadcast");
            }else{
                Log.d(TAG, "Service already started");
            }
        } else {
            Log.i(TAG, "unknown code");
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
