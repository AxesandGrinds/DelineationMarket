package com.eli.orange.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.eli.orange.R;
import com.eli.orange.activity.uploadFiles.ImageUploadActivity;
import com.eli.orange.fragments.orders.My.MyOrdersFragment;
import com.eli.orange.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class userProfileFragment extends Fragment {
    private View view;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    @BindView(R.id.profile_imageview)
    ImageView userProfileImage;
    @BindView(R.id.profile_username)
    TextView userProfileUsername;
    @BindView(R.id.tvNumber3)
    TextView userProfileEmail;
    @BindView(R.id.button_profile_remove_contact)
    Button removeContact;
    @BindView(R.id.profile_edit)
    TextView editProfile;
    private Unbinder unbinder;

    public userProfileFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        unbinder = ButterKnife.bind(this, view);


        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");



        final FragmentTransaction transaction = getChildFragmentManager().beginTransaction();

        transaction.addToBackStack(null);
        //transaction.replace(R.id.rel, new MyOrdersFragment());

        //transaction.commit();

        // load nav menu header data
        if (auth.getCurrentUser() != null) {
            loadNavHeader();
        }

        return view;
    }

    @Override
    public void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
    }

    private void loadNavHeader() {
        // name, website
        databaseReference.child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    updateUI(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void loadCenter(){

    }


    //updateUI with variables for @datasnapshot
    void updateUI(DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);
        userProfileUsername.setText(user.getUsername());
        userProfileEmail.setText(Html.fromHtml(user.getEmail()));

        // Loading profile image
        Glide.with(getContext())
                .load(user.getUserImageUrl())
                .apply(RequestOptions.circleCropTransform())
                .thumbnail(0.5f)
                .into(userProfileImage);
    }
    @OnClick(R.id.button_profile_remove_contact)
    void openuploaded(){
        getContext().startActivity( new Intent(getContext(), ImageUploadActivity.class));
    }



}
