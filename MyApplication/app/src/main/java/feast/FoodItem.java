package feast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by adityaaggarwal on 3/15/16.
 */
public class FoodItem implements Serializable {
    private String foodName;
    private String foodIdentifier;
    private boolean isV;
    private boolean isVT;

    FoodItem(JSONObject jsonObject){
        try {
            foodIdentifier = jsonObject.getString("food_identifier");
            foodIdentifier = foodIdentifier.trim();
            String tempFoodName=jsonObject.getString("food_name");
            tempFoodName=tempFoodName.trim();

            isV=tempFoodName.contains("(V)");
            isVT=tempFoodName.contains("(VT)");
            if(isVT){
                tempFoodName=tempFoodName.replace("(VT)","");
                tempFoodName=tempFoodName.trim();
            }
            if (isV){
                tempFoodName=tempFoodName.replace("(V)","");
                tempFoodName=tempFoodName.trim();
            }
            foodName=tempFoodName;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * This is for creating mock food items
     * @param foodName
     * @param foodIdentifier
     */
    public FoodItem(String foodName, String foodIdentifier){
        this.foodIdentifier = foodIdentifier.trim();
        String tempFoodName=foodName;
        tempFoodName=tempFoodName.trim();

        isV=tempFoodName.contains("(V)");
        isVT=tempFoodName.contains("(VT)");
        if(isVT){
            tempFoodName=tempFoodName.replace("(VT)","");
            tempFoodName=tempFoodName.trim();
        } else if (isV){
            tempFoodName=tempFoodName.replace("(V)","");
            tempFoodName=tempFoodName.trim();
        }
        this.foodName=tempFoodName;
    }

    public String getFoodName(){
        return foodName;
    }
    public String getFoodIdentifier() { return foodIdentifier; }
    public boolean isV(){
        return isV;
    }
    public boolean isVT(){
        return isVT;
    }
    @Override
    public boolean equals(Object o){
        if(o instanceof FoodItem){
            FoodItem toCompare = (FoodItem) o;
            return this.getFoodIdentifier().equals(toCompare.getFoodIdentifier());
        }
        return false;
    }


}
