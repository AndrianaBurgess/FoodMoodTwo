package com.example.aburgess11.foodmood.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by liangelali on 7/12/17.
 */


public class Match implements Comparable<Match>{


    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("url")
    @Expose
    private String imageUrl;

    @SerializedName("location")
    @Expose
    private String location;


    public int rank;

    // initialize from JSON data


    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getLocation() {
        return location;
    }


    @Override
    public int compareTo(@NonNull Match match) {
        return Integer.compare(match.rank, this.rank);
    }
}
