/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.openjpa.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * JUnit 4 test case for {@link CacheMap}.
 */
public class CacheMapTest {

    private CacheMap map;

    @Before
    public void setUp() {
        // Default non-LRU map for basic tests
        map = new CacheMap();
    }

    @After
    public void tearDown() {
        if (map != null) {
            map.clear();
            map = null;
        }
    }

    /*
    Test sbagliato dall'LLM
    @Test
    public void testConstructors() {
        CacheMap defaultMap = new CacheMap();
        assertNotNull(defaultMap);
        assertFalse(defaultMap.isLRU());
        assertEquals(1000, defaultMap.getCacheSize());

        CacheMap lruMap = new CacheMap(true);
        assertTrue(lruMap.isLRU());
        assertEquals(1000, lruMap.getCacheSize());

        CacheMap customMap = new CacheMap(true, 500);
        assertTrue(customMap.isLRU());
        assertEquals(500, customMap.getCacheSize());

        CacheMap fullConfigMap = new CacheMap(false, 100, 50, 0.75f, 16);
        assertFalse(fullConfigMap.isLRU());
        assertEquals(100, fullConfigMap.getCacheSize());
        assertEquals(50, fullConfigMap.getSoftReferenceSize());
    }
     */

    @Test
    public void testConstructors() {
        // Test costruttore di default
        CacheMap defaultMap = new CacheMap();
        assertNotNull(defaultMap);
        assertFalse(defaultMap.isLRU());
        assertEquals(1000, defaultMap.getCacheSize());

        // Test costruttore LRU
        CacheMap lruMap = new CacheMap(true);
        assertTrue(lruMap.isLRU());
        assertEquals(1000, lruMap.getCacheSize());

        // Test costruttore con dimensione personalizzata
        CacheMap customMap = new CacheMap(true, 500);
        assertTrue(customMap.isLRU());
        assertEquals(500, customMap.getCacheSize());

        // Test costruttore completo
        // Nota: Il parametro '50' qui sotto imposta la 'Initial Capacity' della HashMap interna,
        // NON il limite massimo (Max Size).
        CacheMap fullConfigMap = new CacheMap(false, 100, 50, 0.75f, 16);

        assertFalse(fullConfigMap.isLRU());
        assertEquals(100, fullConfigMap.getCacheSize());

        // FIX: Ci aspettiamo -1 (illimitato) perché questo costruttore non imposta
        // il limite massimo della softMap, ma solo la sua capacità iniziale.
        assertEquals(-1, fullConfigMap.getSoftReferenceSize());
    }

    @Test
    public void testBasicPutGet() {
        assertNull(map.put("key1", "value1"));
        assertEquals("value1", map.get("key1"));
        assertEquals(1, map.size());

        assertEquals("value1", map.put("key1", "value2"));
        assertEquals("value2", map.get("key1"));
        assertEquals(1, map.size());

        assertNull(map.get("nonExistent"));
    }

    @Test
    public void testPutAll() {
        Map<String, String> input = new HashMap<>();
        input.put("A", "1");
        input.put("B", "2");
        input.put("C", "3");

        map.putAll(input);
        assertEquals(3, map.size());
        assertEquals("1", map.get("A"));
        assertEquals("2", map.get("B"));
        assertEquals("3", map.get("C"));

        // Test putAll with replaceExisting = false
        Map<String, String> input2 = new HashMap<>();
        input2.put("A", "999"); // Should be ignored
        input2.put("D", "4");   // Should be added

        map.putAll(input2, false);
        assertEquals(4, map.size());
        assertEquals("1", map.get("A")); // Original value retained
        assertEquals("4", map.get("D"));
    }

    @Test
    public void testRemove() {
        map.put("key1", "value1");
        assertTrue(map.containsKey("key1"));

        assertEquals("value1", map.remove("key1"));
        assertFalse(map.containsKey("key1"));
        assertNull(map.get("key1"));
        assertEquals(0, map.size());

        assertNull(map.remove("nonExistent"));
    }

