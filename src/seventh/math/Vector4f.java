/*
 *    leola-live 
 *  see license.txt
 */
package seventh.math;

/**
 * @author Tony
 *
 */
public class Vector4f {

    /**
     * X Component
     */
    public float x;
    
    /**
     * Y Component
     */
    public float y;
    
    /**
     * Z Component
     */
    public float z;
    
    /**
     * W Component
     */
    public float w;

    /**
     * @param x
     * @param y
     * @param z
     * @param w
     */
    public Vector4f(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    /**
     */
    public Vector4f() {
        this(0,0,0,0);
    }

    /**
     * Sets all components.
     * 
     * @param x
     * @param y
     * @param z
     * @param w
     */
    public void set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }
    
    /**
     * @return the x
     */
    public float getX() {
        return this.x;
    }

    /**
     * @param x the x to set
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public float getY() {
        return this.y;
    }

    /**
     * @param y the y to set
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * @return the z
     */
    public float getZ() {
        return this.z;
    }

    /**
     * @param z the z to set
     */
    public void setZ(float z) {
        this.z = z;
    }

    /**
     * @return the w
     */
    public float getW() {
        return this.w;
    }

    /**
     * @param w the w to set
     */
    public void setW(float w) {
        this.w = w;
    }
    
    
}
