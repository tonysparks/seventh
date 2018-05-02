package test.math;

import static org.junit.Assert.*;

import org.junit.Test;

import seventh.math.FastMath;

public class FastMathTest {

     /*
       * Purpose: test intMax in FastMath class
       * Input: int, int
       * Expected: Bigger one 
       * example (2147483647, 0) = 2147483647
       *             
       */
    @Test
    public void intMaxTest() {
        assertEquals(FastMath.max(2147483647, 0),2147483647);
        assertEquals(FastMath.max(0, -2147483648),0);
        assertEquals(FastMath.max(-2147483648, 2147483647),2147483647);
    }
    

      /*
   * Purpose: test doubleMax in FastMath class
   * Input: double, double
   * Expected: bigger one
   * example (2147483647, 0) = 2147483647
   */
    @Test
    public void doubleMaxTest()
    {
        assertTrue(FastMath.max(Double.MAX_VALUE, 0)== Double.MAX_VALUE);
        assertTrue(FastMath.max(0, Double.MIN_VALUE) == Double.MIN_VALUE); //it must be false but true
        assertTrue(FastMath.max(Double.MAX_VALUE, Double.MIN_VALUE) == Double.MAX_VALUE);
    }
     
    
    /*
       * Purpose: test floatMax in FastMath class
       * Input: float, float
       * Expected: bigger one
       * example FastMath.max(Float.MAX_VALUE, 0)= Float.MAX_VALUE; 
       */
    @Test
    public void floatMaxTest()
    {
        assertTrue(FastMath.max(Float.MAX_VALUE, 0)== Float.MAX_VALUE);
        assertTrue(FastMath.max(0,Float.MIN_VALUE)==Float.MIN_VALUE); // it must be false, but true 
        assertTrue(FastMath.max(Float.MAX_VALUE, Float.MIN_VALUE)== Float.MAX_VALUE);
    }
    
      /*
       * Purpose: test doubleMax in FastMath class
       * Input: long, long
       * Expected: bigger one
       * example (2147483647,Long.MAX_VALUE) = Long.MAX_VALUE       
       */
    @Test
    public void longMaxTest()
    {
        assertEquals(FastMath.max(Long.MIN_VALUE,Long.MAX_VALUE), Long.MAX_VALUE);
        assertEquals(FastMath.max(0,Long.MAX_VALUE), Long.MAX_VALUE);
        assertEquals(FastMath.max(Long.MIN_VALUE,0), 0);
    }
    
    /*
       * Purpose: test intMin in FastMath class
       * Input: int, int
       * Expected: smaller one
       * example (-2147483648, 0) = -2147483648       
       */
    @Test
    public void intMinTest()
    {
        assertEquals(FastMath.min(-2147483648, 0),-2147483648);
        assertEquals(FastMath.min(-2147483648, 2147483647),-2147483648);
        assertEquals(FastMath.min(2147483647, 0),0);
    }
    
    /*
       * Purpose: test floatMin in FastMath class
       * Input: float, float
       * Expected: smaller one
       * example (Float.MIN_VALUE, Float.MAX_VALUE) = Float.MIN_VALUE       
       */
    @Test
    public void floatMinTest()
    {
        assertTrue(FastMath.min(Float.MIN_VALUE, Float.MAX_VALUE) == Float.MIN_VALUE);
        assertTrue(FastMath.min(Float.MIN_VALUE, 0) == 0); //it must be false, but true
        assertTrue(FastMath.min(0, Float.MAX_VALUE) == 0);
    }
    /*
       * Purpose: test longMin in FastMath class
       * Input: long,long
       * Expected: smaller one
       * example (Long.MAX_VALUE, Long.MIN_VALUE) = Long.MIN_VALUE
       */
    @Test
    public void longMinTest()
    {
        assertEquals(FastMath.min(Long.MAX_VALUE, Long.MIN_VALUE), Long.MIN_VALUE);
        assertEquals(FastMath.min(Long.MIN_VALUE, 0), Long.MIN_VALUE);
        assertEquals(FastMath.min(0, Long.MAX_VALUE), 0);
    }
    
    /*
       * Purpose: test doubleMin in FastMath class
       * Input: double, double
       * Expected: smaller one
       * example (Double.MAX_VALUE, Double.MIN_VALUE) = Double.MIN_VALUE
       */
    @Test
    public void doubleMinTest()
    {
        assertTrue(FastMath.min(Double.MAX_VALUE, Double.MIN_VALUE) == Double.MIN_VALUE);
        assertTrue(FastMath.min(0, Double.MIN_VALUE) == 0); //it must be false, but true
        assertTrue(FastMath.min(Double.MAX_VALUE, 0) == 0);
    }
    
    
    /*
       * Purpose: test sin in FastMath class
       * Input: float
       * Expected: result = sin(float) 
       * example sin(PI/6) should be 0.5 but, it's a FastMath, so i gave 0.01 error range 
       * 0.49<sin(PI/6)<0.51
       */
    @Test
    public void sinTest()
    {
        assertTrue(FastMath.sin(FastMath.PI/6) > 0.49);
        assertTrue(FastMath.sin(FastMath.PI/6) < 0.51);
        assertTrue(FastMath.sin(0) >= 0);
        assertTrue(FastMath.sin(0) < 0.01);
        assertTrue(FastMath.sin(FastMath.PI/2)>0.9);
        assertTrue(FastMath.sin(FastMath.PI/2)<=1);
    }
    
    /*
       * Purpose: test cosin in FastMath class
       * Input: float
       * Expected: result = cosin(float) 
       * example cos(PI/3) should be 0.5 but, it's a FastMath, so i gave 0.01 error range 
       * 0.49<sin(PI/6)<0.51
       */
    @Test
    public void cosTest()
    {
        assertTrue(FastMath.cos(FastMath.PI/3) > 0.49);
        assertTrue(FastMath.cos(FastMath.PI/3) < 0.51);
        assertTrue(FastMath.cos(0) <= 1);
        assertTrue(FastMath.cos(0) > 0.9);
        assertTrue(FastMath.cos(FastMath.PI/2) > -0.01);
        assertTrue(FastMath.cos(FastMath.PI/2) < 0.01);
        
    }
    
}
