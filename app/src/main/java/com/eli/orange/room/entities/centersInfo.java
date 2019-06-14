package com.eli.orange.room.entities;


import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_info")
public class centersInfo implements Serializable {


    public int getCenterID() {
        return this.centerID;
    }

    public void setCenterID(final int centerID) {
        this.centerID = centerID;
    }

    public String getBusinessname() {
        return this.businessname;
    }

    public void setBusinessname(final String businessname) {
        this.businessname = businessname;
    }

    public String getLocationame() {
        return this.locationame;
    }

    public void setLocationame(final String locationame) {
        this.locationame = locationame;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(final double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(final double longitude) {
        this.longitude = longitude;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(final String username) {
        this.username = username;
    }

    @PrimaryKey(autoGenerate = true)
    private int centerID;
    private String businessname;
    private String locationame;
    private double latitude;
    private double longitude;
    private String username;



}
