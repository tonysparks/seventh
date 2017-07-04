package test.math;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import seventh.math.Vector2f;

public class Vector2fOperationTest {

    /*
     * Purpose: To test round(). x = Math.round(x), y = Math.round(y)
     * Input: Vector2f(2.6f, 3.2f), vector.round()
     * Expected: vector.x = 3, vector.y = 3
     */
    @Test
    public void RoundTest() {
        Vector2f vector = new Vector2f(2.6f, 3.2f);
        vector.round();
        assertTrue(vector.x == 3);
        assertTrue(vector.y == 3);
    }
    
    
    /*
     * Purpose: To test subtract(Vector2f v), it returns Vector2f(this.x-v.x, this.y - v.y)
     * Input: vector(5,5), v = (3,3)
     * Expected: dest.x = 2, dest.y = 2
     */
    @Test
    public void SubtractTest() {
        Vector2f vector = new Vector2f(5, 5);
        Vector2f v = new Vector2f(3, 3);
        Vector2f dest = new Vector2f();
        dest = vector.subtract(v);
        assertTrue(dest.x == 2);
        assertTrue(dest.y == 2);    
    }
    
    /*
     * Purpose: To test addition(Vector2f v), it returns Vector2f(this.x+v.x, this.y + v.y)
     * Input: vector(5,5), v = (3,3)
     * Expected: dest.x = 8, dest.y = 8
     */
    @Test
    public void AdditionTest() {
        Vector2f vector = new Vector2f(5, 5);
        Vector2f v = new Vector2f(3, 3);
        Vector2f dest = new Vector2f();
        dest = vector.addition(v);
        assertTrue(dest.x == 8);
        assertTrue(dest.y == 8);    
    }
    
    
    /*
     * Purpose: To test mult(Vector2f v), it returns Vector2f(this.x*v.x, this.y*v.y)
     * Input: vector(5,5), v = (3,3)
     * Expected: dest.x = 15, dest.y = 15
     */
    @Test
    public void MultiplyVectorTest() {
        Vector2f vector = new Vector2f(5, 5);
        Vector2f v = new Vector2f(3, 3);
        Vector2f dest = new Vector2f();
        dest = vector.mult(v);
        assertTrue(dest.x == 15);
        assertTrue(dest.y == 15);    
    }
    
    /*
     * Purpose: To test mult(float scalar), it returns Vector2f(this.x*scalar, this.y*scalar)
     * Input: vector(5,5), scalar = 3
     * Expected: dest.x = 15, dest.y = 15
     */
    @Test
    public void MultiplyFloatTest() {
        Vector2f vector = new Vector2f(5, 5);
        Vector2f dest = new Vector2f();
        dest = vector.mult(3);
        assertTrue(dest.x == 15);
        assertTrue(dest.y == 15);    
    }
    
    /*
     * Purpose: To test div(Vector2f v), it returns Vector2f(this.x/v.x, this.y/v.y)
     * Input: vector(10, 10), v(2, 5)
     * Expected: dest.x = 5, dest.y = 2
     */
    @Test
    public void DivideVectorTest() {
        Vector2f vector = new Vector2f(10, 10);
        Vector2f v = new Vector2f(2, 5);
        Vector2f dest = new Vector2f();
        dest = vector.div(v);
        assertTrue(dest.x == 5);
        assertTrue(dest.y == 2);    
    }
    
    /*
     * Purpose: To test div(float v), it returns Vector2f(this.x/scalar, this.y/scalar)
     * Input: vector(10, 10), scalar = 5
     * Expected: dest.x = 2, dest.y = 2
     */
    @Test
    public void DivideFloatTest() {
        Vector2f vector = new Vector2f(10, 10);
        Vector2f dest = new Vector2f();
        dest = vector.div(5);
        assertTrue(dest.x == 2);
        assertTrue(dest.y == 2);    
    }


}
