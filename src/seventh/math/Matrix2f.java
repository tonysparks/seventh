/*
 *    leola-live 
 *  see license.txt
 */
package seventh.math;

/**
 * A 2x2 matrix
 * 
 * @author Tony
 *
 */
public class Matrix2f {

    /**
     * Column 1 of the matrix
     */
    public final Vector2f col1;
    
    /**
     * Column 2 of the matrix
     */
    public final Vector2f col2;
    
    /**
     * Identity matrix
     */
    public static final Matrix2f IDENTITY = new Matrix2f(1, 0, 0, 1);
    
    /**
     * @param m
     */
    public Matrix2f(Matrix2f m) {
        this.col1 = new Vector2f(m.col1);
        this.col2 = new Vector2f(m.col2);
    }
    
    /**
     */
    public Matrix2f() {
        this.col1 = new Vector2f();
        this.col2 = new Vector2f();
    }
    
    /**
     * @param c1
     * @param c2
     */
    public Matrix2f(Vector2f c1, Vector2f c2) {
        this.col1 = c1;
        this.col2 = c2;
    }
    
    /**
     * @param m
     */
    public Matrix2f(float[][] m) {
        this.col1 = new Vector2f(m[0][0], m[1][0]);
        this.col2 = new Vector2f(m[0][1], m[1][1]);
    }
    
    /**
     * @param c00
     * @param c01
     * @param c10
     * @param c11
     */
    public Matrix2f(float c00, float c01
                   ,float c10, float c11) {
        this.col1 = new Vector2f();
        this.col2 = new Vector2f();
        
        this.col1.x = c00; this.col2.x = c01;
        this.col1.y = c10; this.col2.y = c11;
    }
    
    /**
     * To Identity matrix
     */
    public void toIdentity() {
        this.col1.x = 1; this.col2.x = 0;
        this.col1.y = 0; this.col2.y = 1;
    }
    
    /**
     * To Zero Matrix
     */
    public void zeroOut() {
        this.col1.x = 0; this.col2.x = 0;
        this.col1.y = 0; this.col2.y = 0;
    }
    
    /**
     * Does a matrix multiplication against the supplied vector.
     * 
     * @param a
     * @param b
     * @param dest
     * @return the dest vector for convenience
     */
    public static Vector2f Matrix2fMult(Matrix2f a, Vector2f b, Vector2f dest) {
        float x = a.col1.x * b.x + a.col2.x * b.y;
        float y = a.col1.y * b.x + a.col2.y * b.y;
        
        dest.x = x;
        dest.y = y;
        
        return dest;
    }
}
