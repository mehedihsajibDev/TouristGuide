package com.packages.touristguide.model;

/**
 * Created by prince on 3/15/2018.
 */

public class TopplaceModel {
    String title;
    String details;
    String address;
    double geolat;
    double geolong;
    String post_image;

    public TopplaceModel(String title, String details, String address, double geolat, double geolong, String post_image) {
        this.title = title;
        this.details = details;
        this.address = address;
        this.geolat = geolat;
        this.geolong = geolong;
        this.post_image = post_image;
    }

    public TopplaceModel() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public double getGeolat() {
        return geolat;
    }

    public void setGeolat(double geolat) {
        this.geolat = geolat;
    }

    public double getGeolong() {
        return geolong;
    }

    public void setGeolong(double geolong) {
        this.geolong = geolong;
    }

    public String getPost_image() {
        return post_image;
    }

    public void setPost_image(String post_image) {
        this.post_image = post_image;
    }
}
