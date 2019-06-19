package com.eli.orange.activity;


import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.eli.orange.R;
import com.eli.orange.activity.BaseActivity;
import com.eli.orange.adapter.LicencesRecyclerAdapter;
import com.eli.orange.adapter.LocationsAdapter;
import com.eli.orange.models.Licence;
import com.eli.orange.utils.Constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class Licences extends BaseActivity {
    private View view;
    @BindView(R.id.licencesRecyclerView)
    RecyclerView licenceRecycler;
    private LicencesRecyclerAdapter recyclerAdapter;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;
    private List<Licence> licenceList = new ArrayList<>();


    public Licences() {
        // Required empty public constructor
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_licences);
        ButterKnife.bind(this);
        mFirebaseInstance = FirebaseDatabase.getInstance();
        new backgroundTask().execute();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Licences");
        }

    }

    public class backgroundTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            mFirebaseInstance.getReference(Constants.DATABASE_PATH_LICENCES).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        licenceList.clear();
                        for(DataSnapshot nDataSnapshot: dataSnapshot.getChildren()){
                            licenceList.add(nDataSnapshot.getValue(Licence.class));
                            if (licenceList!=null) {
                                recyclerAdapter.addItems(licenceList);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getBaseContext(),"Request Canceelled",Toast.LENGTH_LONG).show();

                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            recyclerAdapter = new LicencesRecyclerAdapter(getBaseContext());
            licenceRecycler.setAdapter(recyclerAdapter);
            licenceRecycler.setLayoutManager(new LinearLayoutManager(getBaseContext()));
            recyclerAdapter.setOnItemClickListener(new LocationsAdapter.ClickListener() {
                @Override
                public void onItemClick(int position, View v) {

                }

                @Override
                public void onItemLongClick(int position, View v) {

                }
            });

        }
    }

}
