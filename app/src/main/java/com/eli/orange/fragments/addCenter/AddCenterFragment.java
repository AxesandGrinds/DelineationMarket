package com.eli.orange.fragments.addCenter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eli.orange.R;
import com.eli.orange.fragments.addCenter.AddCenterFragmentPresenter;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddCenterFragment extends Fragment implements AddCenterFragmentPresenter.View{
    private View view;
    private Context context;
    @BindView(R.id.bottonSheetButton)
    Button saveButton;
    @BindView(R.id.edBottomSheetBusinessName)
    EditText businessname;
    @BindView(R.id.edBottomSheetLocatioName)
    TextView locationname;
    @BindView(R.id.edBottomSheetLongitude)
    TextView longLat;
    @BindView(R.id.edtBottonSheetUsername)
    EditText username;
    @BindView(R.id.edBottomSheetCountryName)
    TextView countryName;
    @BindView(R.id.edBottomSheetCityName)
    TextView citName;
    @BindView(R.id.bottomConstraintLayout)
    ConstraintLayout constraintLayout;
    @BindView(R.id.bottomTextViewNoData)
    TextView NoDataTextView;
    private LatLng latLng;
    private AddCenterFragmentPresenter presenter;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view ==null) {
            view = inflater.inflate(R.layout.fragment_add_center, container, false);

        }
        ButterKnife.bind(this, view);

        progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleSmall);
        presenter = new AddCenterFragmentPresenter(getContext());
        final LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);


        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            presenter.buildAlertMessageNoGps();
        } else {
            new callForBackgroundData().execute();
        }


        return view;
    }
    @OnClick(R.id.bottonSheetButton)
    public void saveitems() {
        presenter.createItem(username.getText().toString().trim(),businessname.getText().toString().trim(),latLng.latitude,latLng.longitude,citName.getText().toString().trim());
    }

    @Override
    public void saveToLocalStorage() {

    }

    @Override
    public void isvalidForm() {
        if (TextUtils.isEmpty(username.getText().toString().trim())) {
            username.setError("Username Required !!!");
            return;
        }

        if (username.getText().toString().length() < 3) {
            username.setError("Invalid Username");

            return;
        }

        if (TextUtils.isEmpty(businessname.getText().toString().trim())) {
            businessname.setError("Business Name Required!!!");
            return;
        }
        if (TextUtils.isEmpty(longLat.getText().toString().trim())) {
            longLat.setError("Longitude required!");

            return;
        }
    }
    public class callForBackgroundData extends AsyncTask<Void, Void,Void>{
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            callForData();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
    }

    void callForData() {
        progressBar.setVisibility(View.VISIBLE);

        Geocoder geocoder;
        String bestProvider;
        List<Address> user = null;
        double lat;
        double lng;

        LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        bestProvider = lm.getBestProvider(criteria, false);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


        }
        Location location = lm.getLastKnownLocation(bestProvider);


        if (location == null) {

            NoDataTextView.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Location Not found", Toast.LENGTH_LONG).show();
        } else {
            geocoder = new Geocoder(getContext());
            try {
                user = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                Address address = user.get(0);
                lat = (double) address.getLatitude();
                lng = (double) address.getLongitude();
                latLng = new LatLng(lat,lng);
                longLat.setText("Longitude&Latitude: " + lat + ", " + lng);
                if (!address.getAddressLine(0).isEmpty()) {
                    locationname.setText("Location: " + address.getAddressLine(0));
                    locationname.setVisibility(View.VISIBLE);
                }
                if (!address.getCountryName().isEmpty()) {
                    countryName.setText("Country Name: " + address.getCountryName() + " (" + address.getCountryCode() + ")");
                    countryName.setVisibility(View.VISIBLE);
                }
                if (!address.getLocality().isEmpty()) {
                    citName.setText("City: " + address.getLocality());
                    citName.setVisibility(View.VISIBLE);
                }
                constraintLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    private int checkSelfPermission(String accessFineLocation) {
        // For Android < Android M, self permissions are always granted.
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        }

        return result;
    }

}
