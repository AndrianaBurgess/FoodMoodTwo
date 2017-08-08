package com.example.aburgess11.foodmood.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by snhoward on 8/4/17.
 */

public class Group {

    public Map<String, Object> restaurants;
    public ArrayList<Object> foodItems;
    public List<User> users;
    public String groupId;

    public Group() {
        // Default constructor required for calls to DataSnapshot.getValue(Group.class)
    }

    public Group(String groupId, List<User> users, Map<String, Object> restaurants, ArrayList<Object> foodItems) {
        this.users = users;
        this.groupId = groupId;
        this.restaurants = restaurants;
        this.foodItems = foodItems;
    }

    public Map<String, Object> getRestaurants() {
        return restaurants;
    }

    public ArrayList<Object> getFoodItems() {
        return foodItems;
    }

    public List<User> getUsers() {
        return users;
    }

    public String getGroupId() {
        return groupId;
    }
}
