package com.example.sahil.myapplication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.sahil.myapplication.Utils.Alarm;
import com.example.sahil.myapplication.Utils.CalendarUtils;
import com.example.sahil.myapplication.Utils.Favorites;
import com.example.sahil.myapplication.Utils.Installation;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import butterknife.Bind;
import feast.FeastAPI;


public class MainActivity extends AppCompatActivity {

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    @Bind(R.id.view_pager)
    ViewPager mViewPager;


    public static int day_x = -1;
    public static int month_x = -1;
    public static int year_x = -1;

    public static TreeMap<String, String> restaurants = new TreeMap<>();
    public static Map<String, feast.Menu> menus = new HashMap<>();
    public static Set<String> favoritesSet = new HashSet<>();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    //private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(savedInstanceState==null)
            setContentView(R.layout.activity_login);


        //TODO: execute as aysnchronous task to show splash page
        /**
         * registering user for favoriting
         */
        File installation = new File(getApplicationContext().getFilesDir(), Installation.INSTALLATION);
        if (!installation.exists()) {
            //TODO: Network check because this is first load of data
            Installation.id(getApplicationContext());
            FeastAPI.sharedAPI.registerUser(new FeastAPI.CreateUserCallback() {
                @Override
                public void registeredUser(JSONObject response, VolleyError error) {
                    if (error == null) {

                        JSONObject object = new JSONObject();
                        try {
                            object.put("_id", response.getString("_id"));
                            object.put("_etag", response.getString("_etag"));
                            object.put("installation_id", Installation.id(getApplicationContext()));
                            File installation = new File(getApplicationContext().getFilesDir(), Installation.INSTALLATION);
                            Installation.rewriteInstallationFile(installation, object.toString());
                            loadFavorites();
                            loadRestaurants();
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                            Log.w("Sahil", "Error getting data: " + e.getMessage());
                            Toast toast = Toast.makeText(EatsApplication.applicationContext, "Failed to register user. ", Toast.LENGTH_LONG);
                            toast.show();
                        }


                    } else {
                        Toast toast = Toast.makeText(EatsApplication.applicationContext, "Failed to register user. ", Toast.LENGTH_LONG);
                        toast.show();


                        Log.w("Sahil", "Error getting data: " + error.toString());
                    }
                }
            });


            Alarm.setAlarmManager(getApplicationContext());
            Intent service = new Intent(getApplicationContext(), AlarmService.class);
            if (getApplicationContext().startService(service) != null)
                getApplicationContext().startService(service);
            else
                Log.d("Aditya", "not null");
        } else {
            if(savedInstanceState==null) {
                loadFavorites();
                loadRestaurants();
            } else {
                setContentView(R.layout.activity_main);
                // Create the adapter that will return a fragment for each of the three
                // primary sections of the activity.
                mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

                // Set up the ViewPager with the sections adapter.
                mViewPager = (ViewPager) findViewById(R.id.view_pager);
                if (mViewPager != null) {
                    mViewPager.setAdapter(mSectionsPagerAdapter);
                }


                TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
                if (tabLayout != null) {
                    tabLayout.setupWithViewPager(mViewPager);
                }
                Log.w("Sahil", "OnCreate MainActivity");


                fetchDataForDate(formatDate()); //fetch data
            }
        }
    }

    /**
     * Load list of favorites
     */
    public void loadFavorites() {
        try {
            JSONArray favorites = new JSONArray(Favorites.getFavorites(getApplicationContext()));
            for (int i = 0; i < favorites.length(); ++i) {
                JSONObject favorite = (JSONObject) favorites.get(i);
                favoritesSet.add(favorite.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Load list of restaurants
     */
    public void loadRestaurants() {

        FeastAPI.sharedAPI.setContext(getApplicationContext());
        FeastAPI.sharedAPI.fetchRestaurantsWithCompletion(new FeastAPI.RestaurantsCallback() {
            @Override
            public void fetchedRestaurants(TreeMap<String, String> restaurants, VolleyError error) {
                if (error == null) {

                    MainActivity.restaurants = restaurants;
                    loadActivity();


                } else {
                    Toast toast = Toast.makeText(EatsApplication.applicationContext, "Failed to fetch restaurants.", Toast.LENGTH_LONG);
                    toast.show();


                    Log.w("Sahil", "Error getting data: " + error.toString());
                }
            }
        });
    }

    public void loadActivity() {
        //this.getWindow().setFlags(WindowManager.LayoutParams., WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        LayoutInflater inflator = getLayoutInflater();
        View view = inflator.inflate(R.layout.activity_main, null, false);
        view.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));
        setContentView(view);


        //working with dates
        if (year_x == -1 || month_x == -1 || day_x == -1) {
            year_x = CalendarUtils.getYear();
            month_x = CalendarUtils.getMonth();
            day_x = CalendarUtils.getDay();
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        if (mViewPager != null) {
            mViewPager.setAdapter(mSectionsPagerAdapter);
        }


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        if (tabLayout != null) {
            tabLayout.setupWithViewPager(mViewPager);
        }
        Log.w("Sahil", "OnCreate MainActivity");


        fetchDataForDate(formatDate()); //fetch data
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), FavoriteActivity.class);
            startActivity(intent);
            overridePendingTransition(R.animator.slide_in_right, R.animator.slide_out_left);
        } else if (id == R.id.action_calendar) {
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getFragmentManager(), "MyDialog");
        }

        return super.onOptionsItemSelected(item);
    }

    public Date formatDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = null;
        try {
            date = dateFormat.parse(year_x + "/" + month_x + "/" + day_x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * Fetch Menus based on the date
     *
     * @param date The date selected on the calendar.
     */
    private void fetchDataForDate(Date date) {
        Log.w("Sahil", "fetch menus for this date: " + date);


        FeastAPI.sharedAPI.setContext(this);
        FeastAPI.sharedAPI.fetchMenusForDateWithCompletion(date, new FeastAPI.MenusCallback() {
            @Override
            public void fetchedMenus(Map<String, feast.Menu> menus, VolleyError error) {
                if (error == null) {
                    MainActivity.menus = menus;
                    if (getSupportFragmentManager().getFragments() != null) {
                        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                            if (fragment instanceof RestaurantFragment)
                                ((RestaurantFragment) fragment).refreshView();
                        }
                    }

                } else {
                    Toast toast = Toast.makeText(EatsApplication.applicationContext, "Failed to fetch menu data. ", Toast.LENGTH_LONG);
                    toast.show();

                    Log.w("Sahil", "Error getting data: " + error.toString());
                }
            }
        });

    }








    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int year = year_x;
            int month = month_x - 1;
            int day = day_x;

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
//            DatePickerDialog pickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
//            pickerDialog.getDatePicker().setMaxDate(maxDate);
//            pickerDialog.getDatePicker().setMinDate(minDate); // System.currentTimeMillis() - 1000
//            return pickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user

            year_x = year;
            month_x = month + 1;
            day_x = day;


            fetchDataForDate(formatDate());

            Log.w("Sahil", year + "-" + month + "-" + day);


        }


    }


    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        Object[] titles = restaurants.keySet().toArray();

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            RestaurantFragment restaurantFragment = new RestaurantFragment();
            Bundle args = new Bundle();
//
//            // may want to put something about what the date is
//
//            restaurantFragment.setArguments(args);
//            Log.w("Sahil", "Log tags working");
//            return PlaceholderFragment.newInstance(position + 1);

            args.putString("DiningHallID", titles[position].toString());

            restaurantFragment.setArguments(args);

            return restaurantFragment;
        }

        @Override
        public int getCount() {
            return restaurants.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return restaurants.get(titles[position].toString());
        }
    }
}
