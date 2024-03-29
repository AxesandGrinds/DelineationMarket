package com.eli.orange.fragments.HomeFragment;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProviders;

import butterknife.ButterKnife;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.eli.orange.R;
import com.eli.orange.activity.MainActivity;
import com.eli.orange.fragments.BaseFragmentActivity;
import com.eli.orange.fragments.GetUserUploadsFragment;
import com.eli.orange.fragments.uploadsFragment;
import com.eli.orange.models.InfoWindowDatas;
import com.eli.orange.models.MyItem;
import com.eli.orange.models.UserMapData;
import com.eli.orange.restApi.model.ApiInterface;
import com.eli.orange.models.Cities;
import com.eli.orange.utils.Constants;
import com.eli.orange.utils.PermissionAccessManager;
import com.eli.orange.utils.SharedPreferencesManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.io.IOException;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;
import static android.widget.Toast.*;
import static com.eli.orange.utils.Constants.TAG_CODE_PERMISSION_LOCATION;

/**
 * A simple {@link Fragment} subclass.
 */
public class homeFragment extends BaseFragmentActivity implements
        LocationListener, homeFragmentPresenter.View, GoogleMap.OnInfoWindowLongClickListener {
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
    private FirebaseDatabase mFirebaseInstance;
    private InfoWindowDatas infoWindowDatas;
    private CustomInfoWindowGoogleMap customInfoWindow;
    private Location userLocaction = null;
    private ViewModel mViewModel;
    private SharedPreferencesManager preferencesManager;
    private Geocoder geocoder;
    private List<Address> user = null;


    // Millisecond
    private final long MIN_TIME_BW_UPDATES = 1000;
    // Met
    private final float MIN_DISTANCE_CHANGE_FOR_UPDATES = 1;


    public homeFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        mViewModel = ViewModelProviders.of(this).get(HomeFragmentViewModel.class);

        ((HomeFragmentViewModel) mViewModel).displayData();

        preferencesManager = new SharedPreferencesManager(getContext());

        mFirebaseInstance = FirebaseDatabase.getInstance();

        // get reference to 'users' node
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
            boolean success = myMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.style_json));

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
                    mFirebaseInstance.getReference(Constants.DATABASE_PATH_PLACES)
                            .child(new SharedPreferencesManager(getContext()).getString(SharedPreferencesManager.Key.USER_LOCATION_NAME))
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        myMap.clear();
                                        for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                                            if (npsnapshot.hasChildren()) {
                                                for (DataSnapshot childSnapshot : npsnapshot.getChildren()) {


                                                    InfoWindowDatas data = childSnapshot.getValue(InfoWindowDatas.class);


                                                    infoWindowDatas = new InfoWindowDatas(childSnapshot.getKey(), data.getMBusinessName(), data.getMLocationEmail(), data.getMLocationPhone(),
                                                            data.getMOpenignHours(), data.getMClossingHours(), data.getBusinessTYpe(), data.getMCityName(), data.getMLocationLongitude(),
                                                            data.getMLocationLatitude());


                                                    LatLng markerPosition = new LatLng(data.getMLocationLatitude(), data.getMLocationLongitude());
                                                    MarkerOptions markerOptions = new MarkerOptions().position(markerPosition)
                                                            .title(infoWindowDatas.getMBusinessName());


                                                    Marker marker = myMap.addMarker(markerOptions);
                                                    customInfoWindow = new CustomInfoWindowGoogleMap(getActivity());
                                                    myMap.setInfoWindowAdapter(customInfoWindow);

                                                    marker.setTag(infoWindowDatas);
                                                    //marker.showInfoWindow();

                                                    CameraPosition cameraPosition = new CameraPosition.Builder()
                                                            .target(markerPosition)             // Sets the center of the map to location user
                                                            .zoom(15)                   // Sets the zoom
                                                            .build();

                                                    showMyLocation();
                                                    // Creates a CameraPosition from the builder
                                                    myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                                                    myMap.moveCamera(CameraUpdateFactory.newLatLng(markerPosition));


                                                }
                                            }


                                        }
                                    }
                                }


                                @Override
                                public void onCancelled(DatabaseError error) {
                                    // Failed to read value
                                    Log.e(TAG, "Failed to read app title value.", error.toException());
                                }

                            });
                } else {
                    displayToast("Un authorized entity");
                }

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

            ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    TAG_CODE_PERMISSION_LOCATION);
        }
    }

    public void displayPositionMarker(Double latitude, Double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        myMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title("Here")
                .snippet("....")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(latLng)             // Sets the center of the map to location user
                .zoom(15)                   // Sets the zoom
                .build();
        myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

    }

    // Call this method only when you have the permissions to view a user's location.
    public void showMyLocation() {

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        String locationProvider = permissionAccessManager.getEnabledLocationProvider();

        if (locationProvider == null) {
            return;
        }


        Location myLocation = null;
        try {
            // This code need permissions (Asked above ***)
            locationManager.requestLocationUpdates(
                    locationProvider,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);
            // Getting Location.
            myLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        // With Android API >= 23, need to catch SecurityException.
        catch (SecurityException e) {
            Toast.makeText(getContext(), "Show My Location Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
            return;
        }

        if (myLocation != null) {
            LatLng latLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
            presenter.saveLocationData(latLng);
            myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latLng)             // Sets the center of the map to location user
                    .zoom(15)                   // Sets the orientation of the camera to east
                    .build();                   // Creates a CameraPosition from the builder
            myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            // Add Marker to MapData
            MarkerOptions option = new MarkerOptions();
            option.title("My Location");
            option.snippet("....");
            option.position(latLng);
            Marker currentMarker = myMap.addMarker(option);
        } else {
            displayToast("Location not found!");

        }


    }


    @Override
    public void onLocationChanged(Location location) {
        /*String LOCATION_NAME = getUserLocationName(new LatLng(location.getLatitude(), location.getLongitude()));
        if (preferencesManager.getString(SharedPreferencesManager.Key.USER_LOCATION_NAME).equals(LOCATION_NAME)) {
            //displayToast("Same Location");
        } else {
            preferencesManager.put(SharedPreferencesManager.Key.USER_LOCATION_NAME, LOCATION_NAME);
            displayToast("Location Changed");
        }
        preferencesManager.put(SharedPreferencesManager.Key.USER_LOCATION_LATITUDE, location.getLatitude());
        preferencesManager.put(SharedPreferencesManager.Key.USER_LOCATION_LONGITUDE, location.getLongitude());*/


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
        myProgress.setCancelable(false);
        // Display Progress Bar.
        myProgress.show();

    }

    @Override
    public void hideProgressBar() {
        myProgress.dismiss();

    }

    public String getUserLocationName(LatLng latLng) {
        String localityString = null;
        geocoder = new Geocoder(getContext());
        try {
            user = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            Address address = user.get(0);

            Log.d("ADDRESS",address.toString());
            if (!address.getLocality().isEmpty()) {
                localityString = address.getLocality();
            } else {
                localityString = "Dar es Salaam";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return localityString;
    }

    @Override
    public void onInfoWindowLongClick(Marker marker) {

        InfoWindowDatas infoDatas = (InfoWindowDatas) marker.getTag();

        GetUserUploadsFragment uploadsFragment = new GetUserUploadsFragment();
        Bundle args = new Bundle();
        args.putString("USER_ID", infoDatas.getMUserId());
        args.putDouble("saddrlon", marker.getPosition().longitude);
        args.putDouble("daddrlon", myMap.getMyLocation().getLongitude());
        args.putDouble("saddrlat", marker.getPosition().latitude);
        args.putDouble("daddrlat", myMap.getMyLocation().getLatitude());
        uploadsFragment.setArguments(args);
        getFragmentManager().beginTransaction().replace(R.id.frame, uploadsFragment).commit();
        //((MainActivity) getActivity()).replaceFragments(uploadsFragment.getClass());
    }

    void displayToast(String message) {
        View toastLayout = LayoutInflater.from(getContext()).inflate(R.layout.toast_layout, null);
        TextView text = toastLayout.findViewById(R.id.textView);
        text.setText(message);

        Toast toast = new Toast(getContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(toastLayout);
        toast.setDuration(LENGTH_SHORT);
        toast.show();
    }
}

