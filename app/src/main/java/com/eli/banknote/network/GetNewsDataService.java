package com.eli.banknote.network;

import com.eli.banknote.utils.Constants;
import com.eli.banknote.restApi.model.articlesList;
import com.eli.banknote.restApi.model.newsSourcesList;

import retrofit2.Call;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface GetNewsDataService {
    @GET("everything?"+ Constants.API_KEY)
    Call<articlesList> getEveryThing(@Query("q") String title);

    @GET("sources?"+Constants.API_KEY)
    Call<newsSourcesList> getSources();


    @Headers("Content-Type:application/x-www.form-urlencoded; charset=utf-8")
    @GET("top-headlines?"+Constants.API_KEY)
    Call<articlesList> getTopHeaadLines(@Query(value="sources",encoded = false) String source);
}