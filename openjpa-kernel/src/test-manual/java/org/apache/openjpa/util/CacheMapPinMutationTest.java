package org.apache.openjpa.util;

import org.junit.Ignore;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

public class CacheMapPinMutationTest {

    @Test
    public void testPin_ReleasesLock_EvenIfExceptionOrSuccess() {
        // SETUP
        CacheMap sut = new CacheMap(true, 10);
        Object key = "Key";
        sut.put(key, "Value");

        // ACTION
        // Eseguiamo la pin. Se il mutante è attivo (bug), il lock viene preso MA NON rilasciato.
        sut.pin(key);

        // VERIFICA CONCORRENTE
        // Predisponiamo un task asincrono che prova ad acquisire il lock (anche solo in lettura).
        CompletableFuture<Object> asyncOperation = CompletableFuture.supplyAsync(() -> {
            // Se il writeLock è ancora attivo sul main thread, questa chiamata
            // si bloccherà all'infinito e il Future non si completerà.
            return sut.get(key);
        });

        // AssertJ verifica che il task finisca entro 1 secondo.
        // Se il tempo scade, lancia automaticamente un errore spiegando che il Future non si è completato.
        assertThat(asyncOperation)
                .as("Deadlock rilevato: la pin() non ha rilasciato il writeLock, bloccando le letture successive")
                .succeedsWithin(Duration.ofSeconds(1));
    }
}
