/*
 * see license.txt 
 */
package harenet;

/**
 * @author Tony
 *
 */
public class Time {

	private static long timeBase = System.currentTimeMillis() - (365 * 24 * 60 * 60 * 1000);
	
	
	
	
	public static int time() {
		return (int) (System.currentTimeMillis() - timeBase);
	}

}
