package com.eli.orange.fragments.addCenter;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.eli.orange.models.InfoWindowDatas;
import com.eli.orange.models.MapData;
import com.eli.orange.models.UserMapData;
import com.eli.orange.room.database.DatabaseClient;
import com.eli.orange.room.entities.userInfo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

public class AddCenterFragmentPresenter {
    Context context;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private UserMapData userMapData;

    public AddCenterFragmentPresenter(Context context){
        this.context = context;
    }

    private userInfo userInfo;



    public void saveToLocalStorage(String username,String businessname,double longitude, double latitude, String locationame){
        userInfo = new userInfo();
        userInfo.setUsername(username);
        userInfo.setBusinessname(businessname);
        userInfo.setLatitude(latitude);
        userInfo.setLongitude(longitude);
        userInfo.setLocationame(locationame);
        //adding to database
        DatabaseClient.getmInstance(context).getAppDatabase()
                .roomDao()
                .addInfo(userInfo);
    }
    public interface View{
        void saveToLocalStorage();

    }
    public void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
    public void createItem(@NonNull  InfoWindowDatas infoWindowDatas) {

        mFirebaseInstance = FirebaseDatabase.getInstance();
        mFirebaseDatabase =  mFirebaseInstance.getReference("places");


        mFirebaseDatabase.child(infoWindowDatas.getMCityName()).child(infoWindowDatas.getBusinessTYpe()).child(FirebaseAuth.getInstance().getUid()).setValue(infoWindowDatas).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(context,"Added Succesful",Toast.LENGTH_LONG).show();
            }
        });
    }

}
