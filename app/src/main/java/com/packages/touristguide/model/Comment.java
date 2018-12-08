package com.packages.touristguide.model;

/**
 * Created by prince on 3/15/2018.
 */

public class Comment {
    String name;
    String address;
    String pro_pic;
    String comment;
    String date;

    public Comment(String name, String address, String pro_pic, String comment, String date) {
        this.name = name;
        this.address = address;
        this.pro_pic = pro_pic;
        this.comment = comment;
        this.date = date;
    }

    public Comment() {
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

    public String getPro_pic() {
        return pro_pic;
    }

    public void setPro_pic(String pro_pic) {
        this.pro_pic = pro_pic;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
