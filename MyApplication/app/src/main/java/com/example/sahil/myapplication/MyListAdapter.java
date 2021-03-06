package com.example.sahil.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import feast.FeastAPI;
import feast.FoodItem;
import it.dex.movingimageviewlib.DexMovingImageView;

/**
 * Created by Sahil on 4/11/16.
 */
public class MyListAdapter extends ArrayAdapter<ListItemParent> {
    private ArrayList<ListItemParent> objects;
    private Context context;
    private  ArrayList<Integer> mealPositions = new ArrayList<>();


    // be sure to check that you overloaded the right constructor
    public MyListAdapter(Context context, int resource, ArrayList<ListItemParent> objects) {
        super(context, resource, objects);

        this.objects = objects;
        this.context = context;
        mealPositions.clear();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // assign the view we are converting to a local variable
        View view = convertView;

        final ListItemParent currentItem = objects.get(position);
        int itemViewType = -1;
        itemViewType = getViewItemType(currentItem);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);






            if (itemViewType == ListItemParent.mealHeader) {

                view = inflater.inflate(R.layout.meal_header_row, null);
                TextView mealSeparator = (TextView) view.findViewById(R.id.mealSeparator);
                String formattedMealSeparator = "-" + currentItem.getTitle() + "-";
                mealSeparator.setText(formattedMealSeparator);
            } else if (itemViewType == ListItemParent.sectionHeader)

