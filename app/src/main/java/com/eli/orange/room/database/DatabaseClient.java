package com.eli.orange.room.database;


import android.content.Context;

import androidx.room.Room;

public class DatabaseClient {

    private Context mCtx;
    private static DatabaseClient mInstance;

    private AppDatabase appDatabase;

    private DatabaseClient(Context mCtx){


        appDatabase = Room.databaseBuilder(mCtx, AppDatabase.class, "telespodb").allowMainThreadQueries().build();

    }

    public static synchronized DatabaseClient getmInstance(Context mCtx)
    {
        if (mInstance == null)
        {
            mInstance = new DatabaseClient(mCtx);
        }
        return mInstance;
    }

    public AppDatabase getAppDatabase()
    {
        return appDatabase;
    }
}
