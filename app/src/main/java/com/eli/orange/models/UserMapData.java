package com.eli.orange.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class UserMapData {

    public UserMapData(String username, String businessType, Double lat, Double lng) {
        this.username = username;
        this.businessType = businessType;
        this.lat = lat;
        this.lng = lng;
    }

    public UserMapData(){

    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getBusinessType() {
        return this.businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public Double getLat() {
        return this.lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return this.lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String username;
    public String businessType;
    public Double lat;
    public Double lng;

}