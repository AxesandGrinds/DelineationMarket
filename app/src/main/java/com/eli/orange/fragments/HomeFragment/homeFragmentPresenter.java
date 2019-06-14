package com.eli.orange.fragments.HomeFragment;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.eli.orange.activity.MainActivity;
import com.eli.orange.fragments.uploadsFragment;
import com.eli.orange.models.Cities;
import com.eli.orange.models.MapData;
import com.eli.orange.models.MyItem;
import com.eli.orange.models.UserData;
import com.eli.orange.models.UserMapData;
import com.eli.orange.room.database.DatabaseClient;
import com.eli.orange.room.entities.locationHistory;
import com.eli.orange.utils.SharedPreferencesManager;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import static android.widget.Toast.LENGTH_LONG;

public class homeFragmentPresenter {
    public Context mCtx;
    private SharedPreferencesManager sharedPreferencesManager;
    private String LOCATION_NAME;


    public homeFragmentPresenter(Context mCtx) {
        this.mCtx = mCtx;
        sharedPreferencesManager = new SharedPreferencesManager(mCtx);

    }

    public interface View {

        void showProgressBar();

        void hideProgressBar();

    }

    public void saveLocationData(LatLng latLng) {
        sharedPreferencesManager.put(SharedPreferencesManager.Key.USER_LOCATION_LATITUDE, latLng.latitude);
        sharedPreferencesManager.put(SharedPreferencesManager.Key.USER_LOCATION_LONGITUDE, latLng.longitude);

        getLocationName();
    }

    public void getLocationName() {
        Geocoder gcd = new Geocoder(mCtx, Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(
                    sharedPreferencesManager.getDouble(SharedPreferencesManager.Key.USER_LOCATION_LATITUDE),
                    sharedPreferencesManager.getDouble(SharedPreferencesManager.Key.USER_LOCATION_LONGITUDE), 1);

            if (addresses.size() > 0 ) {
                Address obj = addresses.get(0);
                LOCATION_NAME = addresses.get(0).getFeatureName();


                //Log.d("LOCATION DATA",addresses.toString());
                if (LOCATION_NAME != null){
                    saveLocationName(LOCATION_NAME);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void saveLocationName(String string){
        sharedPreferencesManager.put(SharedPreferencesManager.Key.USER_LOCATION_NAME, string);

    }
}
