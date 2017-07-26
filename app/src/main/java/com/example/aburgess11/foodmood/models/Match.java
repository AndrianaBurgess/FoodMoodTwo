package com.example.aburgess11.foodmood.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by liangelali on 7/12/17.
 */


public class Match implements Comparable<Match>, Parcelable {


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

    public Match(Parcel parcel){

        this.name = parcel.readString();
        this.imageUrl = parcel.readString();
        this.location = parcel.readString();
        this.rank = parcel.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(this.name);
        dest.writeString(this.imageUrl);
        dest.writeString(this.location);
        dest.writeInt(this.rank);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Match createFromParcel(Parcel in) {
            return new Match(in);
        }

        public Match[] newArray(int size) {
            return new Match[size];
            }
        };

    }
