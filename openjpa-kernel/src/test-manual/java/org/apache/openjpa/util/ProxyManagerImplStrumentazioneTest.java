package org.apache.openjpa.util;

import org.apache.openjpa.enhance.PersistenceCapable;
import org.apache.openjpa.util.proxy.ProxyBean;
import org.apache.openjpa.util.proxy.ProxyCollection;
import org.apache.openjpa.util.proxy.ProxyMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests della test suite dei test manuali basati sulla variazione del parametro Object orig
 */
@RunWith(MockitoJUnitRunner.class)
public class ProxyManagerImplStrumentazioneTest {

    // sut
    private ProxyManagerImpl proxyManager;

    // mock utilizzato per passare un entità gestita (menaged) da OpenJPA
    @Mock
    private PersistenceCapable managedMock;

    @Before
    public void setUp() {
        // creiamo una nuova istanza SUT ad ogni test
        proxyManager = new ProxyManagerImpl();
        // Configurazione Standard per questo gruppo di test
        proxyManager.setTrackChanges(true);
        proxyManager.setAssertAllowedType(false);
        proxyManager.setDelayCollectionLoading(false);
    }

    @After
    public void tearDown() {
        // Dereferenziamo il manager per facilitare il lavoro del Garbage Collector (solo per questione di pulizia)
        proxyManager = null;
    }

    @Test // orig null
    public void newCustomProxy_WithNullInput_ShouldReturnNull() {
        Object result = proxyManager.newCustomProxy(null, false);
        assertThat(result)
                .as("Il manager deve gestire l'input nullo restituendo null (non creando alcun proxy)")
                .isNull();
    }

    @Test // {Final Class} - java.lang.String
    public void newCustomProxy_WithFinalClass_ShouldReturnNull() {
        String finalObject = "I am Final";

        Object result = proxyManager.newCustomProxy(finalObject, false);

        assertThat(result)
                .as("Il menager non deve generare proxy per classi final (String)")
                .isNull();
    }

    @Test // {AlreadyProxy}
    public void newCustomProxy_WithAlreadyProxiedObject_ShouldReturnSameInstance() {
        // Creiamo un primo proxy valido (popola la cache interna della singola istanza)
        List<String> original = new ArrayList<>();
        Object firstProxy = proxyManager.newCustomProxy(original, false);

        // Tentiamo di ri-proxare il proxy
        Object result = proxyManager.newCustomProxy(firstProxy, false);

        assertThat(result)
                .as("Idempotenza: il manager non deve creare proxy annidati")
                .isSameAs(firstProxy);
    }

    @Test // {Managed} - Oggetto già gestito da OpenJPA
    public void newCustomProxy_WithManagedObject_ShouldReturnNull() {
        // Usiamo un mock che implementa PersistenceCapable (interfaccia marker di OpenJPA)
        Object result = proxyManager.newCustomProxy(managedMock, false);

        assertThat(result)
                .as("Il manager deve ignorare oggetti già enhanced (PersistenceCapable)")
                .isNull();
    }

    @Test // {Collection} - ArrayList
    public void newCustomProxy_WithArrayList_ShouldReturnProxyCollection() {
        List<String> orig = new ArrayList<>();
        orig.add("Element");

        Object result = proxyManager.newCustomProxy(orig, false);

        assertThat(result)
                .isInstanceOf(ProxyCollection.class) // Verifica interfaccia proxy
                .isInstanceOf(ArrayList.class);      // Verifica che mantenga il tipo originale

        assertThat((List<Object>) result)
                .as("I dati devono essere trasferiti nel proxy")
                .hasSize(1)
                .contains("Element");
    }

