package testOracle;

import static org.junit.Assert.*;

import org.junit.Test;

import seventh.shared.BitArray;

public class BitArrayTest {
/*
 * purpose : test BitArray function coverage in BitArray class.
 * input : integer 33
 * output : the variable data[0]==0
 */
	@Test
	public void testBitArray() {
		BitArray test = new BitArray(33);
		assertTrue(test.getData()[0]==0);		
	}

	

}
