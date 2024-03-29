package com.eli.orange.fragments.HomeFragment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import com.eli.orange.R;
import com.eli.orange.models.MyItem;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class MyClusterRenderer extends DefaultClusterRenderer<MyItem> {

    private Context context;
    private  IconGenerator mClusterIconGenerator=null;

    public MyClusterRenderer(Context context, GoogleMap map, ClusterManager<MyItem> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
        mClusterIconGenerator  = new IconGenerator(context);
    }



    @Override
    protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) {

        BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA);

        markerOptions.icon(markerDescriptor);
    }

    @Override
    protected void onClusterItemRendered(MyItem clusterItem, Marker marker) {
        super.onClusterItemRendered(clusterItem, marker);
    }

    @Override
    protected void onBeforeClusterRendered(Cluster<MyItem> cluster, MarkerOptions markerOptions) {
        final Drawable clusterIcon = context.getResources().getDrawable(R.drawable.map_marker);
        clusterIcon.setColorFilter(context.getResources().getColor(android.R.color.holo_red_dark), PorterDuff.Mode.SRC_ATOP);

        mClusterIconGenerator.setBackground(clusterIcon);

        //modify padding for one or two digit numbers
        if (cluster.getSize() < 10) {
            mClusterIconGenerator.setContentPadding(40, 20, 0, 0);
        } else {
            mClusterIconGenerator.setContentPadding(30, 20, 0, 0);
        }

        Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
    }
}