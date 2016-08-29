package com.company.models;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Created by brian on 4/15/16.
 */
@Entity("users")
public class User {

    public User(String email){
        this.email = email;
//        this.favorites = new ArrayList<>();
    }

    @Id
    public ObjectId _id;

    public String email;



    public User(){
    }

}