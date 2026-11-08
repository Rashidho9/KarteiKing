package com.rashid.karteiking.data.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * Persistente Lernkarte mit Frage, Antwort und Zuordnung zu genau einem Stapel.
 * Der Fremdschlüssel verhindert das Entfernen eines noch referenzierten Stapels.
 * Frage und Antwort sind auf Schemaebene nullable; das Formular prüft leere Eingaben.
 */
@Entity(
        tableName = "karteikarten",
        foreignKeys = @ForeignKey(
                entity = Stapel.class,
                parentColumns = "id",
                childColumns = "stapelId",
                onDelete = ForeignKey.RESTRICT),
        indices = @Index(value = "stapelId")
)
public class Karteikarte {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String frage;
    private String antwort;

    // Java-Vorbelegung und SQL-Default verweisen beide auf Allgemein.
    @ColumnInfo(defaultValue = "1")
    private int stapelId = 1;

    /**
     * Erzeugt eine ungespeicherte Karte; der Standardstapel ist zunächst ID 1.
     */
    public Karteikarte(String frage, String antwort) {
        this.frage = frage;
        this.antwort = antwort;
    }

    /**
     * Liefert die Karten-ID; vor der ersten Speicherung ist sie normalerweise 0.
     */
    public int getId() {
        return id;
    }

    /**
     * Setzt die ID für Room oder für das Aktualisieren einer gespeicherten Karte.
     */
    public void setId(int id) {
        this.id = id;
    }

    /** Liefert den Fragetext; das Datenbankschema erlaubt null. */
    public String getFrage() {
        return frage;
    }

    /** Liefert den Antworttext; das Datenbankschema erlaubt null. */
    public String getAntwort() {
        return antwort;
    }

    /** Liefert die ID des zugeordneten Stapels. */
    public int getStapelId() {
        return stapelId;
    }

    /**
     * Ordnet die Karte einem Stapel zu; beim Speichern muss dieser existieren.
     */
    public void setStapelId(int stapelId) {
        this.stapelId = stapelId;
    }
}