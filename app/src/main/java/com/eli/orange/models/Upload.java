package com.eli.orange.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.database.IgnoreExtraProperties;

import static com.facebook.stetho.inspector.network.ResponseHandlingInputStream.TAG;

@IgnoreExtraProperties
public class Upload implements Parcelable {

    private String title;
    private String description;
    private String price;
    private Double longitude;
    private Double latitude;
    private String url;
    private String productKey;

    public Upload(Parcel in) {
        title = in.readString();
        description = in.readString();
        price = in.readString();
        url = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
        userKey = in.readString();
        productKey = in.readString();

    }

    public static final Creator<Upload> CREATOR = new Creator<Upload>() {
        @Override
        public Upload createFromParcel(Parcel in) {
            return new Upload(in);
        }

        @Override
        public Upload[] newArray(int size) {
            return new Upload[size];
        }
    };

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

    public String getProductKey() {
        return this.productKey;
    }

    public void setProductKey(final String productKey) {
        this.productKey = productKey;
    }


    public String getUserKey() {
        return this.userKey;
    }

    public void setUserKey(final String userKey) {
        this.userKey = userKey;
    }

    private String userKey;

    public Double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(final Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(final Double latitude) {
        this.latitude = latitude;
    }

    public Upload(){}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Upload{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", price='" + price + '\'' +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        Log.d("DEFAULT_TAG", "writeToParcel..."+ flags);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(price);
        dest.writeString(url);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(userKey);
        dest.writeString(productKey);
    }



}
