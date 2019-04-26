package com.eli.orange.restApi.model;


import com.eli.orange.models.Cities;

import retrofit2.Call;
import retrofit2.http.GET;

import static com.eli.orange.utils.Constants.CITY_DATA_PATH;

public interface ApiInterface {


    @GET(CITY_DATA_PATH)
    Call<Cities> getData();

}
