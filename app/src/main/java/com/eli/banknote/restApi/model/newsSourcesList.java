package com.eli.banknote.restApi.model;

import com.eli.banknote.models.newsSource;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class newsSourcesList {

    @SerializedName("sources")
    private ArrayList<newsSource> newsSourceArrayList;

    public ArrayList<newsSource> getNewsSourceArrayList() {
        return newsSourceArrayList;
    }

    public void setNewsSourceArrayList(ArrayList<newsSource> newsSourceArrayList) {
        this.newsSourceArrayList = newsSourceArrayList;
    }
}
