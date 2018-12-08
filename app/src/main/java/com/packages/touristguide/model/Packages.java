package com.packages.touristguide.model;

/**
 * Created by prince on 3/16/2018.
 */

public class Packages {
    String name;
    String details;
    String duration;
    String userId;
    String location;
    String besttime;
    String available;
    String nexttour;
    String price;
    String post_image;
    String contact;

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Packages(String name, String details, String duration, String userId, String location,
                    String besttime, String available, String nexttour, String price, String post_image, String contact) {
        this.name = name;
        this.details = details;
        this.duration = duration;
        this.userId = userId;
        this.location = location;
        this.besttime = besttime;
        this.available = available;
        this.nexttour = nexttour;
        this.price = price;
        this.post_image = post_image;
        this.contact = contact;

    }

    public Packages() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBesttime() {
        return besttime;
    }

    public void setBesttime(String besttime) {
        this.besttime = besttime;
    }

    public String getAvailable() {
        return available;
    }

    public void setAvailable(String available) {
        this.available = available;
    }

    public String getNexttour() {
        return nexttour;
    }

    public void setNexttour(String nexttour) {
        this.nexttour = nexttour;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPost_image() {
        return post_image;
    }

    public void setPost_image(String post_image) {
        this.post_image = post_image;
    }
}
