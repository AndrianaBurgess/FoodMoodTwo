package com.example.aburgess11.foodmood;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import com.example.aburgess11.foodmood.models.SwipeProfile;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yelp.fusion.client.models.Business;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by aburgess11 on 7/12/17.
 */


public class Utils {

    private static final String TAG = "Utils";

    public static List<SwipeProfile> loadProfiles(Context context) {
        try {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            List<SwipeProfile> swipeProfileList = new ArrayList<>();
            //JSONArray array = new JSONArray(loadJSONFromAsset(context, "foods.json"));

            String jsonString = loadJSONFromAsset(context, "foods.json");
            JSONObject obj = new JSONObject(jsonString);
            JSONArray array = obj.getJSONArray("food");

            YelpClient yelpClient = new YelpClient("2000","37.479733","-122.154078");
            ArrayList<Business> businesses = yelpClient.getBusinesses();

            for (int i = 0; i < yelpClient.getTotalNumberOfResult(); i++) {
                for(int j = 0; j < businesses.get(i).getPhotos().size(); j++) {
                    SwipeProfile swipeProfile = gson.fromJson(businesses.get(i).getPhotos().get(j).toString(), SwipeProfile.class);
                    swipeProfileList.add(swipeProfile);
                }
            }
            return swipeProfileList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }



    private static String loadJSONFromAsset(Context context, String jsonFileName) {
        String json = null;
        InputStream is=null;
        try {
            AssetManager manager = context.getAssets();
            Log.d(TAG,"path "+jsonFileName);
            is = manager.open(jsonFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
