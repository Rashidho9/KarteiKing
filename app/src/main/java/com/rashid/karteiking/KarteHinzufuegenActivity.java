package com.rashid.karteiking;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class KarteHinzufuegenActivity extends AppCompatActivity {

    public static final String EXTRA_KARTE_ID = "karte_id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karte_hinzufuegen);

        EditText editFrage = findViewById(R.id.editFrage);
        EditText editAntwort = findViewById(R.id.editAntwort);
        Button buttonSpeichern = findViewById(R.id.buttonSpeichern);

        KarteikarteDao dao =
                AppDatabase.getInstance(this).karteikarteDao();

        boolean bearbeiten = getIntent().hasExtra(EXTRA_KARTE_ID);
        int karteId = getIntent().getIntExtra(EXTRA_KARTE_ID, -1);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(
                    bearbeiten ? "Karte bearbeiten" : "Karte hinzufügen");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (bearbeiten) {
            Karteikarte vorhandeneKarte =
                    karteId == -1 ? null : dao.getById(karteId);

            if (vorhandeneKarte == null) {
                Toast.makeText(this,
                        "Diese Karte existiert nicht mehr.",
                        Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Nach Bildschirmdrehung stellt Android die Eingaben wieder her.
            if (savedInstanceState == null) {
                editFrage.setText(vorhandeneKarte.getFrage());
                editAntwort.setText(vorhandeneKarte.getAntwort());
            }
        }

        buttonSpeichern.setOnClickListener(v -> {
            String frage = editFrage.getText().toString().trim();
            String antwort = editAntwort.getText().toString().trim();

            if (frage.isEmpty()) {
                editFrage.setError("Bitte eine Frage eingeben.");
                editFrage.requestFocus();
                return;
            }

            if (antwort.isEmpty()) {
                editAntwort.setError("Bitte eine Antwort eingeben.");
                editAntwort.requestFocus();
                return;
            }

            if (bearbeiten) {
                // Nicht versehentlich eine inzwischen gelöschte Karte anlegen.
                if (dao.getById(karteId) == null) {
                    Toast.makeText(this,
                            "Diese Karte existiert nicht mehr.",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                Karteikarte geaenderteKarte =
                        new Karteikarte(frage, antwort);
                geaenderteKarte.setId(karteId);
                dao.update(geaenderteKarte);
            } else {
                dao.insertAll(new Karteikarte(frage, antwort));
            }

            Toast.makeText(this,
                    "Karte gespeichert", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}