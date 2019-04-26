package com.eli.orange.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eli.orange.R;

import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationsFragment extends Fragment {
    private View view;
    public NotificationsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_notifications, container, false);
        ButterKnife.bind(this, view);

        return view;

    }

}
