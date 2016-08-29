package com.example.sahil.myapplication;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.example.sahil.myapplication.Utils.Alarm;
import com.example.sahil.myapplication.Utils.CalendarUtils;
import com.example.sahil.myapplication.Utils.Favorites;
import com.example.sahil.myapplication.Utils.Network;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import feast.FeastAPI;
import feast.FoodItem;
import feast.Meal;
import feast.Menu;
import feast.Section;

/**
 * Created by adityaaggarwal on 8/7/16.
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction()!=null)
            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                Alarm.setAlarmManager(context);
                return;
            }

        Log.d("Aditya", "alarm triggered");
        final Set<String> favoritesSet = new HashSet<>();
        try {
            JSONArray favorites = new JSONArray(Favorites.getFavorites(context));
            for(int i=0; i< favorites.length(); ++i) {
                JSONObject favorite = (JSONObject) favorites.get(i);
                favoritesSet.add(favorite.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        //For testing notifications
        /*NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setContentTitle("USC Eats")
                .setContentText("test data")
                .setAutoCancel(true)
                .setShowWhen(true)
                .setWhen(System.currentTimeMillis())
                .setLights(context.getResources().getColor(R.color.colorAccent), 100, 1900)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).setSmallIcon(R.drawable.eats)
                .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE);
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(1, mBuilder.build());*/


        final Context finalContext = context;
        FeastAPI.sharedAPI.fetchMenusForDateWithCompletion(formatDate(), new FeastAPI.MenusCallback() {
            @Override
            public void fetchedMenus(Map<String, Menu> menus, VolleyError error) {
                if(error == null) {
                    String foods = "";
                    boolean foodItemServed = false;
                    for (Map.Entry<String, Menu> entry : menus.entrySet()) {
                        String restaurantTag = entry.getKey();
                        foods += restaurantTag + " is serving ";
                        if(entry.getValue().getMeals()!=null)
                        for (Meal meal : entry.getValue().getMeals()) {
                            if(meal.getSections()!=null)
                            for (Section section : meal.getSections()) {
                                if(section.getFoodItems() != null)
                                for (FoodItem foodItem : section.getFoodItems()) {
                                    JSONObject foodObj = new JSONObject();
                                    try {
                                        foodObj.put("food_name", foodItem.getFoodName());
                                        foodObj.put("food_identifier", foodItem.getFoodIdentifier());
                                        if (favoritesSet.contains(foodObj.toString())) {
                                            foodItemServed = true;
                                            String foodName = foodObj.getString("food_name").trim().replace("(V)", "").replace("(VT)", "");
                                            foods += foodName;
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            }
                            foods +=". ";
                        }
                    }
                    if(foodItemServed) {
                        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(finalContext)
                                .setContentTitle("USC Eats")
                                .setContentText(foods)
                                .setAutoCancel(true)
                                .setShowWhen(true)
                                .setWhen(System.currentTimeMillis())
                                .setLights(finalContext.getResources().getColor(R.color.colorAccent), 100, 1900)
                                .setPriority(NotificationCompat.PRIORITY_DEFAULT).setSmallIcon(R.drawable.eats)
                                .setDefaults(NotificationCompat.DEFAULT_SOUND | NotificationCompat.DEFAULT_VIBRATE);
                        Intent resultIntent = new Intent(finalContext, MainActivity.class);
                        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        PendingIntent resultPendingIntent = PendingIntent.getActivity(finalContext, 0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                        mBuilder.setContentIntent(resultPendingIntent);
                        NotificationManager mNotifyMgr = (NotificationManager) finalContext.getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotifyMgr.notify(1, mBuilder.build());
                    }
                } else
                {
                    Toast toast = Toast.makeText(EatsApplication.applicationContext, "Failed to fetch menu data. ", Toast.LENGTH_LONG);
                    toast.show();

                    Log.w("Sahil", "Error getting data: " + error.toString());
                }
            }
        });

        if(Network.isNetworkAvailable(context)) {
            FeastAPI.sharedAPI.updateFavoritesWithCompletion(new FeastAPI.RequestCallback() {
                @Override
                public void requestFinishedWithSuccess(Boolean success, VolleyError error) {
                    if(!success) {
                        Log.d("Aditya", "Error:" + error.getMessage());
                        error.printStackTrace();
                        Toast toast = Toast.makeText(EatsApplication.applicationContext, "Failed to update favorites. ", Toast.LENGTH_LONG);
                        toast.show();
                    }
                }
            });
        }


    }
    public Date formatDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = null;
        try
        {
            date = dateFormat.parse(CalendarUtils.getYear() + "/" + CalendarUtils.getMonth() + "/" + CalendarUtils.getDay());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return date;
    }
}


