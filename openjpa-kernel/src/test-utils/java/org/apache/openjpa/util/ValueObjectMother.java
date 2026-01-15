package org.apache.openjpa.util;

/**
 * Object Mother Pattern per i test di CacheMap.
 * Fornisce factory method statici per creare oggetti immutabili, mutabili o null.
 */
public class ValueObjectMother {

    private ValueObjectMother() {}

    public static Object createValueObjectImmutable(int index) {
        return "Value-" + index;
    }

    public static Object createValueObjectNull() {
        return null;
    }

    public static MutableObject createValueObjectMutable(int index, String name) {
        return new MutableObject(index,name);
    }


    public static class MutableObject {
        public final int index;
        public String name;

        public MutableObject(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public MutableObject(int index) {
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "MutableObject: index:"+index+",name:" + name + ";";
        }
    }
}
