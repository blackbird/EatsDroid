package com.example.sahil.myapplication;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.sahil.myapplication.Utils.CalendarUtils;
import com.example.sahil.myapplication.Utils.DiningHallUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import feast.FoodItem;
import feast.Meal;
import feast.Menu;
import feast.Section;




/**
 * Created by Sahil on 4/8/16.
 */
public class RestaurantFragment extends Fragment {
//    RecyclerView recyclerView;
//    MealRecyclerViewAdapter mealRecyclerViewAdapter;

    String diningHallID;

    ArrayList<ListItemParent> listItems;
    MyListAdapter myListAdapter;



    public RestaurantFragment() {
        initializeListItems();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        diningHallID = getArguments().getString("DiningHallID");
        //Log.w("Sahil", "dining hall id is: " + diningHallID);
    }



    private void initializeListItems() {
        // create one meal
        Meal dummyMeal = new Meal();
        dummyMeal.setName("Waiting for Data");
        //TODO: Replace with Load Circle/Spinner


        listItems = new ArrayList<ListItemParent>();
        ListItemParent mealItem = new ListItemParent(ListItemParent.mealHeader);
        mealItem.setTitle(dummyMeal.getName());
        listItems.add(mealItem);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.restaurant_fragment, container, false); // false as third parameter?
        return populateListView(view, savedInstanceState);
    }




    private View populateListView(View view, Bundle savedInstanceState) {
        myListAdapter = new MyListAdapter(getContext(), R.id.mealListView, listItems);
        ListView mealListView= (ListView) view.findViewById(R.id.mealListView);
        mealListView.setAdapter(myListAdapter);






        //views first time creates before data for menus is retrieved
        if(savedInstanceState==null)
            if(!MainActivity.menus.isEmpty())
                refreshView(view);
        return view;
    }




    private void updateListItems(Menu selectedMenu) {
        if(selectedMenu.getMeals()!=null)
            for(Meal currentMeal: selectedMenu.getMeals()) {
                // for each Meal - make one header add it to a vector of custom parent type
                // for each section - make a add it to a vector of custom parent type

                ListItemParent mealItem = new ListItemParent(ListItemParent.mealHeader);
                mealItem.setTitle(currentMeal.getName());
                listItems.add(mealItem);

                for(Section mealSection: currentMeal.getSections()) {
                    ListItemParent sectionItem = new ListItemParent(ListItemParent.sectionHeader);
                    sectionItem.setTitle(mealSection.getName());
                    listItems.add(sectionItem);
                    if(mealSection.getFoodItems() != null) {
                        for (FoodItem foodItem : mealSection.getFoodItems()) {
                            ListItemParent food = new ListItemParent(ListItemParent.foodHeader);
                            food.setTitle(foodItem.getFoodName());
                            food.setFoodItem(foodItem);
                            JSONObject favoritesObj = new JSONObject();

                            try {
                                favoritesObj.put("food_identifier", foodItem.getFoodIdentifier());
                                favoritesObj.put("food_name", foodItem.getFoodName());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            food.setFavorite(MainActivity.favoritesSet.contains(favoritesObj.toString()));
                            listItems.add(food);
                        }
                    }
                }
            }
    }


    public void autoScroll(View view) {
        final ListView mealListView = (ListView) view.findViewById(R.id.mealListView);
        if(DiningHallUtils.getCurrentMealTime()== DiningHallUtils.MealTime.BREAKFAST) {
            mealListView.setSelection(0);
        } else if(DiningHallUtils.getCurrentMealTime()== DiningHallUtils.MealTime.LUNCH || DiningHallUtils.getCurrentMealTime()==DiningHallUtils.MealTime.BRUNCH) {
            Handler handler = new Handler();

            final Runnable r = new Runnable() {
                public void run() {
                    if(myListAdapter.getMealPositions().size()>1) {
                        ListView mealListView = (ListView) getView().findViewById(R.id.mealListView);
                        mealListView.setSelection(myListAdapter.getMealPositions().get(1));
                    }
                }
            };

            handler.postDelayed(r, 1000);
        } else if (DiningHallUtils.getCurrentMealTime() == DiningHallUtils.MealTime.DINNER) {
            Handler handler = new Handler();

            final Runnable r = new Runnable() {
                public void run() {
                    if(myListAdapter.getMealPositions().size()>1) {
                        ListView mealListView = (ListView) getView().findViewById(R.id.mealListView);
                        mealListView.setSelection(myListAdapter.getMealPositions().get(myListAdapter.getMealPositions().size()-1));
                    }
                }
            };

            handler.postDelayed(r, 1000);
        }
    }


    /**
     * Auto Scroll if fragment created after initial fetch for data on date
     * @param view
     */
    public void refreshView(View view) {
        if(!MainActivity.menus.isEmpty()) {
            if(myListAdapter==null)
                return;

            listItems.clear();
            // TODO check that a menu for that diningHallID actually exists
            updateListItems(MainActivity.menus.get(diningHallID));
            myListAdapter.notifyDataSetChanged();




            autoScroll(view);


        } else {
            Log.w("Sahil", "Menus was empty");
            Toast toast = Toast.makeText(EatsApplication.applicationContext, "Menu for " + MainActivity.restaurants.get(diningHallID) + " is empty." , Toast.LENGTH_LONG);
            toast.show();
        }
    }

    /**
     * Auto Scroll if fragment created before initial fetch for data on date
     */
    public void refreshView() {
        if(!MainActivity.menus.isEmpty()) {
            if(myListAdapter==null)
                return;

            listItems.clear();
            // TODO check that a menu for that diningHallID actually exists
            updateListItems(MainActivity.menus.get(diningHallID));
            myListAdapter.notifyDataSetChanged();



            if(getView()!=null){
                if(MainActivity.day_x == CalendarUtils.getDay() && MainActivity.month_x == CalendarUtils.getMonth())
                    autoScroll(getView());
                else {
                    ListView mealListView = (ListView) getView().findViewById(R.id.mealListView);
                    mealListView.setSelection(0);
                }
            }

        } else {
            Log.w("Sahil", "Menus was empty");
            Toast toast = Toast.makeText(EatsApplication.applicationContext, "Menu for " + MainActivity.restaurants.get(diningHallID) + " is empty." , Toast.LENGTH_LONG);
            toast.show();
        }
    }
}

