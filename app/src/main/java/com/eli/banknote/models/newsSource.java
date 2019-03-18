package com.eli.banknote.models;

import com.google.gson.annotations.SerializedName;

public class newsSource {

    @SerializedName("id")
    private String sourceId;

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

    public String getSourceDescription() {
        return this.sourceDescription;
    }

    public void setSourceDescription(String sourceDescription) {
        this.sourceDescription = sourceDescription;
    }

    public String getSourceUrl() {
        return this.sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public String getSourceCategory() {
        return this.sourceCategory;
    }

    public void setSourceCategory(String sourceCategory) {
        this.sourceCategory = sourceCategory;
    }

    public String getSourceLanguage() {
        return this.sourceLanguage;
    }

    public void setSourceLanguage(String sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }

    public String getSourceCountry() {
        return this.sourceCountry;
    }

    public void setSourceCountry(String sourceCountry) {
        this.sourceCountry = sourceCountry;
    }

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    private boolean isChecked;

    public boolean getChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }



    @SerializedName("name")
    private String sourceName;
    @SerializedName("description")
    private String sourceDescription;
    @SerializedName("url")
    private String sourceUrl;
    @SerializedName("category")
    private String sourceCategory;
    @SerializedName("language")
    private String sourceLanguage;
    @SerializedName("country")
    private String sourceCountry;

    public newsSource(
            String sourceId,
            String sourceName,
            String sourceDescription,
            String sourceUrl,
            String sourceCategory,
            String sourceLanguage,
            String sourceCountry)
    {
        this.sourceCategory =sourceCategory;
        this.sourceCountry =sourceCountry;
        this.sourceDescription = sourceDescription;
        this.sourceId = sourceId;
        this.sourceLanguage =sourceLanguage;
        this.sourceUrl = sourceUrl;
        this.sourceName = sourceName;
    }


}
