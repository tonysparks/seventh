/*
 * see license.txt 
 */
package seventh.shared;

/**
 * A global instance of {@link Console} that I use so I don't have to pass Console around every where.
 * 
 * NOTE: Switching the implementation around is NOT thread safe.
 * 
 * @author Tony
 *
 */
public class Cons {

	private static Console impl = new DefaultConsole();
	
	/**
	 * Sets the underlying implementation -- this should be
	 * set upon start up -- once.
	 * 
	 * @param console
	 */
	public static void setImpl(Console console) {
		impl = console;
	}
	
	/**
	 * @return the impl
	 */
	public static Console getImpl() {
		return impl;
	}
	
	/**
	 * Adds a {@link Logger} to the global {@link Console} 
	 * @param logger
	 */
	public static void addLogger(Logger logger) {
		impl.addLogger(logger);
	}

	/* (non-Javadoc)
	 * @see palisma.shared.Console#print(java.lang.Object)
	 */	
	public static void print(Object message) {
		impl.print(message);
	}

	/* (non-Javadoc)
	 * @see palisma.shared.Console#println(java.lang.Object)
	 */
	public static void println(Object message) {
		impl.println(message);
	}

	/* (non-Javadoc)
	 * @see palisma.shared.Console#printf(java.lang.Object, java.lang.Object[])
	 */
	
	public void printf(Object message, Object... args) {
		impl.printf(message, args);
	}

	/* (non-Javadoc)
	 * @see palisma.shared.Console#execute(java.lang.String, java.lang.String[])
	 */
	
	public void execute(String command, String... args) {
		impl.execute(command, args);
	}

}
