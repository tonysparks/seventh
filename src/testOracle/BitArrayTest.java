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
	
	/*
	 * purpose : test setAll function coverage in BitArray class.
	 * input : integer 33 and play the function setAll.
	 * expected output : data[0]==0xFFffFFff in BitArray class.
	 */
	@Test
	public void testsetAll(){
		BitArray test = new BitArray(33);
		test.setAll();
		assertTrue(test.getData()[0]==0xFFffFFff);
	}
	
	/*
	 * purpose : test numberOfInts function coverage in BitArray class.
	 * input : integer 32 on class creation.
	 * output : data.length == 2
	 */
	@Test
	public void testnumberOfInts(){
		BitArray test = new BitArray(32);
		assertTrue(test.numberOfInts()==2);
	}
	
	/*
	 * purpose : test numberOfBytes function coverage in BitArray class.
	 * input : integer 32 on class creation.
	 * output : data.length == 8
	 */
	@Test
	public void testnumberOfBytes(){
		BitArray test = new BitArray(32);
		assertTrue(test.numberOfBytes()==8);
	}
	
	/*
	 * purpose : test size function coverage in BitArray class.
	 * input : integer 32 on class creation.
	 * output : return value is 2*32 when call size function.
	 */
	@Test
	public void testsize(){
		BitArray test = new BitArray(32);
		assertTrue(test.size()==2*32);
	}
	
	/*
	 * purpose : test testtoString override function in BitArray class.
	 * input : integer 32 on class creation and call toString().
	 * expected output: test.getData()[0]==0 cause Integer.toBinaryString(0)==0.
	 * 
	 */
	@Test
	public void testtoString(){
		BitArray test = new BitArray(32);
		test.toString();
		assertTrue(test.getData()[0]==0);
		System.out.println(Integer.toBinaryString(0));
		
	}
	/*
	 * purpose : test setBit function in BitArray class.
	 * input : integer 32 on class creation and setBit function each.
	 * expected output: test.getData()[0]==0 cause data[bitIndex(32)] = 0(00000000) and bitOffset(32) = 0(00000000).
	 *  
	 */
	@Test
	public void testsetBit(){
		BitArray test = new BitArray(32);
		test.setBit(32);
		assertTrue(test.getData()[0]==0);
	}
	
	/*
	 * purpose : test getBit function in BitArray class.
	 * input : integer 32 on class creation.
	 * expected output : getbit(32)=false cause data[bitIndex(32)]=0 and bitOffset(32)=0.
	 * 					so result is false.
	 * 					getbit(0)=true cause getData()[0]=1, so data[bitIndex(0)]=1 and bitOffset(0)=0
	 */
	@Test
	public void testgetBit(){
		BitArray test = new BitArray(32);
		test.getData()[0]=1;
		assertTrue(test.getBit(32)==false);
		assertTrue(test.getBit(0)==true);
		
	}

}
