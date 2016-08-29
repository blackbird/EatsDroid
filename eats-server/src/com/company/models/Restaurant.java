package com.company.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Created by brian on 4/15/16.
 */
@Entity("restaurants")
public class Restaurant {

    @Id
    ObjectId _id;

    String name;
    String slug;
    String scrape_url;


    public Restaurant(){}

}
