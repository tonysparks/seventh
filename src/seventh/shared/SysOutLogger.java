/*
 * see license.txt 
 */
package seventh.shared;

/**
 * @author Tony
 *
 */
public class SysOutLogger implements Logger {

	/* (non-Javadoc)
	 * @see shared.Console#print(java.lang.Object)
	 */
	public void print(Object msg) {
		System.out.print(msg);
	}
	
	/* (non-Javadoc)
	 * @see shared.Console#println(java.lang.Object)
	 */
	public void println(Object msg) {
		System.out.println(msg);

	}

	/* (non-Javadoc)
	 * @see shared.Console#printf(java.lang.Object, java.lang.Object[])
	 */
	public void printf(Object msg, Object... args) {
		System.out.printf(msg.toString(), args);
	}

}
