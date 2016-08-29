package com.company.controllers;

import com.company.models.DatabaseSingleton;
import com.company.server.JsonRequestHandlerInterface;
import com.company.server.NotFoundRequestHandler;
import com.company.server.ResponseTuple;
import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.sun.net.httpserver.HttpExchange;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

/**
 * Created by brian on 4/15/16.
 */
public class AuthorizeController implements JsonRequestHandlerInterface {

    public static class AuthorizePacket {
        public String oauth_token;
    }

    @Override
    public ResponseTuple handleLogic(HttpExchange t) throws IOException {
        Scanner s = new Scanner(t.getRequestBody()).useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";

        System.out.println("CALLED AUTHORIZEEEE");

        Gson g = new Gson();
        AuthorizePacket auth = g.fromJson(result, AuthorizePacket.class);

        if(auth == null){
            return NotFoundRequestHandler.throw404(t);
        }


        System.out.println("Trying for token" + auth.oauth_token);

        String user_id = getUserFromToken(auth.oauth_token);


        System.out.println("Trying for user id"+ user_id);

        if(user_id == null){
            return new ResponseTuple(400, "{\"status\":\"error\"}");
        }
        return new ResponseTuple(200, "{\"status\":\"success\", \"user_id\":\""+user_id+"\"}");
    }

    public static class GoogleOAuthResponse{
        public String email;
        public String name;
    }

    /* Example response...
    {
        id: "106488883031741616794",
        email: "brianranglin@gmail.com",
        verified_email: true,
        name: "Brian Anglin",
        given_name: "Brian",
        family_name: "Anglin",
        link: "https://plus.google.com/106488883031741616794",
        picture: "https://lh3.googleusercontent.com/-cd0pIDdluh8/AAAAAAAAAAI/AAAAAAAAAHI/MOWL7k9HA5g/photo.jpg",
        gender: "male"
    }
    */

    public String getUserFromToken(String token){
        String requestURL = "https://www.googleapis.com/oauth2/v1/userinfo?access_token="+token;
        String responseBody = excuteGET(requestURL);

        Gson g = new Gson();
        GoogleOAuthResponse auth = g.fromJson(responseBody, GoogleOAuthResponse.class);
        if(auth == null){
            return null;
        }


        MongoCollection<Document> collection = DatabaseSingleton.getInstance().database.getCollection("users");
        FindIterable<Document> documents = collection.find(new Document("email", auth.email));
        for (Document doc : documents) {
            System.out.println("Found atlesta one");
            ObjectId id = (ObjectId) doc.get("_id");
            return  id.toString();
        }
        // Not found, create
        collection.insertOne(new Document("email", auth.email)); // Should have created it
        documents = collection.find(new Document("email", auth.email));
        for (Document doc : documents) {
            System.out.println("Found atlesta one");
            ObjectId id = (ObjectId) doc.get("_id");
            return  id.toString();
        }
        return null;

//        Datastore ds = new Morphia().createDatastore(new MongoClient("localhost", 27017), "eatseve");
//        ds.ensureIndexes();
//
//        Query<User> query = ds.createQuery(User.class);
//        query.field("email").equal(auth.email);
//        List<User> users = query.asList();
//        System.out.println("User size " + users.size());
//        if(users.size() > 0){
//            return users.get(0)._id.toString();
//        }else{
//            User user =  new User(auth.email);
//            ds.save(user);
//            return user._id.toString();
//        }
    }

    public static String excuteGET(String targetURL) {
        URL url = null;
        try {
            url = new URL(targetURL);
            URLConnection conn = url.openConnection();
            InputStream is = conn.getInputStream();
            Scanner s = new Scanner(is).useDelimiter("\\A");
            String result = s.hasNext() ? s.next() : "";
            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
