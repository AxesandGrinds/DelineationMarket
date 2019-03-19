package com.eli.orange.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eli.orange.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
View view;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        /*Intent dialogIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);*/

        return view;
    }

}
