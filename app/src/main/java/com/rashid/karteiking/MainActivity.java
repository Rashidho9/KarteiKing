package com.rashid.karteiking;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final List<Karteikarte> karten = new ArrayList<>();
    private int index = 0;              // aktuelle Karte
    private boolean zeigeAntwort = false;

    private TextView textKarte;
    private TextView textZaehler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left + 48, bars.top + 48, bars.right + 48, bars.bottom + 48);
            return insets;
        });

        // Beispielkarten (später aus der Datenbank)
        karten.add(new Karteikarte("Was bedeutet OOP?", "Objektorientierte Programmierung"));
        karten.add(new Karteikarte("Was ist eine Klasse?", "Ein Bauplan für Objekte"));
        karten.add(new Karteikarte("Was macht 'git push'?", "Lädt lokale Commits auf GitHub hoch"));

        textKarte = findViewById(R.id.textKarte);
        textZaehler = findViewById(R.id.textZaehler);
        CardView karte = findViewById(R.id.karte);
        Button buttonWeiter = findViewById(R.id.buttonWeiter);

        // Karte umdrehen
        karte.setOnClickListener(v -> {
            zeigeAntwort = !zeigeAntwort;
            zeigeKarte();
        });

        // Nächste Karte (am Ende wieder von vorne)
        buttonWeiter.setOnClickListener(v -> {
            index = (index + 1) % karten.size();
            zeigeAntwort = false;
            zeigeKarte();
        });

        zeigeKarte();
    }

    // Zeigt Frage oder Antwort der aktuellen Karte
    private void zeigeKarte() {
        Karteikarte k = karten.get(index);
        textKarte.setText(zeigeAntwort ? "✅ " + k.getAntwort() : "❓ " + k.getFrage());
        textZaehler.setText("Karte " + (index + 1) + " von " + karten.size());
    }
}