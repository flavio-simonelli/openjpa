package org.apache.openjpa.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Parte della test suit dei test manuali creata in maniera parametrizzata per i filtri di esclusione (Unproxyable).
 */
@RunWith(Parameterized.class)
public class ProxyManagerImplStrumentazioneFilterTest {

    // parameters
    private final String unproxyableConfig;
    private final boolean shouldCreateProxy;

    // fixture
    private ProxyManagerImpl sut;

    // Costruttore per l'iniezione dei parametri
    public ProxyManagerImplStrumentazioneFilterTest(String unproxyableConfig, boolean shouldCreateProxy) {
        this.unproxyableConfig = unproxyableConfig;
        this.shouldCreateProxy = shouldCreateProxy;
    }

    @Parameters(name = "{index}: Config=''{0}'' -> ProxyCreated={1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                //{ "java.util.Vector;java.util.ArrayList", false }, // match esatto : fallisce perchè il controllo se una classe è contenutoa nella lsita unproxyable (isUnproxyAble) è contenuto all'interno del metodo getFactoryProxyBean che viene invocato solo nel caso in cui la classe non è una classe standard di java
                { "java.util.Vector;java.util.Collection", true }, // match interfaccia : il matching di interfaccia non è considerato un matching altrimenti sarebbe troppo restrittivo
                { "java.util.Vector;java.util.Stack", true }, // nessun match
                //{ ";;;;", true }, // test di robustezza: ottiene al momento del setting di unproxyable IndexOutofBoundsException  Index -1 out of bounds for length 0
                { "   ", true } // test di robustezza
        });
    }

    @Before
    public void setUp() {
        // creazione del sut
        sut = new ProxyManagerImpl();
    }

    @Test
    public void testUnproxyableConfiguration() {
        // setup (importante qui avviene il setup dell'unproxyable)
        ProxyManagerImplTestUtil.configureManager(sut, true, false, false, unproxyableConfig);
        // creazione Object orig
        List<String> orig = new ArrayList<>();
        // Action
        Object result = sut.newCustomProxy(orig, false);
        // Assertion
        if (shouldCreateProxy) {
            assertThat(result)
                    .as("Dovrebbe creare un proxy con config: " + unproxyableConfig)
                    .isInstanceOf(org.apache.openjpa.util.Proxy.class); // come dice la documentazione è l'interfaccia implementata da tutti i proxy
        } else {
            assertThat(result)
                    .as("Non dovrebbe creare un proxy con config: " + unproxyableConfig)
                    .isNotInstanceOf(org.apache.openjpa.util.Proxy.class)
                    .isSameAs(orig); // Deve ritornare l'originale
        }
    }
}