package com.melascuco.android.coordinatessender;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;


public class MainActivity extends Activity {

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void scheduleAlarm(View V) {

        Intent intentAlarm = new Intent(this, AlarmReciever.class);
        pendingIntent = PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT);

        // create the object
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                1000,//1500,//1 * 60 * 1000,
//                AlarmReciever.LOCATION_CHECK_INTERVAL,
//                //1500,//1 * 60 * 1000,
//                pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + AlarmReciever.LOCATION_CHECK_INTERVAL,//1500,//1 * 60 * 1000,
                AlarmReciever.LOCATION_CHECK_INTERVAL,
                //1500,//1 * 60 * 1000,
                pendingIntent);

        Toast.makeText(this, "Alarm Scheduled for intervals", Toast.LENGTH_LONG).show();
    }

    public void stopAlarm(View V) {
        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Stopped", Toast.LENGTH_LONG).show();
    }
}
