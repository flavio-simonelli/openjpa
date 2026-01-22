package org.apache.openjpa.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CacheMapPinCoverageTest {
    @Test
    public void testPin_GhostEntry_ReturnsFalse() {
        // SETUP
        CacheMap sut = new CacheMap(true, 10);
        Object key = "GhostKey"; // Chiave che non esiste
        // PRIMA CHIAMATA (Crea la Ghost Entry)
        boolean firstResult = sut.pin(key);
        assertThat(firstResult).isFalse();
        // Verifica stato intermedio: la chiave è in pinnedMap ma vale null
        assertThat(sut.getPinnedKeys()).contains(key);
        assertThat(sut.get(key)).isNull();
        // SECONDA CHIAMATA
        boolean secondResult = sut.pin(key);
        assertThat(secondResult)
                .as("Pinnare nuovamente una chiave 'fantasma' (null value) deve ritornare false")
                .isFalse();
    }
}
