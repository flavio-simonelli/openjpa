package org.apache.openjpa.util;

import org.junit.Ignore;
import org.junit.Test;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;


public class CacheMapPutMutationTest {

    @Test
    public void testPut_PinnedMap_UpdateValue_CallsEntryRemovedAndAdded() {
        // --- SETUP ---
        CacheMap realMap = new CacheMap(true, 10);
        CacheMap spyMap = spy(realMap);

        Object pinnedKey = "PinnedKey";
        Object oldValue = "OldValue";
        Object newValue = "NewValue";
        // 1. Inseriamo la entry nella mappa
        spyMap.put(pinnedKey, oldValue);
        // 2. La pinniamo (ora è in pinnedMap con oldValue)
        spyMap.pin(pinnedKey);
        clearInvocations(spyMap);
        // --- ACTION ---
        // 3. Facciamo l'update.
        spyMap.put(pinnedKey, newValue);
        // --- VERIFY ---
        // Deve chiamare entryRemoved
        verify(spyMap, times(1)).entryRemoved(eq(pinnedKey), eq(oldValue), eq(false));
        // Deve chiamare entryAdded
        verify(spyMap, times(1)).entryAdded(eq(pinnedKey), eq(newValue));
    }

    @Test
    public void testPut_PinnedMap_FirstValue_CallsEntryAddedOnly() {
        // --- SETUP ---
        CacheMap realMap = new CacheMap(true, 10);
        CacheMap spyMap = spy(realMap);
        Object pinnedKey = "PinnedKey";
        Object newValue = "FirstRealValue";
        // 1. "Pinniamo" la chiave.
        // Questo inserisce la chiave nella pinnedMap con valore NULL.
        spyMap.pin(pinnedKey);
        clearInvocations(spyMap);
        // --- ACTION ---
        // 2. Facciamo la PUT.
        spyMap.put(pinnedKey, newValue);
        // --- VERIFY ---
        // Deve chiamare entryAdded
        verify(spyMap, times(1)).entryAdded(eq(pinnedKey), eq(newValue));
        // NON deve chiamare entryRemoved (perché prima era null)
        verify(spyMap, never()).entryRemoved(any(), any(), anyBoolean());
        // deve essere aumentato il valore di pinnedSize e quindi della size della nostra mappa
        assertThat(spyMap.size())
                .as("La dimensione deve essere aumentata di una unità")
                .isEqualTo(1);
    }

    @Test
    public void testPut_NewEntry_NeitherHardNorSoft_CallsEntryAddedOnly() {
        // --- SETUP ---
        CacheMap realMap = new CacheMap(true, 10);
        CacheMap spyMap = spy(realMap);
        Object key = "NewKey";
        Object value = "NewValue";
        // --- ACTION ---
        spyMap.put(key, value);
        // --- VERIFY  ---
        // Deve chiamare entryAdded esattamente 1 volta
        verify(spyMap, times(1)).entryAdded(eq(key), eq(value));
        // NON deve chiamare entryRemoved.
        verify(spyMap, never()).entryRemoved(any(), any(), anyBoolean());
    }

    @Test
    public void testPut_SoftMapPromotion_CallsEntryRemovedAndAdded() {
        // --- SETUP ---
        // Max 1 elemento hard.
        CacheMap realMap = new CacheMap(true, 10);
        realMap.setSoftReferenceSize(10); // Spazio sufficiente nella Soft
        CacheMap spyMap = spy(realMap);

        List<Object> keys = new ArrayList<>();
        // 1. Riempiamo completamente la Hard Map (10 elementi)
        for (int i = 0; i < 10; i++) {
            Object key = KeyObjectMother.createKeyObjectValid(i + 1);
            keys.add(key);
            spyMap.put(key, "Value" + i);
        }
        // 2. Inseriamo l'11 elemento per forzare l'evizione del primo (keys.get(0)) in Soft Map
        Object overflowKey = KeyObjectMother.createKeyObjectValid(999);
        spyMap.put(overflowKey, "OverflowValue");
        clearInvocations(spyMap);
        // --- ACTION ---
        // 3. Facciamo una PUT sulla chiave che è finita in Soft.
        // Questo causerà la rimozione dalla Soft e il reinserimento in Hard (con nuovo valore).
        Object keyInSoft = keys.get(0);
        Object oldValInSoft = "Value0";
        Object newValPromoted = "NewValPromoted";
        spyMap.put(keyInSoft, newValPromoted);
        // --- VERIFY ---
        // Deve chiamare entryRemoved (per il vecchio valore che era in soft)
        verify(spyMap, times(1)).entryRemoved(eq(keyInSoft), eq(oldValInSoft), eq(false));
        // Deve chiamare entryAdded (per il nuovo valore promosso)
        verify(spyMap, times(1)).entryAdded(eq(keyInSoft), eq(newValPromoted));
    }

    @Test
    public void testPut_HardMapUpdate_CallsEntryRemovedAndAdded() {
        // --- SETUP ---
        CacheMap realMap = new CacheMap(true, 10);
        CacheMap spyMap = spy(realMap);
        Object keyInHard = "KeyHard";
        Object valInHard = "ValHard";
        Object newValInHard = "NewValHard";
        // 1. Inseriamo l'elemento (va in Hard)
        spyMap.put(keyInHard, valInHard);
        clearInvocations(spyMap);
        // --- ACTION ---
        spyMap.put(keyInHard, newValInHard);
        // --- VERIFY ---
        // Deve chiamare entryRemoved
        verify(spyMap, times(1)).entryRemoved(eq(keyInHard), eq(valInHard), eq(false));
        // deve chiamare entryAdded
        verify(spyMap, times(1)).entryAdded(eq(keyInHard), eq(newValInHard));
    }

    @Test
    public void testPut_ReleasesLock_EvenIfExceptionOrSuccess() {
        // --- SETUP ---
        CacheMap sut = new CacheMap(true, 10);
        Object key = "Key";
        // --- ACTION ---
        sut.put(key, "Value");
        // VERIFICA CONCORRENTE
        // Predisponiamo un task asincrono che prova ad acquisire il lock.
        CompletableFuture<Object> asyncOperation = CompletableFuture.supplyAsync(() -> {
            // Se il writeLock è ancora attivo sul main thread, questa chiamata
            // si bloccherà all'infinito e il Future non si completerà.
            return sut.get(key);
        });

        // AssertJ verifica che il task finisca entro 1 secondo.
        // Se il tempo scade, lancia automaticamente un errore spiegando che il Future non si è completato.
        assertThat(asyncOperation)
                .as("Deadlock rilevato: la put() non ha rilasciato il writeLock, bloccando le letture successive")
                .succeedsWithin(Duration.ofSeconds(1));
    }
}
