package test.math;

import static org.junit.Assert.*;

import org.junit.Test;

import seventh.math.Tri;

public class TriTest {
	
	/*
	 * Purpose: check that the correct integer values stored
	 * Input: 	Tri<int, int, int> using positive, zero, negative
	 * 			TriP(43, 36, 88), TriZ(00000, 000, 00000), TriN(-55, -38, -25)
	 * Expected:
	 * 			TriP.fisrt ==    43, TriP.second ==  36, TriP.second ==    88
	 * 			TriZ.fisrt == 00000, TriZ.second == 000, TriZ.second == 00000
	 * 			TriN.first ==   -55, TriN.second == -38, TriN.second ==   -25
	 */
	@Test
	public void testTriIntConstructor() {
		Tri TriA = new Tri(   43,   36,   88);
		assertEquals(   43, TriA.getFirst() );
		assertEquals(   36, TriA.getSecond());
		assertEquals(   88, TriA.getThird() );
		
		Tri TriZ = new Tri(00000,  000, 0000);
		assertEquals(00000, TriZ.getFirst() );
		assertEquals(  000, TriZ.getSecond());
		assertEquals(00000, TriZ.getThird() );
		
		Tri TriN = new Tri(  -55, -38,  -25);
		assertEquals(  -55, TriN.getFirst() );
		assertEquals(  -38, TriN.getSecond());
		assertEquals(  -25, TriN.getThird() );
	}

	/*
	 * Purpose: check that the correct double values stored
	 * Input: 	Tri<double, double, double> using positive, zero, negative
	 * 			TriP(6.32, 64.72, 39.4), TriZ(0000.0, 00.0, 0.0), TriN(-93.59, -60.13, -1.78)
	 * Expected:
	 * 			TriP.fisrt ==    6.32, TriP.second ==  64.72,  TriP.second == 39.4
	 * 			TriZ.fisrt == 0000.0,  TriZ.second ==  00.0,   TriZ.second ==  0.0
	 * 			TriN.first ==  -93.59, TriN.second == -60.13,  TriN.second == -1.78
	 */
	@Test
	public void testTriDoubleConstructor() {
		Tri TriA = new Tri( 6.32,   64.72, 39.4);
		assertEquals(   6.32, TriA.getFirst() );
		assertEquals(  64.72, TriA.getSecond());
		assertEquals(  39.4,  TriA.getThird() );
		
		Tri TriZ = new Tri( 0000.0,    00.0, 0.0 );
		assertEquals(0000.0,  TriZ.getFirst() );
		assertEquals(  00.0,  TriZ.getSecond());
		assertEquals(   0.0,  TriZ.getThird() );
		
		Tri TriN = new Tri(-93.59, -60.13, -1.78);
		assertEquals( -93.59, TriN.getFirst() );
		assertEquals( -60.13, TriN.getSecond());
		assertEquals(  -1.78, TriN.getThird() );
	}

	/*
	 * Purpose: check that the correct integer values stored
	 * Input: 	Tri<int, int, int> set positive, zero, negative
	 * 			set TriP( 25,  10, 41),
	 * 			set TriZ(  0,   0, 00),
	 * 			set TriN(-10, -20, -3)
	 * Expected:
	 * 			TriP.fisrt ==  25,  TriP.second ==  10,  TriP.third ==  41
	 * 			TriZ.fisrt ==   0,  TriZ.second ==   0,  TriZ.third ==  00
	 * 			TriN.first == -10,  TriN.second == -20,  TriN.third ==  -3	 
	 * */
	@Test
	public void testTriSetInt() {
		Tri TriA = new Tri(-3,  5, 34);
		TriA.setFirst(  25);
		assertEquals(   25, TriA.getFirst() );
		TriA.setSecond( 10);
		assertEquals(   10, TriA.getSecond());
		TriA.setThird(  41);
		assertEquals(   41, TriA.getThird() );

		Tri TriZ = new Tri(22, 22,  3);
		TriZ.setFirst(   0);
		assertEquals(    0, TriZ.getFirst() );
		TriZ.setSecond(  0);
		assertEquals(    0, TriZ.getSecond());
		TriZ.setThird(  00);
		assertEquals(   00, TriZ.getThird() );

		Tri TriN = new Tri( 0,  0,  8);
		TriN.setFirst( -10);
		assertEquals(  -10, TriN.getFirst() );
		TriN.setSecond(-20);
		assertEquals(  -20, TriN.getSecond());
		TriN.setThird(  -3);
		assertEquals(   -3, TriN.getThird() );
	}

	/*
	 * Purpose: check that the correct double values stored
	 * Input: 	Tri<double, double, double> set positive, zero, negative
	 * 			set TriP(  26.56, 1069.639,  28.6 ), 
	 * 			set TriZ(   0.0,    00.0000, 00.00), 
	 * 			set TriN(-148.14,  -41.149, -30.24)
	 * Expected:
	 * 			TriP.fisrt ==   26.56, TriP.second == 1069.639,  TriP.third ==  28.6
	 * 			TriZ.fisrt ==    0.0,  TriZ.second ==   00.0000, TriZ.third ==  00.00
	 * 			TriN.first == -148.14, TriN.second ==  -41.149,  TriN.third == -30.24	
	 */
	@Test
	public void testTriSetDouble() {
		Tri TriA = new Tri(  45.5,    268.157, 45.5);
		TriA.setFirst(   26.56);
		assertEquals(    26.56,   TriA.getFirst() );
		TriA.setSecond(1069.639);
		assertEquals(  1069.639,  TriA.getSecond());
		TriA.setThird(   28.6);
		assertEquals(    28.6,    TriA.getThird() );
		
		Tri TriZ = new Tri(-479.369, 2308.2, 37.27);
		TriZ.setFirst(    0.0);
		assertEquals(     0.0,    TriZ.getFirst() );
		TriZ.setSecond(  00.0000);
		assertEquals(    00.0000, TriZ.getSecond());
		TriZ.setThird(   00.00);
		assertEquals(    00.00,   TriZ.getThird() );
		
		Tri TriN = new Tri(  29.2,    175.17, 42.14);
		TriN.setFirst( -148.14);
		assertEquals(  -148.14,   TriN.getFirst() );
		TriN.setSecond( -41.149);
		assertEquals(   -41.149,  TriN.getSecond());
		TriN.setThird(  -30.24);
		assertEquals(   -30.24,   TriN.getThird() );
	}
}
