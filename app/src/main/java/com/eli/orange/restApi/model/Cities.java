package com.eli.orange.restApi.model;

import com.google.gson.annotations.SerializedName;

public class Cities {

    public Cities(String country, String countryName, double latitude, double longitude) {
        this.country = country;
        this.countryName = countryName;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountryName() {
        return this.countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @SerializedName("country")
    private String country;
    @SerializedName("name")
    private String countryName;
    @SerializedName("lat")
    private double latitude;
    @SerializedName("lng")
    private double longitude;

}
