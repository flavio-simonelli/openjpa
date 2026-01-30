package org.apache.openjpa.util;

import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CacheMapPutCoverageTest {

    CacheMap sut;

    @Test // Test per coprire: _pinnedSize++ e entryAdded su chiave pinnata con valore null
    public void testPut_UpdatePinnedGhostEntry_IncrementsSize() {
        // --- 1. SETUP ---
        int max = 10;
        sut = new CacheMap(true, max);
        // Definiamo una chiave che NON esiste in mappa
        Object ghostKey = "GhostKey";
        Object newValue = "RealValue";
        // --- 2. PRE-CONDITION: CREAZIONE GHOST ENTRY ---
        // Pinniamo la chiave inesistente.
        // La pin() inserirà la chiave nella pinnedMap con valore NULL.
        boolean pinResult = sut.pin(ghostKey);
        assertThat(pinResult).isFalse();
        assertThat(sut.getPinnedKeys()).contains(ghostKey);
        //assertThat(sut.size()).isEqualTo(1); // aggiunto dopo analisi con pitest
        assertThat(sut.get(ghostKey)).isNull(); // Il valore è null
        // --- 3. ACTION ---
        // Facciamo la PUT su questa chiave.
        Object result = sut.put(ghostKey, newValue);
        // --- 4. VERIFICA ---
        // A. La put deve ritornare null (perché prima il valore era null)
        assertThat(result).isNull();
        // B. Il valore deve essere aggiornato
        assertThat(sut.get(ghostKey)).isEqualTo(newValue);
        // C. Verifica che la chiave è ancora pinnata
        assertThat(sut.getPinnedKeys()).contains(ghostKey);
        assertThat(sut.size()).isEqualTo(1); // aggiunto dopo analisi con pitest
    }
}
