package com.eli.orange.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Licence {


    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public Licence(){}

    private String title;
    private String url;

    public String getHint() {
        return this.hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    private String hint;
}
