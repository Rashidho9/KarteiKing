package com.rashid.karteiking;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class KartenAdapter extends RecyclerView.Adapter<KartenAdapter.ViewHolder> {

    // Wird aufgerufen, wenn der Nutzer auf "Löschen" tippt
    public interface OnLoeschenListener {
        void onLoeschen(Karteikarte karte);
    }

    public interface OnBearbeitenListener {
        void onBearbeiten(Karteikarte karte);
    }

    private List<Karteikarte> karten;
    private final OnLoeschenListener listener;
    private OnBearbeitenListener bearbeitenListener;

    public void setOnBearbeitenListener(OnBearbeitenListener listener) {
        this.bearbeitenListener = listener;
    }

    public KartenAdapter(List<Karteikarte> karten, OnLoeschenListener listener) {
        this.karten = karten;
        this.listener = listener;
    }

    public void setKarten(List<Karteikarte> neueKarten) {
        this.karten = neueKarten;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_karte, parent, false);
        return new ViewHolder(view);
    }

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

    @Override
    public int getItemCount() {
        return karten.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textFrage;
        TextView textAntwort;
        Button buttonLoeschen;
        Button buttonBearbeiten;

        ViewHolder(View itemView) {
            super(itemView);
            textFrage = itemView.findViewById(R.id.textFrage);
            textAntwort = itemView.findViewById(R.id.textAntwort);
            buttonLoeschen = itemView.findViewById(R.id.buttonLoeschen);
            buttonBearbeiten = itemView.findViewById(R.id.buttonBearbeiten);
        }
    }
}