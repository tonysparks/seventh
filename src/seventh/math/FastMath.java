/*
 *	leola-live 
 *  see license.txt
 */
package seventh.math;

import java.lang.reflect.Method;

/**
 * Utility and fast math functions.
 * 
 * Thanks to:<br>
 * Riven on JavaGaming.org for sin/cos/atan2 tables.<br>
 * Roquen on JavaGaming.org for random numbers.<br>
 * pjt33 on JavaGaming.org for fixed point.<br>
 * Jim Shima for atan2_fast.<br>
 * 
 * <p>
 * Taken from JavaGaming.org from <b>Nate</b>
 * 
 * @author Nate
 */
public class FastMath {

	static public final float PI = 3.1415926535897932384626433832795028841971f;
	static public final float fullCircle = PI * 2;
	
	static private final int SIN_BITS = 12; // Adjust for accuracy.
	static private final int SIN_MASK = ~(-1 << SIN_BITS);
	static private final int SIN_COUNT = SIN_MASK + 1;

	static private final float radFull = PI * 2;
	static private final float degFull = 360;
	static private final float radToIndex = SIN_COUNT / radFull;
	static private final float degToIndex = SIN_COUNT / degFull;

	static public final float [] sin = new float[SIN_COUNT];
	static public final float [] cos = new float[SIN_COUNT];
	static {
		for (int i = 0; i < SIN_COUNT; i++) {
			float a = (i + 0.5f) / SIN_COUNT * radFull;
			sin[i] = (float) Math.sin(a);
			cos[i] = (float) Math.cos(a);
		}
	}

	static public final float sin(float rad) {
		return sin[(int) (rad * radToIndex) & SIN_MASK];
	}

	static public final float cos(float rad) {
		return cos[(int) (rad * radToIndex) & SIN_MASK];
	}

	static public final float sin(int deg) {
		return sin[(int) (deg * degToIndex) & SIN_MASK];
	}

	static public final float cos(int deg) {
		return cos[(int) (deg * degToIndex) & SIN_MASK];
	}

	private static final int ATAN2_BITS = 7; // Adjust for accuracy.
	private static final int ATAN2_BITS2 = ATAN2_BITS << 1;
	private static final int ATAN2_MASK = ~(-1 << ATAN2_BITS2);
	private static final int ATAN2_COUNT = ATAN2_MASK + 1;
	private static final int ATAN2_DIM = (int) Math.sqrt(ATAN2_COUNT);
	private static final float INV_ATAN2_DIM_MINUS_1 = 1.0f / (ATAN2_DIM - 1);
	private static final float [] atan2 = new float[ATAN2_COUNT];
	static {
		for (int i = 0; i < ATAN2_DIM; i++) {
			for (int j = 0; j < ATAN2_DIM; j++) {
				float x0 = (float) i / ATAN2_DIM;
				float y0 = (float) j / ATAN2_DIM;
				atan2[j * ATAN2_DIM + i] = (float) Math.atan2(y0, x0);
			}
		}
	}

	public static final float atan2(float y, float x) {
		float add, mul;
		if ( x < 0 ) {
			if ( y < 0 ) {
				y = -y;
				mul = 1;
			}
			else
				mul = -1;
			x = -x;
			add = -3.141592653f;
		}
		else {
			if ( y < 0 ) {
				y = -y;
				mul = -1;
			}
			else
				mul = 1;
			add = 0;
		}
		float invDiv = 1 / (((x < y) ? y : x) * INV_ATAN2_DIM_MINUS_1);
		int xi = (int) (x * invDiv);
		int yi = (int) (y * invDiv);
		return (atan2[yi * ATAN2_DIM + xi] + add) * mul;
	}

	/**
	 * This is a very fast atan2, but not very accurate.
	 */
	static public float atan2_fast(float y, float x) {
		// From: http://dspguru.com/comp.dsp/tricks/alg/fxdatan2.htm
		float abs_y = y < 0 ? -y : y;
		float angle;
		if ( x >= 0 )
			angle = 0.7853981633974483f - 0.7853981633974483f * (x - abs_y) / (x + abs_y);
		else
			angle = 2.356194490192345f - 0.7853981633974483f * (x + abs_y) / (abs_y - x);
		return y < 0 ? -angle : angle;
	}

