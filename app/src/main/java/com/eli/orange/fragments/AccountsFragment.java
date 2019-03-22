package com.eli.orange.fragments;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.eli.orange.R;
import com.eli.orange.adapter.CurrentNewsAdapter;
import com.eli.orange.room.entities.locationHistory;
import com.eli.orange.room.model.roomViewModel;

import java.util.List;


/**
 * A simple {@link AccountsFragment} subclass.
 */
public class AccountsFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private roomViewModel viewModel;
    private CurrentNewsAdapter recyclerViewAdapter;


    public AccountsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_, container, false);
        recyclerView = view.findViewById(R.id.historyRecycler);
        viewModel = ViewModelProviders.of(this).get(roomViewModel.class);
        viewModel.getHistoryList().observe(this, new Observer<List<locationHistory>>() {
            @Override
            public void onChanged(@Nullable List<locationHistory> infoModels) {
                recyclerViewAdapter.addItems(infoModels);
            }
        });


        recyclerViewAdapter = new CurrentNewsAdapter(getContext());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAdapter.setOnItemClickListener(new CurrentNewsAdapter.ClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.d("MSG", "onItemClick position: " + position);
                //Log.d("MSG", "onItemClick position: " + position + disaggrigationList.get(position).getDisag_value());
            }

            @Override
            public void onItemLongClick(int position, View v) {
                Toast.makeText(getActivity(), "text", Toast.LENGTH_LONG).show();
            }
        });




        return view;
    }

}
