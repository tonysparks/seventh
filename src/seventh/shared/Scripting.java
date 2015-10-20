/*
 * see license.txt 
 */
package seventh.shared;

import leola.vm.Args;
import leola.vm.Leola;

/**
 * Scripting runtime factory methods
 * 
 * @author Tony
 *
 */
public class Scripting {

	
	/**
	 * Creates a new {@link Leola} runtime that is in sandboxed mode.
	 * 
	 * @return the runtime
	 */
	public static Leola newSandboxedRuntime() {		
		Leola runtime = Args.builder()
							.setAllowThreadLocals(false)
							.setIsDebugMode(true)
							.setBarebones(true)
							.setSandboxed(true)
							.newRuntime();
		return runtime;
	}
	
	
	/**
	 * Creates a new {@link Leola} runtime that is not in sandboxed mode.
	 *  
	 * @return the runtime
	 */
	public static Leola newRuntime() {				
		Leola runtime = Args.builder()
							.setIsDebugMode(true)
							.setAllowThreadLocals(false)
							.newRuntime();		
		return runtime;
	}

}
