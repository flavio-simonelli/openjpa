package org.apache.openjpa.util;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Classe di utilità che raccoglie diverse tipologie di oggetti (Mock)
 * per il testing del ProxyManagerImpl.
 * * Ogni classe interna rappresenta un caso limite o una categoria di partizione
 * specifica per validare i processi di strumentazione (newCustomProxy)
 * e de-strumentazione (copyCustom).
 * Inoltre mantiene una operazione per agevolare la configurazione
 * di un'istanza esistente di ProxyManagerImpl per i test.
 */
public class ProxyManagerImplTestUtil {

    // Costruttore privato per impedire istanziazione
    private ProxyManagerImplTestUtil() {}

    /**
     * Configura un'istanza esistente di ProxyManagerImpl.
     * * @param pm L'istanza da configurare (NON deve essere null)
     * @param trackChanges Abilita/Disabilita il tracciamento delle modifiche
     * @param assertAllowedType Abilita/Disabilita il controllo sui tipi
     * @param delayCollectionLoading Abilita/Disabilita il caricamento ritardato
     * @param unproxyableConfig Configurazione filtri
     * @return L'istanza configurata
     * @throws org.apache.openjpa.exceptions.IllegalTestConfigurationException se pm è null
     */
    public static ProxyManagerImpl configureManager(ProxyManagerImpl pm,
                                                    boolean trackChanges,
                                                    boolean assertAllowedType,
                                                    boolean delayCollectionLoading,
                                                    String unproxyableConfig) {
        // Guard Clause
        if (pm == null) {
            throw new IllegalArgumentException( "Impossibile configurare il test: l'istanza di ProxyManagerImpl passata è NULL. " + "Assicurati di aver inizializzato il manager nel metodo @Before.");
        }
        // Applicazione della configurazione
        pm.setTrackChanges(trackChanges);
        pm.setAssertAllowedType(assertAllowedType);
        pm.setDelayCollectionLoading(delayCollectionLoading);
        pm.setUnproxyable(unproxyableConfig);

        return pm;
    }

    /**
     * Classe Bean
     */
    public static class CustomSimpleBean implements Serializable {
        private String info;

        public CustomSimpleBean() {}

        public String getInfo() { return info; }
        public void setInfo(String info) { this.info = info; }
    }

    /**
     * Custom class non bean perchè senza costruttore vuoto
     */
    public static class CustomNoBeanConstructor {
        private String data;

        public CustomNoBeanConstructor(String data) {
            this.data = data;
        }

        public String getData() { return data; }
    }

    /**
     * Classe no Bean senza getter/setter
     */
    public static class CustomNoBeanParameter {
        private int secretValue;

        public CustomNoBeanParameter() {
            this.secretValue = 42;
        }
        // Nessun getter o setter disponibile.
    }

    /**
     * Classe che implementa l'interfaccia {@link Cloneable}.
     * Utilizzata per verificare che il ProxyManager utilizzi la strategia
     * di clonazione nativa (metodo clone()) durante la duplicazione dell'oggetto.
     */
    public static class CustomCloneable implements Cloneable {
        private String status;

        public CustomCloneable(String status) {
            this.status = status;
        }


        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        @Override
        public boolean equals(Object o) {
            // 1. Controllo identità (Performance)
            if (this == o) return true;

            // 2. Controllo null e classe esatta (Sicurezza)
            if (o == null || getClass() != o.getClass()) return false;

            // 3. Casting
            CustomCloneable that = (CustomCloneable) o;

            // 4. Confronto campi (usando Objects.equals per gestire i null safe)
            return java.util.Objects.equals(status, that.status);
        }

        @Override
        public int hashCode() {
            // Genera l'hash basato sugli stessi campi usati in equals
            return java.util.Objects.hash(status);
        }
    }

    /**
     * Classe che implementa un "Copy Constructor".
     * Utilizzata per testare la capacità del manager di duplicare un oggetto
     * invocando un costruttore che accetta come parametro un'istanza della classe stessa.
     */
    public static class CustomCopyConstructor {
        private String value;

        /**
         * Costruttore di copia (Copy Constructor).
         * @param original L'istanza da cui copiare i dati.
         */
        public CustomCopyConstructor(CustomCopyConstructor original) {
            if (original != null) {
                this.value = original.value;
            }
        }

        public CustomCopyConstructor(String value) {
            this.value = value;
        }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
    }

    public static class FreshHashMapForTest extends HashMap<String, Integer> {
        public FreshHashMapForTest() { super(); }
    }

    public static class FreshTreeMapForTest extends TreeMap<String, Integer> {
        public FreshTreeMapForTest() {
            super();
        }
        public FreshTreeMapForTest(Comparator<? super String> comparator) {
            super(comparator);
        }
    }

    // Classe "Fresca" per forzare la generazione del bytecode per Date
    public static class FreshDateForTest extends java.util.Date {
        // Costruttore Default OBBLIGATORIO per il proxy
        public FreshDateForTest() {
            super();
        }

        // Costruttore utile per il test
        public FreshDateForTest(long date) {
            super(date);
        }
    }

    // Classe "Fresca" per forzare la generazione del bytecode per Calendar.
    // Estendiamo GregorianCalendar perché Calendar è astratta.
    public static class FreshCalendarForTest extends java.util.GregorianCalendar {

        // Costruttore Default OBBLIGATORIO per il proxy
        public FreshCalendarForTest() {
            super();
        }
    }

    // CASO 1: Ha SOLO il costruttore long.
    // OpenJPA dovrà INIETTARE un costruttore di default nel proxy per farlo funzionare.
    public static class DateWithOnlyLongConstructor extends java.util.Date {
        // Niente costruttore vuoto qui!
        public DateWithOnlyLongConstructor(long date) {
            super(date);
        }
    }

    // CASO 2: Non ha né default né long (ha solo un costruttore inutile per OpenJPA)
    // Questo deve far scattare l'eccezione.
    public static class DateBroken extends java.util.Date {
        // Costruttore arbitrario che OpenJPA non sa gestire
        public DateBroken(String s) {
            super();
        }
    }
}
