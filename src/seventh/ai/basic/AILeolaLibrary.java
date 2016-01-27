/*
 * see license.txt 
 */
package seventh.ai.basic;

import leola.vm.Leola;
import leola.vm.exceptions.LeolaRuntimeException;
import leola.vm.lib.LeolaIgnore;
import leola.vm.lib.LeolaLibrary;
import leola.vm.types.LeoNamespace;
import seventh.ai.AISystem;
import seventh.ai.basic.actions.Actions;

/**
 * AI Library, which includes useful AI functions for scripting.
 * 
 * @see Actions
 * @author Tony
 *
 */
public class AILeolaLibrary implements LeolaLibrary {

	private Leola runtime;
	private Actions actions;
	private AISystem aiSystem;
	
	/**
	 * @param aiSystem
	 */
	public AILeolaLibrary(AISystem aiSystem) {
		this.aiSystem = aiSystem;
	}

	/* (non-Javadoc)
	 * @see leola.vm.lib.LeolaLibrary#init(leola.vm.Leola, leola.vm.types.LeoNamespace)
	 */
	@LeolaIgnore
	@Override
	public void init(Leola leola, LeoNamespace namespace) throws LeolaRuntimeException {
		this.runtime = leola;
		this.runtime.putIntoNamespace(this, namespace);
		
		this.actions = new Actions(this.aiSystem, this.runtime);
		this.runtime.putIntoNamespace(this.actions, namespace);		
		
	}
	
	/**
	 * @return the {@link Actions} factory
	 */
	@LeolaIgnore
	public Actions getActionFactory() {
		return actions;
	}	
}
