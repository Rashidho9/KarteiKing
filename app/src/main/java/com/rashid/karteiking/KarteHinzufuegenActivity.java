package com.rashid.karteiking;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class KarteHinzufuegenActivity extends AppCompatActivity {

    public static final String EXTRA_KARTE_ID = "karte_id";

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_karte_hinzufuegen);

        EditText frage = findViewById(R.id.editFrage);
        EditText antwort = findViewById(R.id.editAntwort);
        Spinner spinner = findViewById(R.id.spinnerStapel);
        TextView titel = findViewById(R.id.textFormTitel);

        AppDatabase db = AppDatabase.getInstance(this);
        KarteikarteDao dao = db.karteikarteDao();
        List<Stapel> stapel = db.stapelDao().getAll();

        boolean bearbeiten = getIntent().hasExtra(EXTRA_KARTE_ID);
        int id = getIntent().getIntExtra(EXTRA_KARTE_ID, -1);
        int zielId = getIntent().getIntExtra(MainActivity.EXTRA_STAPEL_ID, 1);
        titel.setText(bearbeiten ? "Karte bearbeiten" : "Karte hinzufügen");

        if (stapel.isEmpty()) {
            abbrechen("Kein Stapel vorhanden");
            return;
        }

        if (bearbeiten) {
            Karteikarte alt = dao.getById(id);
            if (alt == null) {
                abbrechen("Karte nicht mehr vorhanden");
                return;
            }
            zielId = alt.getStapelId();
            if (state == null) {
                frage.setText(alt.getFrage());
                antwort.setText(alt.getAntwort());
            }
        }

        String[] namen = new String[stapel.size()];
        int auswahl = 0;
        for (int i = 0; i < stapel.size(); i++) {
            namen[i] = stapel.get(i).getName();
            if (stapel.get(i).getId() == zielId) {
                auswahl = i;
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, namen);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        if (state == null) {
            spinner.setSelection(auswahl);
        }

        findViewById(R.id.buttonSpeichern).setOnClickListener(v -> {
            String f = frage.getText().toString().trim();
            String a = antwort.getText().toString().trim();

            if (f.isEmpty()) {
                frage.setError("Bitte Frage eingeben");
                frage.requestFocus();
                return;
            }
            if (a.isEmpty()) {
                antwort.setError("Bitte Antwort eingeben");
                antwort.requestFocus();
                return;
            }
            if (bearbeiten && dao.getById(id) == null) {
                abbrechen("Karte nicht mehr vorhanden");
                return;
            }

            int position = spinner.getSelectedItemPosition();
            if (position < 0 || position >= stapel.size()) return;

            Karteikarte karte = new Karteikarte(f, a);
            karte.setStapelId(stapel.get(position).getId());

            if (bearbeiten) {
                karte.setId(id);
                dao.update(karte);
            } else {
                dao.insertAll(karte);
            }

            Toast.makeText(this, "Karte gespeichert",
                    Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    private void abbrechen(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
        finish();
    }
}