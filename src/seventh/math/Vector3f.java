/*
 *	leola-live 
 *  see license.txt
 */
package seventh.math;

/**
 * @author Tony
 *
 */
public class Vector3f {

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
	 * @param x
	 * @param y
	 * @param z
	 */
	public Vector3f(float x, float y, float z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * @param v
	 */
	public Vector3f(Vector3f v) {
		this(v.x,v.y,v.z);
	}
	
	/**
	 */
	public Vector3f() {
		this(0,0,0);
	}
	
	/**
	 * Sets all components.
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public void set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	/**
	 * Sets all components.
	 * 
	 * @param v
	 */
	public void set(Vector3f v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(x);
		result = prime * result + Float.floatToIntBits(y);
		result = prime * result + Float.floatToIntBits(z);
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Vector3f other = (Vector3f) obj;
		if (Float.floatToIntBits(x) != Float.floatToIntBits(other.x))
			return false;
		if (Float.floatToIntBits(y) != Float.floatToIntBits(other.y))
			return false;
		if (Float.floatToIntBits(z) != Float.floatToIntBits(other.z))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {	
		return "{ x: " + x + ", y: " +  y + ", z: " + z + "}";
	}
	
	/**
	 * Vector add.
	 * 
	 * @param a
	 * @param b
	 * @param dest
	 */
	public static void Vector3fAdd(Vector3f a, Vector3f b, Vector3f dest) {
		dest.x = a.x + b.x;
		dest.y = a.y + b.y;	
		dest.z = a.z + b.z;
	}

	/**
	 * Vector subtract.
	 * 
	 * @param a
	 * @param b
	 * @param dest
	 */
	public static void Vector3fSubtract(Vector3f a, Vector3f b, Vector3f dest) {
		dest.x = a.x - b.x;
		dest.y = a.y - b.y;	
		dest.z = a.z - b.z;
	}
	
	/**
	 * Multiplication.
	 *
	 * @param a
	 * @param b
	 * @param dest
	 */
	public static /*strictfp*/ void Vector3fMult(Vector3f a, Vector3f b, Vector3f dest) {
		dest.x = a.x * b.x;
		dest.y = a.y * b.y;
		dest.z = a.z * b.z;
	}

	/**
	 * Multiplication.
	 *
	 * @param a
	 * @param scalar
	 * @param dest
	 */
	public static /*strictfp*/ void Vector3fMult(Vector3f a, float scalar, Vector3f dest) {
		dest.x = a.x * scalar;
		dest.y = a.y * scalar;
		dest.z = a.z * scalar;
	}
}
