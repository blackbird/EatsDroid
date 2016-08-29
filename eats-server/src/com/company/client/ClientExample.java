package com.company.client;

import com.company.models.Menu;
import com.company.sockets.AuthenticationChecker;
import com.company.sockets.Request;
import com.company.sockets.Response;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by brian on 4/16/16.
 */
public class ClientExample {

    public static void main(String[] args){
        // Correct auth to be included on requests
        Request.RequestAuthentication auth = new Request.RequestAuthentication("admin", AuthenticationChecker.hash("password"));


        // Login request
        Request request = new Request(null, new Request.RequestCheckAuthentication("admin", AuthenticationChecker.hash("password")));
        new Client(request, new ResponseInterface() {

            Response resp;
            @Override
            public void callback(Response resp) {
               this.resp = resp;
            }

            @Override
            public void run() {
                System.out.println("SOCKETS: Callback called");
                if(this.resp != null && this.resp.requestSuccess){
                    System.out.println("Success");
                }else{
                    System.out.println("Failure");
                }

            }
        }).send();


        // Menu Add Request
        request = new Request(auth, new Request.RequestMenuAdd("57118b9b63259774a7d78800", "breakfast", "americana", "test_food", "Test FOOD"));
        new Client(request, new ResponseInterface() {

            Response resp;
            @Override
            public void callback(Response resp) {
                this.resp = resp;
            }

            @Override
            public void run() {
                System.out.println("SOCKETS: Callback called add");
                if(this.resp != null && this.resp.requestSuccess){
                    System.out.println("Success");
                }else{
                    System.out.println("Failure");
                }
            }
        }).send();


        // Menu Delete Request
        request = new Request(auth, new Request.RequestMenuDelete("57118b9b63259774a7d78800", "breakfast", "americana", "test_food"));
        new Client(request, new ResponseInterface() {

            Response resp;
            @Override
            public void callback(Response resp) {
                this.resp = resp;
            }

            @Override
            public void run() {
                System.out.println("SOCKETS: Callback called delete");
                if(this.resp != null && this.resp.requestSuccess){
                    System.out.println("Success");
                }else{
                    System.out.println("Failure");
                }
            }
        }).send();

        // Menu Pull Request

        Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"), Locale.US);
        c.set(Calendar.YEAR, 2016); // regular year
        c.set(Calendar.MONTH, 3); // from 0 - 11
        c.set(Calendar.DATE, 13);  // from 1 - 31..
        c.set(Calendar.HOUR, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        request = new Request(auth, new Request.RequestPullMenus("2016-04-13"));
        new Client(request, new ResponseInterface() {

            Response resp;
            @Override
            public void callback(Response resp) {
                this.resp = resp;
            }

            @Override
            public void run() {
                System.out.println("SOCKETS: Callback called menus");
                if(this.resp != null && this.resp.requestSuccess){
                    List<Menu> menus = (List<Menu>)this.resp.data;
                    for(Menu menu : menus){
                        System.out.println("SOCKETS: menu availablilty -> "+menu.restaurant_availability);
                    }
                    System.out.println("Success");
                }else{
                    System.out.println("Failure");
                }
            }
        }).send();

    }

}
