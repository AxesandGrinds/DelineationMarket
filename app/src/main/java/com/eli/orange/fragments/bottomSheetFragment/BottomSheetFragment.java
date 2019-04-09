package com.eli.orange.fragments.bottomSheetFragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.eli.orange.R;
import com.eli.orange.room.database.DatabaseClient;
import com.eli.orange.room.entities.userInfo;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BottomSheetFragment extends BottomSheetDialogFragment implements BottomSheetFragmentPresenter.View {
    private View view;
    private Context context;
    @BindView(R.id.bottonSheetButton)
    Button saveButton;
    @BindView(R.id.edBottomSheetBusinessName)
    EditText businessname;
    @BindView(R.id.edBottomSheetLocatioName)
    EditText locationname;
    @BindView(R.id.edBottomSheetLongitude)
    EditText longitude;
    @BindView(R.id.edtBottonSheetUsername)
    EditText username;

    private BottomSheetFragmentPresenter presenter;


    public BottomSheetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.bottom_sheet, container, false);
        ButterKnife.bind(this,view);
        presenter = new BottomSheetFragmentPresenter(getContext());


        return view;
    }
    @OnClick(R.id.bottonSheetButton)
    public void saveitems(){
        saveToLocalStorage();
    }

    @Override
    public void saveToLocalStorage() {
        presenter.saveToLocalStorage(
                username.getText().toString().trim(),
                businessname.getText().toString().trim(),
                1.7e+038,1.7e+038,
                locationname.getText().toString().trim());

    }
    @Override
    public void isvalidForm(){

        if (username.getText().toString().length()<3){
            username.setError("Invalid Username");

        }else if (businessname.getText().toString().length()==0){
            businessname.setError("Business Name required!");
        }else if(longitude.getText().toString().length()==0){
            longitude.setError("Longitude required!");
        }else {
            saveButton.setEnabled(true);
        }
    }
}
