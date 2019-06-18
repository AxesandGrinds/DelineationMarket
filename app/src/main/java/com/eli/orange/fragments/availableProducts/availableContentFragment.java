package com.eli.orange.fragments.availableProducts;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.eli.orange.R;
import com.eli.orange.adapter.PagerAdapter;
import com.eli.orange.adapter.UserUploadsContentsAdapter;
import com.eli.orange.adapter.availableContentAdapter;
import com.eli.orange.fragments.LocationDataFragment;
import com.eli.orange.fragments.addCenter.AddCenterFragment;
import com.eli.orange.models.InfoWindowDatas;
import com.eli.orange.models.Upload;
import com.eli.orange.utils.Constants;
import com.eli.orange.utils.SharedPreferencesManager;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class availableContentFragment extends Fragment {
    private View view;
    @BindView(R.id.chipGroup)
    ChipGroup entryChipGroup;
    @BindView(R.id.content_recycler)
    RecyclerView recyclerView;
    private Upload upload;
    private String USER_KEY;


    private FirebaseAuth auth;
    private FirebaseDatabase mFirebaseInstance;
    private List<Upload> uploads;
    private availableContentAdapter adapter;
    private String LOCATION_NAME;
    //Data variables
    private Double longitud, latitud;
    public availableContentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout._available_content, container, false);
        ButterKnife.bind(this, view);
        view.setVisibility(View.GONE);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        LOCATION_NAME = new SharedPreferencesManager(getContext()).getString(SharedPreferencesManager.Key.USER_LOCATION_NAME);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        uploads = new ArrayList<>();
        adapter = new availableContentAdapter(getContext());

        getData();
        getLocationData("food and drinks");

        return view;

    }


    private Chip getChip(final ChipGroup entryChipGroup, String text, Context context, int id) {
        Chip chip = new Chip(context);
        chip.setChipIconTintResource(R.color.primaryColor);
        chip.setChipStrokeColorResource(R.color.secondaryDarkColor);
        /*chip.setChipDrawable(
                ChipDrawable.createFromResource(context, R.xml.chip)
        );*/

        int paddingDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10,
                getResources().getDisplayMetrics()
        );
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp);
        chip.setText(text);
        chip.setId(id);
        chip.setTextColor(getResources().getColor(R.color.white));
        chip.setChipStrokeColorResource(R.color.white);
        chip.setChipBackgroundColorResource(R.color.blue);
        chip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getContext(), chip.getText(),Toast.LENGTH_LONG).show();
                getLocationData(chip.getText().toString());

            }
        });

        return chip;
    }

    private void getData() {

        if (auth.getCurrentUser() != null) {
            mFirebaseInstance.getReference(Constants.DATABASE_PATH_PLACES).child(LOCATION_NAME).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {

                        InfoWindowDatas data = dataSnapshot.getValue(InfoWindowDatas.class);
                        latitud = data.getMLocationLatitude();
                        longitud = data.getMLocationLongitude();

                        //Log.d("DATAS","latitude:"+latitud+ ","+"longitude"+longitud);

                        for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                            Chip entryChip = getChip(entryChipGroup, npsnapshot.getKey(), getContext(), 1);
                            entryChipGroup.addView(entryChip);
                            //getLocationData(npsnapshot.getKey());
                            /*for (DataSnapshot childsnapshot: npsnapshot.getChildren()){

                                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS).child(childsnapshot.getKey());

                                mDatabase.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChildren()) {



                                            uploads.clear();
                                            for (DataSnapshot userdata: dataSnapshot.getChildren()) {

                                                Upload upload = userdata.getValue(Upload.class);
                                                uploads.add(upload);
                                                adapter.addItems(uploads);

                                                recyclerView.setAdapter(adapter);
                                                view.setVisibility(View.VISIBLE);
                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                        showsnackbar("Request Cancelled...");

                                    }
                                });
                            }*/
                        }


                    }else {
                        displayToast("No Data Available in: "+new SharedPreferencesManager(getContext()).getString(SharedPreferencesManager.Key.USER_LOCATION_NAME));
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.e(Constants.TAG, "Failed to read app title value.", error.toException());
                }

            });
        }
    }

    public void getLocationData(String queryString) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_PLACES).child(LOCATION_NAME).child(queryString);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    uploads.clear();
                    if (dataSnapshot.hasChildren()) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            Log.d("FIRTS CHIRLD KEY", childSnapshot.getKey());
                            USER_KEY = childSnapshot.getKey();

                            DatabaseReference uploadDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS).child(USER_KEY);

                            uploadDatabase.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnap) {

                                    // Log.d("SECOND CHLD DATA", dataSnap.toString());

                                    if (dataSnap.exists()) {

                                        InfoWindowDatas data = childSnapshot.getValue(InfoWindowDatas.class);
                                        latitud = data.getMLocationLatitude();
                                        longitud = data.getMLocationLongitude();


                                        uploads.clear();
                                        for (DataSnapshot datasnap : dataSnap.getChildren()) {

                                            //Log.d("SECOND CHLD DATA", datasnap.toString());
                                            upload = datasnap.getValue(Upload.class);
                                            upload.setLatitude(latitud);
                                            upload.setLongitude(longitud);
                                            upload.setUserKey(USER_KEY);
                                            upload.setProductKey(datasnap.getKey());


                                            uploads.add(upload);
                                            adapter.addItems(uploads);
                                            recyclerView.setAdapter(adapter);
                                            view.setVisibility(View.VISIBLE);


                                            //Log.d("AFTER LATLANG SET", upload.toString());

                                        }
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                showsnackbar(databaseError.toException().toString());
            }
        });

    }
    void displayToast(String message) {
        View toastLayout = LayoutInflater.from(getContext()).inflate(R.layout.toast_layout, null);
        TextView text = toastLayout.findViewById(R.id.textView);
        text.setText(message);

        Toast toast = new Toast(getContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(toastLayout);
        toast.show();
    }


    void showsnackbar(String message) {
        View parentLayout = view.findViewById(android.R.id.content);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light))
                .show();
        //Other stuff in
    }

}
