package com.rashid.karteiking;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface KarteikarteDao {

    @Query("SELECT * FROM karteikarten")
    List<Karteikarte> getAll();

    @Insert
    void insertAll(Karteikarte... karten);

    @Query("SELECT COUNT(*) FROM karteikarten")
    int count();

    @Delete
    void delete(Karteikarte karte);

    @Query("SELECT * FROM karteikarten WHERE id = :id LIMIT 1")
    Karteikarte getById(int id);

    @Update
    void update(Karteikarte karte);

}
