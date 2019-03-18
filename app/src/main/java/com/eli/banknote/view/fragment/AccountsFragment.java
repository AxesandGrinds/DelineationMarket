package com.eli.banknote.view.fragment;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.eli.banknote.utils.Constants;
import com.eli.banknote.utils.ConveterAdapter;
import com.eli.banknote.R;
import com.eli.banknote.utils.SharedPreferences;

import java.util.ArrayList;
import java.util.Locale;


/**
 * A simple {@link AccountsFragment} subclass.
 */
public class AccountsFragment extends Fragment {
    View view;
    @BindView(R.id.countrySpinner)
    Spinner countrySpinner;
    int selectionCurrent;
    SharedPreferences sharedPreferences;
    private ConveterAdapter conveterAdapter;
    ArrayList<String> arrayList = new ArrayList<>();


    public AccountsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_, container, false);
        ButterKnife.bind(this,view);

        sharedPreferences = new SharedPreferences(getContext());
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, convertToLocalLanguage(Constants.SUPPORTED_COUNTRIES));
        spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

// Spinner spinYear = (Spinner)findViewById(R.id.spin);
        countrySpinner.setAdapter(spinnerArrayAdapter);

          selectionCurrent= countrySpinner.getSelectedItemPosition();

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (selectionCurrent != position){
                Toast.makeText(getContext(),"Heloo: "+ position,Toast.LENGTH_SHORT).show();
                sharedPreferences.put(SharedPreferences.Key.COUNTRY_NAME,Constants.SUPPORTED_COUNTRIES[position]);

                }
                selectionCurrent= position;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;
    }
    public ArrayList<String> convertToLocalLanguage(String [] lng)

    {
        Locale loc;
        for (int i=0; i<lng.length; i++){
            loc= new Locale("", lng[i]);


            arrayList.add(loc.getDisplayCountry());
        }

        return arrayList;
    }

}
