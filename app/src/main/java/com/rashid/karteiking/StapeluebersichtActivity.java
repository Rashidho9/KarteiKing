package com.rashid.karteiking;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

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

        findViewById(R.id.buttonNeuerStapel)
                .setOnClickListener(v -> neuerStapel());
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

    private void neuerStapel() {
        EditText eingabe = new EditText(this);
        eingabe.setHint("Stapelname");
        eingabe.setSingleLine(true);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Neuer Stapel")
                .setView(eingabe)
                .setPositiveButton("Anlegen", null)
                .setNegativeButton("Abbrechen", null)
                .create();

        // Eigener Klick verhindert das Schließen bei leerem Namen.
        dialog.setOnShowListener(d ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setOnClickListener(v -> {
                            String name = eingabe.getText().toString().trim();
                            if (name.isEmpty()) {
                                eingabe.setError("Bitte einen Namen eingeben");
                                return;
                            }
                            dao.insert(new Stapel(name));
                            ladeStapel();
                            dialog.dismiss();
                        }));
        dialog.show();
    }
}