package org.apache.openjpa.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CacheMapConstructorCoverageTest {
    CacheMap sut;

    @Test
    public void testConstructor_SizeMaxNegative() {
        sut = new CacheMap(false, -1, -1, 0.5f, 1);
        // controllo che sia ritornato e non c'è stata alcuna eccezione quindi sia diverso da null
        assertThat(sut)
                .as("L'oggetto deve essere stato creato senza eccezioni")
                .isNotNull();
        // controllo che la getMaxSize sia corretta
        assertThat(sut.cacheMap.getMaxSize())
                .as("Se max è negativo, deve essere impostato a Integer.MAX_VALUE")
                .isEqualTo(Integer.MAX_VALUE);
    }

}
