package com.example.sahil.myapplication;

import android.app.Application;
import android.content.Context;

import feast.FeastAPI;

/**
 * Created by Riley on 4/13/16.
 */
public class EatsApplication extends Application {

    public static Context applicationContext;

    public void onCreate()
    {
        super.onCreate();

        applicationContext = getApplicationContext();

        FeastAPI.sharedAPI.setContext(getApplicationContext());
    }
}
