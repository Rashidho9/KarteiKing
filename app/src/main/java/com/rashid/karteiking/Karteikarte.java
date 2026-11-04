package com.rashid.karteiking;

// Eine Lernkarte mit Frage und Antwort
public class Karteikarte {

    private final String frage;
    private final String antwort;
    public Karteikarte(String frage, String antwort) {
        this.frage = frage;
        this.antwort = antwort;
    }
    public String getFrage() { return frage; }
    public String getAntwort() { return antwort; }

}
