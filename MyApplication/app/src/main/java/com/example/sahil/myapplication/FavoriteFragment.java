package com.example.sahil.myapplication;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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
        ImageView blackbird = (ImageView)view.findViewById(R.id.blackbird);

        blackbird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return view;
    }

    private void setUpListView(View view) {
        myListAdapter = new MyListAdapter(getActivity(), R.id.favorite_listview, listItems);
        ListView favoriteListView= (ListView) view.findViewById(R.id.favorite_listview);
        favoriteListView.setAdapter(myListAdapter);




        //for feedback
        ArrayList<String> emails= new ArrayList<>();
        emails.add("erikruss@usc.edu");
        emails.add("erika.chesley@usc.edu");

        ArrayList<RestaurantFeedback> feedbacks= new ArrayList<>();

        feedbacks.add(new RestaurantFeedback("Cafe 84", emails));
        feedbacks.add(new RestaurantFeedback("EVK", emails));
        feedbacks.add(new RestaurantFeedback("Parkside", emails));


        ArrayAdapter<RestaurantFeedback> adapter = new ArrayAdapter<RestaurantFeedback>(getActivity().getApplicationContext(), R.layout.feedback_item_row, feedbacks) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                    LayoutInflater inflater = (LayoutInflater) getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    if(convertView==null)
                        convertView = inflater.inflate(R.layout.feedback_item_row, null);
                    TextView textView = (TextView)convertView.findViewById(R.id.feedback_item_text);
                    textView.setText(getItem(position).getName());
                return convertView;
            }
        };

        ListView feedbackListView = (ListView) view.findViewById(R.id.feedback_listview);
        feedbackListView.setAdapter(adapter);
        feedbackListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RestaurantFeedback feedback = (RestaurantFeedback)parent.getAdapter().getItem(position);
                Intent shareIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse(feedback.getMailtoEmails()));
                shareIntent.putExtra(
                        Intent.EXTRA_SUBJECT,
                        feedback.getName() +" Feedback"
                );
                startActivity(shareIntent);
            }
        });



    }


    private void updateListItems(Set<String> favorites) {


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
