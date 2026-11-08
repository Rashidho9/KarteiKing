package com.rashid.karteiking.ui.lernen;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.rashid.karteiking.R;
import com.rashid.karteiking.data.dao.KarteikarteDao;
import com.rashid.karteiking.data.database.AppDatabase;
import com.rashid.karteiking.data.entity.Karteikarte;
import com.rashid.karteiking.data.entity.Stapel;
import com.rashid.karteiking.ui.karten.KarteHinzufuegenActivity;
import com.rashid.karteiking.ui.karten.KartenlisteActivity;

import java.util.List;

/**
 * Lernbildschirm für den über einen Intent ausgewählten Stapel.
 * Zeigt Frage oder Antwort und wechselt zyklisch zur nächsten Karte.
 * Lädt Karten beim erneuten Aktivieren und setzt dabei die Antwortseite zurück.
 */
public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_STAPEL_ID = "stapel_id";

    private TextView textZaehler, textKarte;
    private List<Karteikarte> karten;
    private KarteikarteDao dao;
    private int stapelId;
    private int index = 0;
    private boolean zeigtAntwort;
    private String stapelName;

    /**
     * Initialisiert Ansichten und Klickaktionen und prüft den gewählten Stapel.
     * Stellt gegebenenfalls den gespeicherten Kartenindex wieder her.
     */
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

    /** Öffnet die Ziel-Activity mit der ID des aktuell gewählten Stapels. */
    private void oeffne(Class<?> ziel) {
        Intent intent = new Intent(this, ziel);
        intent.putExtra(EXTRA_STAPEL_ID, stapelId);
        startActivity(intent);
    }

    /**
     * Lädt die aktuellen Stapelkarten und korrigiert einen ungültigen Index.
     * Setzt die Anzeige bewusst auf die Frageseite zurück.
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (dao == null) return;
        karten = dao.getByStapel(stapelId);
        if (index < 0 || index >= karten.size()) index = 0;
        zeigtAntwort = false;
        zeigeKarte();
    }

    /** Zeigt die aktuelle Kartenseite oder den Hinweis auf einen leeren Stapel. */
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

    /** Sichert den Kartenindex für eine Neuerzeugung der Activity. */
    @Override
    protected void onSaveInstanceState(Bundle out) {
        out.putInt("index", index);
        super.onSaveInstanceState(out);
    }
}