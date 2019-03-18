package com.eli.banknote.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sources {

    public String getSourceId() {
        return this.sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceName() {
        return this.sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    @SerializedName("id")
    @Expose private String sourceId;
    @SerializedName("name")
    @Expose private String sourceName;
}
