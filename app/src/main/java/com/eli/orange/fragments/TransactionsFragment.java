package com.eli.orange.fragments;


import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.eli.orange.R;
import com.eli.orange.activity.MainActivity;
import com.eli.orange.activity.SignUpActivity;
import com.eli.orange.models.MapData;
import com.eli.orange.utils.Constants;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransactionsFragment extends Fragment {
    private View view;
    @BindView(R.id.lacationName)
    TextInputEditText locationName;
    @BindView(R.id.lacationLatitude)
    TextInputEditText locationLat;
    @BindView(R.id.lacationLongitude)
    TextInputEditText locationLong;
    @BindView(R.id.lacationSave)
    Button saveBUtton;
    private String userId;
    @BindView(R.id.textDetails)
    TextView txtDetails;

    private static final String TAG = (MainActivity.class).getSimpleName();
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private FirebaseAuth auth;
    public TransactionsFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_movies, container, false);
        ButterKnife.bind(this,view);

        mFirebaseInstance = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();

        // get reference to 'users' node
        mFirebaseDatabase = mFirebaseInstance.getReference(Constants.DATABASE_PATH_PLACES);

        // store app title to 'app_title' node
        mFirebaseInstance.getReference("app_title").setValue("Locations Data");

        // app_title change listener
        mFirebaseInstance.getReference("app_title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e(TAG, "App title updated");

                String appTitle = dataSnapshot.getValue(String.class);


            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read app title value.", error.toException());
            }
        });

        saveBUtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = locationName.getText().toString();
                Double langitude = Double.valueOf(String.valueOf(locationLong.getText()));
                Double latitude = Double.valueOf(String.valueOf(locationLat.getText()));

                // Check for already existed userId
                if (TextUtils.isEmpty(userId)) {
                    createUser(name, langitude, latitude);
                } else {
                    updateUser(name, langitude,latitude);
                }
            }
        });

        toggleButton();

        return view;
    }


    /**
     * Creating new user node under 'users'
     */
    private void createUser(String name, Double  lng, Double lat) {
        // TODO
        // In real apps this userId should be fetched
        // by implementing firebase auth
        if (TextUtils.isEmpty(userId)) {
            userId = mFirebaseDatabase.push().getKey();
        }

        MapData user = new MapData(name, lat,lng);

        mFirebaseDatabase.child(auth.getUid()).child(Constants.DATABASE_PATH_PLACES).setValue(user);

        addUserChangeListener();
    }

    /**
     * User data change listener
     */
    private void addUserChangeListener() {
        // User data change listener
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                MapData user = dataSnapshot.getValue(MapData.class);

                // Check for null
                if (user == null) {
                    Log.e(TAG, "User data is null!");
                    return;
                }

                Log.e(TAG, "User data is changed!" + user.countryCode + ", " + user.lat + ", " +user.lng);

                // Display newly updated name and email
                txtDetails.setText(user.countryCode + ", " + user.lat);

                // clear edit text
                locationLat.setText("");
                locationLong.setText("");
                locationName.setText("");

                toggleButton();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e(TAG, "Failed to read user", error.toException());
            }
        });
    }

    private void updateUser(String name, Double lng, Double lat) {
        // updating the user via child nodes
        if (!TextUtils.isEmpty(name))
            mFirebaseDatabase.child(userId).child("name").setValue(name);

        if (!lng.isNaN())
            mFirebaseDatabase.child(userId).child("lng").setValue(lng);
        if (!lat.isNaN())
            mFirebaseDatabase.child(userId).child("lat").setValue(lat);
    }

    // Changing button text
    private void toggleButton() {
        if (TextUtils.isEmpty(userId)) {
            saveBUtton.setText("Save");
        } else {
            saveBUtton.setText("Update");
        }
    }

    @OnClick(R.id.register)
     void register(){
        getContext().startActivity(new Intent(getActivity(), SignUpActivity.class));
    }



}
