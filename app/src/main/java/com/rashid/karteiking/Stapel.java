package com.rashid.karteiking;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "stapel")
public class Stapel {

    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private String name;

    public Stapel(@NonNull String name){
        this.name = name;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }
}