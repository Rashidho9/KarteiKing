package com.rashid.karteiking;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

@Dao
public abstract class StapelDao {

    @Query("SELECT * FROM stapel ORDER BY name COLLATE NOCASE, id")
    public abstract List<Stapel> getAll();

    @Query("SELECT * FROM stapel WHERE id = :id LIMIT 1")
    public abstract Stapel getById(int id);

    @Insert
    public abstract long insert(Stapel stapel);

    @Query("SELECT COUNT(*) FROM karteikarten WHERE stapelId = :stapelId")
    public abstract int getKartenAnzahl(int stapelId);

    @Query("UPDATE stapel SET name = :name WHERE id = :id AND id != 1")
    protected abstract int nameAendern(int id, String name);

    @Query("DELETE FROM karteikarten WHERE stapelId = :id AND stapelId != 1")
    protected abstract int kartenEntfernen(int id);

    @Query("DELETE FROM stapel WHERE id = :id AND id != 1 "
            + "AND NOT EXISTS (SELECT 1 FROM karteikarten WHERE stapelId = :id)")
    protected abstract int leerenStapelEntfernen(int id);

    public boolean umbenennen(int id, String name) {
        if (id == 1) {
            throw new IllegalArgumentException("Allgemein ist geschützt.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Bitte einen Namen eingeben.");
        }
        return nameAendern(id, name.trim()) == 1;
    }

    @Transaction
    public boolean loeschenMitKarten(int id) {
        if (id == 1) {
            throw new IllegalArgumentException("Allgemein ist geschützt.");
        }
        if (getById(id) == null) return false;

        // Erst zugehörige Karten, dann ihren Stapel löschen.
        kartenEntfernen(id);
        if (leerenStapelEntfernen(id) != 1) {
            throw new IllegalStateException("Stapel konnte nicht gelöscht werden.");
        }
        return true;
    }
}