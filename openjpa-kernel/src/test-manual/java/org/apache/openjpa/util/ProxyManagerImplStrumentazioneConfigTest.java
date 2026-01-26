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

        assertThat(result).isInstanceOf(Proxy.class);
        assertThat(((Proxy) result).getChangeTracker())
                .as("Con trackChanges=false, il tracker deve essere nullo")
                .isNull();
    }

    @Ignore
    @Test // Questo test fallisce per via della Java Type Erasure
    public void testAssertAllowedTypeOn_WithArrayList_ShouldSetElementType() {
        configureManager(mgr, true, true, false, "");

        List<String> orig = new ArrayList<>();
        orig.add("Sono una Stringa");
        Object result = mgr.newCustomProxy(orig, false);

        assertThat(result).isInstanceOf(Proxy.class);
        assertThat(result).isNotNull();

        List proxy = (List) result;
        assertThatThrownBy(() -> {
            // Tentiamo di inserire un Integer in una lista nata per Stringhe
            proxy.add(999);
        }).as("Il proxy configurato con AssertAllowedType=true DEVE rifiutare oggetti di tipo errato");
    }

    @Test
    public void testDelayCollectionLoading_WithArrayList_ShouldReturnDelayedProxy() {
        configureManager(mgr, true, false, true, "");

        List<String> orig = new ArrayList<>();
        Object result = mgr.newCustomProxy(orig, false);

        assertThat(result)
                .as("Con Delayed attivo il proxy deve ritornare un'istanza che implementa DelayedProxy e deve continuare ad essere un'ArrayList")
                .isInstanceOf(DelayedProxy.class)
                .isInstanceOf(ArrayList.class);
    }

    @Test // essendo un campo interno senza reflection non possiamo vedere se il flag autoOff è stato propagato
    public void testAutoOff_ShouldCreateProxy() {
        configureManager(mgr, true, false, true, "");
        List<String> orig = new ArrayList<>();
        Object result = mgr.newCustomProxy(orig, true);

        assertThat(result).isNotNull().isInstanceOf(Proxy.class);
    }

}