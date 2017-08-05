package com.example.aburgess11.foodmood.models;

import java.util.Map;

/**
 * Created by snhoward on 8/4/17.
 */

public class City {

    public Map<String, Object> restaurants;
    public Map<String, Object> foodItems;

    public City() {
        // Default constructor required for calls to DataSnapshot.getValue(City.class)
    }

    public City(Map<String, Object> restaurants, Map<String, Object> foodItems) {
        this.restaurants = restaurants;
        this.foodItems = foodItems;
    }
}
