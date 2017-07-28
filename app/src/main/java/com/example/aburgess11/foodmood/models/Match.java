package com.example.aburgess11.foodmood.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by liangelali on 7/12/17.
 */

@Parcel // annotation indicates class is Parcelable
public class Match implements Comparable<Match>{


    @SerializedName("name")
    @Expose
    String name;

    @SerializedName("url")
    @Expose
    String imageUrl;

    @SerializedName("location")
    @Expose
    String location;


    public int rank;

    // no-arg, empty constructor required for Parceler
    public Match() {}

    // initialize from JSON data
    public Match(JSONObject object) throws JSONException {
        name = object.getString("name");
        imageUrl = object.getString("imageUrl");
        location = object.getString("location");
    }


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
