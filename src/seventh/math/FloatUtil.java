/*
 *	leola-live 
 *  see license.txt
 */
package seventh.math;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * Floating point utilities to account for float precision errors.
 * 
 * @author Tony
 *
 */
public /*strictfp*/ class FloatUtil {
	private FloatUtil() {		
	}
	
	/**
	 * Epsilon
	 */
	public static float epsilon = 0.001f;
	
	/**
	 * Determine if two floats are equal by a given Epsilon.
	 * 
	 * @param f1
	 * @param f2
	 * @return
	 */
	public static boolean eq(float f1, float f2) {
		return Math.abs(f1-f2) < epsilon;
	}
	
	/**
	 * Determine if two floats are equal by a given Epsilon.
	 * 
	 * @param f1
	 * @param f2
	 * @param epsilon
	 * @return true if equal
	 */
	public static boolean eq(float f1, float f2, float epsilon) {
		return Math.abs(f1-f2) < epsilon;
	}
	
	/**
	 * Determine if one float is greater than another
	 * 
	 * @param f1
	 * @param f2
	 * @return
	 */
	public static boolean gt(float f1, float f2) {
		return !eq(f1, f2) && (f1 > f2); 
	}
	
	/**
	 * Determine if one float is less than another
	 * 
	 * @param f1
	 * @param f2
	 * @return
	 */
	public static boolean lt(float f1, float f2) {
		return !eq(f1, f2) && (f1 < f2); 
	}
	
	/**
	 * Determine if one float is greater than another or equal
	 * 
	 * @param f1
	 * @param f2
	 * @return
	 */
	public static boolean gte(float f1, float f2) {
		return gt(f1, f2) || eq(f1, f2); 
	}
	
	/**
	 * Determine if one float is less than another or equal
	 * 
	 * @param f1
	 * @param f2
	 * @return
	 */
	public static boolean lte(float f1, float f2) {
		return lt(f1, f2) || eq(f1, f2); 
	}
	
	/*
	===================================================================================
	Float Vector based Implementation
	===================================================================================
	*/
		

	
	/**
	 * Float index for the X component
	 */
	public static final int X = 0;
	

	/**
	 * Float index for the Y component
	 */
	public static final int Y = 1; 
	
	
	public static void Vector2fAdd(float[] a, float[] b, float[] dest) {
		dest[X] = a[X] + b[X];
		dest[Y] = a[Y] + b[Y];
	}
	
	public static void Vector2fSubtract(float[] a, float[] b, float[] dest) {
		dest[X] = a[X] - b[X];
		dest[Y] = a[Y] - b[Y];
	}
	
	public static void Vector2fMult(float[] a, float[] b, float[] dest) {
		dest[X] = a[X] * b[X];
		dest[Y] = a[Y] * b[Y];
	}
	
	public static void Vector2fMult(float[] a, float scalar, float[] dest) {
		dest[X] = a[X] * scalar;
		dest[Y] = a[Y] * scalar;
	}
	
	
	public static void Vector2fDiv(float[] a, float[] b, float[] dest) {
		dest[X] = a[X] / b[X];
		dest[Y] = a[Y] / b[Y];
	}
	
	public static float Vector2fCross(float[] a, float[] b) {
		return a[X] * b[Y] - a[Y] * b[X];
	}
	
	public static boolean Vector2fEquals(float[] a, float[] b) {
		return a[X] == b[X] && a[Y] == b[Y];
	}
	
	/**
	 * Accounts for float inaccuracy.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean Vector2fApproxEquals(float[] a, float[] b) {
		return FloatUtil.eq(a[X], b[X]) && FloatUtil.eq(a[Y], b[Y]);
	}
	
	public static void Vector2fRotate(float[] a, float radians, float[]dest) {
		dest[X] = (float)(a[X] * cos(radians) - a[Y] * sin(radians));
		dest[Y] = (float)(a[X] * sin(radians) + a[Y] * cos(radians));
	}
	
	public static float Vector2fLength(float[] a) {
		return (float)Math.sqrt( (a[X] * a[X] + a[Y] * a[Y]) );
	}
	
	public static float Vector2fLengthSq(float[] a) {
		return (a[X] * a[X] + a[Y] * a[Y]);
	}
	
	public static void Vector2fNormalize(float[] a, float[] dest) {
		float fLen = (float)Math.sqrt( (a[X] * a[X] + a[Y] * a[Y]) );
		if ( fLen==0 ) return;
		
		fLen = 1.0f / fLen;
		dest[X] = a[X] * fLen;
		dest[Y] = a[Y] * fLen;
	}
	
	public static void Vector2fCopy(float[] a, float[] dest) {
		dest[X] = a[X];
		dest[Y] = a[Y];
	}
	
	public static void Vector2fZeroOut(float[] a) {
		a[X] = a[Y] = 0;
	}
	
	public static boolean Vector2fIsZero(float[] a) {
		return a[X] == 0 && a[Y] == 0;
	}
	
	public static Vector2f Vector2fToVector2f(float[] a) {
		return new Vector2f(a);
	}
	
	public static float[] Vector2fNew() {
		return new float[]{0,0};
	}
	
	/**
	 * Calculates the length squared for each vector and determines if
	 * a is greater or equal in length.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean Vector2fGreaterOrEq(float[] a, float[] b) {
		float aLength = (a[X] * a[X] + a[Y] * a[Y]);
		float bLength = (b[X] * b[X] + b[Y] * b[Y]);
		
		return FloatUtil.gte(aLength, bLength);
	}
	
	public static void Vector2fRound(float[] a, float[] dest) {
		dest[X] = Math.round(a[X]);
		dest[Y] = Math.round(a[Y]);
	}
	
	public static void Vector2fSet(float[] a, float x, float y) {
		a[X] = x;
		a[Y] = y;
	}
	
	public static void Vector2fNegate(float[] a, float[] dest) {
		dest[X] = -a[X];
		dest[Y] = -a[Y];
	}
}
