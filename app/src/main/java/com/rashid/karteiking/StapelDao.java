package com.rashid.karteiking;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface StapelDao {

    @Query("SELECT * FROM stapel ORDER BY name COLLATE NOCASE, id")
    List<Stapel> getAll();

    @Query("SELECT * FROM stapel WHERE id = :id LIMIT 1")
    Stapel getById(int id);

    @Insert
    long insert(Stapel stapel);
}