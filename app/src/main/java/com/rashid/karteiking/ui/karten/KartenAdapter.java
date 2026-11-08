package com.rashid.karteiking.ui.karten;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rashid.karteiking.R;
import com.rashid.karteiking.data.entity.Karteikarte;

import java.util.List;

/**
 * Verbindet Karten mit RecyclerView-Zeilen, ohne selbst Daten zu speichern.
 * Leitet Bearbeiten und Löschen über Rückruffunktionen an die Activity weiter.
 * Erwartet eine nicht-null Kartenliste.
 */
public class KartenAdapter extends RecyclerView.Adapter<KartenAdapter.ViewHolder> {

    /** Vertrag für den Löschwunsch einer Kartenzeile. */
    public interface OnLoeschenListener {
        /** Meldet die ausgewählte Karte an die zuständige Activity. */
        void onLoeschen(Karteikarte karte);
    }

    /** Vertrag für den Bearbeitungswunsch einer Kartenzeile. */
    public interface OnBearbeitenListener {
        /** Meldet die ausgewählte Karte an die zuständige Activity. */
        void onBearbeiten(Karteikarte karte);
    }

    private List<Karteikarte> karten;
    private final OnLoeschenListener listener;
    private OnBearbeitenListener bearbeitenListener;

    /** Registriert die optionale Rückruffunktion für Bearbeitungsaktionen. */
    public void setOnBearbeitenListener(OnBearbeitenListener listener) {
        this.bearbeitenListener = listener;
    }

    /** Übernimmt die Datenliste und die benötigte Rückruffunktion zum Löschen. */
    public KartenAdapter(List<Karteikarte> karten, OnLoeschenListener listener) {
        this.karten = karten;
        this.listener = listener;
    }

    /** Ersetzt die Liste und veranlasst eine vollständige Aktualisierung der Anzeige. */
    public void setKarten(List<Karteikarte> neueKarten) {
        this.karten = neueKarten;
        notifyDataSetChanged();
    }

    /** Erzeugt eine neue Zeilenansicht aus dem Layout item_karte. */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_karte, parent, false);
        return new ViewHolder(view);
    }

    /** Verbindet Texte und Schaltflächen mit der Karte an der aktuellen Position. */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Karteikarte karte = karten.get(position);
        holder.textFrage.setText(karte.getFrage());
        holder.textAntwort.setText(karte.getAntwort());
        holder.buttonLoeschen.setOnClickListener(v -> listener.onLoeschen(karte));
        holder.buttonBearbeiten.setOnClickListener(v -> {
            if (bearbeitenListener != null) {
                bearbeitenListener.onBearbeiten(karte);
            }
        });
    }

    /** Liefert die Anzahl der darzustellenden Karten. */
    @Override
    public int getItemCount() {
        return karten.size();
    }

    /** Hält die vier Ansichten einer wiederverwendbaren Kartenzeile bereit. */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textFrage;
        TextView textAntwort;
        Button buttonLoeschen;
        Button buttonBearbeiten;

        /** Sucht die Zeilenansichten einmalig und speichert ihre Referenzen. */
        ViewHolder(View itemView) {
            super(itemView);
            textFrage = itemView.findViewById(R.id.textFrage);
            textAntwort = itemView.findViewById(R.id.textAntwort);
            buttonLoeschen = itemView.findViewById(R.id.buttonLoeschen);
            buttonBearbeiten = itemView.findViewById(R.id.buttonBearbeiten);
        }
    }
}