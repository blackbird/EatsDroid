package com.example.sahil.myapplication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

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
