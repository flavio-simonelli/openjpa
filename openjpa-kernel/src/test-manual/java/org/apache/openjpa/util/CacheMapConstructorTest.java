package org.apache.openjpa.util;

import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.*;

@RunWith(Parameterized.class)
public class CacheMapConstructorTest {

    // param
    private final boolean lruParam;
    private final int maxParam;
    private final int sizeParam;
    private final float loadParam;
    private final int concurrencyLevelParam;

    // expected value
    private final Class<? extends Throwable> expectedException;

    // sut
    private CacheMap sut;

    public CacheMapConstructorTest(boolean lru, int max, int size, float load,
                                  int concurrencyLevel, Class<? extends Throwable> expectedException) {
        this.lruParam = lru;
        this.maxParam = max;
        this.sizeParam = size;
        this.loadParam = load;
        this.concurrencyLevelParam = concurrencyLevel;
        this.expectedException = expectedException;
    }

    @Parameterized.Parameters(name = "Test {index}: lru={0}, max={1}, size={2}, load={3}, conc={4} -> Expect={5}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // lru, max, size, load, conc, Expected Exception (null = Success)
                { false, 3,  2,  0.0001f, 1,  null },
                { true,  3,  2,  0.0001f, 1,  null },
                { false, 3,  2, -0.0001f, 1,  Exception.class },
                { false, 3,  2,  0f,      1,  Exception.class },
                { false, 3,  2,  1f,      1,  null },
                { false, 3,  2,  1.0001f, 1,  Exception.class },
                //{ false, 3,  2,  0.0001f, -1, Exception.class }, // errore il livello di concorrenza non viene utilizzato
                //{ false, 3,  2,  0.0001f, 0,  Exception.class }, // errore il livello di concorrenza non viene utilizzato
                //{ false, 3, -1,  0.0001f, 1,  Exception.class }, // viene applicata Sanitizzazione non specificata in documentazione (default 500)
                //{ false, 0,  0,  0.0001f, 1,  Exception.class },
                { false, 1,  0,  0.0001f, 1,  null },
                //{ false, -1, 2,  0.0001f, 1,  Exception.class }, // viene applicata Sanitizzazione non specificata in documentazione (default Integer.MAX_VALUE)
                //{ false, 0,  2,  0.0001f, 1,  Exception.class },
                //{ false, 1,  2,  0.0001f, 1,  Exception.class }, // viene considerato valido allocare più memoria di quanta necessaria
                { false, 2,  2,  0.0001f, 1,  null }
        });
    }

    @Test
    public void testCacheMapConstructor() {
            ThrowableAssert.ThrowingCallable initAction = () -> {
                sut = new CacheMap(lruParam, maxParam, sizeParam, loadParam, concurrencyLevelParam);
            };

            if (expectedException != null) {
                // Caso Eccezione
                assertThatThrownBy(initAction)
                        .as("Il Test doveva fallire con %s", expectedException.getSimpleName())
                        .isInstanceOf(expectedException)
                ;
            } else {
                // Caso Valido

                // verifica che non lanci eccezioni
                assertThatCode(initAction)
                        .as("Il Test ha lanciato un'eccezione imprevista")
                        .doesNotThrowAnyException();

                // verifica che la cacheMap istanziata non sia null
                assertThat(sut)
                        .as("L'istanza di CacheMap non deve essere null")
                        .isNotNull();

                // verifichiamo che sia vuota
                assertThat(sut)
                        .as("Una nuova CacheMap deve essere vuota")
                        .isEmpty();

                // verificare la capacità massima impostata
                assertThat(sut.cacheMap.getMaxSize())
                        .as("La dimensione massima deve corrispondere al parametro passato")
                        .isEqualTo(maxParam);

                // verifichiamo il tipo di mappa
                if (lruParam) {
                    // Se lru=true, ci aspettiamo una LRUMap
                    assertThat(sut.cacheMap)
                            .as("Con lru=true, la mappa interna deve essere una LRUMap")
                            .isInstanceOf(org.apache.openjpa.lib.util.LRUMap.class);
                } else {
                    // Se lru=false, ci aspettiamo una ConcurrentHashMap
                    assertThat(sut.cacheMap)
                            .as("Con lru=false, la mappa interna deve essere una ConcurrentHashMap")
                            .isInstanceOf(org.apache.openjpa.lib.util.concurrent.ConcurrentHashMap.class);
                }
            }
    }
}
