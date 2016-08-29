package com.company.models;

import com.company.Constants;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;

/**
 * Created by brian on 4/11/16.
 */
public class DatabaseSingleton {

        public  MongoDatabase database;
        private MongoClient mongoClient;

        private static DatabaseSingleton instance = null;
        protected DatabaseSingleton() {
            mongoClient = new MongoClient(Constants.MONGO_HOST, Constants.MONGO_PORT);
            database = mongoClient.getDatabase(Constants.MONGO_DB);
        }

        public MongoClient getClient(){
            return this.mongoClient;
        }

        public static DatabaseSingleton getInstance() {
            if(instance == null) {
                instance = new DatabaseSingleton();
            }
            return instance;
        }

}
