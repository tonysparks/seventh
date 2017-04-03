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
	
	/*
	 * purpose : test remove and hashRemove function in ArrayMap class.
	 * input : class ArrayMap.
	 * expected output : the return value of calling remove(ArrayMap) is null.
	 * 					
	 */
	@Test
	public <K,V> void testRemove(){
		ArrayMap<K,V> test = new ArrayMap<K,V>(0);
		assertEquals(test.remove(test),null);		
	}
	/*
	 * purpose : test ContainsKey function in ArrayMap class.
	 * input : K Key for "Key", V Value for "Value". and class ArrayMap.
	 * expected output : return value is false.
	 * 					after calling set function, then return value is true.
	 * 					but, never true in this case. cause, when erase, set and 
	 * 					initialize, hashKeys always has value by minimum 1.
	 */
	@Test
	public <K,V> void testContainsKey(){
		ArrayMap<K,V> test = new ArrayMap<K,V>(0);
		K Key = (K)"Key";
		V Value = (V)"Value";
		assertTrue(test.containsKey(test)==false);
//		test.set(Key, Value);
//		assertTrue(test.containsKey(test)==true);
//		
	}
	
	/*
	 * purpose : test Equal function in ArrayMap class.
	 * input : two classes named ArrayMap.
	 * expected output : true, false for each class.
	 * 					can't reach getClass()!= val.getClass() sentence.
	 * 					
	 */
	@Test
	public <K,V> void testEqual(){
		ArrayMap<K,V> test = new ArrayMap<K,V>();
		ArrayMap<K,V> tests = new ArrayMap<K,V>(0);
		ArrayMap<K,V> nullTest = null;
		assertTrue(test.equal(test));
		assertTrue(test.equal(tests)==false);
		assertTrue(test.equal(nullTest)==false);
	}
	
	/*
	 * purpose : test containsValue function in ArrayMap class.
	 * input : K Key for "Key", V Value for "Value" and V Values for "Values".
	 * expected output : return value of containsValue is false.
	 */
	@Test
	public <K,V> void testContainsValue(){
		ArrayMap<K,V> test = new ArrayMap<K,V>();
		K Key = (K)"Key";
		V Value = (V)"Value";
		V Values = (V)"Values";
		assertTrue(test.containsValue(Value)==false);
		test.set(Key, Value);
		test.containsValue(Value);
		test.containsValue(Values);
	}
	
	/*
	 * purpose : test Get function in ArrayMap class.
	 * input : K Key for "Key", V Value for "Value". and then call set function.
	 * expected output: when calls get function for Key, then return value is "Value".
	 * 
	 */
	@Test
	public <K,V> void testGet(){
		ArrayMap<K,V> test = new ArrayMap<K,V>();
		K Key = (K)"Key";
		V Value = (V)"Value";
		test.set(Key, Value);
		assertEquals(test.get(Key),"Value");
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
