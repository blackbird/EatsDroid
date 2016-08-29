package feast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by adityaaggarwal on 3/15/16.
 */
public class Menu implements Serializable{
    ArrayList <Meal> meals=new ArrayList<>();
    String restaurantID;
    Date date;

    public Menu(JSONObject jsonObject){
        try {
            restaurantID = jsonObject.getString("restaurant_id");
            JSONArray mealsJSON= jsonObject.getJSONArray("meals");

            for(int i=0; i<mealsJSON.length(); i++){
                meals.add(new Meal(mealsJSON.getJSONObject(i)));
            }
            String dateString=jsonObject.getString("_updated");
            date= new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US).parse(dateString);
        } catch (JSONException|ParseException e) {
            e.printStackTrace();
        }


    }

    public String getRestaurantID(){
        return restaurantID;
    }
    public Date getDate(){
        return date;
    }
    public ArrayList<Meal> getMeals(){
        return meals;
    }
    public Meal getMeal(String mealtime){
        for(Meal meal:meals){
            if(meal.getName().equals(mealtime))
                return meal;
        }
        return null;
    }

}
