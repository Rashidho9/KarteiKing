package com.rashid.karteiking;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface KarteikarteDao {

    @Query("SELECT * FROM karteikarten ORDER BY id")
    List<Karteikarte> getAll();

    @Query("SELECT * FROM karteikarten WHERE stapelId = :stapelId ORDER BY id")
    List<Karteikarte> getByStapel(int stapelId);

    @Query("SELECT * FROM karteikarten WHERE id = :id LIMIT 1")
    Karteikarte getById(int id);

    @Query("SELECT COUNT(*) FROM karteikarten")
    int count();

    @Insert
    void insertAll(Karteikarte... karten);

    @Update
    void update(Karteikarte karte);

    @Delete
    void delete(Karteikarte karte);
}