            {

                if(view==null)
                    view = inflater.inflate(R.layout.section_item_row, null);
                else {
                    TextView textView = (TextView)view.findViewById(R.id.sectionSeparator);
                    if(textView!=null) {
                        if (!(textView.getText().toString().trim().toLowerCase().contains(currentItem.getTitle().trim().toLowerCase()) || currentItem.getTitle().toLowerCase().trim().contains(textView.getText().toString().trim().toLowerCase()))) {
                            view = inflater.inflate(R.layout.section_item_row, null);
                        } else {
                            return view;
                        }
                    } else {
                        view = inflater.inflate(R.layout.section_item_row, null);
                    }

                }
                TextView sectionSeparator = (TextView) view.findViewById(R.id.sectionSeparator);
                sectionSeparator.setText(currentItem.getTitle());
                DexMovingImageView movingImageView = (DexMovingImageView)view.findViewById(R.id.section_layout);
                movingImageView.setValuesGenerator(new CustomValuesGenerator(movingImageView.getParameters()));
                setBackgroundHeader(currentItem.getTitle(), movingImageView);
            }  else if(itemViewType == ListItemParent.dateHeader) {
                view = inflater.inflate(R.layout.date_header_row, null);
                TextView dateHeader = (TextView) view.findViewById(R.id.headerdate);
                dateHeader.setText(MainActivity.formattedFullMonthDate());
            }else if(itemViewType == ListItemParent.foodHeader) {
                view = inflater.inflate(R.layout.food_item_row, null);
                TextView foodItemName = (TextView) view.findViewById(R.id.food_item_text);
                foodItemName.setText(currentItem.getTitle());
                SpannableString ss = new SpannableString(currentItem.getTitle() + "          ");
                if (position % 2 == 0) {

                    view.setBackgroundColor(Color.WHITE);
                } else {
                    view.setBackgroundColor(getContext().getResources().getColor(R.color.colorRowSecondary));

                }


                if (currentItem.getFoodItem().isV()) {
                    Drawable drawable = getContext().getResources().getDrawable(R.drawable.iconvegan);
                    if (drawable != null)
                        drawable.setBounds(0, 0, 30, 30);
                    ImageSpan imagespan = new ImageSpan(drawable);
                    ss.setSpan(imagespan, currentItem.getTitle().length() + 2, currentItem.getTitle().length() + 5, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                    if (currentItem.getFoodItem().isVT()) {
                        drawable = getContext().getResources().getDrawable(R.drawable.iconvegetarian);
                        if (drawable != null)
                            drawable.setBounds(0, 0, 30, 30);


                        imagespan = new ImageSpan(drawable);


                        ss.setSpan(imagespan, currentItem.getTitle().length() + 7, currentItem.getTitle().length() + 10, Spanned.SPAN_INCLUSIVE_INCLUSIVE);


                    }

                } else if (currentItem.getFoodItem().isVT()) {
                    Drawable drawable = getContext().getResources().getDrawable(R.drawable.iconvegetarian);
                    if (drawable != null)
                        drawable.setBounds(0, 0, 30, 30);


                    ImageSpan imagespan = new ImageSpan(drawable);


                    ss.setSpan(imagespan, currentItem.getTitle().length() + 2, currentItem.getTitle().length() + 5, Spanned.SPAN_INCLUSIVE_INCLUSIVE);


                }
                foodItemName.setText(ss);


                if (Constants.FAVORITES_SWITCH) {
                    final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
                    checkBox.setChecked(currentItem.isFavorite());


                    final FoodItem foodItem = currentItem.getFoodItem();

                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            //update for view
                            checkBox.setChecked(isChecked);

                            //update for cache

                            JSONObject foodObj = new JSONObject();

                            try {
                                foodObj.put("food_name", foodItem.getFoodName());
                                foodObj.put("food_identifier", foodItem.getFoodIdentifier());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (isChecked)
                                MainActivity.favoritesSet.add(foodObj.toString());
                            else
                                MainActivity.favoritesSet.remove(foodObj.toString());


                            FeastAPI.sharedAPI.setContext(getContext());
                            //update for persistence and network
                            FeastAPI.sharedAPI.updateFavoritesWithCompletion(foodItem, new FeastAPI.RequestCallback() {
                                @Override
                                public void requestFinishedWithSuccess(Boolean success, VolleyError error) {
                                    if (!success) {
                                        Log.d("Aditya", "Error:" + error.getMessage());
                                        error.printStackTrace();
                                        Toast toast = Toast.makeText(EatsApplication.applicationContext, "Failed to update favorites. ", Toast.LENGTH_LONG);
                                        toast.show();
                                    }
                                }
                            }, isChecked);


                        }


                    });

                }
            }

        return view;
    }


    public void setBackgroundHeader(String name, ImageView backgroundHeaderView) {
        if (backgroundHeaderView != null) {
            name = name.trim().toLowerCase();
            if (name.contains("americana")) {
                backgroundHeaderView.setImageResource(R.drawable.americana);
            } else if (name.contains("bistro") || name.contains("expo station")) {
                backgroundHeaderView.setImageResource(R.drawable.bistro);
            } else if (name.contains("bread")) {
                backgroundHeaderView.setImageResource(R.drawable.bread);
            } else if (name.contains("eurasia")) {
                backgroundHeaderView.setImageResource(R.drawable.eurasia);
            } else if (name.contains("deli")) {
                backgroundHeaderView.setImageResource(R.drawable.deli);
            } else if (name.contains("fruit") || name.contains("fresh from the") ) {
                backgroundHeaderView.setImageResource(R.drawable.fruit);
            } else if (name.contains("grainery")) {
                backgroundHeaderView.setImageResource(R.drawable.grainery);
            } else if (name.contains("grill")) {
                backgroundHeaderView.setImageResource(R.drawable.grill);
            } else if (name.contains("line") && name.contains("hot")) {
                backgroundHeaderView.setImageResource(R.drawable.hotline);
            } else if (name.contains("mongolian")) {
                backgroundHeaderView.setImageResource(R.drawable.mongolian);
            } else if (name.contains("pastries")) {
                backgroundHeaderView.setImageResource(R.drawable.pastries);
            } else if (name.contains("pie") || name.contains("slice of pi")) {
                backgroundHeaderView.setImageResource(R.drawable.pie);
            } else if (name.contains("pizza")) {
                backgroundHeaderView.setImageResource(R.drawable.pizza);
            } else if (name.contains("salad")) {
                backgroundHeaderView.setImageResource(R.drawable.salad);
            } else if (name.contains("soup")) {
                backgroundHeaderView.setImageResource(R.drawable.soup);
            } else if (name.contains("treats")) {
                backgroundHeaderView.setImageResource(R.drawable.treats);
            } else if(name.contains("allerg")) {
                backgroundHeaderView.setImageResource(R.drawable.allergy);
            } else {
                backgroundHeaderView.setImageResource(R.drawable.entree);
            }
        }
    }

    public ArrayList<Integer> getMealPositions() {
        return mealPositions;
    }

    public void clearMealPositions() {
        mealPositions.clear();
    }

    public void addMealPosition(int position) {
        mealPositions.add(position);
    }




    /**
     * get food item type
     */
    private int getViewItemType(ListItemParent currentItem) {
        if(currentItem.getType() == ListItemParent.mealHeader) {
            return ListItemParent.mealHeader;
        } else if(currentItem.getType() == ListItemParent.sectionHeader) {
            return ListItemParent.sectionHeader;
        } else if(currentItem.getType()==ListItemParent.foodHeader) {
            return ListItemParent.foodHeader;
        } else if(currentItem.getType() == ListItemParent.dateHeader) {
            return ListItemParent.dateHeader;
        } else {
            return -1;
        }
    }




}
