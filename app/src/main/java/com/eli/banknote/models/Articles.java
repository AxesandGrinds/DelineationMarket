package com.eli.banknote.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Articles {

    @SerializedName("author")
    @Expose  private String authorName;
    @SerializedName("title")
    @Expose private String newsTitle;
    @SerializedName("description")
    @Expose private String description;
    @SerializedName("url")
    @Expose private String urlToNewsContent;
    @SerializedName("urlToImage")
    @Expose private String urlToImage;
    @SerializedName("publishedAt")
    @Expose private String publishedAt;
    @SerializedName("content")
    @Expose private String newsContent;

    public Sources getSources() {
        return this.sources;
    }

    public void setSources(Sources sources) {
        this.sources = sources;
    }

    @SerializedName("source")
    private Sources sources;


    public String getAuthorName() {
        return this.authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getNewsTitle() {
        return this.newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrlToNewsContent() {
        return this.urlToNewsContent;
    }

    public void setUrlToNewsContent(String urlToNewsContent) {
        this.urlToNewsContent = urlToNewsContent;
    }

    public String getUrlToImage() {
        return this.urlToImage;
    }

    public void setUrlToImage(String urlToImage) {
        this.urlToImage = urlToImage;
    }

    public String getPublishedAt() {
        return this.publishedAt;
    }

    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public String getNewsContent() {
        return this.newsContent;
    }

    public void setNewsContent(String newsContent) {
        this.newsContent = newsContent;
    }

    public Articles(
            String authorName,
            String newsTitle,
            String description,
            String urlToNewsContent,
            String urlToImage,
            String publishedAt,
            String newsContent
    ) {
        this.authorName = authorName;
        this.description = description;
        this.newsContent = newsContent;
        this.newsTitle = newsTitle;
        this.urlToImage = urlToImage;
        this.urlToNewsContent = urlToNewsContent;
        this.publishedAt = publishedAt;
    }


}
