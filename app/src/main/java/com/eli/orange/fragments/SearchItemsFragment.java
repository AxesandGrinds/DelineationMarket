package com.eli.orange.fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eli.orange.R;
import com.eli.orange.fragments.allProducts.allContentAdapter;
import com.eli.orange.models.Upload;
import com.eli.orange.utils.Constants;
import com.eli.orange.utils.MyDividerItemDecoration;
import com.eli.orange.utils.SharedPreferencesManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchItemsFragment extends Fragment {
    private View view;
    @BindView(R.id.content_recycler)
    RecyclerView recyclerView;
    private Upload upload;
    @BindView(R.id.searchView)
    SearchView searchView;


    private FirebaseAuth auth;
    private FirebaseDatabase mFirebaseInstance;
    private List<Upload> uploads = new ArrayList<>();
    private allContentAdapter adapter;
    private String LOCATION_NAME;
    //Data variables
    private Double longitud, latitud;

    public SearchItemsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_searchitems, container, false);
        ButterKnife.bind(this, view);
        view.setVisibility(View.GONE);




        searchView.setQueryHint("Search by Product name");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                adapter.getFilter().filter(query);
                return false;
            }
        });

        mFirebaseInstance = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        LOCATION_NAME = new SharedPreferencesManager(getContext()).getString(SharedPreferencesManager.Key.USER_LOCATION_NAME);


        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new MyDividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL, 36));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        uploads = new ArrayList<>();
        adapter = new allContentAdapter(getContext(), uploads);

        getLocationData();

        return view;

    }


    public void getLocationData() {

        DatabaseReference uploadDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);

        uploadDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnap) {

                if (dataSnap.exists()) {
                    uploads.clear();

                    for (DataSnapshot datasnap : dataSnap.getChildren()) {
                        Log.d("SEC CHLD DATA", datasnap.getKey());
                        if (datasnap.exists()) {
                            for (DataSnapshot nsnap : datasnap.getChildren()) {

                                Log.d("THIRD CHLD DATA", nsnap.getValue(Upload.class).toString());

                                upload = nsnap.getValue(Upload.class);
                                upload.setLatitude(0.1);
                                upload.setLongitude(1.1);
                                upload.setUserKey(datasnap.getKey());
                                upload.setProductKey(datasnap.getKey());


                                uploads.add(upload);
                                adapter.addItems(uploads);
                                recyclerView.setAdapter(adapter);
                                view.setVisibility(View.VISIBLE);
                            }
                        }

                    }


                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

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
