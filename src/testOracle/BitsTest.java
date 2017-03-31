package testOracle;

import static org.junit.Assert.*;

import org.junit.Test;

import seventh.shared.Bits;

public class BitsTest {

	/*
	 * purpose : test IsSignBitSet function in Bits class.
	 * input : ((byte)-1), ((byte)0), ((byte)1)
	 * expected output : true, false, false each.
	 */

	@Test
	public void testIsSignBitSet() {
		Bits test = new Bits();
		assertTrue(test.isSignBitSet((byte)-1)==true);
		assertTrue(test.isSignBitSet((byte)0)==false);
		assertTrue(test.isSignBitSet((byte)1)==false);
	}

//	@Test
//	public void testSetSignBit() {
//		
//	}

//	@Test
//	public void testGetWithoutSignBit() {
//		
//	}

}