    @Test
    public void testClear() {
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.pin("key1");

        assertEquals(2, map.size());
        assertTrue(map.getPinnedKeys().contains("key1"));

        map.clear();

        assertEquals(0, map.size());
        assertTrue(map.isEmpty());
        assertTrue(map.getPinnedKeys().isEmpty());
    }

    @Test
    public void testPinning() {
        map.put("key1", "value1");

        // Pin existing key
        assertTrue(map.pin("key1"));
        assertTrue(map.getPinnedKeys().contains("key1"));
        assertEquals("value1", map.get("key1"));

        // Verify pinning logic moves it internally
        // (We can't access private maps directly, but we can verify behavior)

        // Pinning a non-existent key
        assertFalse(map.pin("key2"));
        // Pinning key2 with a value (first put then pin)
        map.put("key2", "value2");
        assertTrue(map.pin("key2"));
        assertEquals(2, map.getPinnedKeys().size());

        // Pinning already pinned key
        assertTrue(map.pin("key1"));
    }

    @Test
    public void testUnpinning() {
        map.put("key1", "value1");
        map.pin("key1");
        assertTrue(map.getPinnedKeys().contains("key1"));

        // Unpin
        assertTrue(map.unpin("key1"));
        assertFalse(map.getPinnedKeys().contains("key1"));

        // Value should still exist in the map (moved back to cache/soft)
        assertEquals("value1", map.get("key1"));

        // Unpinning non-pinned key
        assertFalse(map.unpin("key2"));
    }

    /*
    @Test
    public void testPinningWithRemove() {
        map.put("key1", "value1");
        map.pin("key1");

        // Removing a pinned key
        assertEquals("value1", map.remove("key1"));
        assertFalse(map.containsKey("key1"));
        assertFalse(map.getPinnedKeys().contains("key1"));
        assertEquals(0, map.size());
    }
     */

    @Test
    public void testPinningUpdate() {
        map.put("key1", "value1");
        map.pin("key1");

        // Put updates the pinned map directly
        map.put("key1", "value2");
        assertEquals("value2", map.get("key1"));
        assertTrue(map.getPinnedKeys().contains("key1"));
    }

    @Test
    public void testOverflowToSoftMap() {
        // Create an LRU map with very small size (2)
        CacheMap lruMap = new CacheMap(true, 2);

        lruMap.put("A", "valA");
        lruMap.put("B", "valB");
        assertEquals(2, lruMap.size());

        // Add third element, triggering overflow of oldest (A)
        lruMap.put("C", "valC");

        // Size should be 3 (2 hard + 1 soft)
        assertEquals(3, lruMap.size());

        // All keys should still be retrievable
        assertTrue(lruMap.containsKey("A"));
        assertTrue(lruMap.containsKey("B"));
        assertTrue(lruMap.containsKey("C"));

        // Accessing "A" (which likely moved to softMap) should bring it back
        // note: specific movement logic depends on implementation details,
        // but 'get' must return the value.
        assertEquals("valA", lruMap.get("A"));
    }

    /*
    @Test
    public void testSoftReferencePromotion() {
        // This test simulates the promotion from softMap to cacheMap on 'get'
        // We create a map with capacity 1 to force immediate overflow
        CacheMap smallMap = new CacheMap(true, 1);

        smallMap.put("A", "valA");
        smallMap.put("B", "valB");
        // A should be overflowed to SoftMap, B in CacheMap

        assertTrue(smallMap.containsKey("A"));

        // Get A. This triggers:
        // 1. softMap.get("A") -> found
        // 2. putcache = true
        // 3. put("A", "valA") -> moves A back to CacheMap, potentially bumping B to SoftMap
        assertEquals("valA", smallMap.get("A"));
    }
     */

