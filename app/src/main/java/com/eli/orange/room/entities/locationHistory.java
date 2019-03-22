package com.eli.orange.room.entities;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class locationHistory implements Serializable {

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddressline() {
        return this.addressline;
    }

    public void setAddressline(String addressline) {
        this.addressline = addressline;
    }

    public String getCountryname() {
        return this.countryname;
    }

    public void setCountryname(String countryname) {
        this.countryname = countryname;
    }

    public String getCountrycode() {
        return this.countrycode;
    }

    public void setCountrycode(String countrycode) {
        this.countrycode = countrycode;
    }

    public String getAdminarea() {
        return this.adminarea;
    }

    public void setAdminarea(String adminarea) {
        this.adminarea = adminarea;
    }

    public String getPostalcode() {
        return this.postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getSubadminarea() {
        return this.subadminarea;
    }

    public void setSubadminarea(String subadminarea) {
        this.subadminarea = subadminarea;
    }

    public String getLocality() {
        return this.locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getSubThroughfare() {
        return this.subThroughfare;
    }

    public void setSubThroughfare(String subThroughfare) {
        this.subThroughfare = subThroughfare;
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


    @PrimaryKey(autoGenerate = true)
    @NonNull
    private int id;
    private String addressline;
    private String countryname;
    private String countrycode;
    private String adminarea;
    private String postalcode;
    private String subadminarea;
    private String locality;
    private String subThroughfare;
    private double latitude;
    private double longitude;

}
