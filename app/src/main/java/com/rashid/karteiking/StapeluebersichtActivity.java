package com.rashid.karteiking;

import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class StapeluebersichtActivity extends AppCompatActivity {

    private ListView liste;
    private List<Stapel> stapel;
    private StapelDao dao;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_stapeluebersicht);

        dao = AppDatabase.getInstance(this).stapelDao();
        liste = findViewById(R.id.listStapel);

        liste.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(MainActivity.EXTRA_STAPEL_ID,
                    stapel.get(position).getId());
            startActivity(intent);
        });

        liste.setOnItemLongClickListener((parent, view, position, id) -> {
            verwalten(stapel.get(position));
            return true;
        });

        findViewById(R.id.buttonNeuerStapel)
                .setOnClickListener(v -> namendialog(null));
    }

    @Override
    protected void onResume() {
        super.onResume();
        ladeStapel();
    }

    private void ladeStapel() {
        stapel = dao.getAll();
        String[] namen = new String[stapel.size()];
        for (int i = 0; i < stapel.size(); i++) {
            namen[i] = stapel.get(i).getName();
        }
        liste.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, namen));
    }

    private boolean geschuetzt(Stapel eintrag) {
        if (eintrag.getId() != 1) return false;
        meldung("Allgemein kann nicht umbenannt oder gelöscht werden.");
        return true;
    }

    private void verwalten(Stapel eintrag) {
        if (geschuetzt(eintrag)) return;

        new AlertDialog.Builder(this)
                .setTitle(eintrag.getName())
                .setItems(new String[]{"Umbenennen", "Löschen"},
                        (dialog, which) -> {
                            if (which == 0) {
                                namendialog(eintrag);
                            } else {
                                loeschenBestaetigen(eintrag);
                            }
                        })
                .show();
    }

    // null bedeutet: einen neuen Stapel anlegen.
    private void namendialog(Stapel eintrag) {
        if (eintrag != null && geschuetzt(eintrag)) return;

        EditText eingabe = new EditText(this);
        eingabe.setSingleLine(true);
        eingabe.setHint("Stapelname");
        if (eintrag != null) {
            eingabe.setText(eintrag.getName());
            eingabe.setSelection(eingabe.length());
        }

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(eintrag == null ? "Neuer Stapel" : "Stapel umbenennen")
                .setView(eingabe)
                .setPositiveButton(eintrag == null ? "Anlegen" : "Speichern", null)
                .setNegativeButton("Abbrechen", null)
                .create();

        dialog.setOnShowListener(d ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(v -> {
                            String name = eingabe.getText().toString().trim();
                            if (name.isEmpty()) {
                                eingabe.setError("Bitte einen Namen eingeben");
                                return;
                            }

                            try {
                                boolean ok;
                                if (eintrag == null) {
                                    dao.insert(new Stapel(name));
                                    ok = true;
                                } else {
                                    ok = dao.umbenennen(eintrag.getId(), name);
                                }
                                if (!ok) {
                                    meldung("Stapel nicht mehr vorhanden.");
                                }
                            } catch (IllegalArgumentException e) {
                                eingabe.setError(e.getMessage());
                                return;
                            } catch (SQLiteException | IllegalStateException e) {
                                fehler(e);
                                return;
                            }

                            dialog.dismiss();
                            ladeStapel();
                        }));
        dialog.show();
    }

    private void loeschenBestaetigen(Stapel eintrag) {
        if (geschuetzt(eintrag)) return;

        final int anzahl;
        try {
            anzahl = dao.getKartenAnzahl(eintrag.getId());
        } catch (SQLiteException | IllegalStateException e) {
            fehler(e);
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Stapel und alle Karten löschen?")
                .setMessage("Der Stapel „" + eintrag.getName()
                        + "“ und ALLE seine Karten werden dauerhaft gelöscht "
                        + "(aktuell " + anzahl + " Karten). "
                        + "Das kann nicht rückgängig gemacht werden. "
                        + "Andere Stapel bleiben unverändert.")
                .setPositiveButton("Stapel und Karten löschen", (dialog, which) -> {
                    try {
                        boolean ok = dao.loeschenMitKarten(eintrag.getId());
                        meldung(ok
                                ? "Stapel und seine Karten gelöscht."
                                : "Stapel nicht mehr vorhanden.");
                    } catch (SQLiteException | IllegalArgumentException
                             | IllegalStateException e) {
                        fehler(e);
                    }
                    ladeStapel();
                })
                .setNegativeButton("Abbrechen", null)
                .show();
    }

    private void meldung(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    private void fehler(RuntimeException e) {
        Log.e("Stapeluebersicht", "Datenbankoperation fehlgeschlagen", e);
        meldung("Änderung fehlgeschlagen. Bitte Logcat prüfen; App-Daten nicht löschen.");
    }
}