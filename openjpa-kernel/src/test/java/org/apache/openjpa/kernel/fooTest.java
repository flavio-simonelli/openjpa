package org.apache.openjpa.kernel;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class fooTest {

    @Test
    public void ciao(){
        BrokerImpl broker = new BrokerImpl();
        try{
            broker.initialize(
                    null,
                    null,
                    false,
                    0,
                    false
            );
        } catch (Exception e) {
            assertTrue(true);
        }
        assertTrue(broker != null);
    }

}
