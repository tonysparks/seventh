package test.math;

import static org.junit.Assert.*;

import org.junit.Test;

import seventh.math.Pair;

public class PairTest {
	
	/*
	 * Purpose: check that the null values stored
	 * Input: 	Pair default constructor
	 * 			pairD()
	 * Expected:
	 * 			pairD.first = null, pairD.second = null
	 */
	@Test
	public void testPairDefaultConstructor() {
		Pair pairD = new Pair();
		assertNull(pairD.getFirst() );
		assertNull(pairD.getSecond());
	}

	/*
	 * Purpose: check that the correct integer values stored
	 * Input: 	Pair<int, int> using positive, zero, negative
	 * 			pairP(3, 5), pairZ(0, 0), pairN(-7, -4)
	 * Expected:
	 * 			pairP.fisrt ==  3, pairP.second ==  5
	 * 			pairZ.fisrt ==  0, pairZ.second ==  0
	 * 			pairN.first == -7, pairN.second == -4
	 */
	@Test
	public void testPairIntConstructor() {
		Pair pairA = new Pair( 3,  5);
		assertEquals( 3, pairA.getFirst() );
		assertEquals( 5, pairA.getSecond());
		
		Pair pairZ = new Pair( 0,  0);
		assertEquals( 0, pairZ.getFirst() );
		assertEquals( 0, pairZ.getSecond());
		
		Pair pairN = new Pair(-7, -4);
		assertEquals(-7, pairN.getFirst() );
		assertEquals(-4, pairN.getSecond());
	}

	/*
	 * Purpose: check that the correct double values stored
	 * Input: 	Pair<double, double> using positive, zero, negative
	 * 			pairP(3.14, 5.26), pairZ(0.0, 0.0), pairN(-1.414, -4.19)
	 * Expected:
	 * 			pairP.fisrt =  3.14,  pairP.second =  5.26
	 * 			pairZ.fisrt =  0.0,   pairZ.second =  0.0
	 * 			pairN.first = -1.414, pairN.second = -4.19
	 */
	@Test
	public void testPairDoubleConstructor() {
		Pair pairA = new Pair( 3.14,   5.26);
		assertEquals( 3.14,  pairA.getFirst() );
		assertEquals( 5.26,  pairA.getSecond());
		
		Pair pairZ = new Pair( 0.0,    0.0 );
		assertEquals( 0.0 ,  pairZ.getFirst() );
		assertEquals( 0.0 ,  pairZ.getSecond());
		
		Pair pairN = new Pair(-1.414, -4.19);
		assertEquals(-1.414, pairN.getFirst() );
		assertEquals(-4.19,  pairN.getSecond());
	}
	
	/*
	 * Purpose: check that the correct integer values stored
	 * Input: 	Pair<int, int> set positive, zero, negative
	 * 			set pairP( 25,  10),
	 * 			set pairZ(  0,   0),
	 * 			set pairN(-10, -20)
	 * Expected:
	 * 			pairP.fisrt =  25,  pairP.second =  10
	 * 			pairZ.fisrt =   0,  pairZ.second =   0
	 * 			pairN.first = -10,  pairN.second = -20	 
	 * */
	@Test
	public void testPairSetInt() {
		Pair pairA = new Pair(-3,  5);
		pairA.setFirst(  25);
		assertEquals(    25,   pairA.getFirst() );
		pairA.setSecond( 10);
		assertEquals(    10,   pairA.getSecond());

		Pair pairZ = new Pair(22, 22);
		pairZ.setFirst(   0);
		assertEquals(     0,   pairZ.getFirst() );
		pairZ.setSecond(  0);
		assertEquals(     0,   pairZ.getSecond());

		Pair pairN = new Pair( 0,  0);
		pairN.setFirst( -10);
		assertEquals(   -10,   pairN.getFirst() );
		pairN.setSecond(-20);
		assertEquals(   -20,   pairN.getSecond());
	}

	/*
	 * Purpose: check that the correct double values stored
	 * Input: 	Pair<double, double> set positive, zero, negative
	 * 			set pairP(  26.56, 1069.639), 
	 * 			set pairZ(   0.0,    00.0000), 
	 * 			set pairN(-148.14,  -41.149)
	 * Expected:
	 * 			pairP.fisrt =   26.56,  pairP.second =  1069.639
	 * 			pairZ.fisrt =    0.0,   pairZ.second =    00.0000
	 * 			pairN.first = -148.14,  pairN.second =   -41.149	
	 */
	@Test
	public void testPairSetDouble() {
		Pair pairA = new Pair(  45.5,    268.157);
		pairA.setFirst(   26.56);
		assertEquals(     26.56,   pairA.getFirst() );
		pairA.setSecond(1069.639);
		assertEquals(   1069.639,  pairA.getSecond());
		
		Pair pairZ = new Pair(-479.369, 2308.2);
		pairZ.setFirst(    0.0);
		assertEquals(      0.0,    pairZ.getFirst() );
		pairZ.setSecond(  00.0000);
		assertEquals(     00.0000, pairZ.getSecond());
		
		Pair pairN = new Pair(  29.2,    175.17);
		pairN.setFirst( -148.14);
		assertEquals(   -148.14,   pairN.getFirst() );
		pairN.setSecond( -41.149);
		assertEquals(    -41.149,  pairN.getSecond());
	}
}
