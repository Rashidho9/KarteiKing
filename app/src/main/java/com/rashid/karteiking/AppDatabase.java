package com.rashid.karteiking;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(
        entities = {Karteikarte.class, Stapel.class},
        version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract KarteikarteDao karteikarteDao();
    public abstract StapelDao stapelDao();

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
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

    public static synchronized AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "karteiking.db")
                    .addMigrations(MIGRATION_1_2)
                    .addCallback(new Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            db.execSQL("INSERT INTO stapel (id, name) "
                                    + "VALUES (1, 'Allgemein')");
                        }
                    })
                    .allowMainThreadQueries()
                    .build();
        }
        return INSTANCE;
    }
}