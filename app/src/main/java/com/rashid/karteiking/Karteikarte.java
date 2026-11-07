package com.rashid.karteiking;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

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

    @ColumnInfo(defaultValue = "1")
    private int stapelId = 1;

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

    public int getStapelId() {
        return stapelId;
    }

    public void setStapelId(int stapelId) {
        this.stapelId = stapelId;
    }
}