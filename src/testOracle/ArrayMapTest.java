package testOracle;

import static org.junit.Assert.*;

import org.junit.Test;

import seventh.shared.ArrayMap;

public class ArrayMapTest {

	/*
	 * purpose : test ArrayMap and Presize function in ArrayMaptest class.
	 * input : default, value 0, value 1 for class creation.
	 * expected output : assertNotNull for all creation is true.
	 */
	@Test
	public <K,V> void testArrayMapAndPresize() {
		ArrayMap<K,V> testNone = new ArrayMap<K,V>();
		ArrayMap<K,V> testZero = new ArrayMap<K,V>(0);
		ArrayMap<K,V> testOne = new ArrayMap<K,V>(1);	
		
		assertNotNull(testNone);
		assertNotNull(testZero);
		assertNotNull(testOne);
	}

	/*
	 * purpose : test set and rawset function in ArrayMap class.
	 * input : K for "key", V for "value"
	 * expected output : none. cause two function invoke another.
	 */
	@Test
	public <K,V> void testSetAndrawSet() {
		ArrayMap<K,V> test = new ArrayMap<K,V>();
		K Key = (K)"key";
		V Value = (V)"value";
		test.set(Key, Value);
		assertNotNull(test);
	}
	
	/*
	 * purpose : test length function in ArrayMap class.
	 * input : class creation.
	 * expected output : return value is 0. cause nothing in hashEntries.
	 */	
	@Test
	public <K,V> void testLength(){
		ArrayMap<K,V> test = new ArrayMap<K,V>();
		assertTrue(test.length()==0);
	}
	
	/*
	 * purpose : test Size function in ArrayMap class.
	 * input : class creation.
	 * expected output : return value is 0. cause nothing in hashEntries.
	 */	
	@Test
	public <K,V> void testSize(){
		ArrayMap<K,V> test = new ArrayMap<K,V>();
		assertTrue(test.size()==0);
	}

	/*
	 * purpose : test IsEmpty function in ArrayMap class.
	 * input : class creation.
	 * expected output : return value is true at first. cause nothing in hashEntries.
	 *					after put one hashset, then isEmpty is false.
	 */	
	@Test
	public <K,V> void testIsEmpty(){
		ArrayMap<K,V> test = new ArrayMap<K,V>();
		K Key = (K)"Key";
		V Value = (V)"Value";
		assertTrue(test.isEmpty()==true);
		test.hashset(Key, Value);
		assertTrue(test.isEmpty()==false);
	}
	
	/*
	 * purpose : test Put function in ArrayMap class.
	 * input : K Key for "Key", V Value for "Value".
	 * expected output : the return value of calling put function is null.
	 * 
	 */
	@Test
	public <K,V> void testPut(){
		ArrayMap<K,V> test = new ArrayMap<K,V>();
		K Key = (K)"Key";
		V Value = (V)"Value";
		assertEquals(test.put(Key, Value),null);		
	}
	
	
//	@Test
//	public <K,V> void testNexti(){
//		ArrayMap<K,V> test = new ArrayMap<K,V>();
//		K Key = (K)"key";
//		V Value = (V)"value";
//		K nullKey = null;
//		test.hashset(Key, Value);
//		assertTrue(test.nexti(nullKey)==-1);
//		assertTrue(test.nexti(Key)==-1);
//	}
//
//	@Test
//	public <K,V> void testHashset() {
//		ArrayMap<K,V> test = new ArrayMap<K,V>(0);
//		test.clear();
//		K Key = (K)"key";
//		V Value = (V)"value";
//		V nullValue = null;
//		test.clear();
//		test.hashset(Key, Value);
////		test.hashset(Key, nullValue);
//		
//	}



}
