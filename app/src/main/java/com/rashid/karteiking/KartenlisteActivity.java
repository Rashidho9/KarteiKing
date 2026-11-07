package com.rashid.karteiking;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;

public class KartenlisteActivity extends AppCompatActivity {

    private KarteikarteDao dao;
    private KartenAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kartenliste);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Alle Karten");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dao = AppDatabase.getInstance(this).karteikarteDao();

        RecyclerView recycler = findViewById(R.id.recyclerKarten);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        adapter = new KartenAdapter(dao.getAll(), this::frageLoeschen);
        recycler.setAdapter(adapter);

        adapter.setOnBearbeitenListener(karte -> {
            Intent intent = new Intent(
                    this, KarteHinzufuegenActivity.class);

            intent.putExtra(
                    KarteHinzufuegenActivity.EXTRA_KARTE_ID,
                    karte.getId());
            startActivity(intent);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (adapter != null) {
            adapter.setKarten(dao.getAll());
        }
    }

    private void frageLoeschen(Karteikarte karte) {
        new AlertDialog.Builder(this)
                .setTitle("Karte löschen?")
                .setMessage(karte.getFrage())
                .setPositiveButton("Löschen", (dialog, which) -> {
                    dao.delete(karte);
                    adapter.setKarten(dao.getAll());
                    Toast.makeText(this, "Karte gelöscht", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Abbrechen", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}