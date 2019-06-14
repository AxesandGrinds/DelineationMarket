package com.eli.orange.fragments.HomeFragment;

import android.app.Application;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;

import com.eli.orange.R;
import com.eli.orange.utils.Constants;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeFragmentViewModel extends AndroidViewModel {
    private String TAG = getApplication().getClass().getSimpleName();

    private FirebaseAuth auth;
    private FirebaseDatabase mFirebaseInstance;
    private DataSnapshot dataSnap;



    public HomeFragmentViewModel(@NonNull Application application) {

        super(application);
    }

    public void displayData(){

        View toastLayout = LayoutInflater.from(getApplication()).inflate(R.layout.toast_layout, null);

        TextView text = toastLayout.findViewById(R.id.textView);
        text.setText("Call From ViewModel");

        Toast toast = new Toast(getApplication());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(toastLayout);
        toast.setDuration(Toast.LENGTH_LONG);
        //toast.show();
    }


    public DataSnapshot getSnap(){
        auth = FirebaseAuth.getInstance();

        mFirebaseInstance = FirebaseDatabase.getInstance();

        if (auth.getCurrentUser() != null) {
            mFirebaseInstance.getReference(Constants.DATABASE_PATH_PLACES).child("Dar es Salaam").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot npsnapshot : dataSnapshot.getChildren()) {
                            if (npsnapshot.hasChildren()) {
                                for (DataSnapshot childSnapshot : npsnapshot.getChildren()) {
                                    dataSnap = childSnapshot;
                                }
                            }


                        }
                    }
                }


                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.e(TAG, "Failed to read app title value.", error.toException());
                }

            });
        }else
        {

        }

        return dataSnap;

    }
}