    @Test // {Ordered Collection} - TreeSet
    public void newCustomProxy_WithTreeSet_ShouldPreserveComparator() {
        Comparator<String> reverseComp = Collections.reverseOrder();
        TreeSet<String> orig = new TreeSet<>(reverseComp);
        orig.add("A");
        orig.add("B");

        Object result = proxyManager.newCustomProxy(orig, false);

        assertThat(result).isInstanceOf(ProxyCollection.class);

        // Verifica che il Comparator sia stato passato al proxy
        SortedSet<?> proxySet = (SortedSet<?>) result;
        assertThat(proxySet.comparator())
                .as("Il Comparator originale deve essere iniettato nel proxy")
                .isEqualTo(reverseComp);

        assertThat((SortedSet<Object>) proxySet).containsExactly("B", "A");
    }

    @Test // {Map} - HashMap
    public void newCustomProxy_WithHashMap_ShouldReturnProxyMap() {
        Map<String, Integer> orig = new HashMap<>();
        orig.put("Key", 1);

        Object result = proxyManager.newCustomProxy(orig, false);

        assertThat(result)
                .isInstanceOf(ProxyMap.class)
                .isInstanceOf(HashMap.class);
    }

    @Test // {Ordered Map} - TreeMap
    public void newCustomProxy_WithTreeMap_ShouldPreserveKeyOrdering() {
        TreeMap<Integer, String> orig = new TreeMap<>(Collections.reverseOrder());
        orig.put(1, "Uno");
        orig.put(2, "Due");

        Object result = proxyManager.newCustomProxy(orig, false);

        SortedMap<?, ?> proxyMap = (SortedMap<?, ?>) result;
        assertThat(proxyMap.comparator())
                .as("Il Comparator della mappa deve essere preservato")
                .isNotNull();

        assertThat(proxyMap.firstKey())
                .as("L'ordinamento inverso deve essere rispettato")
                .isEqualTo(2);
    }

    @Test // {Date} - java.sql.Timestamp
    public void newCustomProxy_WithSqlTimestamp_ShouldPreserveNanos() {
        long fixedMillis = 1769431706000L;
        Timestamp orig = new Timestamp(fixedMillis);
        orig.setNanos(123456789); // Impostiamo nanosecondi che non esistono in java.util.Date

        Object result = proxyManager.newCustomProxy(orig, false);

        assertThat(result).isInstanceOf(Timestamp.class);

        Timestamp proxyTs = (Timestamp) result;
        assertThat(proxyTs.getTime()).isEqualTo(orig.getTime());
        assertThat(proxyTs.getNanos())
                .as("La copia deve essere profonda: i nanosecondi SQL non devono andare persi")
                .isEqualTo(orig.getNanos());
    }

    @Test // {Calendar} - GregorianCalendar
    public void newCustomProxy_WithGregorianCalendar_ShouldPreserveTimeZone() {
        TimeZone tz = TimeZone.getTimeZone("Asia/Tokyo");
        GregorianCalendar orig = new GregorianCalendar(tz);

        Object result = proxyManager.newCustomProxy(orig, false);

        assertThat(result).isInstanceOf(Calendar.class);

        Calendar proxyCal = (Calendar) result;
        assertThat(proxyCal.getTimeZone())
                .as("Il TimeZone deve essere copiato correttamente")
                .isEqualTo(tz);
    }

    @Test // {Custom Bean} - POJO Semplice
    public void newCustomProxy_WithSimplePojo_ShouldCreateProxyBean() {
        int code = 75;
        String data = "Dato di prova";
        ProxyManagerImplTestUtil.TestCustomBean orig = new ProxyManagerImplTestUtil.TestCustomBean();
        orig.setData(data);
        orig.setCode(code);

        Object result = proxyManager.newCustomProxy(orig, false);

        assertThat(result)
                .as("Deve creare un ProxyBean per oggetti custom validi")
                .isInstanceOf(ProxyBean.class)
                .isInstanceOf(ProxyManagerImplTestUtil.TestCustomBean.class);

        ProxyManagerImplTestUtil.TestCustomBean proxy = (ProxyManagerImplTestUtil.TestCustomBean) result;

        // Verifica copia dati e diversità istanza
        assertThat(proxy.getCode()).isEqualTo(code);
        assertThat(proxy.getData()).isEqualTo(data);
        assertThat(proxy).isNotSameAs(orig);
    }
}