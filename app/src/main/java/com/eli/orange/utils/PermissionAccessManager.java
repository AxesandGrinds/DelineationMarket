package com.eli.orange.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.eli.orange.fragments.HomeFragment.homeFragment;
import com.google.android.gms.location.LocationListener;

import java.security.Permission;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static com.eli.orange.utils.Constants.REQUEST_ID_ACCESS_COURSE_FINE_LOCATION;

public class PermissionAccessManager extends AppCompatActivity implements LocationListener {
    Context context;
    public PermissionAccessManager(Context context){
        this.context = context;

    }

    homeFragment fragment = new homeFragment();


    public void askLocationPermission(){
        if(Build.VERSION.SDK_INT>=23){
            int accessCoursePermission = ContextCompat.checkSelfPermission((Activity)context, Manifest.permission.ACCESS_COARSE_LOCATION);
            int accessFinePermission = ContextCompat.checkSelfPermission((Activity)context,Manifest.permission.ACCESS_FINE_LOCATION);

            if (accessCoursePermission != PackageManager.PERMISSION_GRANTED
            || accessFinePermission != PackageManager.PERMISSION_GRANTED){
                String permissions [] = new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                };
                // Show a dialog asking the user to allow the above permissions.
                ActivityCompat.requestPermissions((Activity) context, permissions,
                        REQUEST_ID_ACCESS_COURSE_FINE_LOCATION);

                return;
            }
        }
    }

    // When you have the request results.
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //
        switch (requestCode) {
            case REQUEST_ID_ACCESS_COURSE_FINE_LOCATION: {

                // Note: If request is cancelled, the result arrays are empty.
                // Permissions granted (read/write).
                if (grantResults.length > 1
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "Permission granted!", Toast.LENGTH_LONG).show();

                    // Show current location on MapData.
                    fragment.showMyLocation();
                }
                // Cancelled or denied.
                else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    // Find Location provider is openning.
    public String getEnabledLocationProvider() {
        LocationManager locationManager = (LocationManager)context.getSystemService(LOCATION_SERVICE);

        // Criteria to find location provider.
        Criteria criteria = new Criteria();

        // Returns the name of the provider that best meets the given criteria.
        // ==> "gps", "network",...
        String bestProvider = locationManager.getBestProvider(criteria, true);

        boolean enabled = locationManager.isProviderEnabled(bestProvider);

        if (!enabled) {
            Toast.makeText(context, "No location provider enabled!", Toast.LENGTH_LONG).show();
            Log.i(Constants.TAG, "No location provider enabled!");
            return null;
        }
        return bestProvider;
    }

}
