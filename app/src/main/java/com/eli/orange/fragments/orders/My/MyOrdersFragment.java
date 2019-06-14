package com.eli.orange.fragments.orders.My;


import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eli.orange.R;
import com.eli.orange.fragments.orders.ChildView;
import com.eli.orange.fragments.orders.HeaderView;
import com.eli.orange.fragments.orders.Orders;
import com.eli.orange.room.model.roomViewModel;
import com.eli.orange.utils.Constants;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mindorks.placeholderview.ExpandablePlaceHolderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link MyOrdersFragment} subclass.
 */
public class MyOrdersFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private roomViewModel viewModel;
    private MyOrdersAdapter recyclerViewAdapter;
    private FirebaseAuth auth;
    private FirebaseDatabase mFirebaseInstance;
    private List<Orders> orders =new ArrayList<>();


    public MyOrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_, container, false);
        recyclerView = view.findViewById(R.id.historyRecycler);



        auth = FirebaseAuth.getInstance();
        new fetchDataInbackground().execute();



        return view;
    }

    public class fetchDataInbackground extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            recyclerViewAdapter = new MyOrdersAdapter(getContext());

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_PATH_ORDERS);

            //Create query for database Child reference
            Query applesQuery = databaseReference.orderByChild("customerIdentity").equalTo(auth.getCurrentUser().getUid());

            applesQuery.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        orders.clear();
                        for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                            Orders order = dataSnap.getValue(Orders.class);
                            orders.add(order);
                            recyclerViewAdapter.addItems(orders);

                            Log.d("ORDER", dataSnap.toString());

                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e(Constants.TAG, "onCancelled", databaseError.toException());
                }
            });


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            recyclerViewAdapter = new MyOrdersAdapter(getContext());
            recyclerView.setAdapter(recyclerViewAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));



        }
    }
   /* private void getHeaderAndChild(List<Orders> movieList) {

        for (Orders movie : movieList) {
            List<Orders> movieList1 = categoryMap.get(movie.getProductIdentity());
            if (movieList1 == null) {
                movieList1 = new ArrayList<>();
            }
            movieList1.add(movie);
            categoryMap.put(movie.getProductIdentity(), movieList1);
        }

        //Log.d("Map",categoryMap.toString());
        Iterator it = categoryMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            Log.d("Key", pair.getKey().toString());
            expandablePlaceHolderView.addView(new HeaderView(getContext(), pair.getKey().toString()));
            List<Orders> movieList1 = (List<Orders>) pair.getValue();
            for (Orders movie : movieList1) {
                expandablePlaceHolderView.addView(new ChildView(getContext(), movie));
            }
            it.remove();
        }
    }*/



}
