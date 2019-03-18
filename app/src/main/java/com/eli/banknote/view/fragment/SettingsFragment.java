package com.eli.banknote.view.fragment;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.eli.banknote.R;
import com.eli.banknote.adapters.SourcesRecyclerAdapter;
import com.eli.banknote.models.newsSource;
import com.eli.banknote.network.GetNewsDataService;
import com.eli.banknote.network.RetrofitInstance;
import com.eli.banknote.restApi.model.newsSourcesList;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends Fragment {
    View view;
    @BindView(R.id.recyclerSettings)
    RecyclerView settingRecyclerView;
    SourcesRecyclerAdapter adapter;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);

        GetNewsDataService service = RetrofitInstance.getRetrofitInstance().create(GetNewsDataService.class);

        /*Call the method with parameter in the interface to get the employee data*/
        Call<newsSourcesList> call = service.getSources();
        /*Log the URL called*/
        Log.wtf("URL Called", call.request().url() + "");

        call.enqueue(new Callback<newsSourcesList>() {
            @Override
            public void onResponse(Call<newsSourcesList> call, Response<newsSourcesList> response) {
                generateSourceList(response.body().getNewsSourceArrayList());
                Log.d("RESPONSE","Returned");
            }

            @Override
            public void onFailure(Call<newsSourcesList> call, Throwable t) {
                Toast.makeText(getContext(), "Something went wrong...Please try later!"+t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        return view;

    }

    /*Method to generate List of employees using RecyclerView with custom adapter*/
    private void generateSourceList(ArrayList<newsSource> empDataList) {

        adapter = new SourcesRecyclerAdapter(empDataList,getContext());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

        settingRecyclerView.setLayoutManager(layoutManager);

        settingRecyclerView.setAdapter(adapter);
    }


}
