package com.eli.orange.fragments.HomeFragment;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.eli.orange.R;
import com.eli.orange.models.InfoWindowDatas;
import com.eli.orange.models.MyItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class CustomInfoWindowGoogleMap implements GoogleMap.InfoWindowAdapter {

    private Context context;
    @BindView(R.id.txtLocMarkerName)
    TextView txtMarkerName;
    @BindView(R.id.txtLocMarkerEmail)
    TextView txtEmailAddress;
    @BindView(R.id.txtLocMarkerPhone)
    TextView txtPhoneNumber;
    @BindView(R.id.txtOpenningHoursValue)
    TextView txtOpeningHours;

    public CustomInfoWindowGoogleMap(Context ctx){
        context = ctx;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View view = ((Activity)context).getLayoutInflater()
                .inflate(R.layout.custom_info_window, null);
        ButterKnife.bind(this,view);


        InfoWindowDatas infoWindowData = (InfoWindowDatas)marker.getTag();

        if (infoWindowData != null) {

            txtMarkerName.setText(infoWindowData.getMBusinessName());
            txtEmailAddress.setText(infoWindowData.getMLocationEmail());
            txtPhoneNumber.setText(infoWindowData.getMLocationPhone());
            txtOpeningHours.setText(infoWindowData.getMOpenignHours() + " to " + infoWindowData.getMClossingHours());
        }

        return view;
    }


}