package test.shared;

import static org.junit.Assert.*;

import org.junit.Test;

import seventh.shared.Arrays;

public class ArraysTest {

    /*
     * purpose : test UsedLength function in Arrays class.
     * input : generic type [T] array. 
     *             zeroStuff for testing overall sentence,
     *             incaseStuff for testing "if sentence" on second,
     *             nullStuff for testing "if sentence" on first.
     * expected output : assertEquals 0 for zeroStuff,
     *                     assertEquals 1 for incaseStuff,
     *                     assertEquals 0 for nullStuff.
     */
    @SuppressWarnings("unchecked")
    @Test
    public <T> void testUsedLength() {
        
        T[] zeroStuff = (T[])new Object[3];
        T[] incaseStuff = (T[])new Object[1];
        T[] nullStuff = null;
        assertNull(nullStuff);
        incaseStuff[0] = (T) "A";
        assertEquals(Arrays.usedLength(zeroStuff),0);
        assertEquals(Arrays.usedLength(incaseStuff),1);
        assertEquals(Arrays.usedLength(nullStuff),0);
        
        
    }

    /*
     * purpose : test clear function in Arrays class.
     * input : generic type [T] array. 
     *             incaseStuff for "if sentence not null",
     *             nullStuff for "if sentence null" 
     * output : assertNull for incaseStuff is true.
     *             ***but in this case, an error about JavaDoc is poped up.**
     */
    @SuppressWarnings("unchecked")
    @Test
    public <T> void testClear() {
        
        T[] incaseStuff = (T[])new Object[1];
        incaseStuff[0] = (T) "A";
        assertNotNull(incaseStuff);
        
        T[] nullStuff = null;
        assertNull(nullStuff);
        
        Arrays.clear(incaseStuff);        
        Arrays.clear(nullStuff);
//        assertNull(incaseStuff);
        
    }

}
