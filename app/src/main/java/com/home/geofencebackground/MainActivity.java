package com.home.geofencebackground;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private int MULTIPLE_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i(TAG, "on create");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            boolean hasPermissionAccesssFineLocation = ContextCompat.checkSelfPermission(getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            boolean hasPermissionAccesssCoarseLocation = ContextCompat.checkSelfPermission(getApplicationContext(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            if (!hasPermissionAccesssFineLocation || !hasPermissionAccesssCoarseLocation)
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION}, MULTIPLE_PERMISSION_REQUEST_CODE);
            else
                startGeofenceService();
        } else {

            startGeofenceService();
        }

        Button stopBtn = (Button) findViewById(R.id.stop_btn);

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(getApplicationContext(), LocationClientService.class));
                Log.i(TAG, "service stopped");
            }
        });

        setUpAlarm();
    }

    public static Intent makeNotificationIntent(Context context, String m) {

        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    private void startGeofenceService() {

        Log.i(TAG, "location client service start");
        Intent intent = new Intent(this, LocationClientService.class);
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MULTIPLE_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //The External Storage Write Permission is granted to you... Continue your left job...
                startGeofenceService();
            } else {
                Log.d(TAG, "permission denied");
            }
        }
    }

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;

    private void setUpAlarm() {

        Log.i(TAG, "configured alarm");
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //Set first Alarm
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("code", 1);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.setTimeInMillis(calendar.getTimeInMillis() + 24 * 60 * 60 * 1000);
            //calendar.add(Calendar.DATE, 1);
        }

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);

        //Set second Alarm
        intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("code", 2);
        alarmIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.setTimeInMillis(calendar.getTimeInMillis() + 24 * 60 * 60 * 1000);
            //calendar.add(Calendar.DATE, 1);
        }

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    private void setupAlarmForServices() {

        Log.i(TAG, "configured alarm");
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //Set first Alarm
        Intent intent = new Intent(this, LocationClientService.class);
        intent.putExtra("code", 1);
        alarmIntent = PendingIntent.getService(this, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 15);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.setTimeInMillis(calendar.getTimeInMillis() + 24 * 60 * 60 * 1000);
            //calendar.add(Calendar.DATE, 1);
        }

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);

        //Set second Alarm
        intent = new Intent(this, LocationClientService.class);
        intent.putExtra("code", 2);
        alarmIntent = PendingIntent.getService(this, 1, intent, 0);

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 7);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.setTimeInMillis(calendar.getTimeInMillis() + 24 * 60 * 60 * 1000);
            //calendar.add(Calendar.DATE, 1);
        }

        alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }
}
