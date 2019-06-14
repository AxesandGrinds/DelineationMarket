package com.eli.orange.room.DAO;


import com.eli.orange.room.entities.locationHistory;
import com.eli.orange.room.entities.centersInfo;

import java.util.List;

import static androidx.room.OnConflictStrategy.IGNORE;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface roomDao {

    @Insert(onConflict = IGNORE)
    void addInfo(centersInfo centersInfo);

    @Insert(onConflict = IGNORE)
    void addLocationHistory(locationHistory locationHistory);

    @Query("SELECT * FROM locationHistory ORDER BY id DESC")
    LiveData<List<locationHistory>> getLocactionHistories();

}
