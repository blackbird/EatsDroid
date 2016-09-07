package com.example.sahil.myapplication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import com.amplitude.api.Amplitude;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by adityaaggarwal on 9/5/16.
 */
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        int year = MainActivity.year_x;
        int month = MainActivity.month_x - 1;
        int day = MainActivity.day_x;
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("year",year);
            jsonObject.put("month", month+1);
            jsonObject.put("day", day);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Amplitude.getInstance().logEvent("Date Chosen on Calendar", jsonObject);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(),R.style.DialogTheme, this, year, month, day);
//            DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
//            pickerDialog.getDatePicker().setMaxDate(maxDate);
//            pickerDialog.getDatePicker().setMinDate(minDate); // System.currentTimeMillis() - 1000
//            return pickerDialog;
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user

        MainActivity.year_x = year;
        MainActivity.month_x = month + 1;
        MainActivity.day_x = day;

        MainActivity ma = (MainActivity)getActivity();
        ma.fetchDataForDate(MainActivity.formatDate());

        Log.w("Sahil", year + "-" + month + "-" + day);


    }


}
