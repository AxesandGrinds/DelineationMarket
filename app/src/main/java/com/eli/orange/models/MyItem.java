package com.eli.orange.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyItem implements ClusterItem {
    private final LatLng mPosition;
    private  String mTitle;
    private  String mSnippet;



    private String userId;

    public MyItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    public MyItem(double lat, double lng, String title, String snippet, String userId) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
        userId = userId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }
}
