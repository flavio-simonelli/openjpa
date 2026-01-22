package org.apache.openjpa.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builder per creare istanze di CacheMap pre-configurate e pre-popolate per i test.
 * Esempio d'uso:
 * CacheMap map = new CacheMapBuilder()
 * .lru(true)
 * .maxSize(50)
 * .populatedWith(10)
 * .build();
 */
public class CacheMapBuilder {

    // Valori di Default
    private int max = 1000;
    private boolean lru = false;
    private int initialCapacity = 500;
    private float loadFactor = 0.75f;
    private int concurrencyLevel = 1;

    // --- LISTE SEPARATE PER STATO ---
    // Stato per la popolazione (si usano liste per evitare di testare la cachemap con dati che si inseriscono in una hashMap
    // si perdono i duplicati e perdiamo l'ordine di inserimento che è fondamentale per testare anche la politica di LRU)

    // 1. Hard (Standard)
    private List<Object> hardKeys = new ArrayList<>();
    private List<Object> hardValues = new ArrayList<>();

    // 2. Soft (Forzati direttamente nella softMap)
    private List<Object> softKeys = new ArrayList<>();
    private List<Object> softValues = new ArrayList<>();

    // 3. Pinned (Put + Pin)
    private List<Object> pinnedKeys = new ArrayList<>();
    private List<Object> pinnedValues = new ArrayList<>();

    // construttore privato impedisce l'istanziazione
    private CacheMapBuilder() {}

    // factory method statico
    public static CacheMapBuilder aCacheMapBuilder() {return new CacheMapBuilder();}

    public CacheMapBuilder maxSize(int max) {
        this.max = max;
        return this;
    }

    public CacheMapBuilder lru(boolean isLru) {
        this.lru = isLru;
        return this;
    }

    public CacheMapBuilder initialCapacity(int initialCapacity) {
        this.initialCapacity = initialCapacity;
        return this;
    }

    public CacheMapBuilder loadFactor(float loadFactor) {
        this.loadFactor = loadFactor;
        return this;
    }

    /**
     * Inserisce entry nella Hard Map (Comportamento standard).
     */
    public CacheMapBuilder withHardEntries(List<Object> keys, List<Object> values) {
        validateLists(keys, values);
        this.hardKeys.addAll(keys);
        this.hardValues.addAll(values);
        return this;
    }

    /**
     * Inserisce entry e le blocca (Pinned).
     * Internamente esegue una put() seguita da una pin().
     */
    public CacheMapBuilder withPinnedEntries(List<Object> keys, List<Object> values) {
        validateLists(keys, values);
        this.pinnedKeys.addAll(keys);
        this.pinnedValues.addAll(values);
        return this;
    }

    /**
     * Inserisce entry FORZATAMENTE nella Soft Map.
     * Bypassando i controlli di dimensione della Hard Map.
     */
    public CacheMapBuilder withSoftEntries(List<Object> keys, List<Object> values) {
        validateLists(keys, values);
        this.softKeys.addAll(keys);
        this.softValues.addAll(values);
        return this;
    }

    // Helper per validazione
    private void validateLists(List<Object> k, List<Object> v) {
        if (k == null || v == null) throw new IllegalArgumentException("Lists cannot be null");
        if (k.size() != v.size()) throw new IllegalArgumentException("Key/Value lists size mismatch");
    }

    public CacheMapBuilder concurrency(int level) {
        this.concurrencyLevel = level;
        return this;
    }

    /**
     * Creerà una CacheMap prepopolata utilizzando l'operazione map.put inserendo all'interno gli oggetti ricevuti
     * @return CacheMap prepopolata
     */
    public CacheMap build() {
        CacheMap map = new CacheMap(lru, max, initialCapacity, loadFactor, concurrencyLevel);

        // 1. Inserimento Pinned keys
        for (int i = 0; i < pinnedKeys.size(); i++) {
            Object k = pinnedKeys.get(i);
            Object v = pinnedValues.get(i);
            map.put(k, v); // Inserisce in mappa
            map.pin(k);    // Blocca la chiave
        }

        // 3. Inserimento Soft Entries (Chirurgico)
        for (int i = 0; i < softKeys.size(); i++) {
            map.put(softKeys.get(i), softValues.get(i));
        }

        // 2. Inserimento Hard Entries
        for (int i = 0; i < hardKeys.size(); i++) {
            map.put(hardKeys.get(i), hardValues.get(i));
        }

        // 3. Inserimento Soft Entries (Chirurgico)
        // Accediamo direttamente al campo protected 'softMap'

        return map;
    }
}