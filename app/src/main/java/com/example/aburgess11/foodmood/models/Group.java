package com.example.aburgess11.foodmood.models;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by snhoward on 8/4/17.
 */

public class Group {

    public Map<String, Restaurant> restaurants;
    public ArrayList<Object> foodItems;
    public Map<String, String> users;
    public String groupId;

    public Group() {
        // Default constructor required for calls to DataSnapshot.getValue(Group.class)
    }

    public Group(String groupId, Map<String, String> users, Map<String, Restaurant> restaurants, ArrayList<Object> foodItems) {
        this.users = users;
        this.groupId = groupId;
        this.restaurants = restaurants;
        this.foodItems = foodItems;
    }

    public void setUsers(Map<String, String> users) {
        this.users = users;
    }

    public Map<String, Restaurant> getRestaurants() {
        return restaurants;
    }

    public ArrayList<Object> getFoodItems() {
        return foodItems;
    }

    public Map<String, String> getUsers() {
        return users;
    }

    public String getGroupId() {
        return groupId;
    }
}

