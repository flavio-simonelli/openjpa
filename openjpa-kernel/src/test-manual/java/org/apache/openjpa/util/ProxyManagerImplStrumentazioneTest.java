package org.apache.openjpa.util;

import org.apache.openjpa.enhance.PersistenceCapable;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.sql.Timestamp;
import java.util.*;

import static org.apache.openjpa.util.ProxyManagerImplTestUtil.configureManager;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

    @Test
    public void testNewCustomProxy_JdkProxyList_ReturnsOpenJpaProxy() {
        // 1. SETUP: Creiamo i dati reali (Backing List)
        final List<String> backingList = new ArrayList<String>();
        backingList.add("ProxyData1");
        backingList.add("ProxyData2");

        // Implementa l'interfaccia List, ma è un java.lang.reflect.Proxy.
        // InvocationHandler: Intercetta ogni chiamata al proxy e la gira alla backingList
        List<?> jdkProxy = (List<?>) java.lang.reflect.Proxy.newProxyInstance(
                getClass().getClassLoader(),
                new Class<?>[] { List.class },
                (proxy, method, args) -> method.invoke(backingList, args)
        );

        // Verifica preliminare (Giusto per essere sicuri del setup del test)
        assertThat(java.lang.reflect.Proxy.isProxyClass(jdkProxy.getClass()))
                .as("Il setup del test deve garantire che l'input sia un JDK Proxy")
                .isTrue();

        // 3. ESECUZIONE: Chiediamo a OpenJPA di creare un SUO proxy partendo dal Proxy Java
        Object result = proxyManager.newCustomProxy(jdkProxy, true);

        // 4. VERIFICHE
        assertThat(result)
                .isNull();
    }

    @Test // {AlreadyProxy}
    public void newCustomProxy_WithAlreadyProxiedObject_ShouldReturnSameInstance() {
        List<String> original = new ArrayList<>();
        Object firstProxy = proxyManager.newCustomProxy(original, false);

        Object result = proxyManager.newCustomProxy(firstProxy, false);

        assertThat(result)
                .as("Idempotenza: il manager non deve creare proxy annidati")
                .isSameAs(firstProxy);
    }

    @Test // {Managed} - Oggetto già gestito da OpenJPA
    public void newCustomProxy_WithManagedObject_ShouldReturnNull() {
        Object result = proxyManager.newCustomProxy(managedMock, false);

        assertThat(result)
                .as("Il manager deve ignorare oggetti già enhanced (PersistenceCapable)")
                .isNull();
    }

    @Test // {Collection} - ArrayList
    public void newCustomProxy_WithArrayList_ShouldReturnProxyCollection() {
        String value = "value";
        List<String> orig = new ArrayList<>();
        orig.add(value);

        Object result = proxyManager.newCustomProxy(orig, false);

        assertThat(result)
                .isNotNull()
                .isNotSameAs(orig)
                .isInstanceOf(Proxy.class) // Verifica interfaccia proxy
                .isInstanceOf(ArrayList.class); // Verifica che mantenga il tipo originale

        @SuppressWarnings("unchecked")
        List<String> listResult = (List<String>)result;
        assertThat(listResult)
                .as("I dati devono essere trasferiti nel proxy")
                .hasSize(1)
                .contains(value);
    }

    @Test // {Ordered Collection} - TreeSet
    public void newCustomProxy_WithTreeSet_ShouldPreserveComparator() {
        Comparator<String> reverseComp = Collections.reverseOrder();
        TreeSet<String> orig = new TreeSet<>(reverseComp);
        orig.add("A");
        orig.add("B");
        orig.add("C");

        Object result = proxyManager.newCustomProxy(orig, false);

        assertThat(result)
                .isNotNull()
                .isNotSameAs(orig)
                .isInstanceOf(Proxy.class)
                .isInstanceOf(TreeSet.class);

        @SuppressWarnings("unchecked")
        SortedSet<String> proxySet = (SortedSet<String>) result;
        assertThat(proxySet.comparator())
                .as("Il Comparator originale deve essere iniettato nel proxy")
                .isEqualTo(reverseComp);

        assertThat(proxySet).containsExactly("C", "B", "A");
    }

    @Test // {Map} - HashMap
    public void newCustomProxy_WithHashMap_ShouldReturnProxyMap() {
        Map<String, Integer> orig = new HashMap<>();
        orig.put("Key", 1);
        orig.put("SecondKey", 42);

        Object result = proxyManager.newCustomProxy(orig, false);

        assertThat(result)
                .isNotNull()
                .isNotSameAs(orig)
                .isInstanceOf(Proxy.class)
                .isInstanceOf(HashMap.class);

        @SuppressWarnings("unchecked")
        HashMap<String, Integer> castedResult = (HashMap<String, Integer>) result;

        assertThat(castedResult)
                .as("Il proxy deve contenere esattamente le stesse entry della mappa originale")
                .hasSize(2)
                .containsEntry("Key", 1)
                .containsEntry("SecondKey", 42)
                .containsAllEntriesOf(orig); // Controllo matematico di uguaglianza tra le mappe
    }

    @Test // {Ordered Map} - TreeMap
    public void newCustomProxy_WithTreeMap_ShouldPreserveKeyOrderingAndContent() {
        TreeMap<Integer, String> orig = new TreeMap<>(Collections.reverseOrder());
        orig.put(1, "Uno");
        orig.put(2, "Due");
        orig.put(3, "Tre");

        Object result = proxyManager.newCustomProxy(orig, false);

        assertThat(result)
                .isNotNull()
                .isInstanceOf(Proxy.class)
                .isInstanceOf(TreeMap.class);

        @SuppressWarnings("unchecked")
        TreeMap<Integer, String> proxyMap = (TreeMap<Integer, String>) result;

        assertThat(proxyMap.comparator())
                .as("Il Comparator (reverseOrder) deve essere preservato nel Proxy")
                .isEqualTo(Collections.reverseOrder());

        assertThat(proxyMap.firstKey())
                .as("La prima chiave deve essere 3 (ordine decrescente)")
                .isEqualTo(3);

        assertThat(proxyMap.lastKey())
                .as("L'ultima chiave deve essere 1 (ordine decrescente)")
                .isEqualTo(1);

        assertThat(proxyMap)
                .as("Il Proxy deve contenere tutte le entry originali")
                .hasSize(3)
                .containsAllEntriesOf(orig);
    }

    @Test // {Date} - java.sql.Timestamp
    public void newCustomProxy_WithSqlTimestamp_ShouldPreserveNanos() {
        long fixedMillis = 1769431706000L;
        Timestamp orig = new Timestamp(fixedMillis);
        orig.setNanos(123456789);

        Object result = proxyManager.newCustomProxy(orig, false);

        assertThat(result)
                .isNotNull()
                .isNotSameAs(orig)
                .isInstanceOf(Proxy.class)
                .isInstanceOf(Timestamp.class);

        Timestamp proxyTs = (Timestamp) result;
        assertThat(proxyTs.getTime()).isEqualTo(orig.getTime());
        assertThat(proxyTs.getNanos())
                .as("La copia deve essere profonda: i nanosecondi SQL non devono andare persi")
                .isEqualTo(orig.getNanos());
    }

    @Test // {Calendar} - GregorianCalendar
    public void newCustomProxy_WithGregorianCalendar_ShouldPreserveTimeZoneAndMillis() {
        TimeZone tz = TimeZone.getTimeZone("Asia/Tokyo");
        long fixedTime = 1709251200000L; // Un timestamp arbitrario

        GregorianCalendar orig = new GregorianCalendar(tz);
        orig.setTimeInMillis(fixedTime);

        Object result = proxyManager.newCustomProxy(orig, false);

        assertThat(result)
                .isNotNull()
                .isNotSameAs(orig)
                .isInstanceOf(Proxy.class)
                .isInstanceOf(Calendar.class);

        Calendar proxyCal = (Calendar) result;

        assertThat(proxyCal.getTimeZone())
                .as("Il TimeZone deve essere copiato correttamente")
                .isEqualTo(tz);

        // 5. Verifica Millisecondi (AGGIUNTO DOPO ANALISI CON PITEST)
        assertThat(proxyCal.getTimeInMillis())
                .as("I millisecondi devono essere preservati esattamente e non resettati")
                .isEqualTo(fixedTime);
    }

    @Test // {Custom Bean} - POJO Semplice
    public void newCustomProxy_WithCustomSimpleBean_ShouldCreateProxyBean() {
        String data = "Dato di prova";
        ProxyManagerImplTestUtil.CustomSimpleBean orig = new ProxyManagerImplTestUtil.CustomSimpleBean();
        orig.setInfo(data);

        Object result = proxyManager.newCustomProxy(orig, false);

        assertThat(result)
                .isNotNull()
                .isNotSameAs(orig)
                .isInstanceOf(Proxy.class)
                .isInstanceOf(ProxyManagerImplTestUtil.CustomSimpleBean.class);

        ProxyManagerImplTestUtil.CustomSimpleBean proxy = (ProxyManagerImplTestUtil.CustomSimpleBean) result;

        assertThat(proxy.getInfo()).isEqualTo(data);
    }

    @Test
    public void newCustomProxy_WithCustomNoBeanConstructor_ShouldNullReturn() {
        ProxyManagerImplTestUtil.CustomNoBeanConstructor orig = new ProxyManagerImplTestUtil.CustomNoBeanConstructor("prova");
        Object result = proxyManager.newCustomProxy(orig, false);
        assertThat(result)
                .isNull();
    }

    @Test
    public void newCustomProxy_WithCustomNoBeanParameter_ShouldNullReturn() {
        ProxyManagerImplTestUtil.CustomNoBeanParameter orig = new ProxyManagerImplTestUtil.CustomNoBeanParameter();
        Object result = proxyManager.newCustomProxy(orig, false);
        assertThat(result)
                .isNull();
    }

    @Test
    public void newCustomProxy_WithCostumSimpleBean_WithNullUnproxyableList_ShouldCreateProxyBean() {
        String data = "Dato di prova";
        ProxyManagerImplTestUtil.CustomSimpleBean orig = new ProxyManagerImplTestUtil.CustomSimpleBean();
        orig.setInfo(data);

        proxyManager.setUnproxyable(null);
        Object result = proxyManager.newCustomProxy(orig, false);

        assertThat(result)
                .isNotNull()
                .isNotSameAs(orig)
                .isInstanceOf(Proxy.class)
                .isInstanceOf(ProxyManagerImplTestUtil.CustomSimpleBean.class);

        ProxyManagerImplTestUtil.CustomSimpleBean proxy = (ProxyManagerImplTestUtil.CustomSimpleBean) result;

        assertThat(proxy.getInfo()).isEqualTo(data);
    }

    // test aggiunti dopo la prima iterazione con jacoco

    // mancava un test in cui verifichiamo il newCustomProxy con una Data che non è un timestamp
    @Test // Test 26 -> Case: java.util.Date
    public void testNewCustomProxy_WithUtilDate_ShouldCopyTimeOnly() {
        long fixedMillis = 1609459200000L;
        java.util.Date orig = new java.util.Date(fixedMillis);

        Object result = proxyManager.newCustomProxy(orig, false);

        assertThat(result)
                .isNotNull()
                .isNotSameAs(orig)
                .isInstanceOf(org.apache.openjpa.util.Proxy.class)
                .isInstanceOf(java.util.Date.class)
                .isNotInstanceOf(java.sql.Timestamp.class); // verifichiamo che non sia stato trasformato in un timestamp

        java.util.Date proxyDate = (java.util.Date) result;

        assertThat(proxyDate.getTime())
                .as("Il tempo in millisecondi deve essere copiato correttamente anche per le Date standard")
                .isEqualTo(fixedMillis);
    }

    @Test // Copre generazione bytecode per Mappe Standard custom ovvero classe che implementa l' interfaccia mappa
    public void testBytecodeGen_ForceNewClass_HashMap() {
        configureManager(proxyManager, true, true, false, "");

        // Usiamo la nostra classe "fresca".
        // OpenJPA non l'ha mai vista -> Genera il bytecode -> Copertura ottenuta!
        ProxyManagerImplTestUtil.FreshHashMapForTest orig = new ProxyManagerImplTestUtil.FreshHashMapForTest();
        orig.put("Key", 1);

        Object result = proxyManager.newCustomProxy(orig, false);

        assertThat(result).isInstanceOf(org.apache.openjpa.util.proxy.ProxyMap.class);

        // Verifica che il metodo .copy() generato funzioni
        org.apache.openjpa.util.proxy.ProxyMap proxy = (org.apache.openjpa.util.proxy.ProxyMap) result;
        Object copy = proxy.copy(orig);

        assertThat(copy)
                .isInstanceOf(ProxyManagerImplTestUtil.FreshHashMapForTest.class) // Deve mantenere il tipo
                .isEqualTo(orig);
    }

    @Test // Copre generazione bytecode per SortedMap con Comparator (Ramo B)
    public void testBytecodeGen_ForceNewClass_TreeMap() {
        configureManager(proxyManager, true, true, false, "");

        Comparator<String> reverseComp = Comparator.reverseOrder();

        // Usiamo la nostra classe "fresca" per TreeMap
        ProxyManagerImplTestUtil.FreshTreeMapForTest orig = new ProxyManagerImplTestUtil.FreshTreeMapForTest(reverseComp);
        orig.put("A", 1);

        Object result = proxyManager.newCustomProxy(orig, false);

        assertThat(result).isInstanceOf(org.apache.openjpa.util.proxy.ProxyMap.class);

        org.apache.openjpa.util.proxy.ProxyMap proxy = (org.apache.openjpa.util.proxy.ProxyMap) result;
        Object copy = proxy.copy(orig);

        assertThat(copy).isInstanceOf(ProxyManagerImplTestUtil.FreshTreeMapForTest.class);

        // Verifica CRUCIALE: Il bytecode ha copiato il comparator?
        // Qui stiamo testando il ramo: if (params[0] == Comparator.class) ...
        TreeMap<?,?> copyTree = (TreeMap<?,?>) copy;
        assertThat(copyTree.comparator()).isEqualTo(reverseComp);
    }

    @Test // Copre generazione bytecode per Date (addProxyDateMethods)
    public void testBytecodeGen_ForceNewClass_Date() {
        configureManager(proxyManager, true, true, false, "");

        long now = System.currentTimeMillis();
        // 1. Istanziamo la nostra classe custom
        ProxyManagerImplTestUtil.FreshDateForTest orig = new ProxyManagerImplTestUtil.FreshDateForTest(now);

        // 2. newCustomProxy vede un tipo mai visto -> GENERA IL BYTECODE
        Object result = proxyManager.newCustomProxy(orig, false);

        // 3. Verifiche strutturali
        // Verifica che implementi l'interfaccia iniettata ProxyDate
        assertThat(result)
                .isNotNull()
                .isInstanceOf(org.apache.openjpa.util.proxy.ProxyDate.class)
                .isInstanceOf(ProxyManagerImplTestUtil.FreshDateForTest.class);

        // 4. Verifica funzionale del metodo .copy() generato dal bytecode
        org.apache.openjpa.util.proxy.ProxyDate proxy = (org.apache.openjpa.util.proxy.ProxyDate) result;

        // Il bytecode generato deve saper chiamare il costruttore o clone
        Object copy = proxy.copy(orig);

        assertThat(copy)
                .isNotNull()
                .isNotSameAs(orig)
                .isInstanceOf(ProxyManagerImplTestUtil.FreshDateForTest.class); // Deve mantenere il tipo custom

        assertThat(((java.util.Date) copy).getTime())
                .as("Il bytecode deve aver copiato correttamente il tempo")
                .isEqualTo(now);
    }

    @Test // Copre generazione bytecode per Calendar
    public void testBytecodeGen_ForceNewClass_Calendar() {
        configureManager(proxyManager, true, true, false, "");

        // 1. Setup: Creiamo la nostra istanza custom
        ProxyManagerImplTestUtil.FreshCalendarForTest orig = new ProxyManagerImplTestUtil.FreshCalendarForTest();
        long now = System.currentTimeMillis();
        orig.setTimeInMillis(now);
        // Impostiamo anche una TimeZone specifica per essere pignoli sulla copia
        orig.setTimeZone(TimeZone.getTimeZone("GMT"));

        // 2. Generazione Proxy (Bypass Cache)
        Object result = proxyManager.newCustomProxy(orig, false);

        // 3. Verifiche Strutturali
        // Deve implementare l'interfaccia ProxyCalendar iniettata dal bytecode
        assertThat(result)
                .isNotNull()
                .isInstanceOf(org.apache.openjpa.util.proxy.ProxyCalendar.class)
                .isInstanceOf(ProxyManagerImplTestUtil.FreshCalendarForTest.class); // Deve mantenere il tipo

        // 4. Verifica Funzionale del metodo .copy() generato
        org.apache.openjpa.util.proxy.ProxyCalendar proxy = (org.apache.openjpa.util.proxy.ProxyCalendar) result;

        // Il bytecode generato clona il Calendar copiando time e timezone
        Object copy = proxy.copy(orig);

        assertThat(copy)
                .isNotNull()
                .isNotSameAs(orig)
                .isInstanceOf(ProxyManagerImplTestUtil.FreshCalendarForTest.class);

        Calendar copyCal = (Calendar) copy;

        assertThat(copyCal.getTimeInMillis())
                .as("Il tempo deve essere copiato correttamente")
                .isEqualTo(now);

        assertThat(copyCal.getTimeZone())
                .as("La TimeZone deve essere copiata correttamente")
                .isEqualTo(TimeZone.getTimeZone("GMT"));
    }

    @Test
    public void testBytecodeGen_Date_WithOnlyLongConstructor_ShouldInjectDefaultCons() {
        configureManager(proxyManager, true, true, false, "");

        long now = System.currentTimeMillis();
        // 1. Creiamo l'istanza originale
        ProxyManagerImplTestUtil.DateWithOnlyLongConstructor orig = new ProxyManagerImplTestUtil.DateWithOnlyLongConstructor(now);

        // 2. Generazione Proxy
        // Qui OpenJPA vede che manca il costruttore di default.
        // Invece di fallire, DEVE generare bytecode che aggiunge "public Proxy() { super(System.currentTimeMillis()); }"
        Object result = proxyManager.newCustomProxy(orig, false);

        // 3. Verifiche
        assertThat(result)
                .isNotNull()
                .isInstanceOf(org.apache.openjpa.util.proxy.ProxyDate.class)
                .isInstanceOf(ProxyManagerImplTestUtil.DateWithOnlyLongConstructor.class);

        // 4. Verifica che il proxy sia funzionante (il costruttore iniettato è stato usato)
        org.apache.openjpa.util.proxy.ProxyDate proxy = (org.apache.openjpa.util.proxy.ProxyDate) result;

        // Proviamo a copiarlo: questo richiede che il proxy sia istanziabile
        Object copy = proxy.copy(orig);
        assertThat(copy).isInstanceOf(ProxyManagerImplTestUtil.DateWithOnlyLongConstructor.class);

        // Verifica che il tempo sia corretto
        assertThat(((java.util.Date)copy).getTime()).isEqualTo(now);
    }

    @Test
    public void testBytecodeGen_Date_WithNoCompatibleConstructor_ShouldThrowException() {
        configureManager(proxyManager, true, true, false, "");

        // Classe "Rotta" per OpenJPA (ha solo costruttore String)
        ProxyManagerImplTestUtil.DateBroken orig = new ProxyManagerImplTestUtil.DateBroken("test");

        assertThatThrownBy(() -> {
            // Questo deve fallire durante la generazione del bytecode
            // perché OpenJPA non sa come estendere la classe senza un costruttore valido
            proxyManager.newCustomProxy(orig, false);
        })
                .as("Deve lanciare UnsupportedException se manca sia il costruttore default che quello long")
                .isInstanceOf(Exception.class);
    }

}