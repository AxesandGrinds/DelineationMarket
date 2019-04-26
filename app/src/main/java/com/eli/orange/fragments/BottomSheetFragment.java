package com.eli.orange.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.eli.orange.R.id;
import com.eli.orange.R.layout;
import com.eli.orange.fragments.addCenter.AddCenterFragment;
import com.eli.orange.fragments.HistoryFragment;
import com.eli.orange.fragments.NotificationsFragment;
import com.eli.orange.fragments.addCenter.AddCenterFragmentPresenter;
import com.eli.orange.fragments.userProfileFragment;
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
        loadFragment(new AddCenterFragment());

        bottomNavigationView.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        return view;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case id.bottom_home:
                fragment = new AddCenterFragment();
                break;
            case id.bottom_history:
                fragment = new HistoryFragment();
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
