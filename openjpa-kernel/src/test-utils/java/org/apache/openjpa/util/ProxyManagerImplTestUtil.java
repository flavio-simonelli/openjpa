package org.apache.openjpa.util;

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
     * Custom Bean statico per i test.
     */
    public static class TestCustomBean {
        private String data;
        private int code;

        public TestCustomBean() {}

        public TestCustomBean(String data, int code) {
            this.data = data;
            this.code = code;
        }

        public String getData() { return data; }
        public void setData(String data) { this.data = data; }
        public int getCode() { return code; }
        public void setCode(int code) { this.code = code; }
    }
}
