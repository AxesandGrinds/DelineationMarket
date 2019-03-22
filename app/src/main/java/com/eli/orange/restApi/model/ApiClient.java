package com.eli.orange.restApi.model;


import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.eli.orange.utils.Constants.CITIES_URL;

public class ApiClient {

    private static Retrofit retrofit;
    private static OkHttpClient client;


    public static Retrofit getInstance() {
        if (retrofit == null) {

            retrofit = new Retrofit.Builder().baseUrl(CITIES_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;


    }
}
