package com.company.server;

import org.bson.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by brian on 4/11/16.
 */
public class Serializer {

    Document obj;
    public Serializer(Document obj){
        this.obj = obj;
    }

    public String serialize(){
        String line = this.obj.toJson();

        String pattern = "\\{\\s\\\"\\$oid\\\"\\s:\\s\\\"([a-z,0-9]+)\\\"\\s\\}";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Now create matcher object.
        Matcher m = r.matcher(line);
        while(m.find()) {
            System.out.println("Found value: " + m.group(0));
            System.out.println("Found value: " + m.group(1));
            line = line.replace(m.group(0), "\"" + m.group(1) + "\"");
            m = r.matcher(line);
        }

        pattern = "\\{\\s\\\"\\$date\\\"\\s:\\s([0-9]+)\\s\\}";
        r = Pattern.compile(pattern);

        m = r.matcher(line);
        while(m.find()) {
            System.out.println("Found value: " + m.group(0));
            System.out.println("Found value: " + m.group(1));
            line = line.replace(m.group(0), m.group(1));
            m = r.matcher(line);
        }

        return line;


    }
}
