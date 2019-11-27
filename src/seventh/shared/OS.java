/*
 * see license.txt 
 */
package seventh.shared;

public class OS {

    public static void sleep(long msec) {
        try {
            Thread.sleep(msec);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
