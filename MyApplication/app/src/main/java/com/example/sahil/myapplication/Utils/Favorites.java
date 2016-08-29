package com.example.sahil.myapplication.Utils;

/**
 * Created by adityaaggarwal on 8/6/16.
 */

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by adityaaggarwal on 8/6/16.
 */
public class Favorites {
    private static String favs = null;
    public static final String FAVORITES = "FAVORITES";


    public synchronized static String getFavorites(Context context) {
        if (favs == null) {
            File favorites = new File(context.getFilesDir(), FAVORITES);
            try {
                if (!favorites.exists()) {
                    favs = "[]";
                    writeFavFile(favorites);
                } else
                    favs = readFavFile(favorites);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return favs;
    }
    public synchronized static String addFavorite(Context context, JSONObject foodItem) {

            try {
                favs =  new JSONArray(getFavorites(context)).put(foodItem).toString();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            File favorites = new File(context.getFilesDir(), FAVORITES);
            try {
                writeFavFile(favorites);
            } catch (IOException e) {
                e.printStackTrace();
            }
        return favs;
    }

    public synchronized static void deleteFavorite(Context context, JSONObject foodItem) {

        try {

            JSONArray jsonArray = new JSONArray(getFavorites(context));
            for (int i =0; i<jsonArray.length(); ++i) {
                if(jsonArray.get(i).equals(foodItem)){
                    jsonArray.remove(i);
                    return;
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        File favorites = new File(context.getFilesDir(), FAVORITES);
        try {
            writeFavFile(favorites);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static String readFavFile(File installation) throws IOException {
        RandomAccessFile f = new RandomAccessFile(installation, "r");
        byte[] bytes = new byte[(int) f.length()];
        f.readFully(bytes);
        f.close();
        return new String(bytes);
    }

    private static void writeFavFile(File installation) throws IOException {
        FileOutputStream out = new FileOutputStream(installation);

        out.write(favs.getBytes());
        out.close();
    }




}
