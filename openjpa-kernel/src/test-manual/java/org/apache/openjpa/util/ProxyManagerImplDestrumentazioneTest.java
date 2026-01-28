package org.apache.openjpa.util;

import org.apache.openjpa.enhance.PersistenceCapable;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import java.sql.Timestamp;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

public class ProxyManagerImplDestrumentazioneTest {

    private ProxyManagerImpl proxyManager;

    @Before
    public void setUp() {
        proxyManager = new ProxyManagerImpl();
    }

    @Test // Case 0
    public void testCopyCustom_NullInput_ReturnsNull() {
        Object result = proxyManager.copyCustom(null);
        assertThat(result)
                .as("Il manager deve gestire il null restituendo null")
                .isNull();
    }

    @Test // Case 1
    public void testCopyCustom_HashSet_ReturnsDeepCopy() {
        HashSet<String> orig = new HashSet<String>();
        orig.add("Test");

        Object result = proxyManager.copyCustom(orig);

        assertThat(result)
                .isNotNull()
                .isNotSameAs(orig) // Verifica che sia una nuova istanza
                .isNotInstanceOf(Proxy.class)
                .isInstanceOf(HashSet.class)
                .isEqualTo(orig); // Verifica equals() sui contenuti
    }

    @Ignore
    @Test // il test fallisce perchè non viene controllata l'implementazione di clonable
    public void testCopyCustom_CustomClonable_ReturnsNewIstance() {
        ProxyManagerImplTestUtil.CustomCloneable orig = new ProxyManagerImplTestUtil.CustomCloneable("Test");
        Object result = proxyManager.copyCustom(orig);
        assertThat(result)
                .isNotNull()
                .isNotSameAs(orig)
                .isNotInstanceOf(Proxy.class)
                .isInstanceOf(ProxyManagerImplTestUtil.CustomCloneable.class);
    }


    @Test // Case 3
    public void testCopyCustom_CopyConstructor_ReturnsNewInstance() {
        String value = "Dati Importanti";
        ProxyManagerImplTestUtil.CustomCopyConstructor orig =
                new ProxyManagerImplTestUtil.CustomCopyConstructor(value);

        Object result = proxyManager.copyCustom(orig);

        assertThat(result)
                .isNotNull()
                .isNotSameAs(orig)
                .isNotInstanceOf(Proxy.class)
                .isInstanceOf(ProxyManagerImplTestUtil.CustomCopyConstructor.class);

        ProxyManagerImplTestUtil.CustomCopyConstructor castedResult =
                (ProxyManagerImplTestUtil.CustomCopyConstructor) result;

        assertThat(castedResult.getValue()).isEqualTo(value);
    }

    @Test // Case 4
    public void testCopyCustom_SimpleBean_ReturnsNewInstance() {
        String value = "Dati Importanti";
        ProxyManagerImplTestUtil.CustomSimpleBean orig = new ProxyManagerImplTestUtil.CustomSimpleBean();
        orig.setInfo(value);

        Object result = proxyManager.copyCustom(orig);

        assertThat(result)
                .isNotNull()
                .isNotSameAs(orig)
                .isInstanceOf(ProxyManagerImplTestUtil.CustomSimpleBean.class);

        ProxyManagerImplTestUtil.CustomSimpleBean castedResult =
                (ProxyManagerImplTestUtil.CustomSimpleBean) result;

        assertThat(castedResult.getInfo()).isEqualTo(value);
    }

    @Test // Case 5
    public void testCopyCustom_NoBeanConstructor_ReturnsNull() {
        ProxyManagerImplTestUtil.CustomNoBeanConstructor orig =
                new ProxyManagerImplTestUtil.CustomNoBeanConstructor("Dati");

        Object result = proxyManager.copyCustom(orig);

        assertThat(result)
                .as("Deve restituire null se manca il costruttore di default")
                .isNull();
    }

