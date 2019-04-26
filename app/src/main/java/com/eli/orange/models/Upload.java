package com.eli.orange.models;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Upload{
    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return this.price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
    public String  getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Upload(String title, String description, String price, String url) {
        this.title = title;
        this.description = description;
        this.price = price;
        this.url = url;
    }

    private String title;
    private String description;
    private String price;



    private String url;
    public Upload(){}
}
