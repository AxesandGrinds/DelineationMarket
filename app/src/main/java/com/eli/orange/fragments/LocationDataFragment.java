package com.eli.orange.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eli.orange.R;

import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationDataFragment extends Fragment {
    private View view;
    @BindView(R.id.passedData)
    TextView passedData;


    public LocationDataFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_location_data, container, false);
        ButterKnife.bind(this,view);

        passedData.setText(getArguments().getString("message"));

        return view;
    }

}
