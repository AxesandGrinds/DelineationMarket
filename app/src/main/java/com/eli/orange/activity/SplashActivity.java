package com.eli.orange.activity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.eli.orange.utils.SharedPreferencesManager;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new SharedPreferencesManager(this).put(SharedPreferencesManager.Key.USER_LOCATION_NAME,getUserLocationName(new LatLng(-6.7774105,39.2408909)));


        new Handler().postDelayed(new Runnable() {


            @Override
            public void run() {
                // This method will be executed once the timer is over
                Intent i = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, 10);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    public String getUserLocationName(LatLng latLng){
        String localityString = null;
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude,latLng.longitude,1);
            if (geocoder.isPresent()) {
                StringBuilder stringBuilder = new StringBuilder();
                if (addresses.size()>0) {
                    Address returnAddress = addresses.get(0);

                    localityString = returnAddress.getLocality();
                }
            } else {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return localityString;
    }
}
