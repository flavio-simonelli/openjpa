package org.apache.openjpa.util;

public class KeyObjectMother {

    private  KeyObjectMother() {}

    public static Object createKeyObjectValid(int index){
        return "Key-"+index;
    }

    public static Object createKeyObjectBad(){
        return new BadKeyObject();
    }

    public static class BadKeyObject {
        @Override
        public int hashCode() {
            throw new RuntimeException("Boom! HashCode Exception");
        }

        @Override
        public boolean equals(Object obj) {
            throw new RuntimeException("Boom! Equals Exception");
        }

        @Override
        public String toString() {
            return "ExplosiveKey";
        }
    }

}
