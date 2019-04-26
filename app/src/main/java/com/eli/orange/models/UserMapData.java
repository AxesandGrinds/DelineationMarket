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

    public String username;
    public String businessType;
    public Double lat;
    public Double lng;

}