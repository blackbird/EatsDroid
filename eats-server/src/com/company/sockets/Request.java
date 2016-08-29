package com.company.sockets;

import java.io.Serializable;

/**
 * Created by brian on 4/16/16.
 */


public class Request implements Serializable{

    public RequestAuthentication auth;
    public Object data;
    public Object request;

    public Request(RequestAuthentication auth, Object data) {
        this.auth = auth;
        this.data = data;
    }

    // Passed along w/ every request
    public static class RequestAuthentication implements Serializable{
        public String username;

        public RequestAuthentication(String username, String password) {
            this.username = username;
            this.password = password;
        }

        public String password;
    }


    // Pragma: Request
    public static class RequestCheckAuthentication extends RequestAuthentication implements Serializable{
        public RequestCheckAuthentication(String username, String password) {
            super(username, password);
        }
    }

    public static class RequestMenuUpdate implements Serializable{
        public String _id, meal_identifier, section_identifier, food_identifier;

        public RequestMenuUpdate(String _id, String meal_identifier, String section_identifier, String food_identifier) {
            this._id = _id;
            this.meal_identifier = meal_identifier;
            this.section_identifier = section_identifier;
            this.food_identifier = food_identifier;
        }
    }

    // Pragma: Request
    public static class RequestMenuAdd extends  RequestMenuUpdate implements Serializable{
        String food_name;
        public RequestMenuAdd(String _id, String meal_identifier, String section_identifier, String food_identifier, String food_name) {
            super(_id, meal_identifier, section_identifier, food_identifier);
            this.food_name = food_name;
        }
    }

    // Pragma: Request
    public static class RequestMenuDelete extends  RequestMenuUpdate implements Serializable{
        public RequestMenuDelete(String _id, String meal_identifier, String section_identifier, String food_identifier) {
            super(_id, meal_identifier, section_identifier, food_identifier);
        }
    }

    // Pragma: Request
    public static class RequestPullMenus implements Serializable{
        public RequestPullMenus(String date) {
            this.date = date;
        }
        String date;
    }


}
