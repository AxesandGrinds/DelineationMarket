package com.eli.banknote.restApi.model;

import com.eli.banknote.models.Articles;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class articlesList {
    @SerializedName("articles")
    private ArrayList<Articles> newsList;

    public ArrayList<Articles> getNewsArrayList() {
        return newsList;
    }

    public void setNewsArrayList(ArrayList<Articles> newsArrayList) {
        this.newsList = newsArrayList;
    }

    public ArrayList<Articles> getNewsList() {
        return this.newsList;
    }

    public void setNewsList(ArrayList<Articles> newsList) {
        this.newsList = newsList;
    }
}
