package com.example.sahil.myapplication;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

/**
 * Created by adityaaggarwal on 8/7/16.
 */
public class AlarmService extends Service
{
    private static BroadcastReceiver m_ScreenOffReceiver;

    @Override
    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    @Override
    public void onCreate()
    {
        registerScreenOffReceiver();
    }

    @Override
    public void onDestroy()
    {
        unregisterReceiver(m_ScreenOffReceiver);
        m_ScreenOffReceiver = null;
    }

    private void registerScreenOffReceiver()
    {
        m_ScreenOffReceiver = new AlarmReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(m_ScreenOffReceiver, filter);
    }
}
