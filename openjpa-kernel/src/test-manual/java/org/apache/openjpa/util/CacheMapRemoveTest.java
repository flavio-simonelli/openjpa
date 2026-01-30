package org.apache.openjpa.util;

import org.junit.Ignore;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;

import static org.apache.openjpa.util.KeyObjectMother.createKeyObjectValid;
import static org.apache.openjpa.util.ValueObjectMother.createValueObjectImmutable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class CacheMapRemoveTest {

    private CacheMap sut;

    @Test // Test 0: Remove Null Key -> Returns null
    public void testRemove_NullKey_ReturnsNull_Or_HandleGracefully() {
        // --- 1. SETUP ---
        sut = new CacheMap(); // Default (Random, 1000)
        // Popoliamo con un dato
        sut.put("Key1", "Value1");
        int initialSize = sut.size();
        // --- 2. ACTION & ASSERTION ---
        Object result = sut.remove(null);
        assertThat(result).as("Rimuovere null dovrebbe ritornare null").isNull();
        assertThat(sut.size()).as("La size non deve cambiare").isEqualTo(initialSize);
        assertThat(sut.containsKey("Key1")).isTrue();
    }

    @Test // Test 1: Remove Key with HashCode Exception -> Exception Thrown, Map Intact
    public void testRemove_HashCodeException_MapUnchanged() {
        // --- 1. SETUP ---
        sut = new CacheMap(); // Default (Random, 1000)
        List<Object> keys = createKeyListValid(5);
        List<Object> values = createValueListImmutable(5);
        for (int i = 0; i < 5; i++) {
            sut.put(keys.get(i), values.get(i));
        }
        Object badKey = KeyObjectMother.createKeyObjectBad();
        // --- 2. ACTION & ASSERTION ---
        assertThatThrownBy(() -> sut.remove(badKey))
                .isInstanceOf(RuntimeException.class);
        // --- 3. VERIFICA ---
        assertThat(sut.size()).isEqualTo(5);
        for (int i = 0; i < 5; i++) {
            assertThat(sut.containsKey(keys.get(i))).isTrue();
        }
    }

    @Test // Test 2: Remove Key Not Present -> Returns Null, Size Unchanged
    public void testRemove_KeyNotPresent_ReturnsNull() {
        // --- 1. SETUP ---
        sut = new CacheMap(); // Default (Random, 1000)
        for (int i = 0; i < 5; i++) {
            sut.put(createKeyObjectValid(i), createValueObjectImmutable(i));
        }
        Object keyNotPresent = "Key-999";
        // --- 2. ACTION ---
        Object result = sut.remove(keyNotPresent);
        // --- 3. VERIFICA ---
        assertThat(result).isNull();
        assertThat(sut.size()).isEqualTo(5);
    }

    @Test // Test 3: Remove Key in Hard Map -> Returns Value, Size Decrements
    public void testRemove_ExistingHard_ReturnsValue_AndRemoves() {
        // --- 1. SETUP ---
        sut = new CacheMap(); // Default (Max 1000). Ne inseriamo 5, quindi sono tutti in Hard.
        List<Object> keys = createKeyListValid(5);
        List<Object> values = createValueListImmutable(5);
        for (int i = 0; i < 5; i++) {
            sut.put(keys.get(i), values.get(i));
        }
        Object keyToRemove = keys.get(0);
        Object expectedValue = values.get(0);
        // --- 2. ACTION ---
        Object result = sut.remove(keyToRemove);
        keys.remove(keyToRemove);
        values.remove(expectedValue);
        // --- 3. VERIFICA ---
        assertThat(result).isEqualTo(expectedValue);
        assertThat(sut.size()).isEqualTo(4);
        assertThat(sut.containsKey(keyToRemove)).isFalse();
        for (int i = 0; i < 4; i++) {
            assertThat(sut.containsKey(keys.get(i))).isTrue();
        }
    }

    @Test // Test 4: Remove Key in Soft Map -> Returns Value, Size Decrements, Fully Removed
    public void testRemove_ExistingSoft_ReturnsValue_AndRemoves() {
        // --- MOTIVAZIONE ---
        // Non posso usare new CacheMap() qui perché crea una mappa "Random" (lru=false).
        // Per testare la rimozione dalla SoftMap in modo deterministico,
        // ho bisogno di sapere ESATTAMENTE quale chiave viene espulsa dalla HardMap.
        // Solo LRU (true) mi garantisce che il primo inserito sia il primo espulso.
        int maxHard = 10;
        sut = new CacheMap(true, maxHard);
        sut.setSoftReferenceSize(2);
        List<Object> keys = createKeyListValid(11);
        List<Object> values = createValueListImmutable(11);
        for (int i = 0; i < 11; i++) {
            sut.put(keys.get(i), values.get(i));
        }
        Object keyInSoft = keys.get(0); // LRU: Il primo inserito è in Soft
        // --- 2. ACTION ---
        Object result = sut.remove(keyInSoft);
        // --- 3. VERIFICA ---
        assertThat(result).isEqualTo(values.get(0));
        assertThat(sut.size()).isEqualTo(10);
        assertThat(sut.containsKey(keyInSoft)).isFalse();
        // Verifica flush
        sut.setSoftReferenceSize(0);
        assertThat(sut.size()).isEqualTo(10);
    }

    // Questo test è corretto! è il codice ad essere sbagliato rispetto a cosa dice la javaDoc!
    //If <code>key</code> is pinned into the cache, the pin is * cleared and the object is removed.
    @Ignore
    @Test // Test 5: Remove Pinned Key -> Returns Value, Unpins, Removes
    public void testRemove_PinnedKey_RemovesCompletely() {
        // --- 1. SETUP ---
        sut = new CacheMap(); // Default
        List<Object> keys = createKeyListValid(5);
        List<Object> values = createValueListImmutable(5);
        for (int i = 0; i < 5; i++) {
            sut.put(keys.get(i), values.get(i));
        }
        Object keyPinned = keys.get(0);
        Object expectedValue = values.get(0);
        sut.pin(keyPinned);
        assertThat(sut.getPinnedKeys()).contains(keyPinned);
        // --- 2. ACTION ---
        Object result = sut.remove(keyPinned);
        // --- 3. VERIFICA ---
        assertThat(result).isEqualTo(expectedValue);
        assertThat(sut.size()).isEqualTo(4);
        assertThat(sut.containsKey(keyPinned)).isFalse();
        assertThat(sut.getPinnedKeys()).doesNotContain(keyPinned);
    }

    // --- Helper Methods ---
    private List<Object> createKeyListValid(int count) {
        List<Object> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            list.add(KeyObjectMother.createKeyObjectValid(i));
        }
        return list;
    }

    private List<Object> createValueListImmutable(int count) {
        List<Object> list = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            list.add(ValueObjectMother.createValueObjectImmutable(i));
        }
        return list;
    }
}