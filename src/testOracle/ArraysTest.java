package testOracle;

import static org.junit.Assert.*;

import org.junit.Test;

import seventh.shared.Arrays;

public class ArraysTest {

	/*
	 * purpose : test UsedLength function in Arrays class.
	 * input : generic type [T] array. 
	 * 			zeroStuff for testing overall sentence,
	 * 			incaseStuff for testing "if sentence" on second,
	 * 			nullStuff for testing "if sentence" on first.
	 * expected output : assertEquals 0 for zeroStuff,
	 * 					assertEquals 1 for incaseStuff,
	 * 					assertEquals 0 for nullStuff.
	 */
	@Test
	public <T> void testUsedLength() {
		
		@SuppressWarnings("unchecked")
		T[] zeroStuff = (T[])new Object[3];
		T[] incaseStuff = (T[])new Object[1];
		T[] nullStuff = null;
		incaseStuff[0] = (T) "A";
		assertEquals(Arrays.usedLength(zeroStuff),0);
		assertEquals(Arrays.usedLength(incaseStuff),1);
		assertEquals(Arrays.usedLength(nullStuff),0);
		
		
	}

//	@Test
//	public void testClear() {
//		fail("Not yet implemented");
//	}

}
