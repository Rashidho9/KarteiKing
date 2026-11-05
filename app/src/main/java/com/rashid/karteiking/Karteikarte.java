package com.rashid.karteiking;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "karteikarten")
public class Karteikarte {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String frage;
    private String antwort;

    public Karteikarte(String frage, String antwort) {
        this.frage = frage;
        this.antwort = antwort;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFrage() {
        return frage;
    }

    public String getAntwort() {
        return antwort;
    }
}