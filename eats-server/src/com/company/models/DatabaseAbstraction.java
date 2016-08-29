package com.company.models;

import com.company.Constants;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by brian on 4/16/16.
 */
public class DatabaseAbstraction {

    public  void main(String[] args){
        System.out.println("YOOOOO;");

        String _id = "57118b9b63259774a7d78800";
        String meal_identifer = "breakfast";
        String section_identifier = "americana";

//        boolean me = addItem(_id,meal_identifer, section_identifier, "yooo", "Yooo");
        boolean me = removeItem(_id,meal_identifer, section_identifier, "yooo");
        System.out.println("yoooo" + me);
    }

    public  boolean removeItem(String _id, String meal_identifier, String section_identifier, String food_identifier){
        Datastore ds = new Morphia().createDatastore(DatabaseSingleton.getInstance().getClient(), Constants.MONGO_DB);
        Query q = ds.createQuery(Menu.class).filter("_id =", new ObjectId(_id));
        List<Menu> menus = q.asList();
        if(menus.size() < 1){
            return false;
        }
        Menu menu = menus.get(0);
        boolean success = false;
        for(Menu.Meal meal : menu.meals) {

            if(meal.meal_identifier.equals(meal_identifier)){
                for(Menu.MealSections mealSections : meal.meal_sections){
                    if(mealSections.section_identifier.equals(section_identifier)){
                        ListIterator<Menu.FoodItem> litr = mealSections.section_items.listIterator();
                        List<Menu.FoodItem> allFoodItems = new ArrayList<>();
                        while(litr.hasNext()){
                            Menu.FoodItem foodItem = litr.next();
                            if(!foodItem.food_identifier.equals(food_identifier)){
                                allFoodItems.add(foodItem);
                            }
                        }
                        // We've found the right section and will setup the old one.
                        mealSections.section_items = allFoodItems;
                        UpdateOperations<Menu> ops = ds.createUpdateOperations(Menu.class).set("meals", menu.meals);
                        ds.update(q, ops);
                        return true;
                    }
                }
                break;
            }
        }
        return false;
    }

    public  boolean addItem(String _id, String meal_identifier, String section_identifier, String food_identifier, String food_name){
        Datastore ds = new Morphia().createDatastore(DatabaseSingleton.getInstance().getClient(), Constants.MONGO_DB);
        Query q = ds.createQuery(Menu.class).filter("_id =", new ObjectId(_id));
        List<Menu> menus = q.asList();
        if(menus.size() < 1){
            return false;
        }
        Menu menu = menus.get(0);
        boolean success = false;
        for(Menu.Meal meal : menu.meals) {

            if(meal.meal_identifier.equals(meal_identifier)){
                for(Menu.MealSections mealSections : meal.meal_sections){
                    if(mealSections.section_identifier.equals(section_identifier)){
                        success = true;
                        for(Menu.FoodItem foodItem : mealSections.section_items) {
                            if(foodItem.equals(food_identifier)){
                                return true;
                            }
                        }
                        // We've found the right section and will insert it
                        mealSections.section_items.add(new Menu.FoodItem(food_identifier, food_name));
                        UpdateOperations<Menu> ops = ds.createUpdateOperations(Menu.class).set("meals", menu.meals);
                        ds.update(q, ops);
                        return true;
                    }
                }
                break;
            }
        }
        return false;
    }


    public synchronized List<Menu>  getMenus(String date){
        //String yo = date.get(Calendar.YEAR)+"-"+String.format("%02d", (date.get(Calendar.MONTH)+1))+"-"+date.get(Calendar.DATE)+"T00:00:00Z";
//        System.out.println(yo);

//        java.util.Date datetimeDate = new DateTime( date+"T00:00:00Z").toDate();
//        DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy");
//        String dataSerialized = df.format(new Date());
//
//
//        System.out.println("GETTING DATETIME: "+datetimeDate);
//        java.util.Date newdatetimeDate = new DateTime( "2016-04-13T00:00:00Z" ).toDate();
//        System.out.println("YOFOIEWOF");
//        System.out.println(datetimeDate);
//        System.out.println(newdatetimeDate);
//        System.out.println(datetimeDate.getTime());
//        Calendar greg = new GregorianCalendar(2014, 1, 22);
//        System.out.println(greg.getTime());
//        if(true){
//            return null;
//        }
        MongoDatabase database;
        MongoClient mongoClient;
        mongoClient = new MongoClient(Constants.MONGO_HOST, Constants.MONGO_PORT);
        String yoooo = date;
        Datastore ds = new Morphia().createDatastore(mongoClient, Constants.MONGO_DB);
        System.out.println("TRYING DATE!!!"+date);

        Query q = ds.createQuery(Menu.class).field("serizalized_date").equal(yoooo).disableValidation();
        return q.asList();
    }

}
