package com.example.sahil.myapplication;

import java.util.ArrayList;

/**
 * Created by adityaaggarwal on 9/5/16.
 */
public class RestaurantFeedback {
    private String name;
    private ArrayList<String> emails;
    public RestaurantFeedback(String name, ArrayList<String> emails) {
        this.name = name;
        this.emails = emails;
    }
    public String getName() {
        return name;
    }
    public String getMailtoEmails(){
       return "mailto:"+android.text.TextUtils.join(",", emails);
    }


}
