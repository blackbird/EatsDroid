package com.example.sahil.myapplication;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by adityaaggarwal on 9/6/16.
 */
public class Filter {
    private String name;
    private boolean isOn;
    Filter(String name, boolean isOn){
        this.name = name;
        this.isOn = isOn;
    }

    public String getName() {
        return name;
    }

    public boolean isOn() {return isOn;}
    public void switchFilter(Context context, boolean isChecked) {
        isOn = isChecked;
        SharedPreferences mPrefs = context.getSharedPreferences("filters", 0);
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putBoolean(name, isOn);
        editor.commit();
    }


}
