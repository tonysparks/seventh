/*
 * see license.txt 
 */
package seventh.shared;

import leola.vm.Args;
import leola.vm.Args.ArgsBuilder;
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
	 * @throws Exception
	 */
	public static Leola newSandboxedRuntime() throws Exception {
		Args args = new ArgsBuilder()
						.setAllowThreadLocals(false)
						
						.setBarebones(true)
						.setSandboxed(true)
						.build();
		
		Leola runtime = new Leola(args);
		return runtime;
	}
	
	
	/**
	 * Creates a new {@link Leola} runtime that is not in sandboxed mode.
	 *  
	 * @return the runtime
	 * @throws Exception
	 */
	public static Leola newRuntime() throws Exception {
		Args leolaArgs = new ArgsBuilder().setAllowThreadLocals(false).build();		
		Leola runtime = new Leola(leolaArgs);		
		return runtime;
	}

}
