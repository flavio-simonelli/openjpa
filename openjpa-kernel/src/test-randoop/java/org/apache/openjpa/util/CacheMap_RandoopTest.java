package org.apache.openjpa.util;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CacheMap_RandoopTest {

    public static boolean debug = false;

    public void assertBooleanArrayEquals(boolean[] expectedArray, boolean[] actualArray) {
        if (expectedArray.length != actualArray.length) {
            throw new AssertionError("Array lengths differ: " + expectedArray.length + " != " + actualArray.length);
        }
        for (int i = 0; i < expectedArray.length; i++) {
            if (expectedArray[i] != actualArray[i]) {
                throw new AssertionError("Arrays differ at index " + i + ": " + expectedArray[i] + " != " + actualArray[i]);
            }
        }
    }

    @Test
    public void test01() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test01");
        org.apache.openjpa.util.CacheMap cacheMap2 = new org.apache.openjpa.util.CacheMap(false, (int) '#');
        // The following exception was thrown during execution in test generation
        try {
            cacheMap2.writeUnlock();
            org.junit.Assert.fail("Expected exception of type java.lang.IllegalMonitorStateException; message: null");
        } catch (java.lang.IllegalMonitorStateException e) {
            // Expected exception.
        }
    }

    @Test
    public void test02() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test02");
        org.apache.openjpa.util.CacheMap cacheMap2 = new org.apache.openjpa.util.CacheMap(false, (int) '#');
        java.lang.Object obj4 = cacheMap2.get((java.lang.Object) (byte) -1);
        java.util.Set set5 = cacheMap2.getPinnedKeys();
        java.util.Collection collection6 = cacheMap2.values();
        org.junit.Assert.assertNull(obj4);
        org.junit.Assert.assertNotNull(set5);
        org.junit.Assert.assertNotNull(collection6);
    }

    @Test
    public void test03() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test03");
        // The following exception was thrown during execution in test generation
        try {
            org.apache.openjpa.util.CacheMap cacheMap4 = new org.apache.openjpa.util.CacheMap(false, 0, (int) '4', 0.0f);
            org.junit.Assert.fail("Expected exception of type java.lang.IllegalArgumentException; message: Illegal Load factor: 0.0");
        } catch (java.lang.IllegalArgumentException e) {
            // Expected exception.
        }
    }

    @Test
    public void test04() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test04");
        org.apache.openjpa.util.CacheMap cacheMap2 = new org.apache.openjpa.util.CacheMap(false, (int) '#');
        boolean boolean3 = cacheMap2.isEmpty();
        java.util.Collection collection4 = cacheMap2.values();
        org.junit.Assert.assertTrue("'" + boolean3 + "' != '" + true + "'", boolean3 == true);
        org.junit.Assert.assertNotNull(collection4);
    }

    @Test
    public void test05() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test05");
        org.apache.openjpa.util.CacheMap cacheMap2 = new org.apache.openjpa.util.CacheMap(false, (int) '#');
        java.lang.Object obj4 = cacheMap2.get((java.lang.Object) (byte) -1);
        cacheMap2.setSoftReferenceSize(0);
        // The following exception was thrown during execution in test generation
        try {
            cacheMap2.readUnlock();
            org.junit.Assert.fail("Expected exception of type java.lang.IllegalMonitorStateException");
        } catch (java.lang.IllegalMonitorStateException e) {
            // Expected exception.
        }
        org.junit.Assert.assertNull(obj4);
    }

    @Test
    public void test06() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test06");
        org.apache.openjpa.util.CacheMap cacheMap2 = new org.apache.openjpa.util.CacheMap(false, (int) ' ');
        cacheMap2.clear();
    }

    @Test
    public void test07() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test07");
        org.apache.openjpa.util.CacheMap cacheMap1 = new org.apache.openjpa.util.CacheMap(false);
    }

    @Test
    public void test08() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test08");
        org.apache.openjpa.util.CacheMap cacheMap2 = new org.apache.openjpa.util.CacheMap(false, (int) '#');
        boolean boolean3 = cacheMap2.isEmpty();
        org.apache.openjpa.util.CacheMap cacheMap7 = new org.apache.openjpa.util.CacheMap(false, (int) '#');
        java.lang.Object obj9 = cacheMap7.get((java.lang.Object) (byte) -1);
        java.util.Set set10 = cacheMap7.getPinnedKeys();
        cacheMap7.setSoftReferenceSize(0);
        cacheMap7.readLock();
        java.lang.Object obj14 = cacheMap2.put((java.lang.Object) 1, (java.lang.Object) cacheMap7);
        java.util.Collection collection15 = cacheMap2.values();
        org.junit.Assert.assertTrue("'" + boolean3 + "' != '" + true + "'", boolean3 == true);
        org.junit.Assert.assertNull(obj9);
        org.junit.Assert.assertNotNull(set10);
        org.junit.Assert.assertNull(obj14);
        org.junit.Assert.assertNotNull(collection15);
    }

    @Test
    public void test09() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test09");
        org.apache.openjpa.util.CacheMap cacheMap2 = new org.apache.openjpa.util.CacheMap(false, (int) '#');
        org.apache.openjpa.util.CacheMap cacheMap5 = new org.apache.openjpa.util.CacheMap(false, (int) '#');
        java.lang.Object obj7 = cacheMap5.get((java.lang.Object) (byte) -1);
        java.util.Set set8 = cacheMap5.getPinnedKeys();
        cacheMap5.setSoftReferenceSize(0);
        java.util.Set set11 = cacheMap5.entrySet();
        boolean boolean13 = cacheMap5.pin((java.lang.Object) (short) 100);
        boolean boolean14 = cacheMap5.isEmpty();
        java.lang.Object obj16 = cacheMap5.remove((java.lang.Object) (short) 100);
        cacheMap2.putAll((java.util.Map) cacheMap5, false);
        org.junit.Assert.assertNull(obj7);
        org.junit.Assert.assertNotNull(set8);
        org.junit.Assert.assertNotNull(set11);
        org.junit.Assert.assertTrue("'" + boolean13 + "' != '" + false + "'", boolean13 == false);
        org.junit.Assert.assertTrue("'" + boolean14 + "' != '" + true + "'", boolean14 == true);
        org.junit.Assert.assertNull(obj16);
    }

    @Test
    public void test10() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test10");
        org.apache.openjpa.util.CacheMap cacheMap2 = new org.apache.openjpa.util.CacheMap(false, (int) '#');
        org.apache.openjpa.util.CacheMap cacheMap5 = new org.apache.openjpa.util.CacheMap(false, (int) (short) -1);
        cacheMap5.setSoftReferenceSize((int) (short) 0);
        cacheMap2.putAll((java.util.Map) cacheMap5, true);
    }

    @Test
    public void test11() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test11");
        org.apache.openjpa.util.CacheMap cacheMap2 = new org.apache.openjpa.util.CacheMap(false, (int) '#');
        java.lang.Object obj4 = cacheMap2.get((java.lang.Object) (byte) -1);
        java.util.Set set5 = cacheMap2.getPinnedKeys();
        cacheMap2.setSoftReferenceSize(0);
        java.util.Set set8 = cacheMap2.entrySet();
        java.lang.Object obj10 = cacheMap2.get((java.lang.Object) (-1.0d));
        org.junit.Assert.assertNull(obj4);
        org.junit.Assert.assertNotNull(set5);
        org.junit.Assert.assertNotNull(set8);
        org.junit.Assert.assertNull(obj10);
    }

    @Test
    public void test12() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test12");
        org.apache.openjpa.util.CacheMap cacheMap2 = new org.apache.openjpa.util.CacheMap(false, (int) '#');
        java.lang.Object obj4 = cacheMap2.get((java.lang.Object) (byte) -1);
        java.util.Set set5 = cacheMap2.entrySet();
        org.junit.Assert.assertNull(obj4);
        org.junit.Assert.assertNotNull(set5);
    }

    @Test
    public void test13() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test13");
        org.apache.openjpa.util.CacheMap cacheMap0 = new org.apache.openjpa.util.CacheMap();
    }

    @Test
    public void test14() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test14");
        org.apache.openjpa.util.CacheMap cacheMap2 = new org.apache.openjpa.util.CacheMap(false, (int) '#');
        java.lang.Object obj4 = cacheMap2.get((java.lang.Object) (byte) -1);
        java.util.Set set5 = cacheMap2.getPinnedKeys();
        org.apache.openjpa.util.CacheMap cacheMap9 = new org.apache.openjpa.util.CacheMap(false, (int) '#');
        java.lang.Object obj11 = cacheMap9.get((java.lang.Object) (byte) -1);
        java.util.Set set12 = cacheMap9.getPinnedKeys();
        cacheMap9.setSoftReferenceSize(0);
        java.util.Set set15 = cacheMap9.entrySet();
        cacheMap9.setSoftReferenceSize(0);
        org.apache.openjpa.util.CacheMap cacheMap20 = new org.apache.openjpa.util.CacheMap(false, (int) '#');
        java.lang.Object obj22 = cacheMap20.get((java.lang.Object) (byte) -1);
        java.util.Set set23 = cacheMap20.getPinnedKeys();
        cacheMap20.setSoftReferenceSize(0);
        java.util.Set set26 = cacheMap20.entrySet();
        java.lang.Object obj27 = cacheMap9.remove((java.lang.Object) set26);
        java.lang.Object obj28 = cacheMap2.put((java.lang.Object) 100L, (java.lang.Object) cacheMap9);
        org.junit.Assert.assertNull(obj4);
        org.junit.Assert.assertNotNull(set5);
        org.junit.Assert.assertNull(obj11);
        org.junit.Assert.assertNotNull(set12);
        org.junit.Assert.assertNotNull(set15);
        org.junit.Assert.assertNull(obj22);
        org.junit.Assert.assertNotNull(set23);
        org.junit.Assert.assertNotNull(set26);
        org.junit.Assert.assertNull(obj27);
        org.junit.Assert.assertNull(obj28);
    }

    @Test
    public void test15() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test15");
        org.apache.openjpa.util.CacheMap cacheMap2 = new org.apache.openjpa.util.CacheMap(false, (int) '#');
        java.lang.Object obj4 = cacheMap2.get((java.lang.Object) (byte) -1);
        org.apache.openjpa.util.CacheMap cacheMap7 = new org.apache.openjpa.util.CacheMap(false, (int) ' ');
        java.util.Set set8 = cacheMap7.getPinnedKeys();
        cacheMap2.putAll((java.util.Map) cacheMap7);
        org.apache.openjpa.util.CacheMap cacheMap12 = new org.apache.openjpa.util.CacheMap(false, 100);
        cacheMap2.putAll((java.util.Map) cacheMap12, true);
        org.junit.Assert.assertNull(obj4);
        org.junit.Assert.assertNotNull(set8);
    }

    @Test
    public void test16() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test16");
        org.apache.openjpa.util.CacheMap cacheMap2 = new org.apache.openjpa.util.CacheMap(false, (int) '#');
        int int3 = cacheMap2.getCacheSize();
        boolean boolean5 = cacheMap2.containsKey((java.lang.Object) (short) 0);
        org.junit.Assert.assertTrue("'" + int3 + "' != '" + 35 + "'", int3 == 35);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + false + "'", boolean5 == false);
    }

    @Test
    public void test17() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test17");
        org.apache.openjpa.util.CacheMap cacheMap2 = new org.apache.openjpa.util.CacheMap(false, (int) '#');
        java.lang.Object obj4 = cacheMap2.get((java.lang.Object) (byte) -1);
        java.util.Set set5 = cacheMap2.getPinnedKeys();
        cacheMap2.setSoftReferenceSize(0);
        java.util.Set set8 = cacheMap2.getPinnedKeys();
        java.util.Set set9 = cacheMap2.keySet();
        int int10 = cacheMap2.getSoftReferenceSize();
        org.apache.openjpa.util.CacheMap cacheMap13 = new org.apache.openjpa.util.CacheMap(false, (int) '#');
        java.lang.Object obj15 = cacheMap13.get((java.lang.Object) (byte) -1);
        java.util.Set set16 = cacheMap13.getPinnedKeys();
        cacheMap13.setSoftReferenceSize(0);
        java.util.Set set19 = cacheMap13.entrySet();
        boolean boolean21 = cacheMap13.pin((java.lang.Object) (short) 100);
        boolean boolean22 = cacheMap13.isEmpty();
        boolean boolean23 = cacheMap2.unpin((java.lang.Object) boolean22);
        java.util.Collection collection24 = cacheMap2.values();
        org.junit.Assert.assertNull(obj4);
        org.junit.Assert.assertNotNull(set5);
        org.junit.Assert.assertNotNull(set8);
        org.junit.Assert.assertNotNull(set9);
        org.junit.Assert.assertTrue("'" + int10 + "' != '" + 0 + "'", int10 == 0);
        org.junit.Assert.assertNull(obj15);
        org.junit.Assert.assertNotNull(set16);
        org.junit.Assert.assertNotNull(set19);
        org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + false + "'", boolean21 == false);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + true + "'", boolean22 == true);
        org.junit.Assert.assertTrue("'" + boolean23 + "' != '" + false + "'", boolean23 == false);
        org.junit.Assert.assertNotNull(collection24);
    }

    @Test
    public void test18() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test18");
        org.apache.openjpa.util.CacheMap cacheMap2 = new org.apache.openjpa.util.CacheMap(false, (int) '#');
        java.lang.Object obj4 = cacheMap2.get((java.lang.Object) (byte) -1);
        java.util.Set set5 = cacheMap2.getPinnedKeys();
        cacheMap2.setSoftReferenceSize(0);
        java.util.Set set8 = cacheMap2.getPinnedKeys();
        java.util.Set set9 = cacheMap2.keySet();
        int int10 = cacheMap2.getSoftReferenceSize();
        org.apache.openjpa.util.CacheMap cacheMap13 = new org.apache.openjpa.util.CacheMap(false, (int) '#');
        java.lang.Object obj15 = cacheMap13.get((java.lang.Object) (byte) -1);
        java.util.Set set16 = cacheMap13.getPinnedKeys();
        cacheMap13.setSoftReferenceSize(0);
        java.util.Set set19 = cacheMap13.entrySet();
        boolean boolean21 = cacheMap13.pin((java.lang.Object) (short) 100);
        boolean boolean22 = cacheMap13.isEmpty();
        boolean boolean23 = cacheMap2.unpin((java.lang.Object) boolean22);
        org.apache.openjpa.util.CacheMap cacheMap26 = new org.apache.openjpa.util.CacheMap(false, (int) '#');
        java.lang.Object obj28 = cacheMap26.get((java.lang.Object) (byte) -1);
        java.util.Set set29 = cacheMap26.getPinnedKeys();
        java.lang.Object obj31 = cacheMap26.get((java.lang.Object) (short) 1);
        java.lang.Object obj32 = cacheMap2.get((java.lang.Object) (short) 1);
        org.junit.Assert.assertNull(obj4);
        org.junit.Assert.assertNotNull(set5);
        org.junit.Assert.assertNotNull(set8);
        org.junit.Assert.assertNotNull(set9);
        org.junit.Assert.assertTrue("'" + int10 + "' != '" + 0 + "'", int10 == 0);
        org.junit.Assert.assertNull(obj15);
        org.junit.Assert.assertNotNull(set16);
        org.junit.Assert.assertNotNull(set19);
        org.junit.Assert.assertTrue("'" + boolean21 + "' != '" + false + "'", boolean21 == false);
        org.junit.Assert.assertTrue("'" + boolean22 + "' != '" + true + "'", boolean22 == true);
        org.junit.Assert.assertTrue("'" + boolean23 + "' != '" + false + "'", boolean23 == false);
        org.junit.Assert.assertNull(obj28);
        org.junit.Assert.assertNotNull(set29);
        org.junit.Assert.assertNull(obj31);
        org.junit.Assert.assertNull(obj32);
    }

    @Test
    public void test19() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test19");
        org.apache.openjpa.util.CacheMap cacheMap2 = new org.apache.openjpa.util.CacheMap(false, (int) '#');
        boolean boolean3 = cacheMap2.isEmpty();
        org.apache.openjpa.util.CacheMap cacheMap7 = new org.apache.openjpa.util.CacheMap(false, (int) '#');
        java.lang.Object obj9 = cacheMap7.get((java.lang.Object) (byte) -1);
        java.util.Set set10 = cacheMap7.getPinnedKeys();
        cacheMap7.setSoftReferenceSize(0);
        cacheMap7.readLock();
        java.lang.Object obj14 = cacheMap2.put((java.lang.Object) 1, (java.lang.Object) cacheMap7);
        boolean boolean15 = cacheMap2.isEmpty();
        org.apache.openjpa.util.CacheMap cacheMap18 = new org.apache.openjpa.util.CacheMap(false, (int) '#');
        java.lang.Object obj20 = cacheMap18.get((java.lang.Object) (byte) -1);
        java.util.Set set21 = cacheMap18.getPinnedKeys();
        cacheMap18.setSoftReferenceSize(0);
        java.util.Set set24 = cacheMap18.entrySet();
        boolean boolean26 = cacheMap18.pin((java.lang.Object) (short) 100);
        java.lang.Object obj28 = cacheMap2.put((java.lang.Object) boolean26, (java.lang.Object) (-1.0f));
        org.junit.Assert.assertTrue("'" + boolean3 + "' != '" + true + "'", boolean3 == true);
        org.junit.Assert.assertNull(obj9);
        org.junit.Assert.assertNotNull(set10);
        org.junit.Assert.assertNull(obj14);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", boolean15 == false);
        org.junit.Assert.assertNull(obj20);
        org.junit.Assert.assertNotNull(set21);
        org.junit.Assert.assertNotNull(set24);
        org.junit.Assert.assertTrue("'" + boolean26 + "' != '" + false + "'", boolean26 == false);
        org.junit.Assert.assertNull(obj28);
    }

    @Test
    public void test20() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test20");
        org.apache.openjpa.util.CacheMap cacheMap2 = new org.apache.openjpa.util.CacheMap(false, (int) ' ');
        org.apache.openjpa.util.CacheMap cacheMap5 = new org.apache.openjpa.util.CacheMap(false, (int) '#');
        org.apache.openjpa.util.CacheMap cacheMap8 = new org.apache.openjpa.util.CacheMap(false, (int) '#');
        java.lang.Object obj10 = cacheMap8.get((java.lang.Object) (byte) -1);
        java.util.Set set11 = cacheMap8.getPinnedKeys();
        cacheMap8.setSoftReferenceSize(0);
        cacheMap8.readLock();
        boolean boolean15 = cacheMap5.containsValue((java.lang.Object) cacheMap8);
        cacheMap2.putAll((java.util.Map) cacheMap8, false);
        org.junit.Assert.assertNull(obj10);
        org.junit.Assert.assertNotNull(set11);
        org.junit.Assert.assertTrue("'" + boolean15 + "' != '" + false + "'", boolean15 == false);
    }

    @Test
    public void test21() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test21");
        org.apache.openjpa.util.CacheMap cacheMap2 = new org.apache.openjpa.util.CacheMap(false, (int) '#');
        java.lang.Object obj4 = cacheMap2.get((java.lang.Object) (byte) -1);
        java.util.Set set5 = cacheMap2.getPinnedKeys();
        cacheMap2.setSoftReferenceSize(0);
        java.util.Set set8 = cacheMap2.getPinnedKeys();
        java.util.Set set9 = cacheMap2.keySet();
        int int10 = cacheMap2.size();
        cacheMap2.clear();
        org.junit.Assert.assertNull(obj4);
        org.junit.Assert.assertNotNull(set5);
        org.junit.Assert.assertNotNull(set8);
        org.junit.Assert.assertNotNull(set9);
        org.junit.Assert.assertTrue("'" + int10 + "' != '" + 0 + "'", int10 == 0);
    }
}
