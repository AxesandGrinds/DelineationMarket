package com.eli.orange.fragments.HomeFragment;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;
import android.util.Log;

import com.eli.orange.activity.MainActivity;
import com.eli.orange.models.Cities;
import com.eli.orange.models.MapData;
import com.eli.orange.models.UserData;
import com.eli.orange.other.MyClusterRenderer;
import com.eli.orange.room.database.DatabaseClient;
import com.eli.orange.room.entities.locationHistory;
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class homeFragmentPresenter {
    public  Context mCtx;
    private String dataId;
    private static final String TAG = (MainActivity.class).getSimpleName();
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private List<MapData> listData;
    private MyClusterRenderer renderer;

    public homeFragmentPresenter(Context mCtx) {
        this.mCtx = mCtx;

    }

    public interface View {

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
            LatLng latLng = new LatLng(lat, lng);
            list.add(new Cities(object.getString("country"), object.getString("name"), lat, lng));

        }
        return list;
    }

    public String getRegionName(double lati, double longi) {
        String regioName = "";
        Geocoder gcd = new Geocoder(mCtx, Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(lati, longi, 1);
            if (addresses.size() > 0) {
                regioName = addresses.get(0).getLocality();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return regioName;


    }

    public void saveToLocalStorage(Marker marker) {
        Geocoder gcd = new Geocoder(mCtx, Locale.getDefault());
        try {
            List<Address> addresses = gcd.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
            if (addresses.size() > 0) {
                Address obj = addresses.get(0);
                //callFirebaseInstance();
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

                //createItem(obj.getCountryCode(), marker.getPosition().latitude, marker.getPosition().longitude, obj.getSubAdminArea());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creating new user node under 'users'
     */
    private void createItem(String countryCode, Double lat, Double lng, String id) {
        // TODO
        // In real apps this userId should be fetched
        // by implementing firebase auth
        if (TextUtils.isEmpty(dataId)) {
            dataId = id;
        }
        MapData mapData = new MapData(countryCode, lat, lng);

        mFirebaseDatabase.child(id).setValue(mapData);
    }

    private void callFirebaseInstance() {
        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("places");

        // store app title to 'app_title' node
        mFirebaseInstance.getReference("app_title").setValue("Locations Data");

        // app_title change listener
        mFirebaseInstance.getReference("places").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                        MapData mapData = npsnapshot.getValue(MapData.class);
                        UserData userData = new UserData();
                        userData.setCountryCode(mapData.getCountryCode());
                        userData.setLat(mapData.getLat());
                        userData.setLng(mapData.getLng());


                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });
    }
    public void readMapData(ClusterManager mClusterManager, GoogleMap myMap) {


        mClusterManager = new ClusterManager<MyItem>(mCtx,myMap);
        renderer = new MyClusterRenderer(mCtx,myMap,mClusterManager);

        myMap.setOnCameraIdleListener(mClusterManager);
        myMap.setOnMarkerClickListener(mClusterManager);
        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("places");
        ArrayList<Cities> lists = new ArrayList<Cities>();
        ClusterManager finalMClusterManager = mClusterManager;
        mFirebaseInstance.getReference("places").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                        MapData mapData = npsnapshot.getValue(MapData.class);
                        MyItem offsetItem = new MyItem(mapData.getLat(), mapData.getLng(),mapData.countryCode,"Snipet");

                        renderer.setMinClusterSize(0);
                        finalMClusterManager.setRenderer(renderer);
                        finalMClusterManager.addItem(offsetItem);
                        finalMClusterManager.setAnimation(false);
                        finalMClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
                            @Override
                            public boolean onClusterClick(Cluster<MyItem> cluster) {
                                return false;
                            }
                        });
                        finalMClusterManager.cluster();

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });
    }

}
