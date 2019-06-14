package com.eli.orange.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.eli.orange.R.id;
import com.eli.orange.R.layout;
import com.eli.orange.fragments.addCenter.AddCenterFragmentPresenter;
import com.eli.orange.fragments.availableProducts.availableContentFragment;
import com.eli.orange.fragments.orders.Customer.CustomerOrdersFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView.OnNavigationItemSelectedListener;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class BottomSheetFragment extends BottomSheetDialogFragment implements OnNavigationItemSelectedListener {
    private View view;
    private AddCenterFragmentPresenter presenter;
    private Fragment fragment;

    @BindView(id.bottomNavigation)
    BottomNavigationView bottomNavigationView;

    private BottomSheetDialog bottomSheetDialog;


    public BottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view  = inflater.inflate(layout.bottom_sheet, container, false);

        ButterKnife.bind(this, view);
        fragment = new availableContentFragment();
        loadFragment(fragment);



        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);



        return view;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case id.bottom_home:
                fragment = new availableContentFragment();
                break;
            case id.bottom_history:
                fragment = new CustomerOrdersFragment();
                break;
            case id.bottom_user_profile:
                fragment = new userProfileFragment();
                break;
            case id.bottom_notifications:
                fragment = new NotificationsFragment();
                break;
        }
        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
