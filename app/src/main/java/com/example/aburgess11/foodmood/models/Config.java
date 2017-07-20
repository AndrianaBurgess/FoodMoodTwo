package com.example.aburgess11.foodmood.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by liangelali on 7/13/17.
 */

public class Config {

    // the base url for loading images
    String imageBaseUrl;
    // the backdrop size to use when fetching images
    String backdropSize;


    public Config(JSONObject object) throws JSONException {
        JSONObject images = object.getJSONObject("images");
        imageBaseUrl = images.getString("secure_base_url");
        JSONArray backdropSizeOptions = images.getJSONArray("backdrop_sizes");
        backdropSize = backdropSizeOptions.optString(1, "w780");
    }

    // helper method for creating urls
    public String getImageUrl(String size, String path) {
        return String.format("%s%s%s", imageBaseUrl, size, path); // concatenate all three
    }

    public String getBackdropSize() {
        return backdropSize;
    }

    public String getImageBaseUrl() {
        return imageBaseUrl;
    }

}

