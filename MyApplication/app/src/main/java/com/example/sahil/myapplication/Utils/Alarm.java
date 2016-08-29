package com.example.sahil.myapplication.Utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.sahil.myapplication.AlarmReceiver;

import java.util.Calendar;

/**
 * Created by adityaaggarwal on 8/7/16.
 */
public class Alarm {
    public static void setAlarmManager(Context context) {
        /**
         * set alarm notifications to trigger push notifications when food items of choice being served
         */
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(context.getApplicationContext(), AlarmReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);

        Calendar alarmStartTime = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        alarmStartTime.set(Calendar.HOUR_OF_DAY, 7);
        alarmStartTime.set(Calendar.MINUTE, 00);
        alarmStartTime.set(Calendar.SECOND, 0);
        if (now.after(alarmStartTime)) {
            Log.d("Aditya","Added a day");
            alarmStartTime.add(Calendar.DATE, 1);
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);


        PendingIntent pendingIntentLunch = PendingIntent.getBroadcast(context.getApplicationContext(), 1, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntentLunch);

        alarmStartTime.set(Calendar.HOUR_OF_DAY, 11);
        alarmStartTime.set(Calendar.MINUTE, 00);
        alarmStartTime.set(Calendar.SECOND, 0);
        if (now.after(alarmStartTime)) {
            Log.d("Aditya","Added a day");
            alarmStartTime.add(Calendar.DATE, 1);
        }


        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntentLunch);



        PendingIntent pendingIntentDinner = PendingIntent.getBroadcast(context.getApplicationContext(), 2, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntentDinner);

        alarmStartTime.set(Calendar.HOUR_OF_DAY, 16);
        alarmStartTime.set(Calendar.MINUTE, 30);
        alarmStartTime.set(Calendar.SECOND, 0);
        if (now.after(alarmStartTime)) {
            Log.d("Aditya","Added a day");
            alarmStartTime.add(Calendar.DATE, 1);
        }
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntentDinner);
    }

}
