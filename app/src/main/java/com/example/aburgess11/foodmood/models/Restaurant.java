package com.example.aburgess11.foodmood.models;

import org.parceler.Parcel;

/**
 * Created by snhoward on 8/1/17.
 */

@Parcel
public class Restaurant extends Object{

    //List of Attributes
    String counter;
    String restaurantId;
    String address;
    String reviewCount;
    String latitude;
    String longitude;
    String imageUrl;
    String phoneNumber;
    String rating;
    String name;

    public Restaurant() {
        // Default constructor required for calls to DataSnapshot.getValue(Restaurant.class)
    }

    public Restaurant(String counter, String restaurauntId, String address, String reviewCount, String latitude, String longitude, String imageUrl, String phoneNumber, String rating, String name) {
        this.counter = counter;
        this.restaurantId = restaurauntId;
        this.address = address;
        this.reviewCount = reviewCount;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.phoneNumber = phoneNumber;
        this.rating = rating;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public String getReviewCount() {
        return reviewCount;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getRating() {
        return rating;
    }

    public String getCounter() {
        return counter;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setCounter(String counter) {
        this.counter = counter;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setReviewCount(String reviewCount) {
        this.reviewCount = reviewCount;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
