package com.packages.touristguide.model;

/**
 * Created by arif on 24-Mar-18.
 */

public class Users {

    public String address;
    public String city;
    public String country;
    public String email;
    public String image;
    public String name;
    public String phone;
    public String role;
    public String userID;

    public Users(){

    }

    public Users(String address, String city, String country, String email, String image, String name, String phone, String role, String userID) {
        this.address = address;
        this.city = city;
        this.country = country;
        this.email = email;
        this.image = image;
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.userID = userID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userId) {
        this.userID = userId;
    }
}
