package org.apache.openjpa.util;

import org.apache.openjpa.util.proxy.DelayedProxy;
import org.apache.openjpa.util.proxy.ProxyMap;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.apache.openjpa.util.ProxyManagerImplTestUtil.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Parte della Test Suite manuale per i flag di configurazione di ProxyManagerImpl.
 * Sfrutta la utility PrxyManagerImplTestUtil per una configurazione robusta.
 */
public class ProxyManagerImplStrumentazioneConfigTest {

    private ProxyManagerImpl mgr;

    @Before
    public void setUp() {
        mgr = new ProxyManagerImpl();
    }

    @Test
    public void testTrackChangesOff_WithArrayList_ShouldNotHaveTracker() {
        configureManager(mgr, false, false, false, "");

        List<String> orig = new ArrayList<>();
        Object result = mgr.newCustomProxy(orig, false);

        assertThat(result)
                .isNotNull()
                .isNotSameAs(orig)
                .isInstanceOf(Proxy.class)
                .isNotInstanceOf(ChangeTracker.class)
                .isInstanceOf(ArrayList.class);
    }

//    @Test // Questo test fallisce per via della Java Type Erasure
//    public void testAssertAllowedTypeOn_WithArrayList_ShouldSetElementType() {
//        configureManager(mgr, true, true, false, "");
//
//        List<String> orig = new ArrayList<>();
//        orig.add("Sono una Stringa");
//        Object result = mgr.newCustomProxy(orig, false);
//
//        assertThat(result)
//                .isNotNull()
//                .isNotSameAs(orig)
//                .isInstanceOf(Proxy.class);
//
//        @SuppressWarnings("unchecked")
//        List<Object> proxy = (List<Object>) result;
//        assertThatThrownBy(() -> {
//            // Tentiamo di inserire un Integer in una lista nata per Stringhe
//            proxy.add(999);
//        }).as("Il proxy configurato con AssertAllowedType=true DEVE rifiutare oggetti di tipo errato");
//    }
//
//    @Test
//    public void testAssertAllowedTypeOn_WithHashMap_ShouldSetKeyAndValueType() {
//        configureManager(mgr, true, true, false, "");
//
//        Map<String, Integer> orig = new HashMap<>();
//        orig.put("Livello", 1);
//
//        Object result = mgr.newCustomProxy(orig, false);
//
//        assertThat(result)
//                .isNotNull()
//                .isNotSameAs(orig)
//                .isInstanceOf(Proxy.class)
//                .isInstanceOf(Map.class);
//
//        @SuppressWarnings("unchecked")
//        Map<Object, Object> proxy = (Map<Object, Object>) result;
//
//        assertThatThrownBy(() -> {
//            // Tentiamo di inserire una chiave Integer (99) in una mappa che si aspetta String
//            proxy.put(99, 100);
//        }).as("Il proxy DEVE rifiutare chiavi di tipo errato (Integer invece di String)");
//
//        assertThatThrownBy(() -> {
//            // Tentiamo di inserire un valore String ("Errore") dove ci si aspetta Integer
//            proxy.put("NuovaChiave", "Errore");
//        }).as("Il proxy DEVE rifiutare valori di tipo errato (String invece di Integer)");
//
//        proxy.put("Valido", 500);
//        assertThat(proxy).containsEntry("Valido", 500);
//    }

