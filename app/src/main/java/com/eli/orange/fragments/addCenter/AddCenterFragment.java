package com.eli.orange.fragments.addCenter;


import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.eli.orange.R;
import com.eli.orange.activity.MainActivity;
import com.eli.orange.fragments.addCenter.AddCenterFragmentPresenter;
import com.eli.orange.models.InfoWindowDatas;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AddCenterFragment extends Fragment implements AddCenterFragmentPresenter.View {
    private View view;
    private Context context;
    @BindView(R.id.bottonSheetButton)
    Button saveButton;
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
    @BindView(R.id.business_option_spinner)
    Spinner spinner;
    @BindView(R.id.edtFromDatePicker)
    TextInputEditText edFromDate;
    @BindView(R.id.edtToDatePicker)
    TextInputEditText edToDate;
    @BindView(R.id.edtContactEmail)
    TextInputEditText edContactEmail;
    @BindView(R.id.edtContactPhone)
    TextInputEditText edContactPhone;

    private String BUSINESS_TYPE = null;
    private LatLng latLng;
    private AddCenterFragmentPresenter presenter;
    private ProgressBar progressBar;
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 0; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds
    private String CITY_NAME;

    private Geocoder geocoder;
    private String bestProvider;
    private List<Address> user = null;
    private double lat;
    private double lng;
    private Unbinder unbinder;
    final Calendar myCalendar = Calendar.getInstance();
    int hour = myCalendar.get(Calendar.HOUR_OF_DAY);
    int minute = myCalendar.get(Calendar.MINUTE);
    private InfoWindowDatas windowDatas;
    private FusedLocationProviderClient fusedLocationClient;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_center, container, false);
        unbinder = ButterKnife.bind(this, view);



        progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleSmall);
        presenter = new AddCenterFragmentPresenter(getContext());
        final LocationManager manager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.business_option_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        edFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                String AM_PM ;
                                if(hourOfDay < 12) {
                                    AM_PM = "AM";
                                } else {
                                    AM_PM = "PM";
                                }

                                edFromDate.setText(hourOfDay + ":" + minute+" "+AM_PM);
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });
        edToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                String AM_PM ;
                                if(hourOfDay < 12) {
                                    AM_PM = "AM";
                                } else {
                                    AM_PM = "PM";
                                }

                                edToDate.setText(hourOfDay + ":" + minute +" "+AM_PM);
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==0) {
                    ((TextView) spinner.getSelectedView()).setError("Please Select Business Type");
                    saveButton.setEnabled(false);
                }else
                    saveButton.setEnabled(true);
                    BUSINESS_TYPE = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ((TextView) spinner.getSelectedView()).setError("Please Select Business Type");
            }
        });


        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            presenter.buildAlertMessageNoGps();
        } else {
            callForData();
        }


        return view;
    }



    @OnClick(R.id.bottonSheetButton)
    public void saveitems() {
        if (TextUtils.isEmpty(username.getText().toString().trim())) {
            username.setError("Username Required !!!");
            return;
        }else if (username.getText().toString().length() < 3) {
            username.setError("Invalid Username");

            return;
        } else if (TextUtils.isEmpty(longLat.getText().toString().trim())) {
            longLat.setError("Longitude required!");

            return;
        }else if(TextUtils.isEmpty(edFromDate.getText().toString().trim())){
            edFromDate.setError(getResources().getString(R.string.opening_date_is_required));
            return;
        }else if(TextUtils.isEmpty(edToDate.getText().toString().trim())) {
            edToDate.setError(getResources().getString(R.string.closing_time_is_manadatory));
            return;
        }else if(TextUtils.isEmpty(edContactEmail.getText().toString().trim())){
            edContactEmail.setError(getResources().getString(R.string.email_is_required));
            return;
        }else if(TextUtils.isEmpty(edContactPhone.getText().toString().trim())){
            edContactPhone.setError(getResources().getString(R.string.phone_is_required));
            return;
        }else
            windowDatas = new InfoWindowDatas("",username.getText().toString().trim(),
                    edContactEmail.getText().toString().trim(),edContactPhone.getText().toString().trim(),
                    edFromDate.getText().toString().trim(),edToDate.getText().toString().trim(),BUSINESS_TYPE.toLowerCase(),
                    CITY_NAME.toString().trim(),latLng.longitude,latLng.latitude);
        presenter.createItem(windowDatas);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }



    void callForData() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener((Activity) getContext(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                updateUI(location);

            }
        });
    }


    private int checkSelfPermission(String accessFineLocation) {
        // For Android < Android M, self permissions are always granted.
        int result = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            result = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        }

        return result;
    }


    private class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
            //Toast.makeText(getActivity(), "Location Changed",   Toast.LENGTH_LONG).show();
            if(location != null) {
                //updateUI(location);
            }

        }

        public void onStatusChanged(String s, int i, Bundle b) {
            Toast.makeText(getContext(), "Provider status changed", Toast.LENGTH_LONG).show();
        }

        public void onProviderDisabled(String s) {
            Toast.makeText(getContext(), "Provider disabled by the user. GPS turned off", Toast.LENGTH_LONG).show();
        }

        public void onProviderEnabled(String s) {
            Toast.makeText(getContext(), "Provider enabled by the user. GPS turned on", Toast.LENGTH_LONG).show();
        }

    }
    private void updateUI(Location location){

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
                CITY_NAME = address.getLocality();
            }
            constraintLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }





}
