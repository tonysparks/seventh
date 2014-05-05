/*
 * see license.txt 
 */
package seventh.game;

import java.util.HashMap;
import java.util.Map;

import seventh.shared.StateMachine;

/**
 * @author Tony
 *
 */
public class EntityStateMachine extends StateMachine<EntityState> {

	private Map<String, EntityState> states;
	
	/**
	 * 
	 */
	public EntityStateMachine() {
		this.states = new HashMap<String, EntityState>();
	}
	
	public void registerState(EntityState state) {
		this.states.put(state.getClass().getSimpleName(), state);
	}
	
	public void changeState(Class<?> stateClass) {
		changeState(stateClass.getSimpleName());
	}

	public void changeState(String stateClassName) {
		EntityState state = this.states.get(stateClassName);
		if(state == null) {
			throw new IllegalStateException("No such state exists");
		}
		
		changeState(state);
	}
}
