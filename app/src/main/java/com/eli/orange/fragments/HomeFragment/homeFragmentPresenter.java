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

import static android.widget.Toast.LENGTH_LONG;

public class homeFragmentPresenter {
    public  Context mCtx;
    private String dataId;
    private static final String TAG = (MainActivity.class).getSimpleName();
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private List<MapData> listData;
    private MyClusterRenderer renderer;
    private UserData userData;
    private CustomInfoWindowGoogleMap customInfoWindow;

    public homeFragmentPresenter(Context mCtx) {
        this.mCtx = mCtx;

    }

    public interface View {

        void showProgressBar();

        void hideProgressBar();

    }

    public void readMapData(ClusterManager mClusterManager, GoogleMap myMap) {


        mClusterManager = new ClusterManager<MyItem>(mCtx,myMap);
        renderer = new MyClusterRenderer(mCtx,myMap,mClusterManager);

        myMap.setOnCameraIdleListener(mClusterManager);
        myMap.setOnMarkerClickListener(mClusterManager);
        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference("places");
        ClusterManager finalMClusterManager = mClusterManager;
        finalMClusterManager.getClusterMarkerCollection().setOnInfoWindowAdapter(new CustomInfoWindowGoogleMap(mCtx));
        myMap.setInfoWindowAdapter(new CustomInfoWindowGoogleMap(mCtx));
        mFirebaseInstance.getReference("places").child("Dar es Salaam").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                        UserMapData mapData = npsnapshot.getValue(UserMapData.class);
                        InfoWindowData infoWindowData = new InfoWindowData("snmas","ashdgaj","sashdAKHSDK","SAKJshka");
                        MyItem offsetItem = new MyItem(mapData.getLat(), mapData.getLng(),mapData.getBusinessType(),mapData.getUsername(),npsnapshot.getKey());
                        //renderer.setMinClusterSize(0);
                        //finalMClusterManager.setRenderer(renderer);
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
