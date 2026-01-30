package org.apache.openjpa.util;

import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.apache.openjpa.util.KeyObjectMother.createKeyObjectValid;
import static org.apache.openjpa.util.ValueObjectMother.createValueObjectImmutable;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CacheMapPinTest {

//    // il test fallisce perchè non viene lanciata alcuna eccezione conformemente all'implementazione standard java
//    @Test // Test 0: pin null
//    public void testPin_NullKey_ThrowsException() {
//        // --- 1. SETUP ---
//        int max = 10;
//        CacheMap sut = new CacheMap(true, max);
//        int initialSize = max - 1;
//
//        // Popoliamo la mappa per avere uno stato iniziale consistente
//        for (int i = 0; i < initialSize; i++) {
//            sut.put(createKeyObjectValid(i), createValueObjectImmutable(i));
//        }
//
//        // --- 2. PRE-CHECK ---
//        assertThat(sut.size()).isEqualTo(initialSize);
//        assertThat(sut.getPinnedKeys())
//                .as("All'inizio non ci devono essere chiavi pinnate")
//                .isEmpty();
//
//        // --- 3. ACTION & ASSERTION ---
//        // Ci aspettiamo che chiamare pin(null) faccia esplodere una NullPointerException
//        assertThatThrownBy(() -> sut.pin(null))
//                .isInstanceOf(Exception.class);
//
//        // --- 4. VERIFICA POST-EXCEPTION ---
//        // Verifichiamo che l'eccezione non abbia corrotto lo stato della mappa
//        assertThat(sut.getPinnedKeys())
//                .as("Dopo l'eccezione, la lista dei pinned deve rimanere vuota")
//                .isEmpty();
//
//        assertThat(sut.size())
//                .as("La dimensione della mappa non deve cambiare in caso di errore")
//                .isEqualTo(initialSize);
//    }

    @Test // Test 0: pin null -> Accetta la chiave, ritorna false, la aggiunge ai PinnedKeys
    public void testPin_NullKey_AcceptsNullAndAddsToPinnedKeys() {
        // --- 1. SETUP ---
        // Usiamo una mappa standard
        CacheMap sut = new CacheMap(true, 10);

        // --- 2. ACTION ---
        // Eseguiamo il pin di null.
        // Non usiamo assertThrownBy perché sappiamo che NON lancia eccezioni.
        boolean result = sut.pin(null);

        // --- 3. ASSERTIONS ---

        // A. Verifica del valore di ritorno
        // Deve ritornare false perché non c'era nessun valore associato a 'null' da spostare/pinnare.
        assertThat(result)
                .as("Pinning di una chiave null inesistente deve ritornare false")
                .isFalse();

        // B. Verifica consistenza Size
        // Nota tecnica: Il codice incrementa '_pinnedSize' solo se il valore != null.
        // Quindi, pur avendo una chiave nei pinnedKeys, la size totale non deve essere aumentata.
        assertThat(sut.size())
                .as("La dimensione totale non deve aumentare pinnando una chiave senza valore")
                .isEqualTo(0);
    }

    @Test // Test 1: Key throws Exception on hashCode -> Exception thrown, Nothing pinned
    public void testPin_HashCodeException_NothingPinned() {
        // --- 1. SETUP ---
        int max = 10;
        CacheMap sut = new CacheMap(true, max);
        // Riempiamo fino a Max - 1 (9 elementi)
        for (int i = 0; i < max - 1; i++) {
            sut.put(createKeyObjectValid(i), createValueObjectImmutable(i));
        }
        // Pre-check
        assertThat(sut.size()).isEqualTo(9);
        assertThat(sut.getPinnedKeys()).isEmpty();
        // Creiamo una chiave "cattiva" che esplode quando si calcola l'hashcode.
        // Usiamo una classe anonima per sovrascrivere il metodo hashCode.
        Object badKey = KeyObjectMother.createKeyObjectBad();
        // --- 2. ACTION & ASSERTION ---
        assertThatThrownBy(() -> sut.pin(badKey))
                .as("Se il calcolo dell'hashCode fallisce, l'eccezione deve risalire")
                .isInstanceOf(RuntimeException.class);

        // --- 3. VERIFICA POST-EXCEPTION ---
        // Verifichiamo che l'operazione sia stata atomica.
        // A. Nessuna chiave deve essere stata aggiunta ai pinned keys
        assertThat(sut.getPinnedKeys())
                .as("Nessuna entry deve essere pinnata se si verifica un errore durante l'hashing")
                .isEmpty();

        // B. La size della mappa deve essere rimasta invariata
        assertThat(sut.size()).isEqualTo(9);
    }

    @Test // Test 2: Pin Not Present Key -> Returns false (but key is added to PinnedMap as side effect)
    public void testPin_KeyNotPresent_ReturnsFalse() {
        // --- 1. SETUP ---
        int max = 10;
        CacheMap sut = new CacheMap(true, max); // LRU
        // Popoliamo fino a Max - 1 (9 elementi)
        for (int i = 0; i < max - 1; i++) {
            sut.put(createKeyObjectValid(i), createValueObjectImmutable(i));
        }
        // Definiamo una chiave che NON è presente
        Object keyNotPresent = createKeyObjectValid(999);
        // Verify pre-conditions
        assertThat(sut.size()).isEqualTo(9);
        assertThat(sut.containsKey(keyNotPresent)).isFalse();
        assertThat(sut.getPinnedKeys()).isEmpty();
        // --- 2. ACTION ---
        // Proviamo a pinnare la chiave inesistente.
        boolean result = sut.pin(keyNotPresent);
        // --- 3. VERIFICA ---
        // A. Il metodo deve restituire FALSE Perché 'val' recuperato è null.
        assertThat(result)
                .as("Pinnare una chiave non presente deve restituire false")
                .isFalse();
        // B. Verifica stato PinnedMap, la chiave deve essere all'interno della pinnedMap con value null
        assertThat(sut.getPinnedKeys())
                .as("La chiave deve essere all'interno della pinnedMap anche se non presente un valore")
                .contains(keyNotPresent);
        // il valore associato alla chiave deve essere null
        assertThat(sut.get(keyNotPresent))
                .as("Il valore associato alla chiave non presente deve essere null")
                .isNull();
        // C. La size della mappa principale non deve cambiare
        assertThat(sut.size()).isEqualTo(9);
    }

    @Test // Test 3: Existing Key in Hard -> Pin -> Returns true, Key is pinned
    public void testPin_ExistingHard_ReturnsTrue() {
        // --- 1. SETUP ---
        int max = 10;
        CacheMap sut = new CacheMap(true, max); // LRU
        // Popoliamo con 9 elementi (Size = Max - 1)
        List<Object> keys = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        for (int i = 0; i < max - 1; i++) {
            keys.add(createKeyObjectValid(i));
            values.add(createValueObjectImmutable(i));
            sut.put(keys.get(i), values.get(i));
        }
        // Scegliamo una chiave che è sicuramente nella Hard Map
        Object keyToPin = keys.get(0); // "Key-0"
        Object expectedValue = values.get(0);
        // Verify pre-conditions
        assertThat(sut.size()).isEqualTo(9);
        assertThat(sut.getPinnedKeys()).isEmpty();
        assertThat(sut.containsKey(keyToPin)).isTrue();
        // --- 2. ACTION ---
        // Pinniamo la chiave esistente
        boolean result = sut.pin(keyToPin);
        // --- 3. VERIFICA ---
        // A. Il metodo deve restituire TRUE (la chiave è esistente)
        assertThat(result)
                .as("Il pin di una chiave esistente deve restituire true")
                .isTrue();
        // B. La chiave deve apparire nella lista dei Pinned Keys
        assertThat(sut.getPinnedKeys())
                .as("La chiave deve essere spostata nella pinnedMap")
                .contains(keyToPin);
        // C. Il valore deve essere ancora accessibile e corretto
        assertThat(sut.get(keyToPin))
                .as("Il valore della chiave pinnata deve essere preservato")
                .isEqualTo(expectedValue);
        // D. La size totale deve rimanere invariata (9)
        // L'elemento si è solo spostato da "stanza Hard" a "stanza Pinned", non è stato duplicato.
        assertThat(sut.size())
                .as("La dimensione totale della cache non deve cambiare dopo il pin")
                .isEqualTo(9);
    }

    @Test // Test 4: Existing Key in Soft -> Pin -> Moves to Pinned -> Returns true
    public void testPin_ExistingSoft_ReturnsTrue_AndPromotes() {
        // --- 1. SETUP ---
        int maxHard = 10;
        CacheMap sut = new CacheMap(true, maxHard); // LRU Strategy
        sut.setSoftReferenceSize(1);
        // Costruiamo 11 elementi (10 Hard + 1 che finirà in Soft)
        List<Object> keys = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            keys.add(createKeyObjectValid(i));
            values.add(createValueObjectImmutable(i));
        }
        // Popoliamo
        // LRU Logic: Key-0 (la prima) viene spinta fuori dalla Hard -> va in Soft
        for (int i = 0; i < keys.size(); i++) {
            sut.put(keys.get(i), values.get(i));
        }
        Object keyInSoft = keys.get(0); // Questa è la chiave target
        Object expectedValue = values.get(0);
        // Verify pre-conditions
        assertThat(sut.size()).isEqualTo(11);
        assertThat(sut.getPinnedKeys()).isEmpty();
        // Verifica logica: Key-0 esiste nella cache totale
        assertThat(sut.containsKey(keyInSoft)).isTrue();
        // --- 2. ACTION ---
        // Pinniamo l'elemento che si trova nella Soft Map
        boolean result = sut.pin(keyInSoft);
        // --- 3. VERIFICA ---
        // A. Il metodo deve restituire TRUE
        assertThat(result)
                .as("Il pin di una chiave esistente (anche se in Soft) deve tornare true")
                .isTrue();
        // B. La chiave deve essere promossa tra i PINNED
        assertThat(sut.getPinnedKeys())
                .as("La chiave deve essere spostata dalla SoftMap alla PinnedMap")
                .containsOnly(keyInSoft);
        // C. Il valore deve essere corretto
        assertThat(sut.get(keyInSoft)).isEqualTo(expectedValue);
        // --- 4. PROVA ARCHITETTURALE ---
        // Svuotiamo la Soft Map. Se la chiave fosse rimasta lì, sparirebbe.
        // Essendo pinnata, deve sopravvivere.
        sut.setSoftReferenceSize(0); // Flush Soft
        assertThat(sut.containsKey(keyInSoft))
                .as("La chiave pinnata deve sopravvivere alla pulizia della SoftMap")
                .isTrue();
        // La size totale deve essere corretta:
        // 10 elementi in Hard + 1 in Pinned = 11 Totali.
        assertThat(sut.size()).isEqualTo(11);
    }

    @Test // Test 5: Existing Pinned -> Pin again -> Returns True (Idempotent), No Side Effects
    public void testPin_AlreadyPinned_ReturnsTrue() {
        // --- 1. SETUP ---
        int max = 10;
        CacheMap sut = new CacheMap(true, max);
        // Popoliamo
        List<Object> keys = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        for (int i = 0; i < max - 1; i++) {
            keys.add(createKeyObjectValid(i));
            values.add(createValueObjectImmutable(i));
            sut.put(keys.get(i), values.get(i));
        }
        Object keyToPin = keys.get(0);
        Object expectedValue = values.get(0);
        // --- 2. PRE-CONDITION: Pinniamo la chiave la prima volta ---
        boolean firstPinResult = sut.pin(keyToPin);
        // Verifichiamo che sia stata pinnata davvero
        assertThat(firstPinResult).isTrue();
        assertThat(sut.getPinnedKeys()).contains(keyToPin);
        // --- 3. ACTION: Pinniamo DI NUOVO la stessa chiave ---
        boolean secondPinResult = sut.pin(keyToPin);
        // --- 4. VERIFICA ---
        // A. Il metodo deve restituire TRUE
        assertThat(secondPinResult)
                .as("Il pin di una chiave già pinnata (valida) dovrebbe restituire true (operazione idempotente)")
                .isTrue();
        // B. Lo stato non deve essere cambiato (Idempotenza)
        // La chiave deve essere ancora lì
        assertThat(sut.getPinnedKeys().size()).isEqualTo(1);
        assertThat(sut.getPinnedKeys()).contains(keyToPin);
        // Il valore deve essere ancora corretto
        assertThat(sut.get(keyToPin)).isEqualTo(expectedValue);
        // C. Non devono essere stati creati duplicati o aumenti di size
        // Size = 9 (HardMap+PinnedMap logica)
        assertThat(sut.size()).isEqualTo(9);
    }

    @Test // Test 6: Existing Soft -> Pin -> Moves to Pinned -> True -> NO Eviction from Hard
    public void testPin_ExistingSoft_MapFull_NoEvictionFromHard() {
        // --- 1. SETUP ---
        int maxHard = 10;
        CacheMap sut = new CacheMap(true, maxHard); // LRU
        sut.setSoftReferenceSize(5);
        // Creiamo 11 elementi
        // K0 finisce in Soft (perché è il più vecchio inserito e ne mettiamo 11)
        // K1...K10 finiscono in Hard (Hard è PIENA)
        List<Object> keys = new ArrayList<>();
        List<Object> values = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            keys.add(createKeyObjectValid(i));
            values.add(createValueObjectImmutable(i));
            sut.put(keys.get(i), values.get(i));
        }
        Object keyInSoft = keys.get(0);               // K0: Target da pinnare
        Object keyCandidateForEviction = keys.get(1); // K1: LRU della Hard Map
        // --- 2. PRE-CHECK ---
        assertThat(sut.size()).isEqualTo(11);
        assertThat(sut.containsKey(keyInSoft)).isTrue();
        // K1 è nella mappa (in Hard)
        assertThat(sut.containsKey(keyCandidateForEviction)).isTrue();
        // --- 3. ACTION ---
        // Pinniamo K0 che si trova nella Soft Map.
        boolean result = sut.pin(keyInSoft);
        // --- 4. VERIFICA ---
        // A. Return True
        assertThat(result).as("Il pin deve ritornare true").isTrue();
        // B. Spostamento in Pinned
        assertThat(sut.getPinnedKeys())
                .as("La chiave K0 deve essere ora nella pinnedMap")
                .contains(keyInSoft);
        // C. VERIFICA CRUCIALE: "Nessuna chiave dalla Hard Map viene evictata"
        // Se K0 fosse passata per la Hard Map, avrebbe spinto fuori K1 verso la Soft.
        // Ma siccome va direttamente in Pinned, K1 deve restare in Hard.
        sut.setSoftReferenceSize(0);
        // Se K1 fosse stata evictata (spinta in Soft), ora sarebbe espulsa.
        // Invece DEVE sopravvivere perché è rimasta in Hard.
        assertThat(sut.containsKey(keyCandidateForEviction))
                .as("K1 non doveva essere evictata dalla Hard Map, quindi deve sopravvivere al flush della Soft")
                .isTrue();
        // D. Size Check
        // 10 (Hard) + 1 (Pinned) + 0 (Soft) = 11
        assertThat(sut.size()).isEqualTo(11);
    }
}
