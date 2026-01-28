package org.apache.openjpa.util;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProxyManagerImpl_RandoopTest {

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
        org.apache.openjpa.util.ProxyManagerImpl proxyManagerImpl0 = new org.apache.openjpa.util.ProxyManagerImpl();
        // The following exception was thrown during execution in test generation
        try {
            java.lang.Object obj2 = proxyManagerImpl0.copyArray((java.lang.Object) 100.0f);
            org.junit.Assert.fail("Expected exception");
        } catch (org.apache.openjpa.util.UnsupportedException e) {
            // Expected exception.
        }
    }

    @Test
    public void test02() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test02");
        org.apache.openjpa.util.ProxyManagerImpl proxyManagerImpl0 = new org.apache.openjpa.util.ProxyManagerImpl();
        java.util.Date date1 = null;
        java.util.Date date2 = proxyManagerImpl0.copyDate(date1);
        org.apache.xbean.asm9.Type type3 = org.apache.openjpa.util.ProxyManagerImpl.TYPE_OBJECT;
        java.lang.Class<?> wildcardClass4 = type3.getClass();
        // The following exception was thrown during execution in test generation
        try {
            org.apache.openjpa.util.Proxy proxy5 = proxyManagerImpl0.newDateProxy((java.lang.Class) wildcardClass4);
            org.junit.Assert.fail("Expected exception");
        } catch (org.apache.openjpa.util.UnsupportedException e) {
            // Expected exception.
        }
        org.junit.Assert.assertNull(date2);
        org.junit.Assert.assertNotNull(type3);
        org.junit.Assert.assertNotNull(wildcardClass4);
    }

    @Test
    public void test03() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test03");
        org.apache.openjpa.util.ProxyManagerImpl proxyManagerImpl0 = new org.apache.openjpa.util.ProxyManagerImpl();
        java.util.Date date1 = null;
        java.util.Date date2 = proxyManagerImpl0.copyDate(date1);
        java.util.Date date3 = null;
        java.util.Date date4 = proxyManagerImpl0.copyDate(date3);
        proxyManagerImpl0.setUnproxyable("");
        org.junit.Assert.assertNull(date2);
        org.junit.Assert.assertNull(date4);
    }

    @Test
    public void test04() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test04");
        org.apache.openjpa.util.ProxyManagerImpl proxyManagerImpl0 = new org.apache.openjpa.util.ProxyManagerImpl();
        java.util.Date date1 = null;
        java.util.Date date2 = proxyManagerImpl0.copyDate(date1);
        java.util.Date date3 = null;
        java.util.Date date4 = proxyManagerImpl0.copyDate(date3);
        org.apache.xbean.asm9.Type type5 = org.apache.openjpa.util.ProxyManagerImpl.TYPE_OBJECT;
        java.lang.Class<?> wildcardClass6 = type5.getClass();
        java.util.TimeZone timeZone7 = null;
        // The following exception was thrown during execution in test generation
        try {
            org.apache.openjpa.util.Proxy proxy8 = proxyManagerImpl0.newCalendarProxy((java.lang.Class) wildcardClass6, timeZone7);
            org.junit.Assert.fail("Expected exception");
        } catch (org.apache.openjpa.util.UnsupportedException e) {
            // Expected exception.
        }
        org.junit.Assert.assertNull(date2);
        org.junit.Assert.assertNull(date4);
        org.junit.Assert.assertNotNull(type5);
        org.junit.Assert.assertNotNull(wildcardClass6);
    }

    @Test
    public void test05() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test05");
        org.apache.openjpa.util.ProxyManagerImpl proxyManagerImpl0 = new org.apache.openjpa.util.ProxyManagerImpl();
        org.apache.xbean.asm9.Type type1 = org.apache.openjpa.util.ProxyManagerImpl.TYPE_OBJECT;
        java.lang.Class<?> wildcardClass2 = type1.getClass();
        // The following exception was thrown during execution in test generation
        try {
            org.apache.openjpa.util.Proxy proxy3 = proxyManagerImpl0.newDateProxy((java.lang.Class) wildcardClass2);
            org.junit.Assert.fail("Expected exception");
        } catch (org.apache.openjpa.util.UnsupportedException e) {
            // Expected exception.
        }
        org.junit.Assert.assertNotNull(type1);
        org.junit.Assert.assertNotNull(wildcardClass2);
    }

    @Test
    public void test06() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test06");
        org.apache.openjpa.util.ProxyManagerImpl proxyManagerImpl0 = new org.apache.openjpa.util.ProxyManagerImpl();
        java.util.Date date1 = null;
        java.util.Date date2 = proxyManagerImpl0.copyDate(date1);
        proxyManagerImpl0.setTrackChanges(false);
        org.apache.xbean.asm9.Type type5 = org.apache.openjpa.util.ProxyManagerImpl.TYPE_OBJECT;
        java.lang.Class<?> wildcardClass6 = type5.getClass();
        java.util.TimeZone timeZone7 = null;
        // The following exception was thrown during execution in test generation
        try {
            org.apache.openjpa.util.Proxy proxy8 = proxyManagerImpl0.newCalendarProxy((java.lang.Class) wildcardClass6, timeZone7);
            org.junit.Assert.fail("Expected exception");
        } catch (org.apache.openjpa.util.UnsupportedException e) {
            // Expected exception.
        }
        org.junit.Assert.assertNull(date2);
        org.junit.Assert.assertNotNull(type5);
        org.junit.Assert.assertNotNull(wildcardClass6);
    }

    @Test
    public void test07() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test07");
        org.apache.openjpa.util.ProxyManagerImpl proxyManagerImpl0 = new org.apache.openjpa.util.ProxyManagerImpl();
        java.util.Date date1 = null;
        java.util.Date date2 = proxyManagerImpl0.copyDate(date1);
        proxyManagerImpl0.setTrackChanges(false);
        org.apache.xbean.asm9.Type type5 = org.apache.openjpa.util.ProxyManagerImpl.TYPE_OBJECT;
        java.lang.Class<?> wildcardClass6 = type5.getClass();
        org.apache.xbean.asm9.Type type7 = org.apache.openjpa.util.ProxyManagerImpl.TYPE_OBJECT;
        java.lang.Class<?> wildcardClass8 = type7.getClass();
        org.apache.xbean.asm9.Type type9 = org.apache.openjpa.util.ProxyManagerImpl.TYPE_OBJECT;
        java.lang.Class<?> wildcardClass10 = type9.getClass();
        java.util.Comparator comparator11 = null;
        // The following exception was thrown during execution in test generation
        try {
            org.apache.openjpa.util.Proxy proxy13 = proxyManagerImpl0.newMapProxy(
                    (java.lang.Class) wildcardClass6,
                    (java.lang.Class) wildcardClass8,
                    (java.lang.Class) wildcardClass10,
                    comparator11,
                    true
            );

            org.junit.Assert.fail("Expected exception");
        } catch (org.apache.openjpa.util.UnsupportedException e) {
            // Expected exception.
        }
        org.junit.Assert.assertNull(date2);
        org.junit.Assert.assertNotNull(type5);
        org.junit.Assert.assertNotNull(wildcardClass6);
        org.junit.Assert.assertNotNull(type7);
        org.junit.Assert.assertNotNull(wildcardClass8);
        org.junit.Assert.assertNotNull(type9);
        org.junit.Assert.assertNotNull(wildcardClass10);
    }

    @Test
    public void test08() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test08");
        org.apache.openjpa.util.ProxyManagerImpl proxyManagerImpl0 = new org.apache.openjpa.util.ProxyManagerImpl();
        java.util.Date date1 = null;
        java.util.Date date2 = proxyManagerImpl0.copyDate(date1);
        java.util.Map map3 = null;
        java.util.Map map4 = proxyManagerImpl0.copyMap(map3);
        org.apache.xbean.asm9.Type type5 = org.apache.openjpa.util.ProxyManagerImpl.TYPE_OBJECT;
        java.lang.Class<?> wildcardClass6 = type5.getClass();
        java.util.TimeZone timeZone7 = null;
        // The following exception was thrown during execution in test generation
        try {
            org.apache.openjpa.util.Proxy proxy8 = proxyManagerImpl0.newCalendarProxy((java.lang.Class) wildcardClass6, timeZone7);
            org.junit.Assert.fail("Expected exception");
        } catch (org.apache.openjpa.util.UnsupportedException e) {
            // Expected exception.
        }
        org.junit.Assert.assertNull(date2);
        org.junit.Assert.assertNull(map4);
        org.junit.Assert.assertNotNull(type5);
        org.junit.Assert.assertNotNull(wildcardClass6);
    }

    @Test
    public void test09() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test09");
        org.apache.openjpa.util.ProxyManagerImpl proxyManagerImpl0 = new org.apache.openjpa.util.ProxyManagerImpl();
        java.util.Date date1 = null;
        java.util.Date date2 = proxyManagerImpl0.copyDate(date1);
        java.util.Map map3 = null;
        java.util.Map map4 = proxyManagerImpl0.copyMap(map3);
        org.apache.openjpa.util.Proxy proxy7 = proxyManagerImpl0.newCustomProxy((java.lang.Object) (byte) 10, false);
        org.apache.xbean.asm9.Type type8 = org.apache.openjpa.util.ProxyManagerImpl.TYPE_OBJECT;
        java.lang.Class<?> wildcardClass9 = type8.getClass();
        // The following exception was thrown during execution in test generation
        try {
            org.apache.openjpa.util.Proxy proxy10 = proxyManagerImpl0.newDateProxy((java.lang.Class) wildcardClass9);
            org.junit.Assert.fail("Expected exception");
        } catch (org.apache.openjpa.util.UnsupportedException e) {
            // Expected exception.
        }
        org.junit.Assert.assertNull(date2);
        org.junit.Assert.assertNull(map4);
        org.junit.Assert.assertNull(proxy7);
        org.junit.Assert.assertNotNull(type8);
        org.junit.Assert.assertNotNull(wildcardClass9);
    }

    @Test
    public void test10() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test10");
        org.apache.openjpa.util.ProxyManagerImpl proxyManagerImpl0 = new org.apache.openjpa.util.ProxyManagerImpl();
        java.util.Date date1 = null;
        java.util.Date date2 = proxyManagerImpl0.copyDate(date1);
        proxyManagerImpl0.setDelayCollectionLoading(true);
        boolean boolean5 = proxyManagerImpl0.getTrackChanges();
        java.lang.Object obj7 = proxyManagerImpl0.copyCustom((java.lang.Object) (byte) 100);
        proxyManagerImpl0.setUnproxyable("hi!");
        org.junit.Assert.assertNull(date2);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5 == true);
        org.junit.Assert.assertNull(obj7);
    }

    @Test
    public void test11() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test11");
        org.apache.openjpa.util.ProxyManagerImpl proxyManagerImpl0 = new org.apache.openjpa.util.ProxyManagerImpl();
        java.util.Date date1 = null;
        java.util.Date date2 = proxyManagerImpl0.copyDate(date1);
        java.util.Date date3 = null;
        java.util.Date date4 = proxyManagerImpl0.copyDate(date3);
        org.apache.openjpa.util.ProxyManagerImpl proxyManagerImpl5 = new org.apache.openjpa.util.ProxyManagerImpl();
        java.util.Date date6 = null;
        java.util.Date date7 = proxyManagerImpl5.copyDate(date6);
        proxyManagerImpl5.setTrackChanges(false);
        java.lang.Class<?> wildcardClass10 = proxyManagerImpl5.getClass();
        org.apache.xbean.asm9.Type type11 = org.apache.openjpa.util.ProxyManagerImpl.TYPE_OBJECT;
        java.lang.Class<?> wildcardClass12 = type11.getClass();
        java.util.Comparator comparator13 = null;
        org.apache.openjpa.util.Proxy proxy15 = proxyManagerImpl0.newCollectionProxy(
                (java.lang.Class) wildcardClass10,
                (java.lang.Class) wildcardClass12,
                comparator13,
                false
        );

        org.junit.Assert.assertNull(date2);
        org.junit.Assert.assertNull(date4);
        org.junit.Assert.assertNull(date7);
        org.junit.Assert.assertNotNull(wildcardClass10);
        org.junit.Assert.assertNotNull(type11);
        org.junit.Assert.assertNotNull(wildcardClass12);
        org.junit.Assert.assertNotNull(proxy15);
    }

    @Test
    public void test12() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test12");
        org.apache.openjpa.util.ProxyManagerImpl proxyManagerImpl0 = new org.apache.openjpa.util.ProxyManagerImpl();
        proxyManagerImpl0.setDelayCollectionLoading(false);
    }

    @Test
    public void test13() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test13");
        org.apache.openjpa.util.ProxyManagerImpl proxyManagerImpl0 = new org.apache.openjpa.util.ProxyManagerImpl();
        java.util.Date date1 = null;
        java.util.Date date2 = proxyManagerImpl0.copyDate(date1);
        java.util.Map map3 = null;
        java.util.Map map4 = proxyManagerImpl0.copyMap(map3);
        org.apache.openjpa.util.Proxy proxy7 = proxyManagerImpl0.newCustomProxy((java.lang.Object) (byte) 10, false);
        proxyManagerImpl0.setTrackChanges(true);
        proxyManagerImpl0.setDelayCollectionLoading(false);
        org.junit.Assert.assertNull(date2);
        org.junit.Assert.assertNull(map4);
        org.junit.Assert.assertNull(proxy7);
    }

    @Test
    public void test14() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test14");
        org.apache.openjpa.util.ProxyManagerImpl proxyManagerImpl0 = new org.apache.openjpa.util.ProxyManagerImpl();
        java.util.Date date1 = null;
        java.util.Date date2 = proxyManagerImpl0.copyDate(date1);
        proxyManagerImpl0.setDelayCollectionLoading(true);
        boolean boolean5 = proxyManagerImpl0.getTrackChanges();
        java.lang.Object obj7 = proxyManagerImpl0.copyCustom((java.lang.Object) (byte) 100);
        java.lang.Class class8 = null;
        // The following exception was thrown during execution in test generation
        try {
            org.apache.openjpa.util.Proxy proxy9 = proxyManagerImpl0.newDateProxy(class8);
            org.junit.Assert.fail("Expected exception of type java.lang.NullPointerException; message: null");
        } catch (java.lang.NullPointerException e) {
            // Expected exception.
        }
        org.junit.Assert.assertNull(date2);
        org.junit.Assert.assertTrue("'" + boolean5 + "' != '" + true + "'", boolean5 == true);
        org.junit.Assert.assertNull(obj7);
    }

    @Test
    public void test15() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test15");
        org.apache.openjpa.util.ProxyManagerImpl proxyManagerImpl0 = new org.apache.openjpa.util.ProxyManagerImpl();
        java.util.Date date1 = null;
        java.util.Date date2 = proxyManagerImpl0.copyDate(date1);
        java.util.Map map3 = null;
        java.util.Map map4 = proxyManagerImpl0.copyMap(map3);
        proxyManagerImpl0.setAssertAllowedType(false);
        org.junit.Assert.assertNull(date2);
        org.junit.Assert.assertNull(map4);
    }

    @Test
    public void test16() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test16");
        org.apache.openjpa.util.ProxyManagerImpl proxyManagerImpl0 = new org.apache.openjpa.util.ProxyManagerImpl();
        java.util.Date date1 = null;
        java.util.Date date2 = proxyManagerImpl0.copyDate(date1);
        proxyManagerImpl0.setTrackChanges(false);
        java.util.Collection collection5 = proxyManagerImpl0.getUnproxyable();
        org.apache.openjpa.util.Proxy proxy8 = proxyManagerImpl0.newCustomProxy((java.lang.Object) 10.0d, false);
        org.junit.Assert.assertNull(date2);
        org.junit.Assert.assertNotNull(collection5);
        org.junit.Assert.assertNull(proxy8);
    }

    @Test
    public void test17() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test17");
        org.apache.openjpa.util.ProxyManagerImpl proxyManagerImpl0 = new org.apache.openjpa.util.ProxyManagerImpl();
        java.util.Date date1 = null;
        java.util.Date date2 = proxyManagerImpl0.copyDate(date1);
        java.util.Map map3 = null;
        java.util.Map map4 = proxyManagerImpl0.copyMap(map3);
        org.apache.openjpa.util.Proxy proxy7 = proxyManagerImpl0.newCustomProxy((java.lang.Object) (byte) 10, false);
        proxyManagerImpl0.setTrackChanges(true);
        boolean boolean10 = proxyManagerImpl0.getDelayCollectionLoading();
        java.util.Collection collection11 = proxyManagerImpl0.getUnproxyable();
        boolean boolean12 = proxyManagerImpl0.getTrackChanges();
        java.util.Map map13 = null;
        java.util.Map map14 = proxyManagerImpl0.copyMap(map13);
        org.junit.Assert.assertNull(date2);
        org.junit.Assert.assertNull(map4);
        org.junit.Assert.assertNull(proxy7);
        org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + false + "'", boolean10 == false);
        org.junit.Assert.assertNotNull(collection11);
        org.junit.Assert.assertTrue("'" + boolean12 + "' != '" + true + "'", boolean12 == true);
        org.junit.Assert.assertNull(map14);
    }

    @Test
    public void test18() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test18");
        org.apache.openjpa.util.ProxyManagerImpl proxyManagerImpl0 = new org.apache.openjpa.util.ProxyManagerImpl();
        java.util.Date date1 = null;
        java.util.Date date2 = proxyManagerImpl0.copyDate(date1);
        java.util.Map map3 = null;
        java.util.Map map4 = proxyManagerImpl0.copyMap(map3);
        java.util.Map map5 = null;
        java.util.Map map6 = proxyManagerImpl0.copyMap(map5);
        proxyManagerImpl0.setDelayCollectionLoading(false);
        org.junit.Assert.assertNull(date2);
        org.junit.Assert.assertNull(map4);
        org.junit.Assert.assertNull(map6);
    }

    @Test
    public void test19() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test19");
        org.apache.openjpa.util.ProxyManagerImpl proxyManagerImpl0 = new org.apache.openjpa.util.ProxyManagerImpl();
        java.util.Date date1 = null;
        java.util.Date date2 = proxyManagerImpl0.copyDate(date1);
        proxyManagerImpl0.setDelayCollectionLoading(true);
        java.lang.Class<?> wildcardClass5 = proxyManagerImpl0.getClass();
        org.junit.Assert.assertNull(date2);
        org.junit.Assert.assertNotNull(wildcardClass5);
    }

    @Test
    public void test20() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test20");
        org.apache.openjpa.util.ProxyManagerImpl proxyManagerImpl0 = new org.apache.openjpa.util.ProxyManagerImpl();
        proxyManagerImpl0.setAssertAllowedType(false);
        java.util.Date date3 = null;
        java.util.Date date4 = proxyManagerImpl0.copyDate(date3);
        org.junit.Assert.assertNull(date4);
    }

    @Test
    public void test21() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test21");
        org.apache.openjpa.util.ProxyManagerImpl proxyManagerImpl0 = new org.apache.openjpa.util.ProxyManagerImpl();
        java.util.Date date1 = null;
        java.util.Date date2 = proxyManagerImpl0.copyDate(date1);
        java.util.Map map3 = null;
        java.util.Map map4 = proxyManagerImpl0.copyMap(map3);
        java.util.Calendar calendar5 = null;
        java.util.Calendar calendar6 = proxyManagerImpl0.copyCalendar(calendar5);
        java.util.Map map7 = null;
        java.util.Map map8 = proxyManagerImpl0.copyMap(map7);
        org.junit.Assert.assertNull(date2);
        org.junit.Assert.assertNull(map4);
        org.junit.Assert.assertNull(calendar6);
        org.junit.Assert.assertNull(map8);
    }

    @Test
    public void test22() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test22");
        org.apache.openjpa.util.ProxyManagerImpl proxyManagerImpl0 = new org.apache.openjpa.util.ProxyManagerImpl();
        java.util.Date date1 = null;
        java.util.Date date2 = proxyManagerImpl0.copyDate(date1);
        java.util.Map map3 = null;
        java.util.Map map4 = proxyManagerImpl0.copyMap(map3);
        org.apache.openjpa.util.Proxy proxy7 = proxyManagerImpl0.newCustomProxy((java.lang.Object) (byte) 10, false);
        proxyManagerImpl0.setDelayCollectionLoading(true);
        org.junit.Assert.assertNull(date2);
        org.junit.Assert.assertNull(map4);
        org.junit.Assert.assertNull(proxy7);
    }

    @Test
    public void test23() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test23");
        org.apache.openjpa.util.ProxyManagerImpl proxyManagerImpl0 = new org.apache.openjpa.util.ProxyManagerImpl();
        java.util.Date date1 = null;
        java.util.Date date2 = proxyManagerImpl0.copyDate(date1);
        java.util.Collection collection3 = null;
        java.util.Collection collection4 = proxyManagerImpl0.copyCollection(collection3);
        java.lang.Object obj5 = null;
        org.apache.openjpa.util.Proxy proxy7 = proxyManagerImpl0.newCustomProxy(obj5, true);
        boolean boolean8 = proxyManagerImpl0.getDelayCollectionLoading();
        java.util.Calendar calendar9 = null;
        java.util.Calendar calendar10 = proxyManagerImpl0.copyCalendar(calendar9);
        org.junit.Assert.assertNull(date2);
        org.junit.Assert.assertNull(collection4);
        org.junit.Assert.assertNull(proxy7);
        org.junit.Assert.assertTrue("'" + boolean8 + "' != '" + false + "'", boolean8 == false);
        org.junit.Assert.assertNull(calendar10);
    }

    @Test
    public void test24() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test24");
        org.apache.openjpa.util.ProxyManagerImpl proxyManagerImpl0 = new org.apache.openjpa.util.ProxyManagerImpl();
        java.util.Date date1 = null;
        java.util.Date date2 = proxyManagerImpl0.copyDate(date1);
        java.util.Map map3 = null;
        java.util.Map map4 = proxyManagerImpl0.copyMap(map3);
        org.apache.openjpa.util.Proxy proxy7 = proxyManagerImpl0.newCustomProxy((java.lang.Object) (byte) 10, false);
        proxyManagerImpl0.setTrackChanges(true);
        boolean boolean10 = proxyManagerImpl0.getDelayCollectionLoading();
        java.util.Collection collection11 = proxyManagerImpl0.getUnproxyable();
        java.util.Map map12 = null;
        java.util.Map map13 = proxyManagerImpl0.copyMap(map12);
        proxyManagerImpl0.setAssertAllowedType(true);
        org.junit.Assert.assertNull(date2);
        org.junit.Assert.assertNull(map4);
        org.junit.Assert.assertNull(proxy7);
        org.junit.Assert.assertTrue("'" + boolean10 + "' != '" + false + "'", boolean10 == false);
        org.junit.Assert.assertNotNull(collection11);
        org.junit.Assert.assertNull(map13);
    }

    @Test
    public void test25() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test25");
        org.apache.openjpa.util.ProxyManagerImpl proxyManagerImpl0 = new org.apache.openjpa.util.ProxyManagerImpl();
        java.util.Date date1 = null;
        java.util.Date date2 = proxyManagerImpl0.copyDate(date1);
        java.util.Collection collection3 = null;
        java.util.Collection collection4 = proxyManagerImpl0.copyCollection(collection3);
        java.lang.Object obj5 = null;
        org.apache.openjpa.util.Proxy proxy7 = proxyManagerImpl0.newCustomProxy(obj5, true);
        java.util.Collection collection8 = proxyManagerImpl0.getUnproxyable();
        org.junit.Assert.assertNull(date2);
        org.junit.Assert.assertNull(collection4);
        org.junit.Assert.assertNull(proxy7);
        org.junit.Assert.assertNotNull(collection8);
    }

    @Test
    public void test26() throws Throwable {
        if (debug)
            System.out.format("%n%s%n", "RegressionTest0.test26");
        org.apache.openjpa.util.ProxyManagerImpl proxyManagerImpl0 = new org.apache.openjpa.util.ProxyManagerImpl();
        java.util.Date date1 = null;
        java.util.Date date2 = proxyManagerImpl0.copyDate(date1);
        java.util.Map map3 = null;
        java.util.Map map4 = proxyManagerImpl0.copyMap(map3);
        proxyManagerImpl0.setUnproxyable("hi!");
        org.junit.Assert.assertNull(date2);
        org.junit.Assert.assertNull(map4);
    }
}