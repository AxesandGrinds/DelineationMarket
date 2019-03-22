package com.eli.orange.fragments.HomeFragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.eli.orange.restApi.model.Cities;
import com.eli.orange.room.database.DatabaseClient;
import com.eli.orange.room.entities.locationHistory;
import com.eli.orange.room.entities.userInfo;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.maps.android.heatmaps.WeightedLatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class homeFragmentPresenter{
    Context mCtx;

    public homeFragmentPresenter(Context mCtx){
        this.mCtx = mCtx;

    }

    public interface View{

        void showProgressBar();
        void hideProgressBar();

    }
    public ArrayList<Cities> readItems(int resource) throws JSONException {
        ArrayList<Cities> list = new ArrayList<Cities>();
        InputStream inputStream = mCtx.getResources().openRawResource(resource);
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("lat");
            double lng = object.getDouble("lng");
            LatLng latLng = new LatLng(lat,lng);
            list.add(new Cities(object.getString("country"),object.getString("name"),lat,lng));
            //Log.d("","Lat: "+lat+"Lang: "+lng+"CountryName: "+object.getString("name"));
        }
        return list;
    }
    public  String getRegionName(double lati, double longi) {
        String regioName = "";
        Geocoder gcd = new Geocoder(mCtx, Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(lati, longi, 1);
            if (addresses.size() > 0) {
                regioName = addresses.get(0).getLocality();
                Log.d("Name", regioName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return regioName;


    }

    public void saveToLocalStorage(Marker marker)
    {
        Geocoder gcd = new Geocoder(mCtx, Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(marker.getPosition().latitude,marker.getPosition().longitude, 1);
            if (addresses.size() > 0) {
                Address obj = addresses.get(0);

                locationHistory location = new locationHistory();
                location.setAddressline(obj.getAddressLine(0));
                location.setAdminarea(obj.getAdminArea());
                location.setCountrycode(obj.getCountryCode());
                location.setCountryname(obj.getCountryName());
                location.setLocality(obj.getLocality());
                location.setPostalcode(obj.getPostalCode());
                location.setAdminarea(obj.getAdminArea());
                location.setSubadminarea(obj.getSubAdminArea());
                location.setSubThroughfare(obj.getSubThoroughfare());
                location.setLongitude(marker.getPosition().longitude);
                location.setLatitude(marker.getPosition().latitude);
        //adding to database
        DatabaseClient.getmInstance(mCtx).getAppDatabase()
                .roomDao()
                .addLocationHistory(location);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
