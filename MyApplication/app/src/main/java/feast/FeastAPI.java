package feast;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.sahil.myapplication.Utils.Favorites;
import com.example.sahil.myapplication.Utils.Installation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;



/**
 * Created by Riley on 4/10/16.
 */
public class FeastAPI
{
    public interface RequestCallback
    {
        public void requestFinishedWithSuccess(Boolean success, VolleyError error);
    }


    public interface CreateUserCallback
    {
        public void registeredUser(JSONObject response, VolleyError error);
    }


    public interface FavoritesCallback
    {
        public void fetchedFavorites(Set<FoodItem> favorites, VolleyError error);
    }

    public interface MenusCallback//EVK, Parkside, Cafe84
    {
        public void fetchedMenus(Map<String, Menu> menus, VolleyError error);
    }

    public interface RestaurantsCallback {
        public void fetchedRestaurants(TreeMap<String, String> restaurants, VolleyError error);
    }

    /**
     * To instantiate the cache and file dir for installation
     * @param context
     */
    public void setContext(Context context)
    {

        this.context=context;
    }

    public static FeastAPI sharedAPI = new FeastAPI();

    public static String baseURL = "https://uscdata.org/eats/v1";
    private Context context;

    private FeastAPI()
    {

    }



    /**
     * Fetch restaurants from ajax call or if in cache, from cache.
     * @param callback
     */
    public void fetchRestaurantsWithCompletion(final RestaurantsCallback callback) {
       String cache = VolleySingleton.getInstance(context).getFromCache(this.baseURL + "/restaurants");
        if(!cache.equals("")){
            JSONObject responseObject = null;
            try {
                responseObject = new JSONObject(cache);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONArray response = null;

            try {
                response = responseObject.getJSONArray("_items");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            TreeMap<String, String> restaurants = new TreeMap<String, String>();

            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject object = response.getJSONObject(i);


                    String restaurantId = object.getString("_id");
                    String restaurantName = object.getString("name");
                    restaurants.put(restaurantId, restaurantName);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            callback.fetchedRestaurants(restaurants, null);
        } else {
            StringRequest request = new StringRequest(Request.Method.GET, this.baseURL + "/restaurants", new Response.Listener<String>() {
                @Override
                public void onResponse(String responseString) {
                    JSONObject responseObject = null;
                    try {
                        responseObject = new JSONObject(responseString);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JSONArray response = null;
                    try {
                        response = responseObject.getJSONArray("_items");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    TreeMap<String, String> restaurants = new TreeMap<String, String>();

                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject object = response.getJSONObject(i);


                            String restaurantId = object.getString("_id");
                            String restaurantName = object.getString("name");
                            restaurants.put(restaurantId, restaurantName);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    callback.fetchedRestaurants(restaurants, null);
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    callback.fetchedRestaurants(null, error);
                }
            }) {
                @Override
                protected Response<String> parseNetworkResponse(NetworkResponse response) {
                    try {
                        Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                        if (cacheEntry == null) {
                            cacheEntry = new Cache.Entry();
                        }
                        final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
                        final long cacheExpired = 24 * 60 * 60 * 1000; // in 24 hours this cache entry expires completely
                        long now = System.currentTimeMillis();
                        final long softExpire = now + cacheHitButRefreshed;
                        final long ttl = now + cacheExpired;
                        cacheEntry.data = response.data;
                        cacheEntry.softTtl = softExpire;
                        cacheEntry.ttl = ttl;
                        String headerValue;
                        headerValue = response.headers.get("Date");
                        if (headerValue != null) {
                            cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                        }
                        headerValue = response.headers.get("Last-Modified");
                        if (headerValue != null) {
                            cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                        }
                        cacheEntry.responseHeaders = response.headers;
                        final String jsonString = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers));
                        return Response.success(jsonString, cacheEntry);
                    } catch (UnsupportedEncodingException e) {
                        return Response.error(new ParseError(e));
                    }
                }
            };
            VolleySingleton.getInstance(context).addToRequestQueue(request);
       }

    }

    /**
     * Fetch menus on a day, specified as a parameter
     * @param date Day to fetch menus on
     * @param callback Called after menus fetched to refresh views and data model related to them
     */
    public void fetchMenusForDateWithCompletion(Date date, final MenusCallback callback)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss");

        String href = null;
        try {
            href = this.baseURL + "/menus?where=" + URLEncoder.encode("{\"date\": \"" + dateFormat.format(date) + " GMT\"}","UTF-8").replace("+","%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String responseString= VolleySingleton.getInstance(context).getFromCache(href);
        if(!responseString.equals("")) {
            JSONObject responseObject = null;
            try {
                responseObject = new JSONObject(responseString);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            HashMap<String, Menu> menus = new HashMap<>();
            JSONArray  response = null;
            try {
                response = responseObject.getJSONArray("_items");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            for (int i = 0; i < response.length(); i++)
            {
                try
                {
                    JSONObject object = response.getJSONObject(i);

                    Menu menu = new Menu(object);

                    String restaurantId = object.getString("restaurant_id");


                    menus.put(restaurantId, menu);
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
            callback.fetchedMenus(menus, null);
            return;
        }

        StringRequest request = new StringRequest(href,   new Response.Listener<String>() {
            @Override
            public void onResponse(String responseString) {
                JSONObject responseObject = null;
                try {
                    responseObject = new JSONObject(responseString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                HashMap<String, Menu> menus = new HashMap<>();
                JSONArray  response = null;
                try {
                    response = responseObject.getJSONArray("_items");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < response.length(); i++)
                {
                    try
                    {
                        JSONObject object = response.getJSONObject(i);

                        Menu menu = new Menu(object);

                        String restaurantId = object.getString("restaurant_id");


                        menus.put(restaurantId, menu);
                    }
                    catch (JSONException e)
                    {
                        e.printStackTrace();
                    }
                }

                callback.fetchedMenus(menus, null);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.fetchedMenus(null, error);
            }
        }) {
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {

                Cache.Entry cacheEntry = HttpHeaderParser.parseCacheHeaders(response);
                if (cacheEntry == null) {
                    cacheEntry = new Cache.Entry();
                }
                final long cacheHitButRefreshed = 3 * 60 * 1000; // in 3 minutes cache will be hit, but also refreshed on background
                final long cacheExpired = 7 * 24 * 60 * 60 * 1000; // in 1 week this cache entry expires completely
                long now = System.currentTimeMillis();
                final long softExpire = now + cacheHitButRefreshed;
                final long ttl = now + cacheExpired;
                cacheEntry.data = response.data;
                cacheEntry.softTtl = softExpire;
                cacheEntry.ttl = ttl;
                String headerValue;
                headerValue = response.headers.get("Date");
                if (headerValue != null) {
                    cacheEntry.serverDate = HttpHeaderParser.parseDateAsEpoch(headerValue);
                }
                headerValue = response.headers.get("Last-Modified");
                if (headerValue != null) {
                    cacheEntry.lastModified = HttpHeaderParser.parseDateAsEpoch(headerValue);
                }
                cacheEntry.responseHeaders = response.headers;
                String jsonString = "";
                try {
                    jsonString = new String(response.data, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    return Response.error(new VolleyError(e));
                }
                return Response.success(jsonString, cacheEntry);

            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();

                params.put("Content-Type", "application/json");
                return params;
            }
    };
        request.setRetryPolicy(new DefaultRetryPolicy(DefaultRetryPolicy.DEFAULT_TIMEOUT_MS * 2, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }



    /**
     *
     * @param favorite
     * @param callback
     * @param isAdd Are you adding or deleting the favorite?
     */
    public void updateFavoritesWithCompletion(FoodItem favorite, final RequestCallback callback, boolean isAdd)
    {
        String href = this.baseURL + "/users/";
        String installationString = Installation.id(context);
        try {

            String installation=new JSONObject(Installation.id(context)).getString("installation_id");
            Log.d("Aditya", installationString + " : " + installation);
            href += new JSONObject(Installation.id(context)).getString("_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject object = null;

        try
        {
            object = new JSONObject();
            object.put("food_identifier", favorite.getFoodIdentifier());
            object.put("food_name", favorite.getFoodName());

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        if(isAdd)
            Favorites.addFavorite(context, object);
        else
            Favorites.deleteFavorite(context, object);
        JSONArray favoritesArray = null;
        try {
            favoritesArray = new JSONArray(Favorites.getFavorites(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject updateFavortiesObject = new JSONObject();

        try {
            updateFavortiesObject.put("device_ids", new JSONArray());
            updateFavortiesObject.put("favorites", favoritesArray);
            updateFavortiesObject.put("send_notifications", false); //TODO: update with appropriate value of settings send notification status
            updateFavortiesObject.toJSONArray(new JSONArray("[\"device_ids\",\"favorites\"]"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("Aditya", updateFavortiesObject.toString());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PATCH, href, updateFavortiesObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    File installation = new File(context.getFilesDir(), Installation.INSTALLATION);
                    JSONObject installationObj = new JSONObject(Installation.id(context));
                    installationObj.put("_etag", response.getString("_etag"));
                    Installation.rewriteInstallationFile(installation, installationObj.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                callback.requestFinishedWithSuccess(true, null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }
                callback.requestFinishedWithSuccess(false, error);
            }
        }){


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();

                params.put("Content-Type", "application/json");

                try {
                    params.put("If-Match", new JSONObject(Installation.id(context)).getString("_etag"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };

        request.setShouldCache(false); //want to update favorites differently each time
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }


    /**
     *
     * @param callback
     */
    public void updateFavoritesWithCompletion(final RequestCallback callback)
    {
        String href = this.baseURL + "/users/";
        String installationString = Installation.id(context);
        try {

            String installation=new JSONObject(Installation.id(context)).getString("installation_id");
            Log.d("Aditya", installationString + " : " + installation);
            href += new JSONObject(Installation.id(context)).getString("_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JSONArray favoritesArray = null;
        try {
            favoritesArray = new JSONArray(Favorites.getFavorites(context));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject updateFavortiesObject = new JSONObject();

        try {
            updateFavortiesObject.put("device_ids", new JSONArray());
            updateFavortiesObject.put("favorites", favoritesArray);
            updateFavortiesObject.put("send_notifications", false); //TODO: update with appropriate value of settings send notification status
            updateFavortiesObject.toJSONArray(new JSONArray("[\"device_ids\",\"favorites\"]"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("Aditya", updateFavortiesObject.toString());
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PATCH, href, updateFavortiesObject, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {

                    File installation = new File(context.getFilesDir(), Installation.INSTALLATION);
                    JSONObject installationObj = new JSONObject(Installation.id(context));
                    installationObj.put("_etag", response.getString("_etag"));
                    Installation.rewriteInstallationFile(installation, installationObj.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                callback.requestFinishedWithSuccess(true, null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }
                callback.requestFinishedWithSuccess(false, error);
            }
        }){


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<>();

                params.put("Content-Type", "application/json");

                try {
                    params.put("If-Match", new JSONObject(Installation.id(context)).getString("_etag"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };

        request.setShouldCache(false); //want to update favorites differently each time
        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }



    public void registerUser(final  CreateUserCallback callback)
    {
        final String href = this.baseURL + "/users";
        JSONObject object = null;

        try
        {
            object = new JSONObject();
            object.put("icloud_id", Installation.id(context));

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, href, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                callback.registeredUser(response, null);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.registeredUser(null, error);
            }
        });

        VolleySingleton.getInstance(context).addToRequestQueue(request);
    }



}
