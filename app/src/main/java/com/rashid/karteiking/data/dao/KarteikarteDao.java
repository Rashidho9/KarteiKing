package com.rashid.karteiking.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.rashid.karteiking.data.entity.Karteikarte;

import java.util.List;

/**
 * Room-Schnittstelle zum Lesen, Anlegen, Aktualisieren und Löschen von Karten.
 * Listen sind nach Karten-ID sortiert; getById kann null liefern.
 * Die Methoden arbeiten synchron und werden derzeit von Activities aufgerufen.
 */
@Dao
public interface KarteikarteDao {

    /** Liest alle Karten in stabiler ID-Reihenfolge. */
    @Query("SELECT * FROM karteikarten ORDER BY id")
    List<Karteikarte> getAll();

    /** Liest nur die Karten des angegebenen Stapels, nach ID sortiert. */
    @Query("SELECT * FROM karteikarten WHERE stapelId = :stapelId ORDER BY id")
    List<Karteikarte> getByStapel(int stapelId);

    /** Sucht eine Karte anhand ihrer ID; liefert null bei fehlendem Eintrag. */
    @Query("SELECT * FROM karteikarten WHERE id = :id LIMIT 1")
    Karteikarte getById(int id);

    /** Zählt die Karten über alle Stapel hinweg. */
    @Query("SELECT COUNT(*) FROM karteikarten")
    int count();

    /**
     * Fügt Karten ein; vorhandene IDs werden nicht als Update behandelt.
     */
    @Insert
    void insertAll(Karteikarte... karten);

    /** Aktualisiert eine vorhandene Karte anhand ihres Primärschlüssels. */
    @Update
    void update(Karteikarte karte);

    /** Entfernt die angegebene Karte anhand ihres Primärschlüssels dauerhaft. */
    @Delete
    void delete(Karteikarte karte);
}