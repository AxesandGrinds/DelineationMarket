package com.eli.orange.room.model;

import android.app.Application;
import android.content.Context;

import com.eli.orange.room.database.AppDatabase;
import com.eli.orange.room.database.DatabaseClient;
import com.eli.orange.room.entities.locationHistory;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

public class roomViewModel extends AndroidViewModel {
    Context context = this.getApplication();

    private final LiveData<List<locationHistory>> histlist;


    private AppDatabase appDatabase;

    public roomViewModel(@NonNull Application application) {
        super(application);

        appDatabase = DatabaseClient.getmInstance(context).getAppDatabase();

        histlist = appDatabase.roomDao().getLocactionHistories();
    }
    public LiveData<List<locationHistory>> getHistoryList()
    {
        return  histlist;
    }
}