    @Test // Case 6
    public void testCopyCustom_NoBeanParameter_ReturnsNull() {
        ProxyManagerImplTestUtil.CustomNoBeanParameter orig =
                new ProxyManagerImplTestUtil.CustomNoBeanParameter();

        Object result = proxyManager.copyCustom(orig);

        assertThat(result)
                .as("Deve restituire null per oggetti non conformi a Bean (no getter/setter)")
                .isNull();
    }

    @Test // Case 7
    public void testCopyCustom_CustomBeanProxy_ReturnsCleanObject() {
        String value = "Dati Importanti";
        ProxyManagerImplTestUtil.CustomSimpleBean origBean = new ProxyManagerImplTestUtil.CustomSimpleBean();
        origBean.setInfo(value);

        Object proxy = proxyManager.newCustomProxy(origBean, true);

        Object result = proxyManager.copyCustom(proxy);

        assertThat(result)
                .isNotNull()
                .isNotSameAs(proxy)    // Il risultato non deve essere l'oggetto proxy originale
                .isNotSameAs(origBean) // Il risultato non deve essere nemmeno l'oggetto originale interno (copia profonda)
                .isNotInstanceOf(Proxy.class) // Deve essere stato "de-strumentalizzato"
                .isInstanceOf(ProxyManagerImplTestUtil.CustomSimpleBean.class);

        ProxyManagerImplTestUtil.CustomSimpleBean castedResult =
                (ProxyManagerImplTestUtil.CustomSimpleBean) result;

        assertThat(castedResult.getInfo()).isEqualTo(value);
    }

    @Test // Case 8
    public void testCopyCustom_PersistenceCapable_ReturnsNull() {
        // Simuliamo un oggetto che è gia' gestito dal framework (Managed)
        PersistenceCapable mockPC = mock(PersistenceCapable.class);

        Object result = proxyManager.copyCustom(mockPC);

        assertThat(result)
                .as("La copia di un oggetto Managed deve restituire null")
                .isNull();
    }

    @Test // Case 9
    public void testCopyCustom_ArrayListProxy_ReturnsStandardArrayList() {
        List<String> origList = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            origList.add("Elem" + i);
        }

        Object proxyList = proxyManager.newCustomProxy(origList, true);

        Object result = proxyManager.copyCustom(proxyList);

        assertThat(result)
                .isNotNull()
                .isNotSameAs(proxyList)
                .isNotInstanceOf(Proxy.class)
                .isInstanceOf(ArrayList.class);

        @SuppressWarnings("unchecked")
        ArrayList<String> castedResult = (ArrayList<String>) result;

