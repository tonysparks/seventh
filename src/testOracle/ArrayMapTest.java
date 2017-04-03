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

//	@Test
//	public void testHashget() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSet() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testRawset() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testLength() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testNexti() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetKey() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetValue() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testNextKey() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testNextValue() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testHashset() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testHashFindSlot() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testHashClearSlot() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testEqual() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testSize() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testIsEmpty() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testContainsKey() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testContainsValue() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGet() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testPut() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testRemove() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testPutAll() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testClear() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testKeySet() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testValues() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testEntrySet() {
//		fail("Not yet implemented");
//	}

}