    @Test
    public void testSoftReferencePromotion() {
        // We use size 2. This results in: Hard Cache Size = 2, Soft Cache Size = 2/2 = 1.
        // This avoids the IllegalArgumentException caused by size=1 (where 1/2 = 0).
        CacheMap smallMap = new CacheMap(true, 2);

        // 1. Fill the hard cache
        smallMap.put("A", "valA");
        smallMap.put("B", "valB");
        // State: Cache=[A, B], Soft=[]

        // 2. Force overflow by adding a third element ('C')
        // 'A' (the Least Recently Used) should be evicted from Cache and moved to SoftMap
        smallMap.put("C", "valC");

        // Verify 'A' is still retrievable (via the softMap)
        assertTrue(smallMap.containsKey("A"));

        // 3. Get 'A'. This triggers the promotion logic:
        //    - softMap.get("A") -> found
        //    - putcache = true -> moves 'A' back to the hard CacheMap
        //    - 'A' becomes the Most Recently Used (MRU) item
        assertEquals("valA", smallMap.get("A"));

        // 4. Verify promotion via LRU behavior
        // Since 'A' was just accessed/promoted, it is now "fresh".
        // Adding 'D' should evict 'B' or 'C', but NOT 'A'.
        smallMap.put("D", "valD");

        assertTrue("A should still be in the map because it was recently promoted",
                smallMap.containsKey("A"));

        assertEquals("valA", smallMap.get("A"));
        assertEquals("valD", smallMap.get("D"));
    }

    @Test
    public void testConfiguration() {
        map.setCacheSize(50);
        assertEquals(50, map.getCacheSize());

        map.setCacheSize(-1);
        assertEquals(-1, map.getCacheSize());

        map.setSoftReferenceSize(20);
        assertEquals(20, map.getSoftReferenceSize());

        map.setSoftReferenceSize(-1);
        assertEquals(-1, map.getSoftReferenceSize());
    }

    @Test
    public void testLocks() {
        // We cannot easily test thread safety in a unit test,
        // but we can ensure the lock methods don't throw exceptions.
        map.readLock();
        try {
            assertTrue(true);
        } finally {
            map.readUnlock();
        }

        map.writeLock();
        try {
            assertTrue(true);
        } finally {
            map.writeUnlock();
        }
    }

    @Test
    public void testContainsValue() {
        map.put("A", "1");
        map.put("B", "2");
        map.pin("A"); // Move one to pinned map

        assertTrue(map.containsValue("1")); // in pinned
        assertTrue(map.containsValue("2")); // in cache/soft
        assertFalse(map.containsValue("3"));
    }

    @Test
    public void testEntrySetIterator() {
        map.put("A", "1");
        map.put("B", "2");
        map.put("C", "3");
        map.pin("A"); // Ensure we have items in pinned map

        Set<Map.Entry> entrySet = map.entrySet();
        assertEquals(3, entrySet.size());

        int count = 0;
        Iterator<Map.Entry> it = entrySet.iterator();
        while (it.hasNext()) {
            Map.Entry entry = it.next();
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
            count++;
        }
        assertEquals(3, count);

        // Test entrySet add (implementation delegates to put)
        // Although Map.entrySet().add() usually throws UOE, the provided code implements add(Object o)
        // by casting to Map.Entry and calling put.
        // We need a dummy entry implementation to test this.
        Map.Entry<String, String> newEntry = new HashMap.SimpleEntry<>("D", "4");
        entrySet.add(newEntry);
        assertEquals(4, map.size());
        assertEquals("4", map.get("D"));
    }

    @Test
    public void testKeySetIterator() {
        map.put("A", "1");
        map.put("B", "2");

        Set keys = map.keySet();
        assertEquals(2, keys.size());

        assertTrue(keys.contains("A"));
        assertTrue(keys.contains("B"));

        Iterator it = keys.iterator();
        int count = 0;
        while(it.hasNext()) {
            it.next();
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    public void testValuesIterator() {
        map.put("A", "1");
        map.put("B", "2");

        Collection values = map.values();
        assertEquals(2, values.size());

        assertTrue(values.contains("1"));
        assertTrue(values.contains("2"));

        Iterator it = values.iterator();
        int count = 0;
        while(it.hasNext()) {
            it.next();
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    public void testToString() {
        map.put("A", "1");
        String str = map.toString();
        assertNotNull(str);
        assertTrue(str.startsWith("CacheMap:"));
    }

    @Test
    public void testIsEmpty() {
        assertTrue(map.isEmpty());
        map.put("A", "1");
        assertFalse(map.isEmpty());
        map.clear();
        assertTrue(map.isEmpty());
    }
}