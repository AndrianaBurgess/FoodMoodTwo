package com.example.aburgess11.foodmood.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by snhoward on 8/1/17.
 */

@Parcel
public class Restaurant extends Object{

    //List of Attributes
    int counter;
    @SerializedName("restaurantId")
            @Expose
    String restaurauntId;

    @SerializedName("address")
            @Expose
    String address;

    @SerializedName("reviewCount")
            @Expose
    String reviewCount;

    @SerializedName("latitude")
            @Expose
    String latitude;

    @SerializedName("longitude")
            @Expose
    String longitude;

    @SerializedName("imageUrl")
            @Expose
    String imageUrl;

    @SerializedName("phoneNumber")
            @Expose
    String phoneNumber;

    @SerializedName("rating")
            @Expose
    String rating;

    @SerializedName("name")
            @Expose
    String name;

    public Restaurant() {}


    public Restaurant(String restaurauntId, int counter) {
        this.restaurauntId = restaurauntId;
        this.counter = counter;
    }

    public Restaurant(JSONObject business) throws JSONException {
        counter = 0;
        restaurauntId = business.getString("id");
//        latitude = business.getString("latitude");
//        longitude = business.getString("longitude");
        imageUrl = business.getString("image_url");
        phoneNumber = business.getString("phone");
        rating = business.getString("rating");
        name = business.getString("name");
//        address = business.getString("cross_streets");

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

    public void setAddress(String address) {
        this.address = address;
    }

    public String getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(String reviewCount) {
        this.reviewCount = reviewCount;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public String getRestaurauntId() {
        return restaurauntId;
    }

    public void setRestaurauntId(String restaurauntId) {
        this.restaurauntId = restaurauntId;
    }
}