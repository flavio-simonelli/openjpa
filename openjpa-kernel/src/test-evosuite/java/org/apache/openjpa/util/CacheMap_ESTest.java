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

import org.junit.Test;
import static org.junit.Assert.*;
import static org.evosuite.shaded.org.mockito.Mockito.*;
import static org.evosuite.runtime.EvoAssertions.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.apache.openjpa.lib.util.LRUMap;
import org.apache.openjpa.lib.util.ReferenceHashMap;
import org.apache.openjpa.lib.util.collections.AbstractReferenceMap;
import org.apache.openjpa.util.CacheMap;
import org.evosuite.runtime.EvoRunner;
import org.evosuite.runtime.EvoRunnerParameters;
import org.evosuite.runtime.ViolatedAssumptionAnswer;
import org.junit.runner.RunWith;

@RunWith(EvoRunner.class) @EvoRunnerParameters(mockJVMNonDeterminism = true,
        useVFS = true,
        useVNET = true,
        resetStaticState = true,
        separateClassLoader = true
)
public class CacheMap_ESTest extends CacheMap_ESTest_scaffolding {

    @Test(timeout = 4000)
    public void test00()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(true);
        Object object0 = new Object();
        cacheMap0.cacheMapOverflowRemoved((Object) null, object0);
        boolean boolean0 = cacheMap0.isEmpty();
        assertEquals(1000, cacheMap0.getCacheSize());
        assertFalse(boolean0);
    }

    @Test(timeout = 4000)
    public void test01()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        Function<Object, Object> function0 = Function.identity();
        CacheMap cacheMap1 = (CacheMap)cacheMap0.computeIfAbsent(cacheMap0, function0);
        boolean boolean0 = cacheMap0.pin(cacheMap0);
        assertTrue(boolean0);

        CacheMap cacheMap2 = (CacheMap)cacheMap1.replace((Object) cacheMap1, (Object) null);
        assertNotNull(cacheMap2);

        CacheMap cacheMap3 = (CacheMap)cacheMap0.put(cacheMap2, (Object) null);
        assertNull(cacheMap3);
        assertEquals(1000, cacheMap2.getCacheSize());
        assertEquals(1000, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test02()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(false);
        cacheMap0.setSoftReferenceSize(0);
        assertEquals(0, cacheMap0.getSoftReferenceSize());
    }

    @Test(timeout = 4000)
    public void test03()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(false, 176, 176, 0.75F, 176);
        cacheMap0.setCacheSize(176);
        assertEquals(176, cacheMap0.getCacheSize());
        assertEquals((-1), cacheMap0.getSoftReferenceSize());
    }

    @Test(timeout = 4000)
    public void test04()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(false, 176, 176, 0.75F, 176);
        Set set0 = cacheMap0.entrySet();
        cacheMap0.softMapOverflowRemoved(set0, (Object) null);
        assertEquals(176, cacheMap0.getCacheSize());
        assertEquals(0, set0.size());
    }

    @Test(timeout = 4000)
    public void test05()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(true);
        HashMap<CacheMap, CacheMap> hashMap0 = new HashMap<CacheMap, CacheMap>();
        cacheMap0.put(cacheMap0, hashMap0);
        cacheMap0.setCacheSize(0);
        assertEquals(0, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test06()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(false);
        cacheMap0.writeLock();
        cacheMap0.writeUnlock();
        assertEquals(1000, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test07()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(true, 0, 524, 0.75F);
        assertEquals(0, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test08()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(false);
        LRUMap lRUMap0 = new LRUMap(1000, 3467.3047F);
        cacheMap0.entryRemoved(lRUMap0, (Object) null, false);
        assertEquals(1000, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test09()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        Object object0 = new Object();
        cacheMap0.entryAdded(object0, (Object) null);
        assertEquals(1000, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test10()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        cacheMap0.putIfAbsent(cacheMap0, cacheMap0);
        int int0 = cacheMap0.size();
        assertEquals(1, int0);
        assertEquals(1000, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test11()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(false, 0);
        CacheMap cacheMap1 = new CacheMap(false);
        Object object0 = cacheMap0.remove((Map) cacheMap1, (Object) cacheMap1);
        assertNull(object0);
        assertEquals(0, cacheMap0.getCacheSize());
        assertEquals(1000, cacheMap1.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test12()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        Function<Object, Object> function0 = Function.identity();
        CacheMap cacheMap1 = (CacheMap)cacheMap0.computeIfAbsent(cacheMap0, function0);
        CacheMap cacheMap2 = (CacheMap)cacheMap1.remove((Map) cacheMap1, (Object) cacheMap1);
        assertNotNull(cacheMap2);
        assertEquals(1000, cacheMap2.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test13()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        cacheMap0.put((Object) null, cacheMap0);
        CacheMap cacheMap1 = (CacheMap)cacheMap0.put((Map) cacheMap0, (Object) null, (Object) null);
        assertNotNull(cacheMap1);
        assertEquals(1000, cacheMap1.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test14()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(false, 176, 176, 0.75F, 176);
        cacheMap0.isLRU();
        assertEquals(176, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test15()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(false);
        int int0 = cacheMap0.getCacheSize();
        assertEquals(1000, int0);
    }

    @Test(timeout = 4000)
    public void test16()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        cacheMap0.get((Object) null);
        assertEquals(1000, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test17()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        Function<Object, Object> function0 = Function.identity();
        Object object0 = cacheMap0.computeIfAbsent(cacheMap0, function0);
        CacheMap cacheMap1 = (CacheMap)cacheMap0.get(object0);
        assertNotNull(cacheMap1);
        assertEquals(1000, cacheMap1.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test18()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(true);
        // Undeclared exception!
        try {
            cacheMap0.remove((Map) null, (Object) cacheMap0);
            fail("Expecting exception: NullPointerException");

        } catch(NullPointerException e) {
            //
            // no message in exception (getMessage() returned null)
            //
            verifyException("org.apache.openjpa.util.CacheMap", e);
        }
    }

    @Test(timeout = 4000)
    public void test19()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(false, 0);
        // Undeclared exception!
        try {
            cacheMap0.remove((Object) null);
            fail("Expecting exception: ArithmeticException");

        } catch(ArithmeticException e) {
            //
            // / by zero
            //
            verifyException("org.apache.openjpa.lib.util.concurrent.ConcurrentReferenceHashMap", e);
        }
    }

    @Test(timeout = 4000)
    public void test20()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        // Undeclared exception!
        try {
            cacheMap0.putAll((Map) null, false);
            fail("Expecting exception: NullPointerException");

        } catch(NullPointerException e) {
            //
            // no message in exception (getMessage() returned null)
            //
            verifyException("org.apache.openjpa.util.CacheMap", e);
        }
    }

    @Test(timeout = 4000)
    public void test21()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(false, 14);
        Collection collection0 = cacheMap0.values();
        LRUMap lRUMap0 = new LRUMap(14);
        // Undeclared exception!
        try {
            cacheMap0.put((Map) null, (Object) collection0, (Object) lRUMap0);
            fail("Expecting exception: NullPointerException");

        } catch(NullPointerException e) {
            //
            // no message in exception (getMessage() returned null)
            //
            verifyException("org.apache.openjpa.util.CacheMap", e);
        }
    }

    @Test(timeout = 4000)
    public void test22()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        AbstractReferenceMap.ReferenceStrength abstractReferenceMap_ReferenceStrength0 = AbstractReferenceMap.ReferenceStrength.WEAK;
        ReferenceHashMap referenceHashMap0 = new ReferenceHashMap(abstractReferenceMap_ReferenceStrength0, abstractReferenceMap_ReferenceStrength0);
        // Undeclared exception!
        try {
            cacheMap0.put((Map) referenceHashMap0, (Object) null, (Object) referenceHashMap0);
            fail("Expecting exception: NullPointerException");

        } catch(NullPointerException e) {
            //
            // null keys not allowed
            //
            verifyException("org.apache.openjpa.lib.util.collections.AbstractReferenceMap", e);
        }
    }

    @Test(timeout = 4000)
    public void test23()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(true);
        CacheMap cacheMap1 = new CacheMap(false, 1);
        // Undeclared exception!
        try {
            cacheMap1.put((Object) null, cacheMap0);
            fail("Expecting exception: ArithmeticException");

        } catch(ArithmeticException e) {
            //
            // / by zero
            //
            verifyException("org.apache.openjpa.lib.util.concurrent.ConcurrentReferenceHashMap", e);
        }
    }

    @Test(timeout = 4000)
    public void test24()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(false, 0);
        // Undeclared exception!
        try {
            cacheMap0.pin(cacheMap0);
            fail("Expecting exception: ArithmeticException");

        } catch(ArithmeticException e) {
            //
            // / by zero
            //
            verifyException("org.apache.openjpa.lib.util.concurrent.ConcurrentReferenceHashMap", e);
        }
    }

    @Test(timeout = 4000)
    public void test25()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(false, 0);
        Object object0 = new Object();
        // Undeclared exception!
        try {
            cacheMap0.get(object0);
            fail("Expecting exception: ArithmeticException");

        } catch(ArithmeticException e) {
            //
            // / by zero
            //
            verifyException("org.apache.openjpa.lib.util.concurrent.ConcurrentReferenceHashMap", e);
        }
    }

    @Test(timeout = 4000)
    public void test26()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(false, 0);
        // Undeclared exception!
        try {
            cacheMap0.containsKey(cacheMap0);
            fail("Expecting exception: ArithmeticException");

        } catch(ArithmeticException e) {
            //
            // / by zero
            //
            verifyException("org.apache.openjpa.lib.util.concurrent.ConcurrentReferenceHashMap", e);
        }
    }

    @Test(timeout = 4000)
    public void test27()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        Object object0 = new Object();
        // Undeclared exception!
        try {
            cacheMap0.cacheMapOverflowRemoved(object0, (Object) null);
            fail("Expecting exception: IllegalArgumentException");

        } catch(IllegalArgumentException e) {
            //
            // Null references not supported
            //
            verifyException("org.apache.openjpa.lib.util.concurrent.ConcurrentReferenceHashMap", e);
        }
    }

    @Test(timeout = 4000)
    public void test28()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(false, 0);
        // Undeclared exception!
        try {
            cacheMap0.cacheMapOverflowRemoved((Object) null, cacheMap0);
            fail("Expecting exception: ArithmeticException");

        } catch(ArithmeticException e) {
            //
            // / by zero
            //
            verifyException("org.apache.openjpa.lib.util.concurrent.ConcurrentReferenceHashMap", e);
        }
    }

    @Test(timeout = 4000)
    public void test29()  throws Throwable  {
        CacheMap cacheMap0 = null;
        try {
            cacheMap0 = new CacheMap(true, (-2138), (-2138), (-2138));
            fail("Expecting exception: IllegalArgumentException");

        } catch(IllegalArgumentException e) {
            //
            // Illegal Load factor: -2138.0
            //
            verifyException("org.apache.openjpa.lib.util.concurrent.ConcurrentReferenceHashMap", e);
        }
    }

    @Test(timeout = 4000)
    public void test30()  throws Throwable  {
        CacheMap cacheMap0 = null;
        try {
            cacheMap0 = new CacheMap(true, 0);
            fail("Expecting exception: IllegalArgumentException");

        } catch(IllegalArgumentException e) {
            //
            // LRUMap max size must be greater than 0
            //
            verifyException("org.apache.openjpa.lib.util.collections.LRUMap", e);
        }
    }

    @Test(timeout = 4000)
    public void test31()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        cacheMap0.putIfAbsent(cacheMap0, (Object) null);
        cacheMap0.replace((Object) cacheMap0, (Object) cacheMap0);
        cacheMap0.setCacheSize(0);
        assertEquals(0, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test32()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        boolean boolean0 = cacheMap0.containsKey((Object) null);
        assertFalse(boolean0);
        assertEquals(1000, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test33()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        Object object0 = cacheMap0.putIfAbsent(cacheMap0, cacheMap0);
        assertNull(object0);

        CacheMap cacheMap1 = (CacheMap)cacheMap0.put(cacheMap0, cacheMap0);
        assertEquals(1000, cacheMap1.getCacheSize());
        assertNotNull(cacheMap1);
    }

    @Test(timeout = 4000)
    public void test34()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(true, 0, 1571, 1.0F, (-168));
        cacheMap0.put((Object) null, (Object) null);
        assertEquals(0, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test35()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(false);
        // Undeclared exception!
        try {
            cacheMap0.writeUnlock();
            fail("Expecting exception: IllegalMonitorStateException");

        } catch(IllegalMonitorStateException e) {
            //
            // no message in exception (getMessage() returned null)
            //
            verifyException("java.util.concurrent.locks.ReentrantReadWriteLock$Sync", e);
        }
    }

    @Test(timeout = 4000)
    public void test36()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(false);
        int int0 = cacheMap0.size();
        assertEquals(0, int0);
        assertEquals(1000, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test37()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(false);
        cacheMap0.readLock();
        assertEquals(1000, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test38()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(true);
        // Undeclared exception!
        try {
            cacheMap0.readUnlock();
            fail("Expecting exception: IllegalMonitorStateException");

        } catch(IllegalMonitorStateException e) {
            //
            // attempt to unlock read lock, not locked by current thread
            //
            verifyException("java.util.concurrent.locks.ReentrantReadWriteLock$Sync", e);
        }
    }

    @Test(timeout = 4000)
    public void test39()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        boolean boolean0 = cacheMap0.pin(cacheMap0);
        assertFalse(boolean0);

        cacheMap0.putAll((Map) cacheMap0, false);
        assertEquals(1000, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test40()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        Function<Object, Object> function0 = Function.identity();
        CacheMap cacheMap1 = (CacheMap)cacheMap0.computeIfAbsent(cacheMap0, function0);
        boolean boolean0 = cacheMap0.pin(cacheMap1);
        assertTrue(boolean0);

        boolean boolean1 = cacheMap1.containsValue(cacheMap0);
        assertTrue(boolean1);
        assertEquals(1000, cacheMap1.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test41()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        boolean boolean0 = cacheMap0.containsValue((Object) null);
        assertFalse(boolean0);
        assertEquals(1000, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test42()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        cacheMap0.put((Map) cacheMap0, (Object) cacheMap0, (Object) null);
        boolean boolean0 = cacheMap0.containsValue((Object) null);
        assertEquals(1000, cacheMap0.getCacheSize());
        assertTrue(boolean0);
    }

    @Test(timeout = 4000)
    public void test43()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        cacheMap0.cacheMapOverflowRemoved((Object) null, cacheMap0);
        cacheMap0.putAll((Map) cacheMap0, false);
        assertEquals(1000, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test44()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        Function<Object, Object> function0 = Function.identity();
        CacheMap cacheMap1 = (CacheMap)cacheMap0.computeIfAbsent(cacheMap0, function0);
        boolean boolean0 = cacheMap1.pin(cacheMap0);
        assertTrue(boolean0);

        cacheMap0.putAll((Map) cacheMap0, false);
        assertEquals(1000, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test45()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        cacheMap0.put((Map) cacheMap0, (Object) cacheMap0, (Object) null);
        cacheMap0.clear();
        assertEquals(1000, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test46()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        BiFunction<CacheMap, CacheMap, CacheMap> biFunction0 =
                (BiFunction<CacheMap, CacheMap, CacheMap>) mock(BiFunction.class, new ViolatedAssumptionAnswer());
        Object object0 = cacheMap0.merge(cacheMap0, cacheMap0, biFunction0);
        assertNotNull(object0);

        cacheMap0.clear();
        assertEquals(1000, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test47()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        cacheMap0.remove((Object) null);
        assertEquals(1000, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test48()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        Function<Object, Object> function0 = Function.identity();
        CacheMap cacheMap1 = (CacheMap)cacheMap0.computeIfAbsent(cacheMap0, function0);
        boolean boolean0 = cacheMap0.pin(cacheMap0);
        assertTrue(boolean0);

        CacheMap cacheMap2 = (CacheMap)cacheMap1.remove((Object) cacheMap0);
        assertNotNull(cacheMap2);
        assertEquals(1000, cacheMap2.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test49()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        boolean boolean0 = cacheMap0.pin(cacheMap0);
        assertFalse(boolean0);

        Object object0 = cacheMap0.remove((Object) cacheMap0);
        assertNull(object0);
        assertEquals(1000, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test50()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        Function<Object, Object> function0 = Function.identity();
        CacheMap cacheMap1 = new CacheMap(true);
        CacheMap cacheMap2 = (CacheMap)cacheMap0.computeIfAbsent(cacheMap1, function0);
        cacheMap2.putAll((Map) cacheMap0, false);
        assertEquals(1000, cacheMap2.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test51()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        boolean boolean0 = cacheMap0.pin(cacheMap0);
        assertFalse(boolean0);

        Object object0 = cacheMap0.put(cacheMap0, cacheMap0);
        assertNull(object0);
        assertEquals(1000, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test52()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        Function<Object, Object> function0 = Function.identity();
        CacheMap cacheMap1 = (CacheMap)cacheMap0.computeIfAbsent(cacheMap0, function0);
        boolean boolean0 = cacheMap0.pin(cacheMap0);
        assertTrue(boolean0);

        cacheMap0.putAll((Map) cacheMap1, true);
        assertEquals(1000, cacheMap1.getCacheSize());
        assertEquals(1000, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test53()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(true);
        Object object0 = new Object();
        cacheMap0.cacheMapOverflowRemoved((Object) null, object0);
        BiFunction<Object, Object, Object> biFunction0 = (BiFunction<Object, Object, Object>) mock(BiFunction.class, new ViolatedAssumptionAnswer());
        doReturn((Object) null).when(biFunction0).apply(any() , any());
        cacheMap0.computeIfPresent((Object) null, biFunction0);
        assertEquals(1000, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test54()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        Function<Object, Object> function0 = Function.identity();
        CacheMap cacheMap1 = (CacheMap)cacheMap0.computeIfAbsent(cacheMap0, function0);
        boolean boolean0 = cacheMap0.pin(cacheMap1);
        assertTrue(boolean0);

        boolean boolean1 = cacheMap0.unpin(cacheMap1);
        assertTrue(boolean1);
        assertEquals(1000, cacheMap1.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test55()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        Object object0 = new Object();
        boolean boolean0 = cacheMap0.unpin(object0);
        assertEquals(1000, cacheMap0.getCacheSize());
        assertFalse(boolean0);
    }

    @Test(timeout = 4000)
    public void test56()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        Function<Object, Object> function0 = Function.identity();
        CacheMap cacheMap1 = (CacheMap)cacheMap0.computeIfAbsent(cacheMap0, function0);
        boolean boolean0 = cacheMap1.pin(cacheMap0);
        assertTrue(boolean0);

        boolean boolean1 = cacheMap0.pin(cacheMap0);
        assertTrue(boolean1);
        assertEquals(1000, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test57()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        boolean boolean0 = cacheMap0.pin(cacheMap0);
        boolean boolean1 = cacheMap0.pin(cacheMap0);
        assertTrue(boolean1 == boolean0);
        assertEquals(1000, cacheMap0.getCacheSize());
        assertFalse(boolean1);
    }

    @Test(timeout = 4000)
    public void test58()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        int int0 = cacheMap0.getSoftReferenceSize();
        assertEquals(1000, cacheMap0.getCacheSize());
        assertEquals((-1), int0);
    }

    @Test(timeout = 4000)
    public void test59()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        cacheMap0.setSoftReferenceSize(643);
        int int0 = cacheMap0.getSoftReferenceSize();
        assertEquals(643, int0);
    }

    @Test(timeout = 4000)
    public void test60()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        cacheMap0.setSoftReferenceSize((-2155));
        assertEquals((-1), cacheMap0.getSoftReferenceSize());
        assertEquals(1000, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test61()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(true, (-3135));
        int int0 = cacheMap0.getCacheSize();
        assertEquals((-1), int0);
    }

    @Test(timeout = 4000)
    public void test62()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(false, 0);
        int int0 = cacheMap0.getCacheSize();
        assertEquals(0, int0);
    }

    @Test(timeout = 4000)
    public void test63()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(false);
        assertEquals(1000, cacheMap0.getCacheSize());

        cacheMap0.setCacheSize((-3366));
        assertEquals((-1), cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test64()  throws Throwable  {
        CacheMap cacheMap0 = null;
        try {
            cacheMap0 = new CacheMap(false, (-792), (-792), 23, 23);
            fail("Expecting exception: IllegalArgumentException");

        } catch(IllegalArgumentException e) {
            //
            // Illegal Load factor: 23.0
            //
            verifyException("org.apache.openjpa.lib.util.concurrent.ConcurrentReferenceHashMap", e);
        }
    }

    @Test(timeout = 4000)
    public void test65()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        cacheMap0.putIfAbsent(cacheMap0, (Object) null);
        // Undeclared exception!
        try {
            cacheMap0.setCacheSize(0);
            fail("Expecting exception: IllegalArgumentException");

        } catch(IllegalArgumentException e) {
            //
            // Null references not supported
            //
            verifyException("org.apache.openjpa.lib.util.concurrent.ConcurrentReferenceHashMap", e);
        }
    }

    @Test(timeout = 4000)
    public void test66()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(false, 0);
        cacheMap0.toString();
        assertEquals(0, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test67()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        cacheMap0.getPinnedKeys();
        assertFalse(cacheMap0.isLRU());
        assertEquals(1000, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test68()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        boolean boolean0 = cacheMap0.isEmpty();
        assertFalse(cacheMap0.isLRU());
        assertTrue(boolean0);
        assertEquals(1000, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test69()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(true);
        boolean boolean0 = cacheMap0.isLRU();
        assertTrue(boolean0);
        assertEquals(1000, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test70()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(false);
        cacheMap0.softMapValueExpired((Object) null);
        assertFalse(cacheMap0.isLRU());
        assertEquals(1000, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test71()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap();
        Set set0 = cacheMap0.keySet();
        assertFalse(cacheMap0.isLRU());
        assertTrue(set0.isEmpty());
        assertEquals(1000, cacheMap0.getCacheSize());
    }

    @Test(timeout = 4000)
    public void test72()  throws Throwable  {
        CacheMap cacheMap0 = new CacheMap(true);
        cacheMap0.putAll((Map) cacheMap0);
        assertTrue(cacheMap0.isLRU());
        assertEquals(1000, cacheMap0.getCacheSize());
    }
}