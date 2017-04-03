package test.shared;

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
		assertTrue(Bits.isSignBitSet((byte)-1)==true);
		assertTrue(Bits.isSignBitSet((byte)0)==false);
		assertTrue(Bits.isSignBitSet((byte)1)==false);
	}
	/*
	 * purpose : test SetSignBit function in Bits class.
	 * input : ((byte)-1)
	 * expected output : -1
	 */

	@Test
	public void testSetSignBit() {
		assertTrue(Bits.setSignBit((byte)-1)==-1);
	}

	/*
	 * purpose : test GetWithoutSignBit function in Bits class.
	 * input : ((byte)0)
	 * expected output : 0
	 */
	@Test
	public void testGetWithoutSignBit() {
		assertTrue(Bits.getWithoutSignBit((byte)0)==0);
	}

}
