package com.eli.orange.fragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.eli.orange.R;
import com.eli.orange.activity.LoginActivity;
import com.eli.orange.activity.uploadFiles.ImageUploadActivity;
import com.eli.orange.adapter.ContentsAdapter;
import com.eli.orange.adapter.UserUploadsContentsAdapter;
import com.eli.orange.models.Upload;
import com.eli.orange.utils.Constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class uploadsFragment extends Fragment {
    //0746 31 9332

    //adapter object
    private ContentsAdapter adapter;


    //database reference
    private DatabaseReference mDatabase;

    //firebase auth
    private FirebaseAuth firebaseAuth;

    //progress dialog
    private ProgressDialog progressDialog;

    //list to hold all the uploaded images
    private List<Upload> uploads;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.fabOpenUpload)
    FloatingActionButton openUpload;


    @BindView(R.id.fabOpenLocation)
    FloatingActionButton openLocation;

    private View view;

    public uploadsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.layout_images, container, false);
        ButterKnife.bind(this, view);

        openLocation.setVisibility(View.GONE);


        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ContentsAdapter(getContext());
        //new requestUploadedDataFromFireBaseRealTimeDatabase().execute();


        progressDialog = new ProgressDialog(getContext());

        uploads = new ArrayList<>();

        //displaying progress dialog while fetching images
        progressDialog.setMessage("Please wait...");
        progressDialog.show();
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getContext(), LoginActivity.class));
        } else {

            mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS).child(firebaseAuth.getCurrentUser().getUid());

            //adding an event listener to fetch values
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    if (snapshot.exists()) {
                        uploads.clear();

                        //iterating through all the values in database
                        for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                            Upload upload = postSnapshot.getValue(Upload.class);
                            uploads.add(upload);
                            adapter.addItems(uploads);

                        }

                        progressDialog.dismiss();
                        recyclerView.setAdapter(adapter);
                    } else {
                        showsnackbar("No data available...!!!");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    progressDialog.dismiss();
                }
            });
        }


        return view;

    }
    @OnClick(R.id.fabOpenUpload)
    void openuploaded(){
        getContext().startActivity( new Intent(getContext(), ImageUploadActivity.class));
    }

    void showsnackbar(String message) {
        progressDialog.dismiss();
        View parentLayout = getActivity().findViewById(android.R.id.content);
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
    class requestUploadedDataFromFireBaseRealTimeDatabase extends AsyncTask<Void, Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {



            return null;
        }

        @Override
        protected void onPreExecute() {
            //dismissing the progress dialog
            progressDialog.dismiss();
            recyclerView.setAdapter(adapter);
        }
    }


}
