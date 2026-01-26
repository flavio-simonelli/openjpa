package org.apache.openjpa.util;

import org.apache.openjpa.kernel.OpenJPAStateManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class ProxyManagerImplNewCollectionProxyTest {

    private ProxyManagerImpl proxyManager;

    @Mock
    private OpenJPAStateManager smMock;

    private final int FIELD_ID = 1;

    @Before
    public void setUp() {
        proxyManager = new ProxyManagerImpl();
        // Reset di base per ogni test per essere sicuri del comportamento nullo
        reset(smMock);
    }

    /**
     * Helper per configurare velocemente i flag del manager.
     */
    private void configureManager(boolean trackChanges, boolean assertType, boolean delay) {
        proxyManager.setTrackChanges(trackChanges);
        proxyManager.setAssertAllowedType(assertType);
        proxyManager.setDelayCollectionLoading(delay);
    }

    @Test // 0: Input Nullo
    public void newCollectionProxy_WithNullType_ShouldThrowException() {
        // --- SETUP ---
        configureManager(false, false, false);
        // --- EXEC & VERIFY ---
        assertThatThrownBy(() ->
                proxyManager.newCollectionProxy(null, null, null, false)
        ).isInstanceOf(Exception.class);
    }

    @Test // 1: Tipo non valido (String invece di Collection)
    public void newCollectionProxy_WithNonCollectionType_ShouldThrowException() {
        // --- SETUP ---
        configureManager(false, false, false);
        Class<?> invalidType = String.class;
        // --- EXEC & VERIFY ---
        assertThatThrownBy(() ->
                proxyManager.newCollectionProxy(invalidType, null, null, false)
        ).isInstanceOf(Exception.class);
    }

    @Test // 2: ArrayList, Element null, No Flags
    @SuppressWarnings("unchecked")
    public void newCollectionProxy_ArrayList_Untyped_MixedTypes_ShouldAcceptAnythingAndNotifyDirty() {
        // --- SETUP ---
        configureManager(false, false, false);
        Class<?> isolationClass = new ArrayList(){}.getClass();
        List proxyList = (List) proxyManager.newCollectionProxy(isolationClass, null, null, false);
        // --- 1 VERIFY ---
        assertThat(proxyList).isNotNull().isInstanceOf(ArrayList.class);
        assertThat(proxyList).isNotInstanceOf(ChangeTracker.class);
        // Setup Owner
        ((Proxy) proxyList).setOwner(smMock, FIELD_ID);
        // --- SECOND VERIFY ---
        // --- add(E e) -> Aggiungo Stringa ---
        proxyList.add("StringItem");
        verify(smMock, times(1)).dirty(FIELD_ID);
        assertThat(proxyList).containsExactly("StringItem");
        reset(smMock);
        // --- add(E e) -> Aggiungo Integer ---
        proxyList.add(100);
        verify(smMock, times(1)).dirty(FIELD_ID);
        assertThat(proxyList).containsExactly("StringItem", 100); // Lista mista
        reset(smMock);
        // --- add(int index, E element) -> Inserisco Double in testa ---
        proxyList.add(0, 99.9d);
        verify(smMock, times(1)).dirty(FIELD_ID);
        assertThat(proxyList).containsExactly(99.9d, "StringItem", 100);
        reset(smMock);
        // --- addAll(Collection) -> Aggiungo Boolean e Long ---
        List<Object> mixedCollection = new ArrayList<>();
        mixedCollection.add(true);
        mixedCollection.add(123456789L);
        proxyList.addAll(mixedCollection);
        verify(smMock, atLeast(2)).dirty(FIELD_ID);
        assertThat(proxyList).containsExactly(99.9d, "StringItem", 100, true, 123456789L);
        reset(smMock);
        // --- set(int index, E element) -> Sostituisco Stringa con un Object ---
        Object customObj = new Object();
        proxyList.set(1, customObj); // Sostituisce "StringItem"
        verify(smMock, times(1)).dirty(FIELD_ID);
        assertThat(proxyList).containsExactly(99.9d, customObj, 100, true, 123456789L);
        reset(smMock);
        // --- remove(Object o) -> Rimuovo il Boolean ---
        proxyList.remove(true);
        verify(smMock, times(1)).dirty(FIELD_ID);
        assertThat(proxyList).containsExactly(99.9d, customObj, 100, 123456789L);
        reset(smMock);
        // --- ListIterator.add() -> Inserisco Float nel mezzo ---
        java.util.ListIterator listIt = proxyList.listIterator();
        listIt.next(); // Salto 99.9d
        listIt.add(1.5f); // Inserisco Float
        verify(smMock, atLeast(1)).dirty(FIELD_ID);
        assertThat(proxyList).containsExactly(99.9d, 1.5f, customObj, 100, 123456789L);
        reset(smMock);
    }

    @Test // 3: ArrayList, Element String, No Flags (Permissive Type Check)
    @SuppressWarnings("unchecked") // Sopprimo i warning perché stiamo testando intenzionalmente la Type Erasure
    public void newCollectionProxy_ArrayList_TypedString_NoAssert_ShouldAcceptInvalidTypes() {
        // --- SETUP ---
        configureManager(false, false, false);
        Class<?> isolationClass = new ArrayList(){}.getClass();
        List proxyList = (List) proxyManager.newCollectionProxy( isolationClass, String.class, null, false);
        // --- FIRST VERIFY ---
        assertThat(proxyList).isNotNull().isInstanceOf(ArrayList.class);
        assertThat(proxyList).isNotInstanceOf(ChangeTracker.class);
        // setup Owner
        ((Proxy) proxyList).setOwner(smMock, FIELD_ID);
        // Action 1: Inserimento Valido (String)
        proxyList.add("Stringa Valida");
        // Verify 1: Verifica puntuale e reset
        verify(smMock, times(1)).dirty(FIELD_ID);
        reset(smMock);
        // Action 2: Inserimento "Invalido" (Integer)
        proxyList.add(123);
        // Verify 2: Deve aver segnato dirty anche per il tipo errato
        verify(smMock, times(1)).dirty(FIELD_ID);
        reset(smMock);
        // Verifichiamo che la lista contenga effettivamente entrambi gli oggetti eterogenei
        assertThat(proxyList)
                .hasSize(2)
                .contains("Stringa Valida", 123);
    }

    @Test // 4: ArrayList, Number, No Flags
    @SuppressWarnings("unchecked")
    public void newCollectionProxy_ArrayList_Polymorphic_ShouldAcceptSubtypes() {
        configureManager(false, false, false);
        Class<?> isolationClass = new ArrayList(){}.getClass();
        List proxyList = (List) proxyManager.newCollectionProxy(isolationClass, Number.class, null, false);
        // --- FIRST VERIFY ---
        assertThat(proxyList).isNotNull().isInstanceOf(ArrayList.class);
        assertThat(proxyList).isNotInstanceOf(ChangeTracker.class);
        // set owner
        ((Proxy) proxyList).setOwner(smMock, FIELD_ID);
        // Action 1: Inserimento invalido (String)
        proxyList.add("Stringa");
        // Verify 1
        verify(smMock, times(1)).dirty(FIELD_ID);
        reset(smMock);
        // Action 2: Inserimento "valido" (Integer)
        proxyList.add(123);
        // Verify 2
        verify(smMock, times(1)).dirty(FIELD_ID);
        reset(smMock);
        // Verifichiamo che la lista contenga effettivamente entrambi gli oggetti eterogenei
        assertThat(proxyList)
                .hasSize(2)
                .contains("Stringa", 123);
    }

    @Test // Test 5 Variation: ArrayList, Element Number, AAT true
    @SuppressWarnings("unchecked")
    public void newCollectionProxy_ArrayList_TypedNumber_AATTrue_Polymorphism() {
        // --- SETUP ---
        // trackChanges=false, assertAllowedType=true, delayCollectionLoading=false
        configureManager(false, true, false);
        // Creiamo una lista tipizzata su 'Number'.
        Class<?> isolationClass = new ArrayList(){}.getClass();
        List proxyList = (List) proxyManager.newCollectionProxy(isolationClass, Number.class, null, false);
        // Verifica preliminare
        assertThat(proxyList).isNotNull().isInstanceOf(ArrayList.class);
        // Setup Owner
        ((Proxy) proxyList).setOwner(smMock, FIELD_ID);
        // --- Action 1: Inserimento Valido (Integer) ---
        proxyList.add(100); // 100 è un Integer, che estende Number
        verify(smMock, times(1)).dirty(FIELD_ID);
        reset(smMock);
        // --- Action 2: Inserimento Valido (Double) ---
        proxyList.add(99.99d); // 99.99d è un Double, che estende Number
        verify(smMock, times(1)).dirty(FIELD_ID);
        reset(smMock);
        // --- Action 3: Inserimento Valido (Float) ---
        proxyList.add(1.5f); // 1.5f è un Float, che estende Number
        verify(smMock, times(1)).dirty(FIELD_ID);
        reset(smMock);
        // --- Action 4: Inserimento Invalido (String) ---
        // String NON estende Number -> Ci aspettiamo un'eccezione
        assertThatThrownBy(() -> proxyList.add("Non sono un numero"))
                .isInstanceOfAny(Exception.class);
        verify(smMock, never()).dirty(anyInt());
        // --- FINAL VERIFY ---
        // La lista deve contenere esattamente i 3 numeri aggiunti, nell'ordine di inserimento.
        // La stringa non deve essere presente.
        assertThat(proxyList)
                .hasSize(3)
                .containsExactly(100, 99.99d, 1.5f)
                .doesNotContain("Non sono un numero");
    }

    @Test // 6: ArrayList, Element String, AAT true
    @SuppressWarnings("unchecked")
    public void newCollectionProxy_ArrayList_TypedString_AATTrue() {
        // --- SETUP ---
        configureManager(false, true, false);
        Class<?> isolationClass = new ArrayList(){}.getClass();
        List proxyList = (List) proxyManager.newCollectionProxy( isolationClass, String.class, null, false);
        // --- FIRST VERIFY ---
        assertThat(proxyList).isNotNull().isInstanceOf(ArrayList.class);
        assertThat(proxyList).isNotInstanceOf(ChangeTracker.class);
        // setup Owner
        ((Proxy) proxyList).setOwner(smMock, FIELD_ID);
        // Action 1: Inserimento Valido (String)
        proxyList.add("Stringa Valida");
        // Verify 1: Verifica puntuale e reset
        verify(smMock, times(1)).dirty(FIELD_ID);
        reset(smMock);
        // --- Action 2: Inserimento Invalido (Integer) ---
        // Con AssertAllowedType=true, ci aspettiamo un'eccezione immediata
        assertThatThrownBy(() -> proxyList.add(123))
                .isInstanceOfAny(Exception.class);
        // Lo StateManager non deve essere stato notificato (nessun dirty)
        verify(smMock, never()).dirty(anyInt());
        // La lista non deve essere stata modificata (deve contenere solo la stringa di prima)
        assertThat(proxyList)
                .hasSize(1)
                .containsExactly("Stringa Valida")
                .doesNotContain(123);
    }

    @Test // Test 7: TrackChanges=true, AutoOff=true, AAT=true
    @SuppressWarnings("unchecked")
    public void newCollectionProxy_TrackChanges_AutoOff_AAT_StartTrackingBehavior() {
        // --- SETUP ---
        configureManager(true, true, false);
        Class<?> isolationClass = new ArrayList(){}.getClass();
        List proxyList = (List) proxyManager.newCollectionProxy( isolationClass, String.class, null, true);
        // --- VERIFY PRELIMINARI ---
        assertThat(proxyList)
                .isInstanceOf(ArrayList.class)
                .isInstanceOf(ChangeTracker.class); // Deve implementare l'interfaccia
        // Setup Owner
        ((Proxy) proxyList).setOwner(smMock, FIELD_ID);

        ChangeTracker tracker = (ChangeTracker) proxyList;
        // -----------------------------------------------------------------
        // FASE 1: Tracking non attivo (Default alla creazione o dopo reset)
        // -----------------------------------------------------------------
        // Action: Aggiungo un elemento
        proxyList.add("Elemento Senza Tracking");
        // VERIFY FASE 1:
        // Lo StateManager viene notificato (Dirty Flag) perché l'oggetto è cambiato.
        verify(smMock, times(1)).dirty(FIELD_ID);
        // ma il ChangeTracker non ha registrato il dettaglio della modifica
        assertThat(tracker.getAdded())
                .as("Senza startTracking(), la lista getAdded() deve essere vuota")
                .isEmpty();
        reset(smMock);
        // -----------------------------------------------------------------
        // FASE 2: Attivazione del Tracking ("Attivo lo stack trace")
        // -----------------------------------------------------------------
        tracker.startTracking();
        // Action: Aggiungo un secondo elemento
        proxyList.add("Elemento Tracciato");
        // Action: Elimino il primo elemento
        proxyList.add("Elemento Senza Tracking");
        // VERIFY FASE 2:
        // Lo StateManager viene notificato nuovamente
        verify(smMock, times(2)).dirty(FIELD_ID);
        // ora il ChangeTracker ha registrato specificamente cosa è cambiato
        assertThat(tracker.getAdded())
                .as("Dopo startTracking(), l'elemento aggiunto deve apparire in getAdded()")
                .contains("Elemento Tracciato")
                .doesNotContain("Elemento Senza Tracking");
        assertThat(tracker.getRemoved())
                .as("Dopo startTracking(), l'elemento eliminato deve apparire in getRemoved()")
                .contains("Elemento Senza Tracking");
    }

    /**

     @Test // 5: ArrayList, String, Tracking=TRUE -> Comprehensive Mutation & Tracking
    public void newCollectionProxy_ArrayList_Tracking_StringOnly_ShouldTrackAllMutations() {
        // 1. Configurazione: TRACKING = TRUE, AssertType = FALSE (o TRUE, qui usiamo solo Stringhe quindi è safe)
        ProxyManagerImpl proxyManag = new ProxyManagerImpl();
        proxyManag.setTrackChanges(true);
        configureManager(true, false, false);
        // 2. Creazione: Lista tipizzata String
        List<String> proxyList = (List<String>) proxyManag.newCollectionProxy(
                ArrayList.class, String.class, null, false);
        ((Proxy) proxyList).setOwner(smMock, FIELD_ID);
        ChangeTracker tracker = (ChangeTracker) proxyList;
        // --- 1. add(E e) ---
        proxyList.add("A");
        verify(smMock, times(1)).dirty(FIELD_ID);
        assertThat(proxyList).containsExactly("A");
        assertThat(tracker.getAdded()).contains("A");
        reset(smMock);
        // --- 2. add(int index, E element) ---
        proxyList.add(0, "B"); // [B, A]
        verify(smMock, times(1)).dirty(FIELD_ID);
        assertThat(proxyList).containsExactly("B", "A");
        assertThat(tracker.getAdded()).contains("A", "B");
        reset(smMock);
        // --- 3. addAll(Collection) ---
        List<String> others = new ArrayList<>();
        others.add("C");
        others.add("D");
        proxyList.addAll(others); // [B, A, C, D]
        verify(smMock, atLeast(1)).dirty(FIELD_ID);
        assertThat(proxyList).containsExactly("B", "A", "C", "D");
        assertThat(tracker.getAdded()).contains("A", "B", "C", "D");
        reset(smMock);
        // --- 4. set(int index, E element) ---
        // Sostituiamo "A" (indice 1) con "E" -> [B, E, C, D]
        proxyList.set(1, "E");
        verify(smMock, times(1)).dirty(FIELD_ID);
        assertThat(proxyList).containsExactly("B", "E", "C", "D");
        // Verifica Tracker su SET:
        // "E" è il nuovo valore -> deve essere in ADDED
        // "A" è il vecchio valore -> deve essere in REMOVED
        assertThat(tracker.getAdded()).contains("E");
        assertThat(tracker.getRemoved()).contains("A");
        reset(smMock);
        // --- 5. remove(Object o) ---
        // Rimuoviamo "C" -> [B, E, D]
        proxyList.remove("C");
        verify(smMock, times(1)).dirty(FIELD_ID);
        assertThat(proxyList).containsExactly("B", "E", "D");
        assertThat(tracker.getRemoved()).contains("A", "C"); // C si aggiunge ai rimossi
        reset(smMock);
        // --- 6. Iterator.remove() ---
        // Rimuoviamo "B" tramite iteratore
        java.util.Iterator<String> it = proxyList.iterator();
        if (it.hasNext()) {
            String val = it.next(); // Legge "B"
            if (val.equals("B")) {
                it.remove(); // Rimuove "B" -> [E, D]
            }
        }
        verify(smMock, times(1)).dirty(FIELD_ID);
        assertThat(proxyList).containsExactly("E", "D");
        assertThat(tracker.getRemoved()).contains("B");
        reset(smMock);
        // --- 7. ListIterator.add() ---
        // Aggiungiamo "F" in mezzo -> [E, F, D]
        java.util.ListIterator<String> listIt = proxyList.listIterator();
        listIt.next(); // Salta E
        listIt.add("F");
        verify(smMock, atLeast(1)).dirty(FIELD_ID);
        assertThat(proxyList).containsExactly("E", "F", "D");
        assertThat(tracker.getAdded()).contains("F");
        reset(smMock);
        // --- 8. Verifica Finale Stato Tracker ---
        // Il tracker mantiene la storia di tutta la transazione (non viene resettato dal nostro reset(smMock))
        // Tutto ciò che è stato aggiunto (anche se poi rimosso, OpenJPA tende a tenere traccia storicamente)
        assertThat(tracker.getAdded()).contains("A", "B", "C", "D", "E", "F");
        // Tutto ciò che è stato rimosso
        assertThat(tracker.getRemoved()).contains("A", "C", "B");
    }

    @Test // 6: ArrayList, String, AutoOff=TRUE
    public void newCollectionProxy_ArrayList_AutoOff_ShouldInitializeCorrectly() {
        configureManager(true, false, false);

        // Passiamo autoOff = true nel metodo factory
        List<String> proxyList = (List<String>) proxyManager.newCollectionProxy(
                ArrayList.class, String.class, null, true);
        ((Proxy) proxyList).setOwner(smMock, FIELD_ID);

        // Verifica comportamento in stato Managed (deve tracciare)
        proxyList.add("ManagedItem");

        verify(smMock, times(1)).dirty(FIELD_ID);
        assertThat((ChangeTracker) proxyList).isInstanceOf(ChangeTracker.class);
        assertThat(((ChangeTracker) proxyList).getAdded()).contains("ManagedItem");
    }

    @Test // 7: ArrayList, String, AssertAllowedType=TRUE (Strict Check)
    public void newCollectionProxy_ArrayList_StrictTypeCheck_ShouldRejectInvalidTypes() {
        configureManager(true, true, false); // Enable AssertAllowedType

        List proxyList = (List) proxyManager.newCollectionProxy(
                ArrayList.class, String.class, null, false);
        ((Proxy) proxyList).setOwner(smMock, FIELD_ID);

        // 1. Valid Type
        proxyList.add("ValidString");
        verify(smMock, times(1)).dirty(FIELD_ID);

        // 2. Invalid Type -> Expect Exception
        assertThatThrownBy(() -> {
            List rawList = proxyList; // Raw cast to bypass compiler
            rawList.add(123); // Integer passed to String collection
        }).isInstanceOf(OpenJPAException.class)
                .hasMessageContaining("123"); // Message usually contains the invalid value
    }

    @Test // 8: ArrayList, String, DelayCollectionLoading=TRUE (Lazy)
    public void newCollectionProxy_ArrayList_Delayed_ShouldCreateLazily() {
        configureManager(true, false, true); // Enable DelayCollectionLoading
        // Passiamo null come 'orig' implicitamente (il metodo factory crea nuova istanza)
        // In un contesto reale questo eviterebbe la lettura immediata
        Object result = proxyManager.newCollectionProxy(ArrayList.class, String.class, null, false);
        assertThat(result).isNotNull()
                .isInstanceOf(ArrayList.class)
                .isInstanceOf(Proxy.class);
        // Verifica funzionale post-creazione
        List<String> proxyList = (List<String>) result;
        ((Proxy) proxyList).setOwner(smMock, FIELD_ID);
        // Trigger loading
        proxyList.add("Item");
        verify(smMock, times(1)).dirty(FIELD_ID);
        assertThat(proxyList).hasSize(1);
    }

    @Test // 9: ArrayList, Unproxyable="ArrayList" (Exact Match)
    public void newCollectionProxy_WithUnproxyableExactMatch_ShouldReturnRawObject() {
        configureManager(true, true, true);
        // Configurazione specifica per questo test
        proxyManager.setUnproxyable(ArrayList.class.getName());

        Object result = proxyManager.newCollectionProxy(
                ArrayList.class, String.class, null, true);

        // Deve ritornare l'oggetto originale, NON un Proxy
        assertThat(result).isInstanceOf(ArrayList.class)
                .isNotInstanceOf(Proxy.class);
    }

    @Test // 10: ArrayList, Unproxyable="Collection" (Inheritance Match)
    public void newCollectionProxy_WithUnproxyableSuperType_ShouldReturnRawObject() {
        configureManager(true, true, true);
        // Configurazione blocco gerarchico
        proxyManager.setUnproxyable("java.util.Collection");

        Object result = proxyManager.newCollectionProxy(
                ArrayList.class, String.class, null, true);

        // Deve rispettare la gerarchia e non proxare
        assertThat(result).isInstanceOf(ArrayList.class)
                .isNotInstanceOf(Proxy.class);
    }

    @Test // 11: ArrayList, Comparator != null (Robustness)
    public void newCollectionProxy_ArrayList_WithComparator_ShouldIgnoreComparatorAndCreateProxy() {
        configureManager(true, true, true);

        // Passiamo un Comparator (Reverse) a una Lista (che non lo supporta)
        java.util.Comparator<String> reverseComp = java.util.Collections.reverseOrder();

        List<String> proxyList = (List<String>) proxyManager.newCollectionProxy(
                ArrayList.class, String.class, reverseComp, true);
        ((Proxy) proxyList).setOwner(smMock, FIELD_ID);

        // Assert: Non deve esplodere e deve mantenere l'ordine di inserimento (ignorando il comparator)
        proxyList.add("A");
        proxyList.add("B");

        verify(smMock, times(2)).dirty(FIELD_ID);
        assertThat(proxyList).containsExactly("A", "B"); // Insertion order preserved
    }

    @Test // 12: HashSet, Base Case (No Flags)
    public void newCollectionProxy_HashSet_Base_ShouldWorkAndNotifyDirty() {
        configureManager(false, false, false);

        Object result = proxyManager.newCollectionProxy(
                java.util.HashSet.class, String.class, null, false);

        assertThat(result).isInstanceOf(java.util.HashSet.class)
                .isInstanceOf(Proxy.class);

        java.util.Set<String> proxySet = (java.util.Set<String>) result;
        ((Proxy) proxySet).setOwner(smMock, FIELD_ID);

        // 1. Add Operation
        boolean added = proxySet.add("UniqueItem");

        assertThat(added).isTrue();
        verify(smMock, times(1)).dirty(FIELD_ID);
        assertThat(proxySet).hasSize(1).contains("UniqueItem");

        // 2. Remove Operation
        proxySet.remove("UniqueItem");
        verify(smMock, times(2)).dirty(FIELD_ID);
    }

    @Test // 13: HashSet, String, Tracking=TRUE (Idempotency Check)
    public void newCollectionProxy_HashSet_Tracking_ShouldHandleDuplicatesCorrectly() {
        configureManager(true, false, false); // Enable Tracking

        java.util.Set<String> proxySet = (java.util.Set<String>) proxyManager.newCollectionProxy(
                java.util.HashSet.class, String.class, null, false);
        ((Proxy) proxySet).setOwner(smMock, FIELD_ID);

        // 1. Add New Element
        boolean firstAdd = proxySet.add("A");

        assertThat(firstAdd).isTrue();
        verify(smMock, times(1)).dirty(FIELD_ID);
        assertThat(((ChangeTracker) proxySet).getAdded()).contains("A");

        // 2. Add Duplicate Element (Idempotency)
        // Il set non cambia stato, quindi dirty() NON deve essere chiamato di nuovo
        boolean secondAdd = proxySet.add("A");

        assertThat(secondAdd).isFalse();
        verify(smMock, times(1)).dirty(FIELD_ID); // Count remains 1
    }

    @Test // 14: TreeSet, String, No Flags (Natural Ordering)
    public void newCollectionProxy_TreeSet_Base_ShouldSortByNaturalOrder() {
        configureManager(false, false, false);

        java.util.SortedSet<String> proxySet = (java.util.SortedSet<String>) proxyManager.newCollectionProxy(
                java.util.TreeSet.class, String.class, null, false);
        ((Proxy) proxySet).setOwner(smMock, FIELD_ID);

        // Add unordered elements
        proxySet.add("B");
        proxySet.add("A");
        proxySet.add("C");

        // Verify Natural Order (A -> B -> C)
        assertThat(proxySet).containsExactly("A", "B", "C");
        assertThat(proxySet.comparator()).isNull(); // Natural ordering implies null comparator
        verify(smMock, times(3)).dirty(FIELD_ID);
    }

    @Test // 15: TreeSet, String, Tracking=TRUE
    public void newCollectionProxy_TreeSet_Tracking_ShouldTrackAndSort() {
        configureManager(true, false, false);

        java.util.Set<String> proxySet = (java.util.Set<String>) proxyManager.newCollectionProxy(
                java.util.TreeSet.class, String.class, null, false);
        ((Proxy) proxySet).setOwner(smMock, FIELD_ID);

        proxySet.add("Z");
        proxySet.add("A");

        // Verify content and tracking
        assertThat(proxySet).containsExactly("A", "Z");
        assertThat(((ChangeTracker) proxySet).getAdded()).contains("A", "Z");
        verify(smMock, times(2)).dirty(FIELD_ID);
    }

    @Test // 16: TreeSet, String, ReverseComparator, All Flags=TRUE (Stress Test)
    public void newCollectionProxy_TreeSet_CustomComparator_AllFlags_ShouldBehaveCorrectly() {
        // Enable AssertAllowedType (AAT) and DelayCollectionLoading (DCL) on Manager
        configureManager(true, true, true);

        java.util.Comparator<String> reverseComp = java.util.Collections.reverseOrder();

        // Create Proxy with AutoOff=TRUE
        java.util.SortedSet<String> proxySet = (java.util.SortedSet<String>) proxyManager.newCollectionProxy(
                java.util.TreeSet.class, String.class, reverseComp, true);
        ((Proxy) proxySet).setOwner(smMock, FIELD_ID);

        // 1. Verify Comparator Injection
        assertThat(proxySet.comparator()).isEqualTo(reverseComp);

        // 2. Verify Custom Sorting (Reverse)
        proxySet.add("A");
        proxySet.add("B");
        proxySet.add("C");
        assertThat(proxySet).containsExactly("C", "B", "A");

        // 3. Verify Tracking (active before auto-off triggers)
        verify(smMock, times(3)).dirty(FIELD_ID);
        assertThat(((ChangeTracker) proxySet).getAdded()).contains("A", "B", "C");

        // 4. Verify Type Safety (AAT is true)
        assertThatThrownBy(() -> {
            java.util.Set rawSet = proxySet;
            rawSet.add(123); // Integer
        }).isInstanceOf(OpenJPAException.class);
    }

    @Test // 17: PriorityQueue, String, No Flags
    public void newCollectionProxy_PriorityQueue_Base_ShouldWorkAndNotifyDirty() {
        // Configurazione: TUTTO FALSE
        configureManager(false, false, false);

        // Creazione: PriorityQueue su String
        Object result = proxyManager.newCollectionProxy(
                java.util.PriorityQueue.class, String.class, null, false);

        // Assert Strutturali
        assertThat(result).isInstanceOf(java.util.PriorityQueue.class)
                .isInstanceOf(Proxy.class);

        java.util.Queue<String> proxyQueue = (java.util.Queue<String>) result;
        ((Proxy) proxyQueue).setOwner(smMock, FIELD_ID);

        // Action: Aggiunta elementi (senza ordine specifico di inserimento)
        // PriorityQueue ordinerà naturalmente "A" prima di "Z"
        proxyQueue.add("Z");
        proxyQueue.add("A");

        // Assert Comportamentali
        // dirty() deve essere chiamato per ogni modifica
        verify(smMock, times(2)).dirty(FIELD_ID);

        // Assert Stato: Verifica che contenga gli elementi e che la testa sia corretta
        assertThat(proxyQueue).hasSize(2).contains("A", "Z");
        assertThat(proxyQueue.peek()).isEqualTo("A"); // "A" viene prima di "Z" lessicograficamente
    }

    @Test // 18: PriorityQueue, String, AssertAllowedType=TRUE
    public void newCollectionProxy_PriorityQueue_StrictTypeCheck_ShouldRejectInvalidTypes() {
        // Configurazione: AssertAllowedType = TRUE
        // Nota: trackChanges=false come da specifica del caso 18
        configureManager(false, true, false);

        java.util.Queue<String> proxyQueue = (java.util.Queue<String>) proxyManager.newCollectionProxy(
                java.util.PriorityQueue.class, String.class, null, false);
        ((Proxy) proxyQueue).setOwner(smMock, FIELD_ID);

        // 1. Caso Valido (String)
        proxyQueue.add("ValidElement");
        verify(smMock, times(1)).dirty(FIELD_ID);

        // 2. Caso Invalido (Integer in coda di Stringhe)
        // Con AssertAllowedType=true, OpenJPA deve impedire l'inserimento
        assertThatThrownBy(() -> {
            java.util.Queue rawQueue = proxyQueue; // Unsafe cast
            rawQueue.add(999); // Integer non compatibile con String
        }).isInstanceOf(OpenJPAException.class)
                .hasMessageContaining("999"); // Verifica che il messaggio citi l'elemento errato
    }

    **/
}