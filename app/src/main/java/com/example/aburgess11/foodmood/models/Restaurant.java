package com.example.aburgess11.foodmood.models;

import org.parceler.Parcel;

import java.util.List;

/**
 * Created by snhoward on 8/1/17.
 */

@Parcel
public class Restaurant extends Object{

    //List of Attributes
    String counter;
    String id;
    String address;
    String review_count;
    String image_url;
    String phone;
    String rating;
    String name;
    Coordinates coordinates;
    List<String> photos;

    public Restaurant() {
        // Default constructor required for calls to DataSnapshot.getValue(Restaurant.class)
    }

    public Restaurant(String counter, String id, String address, String review_count, String image_url, String phone, String rating, String name, Coordinates coordinates, List<String> photos) {
        this.counter = counter;
        this.id = id;
        this.address = address;
        this.review_count = review_count;
        this.image_url = image_url;
        this.phone = phone;
        this.rating = rating;
        this.name = name;
        this.coordinates = coordinates;
        this.photos = photos;
    }

    public List<String> getPhotos() {
        return photos;
    }

    public void setPhotos(List<String> photos) {
        this.photos = photos;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
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

    public String getReview_count() {
        return review_count;
    }


    public String getImage_url() {
        return image_url;
    }

    public String getPhone() {
        return phone;
    }

    public String getRating() {
        return rating;
    }

    public String getCounter() {
        return counter;
    }

    public String getId() {
        return id;
    }

    public void setCounter(String counter) {
        this.counter = counter;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setReview_count(String review_count) {
        this.review_count = review_count;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