	static private Method sqrtMethod;
	static {
		try {
			sqrtMethod = Class.forName("android.util.FloatMath").getMethod("sqrt", new Class[] {
				float.class
			});
		}
		catch (Exception ex) {
			try {
				sqrtMethod = Class.forName("java.lang.Math").getMethod("sqrt", new Class[] {
					double.class
				});
			}
			catch (Exception ignored) {
			}
		}
	}

	static public final float sqrt(float value) {
		try {
			return ((Number) sqrtMethod.invoke(null, value)).floatValue();
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Inverse square root approximation
	 * 
	 * @param x
	 * @return
	 */
	public static float a_isqrt(float x) {
		float hx = x * 0.5f;
		int ix;
		float r;

		// make initial guess
		ix = Float.floatToRawIntBits(x);
		ix = 0x5f3759df - (ix >> 1);
		r = Float.intBitsToFloat(ix);

		// do some number of newton-ralphson steps,
		// each doubles the number of accurate
		// binary digits.
		r = r * (1.5f - hx * r * r);
		// r = r*(1.5f-hx*r*r);
		// r = r*(1.5f-hx*r*r);
		// r = r*(1.5f-hx*r*r);

		return r; // 1/sqrt(x)
		// return r*x; // sqrt(x)
	}

	/**
	 * square root approximation
	 * 
	 * @param x
	 * @return
	 */
	public static float a_sqrt(float x) {
		float hx = x * 0.5f;
		int ix;
		float r;

		// make initial guess
		ix = Float.floatToRawIntBits(x);
		ix = 0x5f3759df - (ix >> 1);
		r = Float.intBitsToFloat(ix);

		// do some number of newton-ralphson steps,
		// each doubles the number of accurate
		// binary digits.
		r = r * (1.5f - hx * r * r);
		// r = r*(1.5f-hx*r*r);
		// r = r*(1.5f-hx*r*r);
		// r = r*(1.5f-hx*r*r);
		
		return r*x; // sqrt(x)
	}

	/**
	 * Fixed point multiply.
	 */
	static public int multiply(int x, int y) {
		return (int) ((long) x * (long) y >> 16);
	}

	/**
	 * Fixed point divide.
	 */
	static public int divide(int x, int y) {
		return (int) ((((long) x) << 16) / y);
	}

	static private int randomSeed = (int) System.currentTimeMillis();

	/**
	 * Returns a random number between 0 (inclusive) and the specified value
	 * (inclusive).
	 * 
	 * @param range
	 *            Must be >= 0.
	 */
	static public final int random(int range) {
		int seed = randomSeed * 1103515245 + 12345;
		randomSeed = seed;
		return ((seed >>> 15) * (range + 1)) >>> 17;
	}

	static public final int random(int start, int end) {
		int seed = randomSeed * 1103515245 + 12345;
		randomSeed = seed;
		return (((seed >>> 15) * ((end - start) + 1)) >>> 17) + start;
	}

	static public final boolean randomBoolean() {
		int seed = randomSeed * 1103515245 + 12345;
		randomSeed = seed;
		return seed > 0;
	}

	static public final float random() {
		int seed = randomSeed * 1103515245 + 12345;
		randomSeed = seed;
		return (seed >>> 8) * (1f / (1 << 24));
	}

	static public int nextPowerOfTwo(int value) {
		return 1 << (32 - Integer.numberOfLeadingZeros(value - 1));
	}

	/**
	 * Takes the minimum value.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static float min(float a, float b) {
		return (a <= b) ? a : b;
	}

	/**
	 * Takes the minimum value.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static double min(double a, double b) {
		return (a <= b) ? a : b;
	}

	/**
	 * Takes the minimum value.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static int min(int a, int b) {
		return (a <= b) ? a : b;
	}

	/**
	 * Takes the minimum value.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static long min(long a, long b) {
		return (a <= b) ? a : b;
	}

	// /---

	/**
	 * Takes the maximum value.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static float max(float a, float b) {
		return (a >= b) ? a : b;
	}

	/**
	 * Takes the maximum value.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static double max(double a, double b) {
		return (a >= b) ? a : b;
	}

	/**
	 * Takes the maximum value.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static int max(int a, int b) {
		return (a >= b) ? a : b;
	}

	/**
	 * Takes the maximum value.
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static long max(long a, long b) {
		return (a >= b) ? a : b;
	}

}