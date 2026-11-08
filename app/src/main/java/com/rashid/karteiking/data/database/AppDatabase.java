package com.rashid.karteiking.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.rashid.karteiking.data.dao.KarteikarteDao;
import com.rashid.karteiking.data.dao.StapelDao;
import com.rashid.karteiking.data.entity.Karteikarte;
import com.rashid.karteiking.data.entity.Stapel;

/**
 * Zentraler Room-Zugang zur Datei karteiking.db.
 * Verwaltet beide Entities, Migration und Erstinitialisierung.
 * Die synchronisierte Fabrik liefert eine gemeinsame Instanz pro Prozess.
 * Hauptthread-Abfragen bleiben erlaubt und können die Oberfläche blockieren.
 */
@Database(
        entities = {Karteikarte.class, Stapel.class},
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    // Gemeinsame Instanz; Zugriff erfolgt ausschließlich über getInstance.
    private static AppDatabase INSTANCE;

    /** Liefert den von Room erzeugten Zugriff auf Karten. */
    public abstract KarteikarteDao karteikarteDao();

    /** Liefert den von Room erzeugten Zugriff auf Stapel. */
    public abstract StapelDao stapelDao();

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        /**
         * Ordnet bestehende Karten Allgemein zu und erhält IDs sowie Textwerte.
         * Für den neuen Fremdschlüssel wird die Kartentabelle neu aufgebaut.
         */
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS stapel ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + "name TEXT NOT NULL)");

            db.execSQL("INSERT INTO stapel (id, name) VALUES (1, 'Allgemein')");

            db.execSQL("CREATE TABLE karteikarten_neu ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
                    + "frage TEXT, antwort TEXT, "
                    + "stapelId INTEGER NOT NULL DEFAULT 1, "
                    + "FOREIGN KEY(stapelId) REFERENCES stapel(id) "
                    + "ON UPDATE NO ACTION ON DELETE RESTRICT)");

            db.execSQL("INSERT INTO karteikarten_neu (id, frage, antwort, stapelId) "
                    + "SELECT id, frage, antwort, 1 FROM karteikarten");

            db.execSQL("DROP TABLE karteikarten");
            db.execSQL("ALTER TABLE karteikarten_neu RENAME TO karteikarten");
            db.execSQL("CREATE INDEX index_karteikarten_stapelId "
                    + "ON karteikarten(stapelId)");
        }
    };

    /**
     * Liefert die gemeinsame Datenbankinstanz mit dem Anwendungskontext.
     * Registriert die Migration und initialisiert neue Datenbanken mit Allgemein.
     */
    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "karteiking.db")
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(new Callback() {
                        /** Legt bei einer neuen Datenbank den Standardstapel ID 1 an. */
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            db.execSQL("INSERT INTO stapel (id, name) "
                                    + "VALUES (1, 'Allgemein')");
                        }
                    })
                    // Lernstand: synchroner Zugriff; größere Datenmengen können UI-Pausen verursachen.
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
}