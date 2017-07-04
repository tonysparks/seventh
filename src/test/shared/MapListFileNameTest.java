package test.shared;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import seventh.shared.MapList;


/*
 * Purpose: Test stripFileExtension function whether it return correct String
 *   maplist.stripFileExtension(String fileName);
 *   FileName = "abc.json"
 *   Return = "abc"
 */


public class MapListFileNameTest {
    
    private MapList maplist;
    
    /*
     * Purpose: Test String Filename is correctly changed 
     * Input: fileName = "abc.json" 
     *        
     * Expected: "abc"
     */

    @Test
    public void testStringFileName() {
        final String fileName = "abc.json";
        String actual = maplist.stripFileExtension(fileName);
        String expected = "abc";
        assertEquals(actual, expected);
    }
    
    
    /*
     * Purpose: Test Integer Filename is correctly changed 
     * Input: fileName = "123.json" 
     *        
     * Expected: "123"
     */
    @Test
    public void testIntegerFileName() {
        final String fileName = "123.json";
        String actual = maplist.stripFileExtension(fileName);
        String expected = "123";
        assertEquals(actual, expected);
    }
    
    /*
     * Purpose: Test Mixed Filename is correctly changed 
     * Input: fileName = "abc123.json" 
     *        
     * Expected: "abc123"
     */
    @Test
    public void testMixedFileName() {
        final String fileName = "abc123.json";
        String actual = maplist.stripFileExtension(fileName);
        String expected = "abc123";
        assertEquals(actual, expected);
    }
    
    /*
     * Purpose: Test other type Filename is correctly changed 
     * Input: fileName = "abc123.exe" 
     *        
     * Expected: "abc.exe"
     */
    @Test
    public void testOtherTypeFileName() {
        final String fileName = "abc.exe";
        String actual = maplist.stripFileExtension(fileName);
        String expected = "abc.exe";
        assertEquals(actual, expected);
    }
    
    /*
     * Purpose: Test long Filename is correctly changed 
     * Input: fileName = "aaabbbbcccddddeeeffffqqqqwerrrrr.json" 
     *        
     * Expected: "aaabbbbcccddddeeeffffqqqqwerrrrr"
     */
    @Test
    public void testLongFileName() {
        final String fileName = "aaabbbbcccddddeeeffffqqqqwerrrrr.json";
        String actual = maplist.stripFileExtension(fileName);
        String expected = "aaabbbbcccddddeeeffffqqqqwerrrrr";
        assertEquals(actual, expected);
    }
    
    
    /*
     * Purpose: Test Single Character Filename is correctly changed 
     * Input: fileName = "a.json" 
     *        
     * Expected: "a"
     */
    @Test
    public void testSingleFileName() {
        final String fileName = "a.json";
        String actual = maplist.stripFileExtension(fileName);
        String expected = "a";
        assertEquals(actual, expected);
    }
    
    
    /*
     * Purpose: Test Single integer Filename is correctly changed 
     * Input: fileName = "1.json" 
     *        
     * Expected: "1"
     */
    @Test
    public void testSingleIntegerFileName() {
        final String fileName = "1.json";
        String actual = maplist.stripFileExtension(fileName);
        String expected = "1";
        assertEquals(actual, expected);
    }
    



}
