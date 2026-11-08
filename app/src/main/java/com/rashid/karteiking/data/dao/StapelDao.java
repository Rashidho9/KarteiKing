package com.rashid.karteiking.data.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;

import com.rashid.karteiking.data.entity.Stapel;

import java.util.List;

/**
 * Room-Zugriff auf Stapel und deren Verwaltungsoperationen.
 * Schützt ID 1 vor Umbenennen und Löschen. Das Entfernen anderer Stapel
 * löscht deren Karten dauerhaft, ohne Verschieben nach Allgemein.
 */
@Dao
public abstract class StapelDao {

    /** Sortiert Stapel nach Namen ohne Beachtung der Großschreibung, dann nach ID. */
    @Query("SELECT * FROM stapel ORDER BY name COLLATE NOCASE, id")
    public abstract List<Stapel> getAll();

    /** Sucht den Stapel anhand seiner ID; liefert null bei fehlendem Eintrag. */
    @Query("SELECT * FROM stapel WHERE id = :id LIMIT 1")
    public abstract Stapel getById(int id);

    /** Speichert einen neuen Stapel und liefert die eingefügte Zeilen-ID. */
    @Insert
    public abstract long insert(Stapel stapel);

    /** Zählt die Karten eines Stapels für den Bestätigungsdialog. */
    @Query("SELECT COUNT(*) FROM karteikarten WHERE stapelId = :stapelId")
    public abstract int getKartenAnzahl(int stapelId);

    /** Ändert einen Namen außer für ID 1 und liefert die betroffene Zeilenanzahl. */
    @Query("UPDATE stapel SET name = :name WHERE id = :id AND id != 1")
    protected abstract int nameAendern(int id, String name);

    /** Löscht die Karten des Zielstapels, außer bei der geschützten ID 1. */
    @Query("DELETE FROM karteikarten WHERE stapelId = :id AND stapelId != 1")
    protected abstract int kartenEntfernen(int id);

    /** Entfernt nur einen leeren Stapel und niemals ID 1. */
    @Query("DELETE FROM stapel WHERE id = :id AND id != 1 "
            + "AND NOT EXISTS (SELECT 1 FROM karteikarten WHERE stapelId = :id)")
    protected abstract int leerenStapelEntfernen(int id);

    /**
     * Prüft den Standardstapelschutz und speichert den bereinigten Namen.
     * @return true bei geändertem Datensatz, false bei nicht vorhandener ID
     * @throws IllegalArgumentException bei ID 1 oder leerem Namen
     */
    public boolean umbenennen(int id, String name) {
        if (id == 1) {
            throw new IllegalArgumentException("Allgemein ist geschützt.");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Bitte einen Namen eingeben.");
        }
        return nameAendern(id, name.trim()) == 1;
    }

    /**
     * Löscht Karten und ihren Stapel in einer gemeinsamen Room-Transaktion.
     * Karten werden zuerst gelöscht, damit der RESTRICT-Fremdschlüssel erfüllt bleibt.
     * Eine Ausnahme verhindert den Commit; erfolgreiches Löschen ist endgültig.
     * @return false bei fehlendem Stapel, sonst true nach erfolgreicher Löschung
     * @throws IllegalArgumentException bei geschützter ID 1
     * @throws IllegalStateException wenn der Stapel nicht entfernt werden konnte
     */
    @Transaction
    public boolean loeschenMitKarten(int id) {
        if (id == 1) {
            throw new IllegalArgumentException("Allgemein ist geschützt.");
        }
        if (getById(id) == null) return false;

        kartenEntfernen(id);
        if (leerenStapelEntfernen(id) != 1) {
            throw new IllegalStateException("Stapel konnte nicht gelöscht werden.");
        }
        return true;
    }
}