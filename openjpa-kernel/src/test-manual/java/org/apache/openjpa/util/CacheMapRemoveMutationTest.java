package org.apache.openjpa.util;

import org.junit.Ignore;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CacheMapRemoveMutationTest {

    @Test
    public void testRemove_StandardEntry_CallsEntryRemoved() {
        // --- SETUP ---
        CacheMap realMap = new CacheMap(true, 10);
        CacheMap spyMap = spy(realMap);
        Object key = "KeyToRemove";
        Object val = "ValueToRemove";
        // 1. Inseriamo l'elemento.
        spyMap.put(key, val);
        clearInvocations(spyMap);
        // --- ACTION ---
        // Rimuoviamo l'elemento
        Object result = spyMap.remove(key);
        // --- VERIFY ---
        verify(spyMap, times(1)).entryRemoved(eq(key), eq(val), eq(false));
    }

    @Test
    public void testRemove_ReleasesWriteLock_EnsuringNoDeadlock() {
        // --- SETUP ---
        CacheMap sut = new CacheMap(true, 10);
        Object key = "KeyToRemove";
        sut.put(key, "Value");
        // --- ACTION ---
        // Chiamiamo la remove.
        sut.remove(key);
        // --- VERIFY ---
        // Verifichiamo che un altro thread possa accedere alla mappa.
        // Se il lock non è stato rilasciato, sut.get() si bloccherà all'infinito.
        CompletableFuture<Object> asyncCheck = CompletableFuture.supplyAsync(() -> {
            // Proviamo ad acquisire il lock (get usa readLock, che richiede che writeLock sia libero)
            return sut.get(key);
        });
        assertThat(asyncCheck)
                .as("La remove() deve rilasciare il writeLock, altrimenti le operazioni successive si bloccano")
                .succeedsWithin(Duration.ofSeconds(1));
    }
}
