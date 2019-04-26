package com.eli.orange.fragments.HomeFragment;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.eli.orange.R;
import com.eli.orange.activity.MainActivity;
import com.eli.orange.fragments.BaseFragmentActivity;
import com.eli.orange.restApi.model.ApiInterface;
import com.eli.orange.models.Cities;
import com.eli.orange.utils.PermissionAccessManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;

import static android.content.Context.LOCATION_SERVICE;
import static android.widget.Toast.*;
import static com.eli.orange.utils.Constants.TAG_CODE_PERMISSION_LOCATION;

/**
 * A simple {@link Fragment} subclass.
 */
public class homeFragment extends BaseFragmentActivity implements
        LocationListener,homeFragmentPresenter.View, GoogleMap.OnInfoWindowLongClickListener {
    View view;
    private static final String TAG = "TAG";
    private GoogleMap myMap;
    private MarkerOptions markerOptions;
    ApiInterface apiInterface;
    private ProgressDialog myProgress;
    private PermissionAccessManager permissionAccessManager;
    private homeFragmentPresenter presenter;
    private List<Cities> list = null;
    private ClusterManager<MyItem> mClusterManager;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseAuth auth;


    public homeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);


        ButterKnife.bind(this, view);
        permissionAccessManager = new PermissionAccessManager(getContext());
        presenter = new homeFragmentPresenter(getContext());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
        auth = FirebaseAuth.getInstance();

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "id");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "name");
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "image");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        showProgressBar();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapfragmet);

        // Set callback listener, on Google MapData ready.
        mapFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {
                onMyMapReady(googleMap);
            }
        });

        return view;
    }

    private void onMyMapReady(GoogleMap googleMap) {
        // Get Google MapData from Fragment.
        myMap = googleMap;
        myMap.setOnInfoWindowClickListener(this::onInfoWindowLongClick);
        // Get the data: latitude/longitude positions of police stations.
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = myMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getContext(), R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        // Sét OnMapLoadedCallback Listener.
        myMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {

            @Override
            public void onMapLoaded() {
                // MapData loaded. Dismiss this dialog, removing it from the screen.
                hideProgressBar();
                if (auth.getCurrentUser() != null) {
                    presenter.readMapData(mClusterManager, myMap);
                }else



                permissionAccessManager.askLocationPermission();
            }
        });

        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            myMap.setMyLocationEnabled(true);
            myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            myMap.getUiSettings().setZoomControlsEnabled(true);
            myMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {

            ActivityCompat.requestPermissions(getActivity(), new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION },
                TAG_CODE_PERMISSION_LOCATION);
        }
    }


    // Call this method only when you have the permissions to view a user's location.
    public void showMyLocation() {

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        String locationProvider = permissionAccessManager.getEnabledLocationProvider();

        if (locationProvider == null) {
            return;
        }

        // Millisecond
        final long MIN_TIME_BW_UPDATES = 1000;
        // Met
        final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;

        Location myLocation = null;
        try {
            // This code need permissions (Asked above ***)
            locationManager.requestLocationUpdates(
                    locationProvider,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);
            // Getting Location.
            myLocation = locationManager
                    .getLastKnownLocation(locationProvider);
        }
        // With Android API >= 23, need to catch SecurityException.
        catch (SecurityException e) {
            Toast.makeText(getContext(), "Show My Location Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            Log.e(TAG, "Show My Location Error:" + e.getMessage());
            e.printStackTrace();
            return;
        }

        if (myLocation != null) {

            LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)             // Sets the center of the map to location user
                    .zoom(15)                   // Sets the zoom
                    .bearing(90)                // Sets the orientation of the camera to east
                    .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            // Add Marker to MapData
            MarkerOptions option = new MarkerOptions();
            option.title("My Location");
            option.snippet("....");
            option.position(latLng);
            Marker currentMarker = myMap.addMarker(option);
            currentMarker.showInfoWindow();
        } else {
            Toast.makeText(getContext(), "Location not found!", Toast.LENGTH_LONG).show();
            Log.i(TAG, "Location not found");
        }


    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    @Override
    public void showProgressBar() {

        // Create Progress Bar.
        myProgress = new ProgressDialog(getContext());
        myProgress.setTitle("MapData Loading ...");
        myProgress.setMessage("Please wait...");
        myProgress.setCancelable(true);
        // Display Progress Bar.
        myProgress.show();

    }

    @Override
    public void hideProgressBar() {
        myProgress.dismiss();

    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {
        Toast.makeText(getContext(), presenter.getRegionName(marker.getPosition().latitude,marker.getPosition().longitude),LENGTH_LONG).show();
        presenter.saveToLocalStorage(marker);
    }
}

