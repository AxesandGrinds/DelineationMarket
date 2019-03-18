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
import android.widget.SearchView;
import android.widget.Toast;

import com.eli.banknote.R;
import com.eli.banknote.utils.SharedPreferences;
import com.eli.banknote.adapters.GoogleNewsRecyclerAdapter;
import com.eli.banknote.models.Articles;
import com.eli.banknote.network.GetNewsDataService;
import com.eli.banknote.network.RetrofitInstance;
import com.eli.banknote.restApi.model.articlesList;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    View view;

    @BindView(R.id.home_recyclerView)
    RecyclerView homeRecyclerView;
    SharedPreferences sp;
    GoogleNewsRecyclerAdapter adapter;



    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);

        sp = new SharedPreferences(getContext());





        /*Call the method with parameter in the interface to get the employee data*/
        getQuery("trump");
        /*Log the URL called*/



        return view;

    }

        /*Method to generate List of employees using RecyclerView with custom adapter*/
        private void generateEmployeeList(ArrayList<Articles> empDataList) {

            adapter = new GoogleNewsRecyclerAdapter(empDataList,getActivity());

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());

            homeRecyclerView.setLayoutManager(layoutManager);

            homeRecyclerView.setAdapter(adapter);
        }
        public String countrycode(){
            String country;
            if (sp.getString(SharedPreferences.Key.SOURCE_NAME)==null) {
            country = "cnn";
            }else {
                country = sp.getString(SharedPreferences.Key.SOURCE_NAME);
            }
            return country.replaceAll("\\r","");
        }
        public void getQuery(String queryString){
            GetNewsDataService service = RetrofitInstance.getRetrofitInstance().create(GetNewsDataService.class);
            Call<articlesList> call = service.getEveryThing(queryString);

            Log.wtf("URL Called", call.request().url() + "");

            call.enqueue(new Callback<articlesList>() {
                @Override
                public void onResponse(Call<articlesList> call, Response<articlesList> response) {
                    generateEmployeeList(response.body().getNewsArrayList());
                    Log.d("RESPONSE","Returned");
                }

                @Override
                public void onFailure(Call<articlesList> call, Throwable t) {
                    Toast.makeText(getContext(), "Something went wrong...Please try later!"+t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }



    }


