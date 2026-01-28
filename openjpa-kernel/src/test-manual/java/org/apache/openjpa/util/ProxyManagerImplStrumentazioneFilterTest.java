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
                //{ "java.util.Vector;java.util.ArrayList", false }, // errore implementativo
                //{ "java.util.Vector;java.util.Collection", false }, // errore implementativo
                { "java.util.Vector;java.util.Stack", true }, // nessun match
                //{ ";;;;", true }, // test di robustezza: ottiene al momento del setting di unproxyable IndexOutofBoundsException  Index -1 out of bounds for length 0
                { "   ", true }
        });
    }

    @Before
    public void setUp() {
        sut = new ProxyManagerImpl();
    }

    @Test
    public void testUnproxyableConfiguration() {
        ProxyManagerImplTestUtil.configureManager(sut, true, false, false, unproxyableConfig);
        List<String> orig = new ArrayList<>();
        // Action
        Object result = sut.newCustomProxy(orig, false);
        // Assertion
        if (shouldCreateProxy) {
            assertThat(result)
                    .as("Dovrebbe creare un proxy con config: " + unproxyableConfig)
                    .isNotNull()
                    .isNotSameAs(orig)
                    .isInstanceOf(Proxy.class)
                    .isInstanceOf(ArrayList.class);
        } else {
            assertThat(result)
                    .as("Non dovrebbe creare un proxy con config: " + unproxyableConfig)
                    .isNull();
        }
    }
}