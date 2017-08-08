package com.example.aburgess11.foodmood.models;

import java.util.Map;

/**
 * Created by snhoward on 8/4/17.
 */

public class City {

    public Map<String, Object> restaurants;
    public Map<String, Object> foodItems;
    public String name;
    public String imageUrl;

    public City() {
        // Default constructor required for calls to DataSnapshot.getValue(City.class)
    }

    public City(Map<String, Object> restaurants, Map<String, Object> foodItems, String name, String imageUrl) {
        this.restaurants = restaurants;
        this.foodItems = foodItems;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
