package com.rashid.karteiking;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentierter Beispieltest für ein Android-Gerät oder einen Emulator.
 * Prüft die Application-ID, nicht die Datenbank oder die Benutzeroberfläche.
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    /** Prüft, ob der Zielkontext zur erwarteten Application-ID gehört. */
    @Test
    public void useAppContext() {
        // Kontext der Ziel-App, nicht der separaten Test-App.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("com.rashid.karteiking", appContext.getPackageName());
    }
}