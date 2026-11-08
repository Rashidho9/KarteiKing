package com.rashid.karteiking.ui.stapel;

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

import com.rashid.karteiking.R;
import com.rashid.karteiking.data.dao.StapelDao;
import com.rashid.karteiking.data.database.AppDatabase;
import com.rashid.karteiking.data.entity.Stapel;
import com.rashid.karteiking.ui.lernen.MainActivity;

import java.util.List;

/**
 * Startbildschirm für Auswahl und Verwaltung der Stapel.
 * Tippen öffnet den Lernbildschirm; langes Drücken bietet Verwaltungsaktionen.
 * Die Löschbestätigung nennt Kartenanzahl und Endgültigkeit der Aktion.
 */
public class StapeluebersichtActivity extends AppCompatActivity {

    private ListView liste;
    private List<Stapel> stapel;
    private StapelDao dao;

    /** Verbindet Listenauswahl, Langdruckmenü und das Anlegen neuer Stapel. */
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

    /** Aktualisiert die Übersicht bei jeder Rückkehr in den Vordergrund. */
    @Override
    protected void onResume() {
        super.onResume();
        ladeStapel();
    }

    /** Lädt die sortierten Stapel und baut ihre Namensanzeige neu auf. */
    private void ladeStapel() {
        stapel = dao.getAll();
        String[] namen = new String[stapel.size()];
        for (int i = 0; i < stapel.size(); i++) {
            namen[i] = stapel.get(i).getName();
        }
        liste.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, namen));
    }

    /** Liefert für Allgemein true und erläutert dessen Schutz. */
    private boolean geschuetzt(Stapel eintrag) {
        if (eintrag.getId() != 1) return false;
        meldung("Allgemein kann nicht umbenannt oder gelöscht werden.");
        return true;
    }

    /** Öffnet das Verwaltungsmenü für einen nicht geschützten Stapel. */
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

    /**
     * Legt bei null einen Stapel an oder benennt den übergebenen Stapel um.
     * Ungültige Eingaben halten den Dialog offen.
     * Gespeichert wird erst nach Betätigung der positiven Schaltfläche.
     */
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

        // Eigener Klick-Listener verhindert das Schließen bei ungültigen Eingaben.
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

    /**
     * Zeigt die Kartenanzahl und warnt vor der endgültigen Löschung.
     * Löscht Stapel und Karten erst nach ausdrücklicher Bestätigung.
     */
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

    /** Zeigt eine kurze Rückmeldung, ohne die Datenbank zu verändern. */
    private void meldung(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    /** Protokolliert die Ausnahme in Logcat und zeigt einen Fehlerhinweis. */
    private void fehler(RuntimeException e) {
        Log.e("Stapeluebersicht", "Datenbankoperation fehlgeschlagen", e);
        meldung("Änderung fehlgeschlagen. Bitte Logcat prüfen; App-Daten nicht löschen.");
    }
}