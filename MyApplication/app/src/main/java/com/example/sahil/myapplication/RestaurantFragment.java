package com.example.sahil.myapplication;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.sahil.myapplication.Utils.CalendarUtils;
import com.example.sahil.myapplication.Utils.DiningHallUtils;
import com.example.sahil.myapplication.Utils.Network;

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

        //TODO: Replace with Load Circle/Spinner


        listItems = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.restaurant_fragment, container, false); // false as third parameter?
        return populateListView(view, savedInstanceState);
    }


    private View populateListView(View view, Bundle savedInstanceState) {
        myListAdapter = new MyListAdapter(getContext(), R.id.mealListView, listItems);
        ListView mealListView = (ListView) view.findViewById(R.id.mealListView);
        mealListView.setAdapter(myListAdapter);


        //views first time creates before data for menus is retrieved
        if (savedInstanceState == null)
            refreshView(view);


        return view;
    }


    private boolean isToday() {
        int month = CalendarUtils.getMonth();
        int day = CalendarUtils.getDay();
        int year = CalendarUtils.getYear();
        return !(MainActivity.month_x != month || MainActivity.day_x != day || MainActivity.year_x != year);
    }

    private void updateListItems(Menu selectedMenu) {
        myListAdapter.clearMealPositions();

        if (selectedMenu.getMeals() != null) {

            ListItemParent dateItem = new ListItemParent(ListItemParent.dateHeader);
            int month = CalendarUtils.getMonth();
            int day = CalendarUtils.getDay();
            int year = CalendarUtils.getYear();
            boolean restaurantClosed = false;
            if (MainActivity.month_x != month || MainActivity.day_x != day || MainActivity.year_x != year) {
                listItems.add(dateItem);

                for (Meal currentMeal : selectedMenu.getMeals()) {
                    // for each Meal - make one header add it to a vector of custom parent type
                    // for each section - make a add it to a vector of custom parent type


                    ListItemParent mealItem = new ListItemParent(ListItemParent.mealHeader);
                    mealItem.setTitle(currentMeal.getName());
                    if (!currentMeal.getName().equals("Breakfast")) {
                        listItems.add(mealItem);
                        myListAdapter.addMealPosition(listItems.size() - 1);
                    }
                    SharedPreferences mPrefs = getActivity().getSharedPreferences("filters", 0);
                    boolean vegetarian = mPrefs.getBoolean("Vegan", false);
                    boolean vegan = mPrefs.getBoolean("Vegetarian", false);
                    for (Section mealSection : currentMeal.getSections()) {
                        if (mealSection.getFoodItems() != null) {
                            if (mealSection.getFoodItems().size() == 0) {
                                continue;
                            } else if (vegetarian) {

                                int vegetarianAmount = 0;
                                for (FoodItem foodItem : mealSection.getFoodItems()) {
                                    if (foodItem.isV() || foodItem.isVT())
                                        ++vegetarianAmount;
                                }
                                if (vegetarianAmount == 0)
                                    continue;


                            } else if (vegan) {
                                int veganAmount = 0;
                                for (FoodItem foodItem : mealSection.getFoodItems()) {
                                    if (foodItem.isV())
                                        ++veganAmount;
                                }
                                if (veganAmount == 0)
                                    continue;
                            }
                        } else
                            continue;

                        ListItemParent sectionItem = new ListItemParent(ListItemParent.sectionHeader);
                        if (mealSection.getName().trim().toLowerCase().contains("must register for access"))
                            sectionItem.setTitle("Allergen Awareness Zone");
                        else
                            sectionItem.setTitle(mealSection.getName());

                        listItems.add(sectionItem);
                        if (mealSection.getFoodItems() != null) {
                            for (FoodItem foodItem : mealSection.getFoodItems()) {
                                if (foodItem.getFoodName().trim().toLowerCase().contains("closed")) {
                                    restaurantClosed = true;
                                    break;
                                }
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


                                if (vegetarian) {
                                    if (foodItem.isVT() || foodItem.isV())
                                        listItems.add(food);
                                } else if (vegan) {
                                    if (foodItem.isV())
                                        listItems.add(food);
                                } else {
                                    listItems.add(food);
                                }
                            }
                        }
                        if (restaurantClosed)
                            break;
                    }
                    if (restaurantClosed)
                        break;
                }
                if (restaurantClosed)
                    listItems.clear();
            } else {
                for (Meal currentMeal : selectedMenu.getMeals()) {
                    if (currentMeal.getName().toLowerCase().contains(DiningHallUtils.getCurrentMealTime().name().toLowerCase())) {
                        for (Section mealSection : currentMeal.getSections()) {
                            SharedPreferences mPrefs = getActivity().getSharedPreferences("filters", 0);
                            boolean vegetarian = mPrefs.getBoolean("Vegetarian", false); //for filters
                            boolean vegan = mPrefs.getBoolean("Vegan", false);
                            if (mealSection.getFoodItems() != null) {
                                if (mealSection.getFoodItems().size() == 0) {
                                    continue;
                                } else if (vegetarian) {

                                    int vegetarianAmount = 0;
                                    for (FoodItem foodItem : mealSection.getFoodItems()) {
                                        if (foodItem.isV() || foodItem.isVT())
                                            ++vegetarianAmount;
                                    }
                                    if (vegetarianAmount == 0)
                                        continue;


                                } else if (vegan) {
                                    int veganAmount = 0;
                                    for (FoodItem foodItem : mealSection.getFoodItems()) {
                                        if (foodItem.isV())
                                            ++veganAmount;
                                    }
                                    if (veganAmount == 0)
                                        continue;
                                }
                            } else
                                continue;
                            ListItemParent sectionItem = new ListItemParent(ListItemParent.sectionHeader);
                            if (mealSection.getName().trim().toLowerCase().contains("must register for access"))
                                sectionItem.setTitle("Allergen Awareness Zone");
                            else
                                sectionItem.setTitle(mealSection.getName());
                            if(!listItems.contains(sectionItem))
                                listItems.add(sectionItem);
                            else
                                continue;

                            if (mealSection.getFoodItems() != null) {
                                for (FoodItem foodItem : mealSection.getFoodItems()) {
                                    if (foodItem.getFoodName().trim().toLowerCase().contains("closed")) {
                                        restaurantClosed = true;
                                        break;
                                    }
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


                                    if (vegetarian) {
                                        if (foodItem.isVT() || foodItem.isV())
                                            listItems.add(food);
                                    } else if (vegan) {
                                        if (foodItem.isV())
                                            listItems.add(food);
                                    } else {
                                        listItems.add(food);
                                    }
                                }
                            }
                            if (restaurantClosed)
                                break;
                        }
                        if (restaurantClosed)
                            break;
                    }

                }

            }


        }
    }


    /**
     * Auto Scroll if fragment created after initial fetch for data on date
     *
     * @param view
     */
    public void refreshView(View view) {

        if (!MainActivity.menus.isEmpty()) {
            MainActivity.loading = false;

            if (myListAdapter == null)
                return;

            listItems.clear();
            // TODO check that a menu for that diningHallID actually exists
            updateListItems(MainActivity.menus.get(diningHallID));
            myListAdapter.notifyDataSetChanged();

            if (view != null) {
                if (listItems.size() <= 1) {
                    ListView mealListView = (ListView) view.findViewById(R.id.mealListView);
                    mealListView.setVisibility(View.INVISIBLE);
                    TextView textView = (TextView) view.findViewById(R.id.closed_info);
                    textView.setText(R.string.closed_restaurant);
                    textView.setVisibility(View.VISIBLE);
                    Button button = (Button) view.findViewById(R.id.view_another_menu);
                    button.setText(R.string.view_another_menu);
                    button.setVisibility(View.VISIBLE);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DialogFragment newFragment = new DatePickerFragment();


                            newFragment.show(getActivity().getFragmentManager(), "MyDialog");
                        }
                    });


                } else {
                    ListView mealListView = (ListView) view.findViewById(R.id.mealListView);
                    mealListView.setVisibility(View.VISIBLE);
                    TextView textView = (TextView) view.findViewById(R.id.closed_info);
                    textView.setVisibility(View.INVISIBLE);
                    Button button = (Button) view.findViewById(R.id.view_another_menu);
                    button.setVisibility(View.INVISIBLE);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });
                }
            }


            //autoScroll(view);


        } else {
            if (view != null) {
                if(!MainActivity.loading) {
                    ListView mealListView = (ListView) view.findViewById(R.id.mealListView);
                    mealListView.setVisibility(View.INVISIBLE);
                    TextView textView = (TextView) view.findViewById(R.id.closed_info);
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(R.string.no_menu_available);

                    Button button = (Button) view.findViewById(R.id.view_another_menu);
                    if (!Network.isNetworkAvailable(getActivity().getApplicationContext()))
                        button.setText(R.string.no_internet_connection);
                    else
                        button.setText(R.string.view_another_menu);
                    button.setVisibility(View.VISIBLE);

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DialogFragment newFragment = new DatePickerFragment();


                            newFragment.show(getActivity().getFragmentManager(), "MyDialog");
                        }
                    });
                }


            }
        }


        if (view != null) {
            ListView mealListView = (ListView) view.findViewById(R.id.mealListView);
            if (!isToday()) {


                mealListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {

                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        Log.d("Aditya", "scroll listener is working " + myListAdapter.getMealPositions().size());
                        ListView mealListView = (ListView) view.findViewById(R.id.mealListView);
                        if (getUserVisibleHint()) {
                            if (myListAdapter.getMealPositions().size() >= 1 && listItems.size() > 1 && mealListView.getVisibility() == View.VISIBLE) {


                                Log.d("Aditya", "meal positions is working " + firstVisibleItem);

                                if (firstVisibleItem >= myListAdapter.getMealPositions().get(0) && firstVisibleItem < myListAdapter.getMealPositions().get(1)) {
                                    TextView textView = (TextView) getActivity().findViewById(R.id.mytitle);
                                    textView.setText("Lunch");
                                } else if (firstVisibleItem >= myListAdapter.getMealPositions().get(1)) {
                                    TextView textView = (TextView) getActivity().findViewById(R.id.mytitle);
                                    textView.setText("Dinner");
                                } else {
                                    TextView textView = (TextView) getActivity().findViewById(R.id.mytitle);
                                    textView.setText("Breakfast");
                                }
                            } else {
                                TextView titleView = (TextView) getActivity().findViewById(R.id.mytitle);
                                if (getView() != null) {
                                    TextView noMenuText = (TextView) getView().findViewById(R.id.closed_info);

                                    if (noMenuText.getText().toString().trim().toLowerCase().contains("no menu available"))
                                        titleView.setText("No Menu");
                                    else
                                        titleView.setText("Closed");


                                }
                            }
                        }
                    }
                });


            } else {
                //nullify any scroll listener
                mealListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {

                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {


                    }
                });

                //set text to current meal time
                TextView textView = (TextView) getActivity().findViewById(R.id.mytitle);
                textView.setText(DiningHallUtils.getCurrentMealTime().name());

            }
            mealListView.setSelection(0);
        }

    }

    /**
     * Auto Scroll if fragment created before initial fetch for data on date
     */
    public void refreshView() {


        if (!MainActivity.menus.isEmpty()) {

            if (myListAdapter == null)
                return;

            listItems.clear();
            // TODO check that a menu for that diningHallID actually exists
            updateListItems(MainActivity.menus.get(diningHallID));
            myListAdapter.notifyDataSetChanged();

            if (getView() != null) {
                if (listItems.size() <= 1) {
                    ListView mealListView = (ListView) getView().findViewById(R.id.mealListView);
                    mealListView.setVisibility(View.INVISIBLE);
                    TextView textView = (TextView) getView().findViewById(R.id.closed_info);
                    textView.setText(R.string.closed_restaurant);
                    textView.setVisibility(View.VISIBLE);
                    Button button = (Button) getView().findViewById(R.id.view_another_menu);
                    button.setText(R.string.view_another_menu);
                    button.setVisibility(View.VISIBLE);

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DialogFragment newFragment = new DatePickerFragment();


                            newFragment.show(getActivity().getFragmentManager(), "MyDialog");
                        }
                    });


                } else {
                    ListView mealListView = (ListView) getView().findViewById(R.id.mealListView);
                    mealListView.setVisibility(View.VISIBLE);
                    TextView textView = (TextView) getView().findViewById(R.id.closed_info);

                    textView.setVisibility(View.INVISIBLE);
                    Button button = (Button) getView().findViewById(R.id.view_another_menu);
                    button.setVisibility(View.INVISIBLE);

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });

                }
            }
            MainActivity.loading = false;
        } else {
            if (getView() != null) {
                if(!MainActivity.loading) {
                    ListView mealListView = (ListView) getView().findViewById(R.id.mealListView);
                    mealListView.setVisibility(View.INVISIBLE);
                    TextView textView = (TextView) getView().findViewById(R.id.closed_info);
                    textView.setText(R.string.no_menu_available);
                    textView.setVisibility(View.VISIBLE);

                    Button button = (Button) getView().findViewById(R.id.view_another_menu);

                    if (!Network.isNetworkAvailable(getActivity().getApplicationContext()))
                        button.setText(R.string.no_internet_connection);
                    else
                        button.setText(R.string.view_another_menu);
                    button.setVisibility(View.VISIBLE);

                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            DialogFragment newFragment = new DatePickerFragment();


                            newFragment.show(getActivity().getFragmentManager(), "MyDialog");
                        }
                    });
                }


            }
        }


        if (getView() != null) {
            ListView mealListView = (ListView) getView().findViewById(R.id.mealListView);
            Log.d("Aditya", "Visisbility " + mealListView.getVisibility());
            if (!isToday()) {
                mealListView.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {

                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                        Log.d("Aditya", "scroll listener is working " + myListAdapter.getMealPositions().size());
                        ListView mealListView = (ListView) getView().findViewById(R.id.mealListView);
                        if (getUserVisibleHint()) {
                            if (myListAdapter.getMealPositions().size() >= 1 && listItems.size() > 1 && mealListView.getVisibility() == View.VISIBLE) {


                                Log.d("Aditya", "meal positions is working " + firstVisibleItem);

                                if (firstVisibleItem >= myListAdapter.getMealPositions().get(0) && firstVisibleItem < myListAdapter.getMealPositions().get(1)) {
                                    TextView textView = (TextView) getActivity().findViewById(R.id.mytitle);
                                    textView.setText("Lunch");
                                } else if (firstVisibleItem >= myListAdapter.getMealPositions().get(1)) {
                                    TextView textView = (TextView) getActivity().findViewById(R.id.mytitle);
                                    textView.setText("Dinner");
                                } else {
                                    TextView textView = (TextView) getActivity().findViewById(R.id.mytitle);
                                    textView.setText("Breakfast");
                                }
                            } else {
                                TextView titleView = (TextView) getActivity().findViewById(R.id.mytitle);
                                if (getView() != null) {
                                    TextView noMenuText = (TextView) getView().findViewById(R.id.closed_info);

                                    if (noMenuText.getText().toString().trim().toLowerCase().contains("no menu available"))
                                        titleView.setText("No Menu");
                                    else
                                        titleView.setText("Closed");
                                }
                            }
                        }
                    }
                });
            } else {
                ListView mealListViewTwo = (ListView) getView().findViewById(R.id.mealListView);
                //nullify any scroll listener
                mealListViewTwo.setOnScrollListener(new AbsListView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(AbsListView view, int scrollState) {

                    }

                    @Override
                    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {


                    }
                });

                //set text to current meal time
                TextView textView = (TextView) getActivity().findViewById(R.id.mytitle);
                textView.setText(DiningHallUtils.getCurrentMealTime().name());

            }
            mealListView.setSelection(0);
        }
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (getView() != null) {
                ListView listView = (ListView) getView().findViewById(R.id.mealListView);
                if (listView.getVisibility() == View.INVISIBLE) {

                    TextView titleView = (TextView) getActivity().findViewById(R.id.mytitle);
                    TextView noMenuText = (TextView) getView().findViewById(R.id.closed_info);
                    Log.d("Aditya", "No menu: " + noMenuText.getText().toString());
                    if (noMenuText.getText().toString().trim().toLowerCase().contains("no menu available"))
                        titleView.setText("No Menu");
                    else
                        titleView.setText("Closed");
                } else {
                    if (myListAdapter.getMealPositions().size() >= 2 && listItems.size() > 2) {


                        Log.d("Aditya", "meal positions is working " + listView.getFirstVisiblePosition());
                        if (listView.getFirstVisiblePosition() >= myListAdapter.getMealPositions().get(0) && listView.getFirstVisiblePosition() < myListAdapter.getMealPositions().get(1)) {
                            TextView textView = (TextView) getActivity().findViewById(R.id.mytitle);
                            textView.setText("Lunch");
                        } else if (listView.getFirstVisiblePosition() >= myListAdapter.getMealPositions().get(1)) {
                            TextView textView = (TextView) getActivity().findViewById(R.id.mytitle);
                            textView.setText("Dinner");
                        } else {
                            TextView textView = (TextView) getActivity().findViewById(R.id.mytitle);
                            textView.setText("Breakfast");
                        }
                    }
                }

            }
        }
    }

}

