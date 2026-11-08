package com.rashid.karteiking.data.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Persistenter Themenstapel mit automatisch vergebener ID und nicht-null Namen.
 * ID 1 bezeichnet Allgemein; dessen Schutz setzt das StapelDao um.
 * Namen sind nicht eindeutig und werden hier nicht auf leere Inhalte geprüft.
 */
@Entity(tableName = "stapel")
public class Stapel {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String name;

    /** Erzeugt einen Stapel; die Datenbank vergibt beim Einfügen die ID. */
    public Stapel(@NonNull String name) {
        this.name = name;
    }

    /** Liefert die persistente Stapel-ID. */
    public int getId() {
        return id;
    }

    /** Setzt die ID, insbesondere beim Rekonstruieren durch Room. */
    public void setId(int id) {
        this.id = id;
    }

    /** Liefert den nicht-null Namen für Anzeige und Sortierung. */
    public String getName() {
        return name;
    }

    /**
     * Ersetzt den Namen im Objekt, ohne ihn automatisch in der Datenbank zu speichern.
     */
    public void setName(@NonNull String name) {
        this.name = name;
    }
}