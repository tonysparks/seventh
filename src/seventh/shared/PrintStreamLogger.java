/*
 * see license.txt 
 */
package seventh.shared;

import java.io.PrintStream;

/**
 * Uses a {@link PrintStream} as an implementation
 * 
 * @author Tony
 *
 */
public class PrintStreamLogger implements Logger {
	
	private PrintStream stream;
	
	/**
	 * @param stream
	 */
	public PrintStreamLogger(PrintStream stream) {
		this.stream = stream;
	}
	
	/* (non-Javadoc)
	 * @see seventh.shared.Logger#print(java.lang.Object)
	 */
	@Override
	public void print(Object msg) {
		stream.print(msg);
	}

	/* (non-Javadoc)
	 * @see seventh.shared.Logger#println(java.lang.Object)
	 */
	@Override
	public void println(Object msg) {
		stream.println(msg);
	}

	/* (non-Javadoc)
	 * @see seventh.shared.Logger#printf(java.lang.Object, java.lang.Object[])
	 */
	@Override
	public void printf(Object msg, Object... args) {
		stream.printf(msg.toString(), args);
	}

	
}
