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
	/*
	 * purpose : test setDataElement coverage in BitArray class.
	 * input : integer 0 ,integer 0 each for i, data
	 * expected output : data[0]==0 in BitArray class
	 */
	
	@Test
	public void testsetDataElement(){
		BitArray test = new BitArray(33);
		test.setDataElement(0, 0);
		assertTrue(test.getData()[0]==0);
	}
	
	/* 
	 * purpose : test getData function coverage in BitArray class.
	 * input : integer 33
	 * expected output : data[0]==0 in BitArray class
	 * 
	 */
	@Test
	public void testgetData(){
		BitArray test = new BitArray(33);
		assertTrue(test.getData()[0]==0);
	}
	
	/*
	 * purpose : test clear function coverage in BitArray class.
	 * input : integer 33 and play the function clear.
	 * expected output : data[0]==0 in BitArray class.
	 */
	@Test
	public void testclear(){
		BitArray test = new BitArray(33);
		test.clear();
		assertTrue(test.getData()[0]==0);
	}
	

}
