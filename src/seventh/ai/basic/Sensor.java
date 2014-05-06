/*
 * see license.txt 
 */
package seventh.ai.basic;

import seventh.shared.TimeStep;

/**
 * A Sensor is a sense, such as Sight, Sound, etc.
 * 
 * @author Tony
 *
 */
public interface Sensor {

	/**
	 * Reset to a starting state
	 * @param brain
	 */
	public void reset(Brain brain);
	
	/**
	 * Updates the Sensor
	 * 
	 * @param timeStep
	 */
	public void update(TimeStep timeStep);
}
