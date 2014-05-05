/*
 *	leola-live 
 *  see license.txt
 */
package seventh.math;

import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * 2 component vector.
 *
 * @author Tony
 *
 */
public /*strictfp*/ final class Vector2f {

	/**
	 * The zero vector
	 */
	public static final Vector2f ZERO_VECTOR = new Vector2f(0,0);
	
	/**
	 * The Right vector
	 */
	public static final Vector2f RIGHT_VECTOR = new Vector2f(1, 0);

	/**
	 * X and Y components.
	 */
	public float x,y;

	/**
	 * Constructs a new Vector2f
	 */
	public Vector2f() {
		this.x = this.y = 0;
	}

	/**
	 * Constructs a new Vector2f
	 *
	 * @param x
	 * @param y
	 */
	public Vector2f(float x, float y) {
		this.x = x; this.y = y;
	}

	/**
	 * Constructs a new Vector2f
	 *
	 * @param v
	 */
	public Vector2f(float []v) {
		this.x = v[0]; this.y = v[1];
	}

	/**
	 * @param v
	 */
	public Vector2f(Vector2f v) {
		this.x = v.x; this.y = v.y;
	}

	/**
	 * Get a component of the vector.
	 *
	 * @param i
	 * @return
	 */
	public float get(int i) {
		return (i==0) ? this.x : this.y;
	}

	/**
	 * Set this vector.
	 *
	 * @param x
	 * @param y
	 */
	public void set(float x, float y) {
		this.x = x; this.y = y;
	}

	/**
	 * Set this vector
	 *
	 * @param v
	 */
	public void set(Vector2f v) {
		this.x = v.x; this.y = v.y;
	}


	/**
	 * Set this vector.
	 * @param v
	 */
	public void set(float[] v) {
		this.x = v[0]; this.y = v[1];
	}

	/**
	 * Zero out the {@link Vector2f}
	 */
	public void zeroOut() {
		this.x=0;
		this.y=0;
	}

	/**
	 * Is this the Zero Vector?
	 * @return
	 */
	public boolean isZero() {
		return this.x==0 && this.y==0;
	}

	/**
	 * Round to the nearest integer.
	 */
	public void round() {
		this.x = Math.round(this.x);
		this.y = Math.round(this.y);
	}

	/**
	 * Subtract a {@link Vector2f} from this one.
	 *
	 * @param v
	 * @return
	 */
	public Vector2f subtract(Vector2f v) {
		Vector2f dest = new Vector2f();
		Vector2fSubtract(this, v, dest);
		return dest;
	}

	/**
	 * Add a {@link Vector2f} to this one.
	 *
	 * @param v
	 * @return
	 */
	public Vector2f addition(Vector2f v) {
		Vector2f dest = new Vector2f();
		Vector2fAdd(this, v, dest);
		return dest;
	}

	/**
	 * Multiply this by a {@link Vector2f}.
	 *
	 * @param v
	 * @return
	 */
	public Vector2f mult(Vector2f v) {
		return new Vector2f( this.x * v.x, this.y * v.y );
	}


	/**
	 * Multiply this by a scalar value.
	 *
	 * @param scalar
	 * @return
	 */
	public Vector2f mult(float scalar) {
		return new Vector2f(this.x * scalar, this.y * scalar );
	}
	/**
	 * Multiply this by a {@link Vector2f}.
	 *
	 * @param v
	 * @return
	 */
	public Vector2f div(Vector2f v) {
		return new Vector2f( this.x / v.x, this.y / v.y );
	}

	/**
	 * Divide this by a scalar value.
	 *
	 * @param scalar
	 * @return
	 */
	public Vector2f div(float scalar) {
		return new Vector2f(this.x / scalar, this.y / scalar );
	}

	/**
	 * Normalize this vector
	 */
	public void normalize() {
		float flLen = (float)Math.sqrt( (this.x * this.x + this.y * this.y) );
        if ( flLen==0 ) return;

		flLen = 1.0f / flLen;
		this.x = this.x * flLen;
		this.y = this.y * flLen;
	}

	/**
	 * The length of this vector squared.
	 *
	 * @return
	 */
	public float lengthSquared() {
		return this.x * this.x + this.y * this.y;
	}

	/**
	 * The length of this vector squared.
	 *
	 * @return
	 */
	public float length() {
		return (float)Math.sqrt( (this.x * this.x + this.y * this.y) );
	}

	/**
	 * Rotate the {@link Vector2f}.
	 *
	 * @param radians
	 */
	public Vector2f rotate(double radians) {
		// T : V->W
		// W = | cos(r) -sin(r) |
		//     | sin(r)  cos(r) |
		//
		// V = | x |
		//     | y |
		//
		// T = | (xcos(r) - ysin(r)) |
		//     | (xsin(r) + ycos(r)) |

		double x1 = this.x * cos(radians) - this.y * sin(radians);
		double y1 = this.x * sin(radians) + this.y * cos(radians);

		return new Vector2f((float)x1, (float)y1);
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */

	public boolean equals(Object o) {
		if ( o instanceof Vector2f ) {
			Vector2f v = (Vector2f)o;
			return v.x==this.x&&this.y==v.y;
		}
		return false;
	}


	public int hashCode() {
		return ((Float)x).hashCode() + ((Float)y).hashCode();
	}


	public String toString() {
		return "(" + this.x + "," + this.y + ")";
	}

	/* (non-Javadoc)
	 * @see org.myriad.shared.Clonable#createClone()
	 */

	public Vector2f createClone() {
		return new Vector2f(this.x, this.y);
	}

	/**
	 * Converts the vector to an array.
	 *
	 * @return
	 */
	public float[] toArray() {
		return new float[]{this.x, this.y};
	}

	/*
	=======================================================================================
	Free functions
	=======================================================================================
	 */

	/**
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static /*strictfp*/ float Vector2fCrossProduct(Vector2f v1, Vector2f v2) {
		return v1.x * v2.y - v1.y * v2.x;
	}

	/**
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static /*strictfp*/ float Vector2fDotProduct(Vector2f v1, Vector2f v2) {
		return v1.x * v2.x + v1.y * v2.y;
	}



	/**
	 * The determinant.
	 *
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static /*strictfp*/ float Vector2fDet(Vector2f v1, Vector2f v2) {
		return v1.x * v2.y - v2.x * v1.y;
	}

	/**
	 * Addition
	 *
	 * @param a
	 * @param b
	 * @param dest
	 */
	public static /*strictfp*/ void Vector2fAdd(Vector2f a, Vector2f b, Vector2f dest) {
		dest.x = a.x + b.x;
		dest.y = a.y + b.y;
	}

	/**
	 * Addition
	 *
	 * @param a
	 * @param scalar
	 * @param dest
	 */
	public static /*strictfp*/ void Vector2fAdd(Vector2f a, float scalar, Vector2f dest) {
		dest.x = a.x + scalar;
		dest.y = a.y + scalar;
	}

	/**
	 * Subtraction
	 *
	 * @param a
	 * @param b
	 * @param dest
	 */
	public static /*strictfp*/ void Vector2fSubtract(Vector2f a, Vector2f b, Vector2f dest) {
		dest.x = a.x - b.x;
		dest.y = a.y - b.y;
	}

	/**
	 * Subtraction
	 *
	 * @param a
	 * @param scalar
	 * @param dest
	 */
	public static /*strictfp*/ void Vector2fSubtract(Vector2f a, float scalar, Vector2f dest) {
		dest.x = a.x - scalar;
		dest.y = a.y - scalar;
	}

	/**
	 * Multiplication.
	 *
	 * @param a
	 * @param b
	 * @param dest
	 */
	public static /*strictfp*/ void Vector2fMult(Vector2f a, Vector2f b, Vector2f dest) {
		dest.x = a.x * b.x;
		dest.y = a.y * b.y;
	}

	/**
	 * Multiplication.
	 *
	 * @param a
	 * @param scalar
	 * @param dest
	 */
	public static /*strictfp*/ void Vector2fMult(Vector2f a, float scalar, Vector2f dest) {
		dest.x = a.x * scalar;
		dest.y = a.y * scalar;
	}

	/**
	 * Division.
	 *
	 * @param a
	 * @param b
	 * @param dest
	 */
	public static /*strictfp*/ void Vector2fDiv(Vector2f a, Vector2f b, Vector2f dest) {
		dest.x = a.x / b.x;
		dest.y = a.y / b.y;
	}

	/**
	 * Division.
	 *
	 * @param a
	 * @param scalar
	 * @param dest
	 */
	public static /*strictfp*/ void Vector2fDiv(Vector2f a, float scalar, Vector2f dest) {
		dest.x = a.x / scalar;
		dest.y = a.y / scalar;
	}

	/**
	 * Rotate about the vector a.
	 *
	 * @param a
	 * @param scalar
	 * @param dest
	 */
	public static /*strictfp*/ void Vector2fRotate(Vector2f a, double radians, Vector2f dest) {
		float x = (float)(a.x * cos(radians) - a.y * sin(radians));
		float y = (float)(a.x * sin(radians) + a.y * cos(radians));
		dest.x = x;
		dest.y = y;
	}

	/**
	 * Takes in two NORMALIZED vectors and calculates the angle between
	 * them.
	 * @param a
	 * @param b
	 * @return the angle between the two vectors in radians.
	 */
	public static /*strictfp*/ double Vector2fAngle(Vector2f a, Vector2f b) {
		double angle = atan2(b.y, b.x) - atan2(a.y, a.x);
		return angle;
	}

	/**
	 * Does a vector scale and addition:
	 *
	 * <p>
	 * 	dest = a + b*scalar;
	 *
	 * @param a
	 * @param b
	 * @param scalar
	 * @param dest
	 */
	public static /*strictfp*/ void Vector2fMA(Vector2f a, Vector2f b, float scalar, Vector2f dest) {
		dest.x = a.x + b.x * scalar;
		dest.y = a.y + b.y * scalar;
	}

	/**
	 * Does a vector scale and subtraction:
	 *
	 * <p>
	 * 	dest = a - b*scalar;
	 *
	 * @param a
	 * @param b
	 * @param scalar
	 * @param dest
	 */
	public static /*strictfp*/ void Vector2fMS(Vector2f a, Vector2f b, float scalar, Vector2f dest) {
		dest.x = a.x - b.x * scalar;
		dest.y = a.y - b.y * scalar;
	}

	/**
	 * Copies a into dest
	 * @param a
	 * @param dest
	 */
	public static /*strictfp*/ void Vector2fCopy(Vector2f a, Vector2f dest) {
		dest.x =  a.x;
		dest.y =  a.y;
	}

	/**
	 * Test for equality.
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static /*strictfp*/ boolean Vector2fEquals(Vector2f a, Vector2f b) {
		return a.x == b.x && a.y == b.y;
	}

	/**
	 * Accounts for float inaccuracy.
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static /*strictfp*/ boolean Vector2fApproxEquals(Vector2f a, Vector2f b) {
		return FloatUtil.eq(a.x, b.x) && FloatUtil.eq(a.y, b.y);
	}

	/**
	 * Accounts for float inaccuracy.
	 *
	 * @param a
	 * @param b
	 * @param epsilon
	 * @return
	 */
	public static /*strictfp*/ boolean Vector2fApproxEquals(Vector2f a, Vector2f b, float epsilon) {
		float tmp = FloatUtil.epsilon;
		FloatUtil.epsilon = epsilon;

		boolean result = FloatUtil.eq(a.x, b.x) && FloatUtil.eq(a.y, b.y);
		FloatUtil.epsilon = tmp;

		return result;
	}

	/**
	 * Unit length of the vector.
	 *
	 * @param a
	 * @return
	 */
	public static /*strictfp*/ float Vector2fLength(Vector2f a) {
		return (float)Math.sqrt( (a.x * a.x + a.y * a.y) );
	}

	/**
	 * The length squared.
	 *
	 * @param a
	 * @return
	 */
	public static /*strictfp*/ float Vector2fLengthSq(Vector2f a) {
		return (a.x * a.x + a.y * a.y);
	}

	/**
	 * The distance between two vectors
	 *
	 * @param a
	 * @return
	 */
	public static /*strictfp*/ float Vector2fDistanceSq(Vector2f a, Vector2f b) {
		return (b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y);
	}

	/**
	 * The distance between two vectors
	 *
	 * @param a
	 * @return
	 */
	public static /*strictfp*/ float Vector2fDistance(Vector2f a, Vector2f b) {
		return (float)Math.sqrt((b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y));
	}

	/**
	 * Mormalizes the vector.
	 *
	 * @param a
	 * @param dest
	 */
	public static /*strictfp*/ void Vector2fNormalize(Vector2f a, Vector2f dest) {
		float fLen = (float)Math.sqrt( (a.x * a.x + a.y * a.y) );
		if ( fLen==0 ) return;

		//fLen = 1.0f / fLen;
		dest.x = a.x / fLen;
		dest.y = a.y / fLen;
	}

	/**
	 * Mormalizes the vector.
	 *
	 * @param a
	 * @param dest
	 */
	public static /*strictfp*/ void Vector2fPerpendicular(Vector2f a, Vector2f dest) {
		float x =  a.y;
		float y = -a.x;

		dest.x = x;
		dest.y = y;
	}


	/**
	 * Zero outs the components.
	 *
	 * @param a
	 */
	public static /*strictfp*/ void Vector2fZeroOut(Vector2f a) {
		a.x = a.y = 0;
	}

	/**
	 * Tests if this is the zero vector.
	 *
	 * @param a
	 * @return
	 */
	public static /*strictfp*/ boolean Vector2fIsZero(Vector2f a) {
		return a.x == 0 && a.y == 0;
	}


	/**
	 * Calculates the length squared for each vector and determines if
	 * a is greater or equal in length.
	 *
	 * @param a
	 * @param b
	 * @return
	 */
	public static /*strictfp*/ boolean Vector2fGreaterOrEq(Vector2f a, Vector2f b) {
		float aLength = (a.x * a.x + a.y * a.y);
		float bLength = (b.x * b.x + b.y * b.y);

		return FloatUtil.gte(aLength, bLength);
	}

	/**
	 * Rounds the components to the nearest whole number.
	 *
	 * @param a
	 * @param dest
	 */
	public static /*strictfp*/ void Vector2fRound(Vector2f a, Vector2f dest) {
		dest.x = Math.round(a.x);
		dest.y = Math.round(a.y);
	}

	/**
	 * Snaps the components to the nearest whole number.
	 *
	 * @param a
	 * @param dest
	 */
	public static /*strictfp*/ void Vector2fSnap(Vector2f a, Vector2f dest) {
		dest.x = (int)a.x;
		dest.y = (int)a.y;
	}

	/**
	 * Sets the components.
	 *
	 * @param a
	 * @param x
	 * @param y
	 */
	public static /*strictfp*/ void Vector2fSet(Vector2f a, float x, float y) {
		a.x = x;
		a.y = y;
	}

	/**
	 * Negate the vector
	 *
	 * @param a
	 * @param x
	 * @param y
	 */
	public static /*strictfp*/ void Vector2fNegate(Vector2f a, Vector2f dest) {
		dest.x = -a.x;
		dest.y = -a.y;
	}

	/**
	 * Anything but zero will be rounded to the next whole number, if negative it
	 * will round to the lower whole number.
	 *
	 * @param a
	 * @param x
	 * @param y
	 */
	public static /*strictfp*/ void Vector2fWholeNumber(Vector2f a, Vector2f dest) {
		int t = (int)a.x;
		float delta = a.x - (float)t;
		if ( ! FloatUtil.eq(delta, 0) ) {
			dest.x = (a.x < 0) ? t - 1 : t + 1;
		}

		t = (int)a.y;
		delta = a.y - (float)t;
		if ( ! FloatUtil.eq(delta, 0) ) {
			dest.y = (a.y < 0 ) ? t - 1 : t + 1;
		}

		Vector2fSnap(dest, dest);
	}


	/**
	 * Interpolate between a and b vectors.
	 *
	 * @param a
	 * @param b
	 * @param alpha
	 * @param dest
	 */
	public static /*strictfp*/ void Vector2fInterpolate(Vector2f a, Vector2f b, float alpha, Vector2f dest ) {
		//dest.x = b.x * alpha + a.x * (1.0f - alpha);
		//dest.y = b.y * alpha + a.y * (1.0f - alpha);

		dest.x = a.x + ((b.x - a.x) * alpha);
		dest.y = a.y + ((b.y - a.y) * alpha);
	}
}
