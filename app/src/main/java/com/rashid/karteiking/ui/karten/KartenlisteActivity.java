package com.rashid.karteiking.ui.karten;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rashid.karteiking.R;
import com.rashid.karteiking.data.dao.KarteikarteDao;
import com.rashid.karteiking.data.database.AppDatabase;
import com.rashid.karteiking.data.entity.Karteikarte;
import com.rashid.karteiking.ui.lernen.MainActivity;

import java.util.ArrayList;

/**
 * Zeigt die Karten genau eines Stapels als RecyclerView-Liste.
 * Öffnet das Bearbeitungsformular und bestätigt das endgültige Löschen einer Karte.
 */
public class KartenlisteActivity extends AppCompatActivity {

    private KarteikarteDao dao;
    private KartenAdapter adapter;
    private int stapelId;

    /** Verbindet die gefilterte Kartenliste mit Adapter und Bearbeitungsnavigation. */
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_kartenliste);

        stapelId = getIntent().getIntExtra(MainActivity.EXTRA_STAPEL_ID, 1);
        dao = AppDatabase.getInstance(this).karteikarteDao();

        RecyclerView recycler = findViewById(R.id.recyclerKarten);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new KartenAdapter(new ArrayList<>(), this::frageLoeschen);
        recycler.setAdapter(adapter);

        adapter.setOnBearbeitenListener(karte -> {
            Intent intent = new Intent(this, KarteHinzufuegenActivity.class);
            intent.putExtra(KarteHinzufuegenActivity.EXTRA_KARTE_ID,
                    karte.getId());
            intent.putExtra(MainActivity.EXTRA_STAPEL_ID, stapelId);
            startActivity(intent);
        });
    }

    /** Lädt Karten erneut, insbesondere nach dem Bearbeiten oder Verschieben. */
    @Override
    protected void onResume() {
        super.onResume();
        adapter.setKarten(dao.getByStapel(stapelId));
    }

    /** Fragt vor dem dauerhaften Löschen nach und aktualisiert danach die Liste. */
    private void frageLoeschen(Karteikarte karte) {
        new AlertDialog.Builder(this)
                .setTitle("Karte löschen?")
                .setMessage(karte.getFrage())
                .setPositiveButton("Löschen", (dialog, which) -> {
                    dao.delete(karte);
                    adapter.setKarten(dao.getByStapel(stapelId));
                    Toast.makeText(this, "Karte gelöscht",
                            Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Abbrechen", null)
                .show();
    }
}