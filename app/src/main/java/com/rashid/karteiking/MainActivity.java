package com.rashid.karteiking;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView textZaehler;
    private TextView textKarte;
    private List<Karteikarte> karten;
    private int index = 0;
    private boolean zeigtAntwort = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textZaehler = findViewById(R.id.textZaehler);
        textKarte = findViewById(R.id.textKarte);
        CardView karte = findViewById(R.id.karte);
        Button buttonWeiter = findViewById(R.id.buttonWeiter);

        AppDatabase db = AppDatabase.getInstance(this);
        KarteikarteDao dao = db.karteikarteDao();

        if (dao.count() == 0) {
            dao.insertAll(
                    new Karteikarte("Was ist eine Activity?", "Eine Bildschirmseite der App"),
                    new Karteikarte("Was ist ein Intent?", "Eine Nachricht, um etwas zu starten"),
                    new Karteikarte("Was ist Room?", "Eine Datenbank-Bibliothek für Android")
            );
        }

        karten = dao.getAll();
        zeigeKarte();

        karte.setOnClickListener(v -> {
            zeigtAntwort = !zeigtAntwort;
            zeigeKarte();
        });

        buttonWeiter.setOnClickListener(v -> {
            if (karten.isEmpty()) {
                return;
            }
            index = (index + 1) % karten.size();
            zeigtAntwort = false;
            zeigeKarte();
        });
    }

    private void zeigeKarte() {
        if (karten == null || karten.isEmpty()) {
            textZaehler.setText("Keine Karten");
            textKarte.setText("Noch keine Karteikarten vorhanden.");
            return;
        }
        Karteikarte k = karten.get(index);
        textZaehler.setText("Karte " + (index + 1) + " von " + karten.size());
        textKarte.setText(zeigtAntwort ? k.getAntwort() : k.getFrage());
    }
}