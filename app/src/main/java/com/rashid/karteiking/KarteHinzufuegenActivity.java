package com.rashid.karteiking;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class KarteHinzufuegenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_karte_hinzufuegen);

        if (getSupportActionBar() != null){
            getSupportActionBar().setTitle("Karte hinzufügen");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        EditText editFrage = findViewById(R.id.editFrage);
        EditText editAntwort = findViewById(R.id.editAntwort);
        Button buttonSpeichern = findViewById(R.id.buttonSpeichern);

        buttonSpeichern.setOnClickListener(v ->{
            String frage = editFrage.getText().toString().trim();
            String antwort = editAntwort.getText().toString().trim();

            if (frage.isEmpty() || antwort.isEmpty()){
                Toast.makeText(this, "Frage und Antwort ausfüllen", Toast.LENGTH_SHORT).show();
                return;
            }

            AppDatabase db = AppDatabase.getInstance(this);
            db.karteikarteDao().insertAll(new Karteikarte(frage, antwort));

            Toast.makeText(this, "Karte gespeichert", Toast.LENGTH_SHORT).show();
            finish();
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
