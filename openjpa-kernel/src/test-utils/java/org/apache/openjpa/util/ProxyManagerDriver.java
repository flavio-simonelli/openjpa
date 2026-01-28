package org.apache.openjpa.util;

import java.util.*;

public class ProxyManagerDriver {

    private final ProxyManagerImpl proxyManager;

    public ProxyManagerDriver() {
        this.proxyManager = new ProxyManagerImpl();
    }

    // --- CONFIGURAZIONE (Sicuri da testare) ---
    public void setTrackChanges(boolean track) {
        proxyManager.setTrackChanges(track);
    }

    public boolean getTrackChanges() {
        return proxyManager.getTrackChanges();
    }

    public void setAssertAllowedType(boolean assertType) {
        proxyManager.setAssertAllowedType(assertType);
    }

    public boolean getAssertAllowedType() {
        return proxyManager.getAssertAllowedType();
    }

    public void setDelayCollectionLoading(boolean delay) {
        proxyManager.setDelayCollectionLoading(delay);
    }

    // --- METODI DI COPIA (Il cuore della logica da testare) ---
    // Questi metodi usano logica Java standard e non generano classi al volo

    public Object copyArray(Object array) {
        return proxyManager.copyArray(array);
    }

    public Collection<?> copyCollection(Collection<?> collection) {
        return proxyManager.copyCollection(collection);
    }

    public Map<?, ?> copyMap(Map<?, ?> map) {
        return proxyManager.copyMap(map);
    }

    public Date copyDate(Date date) {
        return proxyManager.copyDate(date);
    }

    public Calendar copyCalendar(Calendar calendar) {
        return proxyManager.copyCalendar(calendar);
    }

    public Object copyCustom(Object custom) {
        return proxyManager.copyCustom(custom);
    }

    // --- METODI RIMOSSI TEMPORANEAMENTE ---
    // newDateProxy, newCalendarProxy, newMapProxy, newCollectionProxy, newCustomProxy
    // causano il crash ClassNotFoundException in EvoSuite.
}