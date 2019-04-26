package com.eli.orange.fragments;


import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.eli.orange.R;
import com.eli.orange.adapter.LocationsAdapter;
import com.eli.orange.models.Cities;
import com.eli.orange.models.MapData;
import com.eli.orange.room.entities.locationHistory;
import com.eli.orange.room.model.roomViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.android.volley.VolleyLog.TAG;


/**
 * A simple {@link HistoryFragment} subclass.
 */
public class HistoryFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private roomViewModel viewModel;
    private LocationsAdapter recyclerViewAdapter;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    List<MapData> mapData = new ArrayList<>();


    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_, container, false);
        recyclerView = view.findViewById(R.id.historyRecycler);
        /*viewModel = ViewModelProviders.of(this).get(roomViewModel.class);
        viewModel.getHistoryList().observe(this, new Observer<List<locationHistory>>() {
            @Override
            public void onChanged(@Nullable List<locationHistory> infoModels) {
                //recyclerViewAdapter.addItems(infoModels);
            }
        });*/

        mFirebaseInstance = FirebaseDatabase.getInstance();
        new fetchDataInbackground().execute();
        return view;
    }

    public class fetchDataInbackground extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            // get reference to 'users' node
            mFirebaseDatabase = mFirebaseInstance.getReference("places");
            ArrayList<Cities> lists = new ArrayList<Cities>();
            recyclerViewAdapter = new LocationsAdapter(getContext());
            mFirebaseInstance.getReference("places").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                            mapData.add(npsnapshot.getValue(MapData.class));
                            recyclerViewAdapter.addItems(mapData);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.e(TAG, "Failed to read app title value.", error.toException());
                }
            });
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            recyclerViewAdapter = new LocationsAdapter(getContext());
            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerViewAdapter.setOnItemClickListener(new LocationsAdapter.ClickListener() {
                @Override
                public void onItemClick(int position, View v) {
                    Log.d("MSG", "onItemClick position: " + position);
                    //Log.d("MSG", "onItemClick position: " + position + disaggrigationList.get(position).getDisag_value());
                }

                @Override
                public void onItemLongClick(int position, View v) {
                    Toast.makeText(getActivity(), "text", Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}
