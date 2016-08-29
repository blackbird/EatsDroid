package com.company.controllers;

import com.company.models.DatabaseSingleton;
import com.company.server.JsonRequestHandlerInterface;
import com.company.server.ResponseTuple;
import com.company.server.Serializer;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;

/**
 * Created by brian on 4/15/16.
 */
public class RestaurantController implements JsonRequestHandlerInterface {

    @Override
    public ResponseTuple handleLogic(HttpExchange t) throws IOException {
        URI uri = t.getRequestURI();
        String[] pathElements = uri.getPath().split("/");
        // "", "restaurants"

        return new ResponseTuple(200, getRestaurants());
    }
    public String getRestaurants(){
        MongoCollection<Document> collection = DatabaseSingleton.getInstance().database.getCollection("restaurants");
        FindIterable<Document> documents = collection.find();
        ArrayList<Document> castedFavorites = new ArrayList<Document>();
        for(Document doc : documents){
            castedFavorites.add(doc);
        }
        ArrayList<String> renderedDocuments = new ArrayList<String>();
        for (Document favorite : castedFavorites) {
            renderedDocuments.add(new Serializer(favorite).serialize());
        }
        return "[" + String.join(", ", renderedDocuments) + "]";
    }

}
