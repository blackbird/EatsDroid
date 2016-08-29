package com.example.sahil.myapplication;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Set;

import feast.FoodItem;

public class FavoriteFragment extends Fragment {


    ArrayList<ListItemParent> listItems;
    MyListAdapter myListAdapter;



    public FavoriteFragment() {

    }

    private void initializeListItems() {
        Log.w("Favorite", "Inside initializeListItems");
        listItems = new ArrayList<ListItemParent>();
        ListItemParent mealItem = new ListItemParent(ListItemParent.mealHeader);
        mealItem.setTitle("Waiting for Favorites");
        listItems.add(mealItem);
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeListItems();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favorite, container, false);


        updateListItems(MainActivity.favoritesSet);
        return populateListView(view);
    }

    private View populateListView(View view) {
        setUpListView(view);
        return view;
    }

    private void setUpListView(View view) {
        myListAdapter = new MyListAdapter(getActivity(), R.id.favorite_listview, listItems);
        ListView favoriteListView= (ListView) view.findViewById(R.id.favorite_listview);
        favoriteListView.setAdapter(myListAdapter);
    }


    private void updateListItems(Set<String> favorites) {
        ListItemParent mealItem = new ListItemParent(ListItemParent.mealHeader);
        mealItem.setTitle("Your Favorites");
        listItems.add(mealItem);

        for(String favorite: favorites) {
            JSONObject favoriteObj = null;
            try {
                favoriteObj = new JSONObject(favorite);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            FoodItem foodItem = null;
            try {
                foodItem = new FoodItem(favoriteObj != null ? favoriteObj.getString("food_name") : null, favoriteObj != null ? favoriteObj.getString("food_identifier") : null);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            ListItemParent food = new ListItemParent(ListItemParent.foodHeader);
            food.setTitle(foodItem != null ? foodItem.getFoodName() : null);
            food.setFoodItem(foodItem);
            food.setFavorite(true);
            listItems.add(food);
        }
    }
}
