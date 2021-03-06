package com.example.aburgess11.foodmood.models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by snhoward on 8/1/17.
 */

public class FoodItem {

    // List of Attributes
    String restaurantId;
    String imageUrl;

    public FoodItem() {

    }

    public FoodItem(String restaurantId, String imageUrl) {
        this.restaurantId = restaurantId;
        this.imageUrl = imageUrl;
    }

    public FoodItem(JSONObject object) throws JSONException {
        restaurantId = object.getString("id");
        imageUrl = object.getJSONArray("photos").get(0).toString();
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
