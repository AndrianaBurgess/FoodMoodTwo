package com.example.aburgess11.foodmood.models;

import org.parceler.Parcel;

/**
 * Created by snhoward on 8/8/17.
 */

@Parcel
public class Coordinates {

    String latitude;
    String longitude;

    public Coordinates() {

    }

    public Coordinates(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
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
}
