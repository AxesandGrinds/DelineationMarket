package com.eli.orange.room.entities;


import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_info")
public class userInfo implements Serializable {

    @NonNull
    public int getUserID() {
        return this.userID;
    }

    public void setUserID(@NonNull int userID) {
        this.userID = userID;
    }

    public String getBusinessname() {
        return this.businessname;
    }

    public void setBusinessname(String businessname) {
        this.businessname = businessname;
    }

    public String getLocationame() {
        return this.locationame;
    }

    public void setLocationame(String locationame) {
        this.locationame = locationame;
    }



    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



    @PrimaryKey(autoGenerate = true)
    private int userID;
    private String businessname;
    private String locationame;

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

    private double latitude;
    private double longitude;
    private String username;



}
