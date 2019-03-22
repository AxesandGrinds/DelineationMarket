package com.eli.orange.room.database;


import com.eli.orange.room.DAO.roomDao;
import com.eli.orange.room.entities.locationHistory;
import com.eli.orange.room.entities.userInfo;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {userInfo.class, locationHistory.class}, version = 1,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract roomDao roomDao();
}