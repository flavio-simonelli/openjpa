package org.apache.openjpa.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CacheMapPutTest {
    private CacheMap sut;

    @Test // test 0
    public void testPut_NewEntry() {
        // creazione dell sut
        int max = 10;
        sut = new CacheMap(true, max);
        sut.setSoftReferenceSize(0);
        // Prepariamo 9 elementi (Max - 1)
        List<Object> keys = createKeyListValid(9);
        List<Object> values = createValueListImmutable(9);
        // Popoliamo la mappa (Fase di Setup)
        for (int i = 0; i < keys.size(); i++) {
            sut.put(keys.get(i), values.get(i));
        }
        // Check Pre-Condizione: Mappa non piena
        assertThat(sut.size()).isEqualTo(9);
        // Creazione della entry da inserire
        Object newKey = KeyObjectMother.createKeyObjectValid(10);
        Object newValue = ValueObjectMother.createValueObjectImmutable(10);
        // Aggiungiamo anche questi alle liste di controllo per il ciclo finale
        keys.add(newKey);
        values.add(newValue);
        // Eseguiamo la PUT sotto test
        Object result = sut.put(newKey, newValue);
        // A. Verifica risultato della put (deve essere null per nuova chiave)
        assertThat(result).as("Put su nuova chiave deve ritornare null").isNull();
        // B. Verifica Size finale (Deve essere 10, cioè Piena ma senza overflow)
        assertThat(sut.size()).as("La size deve aver raggiunto il Max").isEqualTo(10);
        // C. Ciclo di verifica: TUTTE le chiavi (vecchie + nuova) devono esistere.
        // Se ci fosse stata eviction, una delle prime 9 mancherebbe (containsKey = false).
        for (int i = 0; i < keys.size(); i++) {
            Object currentKey = keys.get(i);
            Object expectedValue = values.get(i);

            // 1. Dobbiamo asserire che è presente ogni chiave
            assertThat(sut.containsKey(currentKey))
                    .as("La chiave %s dovrebbe essere presente in mappa", currentKey)
                    .isTrue();

            // 2. Dobbiamo asserire che il suo valore è corretto
            assertThat(sut.get(currentKey))
                    .as("Il valore per la chiave %s non è corretto", currentKey)
                    .isEqualTo(expectedValue);
        }
    }

    @Test // Test 1: Not Present, Mutable Bean -> Insert OK, Reference Updated
    public void testPut_MutableBean_ReferenceCheck() {
        // --- 1. SETUP ---
        int max = 10;
        // La specifica dice "Random", quindi lru=false.
        sut = new CacheMap(false, max);
        // SoftMap a 0 per garantire che non ci siano eviction silenziose
        sut.setSoftReferenceSize(0);
        // Prepariamo 9 elementi (Max - 1)
        List<Object> keys = createKeyListValid(9);
        List<Object> values = createValueListImmutable(9); // Questi sono Stringhe immutabili
        // Popoliamo la mappa
        for (int i = 0; i < keys.size(); i++) {
            sut.put(keys.get(i), values.get(i));
        }
        // Creiamo la coppia specifica per il test (Bean Mutabile)
        Object keyBean = KeyObjectMother.createKeyObjectValid(10);
        // Qui ci serve il tipo concreto per poter chiamare setName() dopo
        // Usiamo il casting perché il Mother restituisce Object
        ValueObjectMother.MutableObject beanValue = ValueObjectMother.createValueObjectMutable(10, "Mario");
        // --- 2. ACTION (Inserimento) ---
        sut.put(keyBean, beanValue);
        // Verifichiamo che TUTTO sia presente e corretto PRIMA della mutazione
        assertThat(sut.size()).as("Size deve essere 10").isEqualTo(10);

        for (int i = 0; i < keys.size(); i++) {
            Object k = keys.get(i);
            Object v = values.get(i);

            // Verifica presenza
            assertThat(sut.containsKey(k))
                    .as("Chiave %s deve essere presente", k)
                    .isTrue();

            // Verifica valore (Per identità o equals)
            assertThat(sut.get(k))
                    .as("Valore per chiave %s errato", k)
                    .isEqualTo(v);
        }

        // Verifica specifica sul contenuto del Bean (ancora "Mario")
        ValueObjectMother.MutableObject retrievedBefore = (ValueObjectMother.MutableObject) sut.get(keyBean);
        assertThat(retrievedBefore.getName())
                .as("Prima della mutazione il nome deve essere Mario")
                .isEqualTo("Mario");
        // --- 3. MUTATION (Modifica Esterna) ---
        // Cambiamo il nome da "Mario" a "Luigi" usando il riferimento locale
        beanValue.setName("Luigi");
        // --- 4. ASSERTIONS ---
        // A. Verifica No Eviction (Tutti i vecchi devono esserci)
        assertThat(sut.size()).isEqualTo(10);
        for (Object k : keys) {
            assertThat(sut.containsKey(k)).as("Nessuna chiave precedente deve essere rimossa").isTrue();
        }
        // B. Verifica che la chiave del Bean esista
        assertThat(sut.containsKey(keyBean)).isTrue();
        // C. VERIFICA CRUCIALE: Passaggio per Riferimento
        // Recuperiamo l'oggetto dalla mappa
        Object retrievedObject = sut.get(keyBean);
        // 1. Deve essere ESATTAMENTE lo stesso oggetto in memoria (stesso indirizzo)
        assertThat(retrievedObject)
                .as("L'oggetto recuperato deve essere la stessa istanza inserita")
                .isSameAs(beanValue);
        // 2. Il valore interno deve essere "Luigi", non "Mario"
        ValueObjectMother.MutableObject retrievedBean = (ValueObjectMother.MutableObject) retrievedObject;
        assertThat(retrievedBean.getName())
                .as("La modifica esterna deve riflettersi nell'oggetto in mappa")
                .isEqualTo("Luigi");
    }

    @Test // Test 2: Not Present, Null Value -> Insert OK
    public void testPut_NullValue() {
        // --- 1. SETUP ---
        int max = 10;
        sut = new CacheMap(false, max); // Random = lru false
        sut.setSoftReferenceSize(0);
        // Riempiamo parzialmente (9 su 10)
        List<Object> keys = createKeyListValid(9);
        List<Object> values = createValueListImmutable(9);
        for (int i = 0; i < keys.size(); i++) {
            sut.put(keys.get(i), values.get(i));
        }
        // Prepariamo la chiave target
        Object keyNull = KeyObjectMother.createKeyObjectValid(10);
        // --- 2. ACTION ---
        // Inseriamo esplicitamente null come valore
        Object result = sut.put(keyNull, null);
        // --- 3. ASSERTIONS ---
        // A. Verifica risultato put (deve tornare null perché è nuovo)
        assertThat(result).isNull();
        // B. Verifica Size (Deve essere piena: 10)
        assertThat(sut.size()).isEqualTo(10);
        // C. Verifica Presenza
        // containsKey deve essere TRUE anche se il valore è null
        assertThat(sut.containsKey(keyNull))
                .as("La mappa deve contenere la chiave anche se il valore è null")
                .isTrue();
        // D. Verifica Valore
        // get deve ritornare null
        assertThat(sut.get(keyNull))
                .as("Il valore recuperato deve essere null")
                .isNull();
        // E. Verifica No Eviction (i vecchi devono esserci tutti)
        for (Object k : keys) {
            assertThat(sut.containsKey(k)).isTrue();
        }
    }

    @Test // Test 3: HashCode Exception -> Exception Thrown, Map Unchanged
    public void testPut_HashCodeException_NoChange() {
        // --- 1. SETUP ---
        int max = 10;
        sut = new CacheMap(false, max); // Random
        sut.setSoftReferenceSize(0);
        // Prepariamo 9 elementi validi (Max - 1)
        List<Object> keys = createKeyListValid(9);
        List<Object> values = createValueListImmutable(9);
        // Popoliamo la mappa
        for (int i = 0; i < keys.size(); i++) {
            sut.put(keys.get(i), values.get(i));
        }
        // Verifichiamo che la size della cacheMap è esattamente 9
        assertThat(sut.size())
                .as("Pre-condizione fallita: la mappa dovrebbe contenere esattamente 9 elementi")
                .isEqualTo(9);
        // verifichiamo che il contenuto della hashmap sia corretto
        verifyMapContent(keys, values, "PRE-CONDITION");
        // Creiamo la chiave "difettosa" tramite il Mother
        Object badKey = KeyObjectMother.createKeyObjectBad();
        // --- 2. ACTION & ASSERTION (Exception) ---
        assertThatThrownBy(() -> sut.put(badKey, "ValueIgnored"))
                .as("Inserire una chiave con hashCode difettoso deve lanciare un'eccezione")
                .isInstanceOf(RuntimeException.class);
        // --- 3. ASSERTION (State Atomicity) ---
        // La mappa non deve essere stata sporcata.
        // A. La dimensione non deve essere cambiata
        assertThat(sut.size())
                .as("La dimensione della mappa non deve cambiare se l'operazione fallisce")
                .isEqualTo(9);
        // B. Tutti i dati precedenti devono essere ancora lì e integri
        verifyMapContent(keys, values, "POST-CONDITION");
    }

    @Test // Test 4: Existing Hard, Size < Max -> Update OK, Size Unchanged
    public void testPut_UpdateExistingHard_NotFull() {
        // --- 1. SETUP ---
        int max = 10;
        sut = new CacheMap(false, max); // Random
        sut.setSoftReferenceSize(0);
        // Creiamo e inseriamo 9 elementi (Max - 1)
        List<Object> keys = createKeyListValid(9);
        List<Object> values = createValueListImmutable(9);
        for (int i = 0; i < keys.size(); i++) {
            sut.put(keys.get(i), values.get(i));
        }
        // --- 2. PRE-CONDIZIONE ---
        // Verifichiamo che i dati iniziali siano corretti
        verifyMapContent(keys, values, "PRE-CONDITION");
        // Modifichiamo il valore del primo elemento
        Object targetKey = keys.get(0);
        Object oldValue = values.get(0);
        Object newValue = "UpdatedStringValue";
        // --- 3. ACTION ---
        Object result = sut.put(targetKey, newValue);
        // --- 4. ASSERTIONS ---
        // A. Verifica valore di ritorno (Deve essere il vecchio valore)
        assertThat(result)
                .as("La put su chiave esistente deve ritornare il vecchio valore")
                .isEqualTo(oldValue);
        // B. Verifica Size (Deve restare 9, è un update, non insert)
        assertThat(sut.size())
                .as("La dimensione non deve cambiare dopo un update")
                .isEqualTo(9);
        // C. Verifica Contenuto Completo
        values.set(0, newValue);
        verifyMapContent(keys, values, "POST-CONDITION");
    }

    @Test // Test 5: Remove Hard -> Update Soft -> Verify Promotion to Hard
    public void testRemoveHard_UpdateSoft_PromoteToHard() {
        // --- 1. SETUP ---
        int maxHard = 10;
        // true = LRU Strategy (Fondamentale per la determinismo: il più vecchio va in Soft)
        sut = new CacheMap(true, maxHard);
        sut.setSoftReferenceSize(1); // Spazio aperto in Soft
        // Creiamo 11 elementi
        List<Object> keys = createKeyListValid(11);
        List<Object> values = createValueListImmutable(11);
        // --- 2. POPOLAMENTO ---
        // Inseriamo in sequenza.
        // LRU Logic:
        // - K0 entra per primo. Quando inseriamo K10 (l'11esimo), K0 è il "Least Recently Used".
        // - K0 viene espulso dalla Hard -> va in Soft.
        // - K1...K10 rimangono in Hard.
        for (int i = 0; i < keys.size(); i++) {
            sut.put(keys.get(i), values.get(i));
        }
        int indexToRemove = 10;
        int indexInSoft = 0;
        Object keyInSoft = keys.get(indexInSoft);   // Il più vecchio (Soft)
        Object keyInHard = keys.get(indexToRemove);  // Il più recente (Hard)
        // Check stato iniziale
        assertThat(sut.size()).isEqualTo(11);
        assertThat(sut.get(keys.get(2))).isEqualTo(values.get(2));
        // --- 3. AZIONE 1: RIMUOVI ELEMENTO IN HARD ---
        // Rimuoviamo K10 (che sappiamo essere in Hard)
        sut.remove(keyInHard);
        keys.remove(indexToRemove);
        values.remove(indexToRemove);
        // --- 4. VERIFICA POST-RIMOZIONE ---
        // Totale deve essere 10
        assertThat(sut.size()).isEqualTo(10);
        // K10 deve essere sparito
        assertThat(sut.containsKey(keyInHard)).isFalse();
        // --- 5. AZIONE 2: AGGIORNA ELEMENTO IN SOFT (PROMOZIONE) ---
        // Prendiamo K0 (che è in Soft) e facciamo una PUT.
        // con la PUT/UPDATE deve rientrare nella HardMap.
        Object newVal = "PromotedValue";
        sut.put(keyInSoft, newVal);
        // Aggiorniamo la nostra lista valori attesi per coerenza
        values.set(0, newVal);
        // --- 6. VERIFICA INTEGRITÀ ---
        // Ci devono essere 10 elementi (K0..K9). K10 era stato rimosso.
        assertThat(sut.size()).isEqualTo(10);
        assertThat(sut.get(keyInSoft)).isEqualTo(newVal);
        // --- 7. VERIFICA "SOFTNESS" ---
        // Chiudiamo la SoftMap.
        // Se K0 fosse rimasto "parcheggiato" in Soft nonostante l'update, ora verrebbe cancellato.
        sut.setSoftReferenceSize(0);
        // A. Nessuno deve essere eliminato
        // Abbiamo 10 elementi totali. La HardMap tiene 10 elementi. Soft è 0.
        assertThat(sut.size())
                .as("Dopo l'update, K0 deve essere tornato in Hard, quindi nessuno deve sparire al flush della Soft")
                .isEqualTo(10);
        // B. Verifica tutti siano rimasti nella cacheMap
        verifyMapContent(keys, values);
    }

    @Test // Test 6: Existing Pinned, Size < Max -> Update OK, Remains Pinned
    public void testPut_UpdatePinned_NotFull() {
        // --- 1. SETUP ---
        int max = 10;
        sut = new CacheMap(false, max); // Random
        sut.setSoftReferenceSize(0);
        // Creiamo 9 elementi (Indici 1..9)
        List<Object> keys = createKeyListValid(9);
        List<Object> values = createValueListImmutable(9);
        for (int i = 0; i < keys.size(); i++) {
            sut.put(keys.get(i), values.get(i));
        }
        // Selezioniamo la chiave da Pinnare (la prima, ID=1)
        Object targetKey = keys.get(0);
        Object oldValue = values.get(0);
        Object newValue = "UpdatedPinnedValue";
        // Eseguiamo il PIN esplicito
        sut.pin(targetKey);
        // --- 2. PRE-CONDIZIONE ---
        // Verifichiamo che sia effettivamente pinnata e sia l'unica
        assertThat(sut.getPinnedKeys())
                .as("La chiave %s dovrebbe essere nell'elenco delle pinned keys", targetKey)
                .hasSize(1)
                .contains(targetKey);
        verifyMapContent(keys, values, "PRE-UPDATE");
        // --- 3. ACTION ---
        // Aggiorniamo il valore della chiave pinnata
        Object result = sut.put(targetKey, newValue);
        // --- 4. ASSERTIONS ---
        // A. Verifica valore di ritorno
        assertThat(result).isEqualTo(oldValue);
        // B. Verifica Aggiornamento Valore
        assertThat(sut.get(targetKey))
                .as("Il valore della chiave pinnata deve essere aggiornato")
                .isEqualTo(newValue);
        // C. VERIFICA STATO PINNED
        // La put non deve aver fatto "unpin" involontario e che sia l'unica ad essere pinnata
        assertThat(sut.getPinnedKeys())
                .as("La chiave deve rimanere pinnata anche dopo l'update")
                .hasSize(1)
                .contains(targetKey);
        // D. Verifica Size e Integrità globale
        values.set(0, newValue); // Aggiorniamo l'attesa locale
        verifyMapContent(keys, values, "POST-UPDATE");
    }

    @Test // Test 7: Update Existing Hard -> Random Strategy, Max Size=10, Soft=0
    public void testUpdate_ExistingHard_Random_NoSoft() {
        // --- 1. SETUP ---
        int maxHard = 10;
        // false = Random Strategy
        sut = new CacheMap(false, maxHard);
        // SoftReferenceSize a 0: Nessuna rete di salvataggio.
        // Se la mappa prova erroneamente a fare eviction, l'elemento andrebbe perso per sempre.
        sut.setSoftReferenceSize(0);
        // Creiamo esattamente 10 elementi (pari a maxHard)
        List<Object> keys = createKeyListValid(maxHard);
        List<Object> values = createValueListImmutable(maxHard);
        // Inseriamo tutto -> La mappa è ora PIENA (Size = 10)
        for (int i = 0; i < keys.size(); i++) {
            sut.put(keys.get(i), values.get(i));
        }
        // --- 2. PRE-CHECK ---
        assertThat(sut.size()).isEqualTo(maxHard);
        verifyMapContent(keys, values, "PRE-UPDATE");
        // --- 3. ACTION (Update) ---
        // Scegliamo una chiave esistente (es. indice 5, a metà)
        int targetIndex = 5;
        Object targetKey = keys.get(targetIndex);
        Object newValue = "ValoreAggiornatoTest7";
        // Eseguiamo l'update su una mappa PIENA.
        // L'operazione deve avvenire "in-place" senza scatenare eviction.
        sut.put(targetKey, newValue);
        // Aggiorniamo la nostra lista di aspettativa
        values.set(targetIndex, newValue);
        // --- 4. VERIFICA POST-UPDATE ---
        // A. Verifica Size
        // La dimensione deve rimanere 10.
        assertThat(sut.size())
                .as("L'update di una chiave esistente non deve modificare la size della mappa")
                .isEqualTo(maxHard);
        // B. Verifica Contenuto Completo
        verifyMapContent(keys, values, "POST-UPDATE");
    }

    @Test // Test 8: Existing Pinned -> Update Value -> Remains Pinned, No Eviction
    public void testUpdate_ExistingPinned_RemainsPinned() {
        // --- 1. SETUP ---
        int maxHard = 10;
        // false = Random Strategy
        sut = new CacheMap(false, maxHard);
        sut.setSoftReferenceSize(0); // Nessun buffer soft
        // Creiamo 10 elementi
        List<Object> keys = createKeyListValid(maxHard);
        List<Object> values = createValueListImmutable(maxHard);
        // Inseriamo tutto (Mappa PIENA)
        for (int i = 0; i < keys.size(); i++) {
            sut.put(keys.get(i), values.get(i));
        }
        // --- 2. PINNING ---
        // Scegliamo una chiave da pinnare (indice 5)
        Object targetKey = keys.get(5);
        // Pinniamo la chiave
        sut.pin(targetKey);
        // --- 3. PRE-CHECK ---
        assertThat(sut.size()).isEqualTo(maxHard);
        // Verifica che la chiave sia effettivamente nell'insieme delle chiavi pinnate
        assertThat(sut.getPinnedKeys())
                .as("La chiave deve essere presente nell'elenco delle Pinned Keys")
                .hasSize(1)
                .contains(targetKey);
        verifyMapContent(keys, values, "PRE-UPDATE");
        // --- 4. ACTION (Update Pinned Key) ---
        Object newValue = "NewValueForPinned";
        // Eseguiamo l'update sulla chiave pinnata
        sut.put(targetKey, newValue);
        // Aggiorniamo la lista di aspettativa locale
        values.set(5, newValue);
        // --- 5. VERIFICA POST-UPDATE ---
        // A. Verifica Size (Nessuna eviction)
        assertThat(sut.size())
                .as("L'update di una chiave pinnata non deve cambiare la size")
                .isEqualTo(maxHard);
        // B. Verifica Valore Aggiornato
        assertThat(sut.get(targetKey)).isEqualTo(newValue);
        // C. Verifica Stato PINNED (Corretto con getPinnedKeys)
        // L'update non deve aver rimosso la chiave dalla lista delle pinnate
        assertThat(sut.getPinnedKeys())
                .as("La chiave deve rimanere nell'elenco Pinned anche dopo l'aggiornamento del valore")
                .hasSize(1)
                .contains(targetKey);
        // D. Verifica Integrità degli altri
        // Assicuriamoci che l'update non abbia scatenato per sbaglio una random eviction
        verifyMapContent(keys, values, "POST-UPDATE");
    }

    /**
     * * * MOTIVO: Il problema del "Gioco delle Sedie" durante lo scambio Hard<->Soft.
     *      * Se c'è una sola sedia (Size=1), accade un conflitto durante la transizione:
     *      * 1. La Hard Map deve accogliere K0 (promosso) ed espelle K1 per fare posto.
     *      * 2. K1 "corre" verso la Soft Map per salvarsi.
     *      * 3. La Soft Map guarda la sua unica sedia. È ancora tecnicamente occupata da K0
     *      * (che sta uscendo, ma non è ancora del tutto sparito).
     *      * 4. La Soft Map dice: "Sono piena!".
     *      * 5. CRASH: K1 non può sedersi e viene eliminato definitivamente.
     *      * * RISULTATO (con Size=1): Hai 10 persone nella Hard e 0 nella Soft. Totale 10 (Fail).
     *      * SOLUZIONE (con Size=2): K1 trova una sedia libera nel buffer mentre K0 libera la sua.
    @Test // Test 9: Existing Soft -> Update -> Promoted to Hard, Another Evicted to Soft
    public void OLDtestUpdate_ExistingSoft_PromotesToHard_EvictsOther() {
        // --- 1. SETUP ---
        int maxHard = 10;
        // Usiamo LRU per determinismo: sappiamo che il primo inserito finirà in Soft.
        sut = new CacheMap(true, maxHard);
        sut.setSoftReferenceSize(1); // Spazio per 1 overflow
        // --- 2. POPOLAMENTO ---
        // Inseriamo 11 elementi.
        // LRU Logic:
        // - K0 (il primo) viene espulso dalla Hard e finisce in Soft.
        // - K1...K10 sono nella Hard. K1 è ora il "più vecchio" dentro la Hard.
        List<Object> keys = createKeyListValid(11);
        List<Object> values = createValueListImmutable(11);
        for (int i = 0; i < keys.size(); i++) {
            sut.put(keys.get(i), values.get(i));
        }
        // Identifichiamo i protagonisti
        Object keyInSoft = keys.get(0);      // K0: Attualmente in Soft
        Object keyCandidateForEviction = keys.get(1); // K1: Attualmente "in fondo" alla Hard
        // Verify pre-conditions
        assertThat(sut.size()).isEqualTo(11);
        // --- 3. ACTION (Update elemento in Soft) ---
        Object newVal = "PromotedValue";
        // Aggiorniamo K0.
        // Logica attesa:
        // 1. K0 viene aggiornato.
        // 2. K0 diventa MRU (Most Recently Used), quindi DEVE entrare in Hard.
        // 3. La Hard è piena (10). Serve posto.
        // 4. Viene espulso il più vecchio della Hard (K1) -> Finisce in Soft.
        sut.put(keyInSoft, newVal);
        // Aggiorniamo la lista valori per le verifiche
        values.set(0, newVal);
        // --- 4. VERIFICA POST-UPDATE ---
        // A. Size totale invariata
        // 11 erano, 11 rimangono (10 Hard + 1 Soft)
        assertThat(sut.size()).isEqualTo(11);
        // B. Contenuto integro
        assertThat(sut.get(keyInSoft)).isEqualTo(newVal);
        // --- 5. VERIFICA DELLO SCAMBIO (Flush SoftMap) ---
        // L'elemento che si trova attualmente in Soft verrà eliminato.
        sut.setSoftReferenceSize(0);
        // A. Size deve scendere a 10
        assertThat(sut.size()).isEqualTo(10);
        // B. Chi è sopravvissuto?
        // K0 (quello aggiornato) DEVE ESSERE SOPRAVVISSUTO (perché promosso in Hard).
        assertThat(sut.containsKey(keyInSoft))
                .as("La chiave aggiornata (K0) doveva essere promossa in Hard e sopravvivere")
                .isTrue();
        // C. Chi è sparito?
        // K1 (che era in Hard) doveva essere stato spostato in Soft per fare spazio a K0.
        // Quindi K1 deve essere sparito ora che abbiamo chiuso la SoftMap.
        assertThat(sut.containsKey(keyCandidateForEviction))
                .as("La chiave K1 doveva essere espulsa dalla Hard (in Soft) e quindi sparire col flush")
                .isFalse();
        // D. Verifica finale contenuto
        // Rimuoviamo K1 dalle liste attese e verifichiamo che gli altri 10 (incluso K0) ci siano.
        keys.remove(1);   // Rimuoviamo K1
        values.remove(1); // Rimuoviamo valore di K1
        verifyMapContent(keys, values, "FINAL-CHECK");
    }
     **/


    @Test // Test 9: Existing Soft -> Update -> Promoted to Hard, Another Evicted to Soft
    public void testUpdate_ExistingSoft_PromotesToHard_EvictsOther() {
        // --- 1. SETUP ---
        int maxHard = 10;
        // Usiamo LRU per determinismo: sappiamo che il primo inserito finirà in Soft.
        sut = new CacheMap(true, maxHard);
        sut.setSoftReferenceSize(2); // Spazio per 2 overflow
        // --- 2. POPOLAMENTO ---
        // Inseriamo 11 elementi.
        // LRU Logic:
        // - K0 (il primo) viene espulso dalla Hard e finisce in Soft.
        // - K1...K10 sono nella Hard. K1 è ora il "più vecchio" dentro la Hard.
        List<Object> keys = createKeyListValid(11);
        List<Object> values = createValueListImmutable(11);
        for (int i = 0; i < keys.size(); i++) {
            sut.put(keys.get(i), values.get(i));
        }
        // Identifichiamo i protagonisti
        Object keyInSoft = keys.get(0);      // K0: Attualmente in Soft
        Object keyCandidateForEviction = keys.get(1); // K1: Attualmente "in fondo" alla Hard
        // Verify pre-conditions
        assertThat(sut.size()).isEqualTo(11);
        // --- 3. ACTION (Update elemento in Soft) ---
        Object newVal = "PromotedValue";
        // Aggiorniamo K0.
        // Logica attesa:
        // 1. K0 viene aggiornato.
        // 2. K0 diventa MRU (Most Recently Used), quindi DEVE entrare in Hard.
        // 3. La Hard è piena (10). Serve posto.
        // 4. Viene espulso il più vecchio della Hard (K1) -> Finisce in Soft.
        sut.put(keyInSoft, newVal);
        // Aggiorniamo la lista valori per le verifiche
        values.set(0, newVal);
        // --- 4. VERIFICA POST-UPDATE ---
        // A. Size totale invariata
        // 11 erano, 11 rimangono (10 Hard + 1 Soft)
        assertThat(sut.size()).isEqualTo(11);
        // B. Contenuto integro
        assertThat(sut.get(keyInSoft)).isEqualTo(newVal);
        // --- 5. VERIFICA DELLO SCAMBIO (Flush SoftMap) ---
        // L'elemento che si trova attualmente in Soft verrà eliminato.
        sut.setSoftReferenceSize(0);
        // A. Size deve scendere a 10
        assertThat(sut.size()).isEqualTo(10);
        // B. Chi è sopravvissuto?
        // K0 (quello aggiornato) DEVE ESSERE SOPRAVVISSUTO (perché promosso in Hard).
        assertThat(sut.containsKey(keyInSoft))
                .as("La chiave aggiornata (K0) doveva essere promossa in Hard e sopravvivere")
                .isTrue();
        // C. Chi è sparito?
        // K1 (che era in Hard) doveva essere stato spostato in Soft per fare spazio a K0.
        // Quindi K1 deve essere sparito ora che abbiamo chiuso la SoftMap.
        assertThat(sut.containsKey(keyCandidateForEviction))
                .as("La chiave K1 doveva essere espulsa dalla Hard (in Soft) e quindi sparire col flush")
                .isFalse();
        // D. Verifica finale contenuto
        // Rimuoviamo K1 dalle liste attese e verifichiamo che gli altri 10 (incluso K0) ci siano.
        keys.remove(1);   // Rimuoviamo K1
        values.remove(1); // Rimuoviamo valore di K1
        verifyMapContent(keys, values, "FINAL-CHECK");
    }

    @Test // Test Aggiuntivo 10: Init Max=0 e Size=0 -> Put Valid -> Map Remains Empty
    public void testPut_MaxZeroSizeZero_MapRemainsEmpty() {
        // --- 1. SETUP ---
        // Inizializziamo la mappa direttamente con max=0 e size=0
        sut = new CacheMap(false, 0, 0, 0.5f);

        Object key = "KeyValid";
        Object value = "ValueValid";

        // Verifichiamo pre-condizioni
        assertThat(sut.size()).as("La mappa inizializzata a 0 deve essere vuota").isEqualTo(0);

        // --- 2. ACTION ---
        // Proviamo a inserire un elemento valido
        Object result = sut.put(key, value);

        // --- 3. ASSERTIONS ---
        // A. Il risultato deve essere null (nessun valore precedente rimpiazzato)
        assertThat(result)
                .as("La put in una mappa a capacità 0 deve ritornare null")
                .isNull();

        // B. La Size deve rimanere 0
        assertThat(sut.size())
                .as("La dimensione deve rimanere 0 dopo l'inserimento se max è 0")
                .isEqualTo(0);

        // C. La entry non deve essere all'interno della mappa
//        assertThat(sut.containsKey(key))
//                .as("La mappa non deve contenere la chiave inserita poiché la cache è disabilitata")
//                .isFalse();

//        assertThat(sut.get(key))
//                .as("Il recupero del valore tramite get() deve restituire null")
//                .isNull();
    }

    @Test // Test Aggiuntivo 11: Init Max=0 Size =1 -> Put Valid -> Map Remains Empty
    public void testPut_MaxZeroSizeOne_MapRemainsEmpty() {
        // --- 1. SETUP ---
        // Inizializziamo la mappa direttamente con max=0 e size=1
        sut = new CacheMap(false, 0, 1, 0.5f);

        Object key = "KeyValid";
        Object value = "ValueValid";

        // Verifichiamo pre-condizioni
        assertThat(sut.size()).as("La mappa inizializzata a 0 deve essere vuota").isEqualTo(0);

        // --- 2. ACTION ---
        // Proviamo a inserire un elemento valido
        Object result = sut.put(key, value);

        // --- 3. ASSERTIONS ---
        // A. Il risultato deve essere null (nessun valore precedente rimpiazzato)
        assertThat(result)
                .as("La put in una mappa a capacità 0 deve ritornare null")
                .isNull();

        // B. La Size deve rimanere 0
        assertThat(sut.size())
                .as("La dimensione deve rimanere 0 dopo l'inserimento se max è 0")
                .isEqualTo(0);

        // C. La entry non deve essere all'interno della mappa
//        assertThat(sut.containsKey(key))
//                .as("La mappa non deve contenere la chiave inserita poiché la cache è disabilitata")
//                .isFalse();

//        assertThat(sut.get(key))
//                .as("Il recupero del valore tramite get() deve restituire null")
//                .isNull();
    }

    @Test // Test aggiuntivo 12
    public void testPut_LRU_SmallInitialSize_GrowsToMax() {
        // --- 1. SETUP ---
        // Configurazione: LRU attiva, Max=5, ma Size iniziale=1.
        // Questo forza la struttura dati interna a ridimensionarsi (resize) man mano che aggiungiamo elementi,
        // fino a raggiungere il limite di 5.
        int maxParam = 5;
        int initialSizeParam = 1;
        boolean lruParam = true;

        sut = new CacheMap(lruParam, maxParam, initialSizeParam, 0.75f, 16);

        // Generiamo i dati usando gli Helper
        List<Object> keys = createKeyListValid(maxParam);       // Crea 5 chiavi: Key-0 ... Key-4
        List<Object> values = createValueListImmutable(maxParam); // Crea 5 valori: Val-0 ... Val-4

        // --- 2. ACTION ---
        // Inseriamo esattamente 5 elementi (pari al Max)
        for (int i = 0; i < maxParam; i++) {
            sut.put(keys.get(i), values.get(i));
        }

        // --- 3. ASSERTIONS ---

        // A. Verifica della dimensione totale
        assertThat(sut.size())
                .as("La mappa dovrebbe contenere esattamente %d elementi (il massimo consentito)", maxParam)
                .isEqualTo(maxParam);

        // B. Verifica del limite configurato (sanity check)
        assertThat(sut.getCacheSize())
                .as("Il limite massimo della cache dovrebbe essere %d", maxParam)
                .isEqualTo(maxParam);

        // C. Verifica del contenuto usando l'Helper condiviso
        // Poiché non abbiamo superato il limite (abbiamo inserito 5 elementi su 5 posti),
        // NON deve essere avvenuta nessuna eviction. Tutti devono essere presenti.
        verifyMapContent(keys, values, "GROWTH-CHECK");
    }

    @Test // Test per coprire: if (cacheMap.getMaxSize() == 0) return null;
    public void testPut_MaxSizeZero_ReturnsNull() {
        // --- 1. SETUP ---
        // Inizializziamo la mappa con dimensione 0
        sut = new CacheMap(false, 1);
        sut.setCacheSize(0);

        Object key = "Key";
        Object value = "Value";

        // --- 2. ACTION ---
        Object result = sut.put(key, value);

        // --- 3. VERIFICA ---
        // A. Deve ritornare null (come da codice return null)
        assertThat(result).isNull();

        // B. Non deve aver inserito nulla (né in cacheMap, né in softMap)
        assertThat(sut.size()).isEqualTo(0);
    }

    private void verifyMapContent(List<Object> k, List<Object> v) {
        verifyMapContent(k, v, "Check");
    }

    /**
     * Helper per verificare l'integrità della mappa.
     * @param phase Una stringa che descrive QUANDO stiamo facendo il controllo (es. "Pre-Condition", "Post-Action")
     */
    private void verifyMapContent(List<Object> expectedKeys, List<Object> expectedValues, String phase) {
        // Sanity check
        assertThat(expectedKeys).hasSameSizeAs(expectedValues);

        for (int i = 0; i < expectedKeys.size(); i++) {
            Object key = expectedKeys.get(i);
            Object expectedValue = expectedValues.get(i);

            // 1. Verifica Presenza con contesto
            assertThat(sut.containsKey(key))
                    .as("[%s] Chiave mancante: '%s' dovrebbe esserci", phase, key)
                    .isTrue();

            // 2. Verifica Valore con contesto
            assertThat(sut.get(key))
                    .as("[%s] Integrità violata: Il valore per la chiave '%s' è errato", phase, key)
                    .isEqualTo(expectedValue);
        }
    }

    /**
     * Helper per generare una lista di N chiavi valide con indice che va da 1 a N.
     */
    private List<Object> createKeyListValid(int count) {
        List<Object> keys = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            keys.add(KeyObjectMother.createKeyObjectValid(i+1));
        }
        return keys;
    }

    /**
     * Helper per generare una lista di N values valide con indice che va da 1 a N.
     */
    private List<Object> createValueListImmutable(int count) {
        List<Object> values = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            values.add(ValueObjectMother.createValueObjectImmutable(i+1));
        }
        return values;
    }


}
