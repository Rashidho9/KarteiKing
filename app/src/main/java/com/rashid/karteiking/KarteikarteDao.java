package com.rashid.karteiking;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface KarteikarteDao {

    @Query("SELECT * FROM karteikarten")
    List<Karteikarte> getAll();

    @Insert
    void insertAll(Karteikarte... karten);

    @Query("SELECT COUNT(*) FROM karteikarten")
    int count();
}
