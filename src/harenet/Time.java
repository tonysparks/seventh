/*
 * see license.txt 
 */
package harenet;

/**
 * Utility class for calculating delta times
 * 
 * @author Tony
 *
 */
public class Time {

	private static long timeBase = System.currentTimeMillis() - (365 * 24 * 60 * 60 * 1000);
	
	
	
	/**
	 * The time based off of some arbitrary offset.  This should not be 
	 * used to calculate the current epoch time, but rather should be used
	 * for taking delta time calculations
	 * 
	 * @return the current time based off of some some arbitrary offset
	 */
	public static int time() {
		return (int) (System.currentTimeMillis() - timeBase);
	}

}