    @Test
    public void testAssertAllowedTypeOn_WithArrayList_ShouldEnforceElementType() {
        configureManager(mgr, true, true, false, "");

        @SuppressWarnings("unchecked")
        List<String> proxy = (List<String>) mgr.newCollectionProxy(
                ArrayList.class,
                String.class,
                null,
                false
        );

        assertThat(proxy)
                .isNotNull()
                .isInstanceOf(Proxy.class)
                .isInstanceOf(ArrayList.class);

        proxy.add("Stringa Valida");
        assertThat(proxy).contains("Stringa Valida");

        assertThatThrownBy(() -> {
            ((List) proxy).add(999);
        }).as("Il proxy deve lanciare un'eccezione quando si inserisce un tipo errato")
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    public void testAssertAllowedTypeOn_WithHashMap_ShouldEnforceKeyAndValueTypes() {
        configureManager(mgr, true, true, false, "");

        @SuppressWarnings("unchecked")
        Map<String, Integer> proxy = (Map<String, Integer>) mgr.newMapProxy(
                HashMap.class,
                String.class,
                Integer.class,
                null,
                false
        );

        assertThat(proxy)
                .isNotNull()
                .isInstanceOf(Proxy.class)
                .isInstanceOf(HashMap.class);

        proxy.put("Ok", 100);
        assertThat(proxy).containsEntry("Ok", 100);

        assertThatThrownBy(() -> {
            ((Map) proxy).put(99, 100);
        }).as("Il proxy deve rifiutare una CHIAVE di tipo errato")
                .isInstanceOf(Exception.class);

        assertThatThrownBy(() -> {
            ((Map) proxy).put("ChiaveNuova", "No");
        }).as("Il proxy deve rifiutare un VALORE di tipo errato")
                .isInstanceOf(Exception.class);
    }


    @Test
    public void testDelayCollectionLoading_WithArrayList_ShouldReturnDelayedProxy() {
        configureManager(mgr, true, false, true, "");

        List<String> orig = new ArrayList<>();
        Object result = mgr.newCustomProxy(orig, false);

        assertThat(result)
                .as("Con Delayed attivo il proxy deve ritornare un'istanza che implementa DelayedProxy e deve continuare ad essere un'ArrayList")
                .isNotNull()
                .isNotSameAs(orig)
                .isInstanceOf(DelayedProxy.class)
                .isInstanceOf(ArrayList.class);
    }

//    @Test // fallisce perchè non è fra gli oggetti che possono essere Delayed
//    public void testDelayCollectionLoading_WithHashMap_ShouldReturnDelayedProxy() {
//        configureManager(mgr, true, false, true, "");
//
//        HashMap<String, Integer> orig = new HashMap<>();
//        Object result = mgr.newCustomProxy(orig, false);
//
//        assertThat(result)
//                .as("Con Delayed attivo il proxy deve ritornare un'istanza che implementa DelayedProxy e deve continuare ad essere un'HashMap")
//                .isNotNull()
//                .isNotSameAs(orig)
//                .isInstanceOf(DelayedProxy.class)
//                .isInstanceOf(HashMap.class);
//    }

    @Test
    public void testDelayCollectionLoading_WithTreeSet_ShouldReturnDelayedProxy() {
        configureManager(mgr, true, false, true, "");

        TreeSet<String> orig = new TreeSet<>();
        Object result = mgr.newCustomProxy(orig, false);

        assertThat(result)
                .as("Con Delayed attivo il proxy deve ritornare un'istanza che implementa DelayedProxy e deve continuare ad essere un'TreeSet")
                .isNotNull()
                .isNotSameAs(orig)
                .isInstanceOf(DelayedProxy.class)
                .isInstanceOf(TreeSet.class);
    }

    @Test
    public void testAutoOff_ShouldCreateProxy() {
        configureManager(mgr, true, false, false, "");
        List<String> orig = new ArrayList<>();
        Object result = mgr.newCustomProxy(orig, true);

        assertThat(result)
                .as("Con il flag autoOff il proxy deve essere creato")
                .isNotNull()
                .isNotSameAs(orig)
                .isInstanceOf(Proxy.class)
                .isInstanceOf(List.class);
    }

    @Test
    public void testDelayCollectionLoading_WithHashSet_ShouldReturnDelayedProxy() {
        // Attiviamo il Delayed Loading (4° parametro = true)
        configureManager(mgr, true, false, true, "");

        HashSet<String> orig = new HashSet<>();
        orig.add("Test");

        Object result = mgr.newCustomProxy(orig, false);

        assertThat(result)
                .isNotNull()
                .isInstanceOf(HashSet.class)
                .isInstanceOf(org.apache.openjpa.util.proxy.DelayedProxy.class);
    }

    @Test
    public void testDelayCollectionLoading_WithLinkedList_ShouldReturnDelayedProxy() {
        configureManager(mgr, true, false, true, "");

        LinkedList<String> orig = new LinkedList<>();
        orig.add("Test");

        Object result = mgr.newCustomProxy(orig, false);

        assertThat(result)
                .isNotNull()
                .isInstanceOf(LinkedList.class)
                .isInstanceOf(org.apache.openjpa.util.proxy.DelayedProxy.class);
    }

    @Test
    public void testDelayCollectionLoading_WithVector_ShouldReturnDelayedProxy() {
        configureManager(mgr, true, false, true, "");

        Vector<String> orig = new Vector<>();
        orig.add("Test");

        Object result = mgr.newCustomProxy(orig, false);

        assertThat(result)
                .isNotNull()
                .isInstanceOf(Vector.class)
                .isInstanceOf(org.apache.openjpa.util.proxy.DelayedProxy.class);
    }

    @Test
    public void testDelayCollectionLoading_WithLinkedHashSet_ShouldReturnDelayedProxy() {
        configureManager(mgr, true, false, true, "");

        LinkedHashSet<String> orig = new LinkedHashSet<>();
        orig.add("Test");

        Object result = mgr.newCustomProxy(orig, false);

        assertThat(result)
                .isNotNull()
                .isInstanceOf(LinkedHashSet.class)
                .isInstanceOf(org.apache.openjpa.util.proxy.DelayedProxy.class);
    }

    @Test
    public void testDelayCollectionLoading_WithSortedSetInterface_ShouldReturnDelayedProxy() {
        configureManager(mgr, true, false, true, "");

        // SortedSet viene mappato su DelayedTreeSetProxy
        Object result = mgr.newCollectionProxy(SortedSet.class, String.class, null, false);

        assertThat(result)
                .isNotNull()
                .isInstanceOf(SortedSet.class)
                .isInstanceOf(org.apache.openjpa.util.proxy.DelayedProxy.class);
    }

    @Test
    public void testDelayCollectionLoading_WithPriorityQueue_ShouldReturnDelayedProxy() {
        configureManager(mgr, true, false, true, "");

        PriorityQueue<String> orig = new PriorityQueue<>();
        orig.add("Test");

        Object result = mgr.newCustomProxy(orig, false);

        assertThat(result)
                .isNotNull()
                .isInstanceOf(PriorityQueue.class)
                .isInstanceOf(org.apache.openjpa.util.proxy.DelayedProxy.class);
    }

    @Test
    public void testDelayCollectionLoading_WithUnsupportedType_ShouldReturnStandardProxy() {
        configureManager(mgr, true, false, true, "");

        // ArrayDeque non è supportato per il Delayed loading,
        // quindi il manager deve fare fallback e tornare un proxy normale
        java.util.ArrayDeque<String> orig = new java.util.ArrayDeque<>();
        orig.add("Test");

        Object result = mgr.newCustomProxy(orig, false);

        assertThat(result)
                .isNotNull()
                .isInstanceOf(org.apache.openjpa.util.Proxy.class)
                // Verifica cruciale: NON deve essere Delayed
                .isNotInstanceOf(org.apache.openjpa.util.proxy.DelayedProxy.class);
    }

}