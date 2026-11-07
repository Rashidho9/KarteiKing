package com.rashid.karteiking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_STAPEL_ID = "stapel_id";

    private TextView textZaehler, textKarte;
    private List<Karteikarte> karten;
    private KarteikarteDao dao;
    private int stapelId;
    private int index = 0;
    private boolean zeigtAntwort;
    private String stapelName;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);

        stapelId = getIntent().getIntExtra(EXTRA_STAPEL_ID, 1);
        AppDatabase db = AppDatabase.getInstance(this);
        Stapel stapel = db.stapelDao().getById(stapelId);
        if (stapel == null) {
            finish();
            return;
        }

        stapelName = stapel.getName();
        dao = db.karteikarteDao();
        textZaehler = findViewById(R.id.textZaehler);
        textKarte = findViewById(R.id.textKarte);

        if (state != null) {
            index = state.getInt("index", 0);
        }

        findViewById(R.id.buttonHinzufuegen).setOnClickListener(v ->
                oeffne(KarteHinzufuegenActivity.class));
        findViewById(R.id.buttonListe).setOnClickListener(v ->
                oeffne(KartenlisteActivity.class));

        findViewById(R.id.karte).setOnClickListener(v -> {
            zeigtAntwort = !zeigtAntwort;
            zeigeKarte();
        });

        findViewById(R.id.buttonWeiter).setOnClickListener(v -> {
            if (karten == null || karten.isEmpty()) return;
            index = (index + 1) % karten.size();
            zeigtAntwort = false;
            zeigeKarte();
        });
    }

    private void oeffne(Class<?> ziel) {
        Intent intent = new Intent(this, ziel);
        intent.putExtra(EXTRA_STAPEL_ID, stapelId);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (dao == null) return;
        karten = dao.getByStapel(stapelId);
        if (index < 0 || index >= karten.size()) index = 0;
        zeigtAntwort = false;
        zeigeKarte();
    }

    private void zeigeKarte() {
        if (karten == null || karten.isEmpty()) {
            textZaehler.setText(stapelName + " · Keine Karten");
            textKarte.setText("Füge diesem Stapel deine erste Karte hinzu.");
            return;
        }
        Karteikarte karte = karten.get(index);
        textZaehler.setText(stapelName + " · Karte "
                + (index + 1) + " von " + karten.size());
        textKarte.setText(zeigtAntwort ? karte.getAntwort() : karte.getFrage());
    }

    @Override
    protected void onSaveInstanceState(Bundle out) {
        out.putInt("index", index);
        super.onSaveInstanceState(out);
    }
}