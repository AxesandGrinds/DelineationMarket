package com.eli.orange.fragments.bottomSheetFragment;

import android.content.Context;

import com.eli.orange.room.database.DatabaseClient;
import com.eli.orange.room.entities.userInfo;

public class BottomSheetFragmentPresenter {
    Context context;

    public BottomSheetFragmentPresenter(Context context){
        this.context = context;
    }

    private userInfo userInfo;


    public void saveToLocalStorage(String username,String businessname,double longitude, double latitude, String locationame){
        userInfo = new userInfo();
        userInfo.setUsername(username);
        userInfo.setBusinessname(businessname);
        userInfo.setLatitude(latitude);
        userInfo.setLongitude(longitude);
        userInfo.setLocationame(locationame);
        //adding to database
        DatabaseClient.getmInstance(context).getAppDatabase()
                .roomDao()
                .addInfo(userInfo);
    }
    public interface View{
        void saveToLocalStorage();
        void isvalidForm();
    }
}
