package com.example.aburgess11.foodmood;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by aburgess11 on 7/17/17.
 */

public class Restaurant {

    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("url")
    @Expose
    private String imageUrl;

    @SerializedName("location")
    @Expose
    private String location;

    @SerializedName("openTime")
    @Expose
    private String openTime;

    @SerializedName("closeTime")
    @Expose
    private String closeTime;

    @SerializedName("rank")
    @Expose int rank ;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
