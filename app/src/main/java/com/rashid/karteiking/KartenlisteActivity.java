package com.rashid.karteiking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class KartenlisteActivity extends AppCompatActivity {

    private KarteikarteDao dao;
    private KartenAdapter adapter;
    private int stapelId;

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

    @Override
    protected void onResume() {
        super.onResume();
        adapter.setKarten(dao.getByStapel(stapelId));
    }

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