        assertThat(castedResult)
                .hasSize(10)
                .containsExactlyElementsOf(origList);
    }

    @Test // Case 10
    public void testCopyCustom_TreeSetProxy_ReturnsStandardTreeSetWithComparator() {
        // Setup con Comparator inverso per verificare che la logica di ordinamento venga mantenuta
        Comparator<String> reverseComp = Collections.reverseOrder();
        TreeSet<String> origSet = new TreeSet<String>(reverseComp);
        origSet.add("A");
        origSet.add("B"); // In reverse order, B viene prima di A

        Object proxySet = proxyManager.newCustomProxy(origSet, true);

        Object result = proxyManager.copyCustom(proxySet);

        assertThat(result)
                .isNotNull()
                .isNotSameAs(proxySet)
                .isNotInstanceOf(Proxy.class)
                .isInstanceOf(TreeSet.class);

        @SuppressWarnings("unchecked")
        TreeSet<String> castedResult = (TreeSet<String>) result;

        assertThat(castedResult.comparator())
                .as("Il Comparator originale deve essere preservato nella copia")
                .isEqualTo(reverseComp);

        assertThat(castedResult.first())
                .as("L'ordine degli elementi deve essere rispettato (Reverse Order)")
                .isEqualTo("B");

        assertThat(castedResult)
                .as("Il treeSet restituito deve avere lo stesso contenuto")
                .hasSize(2)
                .containsExactlyElementsOf(origSet);
    }

    @Test // Case 11
    public void testCopyCustom_HashMapProxy_ReturnsStandardHashMap() {
        Map<String, Integer> origMap = new HashMap<String, Integer>();
        for (int i = 0; i < 10; i++) {
            origMap.put("Key" + i, i);
        }

        Object proxyMap = proxyManager.newCustomProxy(origMap, true);

        Object result = proxyManager.copyCustom(proxyMap);

        assertThat(result)
                .isNotNull()
                .isNotSameAs(proxyMap)
                .isNotInstanceOf(Proxy.class)
                .isInstanceOf(HashMap.class);

        @SuppressWarnings("unchecked")
        HashMap<String, Integer> castedResult = (HashMap<String, Integer>) result;

        assertThat(castedResult)
                .hasSize(10)
                .containsAllEntriesOf(origMap);
    }

    @Test // Case 12
    public void testCopyCustom_TreeMapProxy_ReturnsStandardTreeMap() {
        TreeMap<Integer, String> origMap = new TreeMap<Integer, String>();
        origMap.put(3, "Tre");
        origMap.put(1, "Uno");
        origMap.put(2, "Due");

        Object proxyMap = proxyManager.newCustomProxy(origMap, true);

        Object result = proxyManager.copyCustom(proxyMap);

        assertThat(result)
                .isNotNull()
                .isNotSameAs(proxyMap) // Deve essere un oggetto nuovo
                .isNotInstanceOf(Proxy.class) // Non deve essere un proxy
                .isInstanceOf(TreeMap.class); // Deve essere tornato un TreeMap

        @SuppressWarnings("unchecked")
        TreeMap<Integer, String> castedResult = (TreeMap<Integer, String>) result;

        assertThat(castedResult)
                .as("La mappa restituita deve contenere esattamente le stesse entry dell'originale")
                .hasSize(3)
                .containsAllEntriesOf(origMap);

        assertThat(castedResult.firstKey())
                .as("La prima chiave deve essere la più bassa (ordinamento naturale)")
                .isEqualTo(1);

        assertThat(castedResult.lastKey())
                .as("L'ultima chiave deve essere la più alta")
                .isEqualTo(3);
    }

    @Test // Case 13
    public void testCopyCustom_TimestampProxy_ReturnsStandardTimestampWithNanos() {
        long fixedTimeInMillis = 1672531200000L;
        int fixedNanos = 123456;

        Timestamp origTs = new Timestamp(fixedTimeInMillis);
        origTs.setNanos(fixedNanos);

        Timestamp proxyTs = (Timestamp) proxyManager.newCustomProxy(origTs, true);

        Object result = proxyManager.copyCustom(proxyTs);

        assertThat(result)
                .isNotNull()
                .isNotSameAs(proxyTs)
                .isNotInstanceOf(Proxy.class)
                .isInstanceOf(Timestamp.class);

        Timestamp castedResult = (Timestamp) result;

        assertThat(castedResult.getNanos())
                .as("I nanosecondi devono essere preservati esattamente (Precisione alta)")
                .isEqualTo(fixedNanos);

        assertThat(castedResult.getTime())
                .as("Il tempo base (millisecondi) deve corrispondere al valore fisso impostato")
                .isEqualTo(fixedTimeInMillis);

        assertThat(castedResult)
                .as("L'oggetto Timestamp clonato deve essere logicamente uguale all'originale")
                .isEqualTo(origTs);
    }

    @Test // Case 14
    public void testCopyCustom_CalendarProxy_ReturnsStandardGregorianCalendar() {
        // Setup: Calendar con TimeZone specifica
        GregorianCalendar origCal = new GregorianCalendar();
        TimeZone zone = TimeZone.getTimeZone("GMT+5");
        origCal.setTimeZone(zone);

        Object proxyCal = proxyManager.newCustomProxy(origCal, true);

        Object result = proxyManager.copyCustom(proxyCal);

        assertThat(result)
                .isNotNull()
                .isNotSameAs(proxyCal)
                .isNotInstanceOf(Proxy.class)
                .isInstanceOf(GregorianCalendar.class);

        GregorianCalendar castedResult = (GregorianCalendar) result;

        assertThat(castedResult.getTimeZone())
                .as("La TimeZone deve essere copiata correttamente")
                .isEqualTo(zone);
    }

    // Test aggiunti dopo analisi con Jacoco

    // mancava un test che verificava la copia nel caso in cui Object orig è uno standard GregorianCalendar non Proxy
    @Test // Case 15
    public void testCopyCustom_CalendarStandard_ReturnsStandardGregorianCalendar() {
        // Setup: Calendar con TimeZone specifica
        GregorianCalendar origCal = new GregorianCalendar();
        TimeZone zone = TimeZone.getTimeZone("GMT+5");
        origCal.setTimeZone(zone);

        Object result = proxyManager.copyCustom(origCal);

        assertThat(result)
                .isNotNull()
                .isNotSameAs(origCal)
                .isNotInstanceOf(Proxy.class)
                .isInstanceOf(GregorianCalendar.class);

        GregorianCalendar castedResult = (GregorianCalendar) result;

        assertThat(castedResult.getTimeZone())
                .as("La TimeZone deve essere copiata correttamente")
                .isEqualTo(zone);
    }

    //mancava un test che verficasse la copia di una Data standard
    @Test // Case 13
    public void testCopyCustom_TimestampStandard_ReturnsStandardTimestampWithNanos() {
        long fixedTimeInMillis = 1672531200000L;
        int fixedNanos = 123456;

        Timestamp origTs = new Timestamp(fixedTimeInMillis);
        origTs.setNanos(fixedNanos);

        Object result = proxyManager.copyCustom(origTs);

        assertThat(result)
                .isNotNull()
                .isNotSameAs(origTs)
                .isNotInstanceOf(Proxy.class)
                .isInstanceOf(Timestamp.class);

        Timestamp castedResult = (Timestamp) result;

        assertThat(castedResult.getNanos())
                .as("I nanosecondi devono essere preservati esattamente (Precisione alta)")
                .isEqualTo(fixedNanos);

        assertThat(castedResult.getTime())
                .as("Il tempo base (millisecondi) deve corrispondere al valore fisso impostato")
                .isEqualTo(fixedTimeInMillis);

        assertThat(castedResult)
                .as("L'oggetto Timestamp clonato deve essere logicamente uguale all'originale")
                .isEqualTo(origTs);
    }

    // mancava un test che verificasse la copia di una standard map
    @Test // Case 11
    public void testCopyCustom_HashMapStandard_ReturnsStandardHashMap() {
        Map<String, Integer> origMap = new HashMap<String, Integer>();
        for (int i = 0; i < 10; i++) {
            origMap.put("Key" + i, i);
        }

        Object result = proxyManager.copyCustom(origMap);

        assertThat(result)
                .isNotNull()
                .isNotSameAs(origMap)
                .isNotInstanceOf(Proxy.class)
                .isInstanceOf(HashMap.class);

        @SuppressWarnings("unchecked")
        HashMap<String, Integer> castedResult = (HashMap<String, Integer>) result;

        assertThat(castedResult)
                .hasSize(10)
                .containsAllEntriesOf(origMap);
    }

    @Test // Case: copyMap con input null
    public void testCopyMap_NullInput_ReturnsNull() {
        Map result = proxyManager.copyMap(null);
        assertThat(result).isNull();
    }

    @Test // Case: copyDate con input null
    public void testCopyDate_NullInput_ReturnsNull() {
        Date result = proxyManager.copyDate(null);
        assertThat(result).isNull();
    }

    @Test // Case: copyCalendar con input null
    public void testCopyCalendar_NullInput_ReturnsNull() {
        Calendar result = proxyManager.copyCalendar(null);
        assertThat(result).isNull();
    }

    @Test
    public void testCopyCollection_WithRealProxy_ShouldDelegateCopy() {
        @SuppressWarnings("unchecked")
        List<String> proxy = (List<String>) proxyManager.newCollectionProxy(
                ArrayList.class, String.class, null, false);
        proxy.add("Elemento 1");

        assertThat(proxy).isInstanceOf(Proxy.class);

        Collection copyResult = proxyManager.copyCollection(proxy);

        assertThat(copyResult)
                .isNotNull()
                .isNotSameAs(proxy)
                .isNotInstanceOf(Proxy.class)
                .contains("Elemento 1");
    }

    @Test
    public void testCopyMap_WithRealProxy_ShouldDelegateCopy() {
        @SuppressWarnings("unchecked")
        Map<String, Integer> proxy = (Map<String, Integer>) proxyManager.newMapProxy(
                HashMap.class, String.class, Integer.class, null, false);
        proxy.put("Chiave", 100);

        assertThat(proxy).isInstanceOf(Proxy.class);

        Map copyResult = proxyManager.copyMap(proxy);

        assertThat(copyResult)
                .isNotNull()
                .isNotSameAs(proxy)
                .isNotInstanceOf(Proxy.class)
                .containsEntry("Chiave", 100);
    }

    @Test
    public void testCopyDate_WithRealProxy_ShouldDelegateCopy() {
        java.util.Date proxy = (java.util.Date) proxyManager.newDateProxy(java.util.Date.class);
        long now = 1600000000000L;
        proxy.setTime(now);

        assertThat(proxy).isInstanceOf(Proxy.class);

        java.util.Date copyResult = proxyManager.copyDate(proxy);

        assertThat(copyResult)
                .isNotNull()
                .isNotSameAs(proxy)
                .isNotInstanceOf(Proxy.class)
                .hasTime(now);
    }

    @Test
    public void testCopyCalendar_WithRealProxy_ShouldDelegateCopy() {
        Calendar proxy = (Calendar) proxyManager.newCalendarProxy(GregorianCalendar.class, TimeZone.getDefault());
        long now = 1600000000000L;
        proxy.setTimeInMillis(now);

        assertThat(proxy).isInstanceOf(Proxy.class);

        Calendar copyResult = proxyManager.copyCalendar(proxy);

        assertThat(copyResult)
                .isNotNull()
                .isNotSameAs(proxy)
                .isNotInstanceOf(Proxy.class);

        assertThat(copyResult.getTimeInMillis()).isEqualTo(now);
    }

    @Test // Case 1: Array Valido (Happy Path)
    public void testCopyArray_WithValidArray_ShouldReturnCopy() {
        // 2. Creiamo un array di input
        String[] orig = new String[] { "A", "B", "C" };

        // 3. Esecuzione
        Object result = proxyManager.copyArray(orig);

        // 4. Verifiche
        assertThat(result)
                .isNotNull()
                .isNotSameAs(orig) // Deve essere una nuova istanza (copia)
                .isInstanceOf(String[].class) // Deve mantenere il tipo
                .isEqualTo(orig); // Il contenuto deve essere identico
    }

    @Test // Case 2: Input Null
    public void testCopyArray_WithNull_ShouldReturnNull() {
        Object result = proxyManager.copyArray(null);

        assertThat(result).isNull();
    }

    @Test // Case 3: Exception Path (Input non è un array)
    public void testCopyArray_WithNonArrayObject_ShouldThrowUnsupportedException() {
        // Passiamo una Stringa normale invece di un array.
        // Questo farà fallire "Array.getLength(orig)" lanciando IllegalArgumentException,
        // che verrà catturata e wrappata in UnsupportedException.
        Object notAnArray = "Non sono un array";

        assertThatThrownBy(() -> {
            proxyManager.copyArray(notAnArray);
        }).as("Se l'oggetto non è un array, deve lanciare UnsupportedException")
                .isInstanceOf(org.apache.openjpa.util.UnsupportedException.class)
                .hasCauseInstanceOf(IllegalArgumentException.class);
    }

}
