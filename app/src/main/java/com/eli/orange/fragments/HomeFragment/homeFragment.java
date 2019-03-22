package com.eli.orange.fragments.HomeFragment;


import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.eli.orange.R;
import com.eli.orange.fragments.BaseFragmentActivity;
import com.eli.orange.restApi.model.ApiClient;
import com.eli.orange.restApi.model.ApiInterface;
import com.eli.orange.restApi.model.Cities;
import com.eli.orange.utils.Constants;
import com.eli.orange.utils.PermissionAccessManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.gson.Gson;
import com.google.maps.android.heatmaps.WeightedLatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;
import static android.widget.Toast.*;
import static com.eli.orange.utils.Constants.BASE_LAT_LANG;
import static com.eli.orange.utils.Constants.MAX_ZOOM_PREFFERENCE;
import static com.eli.orange.utils.Constants.MELBOURNE;
import static com.eli.orange.utils.Constants.MIN_ZOOM_PREFFERENCE;
import static com.eli.orange.utils.Constants.REQUEST_ID_ACCESS_COURSE_FINE_LOCATION;

/**
 * A simple {@link Fragment} subclass.
 */
public class homeFragment extends BaseFragmentActivity implements
        LocationListener,homeFragmentPresenter.View, GoogleMap.OnInfoWindowClickListener {
    View view;
    Context context;
    private static final String TAG = "TAG";
    private GoogleMap myMap;
    private MarkerOptions markerOptions;
    ApiInterface apiInterface;
    private ProgressDialog myProgress;
    private PermissionAccessManager permissionAccessManager;
    private homeFragmentPresenter presenter;
    private List<Cities> list = null;

    public homeFragment(Context context) {
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        permissionAccessManager = new PermissionAccessManager(context);
        presenter = new homeFragmentPresenter(context);



        showProgressBar();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapfragmet);

        // Set callback listener, on Google Map ready.
        mapFragment.getMapAsync(new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {
                onMyMapReady(googleMap);
            }
        });

        return view;
    }

    private void onMyMapReady(GoogleMap googleMap) {
        // Get Google Map from Fragment.
        myMap = googleMap;
        myMap.setOnInfoWindowClickListener(this::onInfoWindowClick);
        // Get the data: latitude/longitude positions of police stations.
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = myMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            context, R.raw.style_json));

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
                // Map loaded. Dismiss this dialog, removing it from the screen.
                hideProgressBar();
                try {
                    list = presenter.readItems(R.raw.cities);
                    for (int i =0; i<list.size(); i++){
                        //Log.d("data",list.get(i).getCountry());
                        LatLng sydney = new LatLng(list.get(i).getLatitude(), list.get(i).getLongitude());
                        /*myMap.addMarker(new MarkerOptions().position(sydney)
                                .title(list.get(i).getCountryName()));*/

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(sydney)
                                .title(list.get(i).getCountry())
                                .snippet(list.get(i).getCountryName());


                        InfoWindowData info = new InfoWindowData();
                        info.setImage("logoassets");
                        info.setHotel("Tanzania"+"("+list.get(i).getCountry()+")");
                        info.setFood("Food : all types of restaurants available");
                        info.setTransport("Reach the site by bus, car and train.");

                        CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(context);
                        myMap.setInfoWindowAdapter(customInfoWindow);

                        Marker m = myMap.addMarker(markerOptions);
                        m.setTag(info);
                        m.showInfoWindow();

                    }
                } catch (JSONException e) {
                    Toast.makeText(context, "Problem reading list of locations.", Toast.LENGTH_LONG).show();
                }



                permissionAccessManager.askLocationPermission();
            }
        });
        myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        myMap.getUiSettings().setZoomControlsEnabled(true);
        myMap.setMyLocationEnabled(true);
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
            Toast.makeText(context, "Show My Location Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
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
            // Add Marker to Map
            MarkerOptions option = new MarkerOptions();
            option.title("My Location");
            option.snippet("....");
            option.position(latLng);
            Marker currentMarker = myMap.addMarker(option);
            currentMarker.showInfoWindow();
        } else {
            Toast.makeText(context, "Location not found!", Toast.LENGTH_LONG).show();
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
        myProgress = new ProgressDialog(context);
        myProgress.setTitle("Map Loading ...");
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
    public void onInfoWindowClick(Marker marker) {

        Toast.makeText(context, presenter.getRegionName(marker.getPosition().latitude,marker.getPosition().longitude),LENGTH_LONG).show();
        presenter.saveToLocalStorage(marker);

    }
}

