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
	/*
	 * purpose : test SetSignBit function in Bits class.
	 * input : ((byte)-1)
	 * expected output : -1
	 */

	@Test
	public void testSetSignBit() {
		Bits test = new Bits();
		assertTrue(test.setSignBit((byte)-1)==-1);
	}

//	@Test
//	public void testGetWithoutSignBit() {
//		
//	}

}
