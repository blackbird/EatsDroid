package com.company.controllers;

import com.company.models.DatabaseSingleton;
import com.company.server.JsonRequestHandlerInterface;
import com.company.server.NotFoundRequestHandler;
import com.company.server.ResponseTuple;
import com.company.server.Serializer;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by brian on 4/11/16.
 */
public class UserController implements JsonRequestHandlerInterface {

    public class FoodObject{
        String food_identifier;
        String food_name;
        public FoodObject(String food_identifier, String food_name){
            this.food_identifier = food_identifier;
            this.food_name = food_name;
        }
    }

    @Override
    public ResponseTuple handleLogic(HttpExchange t) throws IOException {
        // TODO: Implement authentication
        String userId = "5704c2ee098388449b1a6c7b";

        // --- END TODO HACK

        System.out.println("REQUEST METHOD" + t.getRequestMethod());


        URI uri = t.getRequestURI();
        String[] pathElements = uri.getPath().split("/");
        // "", "users", "1", "device_token|favorites", (optional) "food_identifer"
        if (pathElements.length < 4) {

            return NotFoundRequestHandler.throw404(t);
        }
        userId = pathElements[2];
        System.out.println("has over min number of elements");
        // Make sure we have device token
        if (pathElements[3].equals("device_token")) {
            // Dispatch device_token
            // TODO: Implement device token
            return NotFoundRequestHandler.throw404(t);
        } else if (pathElements[3].equals("favorites")) {
            if (pathElements.length == 5 && t.getRequestMethod().equals("DELETE")) {
                // Delete the favorite from the user
                new UserDatabaseAbstraction().removeFavorite(pathElements[4], userId);
                return new ResponseTuple(200, "{\"status\":\"success\"}");
            } else if (pathElements.length == 4) {
                System.out.println("workign on getting favs");
                if (t.getRequestMethod().equals("GET")) {
                    String favoritesForUser = new UserDatabaseAbstraction().getFavoritesForUser(userId);
                    return new ResponseTuple(200, favoritesForUser);
                }else if(t.getRequestMethod().equals("POST")){
                    System.out.println("This is a post request");
                    JsonParser parser = new JsonParser();
                    System.out.println(t.getRequestBody().toString());
                    Scanner s = new Scanner(t.getRequestBody()).useDelimiter("\\A");
                    String result = s.hasNext() ? s.next() : "";

                    Gson g = new Gson();
                    FoodObject foodObject = g.fromJson(result, FoodObject.class);


                    if(foodObject == null){
                        System.out.println("fuk");
                    }else{
                        System.out.println("balls");
                    }

                    new UserDatabaseAbstraction().addFavorite( foodObject.food_identifier,  foodObject.food_name, userId);
                    return new ResponseTuple(200, "{\"status\":\"success\"}");
                }
            }
        } else {
            return NotFoundRequestHandler.throw404(t);
        }
        return new ResponseTuple(200, "{\"user\":\"yo\"}");
    }


    public static class UserDatabaseAbstraction {
        public UserDatabaseAbstraction() {

        }

        public void addFavorite(String foodIdentifier, String foodName, String userId) {
            MongoCollection<Document> collection = DatabaseSingleton.getInstance().database.getCollection("users");
            FindIterable<Document> documents = collection.find(new Document("_id", new ObjectId(userId)));
            for (Document doc : documents) {
                System.out.println("Found atlesta one");

                // Update only the first one...
                Object favorites = doc.get("favorites");
                ArrayList<Document> castedFavorites;
                if (favorites == null) {
                    System.out.print("Favorites are null...");
                    castedFavorites = new ArrayList<Document>();
                }else{
                    castedFavorites = (ArrayList<Document>) favorites;
                }

                for (Document favoriteDocument : castedFavorites) {
                    String identifier = (String) favoriteDocument.get("food_identifier");
                    if (identifier.equals(foodIdentifier)) {
                        System.out.println("Adding found favorite already exits...");
                        break;
                    }
                }
                castedFavorites.add(new Document("food_identifier", foodIdentifier).append("food_name", foodName));
                collection.findOneAndUpdate(new Document("_id", new ObjectId(userId)), new Document("$set", new Document("favorites", castedFavorites)));
                break; // Should only be one...
            }
        }

        public void removeFavorite(String foodIdentifier, String userId) {
            System.out.println("Removing foodIdentifier: " + foodIdentifier);
            MongoCollection<Document> collection = DatabaseSingleton.getInstance().database.getCollection("users");
            FindIterable<Document> documents = collection.find(new Document("_id", new ObjectId(userId)));
            for (Document doc : documents) {
                System.out.println("Found atlesta one");

                // Update only the first one...
                Object favorites = doc.get("favorites");
                if (favorites == null) {
                    System.out.print("Favorites are null...");
                    return;
                }
                ArrayList<Document> castedFavorites = (ArrayList<Document>) favorites;
                ArrayList<Document> newFavorites = new ArrayList<Document>();
                for (Document favoriteDocument : castedFavorites) {
                    String identifier = (String) favoriteDocument.get("food_identifier");
                    if (!identifier.equals(foodIdentifier)) {
                        System.out.println("Adding " + identifier + " back to new favorites");
                        newFavorites.add(favoriteDocument);
                    }
                }
                for (Document newFav : newFavorites) {
                    System.out.println("new favorite: " + newFav.get("food_identifier"));
                }


                collection.findOneAndUpdate(new Document("_id", new ObjectId(userId)), new Document("$set", new Document("favorites", newFavorites)));
                break;
            }

        }

        public String getFavoritesForUser(String userId) {
            MongoCollection<Document> collection = DatabaseSingleton.getInstance().database.getCollection("users");
            FindIterable<Document> documents = collection.find(new Document("_id", new ObjectId(userId)));
            for (Document doc : documents) {
                System.out.println("Found atlesta one");

                // Update only the first one...
                Object favorites = doc.get("favorites");
                if (favorites == null) {
                    System.out.print("Favorites are null...");
                    return "[]"; // Should return [];
                }
                ArrayList<Document> castedFavorites = (ArrayList<Document>) favorites;
                ArrayList<String> renderedDocuments = new ArrayList<String>();
                for (Document favorite : castedFavorites) {
                    renderedDocuments.add(new Serializer(favorite).serialize());
                }
                return "[" + String.join(", ", renderedDocuments) + "]";
            }
            return "[]"; // Should never return none b/c we're authorizing the user first
        }

    }


}
