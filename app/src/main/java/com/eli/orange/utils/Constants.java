package com.eli.orange.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class Constants {
    public static final float MIN_ZOOM_PREFFERENCE = 6.0f;

    public static final float MAX_ZOOM_PREFFERENCE = 14.0f;

    public static final LatLngBounds LAT_LANG_BOUNDS= new LatLngBounds(
            new LatLng(-44, 113), new LatLng(-10, 154));

    public static final LatLng BASE_LAT_LANG = new LatLng(-6.7692302,39.1142027);

    public static final LatLng MELBOURNE = new LatLng(-37.81319, 144.96298);

    public static final String CITIES_URL = "http://newhabari.000webhostapp.com/";

    public static final String CITY_DATA_PATH = "cities.json";

    public static final int  REQUEST_ID_ACCESS_COURSE_FINE_LOCATION = 100;

    public static final String TAG = "TAG";

    public static final String PRIVACY_POLICY_URL = "https://thepolicies.blogspot.com/2019/03/privacy-policy-of-elirehema-elirehema.html";

}