package com.eli.orange.restApi.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class MapData {

    public String getCountryCode() {
        return this.countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
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

    public String countryCode;
    public Double lat;
    public Double lng;

    // Default constructor required for calls to
    // DataSnapshot.getValue(MapData.class)
    public MapData() {
    }

    public MapData(String countryCode, Double lat, Double lng) {
        this.countryCode = countryCode;
        this.lat = lat;
        this.lng = lng;
    }
}