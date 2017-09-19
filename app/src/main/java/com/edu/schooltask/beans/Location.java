package com.edu.schooltask.beans;

import java.io.Serializable;

/**
 * Created by 夜夜通宵 on 2017/9/11.
 */

public class Location implements Serializable{
    private String city;
    private String location = "";
    private String detail;
    private double latitude;
    private double longitude;

    public Location(){}

    public Location(String city){
        this.city = city;
        this.location = city;
    }

    public Location(String city, double latitude, double longitude) {
        this.city = city;
        this.location = city;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public Location(String city, String location, String detail, double latitude, double longitude) {
        this.city = city;
        this.location = location;
        this.detail = detail;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
