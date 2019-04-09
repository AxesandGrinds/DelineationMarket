package com.eli.orange.adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eli.orange.R;
import com.eli.orange.activity.privacyPolicy.PrivacyPolicyActivity;
import com.eli.orange.room.entities.locationHistory;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CurrentNewsAdapter extends RecyclerView.Adapter <CurrentNewsAdapter.ViewHolder>{
    //List<Current> currentList = new ArrayList<>();
    Context context;
    LayoutInflater layoutInflater;
    private static ClickListener clickListener;

    private List<locationHistory> currentList;
    private View.OnLongClickListener longClickListener;
    private final LayoutInflater mInflater;

    public CurrentNewsAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.location_histories_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
       holder.addressLine.setText(currentList.get(position).getAddressline().toString().trim());
        holder.adminarea.setText(""+currentList.get(position).getAdminarea());
        holder.address.setText(currentList.get(position).getLongitude()+","+currentList.get(position).getLatitude());
        holder.adminarea.setText(currentList.get(position).getAdminarea());
        holder.subadminarea.setText(currentList.get(position).getSubadminarea());
        holder.countryname.setText(currentList.get(position).getCountryname()+"("+currentList.get(position).getCountrycode()+")");
        holder.locality.setText(currentList.get(position).getLocality());


    }

    @Override
    public int getItemCount() {
        if (currentList != null) {
            return currentList.size();
        }else {
        }
            return 0;
    }
    public void addItems(List<locationHistory> datasList) {
        this.currentList = datasList;
        notifyDataSetChanged();
    }


    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener,OnMapReadyCallback{
       @BindView(R.id.locAdressLine)
       TextView addressLine;
       @BindView(R.id.locAddress)
       TextView address;
       @BindView(R.id.locAdminArea)
       TextView adminarea;
       @BindView(R.id.locsubAdminArea)
       TextView subadminarea;
       @BindView(R.id.locCountryName)
       TextView countryname;
       @BindView(R.id.locLocality)
       TextView locality;
       @BindView(R.id.locMapView)
        MapView mapView;
       GoogleMap map;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnLongClickListener(this);
            itemView.setOnClickListener(this);


            if (mapView != null) {
                // Initialise the MapView
                mapView.onCreate(null);
                // Set the map ready callback to receive the GoogleMap object
                mapView.getMapAsync(this);
            }
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();

            clickListener.onItemClick(getAdapterPosition(), v);
            Context context = v.getContext();

            Intent intent1 = new Intent(context, PrivacyPolicyActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent1.putExtra("link","");

            context.startActivity(intent1);

        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            MapsInitializer.initialize(context);
            map = googleMap;
            setMapLocation();
        }

        private void setMapLocation() {
            int pos = getAdapterPosition();
            if (map == null) return;

            LatLng sydney = new LatLng(currentList.get(pos).getLatitude(), currentList.get(pos).getLongitude());
                        /*myMap.addMarker(new MarkerOptions().position(sydney)
                                .title(list.get(i).getCountryName()));*/

            map.addMarker(new MarkerOptions().position(sydney)
                    .title(currentList.get(pos).getAdminarea()));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 5));

            // Set the map type back to normal.
            map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }

        private void bindView(int pos) {
            setMapLocation();
        }
    }
    public void setOnItemClickListener(ClickListener clickListener) {
        CurrentNewsAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
        void onItemLongClick(int position, View v);
    }


}
