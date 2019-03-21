package com.eli.orange.fragments.HomeFragment;


import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.eli.orange.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;

import static android.widget.Toast.*;
import static com.eli.orange.utils.Constants.BASE_LAT_LANG;
import static com.eli.orange.utils.Constants.MAX_ZOOM_PREFFERENCE;
import static com.eli.orange.utils.Constants.MELBOURNE;
import static com.eli.orange.utils.Constants.MIN_ZOOM_PREFFERENCE;

/**
 * A simple {@link Fragment} subclass.
 */
public class homeFragment extends Fragment
        implements OnMapReadyCallback,
        GoogleMap.OnPoiClickListener, GoogleMap.OnInfoWindowClickListener {
    View view;
    Context context;
    private static final String TAG = "TAG";
    @BindView
   (R.id.map) MapView mapView;
    private GoogleMap googleMap;
    private MarkerOptions markerOptions;


    public homeFragment(Context context) {
        this.context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this,view);
        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);//
        return view;
    }


    @Override
    public void onMapReady(GoogleMap map) {
        map = map;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            context, R.raw.style_json));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        UiSettings uiSettings = map.getUiSettings();
        uiSettings.setCompassEnabled(false);
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMyLocationButtonEnabled(false);
        map.setOnInfoWindowClickListener(this::onInfoWindowClick);
        map.setOnPoiClickListener(this::onPoiClick);

        for(int i = 0; i<5;i++) {
            double offset = i+6;
            LatLng latlang = new LatLng(-0.1270060+offset,51.5145160+offset);
            markerOptions = new MarkerOptions();
            markerOptions.position(latlang)
                    .title("Business Name:"+i)
                    .snippet("Business name,descriptions.")
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));


        InfoWindowData info = new InfoWindowData();
        info.setImage("logoassets");
        info.setHotel("Hotel : excellent hotels available");
        info.setFood("Food : all types of restaurants available");
        info.setTransport("Reach the site by bus, car and train.");

        CustomInfoWindowGoogleMap customInfoWindow = new CustomInfoWindowGoogleMap(context);
        map.setInfoWindowAdapter(customInfoWindow);

        Marker m = map.addMarker(markerOptions);
        m.setTag(info);
        m.showInfoWindow();

        map.moveCamera(CameraUpdateFactory.newLatLng(BASE_LAT_LANG));
        }
    }


    @Override
    public void onPoiClick(PointOfInterest poi) {
        makeText(context, "Clicked: " +
                        poi.name + "\nPlace ID:" + poi.placeId +
                        "\nLatitude:" + poi.latLng.latitude +
                        " Longitude:" + poi.latLng.longitude,
                LENGTH_SHORT).show();

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(context, "Info window clicked at: "+marker.getPosition(), LENGTH_SHORT).show();
    }
}
